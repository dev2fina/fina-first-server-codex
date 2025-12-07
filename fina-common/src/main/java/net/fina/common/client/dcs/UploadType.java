package net.fina.common.client.dcs;

public enum UploadType {
    MANUAL("fina2.dcs.upload.type.manual"),
    DCS("fina2.dcs.upload.type.dcs"),
    EMAIL_ROBOT("fina2.dcs.upload.type.email"),
    SUBMISSION_TOOL("fina2.dcs.upload.type.submissionTool"),
    IMPORT_MANAGER("fina2.dcs.upload.type.importManager"),
    STAT_FILE("fina2.dcs.upload.type.stat");

    private String code;

    private UploadType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
