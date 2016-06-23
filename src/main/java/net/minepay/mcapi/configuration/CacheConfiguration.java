package net.minepay.mcapi.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnegative;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Component
@ConfigurationProperties(prefix = "cache")
public class CacheConfiguration {
    private int profileCacheTime = 86400;
    private int nameCacheTime = 3024000;
    private int historyCacheTime = 86400;

    @Nonnegative
    public int getProfileCacheTime() {
        return this.profileCacheTime;
    }

    @Nonnegative
    public int getNameCacheTime() {
        return this.nameCacheTime;
    }

    @Nonnegative
    public int getHistoryCacheTime() {
        return this.historyCacheTime;
    }
}
