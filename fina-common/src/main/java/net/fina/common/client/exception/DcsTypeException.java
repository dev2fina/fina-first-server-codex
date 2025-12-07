package net.fina.common.client.exception;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DcsTypeException extends RuntimeException {

    /**
     * A type of current exception.
     */
    protected Type type;
    protected String[] params;
    protected String stackTraceText;
    protected String message;
    protected List<String> reasons = new ArrayList<>();

    public DcsTypeException() {
        super();
    }

    public DcsTypeException(List<String> reasons) {
        this.reasons = reasons;
    }

    public DcsTypeException(String message) {
        this.message = message;
    }

    public DcsTypeException(Throwable throwable) {
        super(throwable);
    }

    public DcsTypeException(Type type) {
        this.type = type;
    }

    public DcsTypeException(Throwable throwable, Type type) {
        super(throwable);
        this.type = type;
    }

    /**
     * Constructor.
     *
     * @param throwable parent.
     * @param type      type.
     * @param params    parameters.
     */
    public DcsTypeException(Throwable throwable, Type type, String[] params) {
        super(throwable);
        this.type = type;
        this.params = params;
    }

    public DcsTypeException(Type type, String stackTraceText) {
        this.type = type;
        this.stackTraceText = stackTraceText;
    }

    public DcsTypeException(Type type, String[] params) {
        this.type = type;
        this.params = params;
    }

    /**
     * Construct exception.
     *
     * @param type    type.
     * @param message message.
     * @return exception.
     */
    public static DcsTypeException create(Type type, String message) {
        DcsTypeException dcsTypeException = new DcsTypeException(message);
        dcsTypeException.setType(type);
        return dcsTypeException;
    }

    public String getMessageUrl() {
        return type.messageUrl;
    }

    public String[] getParams() {
        return params;
    }

    public String getStackTraceText() {
        return stackTraceText;
    }

    public void setStackTraceText(String stackTraceText) {
        this.stackTraceText = stackTraceText;
    }

    /**
     * Get message.
     *
     * @return message.
     */
    public String getMessage() {
        if ((message == null || message.trim().isEmpty())) {
            if (!reasons.isEmpty()) {

                StringBuilder buff = new StringBuilder();
                buff.append(reasons.get(0));
                for (int i = 1; i < reasons.size(); i++) {
                    buff.append(";").append(reasons.get(i));
                }
                return buff.toString();
            } else if (type != null) {
                return type.toString();
            }
        }
        return message;
    }

    public Type getType() {
        return type;
    }

    /**
     * Set type.
     *
     * @param type type.
     * @author Chelomisha.
     */
    public void setType(Type type) {
        this.type = type;
    }

    public void addMessage(String message) {
        this.message += ("\n" + message);
    }

    public List<String> getReasonsList() {
        return this.reasons;
    }

    public enum Type {

        /**
         * Arises when any unknown error occurs.
         */
        GENERAL_ERROR("net.fina.dcs.exception.generalError"),
        /**
         * Arises when system is unable to get main matrix.
         */
        MAIN_MATRIX_NOT_SET("net.fina.props.main.matrix.not.set.error"),
        /**
         * Arises when system is unable to get primary matrix.
         */
        MATRIX_NOT_SET("net.fina.props.matrix.not.set.error"),
        /**
         * Arises when matrix mapping source property is not configured in fina.xml.
         */
        MATRIX_MAPPING_SOURCE_NOT_SET("net.fina.props.matrix.mapping.source.not.set.error"),
        /**
         * Arises when there are no files to proceed.
         */
        NO_FILES_SET("net.fina.dcs.no.files"),
        /**
         * Arises when there is empty content.
         */
        EMPTY_CONTENT_ERROR("net.fina.dcs.empty.content"),
        /**
         * Arises when document reader is not set.
         */
        READER_NOT_SET("net.fina.dcs.reader.not.set"),
        /**
         * Arises when system is unable to identify what kind of file it is.
         */
        UNKNOWN_CONTENT("net.fina.dcs.unknown.content"),
        /**
         * Arises when file quantity is more then limited.
         */
        TOO_MANY_FILES_ERROR("net.fina.dcs.too.many.files.error"),
        /**
         * Arises when system is unable to read file.
         */
        FILE_READ_ERROR("net.fina.dcs.file.read.error"),
        /**
         * Arises when there is no any file(s) in zip.
         */
        EMPTY_ZIP_ERROR("net.fina.dcs.empty.zip.error"),
        /**
         * Arises when dcs is not able to read content.
         */
        CONTENT_READ_ERROR("net.fina.dcs.content.read.error"),
        /**
         * Arises when dcs is not able to identify table type.
         */
        TABLE_TYPE_ERROR("net.fina.dcs.table.type.error"),
        /**
         * Arises when dcs is not able to identify cell type.
         */
        CELL_TYPE_ERROR("net.fina.dcs.cell.type.error"),
        /**
         * Arises when file name is invalid for DCS.
         */
        WRONG_FILE_NAME_ERROR("net.fina.dcs.wrong.file.name"),
        /**
         * Arises when file type is invalid for DCS.
         */
        WRONG_FILE_TYPE_ERROR("net.fina.dcs.wrong.file.type"),
        /**
         * Arises when file name in zip and zip file name are different.
         */
        ZIP_AND_CONTENT_NAME_IS_INVALID("net.fina.dcs.zip.xls.are.different"),
        /**
         * Arises when user does not have such FI or it does not exist.
         */
        USER_HAS_NO_FI("net.fina.dcs.user.has.no.fi"),
        /**
         * Arises when master file has additional sheet or does not have.
         * required one.
         */
        INVALID_STRUCRURE("net.fina.dcs.xls.invalid.struxture"),
        OPTIONS_SHEET_FAILURE("net.fina.dcs.xls.options.sheet.failure"),
        INVALID_PASSWORD("net.fina.dcs.conventer.invalidPassword"),
        PASSWORD_CHANGE_ERROR("net.fina.passwordChangeError"),
        CELL_IS_NULL_OR_IS_EMPTY("net.fina.dcs.converter.incorectCell"),
        ILLEGAL_CELL_TYPE("net.fina.dcs.converter.invalidCellType"),
        MATRIX_DUPLICATED_PATTERN("net.fina.dcs.converter.duplicatePattern"),
        EXCEL_FILE_BUT_WRONG_EXCEL_EXTENSION("net.fina.dcs.converter.excelFileWithWrongExcelExtesion"),
        SECURITY_INVALID_SIGN("net.fina.dcs.security.invalidSign"),
        SECURITY_INVALID_ENCRYPT("net.fina.dcs.security.invalidEncrypt"),
        INVALID_XML_STRUCTURE("net.fina.dcs.invalidXmlStructure"),
        SUBMITED_FILE_INVALID_VERSION("net.fina.dcs.invalid.version"),
        CANNOT_CHANGE_ACCEPTED_RETURN("net.fina.server.cannotChangeAccepted"),
        SUBMITED_FILE_RETURNS_INVALID_PERIOD("net.fina.dcs.invalidPeriod"),
        SUBMITED_FILE_RETURNS_INVALID_FI("net.fina.dcs.invalidFi"),
        SUBMITED_FILE_DUPLICATE_RETURNS("net.fina.dcs.duplicateReturns"),
        INVALID_OST_VERSION("net.fina.ost.invalid.version"),
        INVALID_FILE_NAME_FI_AND_FILE_CONTENT_FI("net.fina.dcs.converter.invalidFileNameFiAndContentFiCode"),
        INVALID_DIGITAL_SIGNATURE("net.fina.dcs.invalidDigitalSignature"),
        CERTIFICATE_REVOKED("net.fina.dcs.security.certificateRevoked"),

        /**
         * Arises when user attempts to set their email to one already used by other user
         */
        EMAIL_UNIQUE("net.fina.dcs.exception.emailUnique");

        private final String messageUrl;

        private Type(String messageUrl) {
            this.messageUrl = messageUrl;
        }

        public String getCode() {
            return messageUrl;
        }

        public String getReplaceableCode() {
            return "${" + getCode() + "}";
        }
    }
}
