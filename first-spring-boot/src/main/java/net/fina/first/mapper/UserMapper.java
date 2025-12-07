package net.fina.first.mapper;

import net.fina.first.dto.response.UserResponse;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for User entity.
 */
@Mapper(
        componentModel = "spring",
        uses = {RoleMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(entity.getFullName())")
    @Mapping(target = "assignedFiRegistryIds", source = "assignedFiRegistries", qualifiedByName = "fiRegistriesToIds")
    UserResponse toResponse(User entity);

    List<UserResponse> toResponseList(List<User> entities);

    @Named("fiRegistriesToIds")
    default Set<Long> fiRegistriesToIds(Set<FiRegistry> fiRegistries) {
        if (fiRegistries == null) {
            return null;
        }
        return fiRegistries.stream()
                .map(FiRegistry::getId)
                .collect(Collectors.toSet());
    }
}
