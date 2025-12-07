package net.fina.server.dcs.uploadfile.impl;

import net.fina.server.dcs.uploadfile.api.UploadFileEventConsumer;
import net.fina.server.dcs.uploadfile.impl.event.UploadFileConvertEvent;
import net.fina.server.dcs.uploadfile.impl.event.UploadFileErrorEvent;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;
import org.jboss.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;

@Stateless
@Local(UploadFileEventConsumer.class)
public class UploadFileEventConsumerSession implements UploadFileEventConsumer {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/jms/queue/UploadFileStateQueue")
    private jakarta.jms.Queue queue;

    @Inject
    private Event<UploadFileErrorEvent> uploadFileErrorEventEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void convertEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) UploadFileConvertEvent uploadFileConvertEvent) {

        UploadFileMetaModel model = new UploadFileMetaModel();
        model.setConvert(true);
        model.setId(uploadFileConvertEvent.getUploadFileId());
        model.setLanguageCode(uploadFileConvertEvent.getLanguageCode());
        model.setFileName(uploadFileConvertEvent.getFileName());
        try {
            context.createProducer().send(queue, context.createObjectMessage(model));
        } catch (Throwable t) {
            log.error("Could Not Send JMS Message, Possible reasons JMS Server is down!!!!");
            log.error(t.getMessage(), t);
            uploadFileErrorEventEvent.fire(new UploadFileErrorEvent(uploadFileConvertEvent.getUploadFileId(), "net.fina.exception.generalError"));
        }
    }
}
