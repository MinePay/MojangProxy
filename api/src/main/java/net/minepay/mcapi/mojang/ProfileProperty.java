package net.minepay.mcapi.mojang;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Provides a representation for profile properties.
 *
 * @param <T> a property value type.
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public class ProfileProperty<T> {
    private final String name;
    private final T value;
    private final String signature;

    @JsonCreator
    public ProfileProperty(@Nonnull @JsonProperty("name") String name, @Nonnull @JsonProperty("value") T value, @Nullable @JsonProperty("signature") String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public T getValue() {
        return this.value;
    }

    @Nullable
    public String getSignature() {
        return this.signature;
    }
}
