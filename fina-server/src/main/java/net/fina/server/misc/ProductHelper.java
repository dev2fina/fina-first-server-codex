package net.fina.server.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.security.product.OSTLicense;
import net.fina.server.security.product.OSTLicenseKind;
import net.fina.server.security.product.Product;
import org.jboss.logging.Logger;
import org.json.JSONObject;

import java.util.Date;

public class ProductHelper {

    private static volatile ProductHelper instance;
    private final Logger log = Logger.getLogger(getClass());
    private boolean isAdvancedDashboardEnable;
    private boolean isExportImportEnable;
    private OSTLicense ostLicense;

    private ProductHelper() {
        try {
            Product.getInstance().check();
            String jsonString = Product.getInstance().getExtrasJsonString();
            JSONObject jsonObject = new JSONObject(jsonString);
            isAdvancedDashboardEnable = jsonObject.getBoolean(LicenseExtraKey.ADVANCED_DASHBOARD_ENABLE);
            isExportImportEnable = jsonObject.getBoolean(LicenseExtraKey.EXPORT_IMPORT_ENABLE);
            if (jsonObject.has(LicenseExtraKey.OST_LICENSE)) {
                this.ostLicense = new ObjectMapper().readValue(jsonObject.getJSONObject(LicenseExtraKey.OST_LICENSE).toString(), OSTLicense.class);
            } else {
                ostLicense = new OSTLicense(OSTLicenseKind.FREE);
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    public static void validateOstLicense(String fiType) throws FinATypeException {
        OSTLicense ostLicense = getInstance().getOstLicense();
        switch (ostLicense.getLicenseKind()) {
            case PAID -> {
                if (new Date().getTime() > ostLicense.getNotAfter().getTime()) {
                    throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR, "OST License Expired");
                }

                if (!ostLicense.getFiTypes().contains(fiType)) {
                    throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR, "OST License does not support this fi type");
                }
            }
            case PROHIBITED ->
                    throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR, "OST Usage is Prohibited for everyone");

        }
    }

    public static ProductHelper getInstance() {
        if (instance == null) {
            synchronized (ProductHelper.class) {
                if (instance == null) {
                    instance = new ProductHelper();
                }
            }
        }
        return instance;
    }

    public boolean isAdvancedDashboardEnable() {
        return isAdvancedDashboardEnable;
    }

    public boolean isExportImportEnable() {
        return isExportImportEnable;
    }

    public OSTLicense getOstLicense() {
        return ostLicense;
    }
}
