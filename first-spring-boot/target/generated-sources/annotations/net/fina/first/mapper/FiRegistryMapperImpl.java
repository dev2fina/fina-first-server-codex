package net.fina.first.mapper;

import javax.annotation.processing.Generated;
import net.fina.first.dto.request.FiRegistryCreateRequest;
import net.fina.first.dto.request.FiRegistryUpdateRequest;
import net.fina.first.dto.response.FiRegistryResponse;
import net.fina.first.model.FiRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:39+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class FiRegistryMapperImpl implements FiRegistryMapper {

    @Autowired
    private FiTypeMapper fiTypeMapper;
    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private LegalFormMapper legalFormMapper;

    @Override
    public FiRegistry toEntity(FiRegistryCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        FiRegistry.FiRegistryBuilder<?, ?> fiRegistry = FiRegistry.builder();

        fiRegistry.applicationNumber( request.getApplicationNumber() );
        fiRegistry.applicationReceivedDate( request.getApplicationReceivedDate() );
        fiRegistry.applicationRegistrationDeadline( request.getApplicationRegistrationDeadline() );
        fiRegistry.licenseType( request.getLicenseType() );
        fiRegistry.firmName( request.getFirmName() );
        fiRegistry.tradeName( request.getTradeName() );
        fiRegistry.capital( request.getCapital() );
        fiRegistry.legalAddressCity( request.getLegalAddressCity() );
        fiRegistry.legalAddress( request.getLegalAddress() );
        fiRegistry.actualAddressSameAsLegal( request.isActualAddressSameAsLegal() );
        fiRegistry.actualOrHeadOfficeAddress( request.getActualOrHeadOfficeAddress() );
        fiRegistry.phone( request.getPhone() );
        fiRegistry.email( request.getEmail() );
        fiRegistry.website( request.getWebsite() );
        fiRegistry.contactPersonFullName( request.getContactPersonFullName() );
        fiRegistry.contactPersonIdentificationNumber( request.getContactPersonIdentificationNumber() );
        fiRegistry.contactPersonPhone( request.getContactPersonPhone() );
        fiRegistry.contactPersonEmail( request.getContactPersonEmail() );
        fiRegistry.foreignBankBranchOrSubsidiary( request.getForeignBankBranchOrSubsidiary() );
        fiRegistry.significantServiceProvider( request.getSignificantServiceProvider() );
        fiRegistry.typeOfActivity( request.getTypeOfActivity() );
        fiRegistry.binder( request.getBinder() );

        return fiRegistry.build();
    }

    @Override
    public void updateEntity(FiRegistryUpdateRequest request, FiRegistry entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getRegistrationNumber() != null ) {
            entity.setRegistrationNumber( request.getRegistrationNumber() );
        }
        if ( request.getRegistrationDate() != null ) {
            entity.setRegistrationDate( request.getRegistrationDate() );
        }
        if ( request.getLegalActNumber() != null ) {
            entity.setLegalActNumber( request.getLegalActNumber() );
        }
        if ( request.getLegalActDate() != null ) {
            entity.setLegalActDate( request.getLegalActDate() );
        }
        if ( request.getFormalComplianceConfirmationDate() != null ) {
            entity.setFormalComplianceConfirmationDate( request.getFormalComplianceConfirmationDate() );
        }
        if ( request.getFormalSuitabilityConfirmationLetterNumber() != null ) {
            entity.setFormalSuitabilityConfirmationLetterNumber( request.getFormalSuitabilityConfirmationLetterNumber() );
        }
        if ( request.getLicensingDeadline() != null ) {
            entity.setLicensingDeadline( request.getLicensingDeadline() );
        }
        if ( request.getLicensingNumber() != null ) {
            entity.setLicensingNumber( request.getLicensingNumber() );
        }
        if ( request.getLicenseIssuanceDate() != null ) {
            entity.setLicenseIssuanceDate( request.getLicenseIssuanceDate() );
        }
        if ( request.getLicenseType() != null ) {
            entity.setLicenseType( request.getLicenseType() );
        }
        if ( request.getFirmName() != null ) {
            entity.setFirmName( request.getFirmName() );
        }
        if ( request.getTradeName() != null ) {
            entity.setTradeName( request.getTradeName() );
        }
        if ( request.getCapital() != null ) {
            entity.setCapital( request.getCapital() );
        }
        if ( request.getLegalAddressCity() != null ) {
            entity.setLegalAddressCity( request.getLegalAddressCity() );
        }
        if ( request.getLegalAddress() != null ) {
            entity.setLegalAddress( request.getLegalAddress() );
        }
        if ( request.getActualAddressSameAsLegal() != null ) {
            entity.setActualAddressSameAsLegal( request.getActualAddressSameAsLegal() );
        }
        if ( request.getActualOrHeadOfficeAddress() != null ) {
            entity.setActualOrHeadOfficeAddress( request.getActualOrHeadOfficeAddress() );
        }
        if ( request.getPhone() != null ) {
            entity.setPhone( request.getPhone() );
        }
        if ( request.getEmail() != null ) {
            entity.setEmail( request.getEmail() );
        }
        if ( request.getWebsite() != null ) {
            entity.setWebsite( request.getWebsite() );
        }
        if ( request.getContactPersonFullName() != null ) {
            entity.setContactPersonFullName( request.getContactPersonFullName() );
        }
        if ( request.getContactPersonIdentificationNumber() != null ) {
            entity.setContactPersonIdentificationNumber( request.getContactPersonIdentificationNumber() );
        }
        if ( request.getContactPersonPhone() != null ) {
            entity.setContactPersonPhone( request.getContactPersonPhone() );
        }
        if ( request.getContactPersonEmail() != null ) {
            entity.setContactPersonEmail( request.getContactPersonEmail() );
        }
        if ( request.getForeignBankBranchOrSubsidiary() != null ) {
            entity.setForeignBankBranchOrSubsidiary( request.getForeignBankBranchOrSubsidiary() );
        }
        if ( request.getForeignReliableBankBranchOrSubsidiary() != null ) {
            entity.setForeignReliableBankBranchOrSubsidiary( request.getForeignReliableBankBranchOrSubsidiary() );
        }
        if ( request.getSignificantServiceProvider() != null ) {
            entity.setSignificantServiceProvider( request.getSignificantServiceProvider() );
        }
        if ( request.getTypeOfActivity() != null ) {
            entity.setTypeOfActivity( request.getTypeOfActivity() );
        }
        if ( request.getServicingCommercialBanks() != null ) {
            entity.setServicingCommercialBanks( request.getServicingCommercialBanks() );
        }
        if ( request.getReferenceExchangeRate() != null ) {
            entity.setReferenceExchangeRate( request.getReferenceExchangeRate() );
        }
        if ( request.getCommissionFee() != null ) {
            entity.setCommissionFee( request.getCommissionFee() );
        }
        if ( request.getBinder() != null ) {
            entity.setBinder( request.getBinder() );
        }
    }

    @Override
    public FiRegistryResponse toResponse(FiRegistry entity) {
        if ( entity == null ) {
            return null;
        }

        FiRegistryResponse.FiRegistryResponseBuilder fiRegistryResponse = FiRegistryResponse.builder();

        fiRegistryResponse.legalAddressRegion( regionMapper.toResponse( entity.getLegalAddressRegion() ) );
        fiRegistryResponse.id( entity.getId() );
        fiRegistryResponse.identificationNumber( entity.getIdentificationNumber() );
        fiRegistryResponse.code( entity.getCode() );
        fiRegistryResponse.fiType( fiTypeMapper.toResponse( entity.getFiType() ) );
        fiRegistryResponse.status( entity.getStatus() );
        fiRegistryResponse.licenseStatus( entity.getLicenseStatus() );
        fiRegistryResponse.applicationNumber( entity.getApplicationNumber() );
        fiRegistryResponse.applicationReceivedDate( entity.getApplicationReceivedDate() );
        fiRegistryResponse.applicationRegistrationDeadline( entity.getApplicationRegistrationDeadline() );
        fiRegistryResponse.registrationNumber( entity.getRegistrationNumber() );
        fiRegistryResponse.registrationDate( entity.getRegistrationDate() );
        fiRegistryResponse.legalActNumber( entity.getLegalActNumber() );
        fiRegistryResponse.legalActDate( entity.getLegalActDate() );
        fiRegistryResponse.licensingDeadline( entity.getLicensingDeadline() );
        fiRegistryResponse.licensingNumber( entity.getLicensingNumber() );
        fiRegistryResponse.licenseIssuanceDate( entity.getLicenseIssuanceDate() );
        fiRegistryResponse.licenseType( entity.getLicenseType() );
        fiRegistryResponse.legalForm( legalFormMapper.toResponse( entity.getLegalForm() ) );
        fiRegistryResponse.firmName( entity.getFirmName() );
        fiRegistryResponse.tradeName( entity.getTradeName() );
        fiRegistryResponse.capital( entity.getCapital() );
        fiRegistryResponse.legalAddressCity( entity.getLegalAddressCity() );
        fiRegistryResponse.legalAddress( entity.getLegalAddress() );
        fiRegistryResponse.actualAddressSameAsLegal( entity.isActualAddressSameAsLegal() );
        fiRegistryResponse.actualOrHeadOfficeAddress( entity.getActualOrHeadOfficeAddress() );
        fiRegistryResponse.phone( entity.getPhone() );
        fiRegistryResponse.email( entity.getEmail() );
        fiRegistryResponse.website( entity.getWebsite() );
        fiRegistryResponse.contactPersonFullName( entity.getContactPersonFullName() );
        fiRegistryResponse.contactPersonIdentificationNumber( entity.getContactPersonIdentificationNumber() );
        fiRegistryResponse.contactPersonPhone( entity.getContactPersonPhone() );
        fiRegistryResponse.contactPersonEmail( entity.getContactPersonEmail() );
        fiRegistryResponse.foreignBankBranchOrSubsidiary( entity.getForeignBankBranchOrSubsidiary() );
        fiRegistryResponse.foreignReliableBankBranchOrSubsidiary( entity.getForeignReliableBankBranchOrSubsidiary() );
        fiRegistryResponse.significantServiceProvider( entity.getSignificantServiceProvider() );
        fiRegistryResponse.typeOfActivity( entity.getTypeOfActivity() );
        fiRegistryResponse.binder( entity.getBinder() );
        fiRegistryResponse.createdAt( entity.getCreatedAt() );
        fiRegistryResponse.createdBy( entity.getCreatedBy() );
        fiRegistryResponse.updatedAt( entity.getUpdatedAt() );
        fiRegistryResponse.updatedBy( entity.getUpdatedBy() );

        fiRegistryResponse.branchCount( entity.getBranches() != null ? entity.getBranches().size() : 0 );
        fiRegistryResponse.administratorCount( entity.getAdministrators() != null ? entity.getAdministrators().size() : 0 );
        fiRegistryResponse.beneficiaryCount( entity.getBeneficiaries() != null ? entity.getBeneficiaries().size() : 0 );
        fiRegistryResponse.licenseCount( entity.getLicenses() != null ? entity.getLicenses().size() : 0 );

        return fiRegistryResponse.build();
    }
}
