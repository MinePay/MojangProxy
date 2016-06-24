package net.minepay.mcapi.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Nonnull;

/**
 * Provides a basic exception which notifies the backing implementation of an unknown profile.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchProfileException extends RuntimeException {

    public NoSuchProfileException(@Nonnull String name) {
        super("No such profile: " + name);
    }
}
