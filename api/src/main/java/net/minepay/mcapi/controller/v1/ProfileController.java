package net.minepay.mcapi.controller.v1;

import net.minepay.mcapi.controller.error.NoSuchProfileException;
import net.minepay.mcapi.mojang.ProfileName;
import net.minepay.mcapi.mojang.ProfileNameChange;
import net.minepay.mcapi.mojang.client.MojangClient;
import net.minepay.mcapi.mojang.cache.MojangCache;
import net.minepay.mcapi.mojang.Profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Handles Mojang profile lookups.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@ThreadSafe
@RestController
@RequestMapping("/v1/profile")
public class ProfileController {
    private final MojangClient client;
    private final MojangCache cache;
    private final NameController nameController;

    @Autowired
    public ProfileController(@Nonnull MojangClient client, @Nonnull MojangCache cache, @Nonnull NameController nameController) {
        this.client = client;
        this.cache = cache;
        this.nameController = nameController;
    }

    /**
     * Attempts to find a profile based on a username or UUID.
     *
     * @param name      a name or identifier.
     * @param timestamp a timestamp.
     * @return a profile.
     *
     * @throws IOException when requesting data from the Mojang API fails.
     */
    @Nonnull
    @RequestMapping(path = "/{name}", method = RequestMethod.GET)
    public Profile lookupById(@Nonnull @PathVariable("name") String name, @Nullable @RequestParam(value = "at", required = false) Instant timestamp) throws IOException {
        if (!Profile.isValidIdentifier(name)) {
            return this.lookupByName(name, timestamp);
        }

        if (Profile.isValidUUID(name)) {
            name = Profile.convertIdentifier(UUID.fromString(name));
        }

        Profile profile = this.cache.findProfile(name);

        if (profile == null) {
            profile = this.client.findProfile(name);

            if (profile != null) {
                this.cache.saveProfile(profile);
            }
        }

        if (profile == null) {
            throw new NoSuchProfileException(name);
        }

        return profile;
    }

    /**
     * Attempts to find a profile based on a username.
     *
     * @param name      a name.
     * @param timestamp a timestamp.
     * @return a profile.
     *
     * @throws IOException when requesting data from the Mojang API fails.
     */
    @Nonnull
    public Profile lookupByName(@Nonnull String name, @Nullable Instant timestamp) throws IOException {
        ProfileName identifier = this.nameController.lookupIdentifier(name, timestamp);
        return this.lookupById(identifier.getId(), null);
    }

    /**
     * Attempts to find a profile history based on a username or UUID.
     *
     * @param name a name or UUID.
     * @return a history.
     *
     * @throws IOException when requesting data from the Mojang API fails.
     */
    @Nonnull
    @RequestMapping(path = "/{name}/history", method = RequestMethod.GET)
    public List<ProfileNameChange> lookupHistoryById(@Nonnull @PathVariable("name") String name) throws IOException {
        if (!Profile.isValidIdentifier(name)) {
            return this.lookupHistoryByName(name);
        }

        if (Profile.isValidUUID(name)) {
            name = Profile.convertIdentifier(UUID.fromString(name));
        }

        List<ProfileNameChange> history = this.cache.findNameHistory(name);

        if (history == null) {
            history = this.client.getNameHistory(name);

            if (history != null) {
                this.cache.saveNameHistory(name, history);
            }
        }

        if (history == null) {
            throw new NoSuchProfileException(name);
        }

        return history;
    }

    /**
     * Attempts to find a profile history based on a username.
     *
     * @param name a name.
     * @return a history.
     *
     * @throws IOException when requesting data from the Mojang API fails.
     */
    @Nonnull
    public List<ProfileNameChange> lookupHistoryByName(@Nonnull String name) throws IOException {
        ProfileName identifier = this.nameController.lookupIdentifier(name, null);
        return this.lookupHistoryById(identifier.getId());
    }
}
