package net.minepay.mcapi.controller.v1;

import net.minepay.mcapi.entity.ServerVersion;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;

/**
 * Provides version information and otherwise relevant instance data.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@RestController
@RequestMapping("/v1")
public class RootController {

    /**
     * Provides a set of globally valid information on the application.
     *
     * @return a server version.
     */
    @Nonnull
    @RequestMapping(method = RequestMethod.GET)
    public ServerVersion index() {
        Package p = this.getClass().getPackage();

        String name = p.getImplementationTitle();
        String version = p.getImplementationVersion();
        String vendor = p.getImplementationVendor();

        if (name == null) {
            name = "MCAPI";
        }

        if (version == null) {
            version = "0.0.0-SNAPSHOT";
        }

        if (vendor == null) {
            vendor = "Minepay";
        }

        return new ServerVersion(name, version, vendor);
    }
}
