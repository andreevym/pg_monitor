package io.github.tbk.postrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Maps;
import io.github.tbk.postrics.postgres.PgMetricSet;
import io.vertx.rxjava.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

@Slf4j
class PostgresMetricVerticle extends AbstractVerticle {
    private final static long MIN_PERIOD_IN_MS = 1_000;

    private final Map<String, Metric> metrics = Maps.newConcurrentMap();

    private final MetricRegistry registry;
    private final PgMetricSet metricSet;
    private final long period;
    private final TimeUnit timeUnit;
    private final long initialDelayInMs;

    PostgresMetricVerticle(MetricRegistry registry, PgMetricSet metricSet, long period, TimeUnit timeUnit) {
        requireNonNull(timeUnit);
        checkArgument(timeUnit.toMillis(period) >= MIN_PERIOD_IN_MS);

        this.registry = requireNonNull(registry);
        this.metricSet = requireNonNull(metricSet);
        this.period = period;
        this.timeUnit = timeUnit;
        this.initialDelayInMs = 200L;
    }

    @Override
    public void start() throws Exception {
        vertx.setTimer(initialDelayInMs, timerId -> {
            registerAbsentMetrics();
            vertx.setPeriodic(timeUnit.toMillis(period), intervalId -> {
                registerAbsentMetrics();
            });
        });

    }

    private void registerAbsentMetrics() {
        metricSet.getMetrics().forEach((key, value) -> {
            metrics.computeIfAbsent(key, k -> registry.register(key, value));
        });
    }
}
