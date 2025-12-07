package net.fina.server.fi.proxy;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.Region;
import net.fina.server.fi.model.RegionDragAndDropRequestModel;
import net.fina.server.fi.model.RegionMetaModel;
import net.fina.server.fi.model.RegionMetaModelHelper;
import net.fina.server.fi.model.RegionPropertyMetaModel;
import net.fina.server.i18n.helper.Description;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class RegionProxySession {
    @Inject
    private RegionLocal regionLocal;

    public List<RegionMetaModel> load() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return RegionMetaModelHelper.toModels(regionLocal.loadRegions(), langId);
    }

    public List<RegionMetaModel> loadRegionsFirstLevel() {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        return RegionMetaModelHelper.toModels(regionLocal.loadRegionsFirstLevel(), lang.getId());
    }

    public RegionMetaModel loadWithId(Long regionId) {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();
        Region region = regionLocal.getRegionWithId(regionId.longValue());
        return RegionMetaModelHelper.toModel(region, lang.getId());
    }


    public List<RegionMetaModel> loadPath() {
        List<RegionMetaModel> models = new ArrayList<>();
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        List<Region> regions = regionLocal.loadRegions();

        Set<Long> parents = new HashSet<>();
        Map<Long, Region> longRegionMap = new HashMap<>();
        for (Region region : regions) {
            longRegionMap.put(region.getId(), region);
            parents.add(region.getParentId());
        }

        for (Region region : regions) {
            if (!parents.contains(region.getId())) {
                RegionMetaModel regionModel = new RegionMetaModel();
                regionModel.setId(region.getId());
                printPath(longRegionMap, region, lang.getId(), regionModel);
                models.add(regionModel);
            }
        }

        return models;
    }

    public void delete(long id) throws FinATypeException {
        regionLocal.delete(id);
    }

    public RegionMetaModel save(RegionMetaModel model) throws FinATypeException {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        Region region = RegionMetaModelHelper.toEntity(model);
        region.setDescription(new Description(lang.getId(), model.getNameStrId(), model.getName()));

        return RegionMetaModelHelper.toModel(regionLocal.save(region), lang.getId());
    }

    public RegionMetaModel restoreDeletedRegion(RegionMetaModel model) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();

        Region region = regionLocal.findByCode(model.getCode());

        if (region != null) {
            if (region.isDeleted()) {
                region.setDescription(new Description(langId, model.getNameStrId(), model.getName()));
                region.setParentId(model.getParentId());
                return RegionMetaModelHelper.toModel(regionLocal.restoreDeletedRegion(region.getId()), langId);
            } else {
                throw new FinATypeException("Region is not deleted");
            }
        } else {
            throw new FinATypeException("Region code doesn't exist. Region code: [ " + model.getCode() + " ]");
        }

    }


    public List<RegionMetaModel> dragAndDrop(RegionDragAndDropRequestModel regionDragAndDropRequestModel) {
        List<Region> regions = new ArrayList<>();

        for (RegionMetaModel regionModel : regionDragAndDropRequestModel.getChildren()) {
            regionModel.setParentId(regionDragAndDropRequestModel.getParent().getId());
            Region region = RegionMetaModelHelper.toEntity(regionModel);
            regions.add(region);
        }
        // Drag and Drop
        regionLocal.dragAndDrop(regions);

        return regionDragAndDropRequestModel.getChildren();
    }

    public List<RegionPropertyMetaModel> loadProperties() {

        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        Map<Integer, String> map = regionLocal.getProperties(lang.getId());
        List<RegionPropertyMetaModel> properties = new ArrayList<>();
        RegionPropertyMetaModel model = new RegionPropertyMetaModel();
        model.setId(0);
        model.setLevel(map.get(0));
        properties.add(model);

        for (int i = 1; i < map.size(); i++) {
            model = new RegionPropertyMetaModel();
            model.setId(i);
            model.setLevel("Level " + i);
            model.setName(map.get(i));
            properties.add(model);
        }

        return properties;
    }

    public void setProperties(List<RegionPropertyMetaModel> regionLevels) {
        Map<Integer, String> levelMap = new HashMap<>();
        levelMap.put(0, String.valueOf((regionLevels.size() - 1)));
        for (int i = 1; i < regionLevels.size(); i++) {
            levelMap.put(i, regionLevels.get(i).getName());
        }
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        regionLocal.setProperties(levelMap, lang.getId());
    }

    public void deleteRegionLastLevel() throws FinATypeException {
        List<RegionPropertyMetaModel> properties = loadProperties();
        validateRegionLevel(properties.size());

        if (properties.size() > 1) {
            properties.remove(properties.size() - 1);
            setProperties(properties);
        }
    }

    public PaginatedListWrapper<RegionMetaModel> loadRegionsPathForCombo() {
        List<RegionMetaModel> regions = loadPath();

        return new PaginatedListWrapper<>(regions, 10, regions.size());

    }

    public PaginatedListWrapper<RegionMetaModel> findRegionModelItems(String text, int limit, int offset) {
        List<RegionMetaModel> result = new ArrayList<>();
        List<RegionMetaModel> temp = new ArrayList<>();

        List<RegionMetaModel> regionModels = loadPath();

        if (text != null && !text.trim().isEmpty()) {
            text = text.toLowerCase();
            if (regionModels != null) {
                for (RegionMetaModel model : regionModels) {
                    if (model.getName().toLowerCase().contains(text)) {
                        temp.add(model);
                    }
                }
            }
        } else { // load all regions
            temp = regionModels;
        }

        int total = temp.size();
        int totalLimit = total;
        if (limit > 0) {
            totalLimit = Math.min(offset + limit, totalLimit);
        }

        if (offset < temp.size()) {
            result.addAll(temp.subList(offset, totalLimit));
        }

        return new PaginatedListWrapper<>(result, limit, total);
    }

    private void printPath(Map<Long, Region> longRegionMap, Region region, long langId, RegionMetaModel regionModel) {
        Region tmpRegion = longRegionMap.get(region.getParentId());
        if (tmpRegion != null) {
            printPath(longRegionMap, tmpRegion, langId, regionModel);
        }
        regionModel.setName((regionModel.getName() == null ? "" : regionModel.getName() + " / ") + "[" + region.getCode() + "]" + region.getDescription().getDescription(langId));
    }

    public List<RegionMetaModel> loadChildren(long parentId) {
        LanguageSampleModel lang = ThreadLocalHolder.getLanguage();

        return RegionMetaModelHelper.toModels(regionLocal.loadByParentId(parentId), lang.getId());
    }

    public List<Long> loadChildIdsByParent(long regionId) {
        return regionLocal.loadRegionIdsByParent(regionId);
    }

    public Map<Long, Region> loadRegionsIdMap() {
        return regionLocal.loadRegionsIdMap();
    }


    public List<RegionMetaModel> loadRegionTreeByParent(long parentId) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<Region> regions = regionLocal.loadRegionTreeByParentId(parentId);

        Map<Long, Region> longRegionMap = new HashMap<>();
        for (Region region : regions) {
            longRegionMap.put(region.getId(), region);
        }
        List<RegionMetaModel> models = new ArrayList<>();

        for (Region region : regions) {
            RegionMetaModel regionModel = new RegionMetaModel();
            regionModel.setId(region.getId());
            regionModel.setParentId(region.getParentId());
            printPath(longRegionMap, region, langId, regionModel);
            models.add(regionModel);

        }

        return models;
    }

    private void validateRegionLevel(int maxLevel) throws FinATypeException {
        List<Region> allRegions = regionLocal.loadRegions();
        for (Region r : allRegions) {
            if (r.getParentId() > 0) {
                AtomicInteger level = new AtomicInteger(2);
                getRegionLevel(r, allRegions, level);

                if (level.get() >= maxLevel) {
                    throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
                }
            }
        }

    }

    private void getRegionLevel(Region region, List<Region> allregions, AtomicInteger level) {
        if (region.getParentId() > 0) {
            level.incrementAndGet();
            List<Region> regions = allregions.stream().filter(r -> r.getId() == region.getParentId()).toList();
            if (!regions.isEmpty()) {
                getRegionLevel(regions.get(0), allregions, level);
            }
        }
    }
}
