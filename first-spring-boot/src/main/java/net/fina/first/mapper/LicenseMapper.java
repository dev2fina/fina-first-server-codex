package net.fina.first.mapper;

import net.fina.first.dto.request.LicenseRequest;
import net.fina.first.dto.response.LicenseResponse;
import net.fina.first.model.License;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for License entity.
 */
@Mapper(
        componentModel = "spring",
        uses = {LicenseTypeMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LicenseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fiRegistry", ignore = true)
    @Mapping(target = "licenseType", ignore = true)
    @Mapping(target = "suspensionDate", ignore = true)
    @Mapping(target = "revocationDate", ignore = true)
    @Mapping(target = "suspensionReason", ignore = true)
    @Mapping(target = "revocationReason", ignore = true)
    @Mapping(target = "sequence", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    License toEntity(LicenseRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fiRegistry", ignore = true)
    @Mapping(target = "licenseType", ignore = true)
    @Mapping(target = "suspensionDate", ignore = true)
    @Mapping(target = "revocationDate", ignore = true)
    @Mapping(target = "suspensionReason", ignore = true)
    @Mapping(target = "revocationReason", ignore = true)
    @Mapping(target = "sequence", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(LicenseRequest request, @MappingTarget License entity);

    @Mapping(target = "fiRegistryId", source = "fiRegistry.id")
    @Mapping(target = "valid", expression = "java(entity.isValid())")
    LicenseResponse toResponse(License entity);

    default List<LicenseResponse> toResponseList(List<License> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::toResponse).toList();
    }
}
