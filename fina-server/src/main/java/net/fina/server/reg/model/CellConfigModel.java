package net.fina.server.reg.model;


public class CellConfigModel {
    private String originalValue;
    private String formattedValue;
    private double numericValue;

    private RegFileType regFileType = RegFileType.EXCEL;

    private int xmlLineNumber;
    private int xmlColumnNumber;

    public CellConfigModel() {
    }

    public CellConfigModel(String originalValue) {
        this.originalValue = originalValue;
    }

    public CellConfigModel(String originalValue, String formattedValue) {
        this.originalValue = originalValue;
        this.formattedValue = formattedValue;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public double getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(double numericValue) {
        this.numericValue = numericValue;
    }

    public int getXmlLineNumber() {
        return xmlLineNumber;
    }

    public void setXmlLineNumber(int xmlLineNumber) {
        this.xmlLineNumber = xmlLineNumber;
    }

    public int getXmlColumnNumber() {
        return xmlColumnNumber;
    }

    public void setXmlColumnNumber(int xmlColumnNumber) {
        this.xmlColumnNumber = xmlColumnNumber;
    }

    public RegFileType getRegFileType() {
        return regFileType;
    }

    public void setRegFileType(RegFileType regFileType) {
        this.regFileType = regFileType;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    public double convertAndGetNumericValue() {
        try {
            return Double.parseDouble(this.getOriginalValue());
        }catch (Throwable ignore) {
            // Ignored intentionally because invalid input is expected and handled by returning NAN
        }
        return Double.NaN;
    }

    @Override
    public String toString() {
        return "CellConfigModel{" +
                "originalValue='" + originalValue + '\'' +
                ", formattedValue='" + formattedValue + '\'' +
                ", numericValue=" + numericValue +
                ", cellType=" + regFileType +
                ", xmlLineNumber=" + xmlLineNumber +
                ", xmlColumnNumber=" + xmlColumnNumber +
                '}';
    }
}


