package net.fina.server.reg.database;


import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.reg.model.InputMetaModel;

import java.util.List;

public class GenerateDeleteStatement extends AbstractGenerateStatement {
    private boolean deleteByFileID;
    private int deleteBatchSize;

    //TODO REFACTOR
    public GenerateDeleteStatement(DatabaseInterface databaseInterface, List<InputMetaModel> fields, List<InputMetaModel> extraFields, boolean deleteByFileID, int deleteBatchSize) {
        super(databaseInterface, fields, extraFields);
        this.deleteByFileID = deleteByFileID;
        this.deleteBatchSize = deleteBatchSize;
    }

    public String get(String tableName) {
        StringBuilder result = new StringBuilder();

        result.append("DELETE TOP (").append(deleteBatchSize).append(") FROM ").append(tableName).append(" WHERE ");

        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                result.append(" AND ");
            }
            String name = fields.get(i).getCode();
            result.append(databaseInterface.getSafeFieldname(name))
                    .append("=?");
        }

        if (!deleteByFileID) {
            result.append(" AND ")
                    .append(databaseInterface.getSafeFieldname(AbstractGenerateStatement.FILE_ID_FIELD))
                    .append("<>?");

        }

        return result.toString();
    }

}
