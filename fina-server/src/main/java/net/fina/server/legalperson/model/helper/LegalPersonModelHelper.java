package net.fina.server.legalperson.model.helper;

import net.fina.server.fi.model.RegionMetaModel;
import net.fina.server.fi.model.RegionMetaModelHelper;
import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.FiLegalPersonConnection;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.person.model.LegalPersonResidentStatus;
import net.fina.server.person.model.helper.CriminalRecordModelHelper;
import net.fina.server.person.model.helper.PersonPositionModelHelper;

import java.util.List;
import java.util.stream.Collectors;

public class LegalPersonModelHelper {

    public static LegalPersonMetaModel toModelSimple(LegalPerson legalPerson, long langId) {
        LegalPersonMetaModel result = new LegalPersonMetaModel();
        result.setId(legalPerson.getId());
        result.setIdentificationNumber(legalPerson.getIdentificationNumber());
        result.setName(legalPerson.getName() != null ? legalPerson.getName().getDescription(langId) : null);
        result.setNameStrId(legalPerson.getName() != null ? legalPerson.getName().getNameStrId() : 0);
        result.setResidentStatus(legalPerson.getResidentStatus());
        result.setStatus(legalPerson.getStatus());

        if (legalPerson.getMetaInfo() != null) {
            result.setMetaInfo(LegalPersonMetaInfoModelHelper.toModel(legalPerson.getMetaInfo()));
        }
        if (legalPerson.getCountry() != null) {
            result.setCountry(new RegionMetaModel(legalPerson.getCountry().getId(), legalPerson.getCountry().getCode(), legalPerson.getCountry().getDescription().getDescription(langId)));
        }

        result.setBank(legalPerson.getFiId() > 0);
        result.setFiId(legalPerson.getFiId());
        result.setRegistrationNumber(legalPerson.getRegistrationNumber());
        result.setConnectionTypes(legalPerson.getConnections().stream().map(FiLegalPersonConnection::getConnectionType).collect(Collectors.toSet()));
        return result;
    }

    public static LegalPersonMetaModel toModel(LegalPerson legalPerson, long langId) {
        LegalPersonMetaModel result = new LegalPersonMetaModel();
        result.setId(legalPerson.getId());
        result.setIdentificationNumber(legalPerson.getIdentificationNumber());
        result.setName(legalPerson.getName().getDescription(langId));
        result.setNameStrId(legalPerson.getName().getNameStrId());
        result.setResidentStatus(legalPerson.getResidentStatus());
        result.setStatus(legalPerson.getStatus());
        if (legalPerson.getCountry() != null) {
            result.setCountry(RegionMetaModelHelper.toModel(legalPerson.getCountry(), langId));
        }
        if (legalPerson.getMetaInfo() != null) {
            result.setMetaInfo(LegalPersonMetaInfoModelHelper.toModel(legalPerson.getMetaInfo()));
        }
        result.setBank(legalPerson.getFiId() > 0);
        result.setFiId(legalPerson.getFiId());
        result.setRegistrationNumber(legalPerson.getRegistrationNumber());
        result.setConnectionTypes(legalPerson.getConnections().stream().map(FiLegalPersonConnection::getConnectionType).collect(Collectors.toSet()));

        if (legalPerson.getContactInfo() != null) {
            result.setContactInfo(LegalPersonContactInfoModelHelper.toModel(legalPerson.getContactInfo(), langId));
        }
        result.setBeneficiaries(BeneficiaryModelHelper.toModels(legalPerson.getBeneficiaries(), langId));
        result.setCriminalRecords(CriminalRecordModelHelper.toModels(legalPerson.getCriminalRecords(), langId));
        result.setManagers(PersonPositionModelHelper.toModels(legalPerson.getPositions(), langId));
        result.setFiLegalPersonId(legalPerson.getFiLegalPersonId());

        return result;
    }

    public static LegalPerson toEntity(LegalPersonMetaModel legalPerson, long langId) {
        LegalPerson result = new LegalPerson();
        result.setId(legalPerson.getId());
        result.setIdentificationNumber(legalPerson.getIdentificationNumber());
        result.setName(new Description(langId, legalPerson.getNameStrId(), legalPerson.getName()));
        result.setResidentStatus(legalPerson.getResidentStatus() != null ? legalPerson.getResidentStatus() : LegalPersonResidentStatus.LEGAL_ENTITY_RESIDENT);
        result.setStatus(legalPerson.getStatus());

        if (legalPerson.getCountry() != null) {
            result.setCountry(RegionMetaModelHelper.toEntity(legalPerson.getCountry()));
        }
        if (legalPerson.getMetaInfo() != null) {
            result.setMetaInfo(LegalPersonMetaInfoModelHelper.toEntity(legalPerson.getMetaInfo()));
        }
        result.setFiId(legalPerson.getFiId());
        result.setRegistrationNumber(legalPerson.getRegistrationNumber());
        if (legalPerson.getContactInfo() != null) {
            result.setContactInfo(LegalPersonContactInfoModelHelper.toEntity(legalPerson.getContactInfo()));
        }
        result.setBeneficiaries(BeneficiaryModelHelper.toEntities(legalPerson.getBeneficiaries(), langId));
        result.setCriminalRecords(CriminalRecordModelHelper.toEntities(legalPerson.getCriminalRecords(), langId));
        result.setFiId(legalPerson.getFiId());
        result.setPositions(PersonPositionModelHelper.toEntities(legalPerson.getManagers(), langId));
        result.setFiLegalPersonId(legalPerson.getFiLegalPersonId());

        return result;
    }


    public static List<LegalPersonMetaModel> toModels(List<LegalPerson> legalPersonList, long langId) {
        return legalPersonList.stream().map(lp -> toModel(lp, langId)).collect(Collectors.toList());
    }

    public static List<LegalPersonMetaModel> toModelsSimple(List<LegalPerson> legalPersonList, long langId) {
        return legalPersonList.stream().map(lp -> toModelSimple(lp, langId)).collect(Collectors.toList());
    }
}
