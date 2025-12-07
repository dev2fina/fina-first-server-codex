package net.fina.common.client.constants;

public interface PermissionIdNames {
    public static final String FINA_WEB_INTERNAL_USER = "fina2.web.internal.user";
    public static final String FINA_WEB_EXTERNAL_USER = "fina2.web.external.user";

    public static final String FINA_REGCITY_AMEND = "fina2.regCity.amend";

    public static final String FINA_METADATA_AMEND = "fina2.metadata.amend";
    public static final String FINA_METADATA_DELETE = "fina2.metadata.delete";
    public static final String FINA_METADATA_REVIEW = "fina2.metadata.review";

    //FI, Fi Types,Fi Groups,Management
    public static final String FINA_BANK_AMEND = "fina2.bank.amend";
    public static final String FINA_BANK_DELETE = "fina2.bank.delete";
    public static final String FINA_BANK_REVIEW = "fina2.bank.review";

    //Menu
    public static final String FINA_MENU_AMEND = "fina2.menu.amend";

    //Security & Settings
    public static final String FINA_SECURITY_AMEND = "fina2.security.amend";
    public static final String FINA_SECURITY_SETTINGS = "fina2.security.settings";

    //Return
    public static final String FINA_RETURNS_AMEND = "fina2.returns.amend";
    public static final String FINA_RETURNS_DELETE = "fina2.returns.delete";
    public static final String FINA_RETURNS_REVIEW = "fina2.returns.review";

    public static final String FINA_RETURNS_PROCESS = "fina2.returns.process";
    public static final String FINA_RETURNS_ACCEPT = "fina2.returns.accept";
    public static final String FINA_RETURNS_RESET = "fina2.returns.reset";
    public static final String FINA_RETURNS_REJECT = "fina2.returns.reject";
    public static final String FINA_RETURNS_STATUSES = "fina2.returns.statuses";

    // Return version Permissions
    public static final String FINA_RETURNS_VERSION_AMEND = "fina2.returns.version.amend";
    public static final String FINA_RETURNS_VERSION_DELETE = "fina2.returns.version.delete";
    public static final String FINA_RETURNS_VERSION_REVIEW = "fina2.returns.version.review";

    //Return Definition
    public static final String FINA_RETURNS_DEFINITION_AMEND = "fina2.returns.definition.amend";
    public static final String FINA_RETURNS_DEFINITION_DELETE = "fina2.returns.definition.delete";
    public static final String FINA_RETURNS_DEFINITION_REVIEW = "fina2.returns.definition.review";
    public static final String FINA_RETURNS_DEFINITION_FORMAT = "fina2.returns.definition.format";
    public static final String FINA_RETURNS_REG_TABLE_GENERATION = "fina.returns.reg.table.generation";

    //Schedule
    public static final String FINA_RETURNS_SCHEDULE_AMEND = "fina2.returns.schedule.amend";
    public static final String FINA_RETURNS_SCHEDULE_DELETE = "fina2.returns.schedule.delete";
    public static final String FINA_RETURNS_SCHEDULE_REVIEW = "fina2.returns.schedule.review";

    public static final String FINA_REPORTS_SCHEDULER_MANAGER = "fina2.reports.scheduler.manager";
    public static final String FINA_REPORTS_SCHEDULER_ADD = "fina2.reports.scheduler.add";

    public static final String FINA_REPORTS_STORED_MANAGER = "fina2.reports.stored.manager";

    /**
     * Period Permissions.
     */
    public static final String FINA_PERIODS_REVIEW = "fina2.periods.review";
    public static final String FINA_PERIODS_AMEND = "fina2.periods.amend";
    public static final String FINA_PERIODS_DELETE = "fina2.periods.delete";

    public static final String FINA_REPORT_AMEND = "fina2.report.amend";
    public static final String FINA_REPORT_GENERATE = "fina2.report.generate";
    public static final String FINA_REPORT_SCHEDULER = "fina2.report.scheduler";
    public static final String FINA_REPORT_FOLDER_GENERATE = "fina2.report.folder.generate";

    /*
     * New Permissions
     */
    // Language Permissions
    public static final String FINA_LANGUAGE_REVIEW = "net.fina.language.review";
    public static final String FINA_LANGUAGE_AMEND = "net.fina.language.amend";
    public static final String FINA_LANGUAGE_DELETE = "net.fina.language.delete";

