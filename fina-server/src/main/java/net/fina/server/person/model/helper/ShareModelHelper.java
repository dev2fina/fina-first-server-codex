package net.fina.server.person.model.helper;

import net.fina.server.fi.model.RegionMetaModelHelper;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.legalperson.model.helper.LegalPersonModelHelper;
import net.fina.server.person.model.ShareMetaModel;

import java.util.ArrayList;
import java.util.List;

public class ShareModelHelper {

    public static ShareMetaModel toModel(Beneficiary beneficiary, long langId) {
        ShareMetaModel model = new ShareMetaModel();
        model.setId(beneficiary.getId());
        model.setShareDate(beneficiary.getCreationDate());
        model.setSharePercentage(beneficiary.getShare());

        if (beneficiary.getLegalPerson() != null) {
            LegalPersonMetaModel company = new LegalPersonMetaModel();
            company.setId(beneficiary.getLegalPerson().getId());
            company.setIdentificationNumber(beneficiary.getLegalPerson().getIdentificationNumber());
            company.setBank(beneficiary.getLegalPerson().getFiId() > 0);
            company.setFiId(beneficiary.getLegalPerson().getFiId());
            company.setName(beneficiary.getLegalPerson().getName().getDescription(langId));
            company.setNameStrId(beneficiary.getLegalPerson().getName().getNameStrId());
            if (beneficiary.getLegalPerson().getCountry() != null) {
                company.setCountry(RegionMetaModelHelper.toModel(beneficiary.getLegalPerson().getCountry(), langId));
            }
            model.setCompany(company);
        }

        return model;
    }

    public static List<ShareMetaModel> beneficiaryToShares(List<Beneficiary> entities, long langId) {
        List<ShareMetaModel> result = new ArrayList<>();
        entities.forEach(e -> result.add(toModel(e, langId)));
        return result;
    }

    public static Beneficiary toBeneficiary(ShareMetaModel shareModel) {
        Beneficiary beneficiary=new Beneficiary();
        beneficiary.setShare(shareModel.getSharePercentage());
        beneficiary.setCreationDate(shareModel.getShareDate());
        beneficiary.setActive(true);
        LegalPerson lp=new LegalPerson();
        lp.setId(shareModel.getCompany().getId());
        beneficiary.setLegalPerson(lp);

        return beneficiary;
    }
}
