package com.teleport.util;

import java.time.Instant;

public class SnowflakeAlgo {

    private static final long CUSTOM_EPOCH = Instant.parse("2020-01-01T00:00:00Z").toEpochMilli();
    private static final int WORKER_ID_BITS = 7;
    private static final int SEQUENCE_BITS = 7;
    private static final int TIMESTAMP_BITS = 41;

    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private final int workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeAlgo(int workerId) {
        this.workerId = workerId;
    }

    public synchronized String nextId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Clock is moving backwards!. Not Possible!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        long timestampPart = (currentTimestamp - CUSTOM_EPOCH) & ((1L << TIMESTAMP_BITS) - 1);
        long id = (timestampPart << (WORKER_ID_BITS + SEQUENCE_BITS))
                | ((long) workerId << SEQUENCE_BITS)
                | sequence;

        return Long.toString(id,32).toUpperCase();
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = System.currentTimeMillis();
        }
        return currentTimestamp;
    }
}
