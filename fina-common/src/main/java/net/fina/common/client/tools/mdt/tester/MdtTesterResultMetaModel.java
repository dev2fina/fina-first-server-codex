package net.fina.common.client.tools.mdt.tester;

import net.fina.common.client.tools.mdt.tester.MdtTesterCheckType;

import java.io.Serializable;
import java.util.Set;

public class MdtTesterResultMetaModel implements Serializable {
    private String uuid;
    private MdtTesterCheckType checkType;
    private long nodeId;
    private String nodeCode;
    private String message;
    private Set<String> correctDependenciesNodeCodes;
    private Set<String> nonExistingNodeCodes;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public MdtTesterCheckType getCheckType() {
        return checkType;
    }

    public void setCheckType(MdtTesterCheckType checkType) {
        this.checkType = checkType;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<String> getCorrectDependenciesNodeCodes() {
        return correctDependenciesNodeCodes;
    }

    public void setCorrectDependenciesNodeCodes(Set<String> correctDependenciesNodeCodes) {
        this.correctDependenciesNodeCodes = correctDependenciesNodeCodes;
    }

    public Set<String> getNonExistingNodeCodes() {
        return nonExistingNodeCodes;
    }

    public void setNonExistingNodeCodes(Set<String> nonExistingNodeCodes) {
        this.nonExistingNodeCodes = nonExistingNodeCodes;
    }
}
