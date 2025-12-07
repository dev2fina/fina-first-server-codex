package net.fina.server.returns.event;

import java.io.Serializable;

public class ReturnFormatChangeEvent implements Serializable {
    private final long returnDefinitionId;

    public ReturnFormatChangeEvent(long returnDefinitionId) {
        this.returnDefinitionId = returnDefinitionId;
    }

    public long getReturnDefinitionId() {
        return returnDefinitionId;
    }
}
