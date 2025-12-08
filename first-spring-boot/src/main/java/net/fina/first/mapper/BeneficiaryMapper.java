package net.fina.first.mapper;

import net.fina.first.dto.request.BeneficiaryRequest;
import net.fina.first.dto.response.BeneficiaryResponse;
import net.fina.first.model.Beneficiary;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for Beneficiary entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeneficiaryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fiRegistry", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "hierarchyLevel", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "sequence", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Beneficiary toEntity(BeneficiaryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fiRegistry", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "hierarchyLevel", ignore = true)
    @Mapping(target = "sequence", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(BeneficiaryRequest request, @MappingTarget Beneficiary entity);

    @Named("toResponse")
    @Mapping(target = "fiRegistryId", source = "fiRegistry.id")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "displayName", expression = "java(entity.getDisplayName())")
    @Mapping(target = "children", ignore = true)
    BeneficiaryResponse toResponse(Beneficiary entity);

    @Named("toResponseWithChildren")
    @Mapping(target = "fiRegistryId", source = "fiRegistry.id")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "displayName", expression = "java(entity.getDisplayName())")
    BeneficiaryResponse toResponseWithChildren(Beneficiary entity);

    @Named("toResponseList")
    default List<BeneficiaryResponse> toResponseList(List<Beneficiary> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::toResponse).toList();
    }
}
