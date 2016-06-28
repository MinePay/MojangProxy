package net.minepay.mcapi.mojang.client;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public class AddressRange implements Iterable<InetAddress> {
    private final List<InetAddress> addresses;

    public AddressRange(@Nonnull String range) throws UnknownHostException {
        List<InetAddress> addresses = new ArrayList<>();
        int separatorIndex = range.indexOf('-');

        if (separatorIndex == -1) {
            throw new IllegalArgumentException("Invalid range: No separator");
        }

        // TODO: v6 support in the future?
        Inet4Address startAddress = (Inet4Address) InetAddress.getByName(range.substring(0, separatorIndex));
        Inet4Address endAddress = (Inet4Address) InetAddress.getByName(range.substring(separatorIndex + 1));

        {
            long startAddressValue = convertAddress(startAddress);
            long endAddressValue = convertAddress(endAddress);
            int offset = 0;

            while ((startAddressValue + offset) <= endAddressValue) {
                long currentAddressValue = startAddressValue + offset++;
                addresses.add(convertAddress(currentAddressValue));
            }
        }

        this.addresses = Collections.unmodifiableList(addresses);
    }

    /**
     * Converts an IPv4 address into its integer representation.
     *
     * @param address an address.
     * @return an integer representation.
     */
    public static long convertAddress(@Nonnull Inet4Address address) {
        long value = 0;
        byte[] raw = address.getAddress();

        for (int i = 0; i < 4; ++i) {
            value <<= 8;
            value |= (raw[i] & 0xFF);
        }

        return value;
    }

    /**
     * Converts an integer representation into an IPv4 address.
     *
     * @param address an integer representation.
     * @return an address.
     *
     * @throws UnknownHostException when the specified hostname is unknown.
     */
    @Nonnull
    public static InetAddress convertAddress(long address) throws UnknownHostException {
        byte[] raw = new byte[4];

        for (int i = 3; i >= 0; --i) {
            raw[i] = (byte) (address & 0xFF);
            address >>>= 8;
        }

        return Inet4Address.getByAddress(raw);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<InetAddress> iterator() {
        return this.addresses.iterator();
    }
}
