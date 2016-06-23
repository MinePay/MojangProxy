package net.minepay.mcapi.controller.v1;

import net.minepay.mcapi.controller.error.NoSuchNameException;
import net.minepay.mcapi.mojang.ProfileName;
import net.minepay.mcapi.mojang.client.MojangClient;
import net.minepay.mcapi.repository.MojangCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;

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
}
