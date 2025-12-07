package net.fina.server.fsop.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.fsop.FsopUploadFileMonitoringService;
import net.fina.server.fsop.api.FsopImportLocal;
import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.event.DcsUploadFileProcessStatusUpdateEvent;
import net.fina.server.reg.api.RegFileProcessorLocal;
import net.fina.server.util.MdbMessageLoggerUtil;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;

@MessageDriven(name = "FsopProcessQueueMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/FsopProcessQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "50"),
        @ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "10800")})
public class FsopProcessQueueMDB implements MessageListener {

    @Inject
    private Logger log;

    @Inject
    private FsopImportLocal fsopImportLocal;

    @EJB
    private FsopUploadFileMonitoringService fsopUploadFileMonitoringService;

    @Inject
    private Event<DcsUploadFileProcessStatusUpdateEvent> dcsUploadFileProcessStatusUpdateEvent;

    @Inject
    private RegFileProcessorLocal regFileProcessorLocal;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage objectMessage) {

                try (StatisticsLogger statLog = new StatisticsLogger(MdbMessageLoggerUtil.getMessageInfo(log, objectMessage), log, Logger.Level.INFO);) {
                    statLog.logMessage("start message process");

                    statLog.logStage("getObject");
                    UploadFileQueue uploadFileQueue = (UploadFileQueue) objectMessage.getObject();
                    statLog.logStage("processFile");
                    if (!fsopUploadFileMonitoringService.getWorkingFiles().containsKey(uploadFileQueue.getFileName())) {
                        fsopUploadFileMonitoringService.getWorkingFiles().put(uploadFileQueue.getFileName(), uploadFileQueue.getFileId());
                    }
                    ProcessEngine processEngine = uploadFileQueue.getProcessEngine();
                    switch (processEngine) {
                        case FINA:
                            fsopImportLocal.processFile(uploadFileQueue);
                            break;
                        case REG:
                        case REG_ADVANCED:
                            regFileProcessorLocal.convertAndProcessRegFile(uploadFileQueue, message.getJMSRedelivered());
                            break;
                    }


                    statLog.logStage("removeUserFileFromWorkingStore");
                    fsopUploadFileMonitoringService.removeUserFileFromWorkingStore(uploadFileQueue);

                    DcsUploadFileProcessStatusUpdateEvent processStatusUpdateEvent = new DcsUploadFileProcessStatusUpdateEvent(uploadFileQueue.getFileId());
                    this.dcsUploadFileProcessStatusUpdateEvent.fire(processStatusUpdateEvent);

                }
            } else {
                log.error("unexpected message received in Queue: " + message);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
