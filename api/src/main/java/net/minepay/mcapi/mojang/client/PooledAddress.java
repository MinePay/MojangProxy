package net.minepay.mcapi.mojang.client;

import java.net.InetAddress;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public class PooledAddress {
    private final InetAddress deviceAddress;
    private final CachingRedisAtomicInteger requestCount;
    private final int rateLimit;

    public PooledAddress(@Nonnull InetAddress deviceAddress, @Nonnull CachingRedisAtomicInteger requestCount, @Nonnegative int rateLimit) {
        this.deviceAddress = deviceAddress;
        this.requestCount = requestCount;
        this.rateLimit = rateLimit;
    }

    @Nonnull
    public InetAddress getDeviceAddress() {
        return this.deviceAddress;
    }

    @Nonnull
    public CachingRedisAtomicInteger getRequestCount() {
        return this.requestCount;
    }

    @Nonnegative
    public int getRateLimit() {
        return this.rateLimit;
    }
}
