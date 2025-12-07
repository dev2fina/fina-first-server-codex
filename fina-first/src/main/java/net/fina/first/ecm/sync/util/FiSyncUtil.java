package net.fina.first.ecm.sync.util;

import net.fina.common.server.fi.sync.model.FiDescriptionSyncMetaModel;
import net.fina.first.ecm.registry.model.FiRegistryMetaModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FiSyncUtil {

    public static List<FiDescriptionSyncMetaModel> getDescriptionsByLanguages(List<Long> languageIds, String value) {
        List<FiDescriptionSyncMetaModel> result = new ArrayList<>();
        for (Long languageId : languageIds) {
            FiDescriptionSyncMetaModel description = new FiDescriptionSyncMetaModel();
            description.setLangId(languageId);
            description.setValue(value);
            result.add(description);
        }
        return result;
    }


    public static String getFiNaturalCode(FiRegistryMetaModel fiRegistryMetaModel) {

        //TODO
        Map<String, String> excludeFiTypePrefixMap = new HashMap<String, String>() {{
            put("LE", "სგს");
            put("FEX", "ვგპ");
            put("CRU", "სკ");
            put("MFO", "მისო");
        }};

        String code = fiRegistryMetaModel.getCode();

        String excludePrefix = excludeFiTypePrefixMap.get(fiRegistryMetaModel.getFiTypeCode());
        if (excludePrefix != null && !excludePrefix.trim().isEmpty() && code.startsWith(excludePrefix + "-")) {
            code = code.substring(excludePrefix.length() + 1, code.length());
        }

        return code.trim();
    }

}
