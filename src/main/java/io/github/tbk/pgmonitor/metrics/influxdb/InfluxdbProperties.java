package io.github.tbk.pgmonitor.metrics.influxdb;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pgmonitor.influxdb")
public class InfluxdbProperties {

    private boolean enabled;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private boolean skipIdleMetrics;
    private long intervalInSeconds;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSkipIdleMetrics() {
        return skipIdleMetrics;
    }

    public void setSkipIdleMetrics(boolean skipIdleMetrics) {
        this.skipIdleMetrics = skipIdleMetrics;
    }

    public long getIntervalInSeconds() {
        return intervalInSeconds;
    }

    public void setIntervalInSeconds(long intervalInSeconds) {
        this.intervalInSeconds = intervalInSeconds;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