    // User Permissions
    public static final String FINA_USER_REVIEW = "net.fina.user.review";
    public static final String FINA_USER_AMEND = "net.fina.user.amend";
    public static final String FINA_USER_DELETE = "net.fina.user.delete";

    // License Type Permissions
    public static final String FINA_LICTYPE_REVIEW = "net.fina.lictype.review";
    public static final String FINA_LICTYPE_AMEND = "net.fina.lictype.amend";
    public static final String FINA_LICTYPE_DELETE = "net.fina.lictype.delete";

    // DCS Permissions
    public static final String DCS_FILEREVIEW = "net.fina.dcs.fileReview";
    public static final String DCS_FILEUPLOAD = "net.fina.dcs.fileUpload";
    public static final String DCS_FILEUPLOAD_DELETE = "net.fina.dcs.fileUpload.delete";
    public static final String DCS_MANULAINPUT = "net.fina.dcs.manualInput";
    public static final String DCS_OFFLINETOOL = "net.fina.dcs.offlineTool";
    public static final String DCS_UNDEFINED_BANK = "net.fina.dcs.undefinedBank";

    //Audit Log(NOT USED IN FINA TOOLS)
    public static final String AUDIT_TRAIL_LOG = "net.fina.auditTrailLog";

    //Tools
    public static final String FINA_TOOL_AUDIT_LOG = "net.fina.tool.auditTrailLog";
    public static final String FINA_TOOL_MAIL_LOG = "net.fina.tool.mailLog";
    public static final String FINA_TOOL_CACHE_MANAGER = "net.fina.tool.cacheManager";
    public static final String FINA_TOOL_OST_RELEASE = "net.fina.tool.ostRelease";
    public static final String FINA_TOOL_MDT_TO_XML = "net.fina.tool.mdtToXml";
    public static final String FINA_TOOL_RETURN_TO_XML = "net.fina.tool.returnToXml";
    public static final String FINA_TOOL_MDT_GENERATOR = "net.fina.tool.mdtGenerator";
    public static final String FINA_TOOL_FINA_FILE_DECRYPTION = "net.fina.tool.finaFileDecryption";
    public static final String FINA_TOOL_FINA_BUNDLE_TRANSLATOR = "net.fina.tool.bundleTranslator";

    //Import Manager
    public static final String FINA_IMPORT_REVIEW = "net.fina.import.review";
    public static final String FINA_IMPORT_UPLOAD = "net.fina.import.upload";
    public static final String FINA_IMPORT_RE_IMPORT = "net.fina.import.reImport";


    //Calendar & Holidays
    public static final String FINA_CALENDAR_REVIEW = "net.fina.calendar.review";
    public static final String FINA_CALENDAR_AMEND = "net.fina.calendar.amend";
    public static final String FINA_CALENDAR_DELETE = "net.fina.calendar.delete";

    //Communicator
    String FINA_COMMUNICATOR_NOTIFICATIONS_REVIEW = "net.fina.communicator.notifications.review";
    String FINA_COMMUNICATOR_NOTIFICATIONS_AMEND = "net.fina.communicator.notifications.amend";
    String FINA_COMMUNICATOR_NOTIFICATIONS_DELETE = "net.fina.communicator.notifications.delete";
    String FINA_COMMUNICATOR_NOTIFICATIONS_ACCEPT = "net.fina.communicator.notifications.accept";

    String FINA_COMMUNICATOR_MESSAGES_REVIEW = "net.fina.communicator.messages.review";
    String FINA_COMMUNICATOR_MESSAGES_AMEND = "net.fina.communicator.messages.amend";
    String FINA_COMMUNICATOR_MESSAGES_DELETE = "net.fina.communicator.messages.delete";
    String FINA_COMMUNICATOR_MESSAGES_ACCEPT = "net.fina.communicator.messages.accept";
    String FINA_COMMUNICATOR_MESSAGES_ACCEPT_WITHOUT_ATTACHMENT = "net.fina.communicator.messages.acceptWithoutAttachment";
    String FINA_COMMUNICATOR_MESSAGES_BOOKMARKS_REVIEW = "net.fina.communicator.messages.bookmarks.review";
    String FINA_COMMUNICATOR_MESSAGES_BOOKMARKS_AMEND = "net.fina.communicator.messages.bookmarks.amend";

