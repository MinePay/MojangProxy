package net.minepay.mcapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebClientAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import java.security.Security;

import javax.annotation.Nonnull;

/**
 * Provides an entry-point for both the JVM and
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = WebClientAutoConfiguration.class)
public class MojangProxy extends SpringBootServletInitializer {

    /**
     * <strong>JVM Entry Point</strong>
     *
     * @param arguments a
     */
    public static void main(@Nonnull String[] arguments) {
        Security.setProperty("networkaddress.cache.ttl", "30");
        Security.setProperty("networkaddress.cache.negative.ttl", "10");

        SpringApplication.run(MojangProxy.class, arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SpringApplicationBuilder configure(@Nonnull SpringApplicationBuilder builder) {
        return builder.sources(MojangProxy.class);
    }
}
