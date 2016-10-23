package io.github.tbk.pgmonitor.metrics.influxdb;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import io.github.tbk.pgmonitor.AppConfiguration;
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
@ConditionalOnBean(MetricRegistry.class)
@ConditionalOnProperty("pgmonitor.influxdb.enabled")
@EnableConfigurationProperties(InfluxdbProperties.class)
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
    public ScheduledReporter influxdbReporter() {
        log.info("prepare influxdb reporter: {}:{}", properties.getHost(), properties.getPort());
        return influxdb().createScheduledReporter(appConfig.appName(), metricRegistry).build();
    }


    @Bean
    public ScheduledReporterVerticle scheduledReporterVerticle() {
        return new ScheduledReporterVerticle(influxdbReporter(), 20, TimeUnit.SECONDS);
    }
}
