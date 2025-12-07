package net.fina.first.mapper;

import net.fina.first.dto.request.BranchRequest;
import net.fina.first.dto.response.BranchResponse;
import net.fina.first.model.Branch;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for Branch entity.
 */
@Mapper(
        componentModel = "spring",
        uses = {RegionMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BranchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fiRegistry", ignore = true)
    @Mapping(target = "region", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Branch toEntity(BranchRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fiRegistry", ignore = true)
    @Mapping(target = "region", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(BranchRequest request, @MappingTarget Branch entity);

    @Mapping(target = "fiRegistryId", source = "fiRegistry.id")
    BranchResponse toResponse(Branch entity);

    List<BranchResponse> toResponseList(List<Branch> entities);
}
