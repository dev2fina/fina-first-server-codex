package net.fina.server.legalperson.model.helper;

import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.legalperson.model.BeneficiaryMetaModel;
import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.person.model.helper.PersonModelHelper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BeneficiaryModelHelper {

    public static Beneficiary toEntity(BeneficiaryMetaModel beneficiary, long langId) {
        Beneficiary result = new Beneficiary();
        result.setId(beneficiary.getId());
        result.setShare(beneficiary.getShare());
        result.setActive(beneficiary.isActive());
        result.setCurrency(beneficiary.getCurrency());
        result.setNominal(beneficiary.getNominal());
        result.setCreationDate(beneficiary.getCreationDate() == null ? new Date() : beneficiary.getCreationDate());

        if (beneficiary.getLegalPerson() != null) {
            LegalPerson legalPerson = new LegalPerson();
            legalPerson.setId(beneficiary.getLegalPerson().getId());
            legalPerson.setName(new Description(langId, beneficiary.getLegalPerson().getNameStrId(), beneficiary.getLegalPerson().getName()));
            legalPerson.setIdentificationNumber(beneficiary.getLegalPerson().getIdentificationNumber());
            legalPerson.setFiId(beneficiary.getLegalPerson().getFiId());
            result.setLegalPerson(legalPerson);
        }

        if (beneficiary.getPhysicalPerson() != null) {
            result.setPhysicalPerson(PersonModelHelper.toEntity(beneficiary.getPhysicalPerson(), langId));
        }

        result.setFinalBeneficiaries(FinalBeneficiaryModelHelper.toEntities(beneficiary.getFinalBeneficiaries(), langId));

        return result;
    }

    public static BeneficiaryMetaModel toModel(Beneficiary beneficiary, long langId) {
        BeneficiaryMetaModel result = new BeneficiaryMetaModel();
        result.setId(beneficiary.getId());
        result.setShare(beneficiary.getShare());
        result.setShare(beneficiary.getShare());
        result.setActive(beneficiary.isActive());
        result.setCurrency(beneficiary.getCurrency());
        result.setNominal(beneficiary.getNominal());
        result.setCreationDate(beneficiary.getCreationDate());

        if (beneficiary.getLegalPerson() != null) {
            // avoid stackOverflow
            LegalPersonMetaModel legalPersonMetaModel = new LegalPersonMetaModel();
            legalPersonMetaModel.setId(beneficiary.getLegalPerson().getId());
            legalPersonMetaModel.setResidentStatus(beneficiary.getLegalPerson().getResidentStatus());
            legalPersonMetaModel.setName(beneficiary.getLegalPerson().getName().getDescription(langId));
            legalPersonMetaModel.setNameStrId(beneficiary.getLegalPerson().getName().getNameStrId());
            if (beneficiary.getLegalPerson().getContactInfo() != null) {
                legalPersonMetaModel.setContactInfo(LegalPersonContactInfoModelHelper.toModel(beneficiary.getLegalPerson().getContactInfo(), langId));
            }
            legalPersonMetaModel.setBank(beneficiary.getLegalPerson().getFiId() > 0);
            legalPersonMetaModel.setFiId(beneficiary.getLegalPerson().getFiId());
            legalPersonMetaModel.setIdentificationNumber(beneficiary.getLegalPerson().getIdentificationNumber());
            result.setLegalPerson(legalPersonMetaModel);
            result.setFinalBeneficiaries(FinalBeneficiaryModelHelper.toModels(beneficiary.getFinalBeneficiaries(), langId));
        }

        if (beneficiary.getPhysicalPerson() != null) {
            result.setPhysicalPerson(PersonModelHelper.toModel(beneficiary.getPhysicalPerson(), langId));
        }

        return result;
    }

    public static List<Beneficiary> toEntities(List<BeneficiaryMetaModel> beneficiaries, long langId) {
        return beneficiaries.stream().map(b -> toEntity(b, langId)).collect(Collectors.toList());
    }

    public static List<BeneficiaryMetaModel> toModels(List<Beneficiary> beneficiaries, long langId) {
        return beneficiaries.stream().map(b -> toModel(b, langId)).collect(Collectors.toList());
    }

}
