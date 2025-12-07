package net.fina.common.client.returns;

import java.io.Serializable;

public class ProcessingStatusModel implements Serializable {

    private String runningCount;
    private String queuePackages;
    private String mdtCacheSize;
    private String returnDefinitionCacheSize;

    public String getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(String runningCount) {
        this.runningCount = runningCount;
    }

    public String getQueuePackages() {
        return queuePackages;
    }

    public void setQueuePackages(String queuePackages) {
        this.queuePackages = queuePackages;
    }

    public String getMdtCacheSize() {
        return mdtCacheSize;
    }

    public void setMdtCacheSize(String mdtCacheSize) {
        this.mdtCacheSize = mdtCacheSize;
    }

    public String getReturnDefinitionCacheSize() {
        return returnDefinitionCacheSize;
    }

    public void setReturnDefinitionCacheSize(String returnDefinitionCacheSize) {
        this.returnDefinitionCacheSize = returnDefinitionCacheSize;
    }
}
