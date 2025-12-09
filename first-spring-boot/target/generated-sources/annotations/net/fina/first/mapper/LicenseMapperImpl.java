package net.fina.first.mapper;

import javax.annotation.processing.Generated;
import net.fina.first.dto.request.LicenseRequest;
import net.fina.first.dto.response.LicenseResponse;
import net.fina.first.model.FiRegistry;
import net.fina.first.model.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:38+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class LicenseMapperImpl implements LicenseMapper {

    @Autowired
    private LicenseTypeMapper licenseTypeMapper;

    @Override
    public License toEntity(LicenseRequest request) {
        if ( request == null ) {
            return null;
        }

        License.LicenseBuilder<?, ?> license = License.builder();

        license.licenseNumber( request.getLicenseNumber() );
        license.status( request.getStatus() );
        license.issuanceDate( request.getIssuanceDate() );
        license.effectiveDate( request.getEffectiveDate() );
        license.expirationDate( request.getExpirationDate() );
        license.legalActNumber( request.getLegalActNumber() );
        license.legalActDate( request.getLegalActDate() );
        license.scope( request.getScope() );
        license.conditions( request.getConditions() );
        license.restrictions( request.getRestrictions() );
        license.notes( request.getNotes() );

        return license.build();
    }

    @Override
    public void updateEntity(LicenseRequest request, License entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getLicenseNumber() != null ) {
            entity.setLicenseNumber( request.getLicenseNumber() );
        }
        if ( request.getStatus() != null ) {
            entity.setStatus( request.getStatus() );
        }
        if ( request.getIssuanceDate() != null ) {
            entity.setIssuanceDate( request.getIssuanceDate() );
        }
        if ( request.getEffectiveDate() != null ) {
            entity.setEffectiveDate( request.getEffectiveDate() );
        }
        if ( request.getExpirationDate() != null ) {
            entity.setExpirationDate( request.getExpirationDate() );
        }
        if ( request.getLegalActNumber() != null ) {
            entity.setLegalActNumber( request.getLegalActNumber() );
        }
        if ( request.getLegalActDate() != null ) {
            entity.setLegalActDate( request.getLegalActDate() );
        }
        if ( request.getScope() != null ) {
            entity.setScope( request.getScope() );
        }
        if ( request.getConditions() != null ) {
            entity.setConditions( request.getConditions() );
        }
        if ( request.getRestrictions() != null ) {
            entity.setRestrictions( request.getRestrictions() );
        }
        if ( request.getNotes() != null ) {
            entity.setNotes( request.getNotes() );
        }
    }

    @Override
    public LicenseResponse toResponse(License entity) {
        if ( entity == null ) {
            return null;
        }

        LicenseResponse.LicenseResponseBuilder licenseResponse = LicenseResponse.builder();

        licenseResponse.fiRegistryId( entityFiRegistryId( entity ) );
        licenseResponse.id( entity.getId() );
        licenseResponse.licenseNumber( entity.getLicenseNumber() );
        licenseResponse.licenseType( licenseTypeMapper.toResponse( entity.getLicenseType() ) );
        licenseResponse.status( entity.getStatus() );
        licenseResponse.issuanceDate( entity.getIssuanceDate() );
        licenseResponse.effectiveDate( entity.getEffectiveDate() );
        licenseResponse.expirationDate( entity.getExpirationDate() );
        licenseResponse.suspensionDate( entity.getSuspensionDate() );
        licenseResponse.revocationDate( entity.getRevocationDate() );
        licenseResponse.legalActNumber( entity.getLegalActNumber() );
        licenseResponse.legalActDate( entity.getLegalActDate() );
        licenseResponse.scope( entity.getScope() );
        licenseResponse.conditions( entity.getConditions() );
        licenseResponse.restrictions( entity.getRestrictions() );
        licenseResponse.suspensionReason( entity.getSuspensionReason() );
        licenseResponse.revocationReason( entity.getRevocationReason() );
        licenseResponse.notes( entity.getNotes() );
        licenseResponse.createdAt( entity.getCreatedAt() );
        licenseResponse.updatedAt( entity.getUpdatedAt() );

        licenseResponse.valid( entity.isValid() );

        return licenseResponse.build();
    }

    private Long entityFiRegistryId(License license) {
        if ( license == null ) {
            return null;
        }
        FiRegistry fiRegistry = license.getFiRegistry();
        if ( fiRegistry == null ) {
            return null;
        }
        Long id = fiRegistry.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
