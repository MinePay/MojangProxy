package net.minepay.mcapi.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    public ServerVersion(@Nonnull @JsonProperty("name") String name, @Nonnull @JsonProperty("version") String version, @Nonnull @JsonProperty("vendor") String vendor) {
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
