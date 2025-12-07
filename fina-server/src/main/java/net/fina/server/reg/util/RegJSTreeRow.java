package net.fina.server.reg.util;

import net.fina.server.processing.script.js.JSTreeBase;
import net.fina.server.reg.model.CellConfigModel;
import net.fina.server.reg.model.RegProcessConfig;

import java.util.Map;

public class RegJSTreeRow implements JSTreeBase {
    private final RegProcessConfig config;
    private final Map<String, CellConfigModel> mdtCodeValues;
    private CellConfigModel currentItem;

    public RegJSTreeRow(RegProcessConfig config, Map<String, CellConfigModel> mdtCodeValues, CellConfigModel currentItem) {
        this.config = config;
        this.mdtCodeValues = mdtCodeValues;
        this.currentItem = currentItem;
    }


    public double lookup(String code) {
        CellConfigModel cellConfigModel = mdtCodeValues.get(code.trim());
        if (cellConfigModel != null) {
            return mdtCodeValues.get(code.trim()).convertAndGetNumericValue();
        }
        return 0;
    }

    public String lookupString(String code) {
        CellConfigModel cellConfigModel = mdtCodeValues.get(code.trim());
        if (cellConfigModel != null) {
            return mdtCodeValues.get(code.trim()).getOriginalValue();
        }
        return null;
    }

    /**
     * @return count of digits after . (i.e. 123.123 returns 3)
     */
    public int decimalCount() {
        if (currentItem == null) {
            return 0;
        }

        String nValue = currentItem.getOriginalValue();

        String[] parts = nValue.split("\\.");
        if (parts.length == 2) {
            return parts[1].length();
        }

        return 0;
    }

}
