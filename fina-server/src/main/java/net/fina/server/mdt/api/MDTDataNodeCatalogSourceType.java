package net.fina.server.mdt.api;

public enum MDTDataNodeCatalogSourceType {
    FI("fi.", "net.fina.catalog.fi.enable", "net.fina.catalog.fi.parent.folder.node.code", "net.fina.catalog.fi.deleted.parent.folder.node.code"),
    FI_BRANCH("fi.branch.", "net.fina.catalog.fi.branch.enable", "net.fina.catalog.fi.branch.parent.folder.node.code", "net.fina.catalog.fi.branch.deleted.parent.folder.node.code"),
    REGIONAL_STRUCTURE("reg.str.", "net.fina.catalog.reg.str.enable", "net.fina.catalog.reg.str.parent.folder.node.code", "net.fina.catalog.reg.str.deleted.parent.folder.node.code");

    private final String codePrefix;
    private final String enablePropertyKey;
    private final String parentFolderNodeCodeKey;
    private final String deletedParentFolderNodeCodeKey;

    MDTDataNodeCatalogSourceType(String codePrefix, String enablePropertyKey, String parentFolderNodeCodeKey, String deletedParentFolderNodeCodeKey) {
        this.codePrefix = codePrefix;
        this.enablePropertyKey = enablePropertyKey;
        this.parentFolderNodeCodeKey = parentFolderNodeCodeKey;
        this.deletedParentFolderNodeCodeKey = deletedParentFolderNodeCodeKey;
    }

    public String getCodePrefix() {
        return codePrefix;
    }

    public String getEnablePropertyKey() {
        return enablePropertyKey;
    }

    public String getParentFolderNodeCodeKey() {
        return parentFolderNodeCodeKey;
    }

    public String getDeletedParentFolderNodeCodeKey() {
        return deletedParentFolderNodeCodeKey;
    }
}
