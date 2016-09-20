package io.github.tbk.postgres.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Slf4jReporter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.tbk.postgres.metrics.postgres.PostgresDao;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Configuration
class BeanConfiguration {

    private AppConfiguration appConfig;

    @Autowired
    public BeanConfiguration(AppConfiguration appConfig) {
        this.appConfig = appConfig;
    }

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean
    public StaticServer staticServer() {
        return new StaticServer(appConfig);
    }

    @Bean
    public PostgresMetricVerticle postgresMetricVerticle() {
        return new PostgresMetricVerticle(metricRegistry(), postgresMetricsSet());
    }

    @Bean
    public ScheduledReporterVerticle scheduledReporterVerticle() {
        return new ScheduledReporterVerticle(influxdbReporter(), 10, TimeUnit.SECONDS);
    }

    @Bean
    public ScheduledReporterVerticle slf4jReporterVerticle() {
        return new ScheduledReporterVerticle(slf4jReporter(), 10, TimeUnit.SECONDS);
    }

    @Bean
    public Influxdb influxdb() {
        final Influxdb build = Influxdb.builder()
                .database(appConfig.metricsDatabaseName())
                .host(appConfig.metricsServerName())
                .port(appConfig.metricsPort())
                .user(appConfig.metricsUser())
                .password(appConfig.databasePassword())
                .build();

        build.createClient()
                .createDatabase(appConfig.metricsDatabaseName());

        return build;
    }

    @Bean
    public ScheduledReporter influxdbReporter() {
        return influxdb().createScheduledReporter(appConfig.appName(), metricRegistry()).build();
    }

    @Bean
    public ScheduledReporter slf4jReporter() {
        return Slf4jReporter.forRegistry(metricRegistry()).build();
    }


    @Bean
    public DbMetricSet postgresMetricsSet() {
        return new DbMetricSet("postgres", postgresDao());
    }

    @Bean
    public MetricRegistry metricRegistry() {
        return SharedMetricRegistries.getOrCreate("registry");
    }

    @Bean
    public PostgresDao postgresDao() {
        return new PostgresDao(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", org.postgresql.ds.PGSimpleDataSource.class.getName());
        props.setProperty("dataSource.serverName", appConfig.databaseServerName());
        props.setProperty("dataSource.portNumber", String.valueOf(appConfig.databasePort()));
        //props.setProperty("dataSource.databaseName", appConfig.databaseDbName());
        props.setProperty("dataSource.user", appConfig.databaseUser());
        props.setProperty("dataSource.password", appConfig.databasePassword());

        HikariConfig config = new HikariConfig(props);
        config.setMaximumPoolSize(1);
        config.setPoolName("hikari-" + appConfig.databaseDbName());
        config.setMetricRegistry(metricRegistry());

        return new HikariDataSource(config);
    }
}
