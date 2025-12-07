package net.fina.server.reg.api;

import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.reg.impl.RegProcessStatus;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.common.server.StatisticsLogger;

import java.util.List;
import java.util.Map;

public interface RegDataProcessorLocal {
    Map<Long, RegProcessStatus> processData(RegProcessConfig config, UploadFile uploadFile,
                                            Map<String, Object> properties,
                                            List<Long> fileIds,
                                            StatisticsLogger statLog) throws Exception;
}
