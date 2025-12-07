package net.fina.server.fsop;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.security.api.PropertyLocal;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * nika on 4/17/2014.
 */
@Startup
@Singleton
@DependsOn({"MDTCacheManager", "ReturnCacheManager"})
public class FsopImportAutoStartService {
    private Logger log = Logger.getLogger(getClass());

    @EJB
    private FsopUploadFileMonitoringService fsopUploadFileMonitoringService;
    @EJB
    private PropertyLocal propertyLocal;

    @Resource
    private TimerService timerService;

    private AtomicBoolean firstStart = new AtomicBoolean(true);

    private AtomicBoolean parallelProcess = new AtomicBoolean(false);

    private AtomicInteger workingFilesLimit = new AtomicInteger(100);

    @PostConstruct
    private void start() {
        String serviceEnable = ConfigurationUtil.get().get("ENABLE_XML_PROCESS_SERVICE");

        log.info("ENABLE_XML_PROCESS_SERVICE: " + serviceEnable);

        if ((serviceEnable != null) && (!serviceEnable.isEmpty()) && (Integer.parseInt(serviceEnable) > 0)) {

            parallelProcess.set(enableParallelProcess());

            setWorkingFilesLimit();

            log.warn("Transaction isolation level: " + ConnectionProviderInitiator.toIsolationNiceName(propertyLocal.getTransactionIsolationLevel()));

            timerService.createIntervalTimer(1000 * 60, 1000 * 10, new TimerConfig(null, false));
        }
    }

    @Timeout
    public void automaticTimeout() {
        log.info("FSOP import...");
        fsopUploadFileMonitoringService.monitoringUploadFilesQueue(firstStart.get(), parallelProcess.get(), workingFilesLimit.get());
        firstStart.set(false);
    }

    private boolean enableParallelProcess() {
        String parallelProcessProperty = propertyLocal.getSystemProperty(PropertyKeys.PARALLEL_FILE_PROCESSING_ENABLE);
        log.info("PARALLEL_FILE_PROCESSING_ENABLE: " + parallelProcessProperty);
        if (parallelProcessProperty != null && (!parallelProcessProperty.isEmpty())) {
            try {
                return Integer.parseInt(parallelProcessProperty) > 0;
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return false;
    }

    private void setWorkingFilesLimit() {
        String limit = ConfigurationUtil.get().get("FSOP_IMPORT_WORKING_FILES_LIMIT");
        log.info("FSOP_IMPORT_WORKING_FILES_LIMIT: " + limit);
        if ((limit != null) && (!limit.isEmpty())) {
            try {
                workingFilesLimit.set(Integer.parseInt(limit));
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
    }
}
