package net.fina.server.legalperson.model.helper;

import net.fina.server.fi.model.RegionMetaModelHelper;
import net.fina.server.legalperson.entity.LegalPersonContactInfo;
import net.fina.server.legalperson.model.LegalPersonContactInfoMetaModel;

public class LegalPersonContactInfoModelHelper {

    public static LegalPersonContactInfo toEntity(LegalPersonContactInfoMetaModel contactInfo) {
        LegalPersonContactInfo result = new LegalPersonContactInfo();
        result.setId(contactInfo.getId());
        result.setPhone(contactInfo.getPhone());
        result.setAddress(contactInfo.getAddress());
        result.setWebSite(contactInfo.getWebSite());
        result.setPhone(contactInfo.getPhone());
        if (contactInfo.getCitizenship() != null) {
            result.setRegion(RegionMetaModelHelper.toEntity(contactInfo.getCitizenship()));
        }

        return result;
    }

    public static LegalPersonContactInfoMetaModel toModel(LegalPersonContactInfo contactInfo, long langId) {
        LegalPersonContactInfoMetaModel result = new LegalPersonContactInfoMetaModel();
        result.setId(contactInfo.getId());
        result.setPhone(contactInfo.getPhone());
        result.setAddress(contactInfo.getAddress());
        result.setWebSite(contactInfo.getWebSite());
        result.setPhone(contactInfo.getPhone());
        if (contactInfo.getRegion() != null) {
            result.setCitizenship(RegionMetaModelHelper.toModel(contactInfo.getRegion(), langId));
        }

        return result;
    }
}
