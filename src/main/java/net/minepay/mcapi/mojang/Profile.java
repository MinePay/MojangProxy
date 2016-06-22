package net.minepay.mcapi.mojang;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Represents a complete Mojang profile.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public class Profile {
    private static final Pattern UUID_PATTERN = Pattern.compile("^([A-F0-9]{8})([A-F0-9]{4})([01-5][0-9A-F]{3})([089AB][0-9A-F]{3})([A-F0-9]{12})$", Pattern.CASE_INSENSITIVE);
    private final String id;
    private final String name;
    private final List<ProfileProperty> properties;

    public Profile(@Nonnull @JsonProperty("id") String id, @Nonnull @JsonProperty("name") String name, @Nonnull @JsonProperty("properties") List<JsonNode> properties) throws IOException {
        this.id = id;
        this.name = name;

        {
            List<ProfileProperty> profileProperties = new ArrayList<>();
            for (JsonNode node : properties) {
                String propertyName = node.get("name").asText();
                JsonNode value = node.get("value");
                String signature = (node.has("signature") ? node.get("signature").asText() : null);

                if ("textures".equalsIgnoreCase(propertyName)) {
                    profileProperties.add(new ProfileProperty<>(propertyName, ProfileTextures.fromBaseString(value.asText()), signature));
                } else {
                    profileProperties.add(new ProfileProperty<>(propertyName, value.asText(), signature));
                }
            }
            this.properties = Collections.unmodifiableList(profileProperties);
        }
    }

    /**
     * Converts a standard UUID into a Mojang identifier.
     *
     * @param uuid a UUID.
     * @return an identifier.
     */
    @Nonnull
    static String convertIdentifier(@Nonnull UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    /**
     * Converts a Mojang identifier into a standard UUID.
     *
     * @param identifier an identifier.
     * @return a UUID.
     */
    @Nonnull
    static UUID convertIdentifier(@Nonnull String identifier) {
        return UUID.fromString(UUID_PATTERN.matcher(identifier).replaceFirst("$1-$2-$3-$4-$5"));
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public List<ProfileProperty> getProperties() {
        return this.properties;
    }
}
