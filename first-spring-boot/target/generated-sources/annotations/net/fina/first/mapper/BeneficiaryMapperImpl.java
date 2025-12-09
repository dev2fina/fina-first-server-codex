package net.fina.first.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import net.fina.first.dto.request.BeneficiaryRequest;
import net.fina.first.dto.response.BeneficiaryResponse;
import net.fina.first.model.Beneficiary;
import net.fina.first.model.FiRegistry;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:37+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class BeneficiaryMapperImpl implements BeneficiaryMapper {

    @Override
    public Beneficiary toEntity(BeneficiaryRequest request) {
        if ( request == null ) {
            return null;
        }

        Beneficiary.BeneficiaryBuilder<?, ?> beneficiary = Beneficiary.builder();

        beneficiary.type( request.getType() );
        beneficiary.capitalPercentage( request.getCapitalPercentage() );
        beneficiary.votingRightsPercentage( request.getVotingRightsPercentage() );
        beneficiary.firstName( request.getFirstName() );
        beneficiary.lastName( request.getLastName() );
        beneficiary.middleName( request.getMiddleName() );
        beneficiary.identificationNumber( request.getIdentificationNumber() );
        beneficiary.citizenship( request.getCitizenship() );
        beneficiary.dateOfBirth( request.getDateOfBirth() );
        beneficiary.address( request.getAddress() );
        beneficiary.legalEntityName( request.getLegalEntityName() );
        beneficiary.legalEntityCode( request.getLegalEntityCode() );
        beneficiary.legalEntityType( request.getLegalEntityType() );
        beneficiary.legalEntityCountry( request.getLegalEntityCountry() );
        beneficiary.legalEntityRegistrationNumber( request.getLegalEntityRegistrationNumber() );
        beneficiary.legalEntityAddress( request.getLegalEntityAddress() );
        beneficiary.ultimateBeneficiary( request.isUltimateBeneficiary() );
        beneficiary.notes( request.getNotes() );

        beneficiary.active( true );

        return beneficiary.build();
    }

    @Override
    public void updateEntity(BeneficiaryRequest request, Beneficiary entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getType() != null ) {
            entity.setType( request.getType() );
        }
        if ( request.getCapitalPercentage() != null ) {
            entity.setCapitalPercentage( request.getCapitalPercentage() );
        }
        if ( request.getVotingRightsPercentage() != null ) {
            entity.setVotingRightsPercentage( request.getVotingRightsPercentage() );
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
        if ( request.getAddress() != null ) {
            entity.setAddress( request.getAddress() );
        }
        if ( request.getLegalEntityName() != null ) {
            entity.setLegalEntityName( request.getLegalEntityName() );
        }
        if ( request.getLegalEntityCode() != null ) {
            entity.setLegalEntityCode( request.getLegalEntityCode() );
        }
        if ( request.getLegalEntityType() != null ) {
            entity.setLegalEntityType( request.getLegalEntityType() );
        }
        if ( request.getLegalEntityCountry() != null ) {
            entity.setLegalEntityCountry( request.getLegalEntityCountry() );
        }
        if ( request.getLegalEntityRegistrationNumber() != null ) {
            entity.setLegalEntityRegistrationNumber( request.getLegalEntityRegistrationNumber() );
        }
        if ( request.getLegalEntityAddress() != null ) {
            entity.setLegalEntityAddress( request.getLegalEntityAddress() );
        }
        entity.setUltimateBeneficiary( request.isUltimateBeneficiary() );
        if ( request.getNotes() != null ) {
            entity.setNotes( request.getNotes() );
        }
    }

    @Override
    public BeneficiaryResponse toResponse(Beneficiary entity) {
        if ( entity == null ) {
            return null;
        }

        BeneficiaryResponse.BeneficiaryResponseBuilder beneficiaryResponse = BeneficiaryResponse.builder();

        beneficiaryResponse.fiRegistryId( entityFiRegistryId( entity ) );
        beneficiaryResponse.parentId( entityParentId( entity ) );
        beneficiaryResponse.id( entity.getId() );
        beneficiaryResponse.type( entity.getType() );
        beneficiaryResponse.capitalPercentage( entity.getCapitalPercentage() );
        beneficiaryResponse.votingRightsPercentage( entity.getVotingRightsPercentage() );
        beneficiaryResponse.firstName( entity.getFirstName() );
        beneficiaryResponse.lastName( entity.getLastName() );
        beneficiaryResponse.middleName( entity.getMiddleName() );
        beneficiaryResponse.identificationNumber( entity.getIdentificationNumber() );
        beneficiaryResponse.citizenship( entity.getCitizenship() );
        beneficiaryResponse.dateOfBirth( entity.getDateOfBirth() );
        beneficiaryResponse.address( entity.getAddress() );
        beneficiaryResponse.legalEntityName( entity.getLegalEntityName() );
        beneficiaryResponse.legalEntityCode( entity.getLegalEntityCode() );
        beneficiaryResponse.legalEntityType( entity.getLegalEntityType() );
        beneficiaryResponse.legalEntityCountry( entity.getLegalEntityCountry() );
        beneficiaryResponse.legalEntityRegistrationNumber( entity.getLegalEntityRegistrationNumber() );
        beneficiaryResponse.legalEntityAddress( entity.getLegalEntityAddress() );
        beneficiaryResponse.hierarchyLevel( entity.getHierarchyLevel() );
        beneficiaryResponse.ultimateBeneficiary( entity.isUltimateBeneficiary() );
        beneficiaryResponse.active( entity.isActive() );
        beneficiaryResponse.notes( entity.getNotes() );
        beneficiaryResponse.createdAt( entity.getCreatedAt() );
        beneficiaryResponse.updatedAt( entity.getUpdatedAt() );

        beneficiaryResponse.displayName( entity.getDisplayName() );

        return beneficiaryResponse.build();
    }

    @Override
    public BeneficiaryResponse toResponseWithChildren(Beneficiary entity) {
        if ( entity == null ) {
            return null;
        }

        BeneficiaryResponse.BeneficiaryResponseBuilder beneficiaryResponse = BeneficiaryResponse.builder();

        beneficiaryResponse.fiRegistryId( entityFiRegistryId( entity ) );
        beneficiaryResponse.parentId( entityParentId( entity ) );
        beneficiaryResponse.id( entity.getId() );
        beneficiaryResponse.type( entity.getType() );
        beneficiaryResponse.capitalPercentage( entity.getCapitalPercentage() );
        beneficiaryResponse.votingRightsPercentage( entity.getVotingRightsPercentage() );
        beneficiaryResponse.firstName( entity.getFirstName() );
        beneficiaryResponse.lastName( entity.getLastName() );
        beneficiaryResponse.middleName( entity.getMiddleName() );
        beneficiaryResponse.identificationNumber( entity.getIdentificationNumber() );
        beneficiaryResponse.citizenship( entity.getCitizenship() );
        beneficiaryResponse.dateOfBirth( entity.getDateOfBirth() );
        beneficiaryResponse.address( entity.getAddress() );
        beneficiaryResponse.legalEntityName( entity.getLegalEntityName() );
        beneficiaryResponse.legalEntityCode( entity.getLegalEntityCode() );
        beneficiaryResponse.legalEntityType( entity.getLegalEntityType() );
        beneficiaryResponse.legalEntityCountry( entity.getLegalEntityCountry() );
        beneficiaryResponse.legalEntityRegistrationNumber( entity.getLegalEntityRegistrationNumber() );
        beneficiaryResponse.legalEntityAddress( entity.getLegalEntityAddress() );
        beneficiaryResponse.hierarchyLevel( entity.getHierarchyLevel() );
        beneficiaryResponse.children( beneficiaryListToBeneficiaryResponseList( entity.getChildren() ) );
        beneficiaryResponse.ultimateBeneficiary( entity.isUltimateBeneficiary() );
        beneficiaryResponse.active( entity.isActive() );
        beneficiaryResponse.notes( entity.getNotes() );
        beneficiaryResponse.createdAt( entity.getCreatedAt() );
        beneficiaryResponse.updatedAt( entity.getUpdatedAt() );

        beneficiaryResponse.displayName( entity.getDisplayName() );

        return beneficiaryResponse.build();
    }

    private Long entityFiRegistryId(Beneficiary beneficiary) {
        if ( beneficiary == null ) {
            return null;
        }
        FiRegistry fiRegistry = beneficiary.getFiRegistry();
        if ( fiRegistry == null ) {
            return null;
        }
        Long id = fiRegistry.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityParentId(Beneficiary beneficiary) {
        if ( beneficiary == null ) {
            return null;
        }
        Beneficiary parent = beneficiary.getParent();
        if ( parent == null ) {
            return null;
        }
        Long id = parent.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<BeneficiaryResponse> beneficiaryListToBeneficiaryResponseList(List<Beneficiary> list) {
        if ( list == null ) {
            return null;
        }

        List<BeneficiaryResponse> list1 = new ArrayList<BeneficiaryResponse>( list.size() );
        for ( Beneficiary beneficiary : list ) {
            list1.add( beneficiaryToBeneficiaryResponse( beneficiary ) );
        }

        return list1;
    }

    protected BeneficiaryResponse beneficiaryToBeneficiaryResponse(Beneficiary beneficiary) {
        if ( beneficiary == null ) {
            return null;
        }

        BeneficiaryResponse.BeneficiaryResponseBuilder beneficiaryResponse = BeneficiaryResponse.builder();

        beneficiaryResponse.id( beneficiary.getId() );
        beneficiaryResponse.type( beneficiary.getType() );
        beneficiaryResponse.capitalPercentage( beneficiary.getCapitalPercentage() );
        beneficiaryResponse.votingRightsPercentage( beneficiary.getVotingRightsPercentage() );
        beneficiaryResponse.firstName( beneficiary.getFirstName() );
        beneficiaryResponse.lastName( beneficiary.getLastName() );
        beneficiaryResponse.middleName( beneficiary.getMiddleName() );
        beneficiaryResponse.identificationNumber( beneficiary.getIdentificationNumber() );
        beneficiaryResponse.citizenship( beneficiary.getCitizenship() );
        beneficiaryResponse.dateOfBirth( beneficiary.getDateOfBirth() );
        beneficiaryResponse.address( beneficiary.getAddress() );
        beneficiaryResponse.legalEntityName( beneficiary.getLegalEntityName() );
        beneficiaryResponse.legalEntityCode( beneficiary.getLegalEntityCode() );
        beneficiaryResponse.legalEntityType( beneficiary.getLegalEntityType() );
        beneficiaryResponse.legalEntityCountry( beneficiary.getLegalEntityCountry() );
        beneficiaryResponse.legalEntityRegistrationNumber( beneficiary.getLegalEntityRegistrationNumber() );
        beneficiaryResponse.legalEntityAddress( beneficiary.getLegalEntityAddress() );
        beneficiaryResponse.hierarchyLevel( beneficiary.getHierarchyLevel() );
        beneficiaryResponse.children( beneficiaryListToBeneficiaryResponseList( beneficiary.getChildren() ) );
        beneficiaryResponse.ultimateBeneficiary( beneficiary.isUltimateBeneficiary() );
        beneficiaryResponse.displayName( beneficiary.getDisplayName() );
        beneficiaryResponse.active( beneficiary.isActive() );
        beneficiaryResponse.notes( beneficiary.getNotes() );
        beneficiaryResponse.createdAt( beneficiary.getCreatedAt() );
        beneficiaryResponse.updatedAt( beneficiary.getUpdatedAt() );

        return beneficiaryResponse.build();
    }
}
