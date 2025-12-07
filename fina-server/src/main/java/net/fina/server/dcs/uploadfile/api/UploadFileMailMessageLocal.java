package net.fina.server.dcs.uploadfile.api;

import net.fina.server.fsop.event.ProcessMailMessageEvent;

public interface UploadFileMailMessageLocal {
    void saveProcessingMailMessage(ProcessMailMessageEvent event);
}
