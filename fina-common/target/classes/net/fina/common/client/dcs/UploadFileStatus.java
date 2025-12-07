package net.fina.common.client.dcs;

public enum UploadFileStatus {
    UPLOADED("fina2.web.uploaded"),                                               // 0
    CONVERTED("fina2.web.converted"),                                             // 1
    REJECTED("fina2.web.rejected"),                                               // 2
    UPLOADED_VALIDATED("fina2.web.uploaded.validated"),                           // 3
    UPLOADED_NOT_VALIDATED("fina2.web.uploaded.not.validated"),                   // 4
    UPLOADED_CONVERTED("fina2.web.uploaded.converted"),                           // 5
    UPLOADED_NOT_CONVERTED("fina2.web.uploaded.not.converted"),                   // 6
    UPLOADED_PROCESSED("fina2.web.uploaded.processed"),                           // 7
    UPLOADED_ERROR_PROCESSING("fina2.web.uploaded.error.processing"),             // 8
    WRONG_FILE_NAME("net.fina.dcs.wrong.file.name"),                              // 9
    WRONG_FILE_TYPE("net.fina.dcs.wrong.file.type"),                              //10
    USER_DOES_NOT_HAVE_FI("net.fina.dcs.wrong.fi"),                               //11
    WRONG_FILE_CONTENT("net.fina.dcs.wrong.file.content"),                        //12
    INVALID_SECURITY("net.fina.dcs.invalid.signature"),                           //13
    INVALID_STRUCTURE("net.fina.dcs.invalid.structure"),                          //14
    UPLOAD_ERROR("net.fina.dcs.uploadError"),                                     //15
    MATRIX_ERROR("net.fina.dcs.converter.matrixError"),                           //16
    INVALID_VERSION("net.fina.dcs.invalid.version"),                              //17
    IMPORTED("net.fina.dcs.fileStatus.imported"),                                 //18
    RETURNS_ALREADY_SUBMITTED("net.fina.dcs.fileStatus.returnsAlreadySubmitted"), //19
    NOT_UNIQUE("net.fina.dcs.fileNotUnique"),                                     //20
    ERROR("net.fina.dcs.converter.error"),                                        //21
    INVALID_OST_VERSION("net.fina.ost.invalid.version"),                          //22
    DELETE("net.fina.dcs.status.delete"),                                         //23
    WORKING("net.fina.dcs.status.working");                                       //24

    private String code;

    private UploadFileStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get status.
     *
     * @param ordinal status number.
     * @return status enum.
     */
    public static UploadFileStatus getStatus(int ordinal) {
        try {
            return values()[ordinal];
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Get status enum.
     *
     * @param ordinal enum number.
     * @return enum.
     */
    public static UploadFileStatus getStatus(String ordinal) {
        int x;
        try {
            x = Integer.parseInt(ordinal);
        } catch (Exception e) {
            return null;
        }
        return getStatus(x);
    }
}
