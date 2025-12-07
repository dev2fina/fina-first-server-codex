package net.fina.server.dcs.uploadfile.impl.reader.excel.util;

import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixMappingSource;
import org.jboss.logging.Logger;

public class MatrixMappingUtil {
    private static final Logger log = Logger.getLogger(MatrixMappingUtil.class.getName());

    public static MatrixMappingSource getMatrixMappingSource() {
        try {
            return MatrixMappingSource.valueOf(ConfigurationUtil.get().get("MATRIX_MAPPING_SOURCE").trim().toUpperCase());
        } catch (Throwable t) {
            log.error("MATRIX_MAPPING_SOURCE property is missing from fina.xml or has incorrect value");
        }

        //TODO Return Unknown
        return MatrixMappingSource.EXCEL;
    }

}
