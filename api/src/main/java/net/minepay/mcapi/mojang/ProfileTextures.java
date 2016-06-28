package net.minepay.mcapi.mojang;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Represents a set of textures which will be displayed in-game for a certain player.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
@JsonIgnoreProperties("signatureRequired")
public class ProfileTextures {
    private final Instant timestamp;
    private final String profileId;
    private final String profileName;
    private final Map<String, Texture> textures;

    @JsonCreator
    public ProfileTextures(@Nonnull @JsonProperty("timestamp") Instant timestamp, @Nonnull @JsonProperty("profileId") String profileId, @Nonnull @JsonProperty("profileName") String profileName, @Nonnull @JsonProperty("textures") Map<String, Texture> textures) {
        this.timestamp = timestamp;
        this.profileId = profileId;
        this.profileName = profileName;
        this.textures = Collections.unmodifiableMap(textures);
    }

    /**
     * Decodes a texture object out of its base64 representation.
     *
     * @param encodedTextures an encoded texture object.
     * @return a profile texture representation.
     *
     * @throws IOException when decoding the data fails.
     */
    @Nonnull
    public static ProfileTextures fromBaseString(@Nonnull String encodedTextures) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        byte[] textureBytes = Base64.getDecoder().decode(encodedTextures);
        return mapper.readerFor(ProfileTextures.class).readValue(new String(textureBytes, StandardCharsets.UTF_8));
    }

    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Nonnull
    public String getProfileId() {
        return this.profileId;
    }

    @Nonnull
    public String getProfileName() {
        return this.profileName;
    }

    @Nonnull
    public Map<String, Texture> getTextures() {
        return this.textures;
    }

    /**
     * Represents a texture.
     */
    @Immutable
    @ThreadSafe
    public static class Texture {
        private final URL url;
        private final Map<String, String> metadata;

        @JsonCreator
        public Texture(@Nonnull @JsonProperty("url") URL url, @Nullable @JsonProperty("metadata") Map<String, String> metadata) {
            this.url = url;

            if (metadata != null) {
                this.metadata = Collections.unmodifiableMap(metadata);
            } else {
                this.metadata = null;
            }
        }

        /**
         * Retrieves a texture's backing URL.
         *
         * @return a url.
         */
        @Nonnull
        public URL getUrl() {
            return this.url;
        }

        /**
         * Retrieves a texture's metadata (such as model type).
         * @return a map of properties.
         */
        @Nonnull
        public Map<String, String> getMetadata() {
            return this.metadata;
        }
    }
}
