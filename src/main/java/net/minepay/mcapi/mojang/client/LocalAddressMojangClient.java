package net.minepay.mcapi.mojang.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.minepay.mcapi.mojang.Profile;
import net.minepay.mcapi.mojang.ProfileName;
import net.minepay.mcapi.mojang.ProfileNameChange;
import net.minepay.mcapi.mojang.client.error.InterfaceException;
import net.minepay.mcapi.mojang.client.error.RateLimitExceededException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides a Mojang client for a specific local IP address.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LocalAddressMojangClient implements MojangClient {
    private static final String USER_AGENT;
    private static final ObjectReader reader;
    private static final ObjectWriter writer;

    static {
        {
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
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            reader = mapper.reader();
            writer = mapper.writer();
        }
    }

    private final InetAddress address;
    private final HttpClient client;
    private final int requestLimitation;
    private final AtomicInteger requestCount = new AtomicInteger(0);

    public LocalAddressMojangClient(@Nonnull InetAddress address, @Nonnegative int requestLimitation) {
        this.address = address;
        this.requestLimitation = requestLimitation;

        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(128); // Maximum amount of cached connections
        manager.setDefaultMaxPerRoute(24); // Maximum amount of cached connections per route

        this.client = HttpClientBuilder.create()
                .disableAuthCaching()
                .disableCookieManagement()
                .disableRedirectHandling()
                .setConnectionManager(manager)
                .setUserAgent(USER_AGENT)
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setLocalAddress(address)
                                .build()
                )
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Profile findProfile(@Nonnull String identifier) throws IOException {
        this.requestCount.incrementAndGet();

        HttpGet request = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/" + identifier);
        HttpResponse response = this.client.execute(request);

        switch (response.getStatusLine().getStatusCode()) {
            case 200: break;
            case 204:
                return null;
            case 429:
                throw new RateLimitExceededException();
            default:
                throw new InterfaceException("Encountered unexpected status code " + response.getStatusLine().getStatusCode() + " while attempting to parse API response");
        }

        try (InputStream inputStream = response.getEntity().getContent()) {
            return reader.forType(Profile.class).readValue(inputStream);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Profile findProfile(@Nonnull UUID identifier) throws IOException {
        return this.findProfile(Profile.convertIdentifier(identifier));
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ProfileName findIdentifier(@Nonnull String name) throws IOException {
        this.requestCount.incrementAndGet();

        HttpGet request = new HttpGet("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, "UTF-8"));
        HttpResponse response = this.client.execute(request);

        switch (response.getStatusLine().getStatusCode()) {
            case 200: break;
            case 204:
                return null;
            case 429:
                throw new RateLimitExceededException();
            default:
                throw new InterfaceException("Encountered unexpected status code " + response.getStatusLine().getStatusCode() + " while attempting to parse API response");
        }

        try (InputStream inputStream = response.getEntity().getContent()) {
            return reader.forType(ProfileName.class).readValue(inputStream);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ProfileName findIdentifier(@Nonnull String name, @Nonnull Instant timestamp) throws IOException {
        this.requestCount.incrementAndGet();

        HttpGet request = new HttpGet("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, "UTF-8") + "?at=" + timestamp.getEpochSecond());
        HttpResponse response = this.client.execute(request);

        switch (response.getStatusLine().getStatusCode()) {
            case 200: break;
            case 204:
                return null;
            case 429:
                throw new RateLimitExceededException();
            default:
                throw new InterfaceException("Encountered unexpected status code " + response.getStatusLine().getStatusCode() + " while attempting to parse API response");
        }

        try (InputStream inputStream = response.getEntity().getContent()) {
            return reader.forType(ProfileName.class).readValue(inputStream);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<ProfileName> findIdentifier(@Nonnull List<String> names) throws IOException {
        this.requestCount.incrementAndGet();

        HttpPost request = new HttpPost("https://api.mojang.com/profiles/minecraft");
        {
            BasicHttpEntity entity = new BasicHttpEntity();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(writer.writeValueAsString(names).getBytes(StandardCharsets.UTF_8));
            entity.setContent(inputStream);
            entity.setContentType("application/json;Charset=UTF-8");

            request.setEntity(entity);
        }

        HttpResponse response = this.client.execute(request);

        switch (response.getStatusLine().getStatusCode()) {
            case 200: break;
            case 204:
                return null;
            case 429:
                throw new RateLimitExceededException();
            default:
                throw new InterfaceException("Encountered unexpected status code " + response.getStatusLine().getStatusCode() + " while attempting to parse API response");
        }

        try (InputStream inputStream = response.getEntity().getContent()) {
            return reader.forType(reader.getTypeFactory().constructCollectionType(List.class, ProfileName.class)).readValue(inputStream);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<ProfileNameChange> getNameHistory(@Nonnull String identifier) throws IOException {
        this.requestCount.incrementAndGet();

        HttpGet request = new HttpGet("https://api.mojang.com/user/profiles/" + identifier + "/names");
        HttpResponse response = this.client.execute(request);

        switch (response.getStatusLine().getStatusCode()) {
            case 200: break;
            case 204:
                return null;
            case 429:
                throw new RateLimitExceededException();
            default:
                throw new InterfaceException("Encountered unexpected status code " + response.getStatusLine().getStatusCode() + " while attempting to parse API response");
        }

        try (InputStream inputStream = response.getEntity().getContent()) {
            return reader.forType(reader.getTypeFactory().constructCollectionType(List.class, ProfileNameChange.class)).readValue(inputStream);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public List<ProfileNameChange> getNameHistory(@Nonnull UUID identifier) throws IOException {
        return this.getNameHistory(Profile.convertIdentifier(identifier));
    }

    /**
     * Checks whether the client has exceeded the configured rate limitation.
     *
     * @return true if exceeded, false otherwise.
     */
    boolean hasExceededRateLimitation() {
        return this.requestCount.get() > this.requestLimitation;
    }

    /**
     * Retrieves the address which will be utilized for queries.
     *
     * @return an address.
     */
    @Nonnull
    public InetAddress getAddress() {
        return this.address;
    }

    /**
     * Retrieves the overall rate limitation for this client.
     *
     * @return a rate limit.
     */
    @Nonnegative
    int getRateLimitation() {
        return this.requestLimitation;
    }

    /**
     * Retrieves the current rate limit usage for this client.
     *
     * @return a rate limit.
     */
    @Nonnegative
    int getRequestCount() {
        return this.requestCount.get();
    }
}
