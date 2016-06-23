package net.minepay.mcapi.controller.v1;

import net.minepay.mcapi.controller.error.NoSuchNameException;
import net.minepay.mcapi.mojang.ProfileName;
import net.minepay.mcapi.mojang.client.MojangClient;
import net.minepay.mcapi.repository.MojangCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@ThreadSafe
@RestController
@RequestMapping("/v1/name")
public class NameController {
    private final MojangCache cache;
    private final MojangClient client;

    @Autowired
    public NameController(@Nonnull MojangCache cache, @Nonnull MojangClient client) {
        this.cache = cache;
        this.client = client;
    }

    /**
     * Attempts to find the UUID which has been associated with a specified display name at a
     * certain point in time.
     *
     * @param name      a display name.
     * @param timestamp a timestamp.
     * @return a profile name.
     *
     * @throws IOException when requesting data from Mojang fails.
     */
    @Nonnull
    @RequestMapping(path = "/{name}", method = RequestMethod.GET)
    public ProfileName lookupIdentifier(@Nonnull @PathVariable("name") String name, @Nullable @RequestParam(name = "at", required = false) Instant timestamp) throws IOException {
        ProfileName identifier;
        name = name.toLowerCase();

        if (timestamp != null) {
            identifier = this.cache.findIdentifier(name, timestamp);

            if (identifier == null) {
                identifier = this.client.findIdentifier(name, timestamp);

                if (identifier != null) {
                    this.cache.saveIdentifier(identifier, timestamp);
                }
            }
        } else {
            identifier = this.cache.findIdentifier(name);

            if (identifier == null) {
                identifier = this.client.findIdentifier(name);

                if (identifier != null) {
                    this.cache.saveIdentifier(identifier);
                }
            }
        }

        if (identifier == null) {
            throw new NoSuchNameException(name);
        }

        return identifier;
    }

    /**
     * Attempts to find a list of UUIDs which have been associated with a specified display name.
     *
     * @param names a list of display names.
     * @return a list of profile name.
     *
     * @throws IOException when requesting data from Mojang fails.
     */
    @Nonnull
    @RequestMapping(path = "/{name}", method = RequestMethod.POST)
    public List<ProfileName> lookupIdentifiers(@Nonnull @RequestBody List<String> names) throws IOException {
        if (names.size() > 100) {
            throw new IllegalArgumentException("Cannot poll more than 100 names at once");
        }

        // check cache first
        List<ProfileName> identifiers = new ArrayList<>();

        for (String name : names) {
            ProfileName identifier = this.cache.findIdentifier(name);

            if (identifier != null) {
                identifiers.add(identifier);
                names.remove(name);
            }
        }

        // poll from Mojang if any data is left to process
        if (names.size() > 0) {
            List<ProfileName> tmp = this.client.findIdentifier(names);

            if (tmp != null) {
                // TODO: Pipeline?
                tmp.forEach(this.cache::saveIdentifier);
                identifiers.addAll(tmp);
            }
        }

        return identifiers;
    }
}
