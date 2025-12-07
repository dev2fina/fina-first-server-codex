package net.fina.common.shared.helper;

import net.fina.common.server.util.ConfigurationUtil;
import org.jboss.logging.Logger;

public class XlsxSupportHelper {

    private Logger log = Logger.getLogger(getClass());

    private static volatile XlsxSupportHelper _instance;

    private boolean enableXlsxSupport;

    private XlsxSupportHelper() {
        try {
            String enableXlsxProperty = ConfigurationUtil.get().get("SUPPORT_XLSX_ENABLE");
            log.info("XLSX support enable property is: " + enableXlsxProperty);
            enableXlsxSupport = Integer.parseInt(enableXlsxProperty) > 0;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    public static XlsxSupportHelper getInstance() {
        if (_instance == null) {
            synchronized (XlsxSupportHelper.class) {
                if (_instance == null) {
                    _instance = new XlsxSupportHelper();
                }
            }
        }
        return _instance;
    }

    public boolean isEnableXlsxSupport() {
        return enableXlsxSupport;
    }
}
