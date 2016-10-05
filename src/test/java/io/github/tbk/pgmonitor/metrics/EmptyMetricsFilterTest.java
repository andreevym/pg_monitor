package io.github.tbk.pgmonitor.metrics;

import com.codahale.metrics.Gauge;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EmptyMetricsFilterTest {

    private EmptyMetricsFilter sut;

    @Before
    public void setUp() {
        this.sut = new EmptyMetricsFilter();
    }

    @Test
    public void itShouldFilterDoubleValueNan() throws Exception {
        final boolean matches = this.sut.matches("test", (Gauge<Double>) () -> Double.NaN);
        assertThat(matches, is(false));
    }

    @Test
    public void itShouldNotFilterDoubleValueZero() throws Exception {
        final boolean matches = this.sut.matches("test", (Gauge<Double>) () -> 0d);
        assertThat(matches, is(true));
    }
}