package net.fina.server.reg.api;


import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.util.processor.RegFileProcessorUtilBase;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

public interface RegProcessor {
    List<String> process(RegProcessConfig config,
                         Connection conn,
                         RegFileProcessorUtilBase regFileProcessorUtil);

    default List<String> processParallel(RegProcessConfig config,
                                         DataSource dataSource,
                                         Connection autClosable,

                                         RegFileProcessorUtilBase regFileProcessorUtil) {
        return process(config, autClosable, regFileProcessorUtil);
    }

}