    //Token authorization
    String FINA_SECURITY_TOKEN_ACCESS = "net.fina.security.token.access";

    // Law Document
    static final String LEGISLATIVE_DOCUMENT_REVIEW = "net.fina.legislativeDocument.review";
    static final String LEGISLATIVE_DOCUMENT_AMEND = "net.fina.legislativeDocument.amend";
    static final String LEGISLATIVE_DOCUMENT_DELETE = "net.fina.legislativeDocument.delete";

    // PostBox
    static final String POSTBOX_REVIEW = "net.fina.postbox.review";
    static final String POSTBOX_AMEND = "net.fina.postbox.amend";
    static final String POSTBOX_DELETE = "net.fina.postbox.delete";

    // EMS
    static final String EMS_INSPECTION_REVIEW = "net.fina.ems.inspection.review";
    static final String EMS_INSPECTION_AMEND = "net.fina.ems.inspection.amend";
    static final String EMS_INSPECTION_DELETE = "net.fina.ems.inspection.delete";


    static final String EMS_INSPECTION_TYPE_REVIEW = "net.fina.ems.inspectionType.review";
    static final String EMS_INSPECTION_TYPE_AMEND = "net.fina.ems.inspectionType.amend";
    static final String EMS_INSPECTION_TYPE_DELETE = "net.fina.ems.inspectionType.delete";

    static final String EMS_SANCTION_TYPE_REVIEW = "net.fina.ems.sanctionType.review";
    static final String EMS_SANCTION_TYPE_AMEND = "net.fina.ems.sanctionType.amend";
    static final String EMS_SANCTION_TYPE_DELETE = "net.fina.ems.sanctionType.delete";
    static final String EMS_SANCTION_FINE_TYPE_REVIEW = "net.fina.ems.sanctionFineType.review";
    static final String EMS_SANCTION_FINE_TYPE_AMEND = "net.fina.ems.sanctionFineType.amend";
    static final String EMS_SANCTION_FINE_TYPE_DELETE = "net.fina.ems.sanctionFineType.delete";

    static final String EMS_INSPECTION_COLUMNS_REVIEW = "net.fina.ems.inspection.columns.review";
    static final String EMS_INSPECTION_COLUMNS_AMEND = "net.fina.ems.inspection.columns.amend";
    static final String EMS_INSPECTION_COLUMNS_DELETE = "net.fina.ems.inspection.columns.delete";

    static final String EMS_FI_EXPORT = "net.fina.ems.fi.export";

    static final String EMS_IMPORT_CONFIGURATION_REVIEW = "net.fina.ems.import.configuration.review";
    static final String EMS_IMPORT_CONFIGURATION_AMEND = "net.fina.ems.import.configuration.amend";
    static final String EMS_IMPORT_CONFIGURATION_DELETE = "net.fina.ems.import.configuration.delete";

    static final String EMS_IMPORT_FILE_REVIEW = "net.fina.ems.import.file.review";
    static final String EMS_IMPORT_FILE_IMPORT = "net.fina.ems.import.file.import";

    static final String EMS_INSPECTION_EXPORT = "net.fina.ems.inspection.export";

    static final String EMS_RECOMMENDATION_REVIEW = "net.fina.ems.recommendation.review";

    static final String EMS_FOLLOWUP_REVIEW = "net.fina.ems.followup.review";
    static final String EMS_FOLLOWUP_AMEND = "net.fina.ems.followup.amend";
    static final String EMS_FOLLOWUP_DELETE = "net.fina.ems.followup.delete";


    static final String FINA_COMMUNICATOR_FILE_SIGN = "net.fina.communicator.file.sign";

    static final String INPUT_MANAGER_REVIEW = "net.fina.inputManager.review";

    static final String FAQ_REVIEW = "net.fina.faq.review";
    static final String FAQ_QUESTION_ASK = "net.fina.faq.ask.question";
    static final String FAQ_AMEND = "net.fina.faq.amend";

    static final String FINA_TAG_REVIEW = "net.fina.tag.review";
    static final String FINA_TAG_AMEND = "net.fina.tag.amend";
    static final String FINA_TAG_DELETE = "net.fina.tag.delete";

    //FIRST Permissions
    static final String FIRST_REVIEW = "net.fina.first.review";

