package net.fina.common.client.property;

public class PropertyKeys {

    public static final String ALLOWED_LOGIN_ATTEMPT_NUMBER = "fina2.security.allowedNumberLoginAttempt";
    public static final String ALLOWED_ACCOUNT_INACTIVITY_PERIOD = "fina2.security.allowedAccountInactivityPerioed";
    public static final String OLD_STORED_PASSWORDS_NUMBER = "fina2.security.numberOfStoredOldPasswords";
    public static final String MINIMAL_PASSWORD_LENGTH = "fina2.security.passwordMinimalLen";
    public static final String PASSWORD_WITH_NUMS = "fina2.security.passwordWithNums";
    public static final String PASSWORD_WITH_LETTERS = "fina2.security.passwordWithLetters";
    public static final String PASSWORD_WITH_LETTERS_UPPERCASE = "fina2.security.passwordWithLettersUpperCase";
    public static final String PASSWORD_WITH_SPECIAL_CHARACTERS = "fina2.security.passwordWithSpecialCharacters";
    public static final String PASSWORD_VALIDITY_PERIOD = "fina2.security.passwordValidityPeriod";
    public static final String IMPORT_THREADS_NUMBER = "fina2.returns.import.threadsNumber";
    public static final String FOLDER_LOCATION = "fina2.xml.folder.location";
    public static final String NAME_PATTERN = "fina2.mfb.xls.name.pattern";
    public static final String DEFAULT_PROTECTION_PASSWORD = "fina2.sheet.protection.password";
    public static final String PROTECTION_PASSWORDS = "fina2.sheet.protection.passwordByFiType";
    public static final String UPLOADED_FILE_UNIQUE = "fina2.mfb.uploaded.file.unique";
    public static final String UPLOADED_FILE_UNIQUE_STATUS = "fina2.mfb.uploaded.file.unique.status";
    public static final String UPDATE_START = "fina2.update.start";
    public static final String UPDATE_GUI_FILE_LOCATION = "fina2.update.gui.filelocation";
    public static final String CONVERTED_XMLS = "fina2.converted.xmls";
    public static final String MDT_RELEASE_VERSION = "net.fina.mdt.releaseVersion";

    public static final String FINA_AUTHENTICATED_MODES = "fina2.authenticatedModes";
    public static final String FINA_CURRENT_AUTHENTICATION = "fina2.current.authentication";
    public static final String LDAP_URL_IP = "fina2.authentication.ldap.urlIp";
    public static final String LDAP_URL_PORT = "fina2.authentication.ldap.urlPort";
    public static final String LDAP_ORGANIZATIONAL_UNIT = "fina2.authentication.ldap.organizationalUnit";
    public static final String LDAP_DOMAIN_COMPONENT = "fina2.authentication.ldap.domainComponent";
    public static final String LDAP_ADMIN_PASSWORD = "fina2.authentication.ldap.password";
    public static final String LDAP_AUTHENTICATION_SECURITY = "fina2.authentication.ldap.security";
    public static final String LDAP_ATTRIBUTE_FILTER = "fina2.authentication.ldap.attribute.filter";
    public static final String LDAP_LOGIN_ATTRIBUTE_NAME = "fina2.authentication.ldap.attribute.login";
    public static final String LDAP_SEARCH_SCOPE = "fina2.authentication.ldap.searchScope";
    public static final String LDAP_ATTRIBUTE_NAMES = "fina2.authentication.ldap.attributeNames";

    public static final String UPDATE_ADDIN = "fina2.update.addin";
    public static final String UPDATE_RUN_BAT = "fina2.update.runBat";
    public static final String UPDATE_RESOURCES = "fina2.update.resources";
    public static final String UPDATE_FINA_UPDATE = "fina2.update.finaUpdate";
    public static final String MAX_RETURNS_SIZE = "fina2.max.returns";
    public static final String MATRIX_PATH = "fina2.dcs.matrix.path";

    public static final String MAIL_USER = "mail.user";
    public static final String MAIL_PASSWORD = "mail.password";
    public static final String MAIL_ADDRESS = "mail.address";
    public static final String MAIL_NOT_READ = "fina2.mail.notReadMails";
    public static final String MAIL_SYNC = "fina2.mail.sync";
    public static final String MAIL_LAST_SYNC_STATUS = "fina2.mail.lastSyncStatus";

    // pop3
    public static final String MAIL_POP3_SSL_ENABLE = "mail.pop3.ssl.enable";
    public static final String MAIL_POP3_HOST = "mail.pop3.host";
    public static final String MAIL_POP3_PORT = "mail.pop3.port";
    public static final String MAIL_POP3_CONNECTION_TIMEUT = "mail.pop3.connectiontimeout";

