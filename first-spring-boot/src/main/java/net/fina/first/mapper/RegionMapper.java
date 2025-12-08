package net.fina.first.mapper;

import net.fina.first.dto.response.RegionResponse;
import net.fina.first.model.Region;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for Region entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegionMapper {

    @Named("toResponse")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "children", ignore = true)
    RegionResponse toResponse(Region entity);

    @Named("toResponseWithChildren")
    @Mapping(target = "parentId", source = "parent.id")
    RegionResponse toResponseWithChildren(Region entity);

    @Named("toResponseList")
    default List<RegionResponse> toResponseList(List<Region> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::toResponse).toList();
    }
}
