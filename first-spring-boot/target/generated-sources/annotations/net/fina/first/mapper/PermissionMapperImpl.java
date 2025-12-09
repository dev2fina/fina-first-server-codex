package net.fina.first.mapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import net.fina.first.dto.response.PermissionResponse;
import net.fina.first.model.Permission;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:37+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class PermissionMapperImpl implements PermissionMapper {

    @Override
    public PermissionResponse toResponse(Permission entity) {
        if ( entity == null ) {
            return null;
        }

        PermissionResponse.PermissionResponseBuilder permissionResponse = PermissionResponse.builder();

        permissionResponse.id( entity.getId() );
        permissionResponse.code( entity.getCode() );
        permissionResponse.name( entity.getName() );
        permissionResponse.description( entity.getDescription() );
        permissionResponse.module( entity.getModule() );
        permissionResponse.active( entity.isActive() );

        return permissionResponse.build();
    }

    @Override
    public List<PermissionResponse> toResponseList(List<Permission> entities) {
        if ( entities == null ) {
            return null;
        }

        List<PermissionResponse> list = new ArrayList<PermissionResponse>( entities.size() );
        for ( Permission permission : entities ) {
            list.add( toResponse( permission ) );
        }

        return list;
    }

    @Override
    public Set<PermissionResponse> toResponseSet(Set<Permission> entities) {
        if ( entities == null ) {
            return null;
        }

        Set<PermissionResponse> set = new LinkedHashSet<PermissionResponse>( Math.max( (int) ( entities.size() / .75f ) + 1, 16 ) );
        for ( Permission permission : entities ) {
            set.add( toResponse( permission ) );
        }

        return set;
    }
}
