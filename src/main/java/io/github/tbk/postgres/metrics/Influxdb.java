package io.github.tbk.postgres.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Strings;
import lombok.Builder;
import metrics_influxdb.InfluxdbReporter;
import metrics_influxdb.api.protocols.InfluxdbProtocol;
import metrics_influxdb.api.protocols.InfluxdbProtocols;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

class Influxdb {
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;

    @Builder
    Influxdb(String host, int port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    InfluxDB createClient() {
        String url = "http://" + host + ":" + port;
        return InfluxDBFactory.connect(url, user, password);
    }

    InfluxdbReporter.Builder createScheduledReporter(final String appName, final MetricRegistry registry) {
        checkArgument(!Strings.isNullOrEmpty(appName), "influxdb reporter needs a valid application name to work properly");

        return InfluxdbReporter
                .forRegistry(registry)
                .protocol(influxdbProtocol())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(new EmptyMetricsFilter())
                .transformer(new TaggingMeasurementTransformer(appName));

    }

    private InfluxdbProtocol influxdbProtocol() {
        return InfluxdbProtocols.http(host, port, user, password, database);
    }
}