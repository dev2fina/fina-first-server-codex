package net.fina.server.returns.model.helper;

import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.returns.model.ImportedReturnMetaModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ImportedReturnModelHelper {
    private static final DateFormat DEFAULT_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<ImportedReturnMetaModel> toMetaModel(List<ImportedReturn> importedReturns) {
        List<ImportedReturnMetaModel> result = new ArrayList<>();
        if (importedReturns != null) {
            for (ImportedReturn ir : importedReturns) {
                result.add(new ImportedReturnMetaModel().setImportedReturn(ir, DEFAULT_DATETIME_FORMAT));
            }
        }
        return result;
    }

    public static ImportedReturnMetaModel toMetaModel(ImportedReturn ir){
      return   new ImportedReturnMetaModel().setImportedReturn(ir, DEFAULT_DATETIME_FORMAT);
    }

}
