package net.fina.server.fsop.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.server.dcs.uploadfile.api.UploadFileLocal;
import net.fina.server.fsop.FsopUploadFileMonitoringService;
import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.event.RemoveUploadFileQueueEvent;
import org.jboss.logging.Logger;

@MessageDriven(name = "DLQ", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/DLQ"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "5"),
        @ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "10800")})
public class DLQQueueMDB implements MessageListener {
    @Inject
    private Logger log;

    @Inject
    private Event<RemoveUploadFileQueueEvent> removeUploadFileQueueEvent;
    @Inject
    private UploadFileLocal uploadFileLocal;
    @Inject
    private FsopUploadFileMonitoringService fsopUploadFileMonitoringService;


    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage objectMessage) {
            try {

                log.info("Received DLQ message " + objectMessage);
                if (objectMessage.getObject() instanceof UploadFileQueue queueFile) {
                    log.info("Change Upload File Status, file id : " + queueFile.getFileId());
                    uploadFileLocal.changeStatus(queueFile.getFileId(), UploadFileStatus.ERROR, "${net.fina.processing.generalError}");
                    log.info("Remove Upload File from queue" + queueFile);
                    RemoveUploadFileQueueEvent event = new RemoveUploadFileQueueEvent(queueFile);
                    removeUploadFileQueueEvent.fire(event);
                    fsopUploadFileMonitoringService.removeUserFileFromWorkingStore(queueFile);
                }
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
        }

    }
}
