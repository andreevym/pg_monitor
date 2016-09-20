package io.github.tbk.postgres.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.collect.ImmutableMap;
import io.github.tbk.postgres.metrics.postgres.PostgresDao;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.Objects.requireNonNull;

@Slf4j
public class DbMetricSet implements MetricSet {

    private final String prefix;
    private final PostgresDao postgresDao;

    public DbMetricSet(String metricPrefix, PostgresDao postgresDao) {
        this.prefix = metricPrefix;
        this.postgresDao = requireNonNull(postgresDao);
    }

    @Override
    public Map<String, Metric> getMetrics() {
        ImmutableMap.Builder<String, Metric> registeredMetrics = ImmutableMap.builder();
        this.postgresDao.getDatabaseNames()
                .toBlocking()
                .toIterable()
                .forEach(dbName -> {
                    String tags = ",database_name=" + dbName;
                    registeredMetrics.put(name(prefix, "database", "logons", "current") + tags, getCurrentLogon(dbName));
                    registeredMetrics.put(name(prefix, "database", "size") + tags, getSize(dbName));
                    //registeredMetrics.put(name(prefix, "database", "tables") + tags, getUserTablesAmount(dbName));
                    registeredMetrics.put(name(prefix, "database", "commits") + tags, getCommits(dbName));
                    registeredMetrics.put(name(prefix, "database", "rollbacks") + tags, getRollbacks(dbName));
                });
        return registeredMetrics.build();
    }

    private Gauge<Integer> getCurrentLogon(String dbName) {
        return getIntAsGauge("select count(0) from pg_stat_activity where datname='" + dbName + "' and state <> 'idle' and pid <> pg_backend_pid()");
    }

    private Gauge<Integer> getSize(String dbName) {
        return getIntAsGauge("select pg_database_size('" + dbName + "')");
    }

    private Gauge<Integer> getCommits(String dbName) {
        return getIntAsGauge("select xact_commit from pg_stat_database where datname = '" + dbName + "'");
    }

    private Gauge<Integer> getUserTablesAmount(String dbName) {
        return getIntAsGauge("select count(0) from pg_stat_user_tables where datname = '" + dbName + "'");
    }

    private Gauge<Integer> getRollbacks(String dbName) {
        return getIntAsGauge("select xact_rollback from pg_stat_database where datname = '" + dbName + "'");
    }

    private Gauge<Integer> getIntAsGauge(String sql) {
        return () -> postgresDao.getInt(sql).orElse(null);
    }

}