package io.github.tbk.postgres.metrics;

import com.codahale.metrics.ScheduledReporter;
import io.vertx.rxjava.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Slf4j
class ScheduledReporterVerticle extends AbstractVerticle {

    private final ScheduledReporter scheduledReporter;
    private final long period;
    private final TimeUnit timeUnit;

    ScheduledReporterVerticle(ScheduledReporter scheduledReporter, long period, TimeUnit timeUnit) {
        this.scheduledReporter = requireNonNull(scheduledReporter);
        this.period = period;
        this.timeUnit = requireNonNull(timeUnit);
    }

    @Override
    public void start() throws Exception {
        scheduledReporter.start(period, timeUnit);
    }

    @Override
    public void stop() throws Exception {
        scheduledReporter.stop();
    }
}