    // smtp
    public static final String MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String MAIL_SMTP_CONNECTION_TIMEUT = "mail.smtp.connectiontimeout";
    public static final String MAIL_SMTP_STARTTSL_ENABLE = "mail.smtp.starttls.enable";
    public static final String MAIL_SSL_PROTOCOLS = "mail.ssl.protocols";
    public static final String MAIL_SMTP_EHLO = "mail.smtp.ehlo";
    public static final String MAIL_SMTP_AUTH = "fina2.mail.smtp.auth";
    public static final String MAIL_SEND_DISABLED = "fina2.mail.send.disabled";

    // imap
    public static final String MAIL_IMAP_SSL_ENABLE = "mail.imap.ssl.enable";
    public static final String MAIL_IMAP_HOST = "mail.imap.host";
    public static final String MAIL_IMAP_PORT = "mail.imap.port";
    public static final String MAIL_IMAP_CONNECTION_TIMEUT = "mail.imap.connectiontimeout";
    public static final String MAIL_IMAP_STARTTSL_ENABLE = "mail.imap.starttls.enable";
    public static final String MAIL_READ_DISABLED = "fina2.mail.read.disabled";


    // Exchange
    public static final String EXCHANGE_MAILBOX = "org.exjello.mail.mailbox";
    public static final String EXJELLO_MAIL_UNFILTERED = "org.exjello.mail.unfiltered";
    public static final String EXJELLO_MAIL_LIMIT = "org.exjello.mail.limit";
    public static final String EXJELLO_MAIL_DELETE = "org.exjello.mail.delete";

    // Mail Service
    public static final String MAIL_CHECK_INTERVAL = "fina2.dcs.mail.check.interval";
    public static final String LAST_READ_DATE = "fina2.mail.last.read.date";
    public static final String MAIL_CONNECTION_TYPE = "fina2.mail.connectionType";
    public static final String MAIL_SEND_RESPONCE_ENABLE = "fina2.mail.sendResponceEnable";
    public static final String MAIL_RESPONCE_CC = "fina2.mail.responceMailsCC";
    public static final String MAIL_RESPONCE_UNKNOWN_USER = "fina2.mail.responceUnknownUser";
    public static final String MAIL_CYCLED_MAX_SIZE = "fina2.mail.blockCycledMail.size";

    // process timeout
    public static final String PROCESS_TIMEOUT = "fina2.process.timeout";
    public static final String UNCHANGED_RETURN_STATUSES = "fina2.process.unchangedReturn.statuses";

    public static final String DEFAULT_LANGUAGE = "fina2.default.language.id";

    // Region Properties
    public static final String REGION_PROPETIES = "net.fina2.region.properties";
    public static final String REGION_PROPERTIES_MAXLEVEL = "fina2.regionstructuretree.maxlevel";
    public static final String REGION_PROPERTIES_LEVELNAMES = "fina2.regionstructuretree.levelname";

    //Database Schema Version
    public static final String DATABASE_SCHEMA_VERSION = "fina2.database.schemaVersion";

    public static final String VCT_EMPTY_LINES = "net.fina.converter.vct.emptyLines";

    //DCS file schedule due date control
    public static final String UPLOAD_FILE_DUE_DATE_CONTROL_ENABLE = "net.fina.dcs.uploadFile.dueDate.enable";
    //DCS file schedule date control
    public static final String UPLOAD_FILE_DATE_CONTROL_ENABLE = "net.fina.dcs.uploadFile.date.enable";

    //DCS Zip File one file control enable
    public static final String UPLOAD_FILE_ZIP_ONE_FILE_CONTROL = "net.fina.dcs.uploadFile.zipOneFileControl";

    public static final String UPLOAD_FILE_EXCEL_SHEET_CONTROL = "net.fina.dcs.uploadFile.excelSheetControl";

    // Process result mail enable
    public static final String PROCESSING_MAIL_ENABLE = "net.fina.processing.mail.enable";

    // Process result mail enable
    public static final String PROCESSING_PACKAGE_REJECT_ENABLE = "net.fina.processing.packageReject.enable";

    //Parallel file processing
    public static final String PARALLEL_FILE_PROCESSING_ENABLE = "net.fina.processing.parallel.enable";

    // Query Builder
    public static final String QUERY_BUILDER_URL = "net.fina.qb.url";

    // fina stat
    public static final String FINA_STAT_URL = "net.fina.stat.url";

