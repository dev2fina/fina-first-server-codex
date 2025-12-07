package net.fina.server.fi.model;

import net.fina.common.client.fis.FiTypeSimpleModel;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;

import java.util.ArrayList;
import java.util.List;

public class UserRoleFiModelHelper {

    public static UserRoleFiModel toModel(Fi fi, long langId, Boolean isUserFi) {
        UserRoleFiModel userRoleFiModel = new UserRoleFiModel();

        userRoleFiModel.setId(fi.getId());
        userRoleFiModel.setCode(fi.getCode());
        userRoleFiModel.setModifiedAt(fi.getModifiedAt());
        userRoleFiModel.setCreatedAt(fi.getCreatedAt());

        if (fi.getDescription() != null) {
            userRoleFiModel.setName(fi.getDescription().getDescription(langId));
        }
        if (fi.getAddressDescription() != null) {
            userRoleFiModel.setAddress(fi.getAddressDescription().getDescription(langId));
        }

        if (isUserFi != null && isUserFi) {
            userRoleFiModel.setUserFi(true);
        } else if (isUserFi != null) {
            userRoleFiModel.setRoleFi(true);
        }
        if (fi.getFiType() != null) {
            FiType fiType = fi.getFiType();
            userRoleFiModel.setFiType(new FiTypeSimpleModel(fiType.getId(), fiType.getCode(), fiType.getDescription().getDescription(langId)));
        }

       userRoleFiModel.setLicenseCode(fi.getDefaultLicenceCode());

        return userRoleFiModel;

    }


    public static List<UserRoleFiModel> toModels(List<Fi> fis, long langId, Boolean isUserFi) {
        if (fis != null) {
            return fis.stream().map(fi -> toModel(fi, langId, isUserFi)).toList();
        }
        return new ArrayList<>();
    }
}
