package net.fina.first.mapper;

import javax.annotation.processing.Generated;
import net.fina.first.dto.request.BranchRequest;
import net.fina.first.dto.response.BranchResponse;
import net.fina.first.model.Branch;
import net.fina.first.model.FiRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:39+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class BranchMapperImpl implements BranchMapper {

    @Autowired
    private RegionMapper regionMapper;

    @Override
    public Branch toEntity(BranchRequest request) {
        if ( request == null ) {
            return null;
        }

        Branch.BranchBuilder<?, ?> branch = Branch.builder();

        branch.code( request.getCode() );
        branch.name( request.getName() );
        branch.branchType( request.getBranchType() );
        branch.status( request.getStatus() );
        branch.city( request.getCity() );
        branch.address( request.getAddress() );
        branch.phone( request.getPhone() );
        branch.email( request.getEmail() );
        branch.delegationPersonName( request.getDelegationPersonName() );
        branch.delegationPersonIdentificationNumber( request.getDelegationPersonIdentificationNumber() );
        branch.registrationDate( request.getRegistrationDate() );
        branch.cancellationDate( request.getCancellationDate() );
        branch.legalActNumber( request.getLegalActNumber() );
        branch.legalActDate( request.getLegalActDate() );
        branch.sequence( request.getSequence() );

        return branch.build();
    }

    @Override
    public void updateEntity(BranchRequest request, Branch entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getCode() != null ) {
            entity.setCode( request.getCode() );
        }
        if ( request.getName() != null ) {
            entity.setName( request.getName() );
        }
        if ( request.getBranchType() != null ) {
            entity.setBranchType( request.getBranchType() );
        }
        if ( request.getStatus() != null ) {
            entity.setStatus( request.getStatus() );
        }
        if ( request.getCity() != null ) {
            entity.setCity( request.getCity() );
        }
        if ( request.getAddress() != null ) {
            entity.setAddress( request.getAddress() );
        }
        if ( request.getPhone() != null ) {
            entity.setPhone( request.getPhone() );
        }
        if ( request.getEmail() != null ) {
            entity.setEmail( request.getEmail() );
        }
        if ( request.getDelegationPersonName() != null ) {
            entity.setDelegationPersonName( request.getDelegationPersonName() );
        }
        if ( request.getDelegationPersonIdentificationNumber() != null ) {
            entity.setDelegationPersonIdentificationNumber( request.getDelegationPersonIdentificationNumber() );
        }
        if ( request.getRegistrationDate() != null ) {
            entity.setRegistrationDate( request.getRegistrationDate() );
        }
        if ( request.getCancellationDate() != null ) {
            entity.setCancellationDate( request.getCancellationDate() );
        }
        if ( request.getLegalActNumber() != null ) {
            entity.setLegalActNumber( request.getLegalActNumber() );
        }
        if ( request.getLegalActDate() != null ) {
            entity.setLegalActDate( request.getLegalActDate() );
        }
        if ( request.getSequence() != null ) {
            entity.setSequence( request.getSequence() );
        }
    }

    @Override
    public BranchResponse toResponse(Branch entity) {
        if ( entity == null ) {
            return null;
        }

        BranchResponse.BranchResponseBuilder branchResponse = BranchResponse.builder();

        branchResponse.fiRegistryId( entityFiRegistryId( entity ) );
        branchResponse.region( regionMapper.toResponse( entity.getRegion() ) );
        branchResponse.id( entity.getId() );
        branchResponse.code( entity.getCode() );
        branchResponse.name( entity.getName() );
        branchResponse.branchType( entity.getBranchType() );
        branchResponse.status( entity.getStatus() );
        branchResponse.city( entity.getCity() );
        branchResponse.address( entity.getAddress() );
        branchResponse.phone( entity.getPhone() );
        branchResponse.email( entity.getEmail() );
        branchResponse.delegationPersonName( entity.getDelegationPersonName() );
        branchResponse.delegationPersonIdentificationNumber( entity.getDelegationPersonIdentificationNumber() );
        branchResponse.registrationDate( entity.getRegistrationDate() );
        branchResponse.cancellationDate( entity.getCancellationDate() );
        branchResponse.legalActNumber( entity.getLegalActNumber() );
        branchResponse.legalActDate( entity.getLegalActDate() );
        branchResponse.sequence( entity.getSequence() );
        branchResponse.createdAt( entity.getCreatedAt() );
        branchResponse.updatedAt( entity.getUpdatedAt() );

        return branchResponse.build();
    }

    private Long entityFiRegistryId(Branch branch) {
        if ( branch == null ) {
            return null;
        }
        FiRegistry fiRegistry = branch.getFiRegistry();
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
