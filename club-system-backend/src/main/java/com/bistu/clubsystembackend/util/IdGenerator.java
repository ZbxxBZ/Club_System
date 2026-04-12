package com.bistu.clubsystembackend.util;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class IdGenerator {

    private static final long EPOCH = 1704067200000L;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public synchronized long nextId() {
        long timestamp = Instant.now().toEpochMilli();
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & 4095;
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = Instant.now().toEpochMilli();
                }
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - EPOCH) << 12) | sequence;
    }
}
