package io.github.tbk.pgmonitor.metrics.influxdb;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import metrics_influxdb.InfluxdbReporter;
import metrics_influxdb.api.protocols.InfluxdbProtocol;
import metrics_influxdb.api.protocols.InfluxdbProtocols;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

public class Influxdb {
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;

    @Builder
    public Influxdb(String host, int port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public InfluxDB createClient() {
        String url = "http://" + host + ":" + port;
        return InfluxDBFactory.connect(url, user, password);
    }

    public InfluxdbReporter.Builder createScheduledReporter(final String appName, final MetricRegistry registry) {
        checkArgument(!Strings.isNullOrEmpty(appName), "influxdb reporter needs a valid application name to work properly");

        final ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder().put("app", appName).build();

        return InfluxdbReporter
                .forRegistry(registry)
                .protocol(influxdbProtocol())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(new EmptyMetricsFilter())
                .transformer(new TaggingMeasurementTransformer(metricName -> tags));
    }

    private InfluxdbProtocol influxdbProtocol() {
        return InfluxdbProtocols.http(host, port, user, password, database);
    }
}
