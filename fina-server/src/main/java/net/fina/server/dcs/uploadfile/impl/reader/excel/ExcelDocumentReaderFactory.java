package net.fina.server.dcs.uploadfile.impl.reader.excel;

import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.v2.ExcelDocumentReaderV2;

import java.util.Map;
import java.util.Objects;

public class ExcelDocumentReaderFactory {
    private static ExcelDocumentReaderFactory instance;

    private ExcelDocumentReaderFactory() {
    }

    public static ExcelDocumentReaderFactory getInstance() {
        if (instance == null) {
            instance = new ExcelDocumentReaderFactory();
        }
        return instance;
    }

    public DocumentReader createDocumentReader(Map<String, Object> properties) {
        MatrixMappingSource source = (MatrixMappingSource) properties.get("dcs.matrix.mapping.source");
        if (Objects.requireNonNull(source) == MatrixMappingSource.DATABASE) {
            return new ExcelDocumentReaderV2(properties);
        }
        return new ExcelDocumentReader(properties);
    }
}
