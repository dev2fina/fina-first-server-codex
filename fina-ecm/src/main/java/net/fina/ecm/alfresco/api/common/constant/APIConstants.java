package net.fina.ecm.alfresco.api.common.constant;

public interface APIConstants {
    String PROP_AUTO_VERSION_ON_UPDATE_PROPS = "cm:autoVersionOnUpdateProps";

    String FOLDER_ROOT = "-root-";

    String FOLDER_MY = "-my-";

    String FOLDER_SHARED = "-shared-";

    String SKIP_COUNT_VALUE = "skipCount";

    String ORDER_BY_VALUE = "orderBy";

    String MAX_ITEMS_VALUE = "maxItems";

    String MAX_RESULTS_VALUE = "maxResults";

    String AUTO_RENAME_VALUE = "autoRename";

    String MAJOR_VERSION_VALUE = "majorVersion";

    String INCLUDE_VALUE = "include";

    String RELATIONS_VALUE = "relations";

    String FIELDS_VALUE = "fields";

    String PROPERTIES_VALUE = "properties";

    String PERMISSIONS_VALUE = "permissions";

    String RELATIVE_PATH_VALUE = "relativePath";

    String WHERE_VALUE = "where";

    String INCLUDE_SOURCE_VALUE = "includeSource";

    String PERMANENT_VALUE = "permanent";

    String PROCESS_DEFINITION_ID = "processDefinitionId";

    String PROCESS_ID = "processId";

    String MULTIPART_FILE_DATA = "filedata";

    // web services
    String PREFIX_PUBLIC_API = "api/-default-/public/";

    String CORE_PUBLIC_API_V1 = PREFIX_PUBLIC_API + "alfresco/versions/1";

    String SEARCH_PUBLIC_API_V1 = PREFIX_PUBLIC_API + "search/versions/1";

    String WORKFLOW_PUBLIC_API_V1 = PREFIX_PUBLIC_API + "workflow/versions/1";

    String SERVICE_API = "/service/api";

    String WEBSCRIPT_API = "s";

    int CMIS_MAX_PAGE_SIZE = 100000;
}
