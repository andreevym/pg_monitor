package io.github.tbk.postgres.metrics;

import com.codahale.metrics.MetricRegistry;
import io.vertx.rxjava.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.requireNonNull;

@Slf4j
class PostgresMetricVerticle extends AbstractVerticle {

    private final MetricRegistry registry;
    private final DbMetricSet metricSet;

    PostgresMetricVerticle(MetricRegistry registry, DbMetricSet metricSet) {
        this.registry = requireNonNull(registry);
        this.metricSet = requireNonNull(metricSet);
    }

    @Override
    public void start() throws Exception {
        registry.registerAll(metricSet);
    }
}
