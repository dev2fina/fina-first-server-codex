package net.fina.first.mapper;

import net.fina.first.dto.response.FiTypeResponse;
import net.fina.first.model.FiType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for FiType entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FiTypeMapper {

    FiTypeResponse toResponse(FiType entity);

    List<FiTypeResponse> toResponseList(List<FiType> entities);
}
