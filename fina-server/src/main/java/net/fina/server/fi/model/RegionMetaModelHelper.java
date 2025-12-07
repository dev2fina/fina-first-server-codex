package net.fina.server.fi.model;

import net.fina.server.fi.entity.Region;

import java.util.ArrayList;
import java.util.List;

public class RegionMetaModelHelper {

    public static List<RegionMetaModel> toModels(List<Region> regions, long langId) {
        List<RegionMetaModel> result = new ArrayList<>();
        if (regions != null && !regions.isEmpty()) {
            for (Region region : regions) {
                result.add(toModel(region, langId));
            }
        }
        return result;
    }

    public static RegionMetaModel toModel(Region region, long langId) {
        RegionMetaModel model = new RegionMetaModel();
        model.setId(region.getId());
        model.setCode(region.getCode());
        model.setCode1(region.getCode1());

        if (region.getDescription() != null) {
            model.setName(region.getDescription().getDescription(langId));
            model.setNameStrId(region.getDescription().getNameStrId());
        }

        model.setParentId(region.getParentId());
        model.setSequence(region.getSequence());
        model.setParentId(region.getParentId());

        if (region.getVersion() != null) {
            model.setLevel(region.getVersion());

        }

        return model;
    }

    public static Region toEntity(RegionMetaModel model) {
        Region entity = new Region();
        entity.setId(model.getId());
        entity.setCode(model.getCode());
        entity.setCode1(model.getCode1());
        entity.setParentId(model.getParentId());
        entity.setSequence(model.getSequence());
        entity.setParentId(model.getParentId());

        return entity;
    }
}
