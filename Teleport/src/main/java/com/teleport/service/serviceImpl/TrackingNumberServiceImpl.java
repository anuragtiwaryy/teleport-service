package com.teleport.service.serviceImpl;

import com.teleport.dto.TrackingNumberRequest;
import com.teleport.dto.TrackingNumberResponse;
import com.teleport.service.TrackingNumberService;
import com.teleport.util.RedisWorkerIdAllocator;
import com.teleport.util.SnowflakeAlgo;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@Slf4j
public class TrackingNumberServiceImpl implements TrackingNumberService {

    private final RedisWorkerIdAllocator allocator;
    private final SnowflakeAlgo snowflake;

    public TrackingNumberServiceImpl(StringRedisTemplate redisTemplate) {
        this.allocator = new RedisWorkerIdAllocator(redisTemplate, 128, 30);
        int workerId = allocator.allocate();
        this.snowflake = new SnowflakeAlgo(workerId);
        log.info("Assigned workerId = {} for this Instance", workerId);
    }

    @Override
    public TrackingNumberResponse generateTrackingNumber(TrackingNumberRequest req) {

        String uniqueId = snowflake.nextId();

        String route = (req.getOrigin_country_id() + req.getDestination_country_id()).toUpperCase();

        String token = route + getWeightClass(req.getWeight()) + uniqueId;
        token = token.length() <= 16 ? token : token.substring(0, 16);

        return TrackingNumberResponse.builder()
                .trackingNumber(token)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    private String getWeightClass(BigDecimal weight) {
        if (weight.compareTo(BigDecimal.ONE) <= 5) return "L";
        else if (weight.compareTo(new BigDecimal("10")) <= 0) return "M";
        else return "H";
    }

    @PreDestroy
    public void cleanup() {
        allocator.close();
    }
}
