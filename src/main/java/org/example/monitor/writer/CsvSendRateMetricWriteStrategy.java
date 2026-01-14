package org.example.monitor.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.example.monitor.MessageMetric;

public class CsvSendRateMetricWriteStrategy implements ISendRateMetricWriteStrategy {

    private final String filepath;

    private BufferedWriter writer;

    public CsvSendRateMetricWriteStrategy(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void write(double reqRate) {
        if (writer == null) {
            try {
                this.writer = new BufferedWriter(new FileWriter(filepath));
                writer.append("SendRate\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            writer.append(String.valueOf(reqRate))
                    .append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean commit() {
        try {
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
