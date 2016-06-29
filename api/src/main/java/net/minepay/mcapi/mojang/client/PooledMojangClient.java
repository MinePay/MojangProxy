package net.minepay.mcapi.mojang.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.minepay.mcapi.mojang.Profile;
import net.minepay.mcapi.mojang.ProfileName;
import net.minepay.mcapi.mojang.ProfileNameChange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Component
@Immutable
@ThreadSafe
public class PooledMojangClient implements MojangClient {
    private final List<LocalAddressMojangClient> clients;
    private final AtomicInteger clientIndex = new AtomicInteger(0);

    @Autowired
    public PooledMojangClient(@Nonnull RedisTemplate<String, Integer> rateLimitRedisTemplate, @Nonnull TaskExecutor taskExecutor) throws IOException {
        try (InputStream inputStream = new FileInputStream("addresses.json")) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            Map<String, Integer> addresses = mapper.readerFor(mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class)).readValue(inputStream);

            List<LocalAddressMojangClient> clients = new ArrayList<>();
            addresses.forEach((a, l) -> {
                try {
                    if (a.contains("-")) {
                        (new AddressRange(a)).forEach((addr) -> clients.add(new LocalAddressMojangClient(addr, l, new CachingRedisAtomicInteger("address:" + addr.getHostAddress(), rateLimitRedisTemplate, taskExecutor))));
                    } else {
                        clients.add(new LocalAddressMojangClient(InetAddress.getByName(a), l, new CachingRedisAtomicInteger("address:" + a, rateLimitRedisTemplate, taskExecutor)));
                    }
                } catch (UnknownHostException ex) {
                    throw new IllegalArgumentException("Could not bind to local address: " + ex.getMessage(), ex);
                }
            });
            this.clients = Collections.unmodifiableList(clients);
        }
    }

    /**
     * Retrieves the currently selected client implementation in the client pool.
     *
     * @return a client.
     */
    @Nonnull
    private MojangClient getCurrentClient() {
        LocalAddressMojangClient client;
        int index = this.clientIndex.get();

        if  (index < this.clients.size()) {
            client = this.clients.get(index);
        } else {
            client = null;
        }

        while (client == null || client.hasExceededRateLimitation()) {
            int newIndex = this.clientIndex.get();

            if (newIndex >= this.clients.size()) {
                newIndex = 0;
                this.clientIndex.compareAndSet(newIndex, 0);
            }

            if (client != null) {
                client.resetRateLimit();
            }

            if (this.clientIndex.compareAndSet(index, newIndex)) {
                client = this.clients.get(index);
            } else {
                client = this.clients.get(this.clientIndex.get());
            }
        }

        return client;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Profile findProfile(@Nonnull String identifier) throws IOException {
        return this.getCurrentClient().findProfile(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Profile findProfile(@Nonnull UUID identifier) throws IOException {
        return this.getCurrentClient().findProfile(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ProfileName findIdentifier(@Nonnull String name) throws IOException {
        return this.getCurrentClient().findIdentifier(name);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ProfileName findIdentifier(@Nonnull String name, @Nonnull Instant timestamp) throws IOException {
        return this.getCurrentClient().findIdentifier(name, timestamp);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<ProfileName> findIdentifier(@Nonnull List<String> names) throws IOException {
        return this.getCurrentClient().findIdentifier(names);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<ProfileNameChange> getNameHistory(@Nonnull String identifier) throws IOException {
        return this.getCurrentClient().getNameHistory(identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<ProfileNameChange> getNameHistory(@Nonnull UUID identifier) throws IOException {
        return this.getCurrentClient().getNameHistory(identifier);
    }
}
