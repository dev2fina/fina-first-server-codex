package net.fina.common.shared.dashboard;

public class ChartContentMetaModel {
    private String fileName;
    private byte[] chartContent;

    public ChartContentMetaModel(String fileName, byte[] chartContent) {
        this.fileName = fileName;
        this.chartContent = chartContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getChartContent() {
        return chartContent;
    }

    public void setChartContent(byte[] chartContent) {
        this.chartContent = chartContent;
    }
}
