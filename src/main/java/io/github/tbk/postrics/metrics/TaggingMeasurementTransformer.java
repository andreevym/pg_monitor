package io.github.tbk.postrics.metrics;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import metrics_influxdb.api.measurements.MetricMeasurementTransformer;

import java.util.Map;

class TaggingMeasurementTransformer implements MetricMeasurementTransformer {
    private final String appName;

    private final Splitter.MapSplitter tagSplitter = Splitter.on(',')
            .omitEmptyStrings()
            .trimResults()
            .withKeyValueSeparator('=');

    TaggingMeasurementTransformer(String appName) {
        this.appName = appName;
    }

    @Override
    public Map<String, String> tags(final String metricName) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("app", appName);

        int commaPos = metricName.indexOf(',');
        if (commaPos > 0) {
            builder.putAll(tagSplitter.split(metricName.substring(commaPos + 1, metricName.length())));
        }

        return builder.build();
    }

    @Override
    public String measurementName(final String metricName) {
        int commaPos = metricName.indexOf(',');
        return commaPos > 0 ? metricName.substring(0, commaPos) : metricName;
    }
}