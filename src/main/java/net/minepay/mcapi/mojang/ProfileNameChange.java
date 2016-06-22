package net.minepay.mcapi.mojang;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Represents a display name change which has occured in the past of a certain account.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public class ProfileNameChange {
    private final String name;
    private final Instant changedToAt;

    @JsonCreator
    public ProfileNameChange(@Nonnull @JsonProperty("name") String name, @Nullable @JsonProperty("changedToAt") Instant changedToAt) {
        this.name = name;
        this.changedToAt = changedToAt;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nullable
    public Instant getChangedToAt() {
        return this.changedToAt;
    }
}
