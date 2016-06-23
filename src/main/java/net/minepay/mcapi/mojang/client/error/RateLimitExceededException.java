package net.minepay.mcapi.mojang.client.error;

import java.io.IOException;

/**
 * Provides an exception for cases where the backing API implementation reports an exceeded rate
 * limit.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RateLimitExceededException extends IOException {
}
