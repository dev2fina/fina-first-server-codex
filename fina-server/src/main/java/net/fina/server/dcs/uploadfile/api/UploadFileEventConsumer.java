package net.fina.server.dcs.uploadfile.api;


import net.fina.common.client.exception.FinATypeException;
import net.fina.server.dcs.uploadfile.impl.event.UploadFileConvertEvent;

public interface UploadFileEventConsumer {
    void convertEvent(UploadFileConvertEvent uploadFileConvertEvent);
}
