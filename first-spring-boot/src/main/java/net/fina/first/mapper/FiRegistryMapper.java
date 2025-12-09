package net.fina.first.mapper;

import net.fina.first.dto.request.FiRegistryCreateRequest;
import net.fina.first.dto.request.FiRegistryUpdateRequest;
import net.fina.first.dto.response.FiRegistryResponse;
import net.fina.first.model.FiRegistry;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for FiRegistry entity.
 */
@Mapper(
        componentModel = "spring",
        uses = {FiTypeMapper.class, RegionMapper.class, LegalFormMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FiRegistryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "identificationNumber", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "fiType", ignore = true)
    @Mapping(target = "legalForm", ignore = true)
    @Mapping(target = "legalAddressRegion", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "licenseStatus", ignore = true)
    @Mapping(target = "branches", ignore = true)
    @Mapping(target = "administrators", ignore = true)
    @Mapping(target = "beneficiaries", ignore = true)
    @Mapping(target = "licenses", ignore = true)
    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    FiRegistry toEntity(FiRegistryCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "identificationNumber", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "fiType", ignore = true)
    @Mapping(target = "legalForm", ignore = true)
    @Mapping(target = "legalAddressRegion", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "licenseStatus", ignore = true)
    @Mapping(target = "branches", ignore = true)
    @Mapping(target = "administrators", ignore = true)
    @Mapping(target = "beneficiaries", ignore = true)
    @Mapping(target = "licenses", ignore = true)
    @Mapping(target = "actions", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(FiRegistryUpdateRequest request, @MappingTarget FiRegistry entity);

    @Mapping(target = "legalAddressRegion", source = "legalAddressRegion", qualifiedByName = "toResponse")
    @Mapping(target = "branchCount", expression = "java(entity.getBranches() != null ? entity.getBranches().size() : 0)")
    @Mapping(target = "administratorCount", expression = "java(entity.getAdministrators() != null ? entity.getAdministrators().size() : 0)")
    @Mapping(target = "beneficiaryCount", expression = "java(entity.getBeneficiaries() != null ? entity.getBeneficiaries().size() : 0)")
    @Mapping(target = "licenseCount", expression = "java(entity.getLicenses() != null ? entity.getLicenses().size() : 0)")
    FiRegistryResponse toResponse(FiRegistry entity);

    default List<FiRegistryResponse> toResponseList(List<FiRegistry> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::toResponse).toList();
    }
}
