package net.fina.common.client.returns;

public enum ImportedFileType {
    UNKNOWN("Unknown"),
    DCS("fina2.dcs.upload.type.dcs"),
    MAIL("fina2.dcs.upload.type.email"),
    MANUAL("fina2.dcs.upload.type.manual"),
    MIXED("fina2.dcs.upload.type.mixed"),
    SUBMISSION_TOOL("fina2.dcs.upload.type.submissionTool");

    String code;

    private ImportedFileType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Integer getIndex(String code) {
        if (code.equals(ImportedFileType.DCS.getCode())) {
            return 1;
        } else if (code.equals(ImportedFileType.MAIL.getCode())) {
            return 2;
        } else {
            return 3;
        }
    }

    public static String getType(Integer index) {

        //TODO: fix logic
        if (index == null || index == 0) {
            index = ImportedFileType.MANUAL.ordinal();
        }
        return ImportedFileType.values()[index].getCode();

    }
}
