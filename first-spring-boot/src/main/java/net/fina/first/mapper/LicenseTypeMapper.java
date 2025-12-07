package net.fina.first.mapper;

import net.fina.first.dto.response.LicenseTypeResponse;
import net.fina.first.model.LicenseType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for LicenseType entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LicenseTypeMapper {

    LicenseTypeResponse toResponse(LicenseType entity);

    List<LicenseTypeResponse> toResponseList(List<LicenseType> entities);
}
