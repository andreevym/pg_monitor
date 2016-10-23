package io.github.tbk.pgmonitor.metrics.influxdb;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import metrics_influxdb.api.measurements.MetricMeasurementTransformer;

import java.util.Map;
import java.util.function.Function;

class TaggingMeasurementTransformer implements MetricMeasurementTransformer {
    private final Splitter.MapSplitter tagSplitter = Splitter.on(',')
            .omitEmptyStrings()
            .trimResults()
            .withKeyValueSeparator('=');
    private Function<String, Map<String, String>> tagsSupplier;

    TaggingMeasurementTransformer(Function<String, Map<String, String>> staticTagsSupplier) {
        this.tagsSupplier = staticTagsSupplier;
    }

    @Override
    public Map<String, String> tags(final String metricName) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder()
                .putAll(tagsSupplier.apply(metricName));

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
