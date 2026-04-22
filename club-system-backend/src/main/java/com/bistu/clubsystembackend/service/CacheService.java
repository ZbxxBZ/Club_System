package com.bistu.clubsystembackend.service;

import com.bistu.clubsystembackend.entity.AuthSessionInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    private static final String SESSION_PREFIX = "session:";
    private static final String CLUB_VIEWS_PREFIX = "club:views:";
    private static final String HOT_CLUBS_KEY = "club:hot:top20";
    private static final String EVENT_SLOTS_PREFIX = "event:slots:";
    private static final String EVENT_QUOTA_PREFIX = "event:quota:";
    private static final String EVENT_LOCK_PREFIX = "event:lock:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public CacheService(RedisTemplate<String, Object> redisTemplate,
                        StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // ===== Session cache (24h) =====

    public void cacheSessionInfo(Long userId, AuthSessionInfo info) {
        try {
            redisTemplate.opsForValue().set(SESSION_PREFIX + userId, info, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("Redis write failed for session:{}", userId, e);
        }
    }

    public AuthSessionInfo getSessionInfo(Long userId) {
        try {
            Object obj = redisTemplate.opsForValue().get(SESSION_PREFIX + userId);
            if (obj instanceof AuthSessionInfo) {
                return (AuthSessionInfo) obj;
            }
        } catch (Exception e) {
            log.warn("Redis read failed for session:{}, falling back to DB", userId, e);
        }
        return null;
    }

    public void evictSessionInfo(Long userId) {
        try {
            redisTemplate.delete(SESSION_PREFIX + userId);
        } catch (Exception e) {
            log.warn("Redis delete failed for session:{}", userId, e);
        }
    }

    // ===== Hot clubs (ZSET by day, TOP20 cached 1h) =====

    public void incrementClubView(Long clubId) {
        try {
            String dayKey = CLUB_VIEWS_PREFIX + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            redisTemplate.opsForZSet().incrementScore(dayKey, clubId.toString(), 1);
            redisTemplate.expire(dayKey, 8, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Redis incrementClubView failed for club:{}", clubId, e);
        }
    }

    public List<Long> getTopViewedClubIds(int topN) {
        try {
            String tempKey = "club:views:_temp_union";
            LocalDate today = LocalDate.now();
            DateTimeFormatter fmt = DateTimeFormatter.BASIC_ISO_DATE;
            String[] dayKeys = new String[7];
            for (int i = 0; i < 7; i++) {
                dayKeys[i] = CLUB_VIEWS_PREFIX + today.minusDays(i).format(fmt);
            }
            redisTemplate.opsForZSet().unionAndStore(dayKeys[0],
                    List.of(java.util.Arrays.copyOfRange(dayKeys, 1, dayKeys.length)), tempKey);
            Set<ZSetOperations.TypedTuple<Object>> tuples =
                    redisTemplate.opsForZSet().reverseRangeWithScores(tempKey, 0, topN - 1);
            redisTemplate.delete(tempKey);
            if (tuples == null || tuples.isEmpty()) {
                return Collections.emptyList();
            }
            List<Long> ids = new ArrayList<>();
            for (ZSetOperations.TypedTuple<Object> t : tuples) {
                if (t.getValue() != null) {
                    ids.add(Long.parseLong(t.getValue().toString()));
                }
            }
            return ids;
        } catch (Exception e) {
            log.warn("Redis getTopViewedClubIds failed", e);
            return Collections.emptyList();
        }
    }

    public <T> void cacheHotClubs(List<T> clubs) {
        try {
            String json = objectMapper.writeValueAsString(clubs);
            stringRedisTemplate.opsForValue().set(HOT_CLUBS_KEY, json, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("Redis cacheHotClubs failed", e);
        }
    }

    public <T> List<T> getHotClubs(TypeReference<List<T>> typeRef) {
        try {
            String json = stringRedisTemplate.opsForValue().get(HOT_CLUBS_KEY);
            if (json != null) {
                return objectMapper.readValue(json, typeRef);
            }
        } catch (Exception e) {
            log.warn("Redis getHotClubs failed", e);
        }
        return null;
    }

    // ===== Event slots display cache (10s) =====

    public void cacheEventSlots(Long eventId, int remaining) {
        try {
            stringRedisTemplate.opsForValue().set(EVENT_SLOTS_PREFIX + eventId,
                    String.valueOf(remaining), 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis cacheEventSlots failed for event:{}", eventId, e);
        }
    }

    public Integer getEventSlots(Long eventId) {
        try {
            String val = stringRedisTemplate.opsForValue().get(EVENT_SLOTS_PREFIX + eventId);
            if (val != null) {
                return Integer.parseInt(val);
            }
        } catch (Exception e) {
            log.warn("Redis getEventSlots failed for event:{}", eventId, e);
        }
        return null;
    }

    // ===== Event quota atomic operations =====

    public void initEventQuota(Long eventId, int count) {
        try {
            stringRedisTemplate.opsForValue().set(EVENT_QUOTA_PREFIX + eventId,
                    String.valueOf(Math.max(0, count)), 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("Redis initEventQuota failed for event:{}", eventId, e);
        }
    }

    public long decrementEventQuota(Long eventId) {
        Long result = stringRedisTemplate.opsForValue().decrement(EVENT_QUOTA_PREFIX + eventId);
        return result != null ? result : -1;
    }

    public long incrementEventQuota(Long eventId) {
        try {
            Long result = stringRedisTemplate.opsForValue().increment(EVENT_QUOTA_PREFIX + eventId);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.warn("Redis incrementEventQuota failed for event:{}", eventId, e);
            return 0;
        }
    }

    public Long getEventQuota(Long eventId) {
        try {
            String val = stringRedisTemplate.opsForValue().get(EVENT_QUOTA_PREFIX + eventId);
            return val != null ? Long.parseLong(val) : null;
        } catch (Exception e) {
            log.warn("Redis getEventQuota failed for event:{}", eventId, e);
            return null;
        }
    }

    // ===== Signup dedup lock =====

    public boolean trySignupLock(Long eventId, Long userId, long timeoutMs) {
        try {
            String key = EVENT_LOCK_PREFIX + eventId + ":" + userId;
            Boolean success = stringRedisTemplate.opsForValue()
                    .setIfAbsent(key, "1", timeoutMs, TimeUnit.MILLISECONDS);
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            log.warn("Redis trySignupLock failed", e);
            return true; // degrade: allow through
        }
    }

    public void releaseSignupLock(Long eventId, Long userId) {
        try {
            stringRedisTemplate.delete(EVENT_LOCK_PREFIX + eventId + ":" + userId);
        } catch (Exception e) {
            log.warn("Redis releaseSignupLock failed", e);
        }
    }

    // ===== Config cache (no TTL) =====

    public void setConfig(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.warn("Redis setConfig failed for key:{}", key, e);
        }
    }

    public String getConfig(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Redis getConfig failed for key:{}", key, e);
            return null;
        }
    }

    // ===== Generic delete =====

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis delete failed for key:{}", key, e);
        }
    }
}
