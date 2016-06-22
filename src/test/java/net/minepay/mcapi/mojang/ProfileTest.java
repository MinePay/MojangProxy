package net.minepay.mcapi.mojang;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ProfileTest {

    @Test
    public void convertIdentifier() {
        Assert.assertEquals("00000000-0000-0000-0000-000000000000", Profile.convertIdentifier("00000000000000000000000000000000").toString());
        Assert.assertEquals("00000000-0000-0000-0000-000000000000", Profile.convertIdentifier("00000000-0000-0000-0000-000000000000").toString());
        Assert.assertEquals("00000000000000000000000000000000", Profile.convertIdentifier(new UUID(0, 0)));
    }

    @Test
    public void isValidMojangIdentifier() {
        Assert.assertTrue(Profile.isValidMojangIdentifier("00000000000000000000000000000000"));
        Assert.assertFalse(Profile.isValidMojangIdentifier("00000000-0000-0000-0000-000000000000"));
        Assert.assertFalse(Profile.isValidMojangIdentifier("HighLordAkkarin"));
    }

    @Test
    public void isValidUUID() {
        Assert.assertTrue(Profile.isValidUUID("00000000-0000-0000-0000-000000000000"));
        Assert.assertFalse(Profile.isValidUUID("00000000000000000000000000000000"));
        Assert.assertFalse(Profile.isValidUUID("HighLordAkkarin"));
    }

    @Test
    public void isValidIdentifier() {
        Assert.assertTrue(Profile.isValidIdentifier("00000000-0000-0000-0000-000000000000"));
        Assert.assertTrue(Profile.isValidIdentifier("00000000000000000000000000000000"));
        Assert.assertFalse(Profile.isValidIdentifier("HighLordAkkarin"));
    }
}
