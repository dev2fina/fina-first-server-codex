package net.fina.server.reg.database;

import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.util.RegExcelProcessingUtil;

import java.util.List;

public class GenerateSelectKeysStatement extends AbstractGenerateStatement {
    public GenerateSelectKeysStatement(DatabaseInterface databaseInterface, List<InputMetaModel> fields, List<InputMetaModel> extraFields) {
        super(databaseInterface, fields, extraFields);
    }

    public String get(String tableName, InputMetaModel fileId, String fiCode) {
        StringBuilder result = new StringBuilder();

        boolean isKeyProvided = false;

        result.append("SELECT CONCAT('',");

        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).isKey()) {
                if (i > 0 && isKeyProvided) {
                    result.append("'")
                            .append(RegExcelProcessingUtil.REG_ADVANCED_KEY_SEPARATOR)
                            .append("',");
                }
                isKeyProvided = true;

                String name = fields.get(i).getCode();
                result.append(databaseInterface.getSafeFieldname(name))
                        .append(",");
            }
        }
        result.setLength(result.length() - 1);

        result.append(") as ROW_KEY ")
                .append("FROM ")
                .append(tableName)
                .append(" WHERE ")
                .append(fileId.getCode())
                .append(" ");

        return !isKeyProvided ? null : result.toString();
    }
}
