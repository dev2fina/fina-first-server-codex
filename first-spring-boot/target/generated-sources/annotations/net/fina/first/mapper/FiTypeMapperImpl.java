package net.fina.first.mapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import net.fina.first.dto.response.FiTypeResponse;
import net.fina.first.model.FiType;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:37+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class FiTypeMapperImpl implements FiTypeMapper {

    @Override
    public FiTypeResponse toResponse(FiType entity) {
        if ( entity == null ) {
            return null;
        }

        FiTypeResponse.FiTypeResponseBuilder fiTypeResponse = FiTypeResponse.builder();

        fiTypeResponse.id( entity.getId() );
        fiTypeResponse.code( entity.getCode() );
        fiTypeResponse.name( entity.getName() );
        fiTypeResponse.nameLocal( entity.getNameLocal() );
        fiTypeResponse.description( entity.getDescription() );
        fiTypeResponse.hasBranches( entity.isHasBranches() );
        fiTypeResponse.hasAdministrators( entity.isHasAdministrators() );
        fiTypeResponse.hasBeneficiaries( entity.isHasBeneficiaries() );
        fiTypeResponse.hasLicenses( entity.isHasLicenses() );
        Set<String> set = entity.getBranchTypes();
        if ( set != null ) {
            fiTypeResponse.branchTypes( new LinkedHashSet<String>( set ) );
        }
        fiTypeResponse.active( entity.isActive() );

        return fiTypeResponse.build();
    }

    @Override
    public List<FiTypeResponse> toResponseList(List<FiType> entities) {
        if ( entities == null ) {
            return null;
        }

        List<FiTypeResponse> list = new ArrayList<FiTypeResponse>( entities.size() );
        for ( FiType fiType : entities ) {
            list.add( toResponse( fiType ) );
        }

        return list;
    }
}
