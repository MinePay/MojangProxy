package net.minepay.mcapi.mojang.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Provides a simple Jackson based serialization interface.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@ThreadSafe
public class JacksonRedisSerializer<T> implements RedisSerializer<T> {
    private final ObjectReader reader;
    private final ObjectWriter writer;

    public JacksonRedisSerializer(@Nonnull Class<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.reader = mapper.readerFor(type);
        this.writer = mapper.writerFor(type);
    }

    public JacksonRedisSerializer(@Nonnull JavaType type) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        this.reader = mapper.readerFor(type);
        this.writer = mapper.writerFor(type);
    }

    public JacksonRedisSerializer(@Nonnull ObjectReader reader, @Nonnull ObjectWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return null;
        }

        try {
            return this.writer.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not serialize object: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }

        try {
            return this.reader.readValue(bytes);
        } catch (IOException e) {
            throw new SerializationException("Could not de-serialize object: " + e.getMessage(), e);
        }
    }
}
