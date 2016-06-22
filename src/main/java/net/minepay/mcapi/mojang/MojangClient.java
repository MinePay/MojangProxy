package net.minepay.mcapi.mojang;

import java.time.Instant;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Provides a base interface for all Mojang clients and load distributing services.
 *
 * <strong>Implementation Notes:</strong> Implementations are expected to be thread safe as they
 * will be called from request handler threads as needed.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@ThreadSafe
public interface MojangClient {

    /**
     * Attempts to find a profile for the specified Mojang profile identifier.
     *
     * @param identifier an identifier.
     * @return a profile or, if no such profile was found, null.
     */
    @Nullable
    Profile findProfile(@Nonnull String identifier);

    /**
     * Attempts to find a profile for the specified profile UUID.
     *
     * @param identifier a uuid.
     * @return a profile or, if no such profile was found, null.
     */
    @Nullable
    Profile findProfile(@Nonnull UUID identifier);

    /**
     * Attempts to find the profile identifier which corresponds to the specified display name at
     * this time.
     *
     * @param name a name.
     * @return an identifier or, if no such name is registered, null.
     */
    @Nullable
    ProfileName findIdentifier(@Nonnull String name);

    /**
     * Attempts to find the profile identifier which corresponds to the specified display name at a
     * specified point in time.
     *
     * @param name      a name.
     * @param timestamp a timestamp.
     * @return an identifier or, if no such name is registered, null.
     */
    @Nullable
    ProfileName findIdentifier(@Nonnull String name, @Nonnull Instant timestamp);
}
