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

@Slf4j
public class PgGlobalMetricSet implements MetricSet {

    private final String prefix;
    private final CommandExecutor commandExecutor;

    public PgGlobalMetricSet(String metricPrefix, CommandExecutor commandExecutor) {
        this.prefix = metricPrefix;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public Map<String, Metric> getMetrics() {
        ImmutableMap.Builder<String, Metric> registeredMetrics = ImmutableMap.builder();

        registeredMetrics.put(name(prefix, "database", "processes"), asGauge(Commands.SELECT_GLOBAL_PROCESS_COUNT));
        registeredMetrics.put(name(prefix, "database", "processes") + ",state=active", asGauge(Commands.SELECT_GLOBAL_ACTIVE_PROCESS_COUNT));
        registeredMetrics.put(name(prefix, "database", "processes") + ",state=idle", asGauge(Commands.SELECT_GLOBAL_IDLE_PROCESS_COUNT));

        registeredMetrics.put(name(prefix, "database", "size"), asGauge(Commands.SELECT_GLOBAL_SIZE));
        return registeredMetrics.build();
    }


    private <T> Gauge<T> asGauge(Command<T> command) {
        return () -> commandExecutor.execute(command).toBlocking().single();
    }
}