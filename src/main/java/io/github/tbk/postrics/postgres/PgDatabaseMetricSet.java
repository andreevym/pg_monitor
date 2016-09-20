package io.github.tbk.postrics.postgres;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.collect.ImmutableMap;
import io.github.tbk.postrics.postgres.command.Command;
import io.github.tbk.postrics.postgres.command.CommandExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.Objects.requireNonNull;

@Slf4j
public class PgDatabaseMetricSet implements MetricSet {

    private final String prefix;
    private final CommandExecutor commandExecutor;

    private final String databaseName;

    public PgDatabaseMetricSet(String metricPrefix, CommandExecutor commandExecutor) {
        this.prefix = metricPrefix;
        this.commandExecutor = requireNonNull(commandExecutor);
        this.databaseName = commandExecutor.execute(Commands.SELECT_CURRENT_DATABASE).toBlocking().single();
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

    private Gauge<Long> getCurrentLogon() {
        return asGauge(Commands.SELECT_DATABASE_PROCESS_COUNT.apply(databaseName));
    }

    private Gauge<Long> getSize() {
        return asGauge(Commands.SELECT_DATABASE_SIZE.apply(databaseName));
    }


    private Gauge<Long> getRollbacks() {
        return asGauge(Commands.SELECT_DATABASE_ROLLBACK_COUNT.apply(databaseName));
    }

    private Gauge<Long> getCommits() {
        return asGauge(Commands.SELECT_DATABASE_COMMIT_COUNT.apply(databaseName));
    }

    private Gauge<Long> getUserTablesAmount() {
        return asGauge(Commands.ROW_COUNT.apply("pg_stat_user_tables"));
    }

    private Gauge<Long> getSysTablesAmount() {
        return asGauge(Commands.ROW_COUNT.apply("pg_stat_sys_tables"));
    }

    private Gauge<Long> getAllTablesAmount() {
        return asGauge(Commands.ROW_COUNT.apply("pg_stat_all_tables"));
    }

    private Gauge<Long> getUserSchemataAmount() {
        return asGauge(Commands.ROW_COUNT_BY_COLUMN.apply("pg_stat_user_tables", "distinct(schemaname)"));
    }

    private <T> Gauge<T> asGauge(Command<T> command) {
        return () -> commandExecutor.execute(command).toBlocking().single();
    }
}