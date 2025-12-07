package net.fina.common.shared.menu;

public enum Menus {

    ROOT("FinA:Root"),

    /**
     * Dashboard
     */
    DASHBOARD("net.fina.menu.dashboard"),

    /**
     * Administration
     */
    ADMINISTRATION("net.fina.web.menu.administration"),
    USERS("net.fina.menu.userManager"),
    LANGUAGES("net.fina.menu.languages"),
    NOTIFICATIONS("net.fina.menu.communicator.notifications"),
    MESSAGES("net.fina.menu.communicator.messages"),
    CONFIGURATION("net.fina.menu.settings"),

    /**
     * FI Institutions
     */
    FI_INSTITUTIONS("net.fina.financialInstitutions"),
    FIS("net.fina.fis"),
    LICENSE("net.fina.menu.licenseTypes"),
    FIGROUPS("net.fina.menu.figroups"),
    FITYPES("net.fina.menu.fitypes"),
    REGIONS("net.fina.menu.regionals"),
    MANAGEMENT("net.fina.menu.management"),
    EMS("net.fina.menu.ems"),
    FIRST("net.fina.menu.first"),
    TASK_MANAGER("net.fina.menu.taskManager"),
    DOCUMENT_MANAGEMENT("net.fina.menu.documentManagement"),
    ORGANIZATIONS_AND_INDIVIDUALS_REGISTRY("net.fina.menu.organizationsAndIndividualsRegistry"),
    FI_DOCUMENT_REQUEST_REGISTRY("net.fina.menu.fiDocumentRequestRegistry"),

    /**
     * Meta data
     */
    METADATA("net.fina.metaData"),
    MDT("net.fina.menu.mdt"),
    PTYPE("net.fina.periodType"),
    PDEFINITION("net.fina.menu.perioddefinition"),
    RTYPE("net.fina.menu.returnTypes"),
    RDEFINITION("net.fina.menu.returndefinition"),
    SCHEDULES("net.fina.menu.scheduledefinition"),
    COMPARISONS("net.fina.menu.comparisonrules.item"),
    RVERSION("net.fina.menu.returnVersion"),
    PACKAGE("net.fina.menu.ost.package"),

    /**
     * Processing
     */
    PROCESSING("net.fina.processing"),
    RMANAGER("net.fina.returnmanager.item"),
    RAUTOMATION("net.fina.returns.schedule.returns.item"),
    IMPORT("net.fina.importmanager.item"),
    RSEARCH("net.fina.returnmanager.search"),
    OVERDUE_RETURNS("net.fina.menu.returns.overdue"),
    INPUT_MANAGER("net.fina.menu.inputManager"),

    /**
     * Reporting
     */
    REPORTING("net.fina.reporting"),
    REPORTS("net.fina.reportmanager.menu.id"),
    RSCHEDULER("net.fina.menu.schedulermanager"),
    SREPORT("net.fina.menu.storedreportmanager"),
    BI_REPORTS("net.fina.menu.biReports"),

    /**
     * Tools
     */
    TOOLS("net.fina.tools"),
    SERVICE_MONITOR("net.fina.tools.serviceMonitor"),
    AUDIT_LOG("net.fina.tools.auditLog"),
    MAIL_LOG("net.fina.tools.mailLog"),
    CACHE_MANAGER("net.fina.tools.cacheManager"),
    MDT_TO_XML("net.fina.tools.mdtToXml"),
    RELEASE_MDT("net.fina.tools.releaseMdt"),
    RETURN_TO_XML("net.fina.tools.returnToXml"),
    MDT_GENERATOR("net.fina.tools.mdtGenerator"),
    FINA_FILE_DECRYPTION("net.fina.tools.finaFileDecryption"),
    SAIKU("net.fina.menu.queryBuilder"),
    LEGISLATIVE_DOCUMENT("net.fina.menu.legislativeDocument"),
    POSTBOX("net.fina.menu.postBox"),
    FAQ("net.fina.menu.faq"),
    BUNDLE_TRANSLATOR("net.fina.menu.bundleTranslation"),
    FEEDBACK("net.fina.menu.feedback"),

    /**
     * Help
     */
    HELP("net.fina.menu.help"),
    ABOUT("net.fina.menu.about"),

    FRAME("net.fina.menu.frame"),
    FRAME_EXTERNAL("net.fina.menu.frameExternal");

    private final String code;

    /**
     * Creates the instance of the type
     */
    private Menus(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
