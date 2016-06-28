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

        assertEquals(3232235777L, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("192.168.1.1")));
        assertEquals(3232235778L, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("192.168.1.2")));
        assertEquals(3232235779L, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("192.168.1.3")));

        assertEquals(3232281089L, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("192.168.178.1")));
        assertEquals(3232281090L, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("192.168.178.2")));
        assertEquals(3232281091L, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("192.168.178.3")));

        assertEquals(167772161, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("10.0.0.1")));
        assertEquals(167772162, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("10.0.0.2")));
        assertEquals(167772163, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("10.0.0.3")));
        assertEquals(167772417, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("10.0.1.1")));
        assertEquals(167772418, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("10.0.1.2")));
        assertEquals(167772419, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("10.0.1.3")));
        assertEquals(167837697, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("10.1.0.1")));
        assertEquals(167837698, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("10.1.0.2")));
        assertEquals(167837699, AddressRange.convertAddress((Inet4Address) InetAddress.getByName("10.1.0.3")));

        assertEquals("127.0.0.1", AddressRange.convertAddress(2130706433).getHostAddress());
        assertEquals("127.0.0.2", AddressRange.convertAddress(2130706434).getHostAddress());
        assertEquals("127.0.0.3", AddressRange.convertAddress(2130706435).getHostAddress());
    }

    @Test
    public void parse() throws Exception {
        {
            AddressRange range = new AddressRange("127.0.0.1-127.0.0.3");
            Iterator<InetAddress> it = range.iterator();

            assertEquals("127.0.0.1", it.next().getHostAddress());
            assertEquals("127.0.0.2", it.next().getHostAddress());
            assertEquals("127.0.0.3", it.next().getHostAddress());
            assertFalse(it.hasNext());
        }

        {
            AddressRange range = new AddressRange("192.168.0.1-192.168.0.3");
            Iterator<InetAddress> it = range.iterator();

            assertEquals("192.168.0.1", it.next().getHostAddress());
            assertEquals("192.168.0.2", it.next().getHostAddress());
            assertEquals("192.168.0.3", it.next().getHostAddress());
            assertFalse(it.hasNext());
        }

        {
            AddressRange range = new AddressRange("192.168.178.1-192.168.178.3");
            Iterator<InetAddress> it = range.iterator();

            assertEquals("192.168.178.1", it.next().getHostAddress());
            assertEquals("192.168.178.2", it.next().getHostAddress());
            assertEquals("192.168.178.3", it.next().getHostAddress());
            assertFalse(it.hasNext());
        }
    }
}
