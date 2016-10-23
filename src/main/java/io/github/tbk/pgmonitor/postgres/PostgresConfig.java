package io.github.tbk.pgmonitor.postgres;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.tbk.pgmonitor.postgres.command.CommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
public class PostgresConfig {

    @Autowired
    private DatabaseProperties database;

    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    @Bean
    public PgGlobalMetricSet globalMetricsSet() {
        return new PgGlobalMetricSet("postgres", commandExecutor());
    }

    @Bean
    public CommandExecutor commandExecutor() {
        return new CommandExecutor(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        log.info("setup data source {}@{}:{}/{}", database.getUser(), database.getHost(),
                database.getPort(), database.getDb());

        Properties props = new Properties();
        props.setProperty("dataSourceClassName", org.postgresql.ds.PGSimpleDataSource.class.getName());
        props.setProperty("dataSource.serverName", database.getHost());
        props.setProperty("dataSource.portNumber", database.getPort());
        props.setProperty("dataSource.databaseName", database.getDb());
        props.setProperty("dataSource.user", database.getUser());
        props.setProperty("dataSource.password", database.getPassword());

        HikariConfig config = new HikariConfig(props);
        config.setMaximumPoolSize(1);
        config.setPoolName("hikari-" + database.getDb());

        if (metricRegistry != null) {
            config.setMetricRegistry(metricRegistry);
        }

        return new HikariDataSource(config);
    }

    @Bean
    @ConditionalOnBean(MetricRegistry.class)
    public PostgresMetricVerticle globalMetricsVerticle() {
        return new PostgresMetricVerticle(metricRegistry, globalMetricsSet(), 60, TimeUnit.SECONDS);
    }
}
