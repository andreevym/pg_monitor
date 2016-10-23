package io.github.tbk.pgmonitor.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Slf4jReporter;
import io.github.tbk.pgmonitor.ScheduledReporterVerticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableConfigurationProperties(DropwizardMetricsProperties.class)
public class DropwizardMetricsConfig {
    private final static long MIN_IN_SECONDS = 1;

    @Autowired
    private DropwizardMetricsProperties properties;

    @Bean
    public MetricRegistry metricRegistry() {
        return SharedMetricRegistries.getOrCreate("registry");
    }

    @Bean
    @ConditionalOnProperty("pgmonitor.metrics.console")
    public Slf4jReporter slf4jReporter() {
        return Slf4jReporter.forRegistry(metricRegistry())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
    }

    @Bean
    @ConditionalOnBean(Slf4jReporter.class)
    public ScheduledReporterVerticle slf4jReporterVerticle() {
        return new ScheduledReporterVerticle(slf4jReporter(), intervalInSeconds(), TimeUnit.SECONDS);
    }

    private long intervalInSeconds() {
        long intervalInSeconds = properties.getIntervalInSeconds();

        if (intervalInSeconds <= 0L) {
            intervalInSeconds = MIN_IN_SECONDS;
        }

        return intervalInSeconds;
    }
}
