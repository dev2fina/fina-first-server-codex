package net.fina.server.returns.model.helper;

import net.fina.common.client.returns.ReturnTypeModel;
import net.fina.server.i18n.helper.Description;
import net.fina.server.returns.entity.ReturnType;
import net.fina.server.returns.util.ReturnTypeReservedCodesUtil;

public class ReturnTypeModelHelper {

    public static ReturnTypeModel toModel(ReturnType type, long langId) {
        ReturnTypeModel model = new ReturnTypeModel();
        model.setId(type.getId());
        model.setVersion(type.getVersion());
        model.setCode(type.getCode());
        model.setEditable(!ReturnTypeReservedCodesUtil.getInstance().isCregCode(model.getCode()));
        model.setName(type.getDescription() != null ? type.getDescription().getDescription(langId) : null);
        model.setNameStrId(type.getDescription() != null ? type.getDescription().getNameStrId() : 0);
        model.setExcelTemplate(type.isExcelTemplate());
        return model;
    }

    public static ReturnType toEntity(ReturnTypeModel type, long langId) {
        ReturnType result = new ReturnType();
        result.setId(type.getId());
        result.setVersion(type.getVersion());
        result.setCode(type.getCode());
        result.setDescription(new Description(langId, type.getNameStrId(), type.getName()));

        return result;
    }
}
