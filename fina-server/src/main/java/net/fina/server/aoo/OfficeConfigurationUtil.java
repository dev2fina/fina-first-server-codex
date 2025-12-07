package net.fina.server.aoo;

import net.fina.common.server.util.ConfigurationUtil;
import org.artofsolving.jodconverter.office.OfficeConnectionProtocol;
import org.artofsolving.jodconverter.process.ProcessManager;
import org.jboss.logging.Logger;

public class OfficeConfigurationUtil {
    private static final Logger log = Logger.getLogger(OfficeConfigurationUtil.class);

    public static boolean isMacroEnable() {
        String macroEnabled = ConfigurationUtil.get().get("OFFICE_MACRO_ENABLE");
        log.info("Office macro enable:" + macroEnabled);
        boolean isMacroEnabled = false;
        if (macroEnabled != null && !macroEnabled.isEmpty() && Integer.parseInt(macroEnabled) > 0) {
            isMacroEnabled = true;
        }
        return isMacroEnabled;
    }

    public static boolean isCloseDocumentEnable() {
        String documentCloseEnableString = ConfigurationUtil.get().get("OFFICE_DOCUMENT_CLOSE_ENABLE");
        log.info("Office document close enable:" + documentCloseEnableString);
        boolean closeDocument = false;
        if (documentCloseEnableString != null && !documentCloseEnableString.isEmpty() && Integer.parseInt(documentCloseEnableString) > 0) {
            closeDocument = true;
        }
        return closeDocument;
    }

    public static int getMaxTaskPerProcess() {
        String maxTaskPerProcessString = ConfigurationUtil.get().get("OFFICE_MAX_TASK_PER_PROCESS");
        log.info("Office max task per process:" + maxTaskPerProcessString);
        int maxTaskPerProcess = 200;
        if (maxTaskPerProcessString != null && !maxTaskPerProcessString.isEmpty()) {
            maxTaskPerProcess = Integer.parseInt(maxTaskPerProcessString);
        }
        return maxTaskPerProcess;
    }

    public static OfficeConnectionProtocol getOfficeConnectionProtocol() {
        OfficeConnectionProtocol officeConnectionProtocol = OfficeConnectionProtocol.SOCKET;
        String connectionProtocol = ConfigurationUtil.get().get("OFFICE_CONNECTION_PROTOCOL");
        log.info("Office connection protocol:" + officeConnectionProtocol);
        if (connectionProtocol != null && (!connectionProtocol.isEmpty())) {
            officeConnectionProtocol = OfficeConnectionProtocol.valueOf(connectionProtocol);
        }
        return officeConnectionProtocol;
    }

    public static ProcessManager getProcessManager() {
        ProcessManager processManager = null;
        try {
            String processManagerClassString = ConfigurationUtil.get().get("OFFICE_PROCESS_MANAGER");
            log.info("Office process manager: " + processManagerClassString);
            ClassLoader classLoader = OfficeConfigurationUtil.class.getClassLoader();
            Class<?> processManagerClass = classLoader.loadClass(processManagerClassString);
            processManager = (ProcessManager) processManagerClass.newInstance();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return processManager;
    }

    public static String getOfficeTemplatePath() {
        return ConfigurationUtil.get().get("OFFICE_TEMPLATE_DIR");
    }
}
