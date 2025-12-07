package net.fina.server.reg.database;

import net.fina.server.reg.model.InputMetaModel;

import java.util.List;

public class GenerateDeleteAdvancedStatement extends AbstractGenerateStatement {

    public GenerateDeleteAdvancedStatement(DatabaseInterface databaseInterface, List<InputMetaModel> fields, List<InputMetaModel> extraFields) {
        super(databaseInterface, fields, extraFields);
    }

    public String get(String tableName, InputMetaModel fileId, String fiCode) {
        StringBuilder result = new StringBuilder();

        result.append("DELETE FROM ").append(tableName).append(" WHERE 1=1");

        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).isKey()) {
                result.append(" AND ");
                String name = fields.get(i).getCode();
                result.append(databaseInterface.getSafeFieldname(name)).append(" = ?");
            }
        }

        result.append(" AND ")
                .append(fileId.getCode())
                .append(" IN(SELECT id from SYS_UPLOADEDFILE where bankCode = '")
                .append(fiCode)
                .append("')");

        return result.toString();
    }
}
