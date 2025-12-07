package net.fina.server.fsop;

import jakarta.ejb.*;
import net.fina.server.fsop.api.FsopImportLocal;
import net.fina.server.fsop.api.UploadFileQueueStoreLocal;
import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.entity.UploadFileQueueStatus;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class FsopUploadFileMonitoringService {

    private final ConcurrentHashMap<Long, List<Long>> workingFilesByUser = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> workingFilesByName = new ConcurrentHashMap<>();
    private final Logger log = Logger.getLogger(getClass());
    @EJB
    private UploadFileQueueStoreLocal uploadFileQueueStoreLocal;
    @EJB
    private FsopImportLocal importLocal;

    @Lock(LockType.READ)
    @AccessTimeout(unit = TimeUnit.MINUTES, value = 2)
    public void monitoringUploadFilesQueue(boolean firstStart, boolean parallelProcess, int workingFilesLimit) {
        if ((!parallelProcess) && (!workingFilesByName.isEmpty())) {
            return;
        }

        if (workingFilesByName.size() >= workingFilesLimit) {
            return;
        }

        List<UploadFileQueue> queueList = uploadFileQueueStoreLocal.load(firstStart, (parallelProcess ? workingFilesLimit : 1));

        Map<Long, List<UploadFileQueue>> filesByUser = new HashMap<>();

        Map<Long, List<Long>> temp = new ConcurrentHashMap<>();

        for (UploadFileQueue file : queueList) {
            if ((workingFilesByName.get(file.getFileName()) == null) && (workingFilesByUser.get(file.getUserId()) == null || workingFilesByUser.get(file.getUserId()).isEmpty())) {

                workingFilesByName.put(file.getFileName(), file.getFileId());

                List<Long> userWorkingFiles = temp.get(file.getUserId());
                if (userWorkingFiles == null) {
                    userWorkingFiles = new CopyOnWriteArrayList<>();
                    temp.put(file.getUserId(), userWorkingFiles);
                }
                userWorkingFiles.add(file.getFileId());

                List<UploadFileQueue> userFiles = filesByUser.get(file.getUserId());
                if (userFiles == null) {
                    userFiles = new ArrayList<>();
                    filesByUser.put(file.getUserId(), userFiles);
                }
                userFiles.add(file);
            }
        }

        workingFilesByUser.putAll(temp);

        for (List<UploadFileQueue> fileQueues : filesByUser.values()) {
            try {
                uploadFileQueueStoreLocal.changeStatus(fileQueues, UploadFileQueueStatus.IN_PROGRESS);
                importLocal.processFiles(fileQueues);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                uploadFileQueueStoreLocal.changeStatus(fileQueues, UploadFileQueueStatus.UNDEFINED);
            }
        }
    }

    public void removeUserFileFromWorkingStore(UploadFileQueue uploadFileQueue) {
        List<Long> userFiles = workingFilesByUser.get(uploadFileQueue.getUserId());
        if (userFiles != null) {
            userFiles.remove(uploadFileQueue.getFileId());
        }
        workingFilesByName.remove(uploadFileQueue.getFileName());
    }

    @Lock(LockType.READ)
    public ConcurrentHashMap<String, Long> getWorkingFiles() {
        return this.workingFilesByName;
    }


}
