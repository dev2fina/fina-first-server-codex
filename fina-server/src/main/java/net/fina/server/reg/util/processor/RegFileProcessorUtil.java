package net.fina.server.reg.util.processor;


import net.fina.server.reg.model.CellConfigModel;
import net.fina.server.reg.model.InputsMetaModel;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.validator.InputValidator;

import java.sql.PreparedStatement;
import java.util.Map;

public class RegFileProcessorUtil extends RegFileProcessorUtilBase {

    protected void checkAndPersistData(final InputsMetaModel input,
                                       Map<String, CellConfigModel> currentRowValues,
                                       PreparedStatement ps,
                                       int currentRow,
                                       InputValidator inputValidator,
                                       String sheetName,
                                       RegProcessConfig config,
                                       boolean persistDate) throws Exception {
        int excelRow = currentRow + 1;

        // check current row's cells stringValues
        Map<String, CellConfigModel> result = inputValidator.validate(currentRowValues, excelRow, config, sheetName);

        if (persistDate && inputValidator.isValid()) {
                persist(input, ps, config, excelRow, result);
            }

    }

    protected void persist(final InputsMetaModel input,
                           PreparedStatement ps,
                           RegProcessConfig config,
                           int excelRow,
                           Map<String, CellConfigModel> result) throws Exception {

        int index = 1;
        index = initRowStatement(input.getInputs(), result, excelRow, ps, index);

        ps.setLong(index, config.getUploadFile().getId());
        ps.setLong(++index, config.getShceduleMap().get(input.getReturnCode()));
        ps.addBatch();

        int batchSize = config.getInsertBatchSize();

        if (excelRow % batchSize == 0) {
            ps.executeBatch();
            ps.clearBatch();
            ps.clearParameters();
            ps.getConnection().commit();
        }
    }

}
