package net.fina.common.client.exception;

/**
 * FinA exception with type/code specification.
 */
public class FinATypeException extends Exception {

    /**
     * A type of current exception.
     */
    private Type type;
    private String[] params;

    /**
     * Default Constructor.
     */
    public FinATypeException() {
    }

    /**
     * Constructor.
     *
     * @param message FinA type exception message.
     */
    public FinATypeException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param type FinA Exception Type.
     */
    public FinATypeException(Type type) {
        super(type != null ? type.name() : "");
        this.type = type;
    }

    /**
     * Creates the exception of given type.
     */
    public FinATypeException(Throwable throwable, Type type) {
        super(throwable);
        this.type = type;
    }

    /**
     * Creates the exception of given type and parameters.
     */
    public FinATypeException(Throwable throwable, Type type, String[] params) {
        super(throwable);
        this.type = type;
        this.params = params;
    }

    public FinATypeException(Type type, String[] params) {
        this.type = type;
        this.params = params;
    }

    public FinATypeException(Type type, String message) {
        super(message);
        this.type = type;
    }


    /**
     * Returns the exception message url.
     */
    public String getMessageUrl() {
        return type.messageUrl;
    }

    public Type getType() {
        return this.type;
    }

    public String[] getParams() {
        return params;
    }

    /**
     * Exception types.
     */
    public enum Type {

        /* Type instance */
        GENERAL_ERROR("net.fina.exception.generalError"),

        CONCURENT_MODIFICATION("net.fina.exception.concurentModification"),

        DEPENDENT_LANGUAGE_DATA("net.fina.exception.dependentLanguageData"),
        /**
         * Server returns this message code when some exception raises during.
         * adding node
         */
        NODE_ADD_ERROR("net.fina.exception.nodeAddError"),
        /**
         * Server returns this message code when some exception raises during.
         * saving existing node.
         */
        NODE_SAVE_ERROR("net.fina.exception.nodeSaveError"),
        /**
         * Server returns this message code when some exception raises during.
         * removing node.
         */
        NODE_REMOVE_ERROR("net.fina.exception.nodeRemoveError"),
        /**
         * Server returns this message code when user tries to add already.
         * existed node (Identifies by code).
         */
        NODE_EXISTS_ERROR("net.fina.exception.node.exists.error"),

        CODE_UNIQUE("net.fina.exception.codeUnique"),
        /**
         * Server returns this message code when code field is null.
         */
        CODE_NULL("net.fina.exception.codeNull"),

        /**
         * Server returns this message code when node's id is 0 during retriving.
         * from database.
         */
        NODE_CONVERT_ERROR("net.fina.exception.nodeTranslateError"),

        /**
         * Server returns this message code when node has a dependency from.
         * database.
         */
        DEPENDENCY_ERROR("net.fina.exception.dependency"),
        /**
         * Server returns this message code when schedule is not unique from.
         * database.
         */
        SCHEDULE_UNIQUE_ERROR("net.fina.schedule.unique"),

        /**
         * Server returns this message code when any error occurs during loading.
         * statistics of imported files.
         */
        IMPORTED_FILES_STATISTIC_ERROR("net.fina.imported.files.stat.error"),

        /**
         * Exception type use in user delete.
         */
        USER_DELETE_DEPENDENCIES("net.fina.exception.userHasDependencies"),

        ROLE_DELETE_DEPENDENCIES("net.fina.exception.roleHasDependencies"),

        USER_DELETE_CURRENT("net.fina.exception.userIsCurrent"),

        LOAD_REPORTS_ERROR("net.fina.exception.errorDuringLoad"),

        USED_BY_BANK("net.fina.exception.usedByBank"),

        USED_BY_BANK_BRANCH("net.fina.exception.usedByBankBranch"),

        CANNOT_MAKE_DEFAULT_CRITERION("net.fina.exception.defaultCriterion"),

        CRITERION_SAVED_WITH_WARNINGS("net.fina.exception.criterionSavedWithWarning"),

        REPORT_GENERATE_ERROR("net.fina.exception.errorDuringGeneration"),

        OLD_STORED_PASSWORDS_NUMBER_ERROR("net.fina.exception.oldStoredPasswordNumberError"),
        /**
         * Arises when user want to generate 'Month' (calendar month for example) period and
         * hi fill 'Start Date' gap width 05.feb.1992 (for example)
         * Reason: 5th of february isn't first day of month;
         */
        INVALID_START_DATE("net.fina.exception.invalidStartDate"),

        PERIOD_UNIQUE("net.fina.exception.period.unique"),

        NO_CRITERION_TO_SELECT("net.fina.exception.noCriterion"),

        PROPERTY_MISSING_VALUE("net.fina.exception.propertyMissingValue"),

        MDT_MODEL_CONVERT_ERROR("net.fina.exception.mdtModelConvertError"),

        REPORT_DEPENDENCY_IN_OUT_STORED_REPORTS("net.fina.exception.reportDependencyInOutStoredReports"),

        REPORT_DEPENDENCY_IN_SCHEDULED_REPORTS("net.fina.exception.reportDependencyInScheduledReports"),

        EMAIL_UNIQUE("net.fina.exception.emailUnique"),
        EMAIL_INVALID("net.fina.exception.emailInvalid"),
        EMAIL_INVALID_DOMAIN("net.fina.exception.emailInvalidDomain"),
        PHONE_UNIQUE("net.fina.exception.phoneUnique"),

        FI_EMAIL_UNIQUE("net.fina.exception.fiEmailUnique"),

        HOLIDAY_UNIQUE("net.fina.exception.holidayUnique"),

        INVALID_PERMISSIONS("net.fina.exception.invalidUserPermissions"),

        USER_NOT_FOUND("net.fina.exception.userNotFound"),

        PASSWORD_ALREADY_RESET("net.fina.exception.passwordAlreadyReset"),

        USER_BLOCKED("net.fina.exceptions.userIsBlocked"),

        NOT_UNIQUE_USER_MAIL("net.fina.exceptions.userMailNotUnique"),

        ECM_NOT_ADMIN_PERMISSION("net.fina.ecm.not.admin.permission"),

        NO_RECIPIENT("net.fina.communicator.publishRecipientError"),

        ECM_NOT_ENABLED("net.fina.emc.notEnabled"),

        USER_PROGRAMMATICALLY_DELETE("net.fina.user.programmaticallyDeleted"),

        LANGUAGE_PROGRAMMATICALLY_DELETED("net.fina.language.programmaticallyDeleted"),

        EMS_SANCTION_DOCUMENT_IS_NOT_UNIQUE("net.fina.ems.sanction.document.isNotUnique"),

        FIS_FIRST_SYNC_FAILED("net.fina.fis.syncFromFirstFailed"),

        INVALID_DUE_DATE("net.fina.schedule.invalidDueDate"),

        INVALID_CODE("net.fina.invalid.code"),

        USER_DISABLED("net.fina.exceptions.userIsDisabled"),

        INVALID_VALUE("net.fina.exceptions.invalidValue"),

        ENTITY_PROGRAMMATICALLY_DELETED("net.fina.exceptions.entityProgrammaticallyDeleted"),

        RETURN_DELETE_ERROR("returnDeleteError");

        private final String messageUrl;

        /**
         * Creates the instance of the type.
         */
        Type(String messageUrl) {
            this.messageUrl = messageUrl;
        }

        public String getCode() {
            return messageUrl;
        }
    }
}