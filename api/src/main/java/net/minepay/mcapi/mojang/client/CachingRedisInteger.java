package net.minepay.mcapi.mojang.client;

import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@NotThreadSafe
public class CachingRedisInteger {
    private final String keyName;
    private int integer;
    private final RedisTemplate<String, Integer> redisTemplate;
    private final TaskExecutor taskExecutor;

    public CachingRedisInteger(@Nonnull String keyName, @Nonnull RedisTemplate<String, Integer> redisTemplate, @Nonnull TaskExecutor taskExecutor) {
        this.keyName = keyName;
        this.redisTemplate = redisTemplate;
        this.taskExecutor = taskExecutor;

        Integer currentValue = redisTemplate.opsForValue().get(keyName);
        this.integer = (currentValue == null ? 0 : currentValue);
    }

    /**
     * Retrieves the current integer value.
     *
     * @return a value.
     */
    public int get() {
        return this.integer;
    }

    /**
     * Increments the current integer value.
     *
     * @return a new value.
     */
    public int increment() {
        this.taskExecutor.execute(() -> {
            this.redisTemplate.opsForValue().increment(this.keyName, 1);
            this.redisTemplate.expire(this.keyName, 10, TimeUnit.MINUTES);
        });
        return this.integer++;
    }

    /**
     * Resets the integer value.
     */
    public void reset() {
        this.integer = 0;
    }
}
