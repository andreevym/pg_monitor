package io.github.tbk.pgmonitor.metrics.influxdb;

import com.codahale.metrics.MetricRegistry;
import io.github.tbk.pgmonitor.AppConfiguration;
import io.github.tbk.pgmonitor.ScheduledReporterVerticle;
import lombok.extern.slf4j.Slf4j;
import metrics_influxdb.measurements.MeasurementReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Slf4j
@Configuration
@EnableConfigurationProperties(InfluxdbProperties.class)
@ConditionalOnProperty("pgmonitor.influxdb.enabled")
public class InfluxdbMetricsConfig {

    @Autowired
    private InfluxdbProperties properties;


    private final AppConfiguration appConfig;
    private final MetricRegistry metricRegistry;

    public InfluxdbMetricsConfig(AppConfiguration appConfig, MetricRegistry metricRegistry) {
        this.appConfig = appConfig;
        this.metricRegistry = metricRegistry;
    }

    @Bean
    public Influxdb influxdb() {
        final Influxdb build = Influxdb.builder()
                .database(properties.getDatabase())
                .host(properties.getHost())
                .port(Integer.valueOf(properties.getPort()))
                .user(properties.getUsername())
                .password(properties.getPassword())
                .build();

        build.createClient()
                .createDatabase(properties.getDatabase());

        return build;
    }

    @Bean
    public MeasurementReporter influxdbReporter() {
        log.info("setup influxdb reporter: {}@{}:{}/{}", properties.getUsername(), properties.getHost(),
                properties.getPort(), properties.getDatabase());

        return (MeasurementReporter) influxdb().createScheduledReporter(appConfig.appName(), metricRegistry).build();
    }


    @Bean
    public ScheduledReporterVerticle scheduledReporterVerticle() {
        return new ScheduledReporterVerticle(influxdbReporter(), 20, TimeUnit.SECONDS);
    }
}
