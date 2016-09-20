package io.github.tbk.postgres.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
class AppConfiguration {

    private Environment environment;

    @Autowired
    public AppConfiguration(Environment environment) {
        this.environment = environment;
    }

    String appName() {
        return environment.getProperty("appName", "app");
    }

    int httpPort() {
        return environment.getProperty("http.port", Integer.class, 8080);
    }

    String databaseDbName() {
        return environment.getProperty("db.name", "postgres");
    }

    String databaseUser() {
        return environment.getProperty("db.user", "postgres");
    }

    String databasePassword() {
        return environment.getProperty("db.password", "postgres");
    }

    String databaseServerName() {
        return environment.getProperty("db.serverName", "localhost");
    }

    int databasePort() {
        return environment.getProperty("db.port", Integer.class, 5432);
    }

    String metricsDatabaseName() {
        return environment.getProperty("metrics.name", "metrics");
    }

    String metricsUser() {
        return environment.getProperty("metrics.user", "metrics");
    }

    String metricsPassword() {
        return environment.getProperty("metrics.password", "metrics");
    }

    String metricsServerName() {
        return environment.getProperty("metrics.serverName", "localhost");
    }

    int metricsPort() {
        return environment.getProperty("metrics.port", Integer.class, 8086);
    }



}
