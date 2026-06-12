package org.example.salaryms.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class IdempotencyGuard {

    StringRedisTemplate redisTemplate;
    static Duration TTL = Duration.ofHours(24);

    public boolean tryAcquire(String idempotencyKey) {
        Boolean isNew = redisTemplate
                .opsForValue()
                .setIfAbsent("idempotency:" + idempotencyKey, "1", TTL);
        return Boolean.TRUE.equals(isNew);
    }
}
