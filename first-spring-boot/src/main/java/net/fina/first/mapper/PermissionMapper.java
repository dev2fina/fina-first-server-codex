package net.fina.first.mapper;

import net.fina.first.dto.response.PermissionResponse;
import net.fina.first.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Permission entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    PermissionResponse toResponse(Permission entity);

    List<PermissionResponse> toResponseList(List<Permission> entities);

    Set<PermissionResponse> toResponseSet(Set<Permission> entities);
}
