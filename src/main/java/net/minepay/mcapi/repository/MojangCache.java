package net.minepay.mcapi.repository;

import net.minepay.mcapi.mojang.Profile;
import net.minepay.mcapi.mojang.ProfileName;
import net.minepay.mcapi.mojang.ProfileNameChange;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides a repository capable of storing profiles.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface MojangCache {

    /**
     * Retrieves a profile from the application cache.
     *
     * @param identifier an identifier.
     * @return a profile or, if no cached version could be located, null.
     */
    @Nullable
    Profile findProfile(@Nonnull String identifier);

    /**
     * Retrieves an identifier from the application cache.
     *
     * @param name a display name.
     * @return an identifier or, if no cached version could be located, null.
     */
    @Nullable
    ProfileName findIdentifier(@Nonnull String name);

    /**
     * Retrieves a history of name changes for the specified profile.
     *
     * @param identifier a profile identifier.
     * @return a list of profile changes or, if no cached version could be located, null.
     */
    @Nullable
    List<ProfileNameChange> findNameHistory(@Nonnull String identifier);
}
