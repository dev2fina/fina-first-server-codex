package net.fina.server.reg.database;


import net.fina.server.reg.model.InputMetaModel;

import java.util.List;

public class GenerateInsertStatement extends AbstractGenerateStatement {

    public GenerateInsertStatement(DatabaseInterface databaseInterface, List<InputMetaModel> fields, List<InputMetaModel> extraFields) {
        super(databaseInterface, fields, extraFields);
    }

    public String get(String tableName) {
        StringBuilder result = new StringBuilder();

        result.append("INSERT INTO ").append(tableName).append(" (");

        // now add the names in the row:
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            String name = fields.get(i).getCode();
            result.append(databaseInterface.getSafeFieldname(name));
        }
        result.append(") VALUES (");

        // Add placeholders...
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(" ?");
        }
        result.append(')');

        return result.toString();
    }
}
