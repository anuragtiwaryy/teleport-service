package com.teleport.util;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RedisWorkerIdAllocator implements AutoCloseable {

    private static final String WORKER_KEY_PREFIX = "SNOWFLAKE:WORKER:";
    private final StringRedisTemplate redisTemplate;
    private final int maxWorkerId;
    private final int ttlSeconds;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final String instanceId;
    private int workerId = -1;

    public RedisWorkerIdAllocator(StringRedisTemplate redisTemplate, int maxWorkerId, int ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.maxWorkerId = maxWorkerId;
        this.ttlSeconds = ttlSeconds;
        this.instanceId = UUID.randomUUID().toString();
    }

    public int allocate() {
        for (int i = 0; i < maxWorkerId; i++) {
            String key = WORKER_KEY_PREFIX + i;
            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, instanceId, ttlSeconds, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(success)) {
                this.workerId = i;
                startHeartbeat(key);
                return i;
            }
        }
        throw new IllegalStateException("No available workerId slots in Redis");
    }

    private void startHeartbeat(String key) {
        scheduler.scheduleAtFixedRate(() -> {
            String value = redisTemplate.opsForValue().get(key);
            if (instanceId.equals(value)) {
                redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
            }
        }, ttlSeconds / 3, ttlSeconds / 3, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        if (workerId >= 0) {
            String key = WORKER_KEY_PREFIX + workerId;
            String value = redisTemplate.opsForValue().get(key);
            if (instanceId.equals(value)) {
                redisTemplate.delete(key);
            }
        }
        scheduler.shutdown();
    }
}
