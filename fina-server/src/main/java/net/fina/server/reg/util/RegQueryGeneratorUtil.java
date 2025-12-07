package net.fina.server.reg.util;

import net.fina.common.shared.reg.GenerateSourceResult;
import net.fina.common.shared.reg.GenerateSourceType;
import net.fina.server.reg.database.*;
import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.InputTypeEnum;
import net.fina.server.reg.model.InputsMetaModel;

import java.sql.Connection;
import java.util.*;

public class RegQueryGeneratorUtil {

    private static int batchSize;

    public static GenerateSourceResult generate(GenerateSourceType type, List<InputsMetaModel> inputsMetaModels, Connection connection, int batchSize) {
        RegQueryGeneratorUtil.batchSize = batchSize;
        return generate(type, inputsMetaModels, connection, null);
    }

    public static GenerateSourceResult generate(GenerateSourceType type, List<InputsMetaModel> inputsMetaModels, Connection connection) {
        return generate(type, inputsMetaModels, connection, null);
    }

    public static GenerateSourceResult generate(GenerateSourceType type, List<InputsMetaModel> inputsMetaModels, Connection connection, String fiCode) {

        Map<String, String> source = new HashMap<>();

        RegDatabaseUtil regDatabaseUtil = new RegDatabaseUtil(connection);

        DatabaseInterface databaseInterface;
        if (regDatabaseUtil.isMsSql()) {
            databaseInterface = new MSSQLServerDatabaseMeta();
        } else {
            databaseInterface = new OracleDatabaseMeta();
        }

        List<InputMetaModel> extraFields = new ArrayList<>();
        InputMetaModel defaultFileId = new InputMetaModel();
        defaultFileId.setCode(AbstractGenerateStatement.FILE_ID_FIELD);
        defaultFileId.setTypeEnum(InputTypeEnum.INTEGER);
        defaultFileId.setPrecision(0);
        defaultFileId.setType(defaultFileId.getTypeEnum().getJavaType());
        defaultFileId.setLength(AbstractGenerateStatement.FILE_ID_FIELD_SIZE);

        extraFields.add(defaultFileId);

        InputMetaModel scheduleIdField = new InputMetaModel();
        scheduleIdField.setCode(AbstractGenerateStatement.SCHEDULE_ID_FIELD);
        scheduleIdField.setTypeEnum(InputTypeEnum.INTEGER);
        scheduleIdField.setPrecision(0);
        scheduleIdField.setType(scheduleIdField.getTypeEnum().getJavaType());
        scheduleIdField.setLength(AbstractGenerateStatement.FILE_ID_FIELD_SIZE);

        extraFields.add(scheduleIdField);
        String fileName = "Table_Generation.sql";

        switch (type) {
            case TABLE:
                for (InputsMetaModel inputs : inputsMetaModels) {
                    String sql = "";
                    GenerateTableStatement gis = new GenerateTableStatement(databaseInterface, new ArrayList<>(inputs.getInputs()), extraFields, null, false, null, true);
                    String tableDDL = gis.get(inputs.getTableName());
                    sql += tableDDL;
                    sql += Const.CR;
                    source.put(inputs.getReturnCode(), sql);
                }

                break;
            case INSERT:
                for (InputsMetaModel inputs : inputsMetaModels) {
                    String sql = "";
                    GenerateInsertStatement gis = new GenerateInsertStatement(databaseInterface, new ArrayList<>(inputs.getInputs()), extraFields);
                    String insert = gis.get(inputs.getTableName());
                    sql += insert;
                    sql += Const.CR;
                    source.put(inputs.getReturnCode(), sql);
                }
                break;
            case DELETE_BY_FILE_ID:
                for (InputsMetaModel inputs : inputsMetaModels) {
                    String sql = "";
                    GenerateDeleteStatement gis = new GenerateDeleteStatement(databaseInterface, Collections.singletonList(defaultFileId), new ArrayList<>(), true, RegQueryGeneratorUtil.batchSize);
                    String delete = gis.get(inputs.getTableName());
                    sql += delete;
                    sql += Const.CR;
                    source.put(inputs.getReturnCode(), sql);
                }
                break;
            case DELETE:
                for (InputsMetaModel inputs : inputsMetaModels) {
                    String sql = "";
                    GenerateDeleteStatement gis = new GenerateDeleteStatement(databaseInterface, Collections.singletonList(scheduleIdField), new ArrayList<>(), false, RegQueryGeneratorUtil.batchSize);
                    String delete = gis.get(inputs.getTableName());
                    sql += delete;
                    sql += Const.CR;
                    source.put(inputs.getReturnCode(), sql);
                }
                break;
            case UPDATE:
                for (InputsMetaModel inputs : inputsMetaModels) {
                    String sql = "";
                    GenerateUpdateStatement stmnt = new GenerateUpdateStatement(databaseInterface, new ArrayList<>(inputs.getInputs()), new ArrayList<>());
                    String update = stmnt.get(inputs.getTableName(), defaultFileId, fiCode);
                    sql += update;
                    sql += Const.CR;
                    source.put(inputs.getReturnCode(), sql);
                }
                break;
            case SELECT_KEYS:
                for (InputsMetaModel inputs : inputsMetaModels) {
                    String sql = null;
                    GenerateSelectKeysStatement stmnt = new GenerateSelectKeysStatement(databaseInterface, new ArrayList<>(inputs.getInputs()), new ArrayList<>());
                    String selectKeys = stmnt.get(inputs.getTableName(), defaultFileId, fiCode);
                    if (selectKeys != null) {
                        sql = "";
                        sql += selectKeys;
                        sql += Const.CR;
                    }
                    source.put(inputs.getReturnCode(), sql);
                }
                break;
            case DELETE_ADVANCED:
                for (InputsMetaModel inputs : inputsMetaModels) {
                    String sql = "";
                    GenerateDeleteAdvancedStatement stmnt = new GenerateDeleteAdvancedStatement(databaseInterface, new ArrayList<>(inputs.getInputs()), new ArrayList<>());
                    String deleteAdvanced = stmnt.get(inputs.getTableName(), defaultFileId, fiCode);
                    sql += deleteAdvanced;
                    sql += Const.CR;
                    source.put(inputs.getReturnCode(), sql);
                }
                break;
        }


        GenerateSourceResult result = new GenerateSourceResult();
        result.setSource(source);

        StringBuilder stringSource = new StringBuilder();
        for (Map.Entry<String, String> e : source.entrySet()) {
            stringSource.append(e.getValue());
            stringSource.append(Const.CR);
        }
        result.setStringSource(stringSource.toString());
        result.setFileName(fileName);
        result.setType(type);

        return result;

    }
}
