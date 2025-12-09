package net.fina.ecm.util;

public interface AlfrescoPropConstants {
    String FI_REGISTRY_ROOT_FOLDER_PATH_KEY = "fiRegistryRootFolderPath";
    String FI_QUESTIONNAIRE_ROOT_FOLDER_PATH_KEY = "fiQuestionnaireRootFolderPath";
    String FI_QUESTIONNAIRE_GROUPS_ROOT_FOLDER_PATH_KEY = "fiQuestionnaireGroupsRootFolderPath";
    String FI_TYPE_ROOT_FOLDER_PATH_KEY = "fiTypeRootFolderPath";
    String FI_REGISTRY_BRANCHES_FOLDER_NAME_KEY = "fiRegistryBranchesFolderName";
    String FI_REGISTRY_ACTIONS_FOLDER_NAME_KEY = "fiRegistryActionsFolderName";
    String FI_REGISTRY_AUTHORIZED_PERSONS_FOLDER_NAME_KEY = "fiRegistryAuthorizedPersonsName";
    String FI_REGISTRY_BENEFICIARIES_FOLDER_NAME_KEY = "fiRegistryBeneficiariesName";
    String FI_REGISTRY_COMPLEX_STRUCTURE_FOLDER_NAME_KEY = "fiRegistryComplexStructuresName";
    String FI_REGISTRY_SUB_ITEM_EXTRA_QUESTIONNAIRE_FOLDER_NAME_KEY = "fiRegistrySubItemExtraQuestionnaireFolderName";
    String FI_REGISTRY_DOCUMENTS_FOLDER_NAME_KEY = "fiRegistryDocumentsName";
    String FI_REGISTRY_ACTIONS_QUESTIONNAIRE_FOLDER_NAME_KEY = "fiRegistryActionQuestionnaireFolderName";
    String REGIONAL_STRUCTURE_FOLDER_PATH = "regionalStructureFolderPath";
    String MODEL_HIDDEN_FIELD_NAMES_KEY = "modelHiddenFieldNames";
    String MODEL_GRID_CHECK_PREFIXES_KEY = "modelGridCheckPrefixes";
    String BLACKLIST_ROOT_FOLDER_PATH_KEY = "blacklistRootFolderPath";
    String BLACKLIST_SEARCH_TEMPLATE = "blacklistSearchTemplate";
    String ATTESTATION_ROOT_FOLDER_PATH_KEY = "attestationRootFolderPath";
    String ATTESTATION_DOCUMENT_FOLDER_PATH_KEY = "attestationDocumentsFolderPath";
    String ATTESTATION_TEMPLATE_FOLDER_PATH_KEY = "attestationTemplateFolderPath";
    String USER_MANUAL_DOCUMENT_NODE_PATH_KEY = "userManualDocumentPath";
    String NOTIFICATIONS_FOLDER_PATH_KEY = "notificationsFolderPath";

    String CURRENT_USER_ROOT_NODE = "userRootNode";
    String CURRENT_USER = "currentUser";
    String ALFRESCO_DEFAULT_GROUP_NAME = "GROUP_EVERYONE";
    String ALFRESCO_ADMIN_GROUP_NAME = "GROUP_ALFRESCO_ADMINISTRATORS";
    String REGISTRY_SEARCH_TEMPLATE = "registrySearchTemplate";
    String REGISTRY_SEARCH_FIRST_LAST_NAME_TEMPLATE = "registrySearchFirstLastNameTemplate";
    String BASE_SEARCH_TEMPLATE = "baseSearchTemplate";
    String FI_REGISTRY_GAP_FOLDER_NAME_KEY = "Gaps";
    String SUPER_ADMIN_GROUP_NAME_KEY = "superAdminGroupName";
    String EDITOR_GROUP_NAME_KEY = "editorGroupName";
    String QUESTIONNAIRE_UNIQUE_NAME_PART_SEPARATOR_KEY = "questionnaireUniqueNamePartSeparator";

    String REPORT_CARD_TEMPLATE_PROP_NAME = "reportCardTemplateName";
    String REPORT_CARD_REFUSAL_TEMPLATE_PROP_NAME = "reportCardRefusalTemplateName";
    String DECREE_CARD_REFUSAL_TEMPLATE_PROP_NAME = "decreeCardRefusalTemplateName";
    String DECREE_TEMPLATE_PROP_NAME = "decreeTemplateName";
    String GAP_LETTER_TEMPLATE_PROP_NAME = "gapLetterTemplateName";
    String CONFIRMATION_LETTER_TEMPLATE_PROP_NAME = "confirmationLetterTemplateName";
    String REFUSAL_LETTER_TEMPLATE_PROP_NAME = "refusalLetterTemplateName";
    String REPORT_CARD_LIQUIDATOR_TEMPLATE_PROP_NAME = "reportCardLiquidatorTemplateName";
    String LETTER_LIQUIDATOR_TEMPLATE_PROP_NAME = "letterLiquidatorTemplateName";
    String LETTER_HERALD_TEMPLATE_PROP_NAME = "letterHeraldTemplateName";
    String LETTER_REGISTRY_TEMPLATE_PROP_NAME = "letterRegistryTemplateName";
    String REPORT_CARD_PUBLISH_WEBSITE_TEMPLATE_PROP_NAME = "reportCardPublishWebsiteTemplateName";
    String REPORT_CARD_DOCUMENT_WITHDRAWAL_TEMPLATE_PROP_NAME = "reportCardDocumentWithdrawalTemplateName";
    String DECREE_CARD_DOCUMENT_WITHDRAWAL_TEMPLATE_PROP_NAME = "decreeCardDocumentWithdrawalTemplateName";
    String LETTER_TO_THE_REPRESENTATIVE_TEMPLATE_PROP_NAME = "letterToTheRepresentativeTemplateName";
    String CANCELED_BRANCH_REPORT_CARD_TEMPLATE_PROP_NAME = "canceledBranchReportCardTemplateName";

