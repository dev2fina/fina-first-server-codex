package net.fina.server.reg.api;

import net.fina.server.fsop.entity.UploadFileQueue;

public interface RegFileProcessorLocal {
    void convertAndProcessRegFile(UploadFileQueue uploadFileQueue, boolean jMSRedelivered) throws Exception;
}
