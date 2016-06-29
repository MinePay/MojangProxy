package net.minepay.mcapi.mojang.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Provides a Mojang client for a specific local IP address.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public class LocalAddressMojangClient implements MojangClient {
    private static final ObjectReader reader;
    private static final ObjectWriter writer;

    static {
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            reader = mapper.reader();
            writer = mapper.writer();
        }
    }

    private final PooledAddress address;
    private final HttpClient client;

    public LocalAddressMojangClient(@Nonnull HttpClient client, @Nonnull PooledAddress address) {
        this.client = client;
        this.address = address;
    }

    /**
     * Executes an HTTP request.
     *
     * @param request a request.
     * @return a response.
     *
     * @throws IOException when an error occurs while connecting, writing or reading.
     */
    @Nullable
    public HttpResponse execute(@Nonnull HttpRequestBase request) throws IOException {
        request.setConfig(RequestConfig.custom().setLocalAddress(this.address.getDeviceAddress()).build());
        HttpResponse response = this.client.execute(request);

        switch (response.getStatusLine().getStatusCode()) {
            case 200:
                return response;
            case 204:
                return null;
            case 429:
                throw new RateLimitExceededException();
            default:
                throw new InterfaceException("Encountered unexpected status code " + response.getStatusLine().getStatusCode() + " while attempting to parse API response");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Profile findProfile(@Nonnull String identifier) throws IOException {
        this.updateRequestCount();

        HttpGet request = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/" + identifier + "?unsigned=false");
        HttpResponse response = this.execute(request);

        if (response == null) {
            return null;
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
        this.updateRequestCount();

        HttpGet request = new HttpGet("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, "UTF-8"));
        HttpResponse response = this.execute(request);

        if (response == null) {
            return null;
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
        this.updateRequestCount();

        HttpGet request = new HttpGet("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, "UTF-8") + "?at=" + timestamp.getEpochSecond());
        HttpResponse response = this.execute(request);

        if (response == null) {
            return null;
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
        this.updateRequestCount();

        HttpPost request = new HttpPost("https://api.mojang.com/profiles/minecraft");
        {
            BasicHttpEntity entity = new BasicHttpEntity();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(writer.writeValueAsString(names).getBytes(StandardCharsets.UTF_8));
            entity.setContent(inputStream);
            entity.setContentType("application/json;Charset=UTF-8");

            request.setEntity(entity);
        }

        HttpResponse response = this.execute(request);

        if (response == null) {
            return null;
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
        this.updateRequestCount();

        HttpGet request = new HttpGet("https://api.mojang.com/user/profiles/" + identifier + "/names");
        HttpResponse response = this.execute(request);

        if (response == null) {
            return null;
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
        return this.address.getRequestCount().get() > this.address.getRateLimit();
    }

    /**
     * Retrieves the address which will be utilized for queries.
     *
     * @return an address.
     */
    @Nonnull
    public PooledAddress getAddress() {
        return this.address;
    }

    /**
     * Retrieves the overall rate limitation for this client.
     *
     * @return a rate limit.
     */
    @Nonnegative
    int getRateLimitation() {
        return this.address.getRateLimit();
    }

    /**
     * Retrieves the current rate limit usage for this client.
     *
     * @return a rate limit.
     */
    @Nonnegative
    int getRequestCount() {
        return this.address.getRequestCount().get();
    }

    /**
     * Resets the rate limitation.
     */
    public void resetRateLimit() {
        this.address.getRequestCount().reset();
    }

    /**
     * Updates the request counter.
     */
    private void updateRequestCount() {
        this.address.getRequestCount().increment();
    }
}
