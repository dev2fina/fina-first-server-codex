package net.fina.common.shared.reg;

import java.io.Serializable;
import java.util.Map;

public class GenerateSourceResult implements Serializable {

    private Map<String, String> source;
    private String stringSource;
    private String fileName;

    private GenerateSourceType type;

    public Map<String, String> getSource() {
        return source;
    }

    public void setSource(Map<String, String> source) {
        this.source = source;
    }

    public GenerateSourceType getType() {
        return type;
    }

    public void setType(GenerateSourceType type) {
        this.type = type;
    }

    public String getStringSource() {
        return stringSource;
    }

    public void setStringSource(String stringSource) {
        this.stringSource = stringSource;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
