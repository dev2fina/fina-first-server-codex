package net.fina.common.client.fis;

public class FiImportWrapper {

    private FiImportResult importResult;

    private long percentage;


    public FiImportWrapper(){}

    public FiImportWrapper(FiImportResult importResult, long percentage) {
        this.importResult = importResult;
        this.percentage = percentage;
    }


    public FiImportResult getImportResult() {
        return importResult;
    }

    public void setImportResult(FiImportResult importResult) {
        this.importResult = importResult;
    }

    public long getPercentage() {
        return percentage;
    }

    public void setPercentage(long percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "FiImportWrapper{" +
                "importResult=" + importResult +
                ", percentage=" + percentage +
                '}';
    }
}
