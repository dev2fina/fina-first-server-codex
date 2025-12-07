package net.fina.server.legalperson.model.helper;

import net.fina.server.legalperson.entity.FinalBeneficiary;
import net.fina.server.legalperson.model.FinalBeneficiaryMetaModel;
import net.fina.server.person.model.PersonMetaModel;
import net.fina.server.person.model.helper.PersonModelHelper;

import java.util.List;
import java.util.stream.Collectors;

public class FinalBeneficiaryModelHelper {

    public static FinalBeneficiaryMetaModel toModel(FinalBeneficiary beneficiary, long langId) {
        FinalBeneficiaryMetaModel result = new FinalBeneficiaryMetaModel();
        result.setId(beneficiary.getId());
        result.setPerson(PersonModelHelper.toModel(beneficiary.getPerson(), langId));
        return result;
    }

    public static FinalBeneficiary toEntity(FinalBeneficiaryMetaModel beneficiary, long langId) {
        FinalBeneficiary result = new FinalBeneficiary();
        result.setId(beneficiary.getId());
        result.setPerson(PersonModelHelper.toEntity(beneficiary.getPerson(), langId));
        return result;
    }

    public static List<FinalBeneficiary> toEntities(List<FinalBeneficiaryMetaModel> beneficiaries, long langId) {
        if (beneficiaries == null || beneficiaries.isEmpty()) {
            return null;
        }
        return beneficiaries.stream().map(b -> toEntity(b, langId)).collect(Collectors.toList());
    }

    public static List<FinalBeneficiaryMetaModel> toModels(List<FinalBeneficiary> beneficiaries, long langId) {
        return beneficiaries.stream().map(b -> toModel(b, langId)).collect(Collectors.toList());
    }
}
