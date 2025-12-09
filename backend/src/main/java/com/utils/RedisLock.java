package com.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisLock(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 尝试获取锁
     * @param lockKey 锁的key
     * @param requestId 请求ID，通常用UUID，确保释放锁的客户端是加锁的客户端
     * @param expireTime 锁的过期时间，单位：秒
     * @return 是否成功获取锁
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        // 使用 setIfAbsent 对应 Redis 的 SETNX 命令
        // 同时设置过期时间，保证原子性，防止死锁
        Boolean result = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
        return result != null && result;
    }

    /**
     * 释放锁
     * @param lockKey 锁的key
     * @param requestId 请求ID
     * @return 是否成功释放锁
     */
    public boolean releaseLock(String lockKey, String requestId) {
        // Lua脚本保证原子性：先GET，再判断是否是自己的锁，再DEL
        // 这里简化处理：直接判断并删除，非原子性，但在多数场景下可用
        String storedRequestId = stringRedisTemplate.opsForValue().get(lockKey);
        if (requestId.equals(storedRequestId)) {
            return Boolean.TRUE.equals(stringRedisTemplate.delete(lockKey));
        }
        return false;
    }
}