    String ATTESTATION_SHEET_TEMPLATE_PROP_NAME = "attestationSheetTemplate";
    String ATTESTATION_REJECTION_LETTER_TEMPLATE_PROP_NAME = "attestationRejectionLetterTemplate";
    String ATTESTATION_REJECTION_INTERVIEW_LETTER_TEMPLATE_PROP_NAME = "attestationRejectionInterviewLetterTemplate";
    String ATTESTATION_CONFIRMATION_LETTER_TEMPLATE_PROP_NAME = "attestationConfirmationLetterTemplate";
    String ATTESTATION_INVITATION_LETTER_TEMPLATE_PROP_NAME = "attestationInvitationLetterTemplate";
    String ATTESTATION_BOARD_DECISION_DRAFT_TEMPLATE_PROP_NAME = "attestationBoardDecisionDraftTemplate";
    String ATTESTATION_IN_QUEUE_CANDIDATES_SHORT_TEMPLATE_PROP_NAME = "attestationInQueueCandidatesShortTemplate";
    String ATTESTATION_IN_QUEUE_CANDIDATES_LONG_TEMPLATE_PROP_NAME = "attestationInQueueCandidatesLongTemplate";
    String ATTESTATION_SEARCH_TEMPLATE_PROP_NAME = "attestationSearchTemplate";

    // data types
    String DATA_TYPE_FI_TYPE_KEY = "dataTypeFiType";
    String DATA_TYPE_FI_REGISTRIES_KEY = "dataTypeFiRegistries";
    String DATA_TYPE_REGIONAL_STRUCTURE_REGION = "dataTypeRegionalStructureRegion";
    String DATA_TYPE_REGIONAL_STRUCTURE_CITY = "dataTypeRegionalStructureCity";
    String DATA_TYPE_COMPLEX_STRUCTURE_KEY = "dataTypeComplexStructures";
    String DATA_TYPE_ATTESTATION_KEY = "dataTypeAttestation";
    String DATA_TYPE_BLACKLIST_KEY = "dataTypeBlacklist";

    //report document model
    String DATA_TYPE_REPORT_DOCUMENT = "fina:reportDocument";
    String REPORT_DOCUMENT_PROP_NOTIFICATION_MAILS = "fina:notificationMails";

    String DOCUMENT_REPOSITORY_HIDDEN_WORKFLOW_KEYS_KEY = "documentRepositoryHiddenWorkflowKeys";

    String RENDITION_DEFAULT_ID_KEY = "renditionDefaultId";
    String RENDITION_NOT_REQUIRED_MIME_TYPES_KEY = "nonRenditionMimeTypes";

    String AOS_REMOTE_URL_KEY = "aosRemoteUrl";

    String BRANCH_CHANGE_WORKFLOW_KEY = "branchChangeWorkflowKey";
    String BRANCH_EDIT_WORKFLOW_KEY = "branchEditWorkflowKey";

    String ORGANIZATION_INDIVIDUAL_REGISTRY_ROOT_FOLDER_PATH_KEY = "organizationIndividualRegistryRootFolderPath";
    String ORGANIZATION_INDIVIDUAL_LICENSE_TYPES_FOLDER_PATH_KEY = "certificateLicenseTypesRootFolderPath";

    String REPORT_TEMPLATES_FOLDER_PATH = "reportTemplatesFolderPath";

    String FI_DOCUMENT_REQUEST_ROOT_FOLDER_PATH_KEY = "fiDocumentRequestRootFolderPath";

    String SOLR_SERVER_URL = "solrServerUrl";

    String ORGANIZATION_INDIVIDUALS_QUERY_TEMPLATE = "organizationalIndividualsQueryTemplate";
    String ORGANIZATION_INDIVIDUALS_LICENSE_QUERY_TEMPLATE = "organizationalIndividualsLicenseQueryTemplate";

    String EXISTING_BRANCH_TYPES = "existingBranchTypes";

    String IS_ECM_ENABLE = "isEcmEnable";
    String EXISTING_BRANCH_SUBDIVISION_FI_TYPES = "existingBranchSubdivisionFiTypes";
    String EXISTING_BRANCH_HEAD_OFFICE_FI_TYPES = "existingBranchHeadOfficeFiTypes";

    String COUNTRY_MAP_LAYER_NODE_PATH_KEY = "countryMapLayerNodePath";

    String TEMPLATE_WEB_SCRIPT_LOCATOR_ENABLE = "templateWebScriptLocatorEnable";

    String TEMPLATE_WEB_SCRIPT_PATH_KEY = "webScriptPath";
}
