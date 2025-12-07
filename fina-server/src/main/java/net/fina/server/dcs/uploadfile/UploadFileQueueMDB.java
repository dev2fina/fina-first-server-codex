package net.fina.server.dcs.uploadfile;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import net.fina.server.dcs.uploadfile.api.UploadFileLocal;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;
import net.fina.server.util.MdbMessageLoggerUtil;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;

@MessageDriven(name = "UploadFileQueueMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/UploadFileStateQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "3600")})
public class UploadFileQueueMDB implements MessageListener {

    @Inject
    private Logger log;

    @Inject
    private UploadFileLocal uploadFileLocal;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage objectMessage) {

                try (StatisticsLogger statLog = new StatisticsLogger(MdbMessageLoggerUtil.getMessageInfo(log, objectMessage), log, Logger.Level.INFO)) {
                    statLog.logMessage("start message process");
                    statLog.logStage("getObject");
                    UploadFileMetaModel uploadFile = (UploadFileMetaModel) objectMessage.getObject();

                    if (uploadFile.isConvert()) {
                        statLog.logStage("convert");
                        uploadFileLocal.convert(uploadFile.getId(), uploadFile.getLanguageCode());
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
