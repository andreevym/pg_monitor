package io.github.tbk.pgmonitor.postgres;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.google.common.collect.ImmutableMap;
import io.github.tbk.pgmonitor.postgres.command.Command;
import io.github.tbk.pgmonitor.postgres.command.CommandExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;

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

        //registeredMetrics.put(name(prefix, "version"), asGauge(Commands.SHOW_VERSION, PgVersion::getNumeric));

        return registeredMetrics.build();
    }

    private <T, K> Gauge<T> asGauge(Command<K> command, Function<K, T> mapper) {
        return () -> commandExecutor.execute(command)
                .map(mapper::apply)
                .toBlocking().single();
    }
}