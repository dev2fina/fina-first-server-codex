package net.fina.server.reg.util.processor;

import net.fina.server.dcs.uploadfile.model.RegAdvancedFileType;
import net.fina.server.reg.model.CellConfigModel;
import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.InputsMetaModel;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.validator.InputValidator;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegAdvancedFileProcessorUtil extends RegFileProcessorUtilBase {
    @Override
    protected void checkAndPersistData(InputsMetaModel input, Map<String, CellConfigModel> currentRowValues, PreparedStatement ps, int currentRow, InputValidator inputValidator, String sheetName, RegProcessConfig config, boolean persistDate) throws Exception {
        int excelRow = currentRow + 1;

        // check current row's cells stringValues
        Map<String, CellConfigModel> result = inputValidator.validate(currentRowValues, excelRow, config, sheetName);

        // check reg advanced
        inputValidator.checkRegAdvancedRow(currentRowValues, config, sheetName, excelRow);

        if (persistDate && inputValidator.isValid()) {
            persist(input, ps, config, excelRow, result);
        }
        currentRowValues.clear();
    }

    @Override
    protected void persist(InputsMetaModel input, PreparedStatement ps, RegProcessConfig config, int excelRow, Map<String, CellConfigModel> result) throws Exception {
        int index = 1;
        if (config.getRegAdvancedFileType().equals(RegAdvancedFileType.CREATE)) {
            index = initRowStatement(input.getInputs(), result, excelRow, ps, index);

            ps.setLong(index, config.getUploadFile().getId());
            ps.setLong(++index, config.getShceduleMap().get(input.getReturnCode()));
        } else {

            if (!config.getRegAdvancedFileType().equals(RegAdvancedFileType.DELETE)) {
                // init non key columns
                List<InputMetaModel> nonKeyInputs = input.getInputs().stream().filter(i -> !i.isKey()).collect(Collectors.toList());
                index = initRowStatement(nonKeyInputs, result, excelRow, ps, index);
            }

            // init key columns
            List<InputMetaModel> keyInputs = input.getInputs().stream().filter(InputMetaModel::isKey).collect(Collectors.toList());
            initRowStatement(keyInputs, result, excelRow, ps, index);
        }

        ps.addBatch();

        if (excelRow % 20000 == 0) {
            ps.executeBatch();
            ps.clearBatch();
        }
    }
}
