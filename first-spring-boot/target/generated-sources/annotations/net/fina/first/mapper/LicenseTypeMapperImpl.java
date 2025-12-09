package net.fina.first.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import net.fina.first.dto.response.LicenseTypeResponse;
import net.fina.first.model.LicenseType;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:37+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class LicenseTypeMapperImpl implements LicenseTypeMapper {

    @Override
    public LicenseTypeResponse toResponse(LicenseType entity) {
        if ( entity == null ) {
            return null;
        }

        LicenseTypeResponse.LicenseTypeResponseBuilder licenseTypeResponse = LicenseTypeResponse.builder();

        licenseTypeResponse.id( entity.getId() );
        licenseTypeResponse.code( entity.getCode() );
        licenseTypeResponse.name( entity.getName() );
        licenseTypeResponse.nameLocal( entity.getNameLocal() );
        licenseTypeResponse.description( entity.getDescription() );
        licenseTypeResponse.validityPeriodMonths( entity.getValidityPeriodMonths() );
        licenseTypeResponse.renewable( entity.isRenewable() );
        licenseTypeResponse.active( entity.isActive() );

        return licenseTypeResponse.build();
    }

    @Override
    public List<LicenseTypeResponse> toResponseList(List<LicenseType> entities) {
        if ( entities == null ) {
            return null;
        }

        List<LicenseTypeResponse> list = new ArrayList<LicenseTypeResponse>( entities.size() );
        for ( LicenseType licenseType : entities ) {
            list.add( toResponse( licenseType ) );
        }

        return list;
    }
}
