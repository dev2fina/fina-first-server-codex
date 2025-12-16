package net.fina.first.mapper;

import net.fina.first.dto.RoleDto;
import net.fina.first.dto.UserDto;
import net.fina.first.model.Role;
import net.fina.first.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserDto dto);

    UserDto toDto(User entity);

    RoleDto toRoleDto(Role role);
}
