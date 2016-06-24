package net.minepay.mcapi.mojang.client.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * Provides an exception for cases where the API responds with an unexpected status code.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class InterfaceException extends IOException {

    public InterfaceException() {
        super();
    }

    public InterfaceException(String message) {
        super(message);
    }

    public InterfaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterfaceException(Throwable cause) {
        super(cause);
    }
}
