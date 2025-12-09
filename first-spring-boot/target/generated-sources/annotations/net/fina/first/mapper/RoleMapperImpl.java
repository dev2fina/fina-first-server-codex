package net.fina.first.mapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import net.fina.first.dto.response.RoleResponse;
import net.fina.first.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:37+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public RoleResponse toResponse(Role entity) {
        if ( entity == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.id( entity.getId() );
        roleResponse.code( entity.getCode() );
        roleResponse.name( entity.getName() );
        roleResponse.description( entity.getDescription() );
        roleResponse.system( entity.isSystem() );
        roleResponse.active( entity.isActive() );
        roleResponse.permissions( permissionMapper.toResponseSet( entity.getPermissions() ) );

        return roleResponse.build();
    }

    @Override
    public List<RoleResponse> toResponseList(List<Role> entities) {
        if ( entities == null ) {
            return null;
        }

        List<RoleResponse> list = new ArrayList<RoleResponse>( entities.size() );
        for ( Role role : entities ) {
            list.add( toResponse( role ) );
        }

        return list;
    }

    @Override
    public Set<RoleResponse> toResponseSet(Set<Role> entities) {
        if ( entities == null ) {
            return null;
        }

        Set<RoleResponse> set = new LinkedHashSet<RoleResponse>( Math.max( (int) ( entities.size() / .75f ) + 1, 16 ) );
        for ( Role role : entities ) {
            set.add( toResponse( role ) );
        }

        return set;
    }
}
