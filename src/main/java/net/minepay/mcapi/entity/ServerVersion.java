package net.minepay.mcapi.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.annotation.Nonnull;

/**
 * Represents public server version information.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ServerVersion {
    private final String name;
    private final String version;
    private final String vendor;

    @JsonCreator
    public ServerVersion(@Nonnull String name, @Nonnull String version, @Nonnull String vendor) {
        this.name = name;
        this.version = version;
        this.vendor = vendor;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public String getVersion() {
        return this.version;
    }

    @Nonnull
    public String getVendor() {
        return this.vendor;
    }
}
