package net.fina.first.mapper;

import net.fina.first.dto.response.RoleResponse;
import net.fina.first.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Role entity.
 */
@Mapper(
        componentModel = "spring",
        uses = {PermissionMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoleMapper {

    RoleResponse toResponse(Role entity);

    List<RoleResponse> toResponseList(List<Role> entities);

    Set<RoleResponse> toResponseSet(Set<Role> entities);
}
