package net.fina.server.reg.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelMappingReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixMappingOptionAdapter;
import net.fina.server.reg.model.InputsMetaModel;

import java.util.List;

public interface RegFileProcessorBaseLocal {
    List<InputsMetaModel> prepareInputs(List<MatrixMappingOptionAdapter> primaryOptions, List<String> definitionCodes, long langId) throws FinATypeException;

    int getStringColumnMaxLength();

    List<String> checkDefinitionCode(List<MatrixMappingOptionAdapter> options, List<String> definitionCodes);

    RegProcessor getInstance(String extension);

    String getReason(String reason);
}
