package net.fina.first.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import net.fina.first.dto.response.UserResponse;
import net.fina.first.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:38+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserResponse toResponse(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.assignedFiRegistryIds( fiRegistriesToIds( entity.getAssignedFiRegistries() ) );
        userResponse.id( entity.getId() );
        userResponse.login( entity.getLogin() );
        userResponse.firstName( entity.getFirstName() );
        userResponse.lastName( entity.getLastName() );
        userResponse.email( entity.getEmail() );
        userResponse.phone( entity.getPhone() );
        userResponse.title( entity.getTitle() );
        userResponse.department( entity.getDepartment() );
        userResponse.blocked( entity.isBlocked() );
        userResponse.disabled( entity.isDisabled() );
        userResponse.changePassword( entity.isChangePassword() );
        userResponse.lastLoginDate( entity.getLastLoginDate() );
        userResponse.lastPasswordChangeDate( entity.getLastPasswordChangeDate() );
        userResponse.roles( roleMapper.toResponseSet( entity.getRoles() ) );
        userResponse.createdAt( entity.getCreatedAt() );
        userResponse.updatedAt( entity.getUpdatedAt() );

        userResponse.fullName( entity.getFullName() );

        return userResponse.build();
    }

    @Override
    public List<UserResponse> toResponseList(List<User> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UserResponse> list = new ArrayList<UserResponse>( entities.size() );
        for ( User user : entities ) {
            list.add( toResponse( user ) );
        }

        return list;
    }
}
