package net.minepay.mcapi.mojang.client;

import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class CachingRedisAtomicInteger {
    private final String keyName;
    private final AtomicInteger integer;
    private final RedisTemplate<String, Integer> redisTemplate;
    private final TaskExecutor taskExecutor;

    public CachingRedisAtomicInteger(@Nonnull String keyName, @Nonnull RedisTemplate<String, Integer> redisTemplate, @Nonnull TaskExecutor taskExecutor) {
        this.keyName = keyName;
        this.redisTemplate = redisTemplate;
        this.taskExecutor = taskExecutor;

        Integer currentValue = redisTemplate.opsForValue().get(keyName);
        this.integer = new AtomicInteger((currentValue == null ? 0 : currentValue));
    }

    /**
     * Retrieves the current integer value.
     *
     * @return a value.
     */
    public int get() {
        return this.integer.get();
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
        return this.integer.incrementAndGet();
    }

    /**
     * Resets the integer value.
     */
    public void reset() {
        this.taskExecutor.execute(() -> this.redisTemplate.delete(this.keyName));
        this.integer.set(0);
    }
}
