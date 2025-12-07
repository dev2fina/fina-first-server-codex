package net.fina.server.fi.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.entity.Region;

import java.util.List;
import java.util.Map;

public interface RegionLocal {

    List<Region> loadRegions();

    List<Region> loadRegionsFirstLevel();

    Map<String, Region> loadRegionsCodeMap();

    Map<String, Region> loadRegionsCode1Map();

    Map<Long, Region> loadRegionsIdMap();

    Region save(Region region) throws FinATypeException;

    void delete(Long id) throws FinATypeException;

    void setProperties(Map<Integer, String> map, Long curLangId);

    Map<Integer, String> getProperties(Long curLangId);

    String getPath(Long id, Long langId);

    Map<Integer, String> getPropertiesOld(Long langId);

    List<Region> dragAndDrop(List<Region> regions);

    Region getRegionWithId(long regionId);

    List<Long> loadRegionChildrenIdsByCode(String regionCode);

    Region findByCode(String regionCode) throws FinATypeException;

    Region findByCode1(String regionCode) throws FinATypeException;

    List<Region> getRegionByIds(List<Long> ids);

    Region restoreDeletedRegion(long regionId) throws FinATypeException;

    Region findDeletedRegionByCode(String regionCode);

    List<Region> loadByParentId(long parentId);

    List<Long> loadRegionIdsByParent(long regionId);


    List<Region> loadRegionTreeByParentId(long parentId);
}
