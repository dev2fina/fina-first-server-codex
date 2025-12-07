package net.fina.server.license;

import global.namespace.truelicense.api.License;
import net.fina.product.keymgr.LicenseManager;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static global.namespace.fun.io.bios.BIOS.path;

public class LicenseValidityTest {


    @Test
    public void checkLicense()throws Exception{
        LicenseManager manager = LicenseManager.standard;

        License bean = manager.context().licenseFactory().license();
        bean.setInfo(" FinA Product");
        net.fina.product.keymgr.LicenseManager lm= net.fina.product.keymgr.LicenseManager.standard;
        lm.install(path(new File("./src/test/resources/net.fina.server.license/license.lic").toPath()) );

        lm.verify();
        License license = lm.load();
        Date notAfter = license.getNotAfter();
        Date notBefore = license.getNotBefore();

        System.out.println(notAfter);
    }
}
