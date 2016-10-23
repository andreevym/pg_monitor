package io.github.tbk.pgmonitor;

import io.github.tbk.pgmonitor.postgres.DatabaseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
public class AppConfiguration {

    private final Environment environment;

    @Autowired
    public AppConfiguration(Environment environment) {
        this.environment = environment;
    }

    public String appName() {
        return environment.getProperty("appName", "app");
    }

    public int httpPort() {
        return environment.getProperty("http.port", Integer.class, 8080);
    }
}
