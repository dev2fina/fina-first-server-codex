package net.fina.server.processing.impl;


import jakarta.ejb.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class ReturnLockSingleton {

    /**
     * < long scheduleId, List < long versionId > >
     **/
    private final static ConcurrentHashMap<Long, CopyOnWriteArrayList<Long>> SCHEDULE_AND_VERSIONS = new ConcurrentHashMap<>();

    /**
     * < long returnId >
     **/
    private final static CopyOnWriteArrayList<Long> RETURNS = new CopyOnWriteArrayList<>();

    @Lock(LockType.WRITE)
    @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
    public void lock(long scheduleId, long versionId) {
        CopyOnWriteArrayList<Long> scheduleVersions = SCHEDULE_AND_VERSIONS.get(scheduleId);
        if (scheduleVersions == null) {
            scheduleVersions = new CopyOnWriteArrayList<>();
            SCHEDULE_AND_VERSIONS.put(scheduleId, scheduleVersions);
        }
        scheduleVersions.add(versionId);
    }

    @Lock(LockType.WRITE)
    @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
    public void unlock(long scheduleId, long versionId) {
        CopyOnWriteArrayList<Long> scheduleVersions = SCHEDULE_AND_VERSIONS.get(scheduleId);
        if (scheduleVersions != null) {
            scheduleVersions.remove(versionId);
        }
    }

    @Lock(LockType.WRITE)
    @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
    public void lock(long returnId) {
        RETURNS.add(returnId);
    }

    @Lock(LockType.WRITE)
    @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
    public void unlock(long returnId) {
        RETURNS.remove(returnId);
    }

    @Lock(LockType.WRITE)
    @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
    public void lock(long scheduleId, long versionId, long returnId) {
        lock(returnId);
        lock(scheduleId, versionId);
    }

    @Lock(LockType.WRITE)
    @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
    public void unlock(long scheduleId, long versionId, long returnId) {
        unlock(returnId);
        unlock(scheduleId, versionId);
    }

    @Lock(LockType.READ)
    @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
    public boolean isLock(long scheduleId, long versionId) {
        CopyOnWriteArrayList<Long> scheduleVersions = SCHEDULE_AND_VERSIONS.get(scheduleId);
        return scheduleVersions != null && scheduleVersions.contains(versionId);
    }

    @Lock(LockType.READ)
    @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
    public boolean isLock(long returnId) {
        return RETURNS.contains(returnId);
    }
}
