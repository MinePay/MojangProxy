package net.minepay.mcapi.configuration;

import com.fasterxml.jackson.databind.type.TypeFactory;

import net.minepay.mcapi.mojang.Profile;
import net.minepay.mcapi.mojang.ProfileName;
import net.minepay.mcapi.mojang.ProfileNameChange;
import net.minepay.mcapi.mojang.cache.JacksonRedisSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

import javax.annotation.Nonnull;

import redis.clients.jedis.JedisPoolConfig;

/**
 * Provides a Redis connection configuration.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Configuration
public class RedisConfiguration {
    @Value("${redis.hostname:localhost}")
    private String hostname;
    @Value("${redis.port:6379}")
    private int portNumber;
    @Value("${redis.database-number:1}")
    private int databaseNumber;
    @Value("${redis.password:}")
    private String password;
    @Value("${redis.pool-size:12}")
    private int poolSize;

    @Bean
    @Nonnull
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxIdle(-1);
        poolConfig.setMaxWaitMillis(5);
        poolConfig.setMinIdle(this.poolSize);
        poolConfig.setMaxTotal(this.poolSize);

        poolConfig.setBlockWhenExhausted(false);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);

        return poolConfig;
    }

    @Bean
    @Nonnull
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(this.jedisPoolConfig());
        connectionFactory.setUsePool(true);

        connectionFactory.setHostName(this.hostname);
        connectionFactory.setPort(this.portNumber);
        connectionFactory.setDatabase(this.databaseNumber);

        if (this.password != null && !this.password.isEmpty()) {
            connectionFactory.setPassword(this.password);
        }

        return connectionFactory;
    }

    /**
     * Provides a Redis template for Profile objects.
     *
     * @return a template.
     */
    @Bean
    @Nonnull
    public RedisTemplate<String, Profile> profileRedisTemplate() {
        RedisTemplate<String, Profile> template = new RedisTemplate<>();
        template.setConnectionFactory(this.redisConnectionFactory());
        template.setDefaultSerializer(new JacksonRedisSerializer<>(Profile.class));
        template.setKeySerializer(new StringRedisSerializer());
        template.setEnableTransactionSupport(true);
        return template;
    }

    /**
     * Provides a Redis template for profile name objects.
     *
     * @return a template.
     */
    @Bean
    @Nonnull
    public RedisTemplate<String, ProfileName> identifierRedisTemplate() {
        RedisTemplate<String, ProfileName> template = new RedisTemplate<>();
        template.setConnectionFactory(this.redisConnectionFactory());
        template.setDefaultSerializer(new JacksonRedisSerializer<>(ProfileName.class));
        template.setKeySerializer(new StringRedisSerializer());
        template.setEnableTransactionSupport(true);
        return template;
    }

    /**
     * Provides a Redis template for name change histories.
     *
     * @return a template.
     */
    @Bean
    @Nonnull
    public RedisTemplate<String, List<ProfileNameChange>> nameHistoryRedisTemplate() {
        RedisTemplate<String, List<ProfileNameChange>> template = new RedisTemplate<>();
        template.setConnectionFactory(this.redisConnectionFactory());
        template.setDefaultSerializer(new JacksonRedisSerializer<>(TypeFactory.defaultInstance().constructCollectionType(List.class, ProfileNameChange.class)));
        template.setKeySerializer(new StringRedisSerializer());
        template.setEnableTransactionSupport(true);
        return template;
    }
}
