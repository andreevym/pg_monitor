package io.github.tbk.pgmonitor;

import com.codahale.metrics.ScheduledReporter;
import io.vertx.rxjava.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

@Slf4j
public class ScheduledReporterVerticle extends AbstractVerticle {
    private final static long MIN_PERIOD_IN_MS = 1_000;

    private final ScheduledReporter scheduledReporter;
    private final long period;
    private final TimeUnit timeUnit;
    private final long initialDelayInMs;

    public ScheduledReporterVerticle(ScheduledReporter scheduledReporter, long period, TimeUnit timeUnit) {
        requireNonNull(timeUnit);
        checkArgument(timeUnit.toMillis(period) >= MIN_PERIOD_IN_MS);

        this.scheduledReporter = requireNonNull(scheduledReporter);
        this.timeUnit = requireNonNull(timeUnit);
        this.period = period;
        this.initialDelayInMs = 200L;
    }

    @Override
    public void start() throws Exception {
        vertx.setTimer(initialDelayInMs, timerId -> {
            scheduledReporter.report();
            scheduledReporter.start(period, timeUnit);
        });
    }

    @Override
    public void stop() throws Exception {
        scheduledReporter.stop();
    }
}
