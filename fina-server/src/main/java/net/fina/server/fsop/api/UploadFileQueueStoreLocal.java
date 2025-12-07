package net.fina.server.fsop.api;


import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.entity.UploadFileQueueStatus;
import net.fina.server.fsop.event.RemoveUploadFileQueueEvent;

import java.util.List;
import java.util.Map;

public interface UploadFileQueueStoreLocal {

    List<UploadFileQueue> load(boolean firstStart, int limit);

    void changeStatus(List<UploadFileQueue> queueList, UploadFileQueueStatus status);

    void removeUploadFileQueue(RemoveUploadFileQueueEvent event);

    Map<String, Long> getWorkingFiles();

    long getNumberOfFilesInProgress();
}
