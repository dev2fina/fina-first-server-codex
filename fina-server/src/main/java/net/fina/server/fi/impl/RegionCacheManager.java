package net.fina.server.fi.impl;

import net.fina.common.client.fis.RegionModel;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.Region;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
public class RegionCacheManager {

    private final Map<Long, Region> regionCache = new ConcurrentHashMap<>();

    @Inject
    private RegionLocal regionLocal;

    @PostConstruct
    private void autoStart() {
        initCache();
    }


    private void initCache() {
        regionCache.putAll(regionLocal.loadRegionsIdMap());
    }

    public void addToCache(Region region) {
        regionCache.put(region.getId(), region);
    }

    public RegionModel getRegion(long regionId, long langId) {
        Region region = regionCache.get(regionId);
        if (region != null) {
            return new RegionModel(region.getId(), region.getCode(), region.getDescription().getNameStrId(), region.getDescription().getDescription(langId), region.getParentId());
        }
        return null;
    }

    @PreDestroy
    private void cleanUp() {
        regionCache.clear();
    }
}
