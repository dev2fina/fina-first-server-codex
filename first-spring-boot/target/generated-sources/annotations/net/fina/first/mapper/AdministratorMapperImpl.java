package net.fina.first.mapper;

import javax.annotation.processing.Generated;
import net.fina.first.dto.request.AdministratorRequest;
import net.fina.first.dto.response.AdministratorResponse;
import net.fina.first.model.Administrator;
import net.fina.first.model.FiRegistry;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:38+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class AdministratorMapperImpl implements AdministratorMapper {

    @Override
    public Administrator toEntity(AdministratorRequest request) {
        if ( request == null ) {
            return null;
        }

        Administrator.AdministratorBuilder<?, ?> administrator = Administrator.builder();

        administrator.firstName( request.getFirstName() );
        administrator.lastName( request.getLastName() );
        administrator.middleName( request.getMiddleName() );
        administrator.identificationNumber( request.getIdentificationNumber() );
        administrator.citizenship( request.getCitizenship() );
        administrator.dateOfBirth( request.getDateOfBirth() );
        administrator.phone( request.getPhone() );
        administrator.email( request.getEmail() );
        administrator.address( request.getAddress() );
        administrator.position( request.getPosition() );
        administrator.positionType( request.getPositionType() );
        administrator.appointmentDate( request.getAppointmentDate() );
        administrator.terminationDate( request.getTerminationDate() );
        administrator.documentType( request.getDocumentType() );
        administrator.documentNumber( request.getDocumentNumber() );
        administrator.documentIssueDate( request.getDocumentIssueDate() );
        administrator.documentIssuer( request.getDocumentIssuer() );
        administrator.nonResident( request.isNonResident() );
        administrator.nonResidentDocumentType( request.getNonResidentDocumentType() );
        administrator.nonResidentDocumentNumber( request.getNonResidentDocumentNumber() );
        administrator.active( request.isActive() );

        return administrator.build();
    }

    @Override
    public void updateEntity(AdministratorRequest request, Administrator entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getFirstName() != null ) {
            entity.setFirstName( request.getFirstName() );
        }
        if ( request.getLastName() != null ) {
            entity.setLastName( request.getLastName() );
        }
        if ( request.getMiddleName() != null ) {
            entity.setMiddleName( request.getMiddleName() );
        }
        if ( request.getIdentificationNumber() != null ) {
            entity.setIdentificationNumber( request.getIdentificationNumber() );
        }
        if ( request.getCitizenship() != null ) {
            entity.setCitizenship( request.getCitizenship() );
        }
        if ( request.getDateOfBirth() != null ) {
            entity.setDateOfBirth( request.getDateOfBirth() );
        }
        if ( request.getPhone() != null ) {
            entity.setPhone( request.getPhone() );
        }
        if ( request.getEmail() != null ) {
            entity.setEmail( request.getEmail() );
        }
        if ( request.getAddress() != null ) {
            entity.setAddress( request.getAddress() );
        }
        if ( request.getPosition() != null ) {
            entity.setPosition( request.getPosition() );
        }
        if ( request.getPositionType() != null ) {
            entity.setPositionType( request.getPositionType() );
        }
        if ( request.getAppointmentDate() != null ) {
            entity.setAppointmentDate( request.getAppointmentDate() );
        }
        if ( request.getTerminationDate() != null ) {
            entity.setTerminationDate( request.getTerminationDate() );
        }
        if ( request.getDocumentType() != null ) {
            entity.setDocumentType( request.getDocumentType() );
        }
        if ( request.getDocumentNumber() != null ) {
            entity.setDocumentNumber( request.getDocumentNumber() );
        }
        if ( request.getDocumentIssueDate() != null ) {
            entity.setDocumentIssueDate( request.getDocumentIssueDate() );
        }
        if ( request.getDocumentIssuer() != null ) {
            entity.setDocumentIssuer( request.getDocumentIssuer() );
        }
        entity.setNonResident( request.isNonResident() );
        if ( request.getNonResidentDocumentType() != null ) {
            entity.setNonResidentDocumentType( request.getNonResidentDocumentType() );
        }
        if ( request.getNonResidentDocumentNumber() != null ) {
            entity.setNonResidentDocumentNumber( request.getNonResidentDocumentNumber() );
        }
        entity.setActive( request.isActive() );
    }

    @Override
    public AdministratorResponse toResponse(Administrator entity) {
        if ( entity == null ) {
            return null;
        }

        AdministratorResponse.AdministratorResponseBuilder administratorResponse = AdministratorResponse.builder();

        administratorResponse.fiRegistryId( entityFiRegistryId( entity ) );
        administratorResponse.id( entity.getId() );
        administratorResponse.firstName( entity.getFirstName() );
        administratorResponse.lastName( entity.getLastName() );
        administratorResponse.middleName( entity.getMiddleName() );
        administratorResponse.identificationNumber( entity.getIdentificationNumber() );
        administratorResponse.citizenship( entity.getCitizenship() );
        administratorResponse.dateOfBirth( entity.getDateOfBirth() );
        administratorResponse.phone( entity.getPhone() );
        administratorResponse.email( entity.getEmail() );
        administratorResponse.address( entity.getAddress() );
        administratorResponse.position( entity.getPosition() );
        administratorResponse.positionType( entity.getPositionType() );
        administratorResponse.appointmentDate( entity.getAppointmentDate() );
        administratorResponse.terminationDate( entity.getTerminationDate() );
        administratorResponse.documentType( entity.getDocumentType() );
        administratorResponse.documentNumber( entity.getDocumentNumber() );
        administratorResponse.documentIssueDate( entity.getDocumentIssueDate() );
        administratorResponse.documentIssuer( entity.getDocumentIssuer() );
        administratorResponse.nonResident( entity.isNonResident() );
        administratorResponse.active( entity.isActive() );
        administratorResponse.createdAt( entity.getCreatedAt() );
        administratorResponse.updatedAt( entity.getUpdatedAt() );

        administratorResponse.fullName( entity.getFullName() );

        return administratorResponse.build();
    }

    private Long entityFiRegistryId(Administrator administrator) {
        if ( administrator == null ) {
            return null;
        }
        FiRegistry fiRegistry = administrator.getFiRegistry();
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
