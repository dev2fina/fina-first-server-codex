package net.fina.server.dcs.uploadfile.model.helper;

import net.fina.server.dcs.uploadfile.model.ImportStatusDetailModel;
import net.fina.server.returns.entity.ImportedReturn;

import java.util.ArrayList;
import java.util.List;

public class ImportStatusMetaModelHelper {

    public static ImportStatusDetailModel toModel(ImportedReturn ir) {
        ImportStatusDetailModel model = new ImportStatusDetailModel();
        model.setId(ir.getId());
        model.setStatus(ir.getStatus() != null ? String.valueOf(ir.getStatus()) : null);
        model.setCode(ir.getReturnCode());
        model.setVersion(ir.getVersionCode());
        model.setImportEnd(ir.getImportEnd());
        model.setMessage(ir.getMessage());
        model.setType(ir.getType() != null ? String.valueOf(ir.getType()) : null);
        model.setPeriodStart(ir.getPeriodStart());
        model.setPeriodEnd(ir.getPeriodEnd());
        model.setImportStart(ir.getImportStart());
        model.setImportEnd(ir.getImportEnd());
        return model;
    }

    public static List<ImportStatusDetailModel> toModels(List<ImportedReturn> importedReturns) {
        List<ImportStatusDetailModel> result = new ArrayList<>();
        importedReturns.forEach(ir -> result.add(toModel(ir)));
        return result;
    }
}
