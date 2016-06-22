package net.minepay.mcapi.mojang;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Represents a profile name to profile association at a certain point in time.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public class ProfileName {
    private final String id;
    private final String name;

    @JsonCreator
    public ProfileName(@Nonnull @JsonProperty("id") String id, @Nonnull @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }
}