    // audit log
    public static final String AUDIT_LOG_ENABLE = "fina2.auditlog.enable";

    // report audit log
    public static final String AUDIT_LOG_LEVEL = "net.fina.auditLog.level";

    public static final String AUDIT_LOG_SERVICE_INTERVAL = "fina2.auditlog.service.interval";

    public static final String AUDIT_LOG_SORT_DISABLED = "fina2.auditlog.sort.disabled";
    // theme state key
    public static final String APP_THEME_COOKIE_AND_STATE_ID = "net.fina.user.theme";

    // fi fax label
    public static final String FI_FAX_LABEL = "net.fina.fi.fax.label";

    // max number of days to perform audit log sort
    public static final String AUDIT_LOG_SORT_MAX_DAYS = "net.fina.audit.log.sort.max.days";


    // Service Monitor
    public static final String SERVICE_MONITOR_MAIL_SERVICE_STATUS_CURR = "net.fina.serviceMonitor.mailService.status.curr";

    public static final String SERVICE_MONITOR_MAIL_SERVICE_STATUS_LAST = "net.fina.serviceMonitor.mailService.status.last";

    public static final String SERVICE_MONITOR_MAIL_SERVICE_NEXT_FIRE_TIME = "net.fina.serviceMonitor.mailService.nextFireTime";

    public static final String RETURN_DEFINITION_DISABLE_FEATURE_ENABLE = "net.fina.returnDefinitionDisable.feature.enable";

    // ems
    public static final String EMS_EXPORT_CONTACT_PERSON_CODE = "net.fina.ems.exportContactPersonCode";
    public static final String EMS_EXPORT_BENEFICIAL_OWNER_CODE = "net.fina.ems.exportBeneficialOwnerCode";
    public static final String EMS_EXPORT_LICENSE_CODE = "net.fina.ems.exportBeneficialOwnerCode";

    public static final String DCS_RESTRICT_FILE_UPLOAD_WHILE_PROCESSING = "net.fina.dcs.fileUpload.restrictWhileProcessing";

    public static final String FILE_SIGNATURE_CHECKER_SIGNER_PROPERTY = "net.fina.dcs.signatureCheckerProperty";

    public static final String RETURN_DEFINITION_TEMPLATE_GENERATION_ENABLE = "net.fina.returnDefinition.templateGeneration.enable";

    // content storage
    public static final String CONTENT_STORAGE_PROVIDER = "net.fina.contentStorage.provider";
    public static final String CONTENT_STORAGE_REPOSITORY_PROVIDER = "net.fina.contentStorage.repository.provider";

    public static final String CATALOG_PARENT_FOLDER_NODE_CODE = "net.fina.catalog.parent.folder.node.code";
    public static final String CATALOG_TRASH_FOLDER_NODE_CODE = "net.fina.catalog.trash.folder.node.code";
    public static final String REG_STRING_COLUMN_MAX_LENGTH = "net.fina.reg.string.column.maxLength";
    public static final String REG_SHEET_PARALLEL_PROCESS_ENABLE = "net.fina.reg.sheet.parallel.process.enable";
    public static final String REG_INSERT_BATCH_SIZE = "net.fina.reg.insert.batch.size";
    public static final String REG_DELETE_BATCH_SIZE = "net.fina.reg.delete.batch.size";

    //SYS STRINGS MIGRATION PROPERTY
    public static final String SYS_STRINGS_DECODED = "net.fina.sys.strings.decoded";

    public static final String EMS_SYSTEM_IMPLEMENTATION_NAME = "net.fina.ems.system.impl.name";
    public static final String I18N_TRANSLATION_BUNDLE_VERSION = "net.fina.ui.i18n.translation.version";
    public static final String BLOCKED_EMAIL_DOMAINS = "net.fina.blocked.email.domains";
    public static final String USER_REQUIRED_FIELDS = "net.fina.user.requiredFields";
    public static final String RETURN_NOTIFICATION_PROVIDERS = "net.fina.return.status.notification.providers";

    public static final String OST_TAXONOMY_ENCRYPT_ENABLE = "net.fina.ost.taxonomy.encrypt.enable";
    public static final String PROCESSING_CROSS_FILE_VALIDATION_ENABLED = "net.fina.processing.cross.file.validation.enable";
    public static final String PROCESSING_TEMPLATE_PROCESSOR_VERSION = "net.fina.processing.template.processor.version";
    public static final String SCRIPT_ENGINE_PROVIDER = "net.fina.processing.script.engine.provider";

    public static final String MAIL_TENANT_ID = "fina2.mail.tenantId";

}
