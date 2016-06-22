package net.minepay.mcapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Nonnull;

/**
 * Provides an entry-point for both the JVM and
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@SpringBootApplication
public class MojangProxy {

    /**
     * <strong>JVM Entry Point</strong>
     *
     * @param arguments a
     */
    public static void main(@Nonnull String[] arguments) {
        SpringApplication.run(MojangProxy.class, arguments);
    }
}
