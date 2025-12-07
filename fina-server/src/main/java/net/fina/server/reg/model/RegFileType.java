package net.fina.server.reg.model;

public enum RegFileType {
    EXCEL,
    XML,
    FINA;

    public static RegFileType getTypeOrDefault(String type) {
        try {
            switch (type.toUpperCase()) {
                case "XLSX":
                    return RegFileType.EXCEL;
                case "XML":
                    return RegFileType.XML;
                case "FINA":
                    return RegFileType.FINA;
            }
            return RegFileType.EXCEL;
        } catch (Exception ignore) {
        }
        return EXCEL;
    }
}
