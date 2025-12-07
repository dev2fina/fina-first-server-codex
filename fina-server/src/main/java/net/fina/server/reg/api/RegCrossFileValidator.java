package net.fina.server.reg.api;

import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.RegProcessConfig;

import java.util.List;
import java.util.Map;

public interface RegCrossFileValidator {
    void validatePostProcessComparisons(RegProcessConfig config, Map<String, List<InputMetaModel>> postProcessComparisonInputs);

    void validateUniqueColumnData(RegProcessConfig config);
}
