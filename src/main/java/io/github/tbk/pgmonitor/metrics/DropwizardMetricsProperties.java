package io.github.tbk.pgmonitor.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pgmonitor.metrics")
public class DropwizardMetricsProperties {

    private boolean console;
    private long intervalInSeconds;

    public boolean isConsole() {
        return console;
    }

    public void setConsole(boolean console) {
        this.console = console;
    }

    public long getIntervalInSeconds() {
        return intervalInSeconds;
    }

    public void setIntervalInSeconds(long intervalInSeconds) {
        this.intervalInSeconds = intervalInSeconds;
    }
}
