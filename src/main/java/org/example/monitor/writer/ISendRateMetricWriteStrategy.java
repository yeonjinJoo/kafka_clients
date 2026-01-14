package org.example.monitor.writer;

public interface ISendRateMetricWriteStrategy {
    void write(double log);

    boolean commit();
}
