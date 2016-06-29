package net.minepay.mcapi.mojang.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.minepay.mcapi.mojang.Profile;
import net.minepay.mcapi.mojang.ProfileName;
import net.minepay.mcapi.mojang.ProfileNameChange;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

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
    private final ObjectPool<LocalAddressMojangClient> clientObjectPool;

    @Autowired
    public PooledMojangClient(@Nonnull RedisTemplate<String, Integer> rateLimitRedisTemplate, @Nonnull TaskExecutor taskExecutor) throws IOException {
        try (InputStream inputStream = new FileInputStream(Paths.get("addresses.json").toFile())) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            Map<String, Integer> addresses = mapper.readerFor(mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class)).readValue(inputStream);

            List<PooledAddress> clients = new ArrayList<>();
            addresses.forEach((a, l) -> {
                try {
                    if (a.contains("-")) {
                        (new AddressRange(a)).forEach((addr) -> clients.add(new PooledAddress(addr, new CachingRedisInteger("address:" + addr.getHostAddress(), rateLimitRedisTemplate, taskExecutor), l)));
                    } else {
                        clients.add(new PooledAddress(InetAddress.getByName(a), new CachingRedisInteger("address:" + a, rateLimitRedisTemplate, taskExecutor), l));
                    }
                } catch (UnknownHostException ex) {
                    throw new IllegalArgumentException("Could not bind to local address: " + ex.getMessage(), ex);
                }
            });

            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMinIdle(12);
            config.setMaxIdle(24);
            config.setMaxTotal(48);
            config.setBlockWhenExhausted(false);
            config.setMinEvictableIdleTimeMillis(1000);
            config.setTestOnCreate(false);
            config.setTestWhileIdle(false);
            config.setTestOnReturn(true);
            config.setTestOnBorrow(true);

            this.clientObjectPool = new GenericObjectPool<>(new ClientPooledObjectFactory(clients), config);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Profile findProfile(@Nonnull String identifier) throws IOException {
        try {
            LocalAddressMojangClient client = this.clientObjectPool.borrowObject();

            try {
                return client.findProfile(identifier);
            } finally {
                this.clientObjectPool.returnObject(client);
            }
        } catch (Exception ex) {
            throw new IOException("Could not borrow/return client from/to pool: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Profile findProfile(@Nonnull UUID identifier) throws IOException {
        try {
            LocalAddressMojangClient client = this.clientObjectPool.borrowObject();

            try {
                return client.findProfile(identifier);
            } finally {
                this.clientObjectPool.returnObject(client);
            }
        } catch (Exception ex) {
            throw new IOException("Could not borrow/return client from/to pool: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ProfileName findIdentifier(@Nonnull String name) throws IOException {
        try {
            LocalAddressMojangClient client = this.clientObjectPool.borrowObject();

            try {
                return client.findIdentifier(name);
            } finally {
                this.clientObjectPool.returnObject(client);
            }
        } catch (Exception ex) {
            throw new IOException("Could not borrow/return client from/to pool: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ProfileName findIdentifier(@Nonnull String name, @Nonnull Instant timestamp) throws IOException {
        try {
            LocalAddressMojangClient client = this.clientObjectPool.borrowObject();

            try {
                return client.findIdentifier(name, timestamp);
            } finally {
                this.clientObjectPool.returnObject(client);
            }
        } catch (Exception ex) {
            throw new IOException("Could not borrow/return client from/to pool: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<ProfileName> findIdentifier(@Nonnull List<String> names) throws IOException {
        try {
            LocalAddressMojangClient client = this.clientObjectPool.borrowObject();

            try {
                return client.findIdentifier(names);
            } finally {
                this.clientObjectPool.returnObject(client);
            }
        } catch (Exception ex) {
            throw new IOException("Could not borrow/return client from/to pool: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<ProfileNameChange> getNameHistory(@Nonnull String identifier) throws IOException {
        try {
            LocalAddressMojangClient client = this.clientObjectPool.borrowObject();

            try {
                return client.getNameHistory(identifier);
            } finally {
                this.clientObjectPool.returnObject(client);
            }
        } catch (Exception ex) {
            throw new IOException("Could not borrow/return client from/to pool: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<ProfileNameChange> getNameHistory(@Nonnull UUID identifier) throws IOException {
        try {
            LocalAddressMojangClient client = this.clientObjectPool.borrowObject();

            try {
                return client.getNameHistory(identifier);
            } finally {
                this.clientObjectPool.returnObject(client);
            }
        } catch (Exception ex) {
            throw new IOException("Could not borrow/return client from/to pool: " + ex.getMessage(), ex);
        }
    }

    /**
     * Provides a pooled object factory for Mojang client objects.
     */
    public static class ClientPooledObjectFactory extends BasePooledObjectFactory<LocalAddressMojangClient> {
        private static final String USER_AGENT;

        static {
            Package p = LocalAddressMojangClient.class.getPackage();

            String name = p.getImplementationTitle();
            String version = p.getImplementationVersion();
            String vendor = p.getImplementationVendor();

            if (name == null) {
                name = "MCAPI";
            }

            if (version == null) {
                version = "0.0.0-SNAPSHOT";
            }

            if (vendor == null) {
                vendor = "Minepay";
            }

            USER_AGENT = String.format("%s/%s (+%s)", name, version, vendor);
        }

        private final Deque<PooledAddress> addresses;
        private final HttpClient client;

        public ClientPooledObjectFactory(@Nonnull List<PooledAddress> addresses) {
            this.addresses = new ConcurrentLinkedDeque<>(addresses);

            PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
            manager.setMaxTotal(128); // Maximum amount of cached connections
            manager.setDefaultMaxPerRoute(12); // Maximum amount of cached connections per route

            this.client = HttpClientBuilder.create()
                    .disableAuthCaching()
                    .disableCookieManagement()
                    .disableRedirectHandling()
                    .setConnectionManager(manager)
                    .setUserAgent(USER_AGENT)
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public LocalAddressMojangClient create() throws Exception {
            PooledAddress address = this.addresses.pop();
            return new LocalAddressMojangClient(this.client, address);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PooledObject<LocalAddressMojangClient> wrap(@Nonnull LocalAddressMojangClient obj) {
            return new DefaultPooledObject<>(obj);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean validateObject(@Nonnull PooledObject<LocalAddressMojangClient> p) {
            return !p.getObject().hasExceededRateLimitation();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void destroyObject(@Nonnull PooledObject<LocalAddressMojangClient> p) throws Exception {
            LocalAddressMojangClient client = p.getObject();
            client.resetRateLimit();

            this.addresses.add(client.getAddress());
        }
    }
}