    static final String FIRST_FI_REGISTRY_REVIEW = "net.fina.first.registry.review";
    static final String FIRST_FI_REGISTRY_REGISTER_NEW = "net.fina.first.registry.registerNew";

    static final String FIRST_CONFIG_REVIEW = "net.fina.first.config.review";
    static final String FIRST_CONFIG_AMEND = "net.fina.first.config.amend";
    static final String FIRST_CONFIG_DELETE = "net.fina.first.config.delete";

    static final String FIRST_SEARCH_REVIEW = "net.fina.first.search.review";

    static final String FIRST_DOCUMENT_REVIEW = "net.fina.first.document.review";
    static final String FIRST_DOCUMENT_AMEND = "net.fina.first.document.amend";
    static final String FIRST_DOCUMENT_DELETE = "net.fina.first.document.delete";

    static final String FIRST_TASK_REVIEW = "net.fina.first.task.review";
    static final String FIRST_TASK_START = "net.fina.first.task.start";

    static final String FIRST_ATTESTATION_REVIEW = "net.fina.first.attestation.review";

    static final String FIRST_BLACKLIST_REVIEW = "net.fina.first.blacklist.review";
    static final String FIRST_BLACKLIST_AMEND = "net.fina.first.blacklist.amend";
    static final String FIRST_BLACKLIST_DELETE = "net.fina.first.blacklist.delete";

    static final String DCS_FI_PROFILE_REVIEW = "net.fina.dcs.fiProfile.review";

    static final String DCS_DASHBOARD_REVIEW = "net.fina.dcs.dashboard.review";

    String FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_REVIEW = "net.fina.first.organization.individual.registry.review";
    String FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_AMEND = "net.fina.first.organization.individual.registry.amend";
    String FIRST_ORGANIZATION_INDIVIDUAL_REGISTRY_DELETE = "net.fina.first.organization.individual.registry.delete";

    String FIRST_FI_DOCUMENT_REQUEST_REVIEW = "net.fina.first.fi.document.request.review";
    String FIRST_FI_DOCUMENT_REQUEST_AMEND = "net.fina.first.fi.document.request.amend";
    String FIRST_FI_DOCUMENT_REQUEST_DELETE = "net.fina.first.fi.document.request.delete";

    String FIRST_FI_REGISTRY_IMPORT = "net.fina.first.registry.import";

    String EMS_SYNC_PERMISSION = "net.fina.ems.sync";

    String FINA_NOTIFICATIONS = "net.fina.web.notifications";

    String USER_FILE_REPOSITORY = "net.fina.web.userFileRepository";
    String DCS_USER_FILE_REPOSITORY = "net.fina.dcs.userFileRepository";

    String FEEDBACK_REVIEW = "net.fina.feedback.review";
    String FEEDBACK_AMEND = "net.fina.feedback.amend";
    String FEEDBACK_CATEGORY_REVIEW = "net.fina.feedbackCategory.review";
    String FEEDBACK_CATEGORY_AMEND = "net.fina.feedbackCategory.amend";

    // CATALOG
    String CATALOG_REVIEW = "net.fina.catalog.item.review";
    String CATALOG_AMEND = "net.fina.catalog.item.amend";
    String CATALOG_DELETE = "net.fina.catalog.item.delete";
    String CATALOG_IMPORT = "net.fina.catalog.import";
    String CATALOG_EXPORT = "net.fina.catalog.export";

    //SURVEY
    String SURVEY_REVIEW = "net.fina.survey.review";
    String SURVEY_PUBLIC_UPLOAD = "net.fina.survey.upload.public";
    String SURVEY_PRIVATE_UPLOAD = "net.fina.survey.upload.private";

    // POWER BI
    String POWER_BI_USER = "net.fina.powerBi.user";

    String ACTIVE_USERS_REVIEW = "net.fina.active.users.review";

    //    Dashboard
    String DASHBOARD_MANAGER = "net.fina.dashboard.manager";
    String I18N_AMEND = "net.fina.i18n.amend";
    String STORED_REPORTS_DELETE = "fina2.stored.reports.delete";
    String RETURN_SCHEDULER_REVIEW = "net.fina.return.scheduler.review";
    String RETURN_SCHEDULER_AMEND = "net.fina.return.scheduler.amend";
    String RETURN_SCHEDULER_DELETE = "net.fina.return.scheduler.delete";
    String MENU_MATRIX = "net.fina.menu.matrix";
}
