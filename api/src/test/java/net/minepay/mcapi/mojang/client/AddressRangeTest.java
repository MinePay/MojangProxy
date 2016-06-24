package net.minepay.mcapi.mojang.client;

import org.junit.Assert;
import org.junit.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AddressRangeTest {

    @Test
    public void convertAddress() throws Exception {
        assertEquals(2130706433, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("127.0.0.1")));
        assertEquals(2130706434, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("127.0.0.2")));
        assertEquals(2130706435, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("127.0.0.3")));

        assertEquals("127.0.0.1", AddressRange.convertAddress(2130706433).getHostAddress());
        assertEquals("127.0.0.2", AddressRange.convertAddress(2130706434).getHostAddress());
        assertEquals("127.0.0.3", AddressRange.convertAddress(2130706435).getHostAddress());
    }

    @Test
    public void parse() throws Exception {
        AddressRange range = new AddressRange("127.0.0.1-127.0.0.3");
        Iterator<InetAddress> it = range.iterator();

        assertEquals("127.0.0.1", it.next().getHostAddress());
        assertEquals("127.0.0.2", it.next().getHostAddress());
        assertEquals("127.0.0.3", it.next().getHostAddress());
        assertFalse(it.hasNext());
    }
}
