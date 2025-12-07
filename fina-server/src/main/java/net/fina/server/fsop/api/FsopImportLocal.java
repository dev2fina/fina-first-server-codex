package net.fina.server.fsop.api;


import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.processing.impl.FspFileProcessDoneEvent;

import java.util.List;

public interface FsopImportLocal {
    void processFiles(List<UploadFileQueue> queueFiles);

    void processFile(UploadFileQueue queueFile);

    void afterProcessDoneEventHandler(FspFileProcessDoneEvent event);
}
