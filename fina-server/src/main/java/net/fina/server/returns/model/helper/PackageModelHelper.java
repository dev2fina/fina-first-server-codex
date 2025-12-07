package net.fina.server.returns.model.helper;

import net.fina.common.client.fis.FiTypeModel;
import net.fina.common.client.returns.ReturnDefinitionModel;
import net.fina.common.shared.PackageMetaModel;
import net.fina.server.fi.entity.FiType;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnPackage;

import java.util.ArrayList;
import java.util.List;

public class PackageModelHelper {

    public static PackageMetaModel toModel(ReturnPackage returnPackage,long langId){

        List<FiTypeModel> fiTypeModels = new ArrayList<>();
        List<ReturnDefinitionModel> rdModels = new ArrayList<>();

        PackageMetaModel result = new PackageMetaModel();
        result.setId(returnPackage.getId());
        result.setCode(returnPackage.getCode());
        result.setNote(returnPackage.getNote());
        result.setHasFiTypes(false);
        result.setHasReturnDefinitions(false);

        if (returnPackage.getFiTypes() != null) {
            result.setHasFiTypes(true);
            for (FiType ft : returnPackage.getFiTypes()) {
                FiTypeModel model = new FiTypeModel();
                model.setId(ft.getId());
                model.setCode(ft.getCode());
                model.setVersion(ft.getVersion());
                model.setName(ft.getDescription().getDescription(langId));
                fiTypeModels.add(model);
            }
        }

        if (returnPackage.getReturnDefinitions() != null) {
            result.setHasReturnDefinitions(true);
            for (ReturnDefinition rd : returnPackage.getReturnDefinitions()) {
                ReturnDefinitionModel model = new ReturnDefinitionModel();
                model.setId(rd.getId());
                model.setCode(rd.getCode());
                model.setName(rd.getDescription().getDescription(langId));
                model.setVersion(rd.getVersion());
                rdModels.add(model);
            }
        }

        result.setFiTypes(fiTypeModels);
        result.setReturnDefinitions(rdModels);

        return result;
    }
}
