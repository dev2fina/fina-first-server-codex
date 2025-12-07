package net.fina.server.reg.database;


import net.fina.server.reg.model.InputMetaModel;

import java.util.ArrayList;
import java.util.List;

public class GenerateTableStatement extends AbstractGenerateStatement {

    private final String tk;
    private final boolean useAutoinc;
    private final String pk;
    private final boolean semicolon;

    public GenerateTableStatement(DatabaseInterface databaseInterface,
                                  List<InputMetaModel> fields,
                                  List<InputMetaModel> extraFields,
                                  String tk,
                                  boolean useAutoinc,
                                  String pk,
                                  boolean semicolon) {
        super(databaseInterface, fields, extraFields);

        this.tk = tk;
        this.useAutoinc = useAutoinc;
        this.pk = pk;
        this.semicolon = semicolon;
    }

    public String get(String tableName) {
        StringBuilder result = new StringBuilder();

        List<String> keys = new ArrayList<>();

        result.append("CREATE TABLE ");

        result.append(tableName).append(Const.CR);
        result.append("(").append(Const.CR);
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                result.append(", ");
            } else {
                result.append("  ");
            }

            InputMetaModel v = fields.get(i);
            if (v.isKey()) {
                keys.add(v.getCode());
            }
            result.append(databaseInterface.getFieldDefinition(v, tk, pk, useAutoinc, true, semicolon));
        }
        // At the end, before the closing of the statement, we might need to add
        // some constraints...
        // Technical keys
        if (tk != null) {
            if (databaseInterface.requiresCreateTablePrimaryKeyAppend()) {
                result.append(", PRIMARY KEY (").append(tk).append(")").append(Const.CR);
            }
        }

        // Primary keys
        if (pk != null) {
            if (databaseInterface.requiresCreateTablePrimaryKeyAppend()) {
                result.append(", PRIMARY KEY (").append(pk).append(")").append(Const.CR);
            }
        }
        result.append(")").append(Const.CR);

        if (semicolon) {
            result.append(";");
        }

        if (!keys.isEmpty()) {
            result.append("\n");
            result.append("CREATE UNIQUE INDEX UNIQUE_KEY_")
                    .append(tableName)
                    .append(" ON ").append(tableName).append("(");
            for (String key : keys) {
                result.append(key)
                        .append(",");
            }
            result.append("schedule_id);");
        }

        return result.toString();
    }
}
