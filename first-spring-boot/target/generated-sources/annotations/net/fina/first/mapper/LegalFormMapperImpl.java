package net.fina.first.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import net.fina.first.dto.response.LegalFormResponse;
import net.fina.first.model.LegalForm;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:38+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class LegalFormMapperImpl implements LegalFormMapper {

    @Override
    public LegalFormResponse toResponse(LegalForm entity) {
        if ( entity == null ) {
            return null;
        }

        LegalFormResponse.LegalFormResponseBuilder legalFormResponse = LegalFormResponse.builder();

        legalFormResponse.id( entity.getId() );
        legalFormResponse.code( entity.getCode() );
        legalFormResponse.name( entity.getName() );
        legalFormResponse.nameLocal( entity.getNameLocal() );
        legalFormResponse.description( entity.getDescription() );
        legalFormResponse.active( entity.isActive() );

        return legalFormResponse.build();
    }

    @Override
    public List<LegalFormResponse> toResponseList(List<LegalForm> entities) {
        if ( entities == null ) {
            return null;
        }

        List<LegalFormResponse> list = new ArrayList<LegalFormResponse>( entities.size() );
        for ( LegalForm legalForm : entities ) {
            list.add( toResponse( legalForm ) );
        }

        return list;
    }
}
