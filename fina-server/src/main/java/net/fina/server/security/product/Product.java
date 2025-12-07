package net.fina.server.security.product;

import global.namespace.fun.io.api.Source;
import global.namespace.truelicense.api.ConsumerLicenseManager;
import global.namespace.truelicense.api.License;
import global.namespace.truelicense.api.LicenseManagementException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.product.keymgr.LicenseManager;
import org.jboss.logging.Logger;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Date;

import static global.namespace.fun.io.bios.BIOS.file;

@SuppressWarnings("java:S6548")
public class Product {
    private static Product ourInstance;
    private static final Logger log = Logger.getLogger(Product.class);
    private final ConsumerLicenseManager manager = LicenseManager.get();
    private Date notAfter;
    private Date notBefore;
    private JSONObject extras;

    private Product() throws LicenseManagementException {
        install(ConfigurationUtil.get().get("LICENSE"));
    }

    public static Product getInstance() throws LicenseManagementException {
        if (ourInstance == null) {
            ourInstance = new Product();
        }
        return ourInstance;
    }

    @SuppressWarnings("java:S112")
    public void check() throws LicenseManagementException {
        try {
            manager.verify();
        } catch (LicenseManagementException e) {
            SecureRandom random = new SecureRandom();
            int randomInt = random.nextInt(1000);
            throw new RuntimeException("181112:" + (randomInt));
        }
    }

    private void install(String resourceName) throws LicenseManagementException {
        Source source = file(resourceName);
        manager.install(source);

        License license = manager.load();
        notAfter = license.getNotAfter();
        notBefore = license.getNotBefore();
        extras = getExtrasJson(license.getExtra());
    }

    private JSONObject getExtrasJson(Object extras) {
        if (extras != null) {
            try {
                return new JSONObject(String.valueOf(extras));
            } catch (Exception t) {
                log.error(t.getMessage(), t);
            }
        }
        return null;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public JSONObject getExtrasJson() {
        return extras;
    }

    public String getExtrasJsonString() {
        if (extras != null) {
            return extras.toString();
        }
        return null;
    }
}
