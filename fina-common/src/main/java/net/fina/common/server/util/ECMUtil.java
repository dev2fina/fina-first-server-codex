package net.fina.common.server.util;

public class ECMUtil {

    public static boolean isEcmEnable() {
        String enableEcm = ConfigurationUtil.get().get("ECM.enable");
        return enableEcm != null && (!enableEcm.isEmpty()) && Integer.parseInt(enableEcm) > 0;
    }
}
