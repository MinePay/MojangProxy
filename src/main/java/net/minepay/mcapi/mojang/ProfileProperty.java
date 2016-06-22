package net.minepay.mcapi.mojang;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Provides a representation for profile properties.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public class ProfileProperty<T> {
    private final String name;
    private final T value;

    @JsonCreator
    public ProfileProperty(@Nonnull @JsonProperty("name") String name, @Nonnull @JsonProperty("value") T value) {
        this.name = name;
        this.value = value;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public T getValue() {
        return this.value;
    }
}
