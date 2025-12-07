package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Map;

public class CanProcessResult implements Serializable {
    private Map<String, String> expectedCodeNames;
    private Map<String, Integer> returnNodes;

    public Map<String, String> getExpectedCodeNames() {
        return expectedCodeNames;
    }

    public void setExpectedCodeNames(Map<String, String> expectedCodeNames) {
        this.expectedCodeNames = expectedCodeNames;
    }

    public Map<String, Integer> getReturnNodes() {
        return returnNodes;
    }

    public void setReturnNodes(Map<String, Integer> returnNodes) {
        this.returnNodes = returnNodes;
    }
}
