package net.fina.first.ecm.file.service;

import net.fina.common.server.util.CommonUtil;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.first.ecm.file.exception.FileImportIsNotEnabledException;
import net.fina.first.ecm.file.model.BulkFileImportStatusMetaModel;
import net.fina.first.ecm.file.proxy.FileImportProxySession;
import org.jboss.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;

@Startup
@Singleton
public class FileImportAutoStartService {

    private Logger log = Logger.getLogger(getClass());

    @Resource
    private TimerService timerService;

    @Inject
    private FileImportProxySession fileImportProxySession;

    @PostConstruct
    private void start() {
        if (CommonUtil.isEcmEnable() && CommonUtil.isEcmFileImportEnabled()) {
            int checkInterval = getCheckInterval();
            timerService.createIntervalTimer(60 * 1000, checkInterval, new TimerConfig(null, false));
        }
    }

    @Timeout
    public void timeout() {
        log.info("ECM File Import Service ...");

        try {
            BulkFileImportStatusMetaModel currentBulkFileImportStatus = fileImportProxySession.getBulkFileImportStatus();
            if (currentBulkFileImportStatus != null && currentBulkFileImportStatus.getCurrentStatus().equalsIgnoreCase("Idle")) {
                currentBulkFileImportStatus = fileImportProxySession.initiateBulkFileImport();

                log.info("ECM File Import Service Status ...");
                log.info(currentBulkFileImportStatus);
            }
        } catch (FileImportIsNotEnabledException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int getCheckInterval() {
        int checkInterval = 60 * 1000;

        try {
            checkInterval = Integer.parseInt(ConfigurationUtil.get().get("ECM.fileImport.checkInterval"));
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return checkInterval;
    }
}
