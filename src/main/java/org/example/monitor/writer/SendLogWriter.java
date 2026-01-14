package org.example.monitor.writer;

import org.example.util.IMessageAdaptor;

import java.util.Queue;

// TODO: 일정 시간동안 monitorQueue에 있는 데이터가 flush안되면 자동으로 flush해주는 기능 추가하기
public class SendLogWriter implements Runnable {

    private final Queue<Double> monitorQueue;

    private final ISendRateMetricWriteStrategy writeStrategy;

    private final IMessageAdaptor messageGenerator;

    private final int batchSize;

    private boolean terminated = false;

    private int curWrittenCnt = 0;

    public SendLogWriter(ISendRateMetricWriteStrategy writeStrategy, IMessageAdaptor messageGenerator, int batchSize, Queue<Double> monitorQueue) {
        this.writeStrategy = writeStrategy;
        this.messageGenerator = messageGenerator;
        this.batchSize = batchSize;
        this.monitorQueue = monitorQueue;
    }

    public void gracefulShutdown() {
        terminated = true;
    }

    public void syncedWait() {
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void syncedNotify() {
        synchronized (this) {
            notify();
        }
    }

    public void notifyIfNeeded() {
        if (monitorQueue.size() >= batchSize) {
            syncedNotify();
        }
    }

    @Override
    public void run() {
        while (!(terminated && monitorQueue.isEmpty())) {
            if (!terminated && monitorQueue.isEmpty()) {
                syncedWait();
            }
            double log = monitorQueue.poll();
            writeStrategy.write(log);
            curWrittenCnt += 1;
            tryFlushBatch();
        }
        flushBatch();
    }

    private void tryFlushBatch() {
        if (curWrittenCnt >= batchSize) {
            flushBatch();
            if (!terminated && monitorQueue.size() < batchSize) {
                syncedWait();
            }
        }
    }

    private void flushBatch() {
        writeStrategy.commit();
        curWrittenCnt = 0;
    }

}