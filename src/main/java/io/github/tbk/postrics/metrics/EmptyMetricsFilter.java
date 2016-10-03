package io.github.tbk.postrics.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;

public final class EmptyMetricsFilter implements MetricFilter {
    @Override
    public boolean matches(final String name, final Metric metric) {
        if (metric instanceof Gauge) {
            final Object val = ((Gauge) metric).getValue();
            if (val instanceof Double) {
                final Double val1 = (Double) val;
                return isValidDouble(val1);
            }
        }
        return true;
    }

    private boolean isValidDouble(final Double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }
}