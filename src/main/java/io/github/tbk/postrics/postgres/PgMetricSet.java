package io.github.tbk.postrics.postgres;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.Objects.requireNonNull;

@Slf4j
public class PgMetricSet implements MetricSet {

    private final String prefix;
    private final PostgresDao postgresDao;

    private final String databaseName;

    public PgMetricSet(String metricPrefix, PostgresDao postgresDao) {
        this.prefix = metricPrefix;
        this.postgresDao = requireNonNull(postgresDao);
        this.databaseName = postgresDao.getDatabaseName().toBlocking().single();
    }

    @Override
    public Map<String, Metric> getMetrics() {
        ImmutableMap.Builder<String, Metric> registeredMetrics = ImmutableMap.builder();

        String tags = ",database_name=" + databaseName;
        registeredMetrics.put(name(prefix, "database", "logons", "current") + tags, getCurrentLogon());
        
        registeredMetrics.put(name(prefix, "database", "size") + tags, getSize());

        registeredMetrics.put(name(prefix, "database", "tables") + tags + ",type=user", getUserTablesAmount());
        registeredMetrics.put(name(prefix, "database", "tables") + tags + ",type=sys", getSysTablesAmount());
        registeredMetrics.put(name(prefix, "database", "tables") + tags, getAllTablesAmount());

        registeredMetrics.put(name(prefix, "database", "schemata") + tags + ",type=user", getUserSchemataAmount());

        registeredMetrics.put(name(prefix, "database", "commits") + tags, getCommits());
        registeredMetrics.put(name(prefix, "database", "rollbacks") + tags, getRollbacks());
        return registeredMetrics.build();
    }

    private Gauge<Integer> getCurrentLogon() {
        return getIntAsGauge("select count(0) from pg_stat_activity where datname='" + databaseName + "' and state <> 'idle' and pid <> pg_backend_pid()");
    }

    private Gauge<Integer> getSize() {
        return getIntAsGauge("select pg_database_size('" + databaseName + "')");
    }

    private Gauge<Integer> getCommits() {
        return getIntAsGauge("select xact_commit from pg_stat_database where datname = '" + databaseName + "'");
    }

    private Gauge<Integer> getUserTablesAmount() {
        return getIntAsGauge("select count(0) from pg_stat_user_tables");
    }

    private Gauge<Integer> getUserSchemataAmount() {
        return getIntAsGauge("select count(distinct(schemaname)) from pg_stat_user_tables");
    }

    private Gauge<Integer> getSysTablesAmount() {
        return getIntAsGauge("select count(0) from pg_stat_sys_tables");
    }

    private Gauge<Integer> getAllTablesAmount() {
        return getIntAsGauge("select count(0) from pg_stat_all_tables");
    }


    private Gauge<Integer> getRollbacks() {
        return getIntAsGauge("select xact_rollback from pg_stat_database where datname = '" + databaseName + "'");
    }

    private Gauge<Integer> getIntAsGauge(String sql) {
        return () -> postgresDao.getInt(sql).orElse(null);
    }

}