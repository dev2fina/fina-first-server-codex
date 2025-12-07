package net.fina.first.mapper;

import net.fina.first.dto.response.RegionResponse;
import net.fina.first.model.Region;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for Region entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegionMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "children", ignore = true)
    RegionResponse toResponse(Region entity);

    @Mapping(target = "parentId", source = "parent.id")
    RegionResponse toResponseWithChildren(Region entity);

    List<RegionResponse> toResponseList(List<Region> entities);
}
