package net.minepay.mcapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import javax.annotation.Nonnull;

/**
 * Provides an entry-point for both the JVM and
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@SpringBootApplication
public class MojangProxy extends SpringBootServletInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    protected SpringApplicationBuilder configure(@Nonnull SpringApplicationBuilder builder) {
        return builder.sources(MojangProxy.class);
    }

    /**
     * <strong>JVM Entry Point</strong>
     *
     * @param arguments a
     */
    public static void main(@Nonnull String[] arguments) {
        SpringApplication.run(MojangProxy.class, arguments);
    }
}
