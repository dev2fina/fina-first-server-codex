package net.fina.first.mapper;

import net.fina.first.dto.response.LegalFormResponse;
import net.fina.first.model.LegalForm;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for LegalForm entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LegalFormMapper {

    LegalFormResponse toResponse(LegalForm entity);

    List<LegalFormResponse> toResponseList(List<LegalForm> entities);
}
