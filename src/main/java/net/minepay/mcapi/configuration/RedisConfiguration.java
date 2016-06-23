package net.minepay.mcapi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

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

    /**
     * Provides a Redis connection factory.
     *
     * @return a connection factory.
     */
    @Bean
    @Nonnull
    public RedisConnectionFactory redisConnectionFactory() {
        // TODO: Cluster configuration?

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(this.poolSize);

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);
        connectionFactory.setHostName(this.hostname);
        connectionFactory.setPort(this.portNumber);
        connectionFactory.setDatabase(this.databaseNumber);

        if (this.password != null && !this.password.isEmpty()) {
            connectionFactory.setPassword(this.password);
        }

        return connectionFactory;
    }

    /**
     * Provides a Redis template.
     *
     * @return a template.
     */
    @Bean
    @Nonnull
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(this.redisConnectionFactory());
        return template;
    }
}
