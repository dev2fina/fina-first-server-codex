package net.fina.server.rvc.impl;

import net.fina.server.rvc.event.ReturnVersionControlEvent;

import jakarta.ejb.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class ReturnVersionControlCacheInMemoryStoreService {

    private final static ConcurrentHashMap<String, ConcurrentHashMap<String, ReturnVersionControlEvent>> CACHE = new ConcurrentHashMap<>();

    @Lock(LockType.WRITE)
    public void set(String type, ReturnVersionControlEvent event) {
        if (type != null && event != null && event.getProcessId() != null) {
            ConcurrentHashMap<String, ReturnVersionControlEvent> processData = CACHE.get(event.getProcessId());
            if (processData == null) {
                processData = new ConcurrentHashMap<>();
                CACHE.put(event.getProcessId(), processData);
            }
            processData.put(type, event);
        }
    }

    @Lock(LockType.READ)
    public ReturnVersionControlEvent get(String processId, String type) {
        ConcurrentHashMap<String, ReturnVersionControlEvent> processData = CACHE.get(processId);
        if (processData != null) {
            return processData.get(type);
        }
        return null;
    }

    @Lock(LockType.WRITE)
    public void remove(ReturnVersionControlEvent event) {
        CACHE.remove(event.getProcessId());
    }
}
