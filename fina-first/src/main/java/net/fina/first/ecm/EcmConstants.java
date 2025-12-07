package net.fina.first.ecm;

public interface EcmConstants {

    // registry action
    String ACTION_TYPE_ACTION = "fina:fiRegistryAction";
    String ACTION_PROP_TYPE = "fina:fiRegistryActionType";
    String ACTION_PROP_AUTHOR = "fina:fiRegistryActionAuthor";
    String ACTION_PROP_IDENTITY = "fina:fiRegistryActionIdentity";
    String ACTION_PROP_RELEVANCE_TIME = "fina:fiRegistryActionRelevanceTime";
    String ACTION_PROP_PROCESS_ID = "fina:fiRegistryActionProcessId";
    String ACTION_PROP_CONTROL_STATUS = "fina:fiRegistryActionControlStatus";
    String ACTION_PROP_REDACTING_STATUS = "fina:fiRegistryActionRedactingStatus";
    String ACTION_PROP_DOCUMENTS_FOLDER_ID = "fina:fiRegistryActionDocumentsFolderId";
    String ACTION_PROP_CANCELLATION_IS_LIQUIDATOR_REQUIRED = "fina:fiCancellationIsLiquidatorRequired";
    String ACTION_PROP_CANCELLATION_REASON = "fina:fiCancellationReason";
    String ACTION_PROP_STEP = "fina:fiRegistryActionStep";
    String ACTION_PROP_FINAL_PROGRESS_STATUS = "fina:fiRegistryActionFinalProgressStatus";
    String ACTION_PROP_NUM_DAYS_FOR_GAP_CORRECTION = "fina:fiRegistryActionNumDaysToCorrectGaps";
    String ACTION_PROP_NUM_DAYS_TO_FINISH = "fina:fiRegistryActionNumDaysToFinish";

    // registry action questionnaire
    String ACTION_QUESTIONNAIRE_TYPE_QUESTIONNAIRE = "fina:fiRegistryActionQuestionnaire";
    String ACTION_QUESTIONNAIRE_PROP_STATUS = "fina:fiRegistryActionQuestionnaireStatus";
    String ACTION_QUESTIONNAIRE_PROP_NOTE = "fina:fiRegistryActionQuestionnaireNote";
    String ACTION_QUESTIONNAIRE_PROP_QUESTION = "fina:fiRegistryActionQuestionnaireQuestion";
    String ACTION_QUESTIONNAIRE_PROP_PREDEFINED = "fina:fiRegistryActionQuestionnairePredefined";
    String ACTION_QUESTIONNAIRE_PROP_OBLIGATORY = "fina:fiRegistryActionQuestionnaireObligatory";
    String ACTION_QUESTIONNAIRE_MODIFIED_AT = "modifiedAt";
    String ACTION_QUESTIONNAIRE_PARENT_ID = "fina:fiActionQuestionnaireParentId";
    String ACTION_QUESTIONNAIRE_GROUP_NAME = "fina:fiActionQuestionnaireGroupName";
    String ACTION_QUESTIONNAIRE_GROUP_CHECK_SIZE = "fina:fiActionQuestionnaireGroupCheckSize";
    String ACTION_QUESTIONNAIRE_SEQUENCE = "fina:fiActionQuestionnaireSequence";
    String ACTION_QUESTIONNAIRE_CODE = "fina:fiActionQuestionnaireCode";
    String QUESTIONNAIRE_TYPE_MAIN_TITLE = "1_QUESTIONNAIRE_TYPE_MAIN";
    String QUESTIONNAIRE_TYPE_EXTRA_TITLE = "2_QUESTIONNAIRE_TYPE_EXTRA";

    // extra questionnaire
    String QUESTIONNAIRE_EXTRA_NEW_PROP = "extraQuestionnaireNew";
    String QUESTIONNAIRE_EXTRA_UPDATED_PROP = "extraQuestionnaireUpdated";
    String QUESTIONNAIRE_EXTRA_REMOVED_PROP = "extraQuestionnaireRemoved";

    // registry
    String REGISTRY_TYPE_REGISTRY = "fina:fiRegistry";
    String REGISTRY_PROP_STATUS = "fina:fiRegistryStatus";
    String REGISTRY_PROP_ACTION_TYPE = "fina:fiActionType";
    String REGISTRY_PROP_CODE = "fina:fiRegistryCode";
    String REGISTRY_PROP_IDENTITY = "fina:fiRegistryIdentity";
    String REGISTRY_PROP_EMAIL = "fina:fiRegistryEmail";
    String REGISTRY_PROP_LEGAL_FORM_TYPE = "fina:fiRegistryLegalFormType";
    String REGISTRY_PROP_NAME = "fina:fiRegistryName";
    String REGISTRY_PROP_PHONE = "fina:fiRegistryPhone";
    String REGISTRY_PROP_REGISTRATION_DATE = "fina:fiRegistryRegistrationDate";
    String REGISTRY_PROP_WEBSITE = "fina:fiRegistryWebsite";
    String REGISTRY_PROP_LAST_PROCESS_ID = "fina:fiRegistryLastProcessId";
    String REGISTRY_PROP_LAST_ACTION_ID = "fina:fiRegistryLastActionId";
    String REGISTRY_PROP_LAST_ACTION_DATE = "fina:fiRegistryLastActionDate";
    String REGISTRY_ASSOC_FI_TYPE = "fina:fiRegistryFiType";
    String REGISTRY_PROP_TYPE_CODE = "fina:fiRegistryFiTypeCode";
    String REGISTRY_PROP_LAST_INSPECTOR_ID = "fina:fiRegistryLastInspectorId";
    String REGISTRY_PROP_LAST_EDITOR_ID = "fina:fiRegistryLastEditorId";
    String REGISTRY_PROP_LAST_EDITOR_FULL_NAME = "fina:fiRegistryLastEditorFullName";
    String REGISTRY_PROP_LICENSE_STATUS = "fina:fiRegistryLicenseStatus";
    String REGISTRY_PROP_IS_HISTORIC_DATA = "fina:fiRegistryIsHistoricData";
    String REGISTRY_PROP_HAS_REFUSAL_DOCUMENT = "fina:fiRegistryHasRefusalDocuments";
    String REGISTRY_PROP_ARCHIVED_GAP_TASK_COUNT = "fina:fiRegistryArchivedGapTaskCount";
    String REGISTRY_PROP_LEGAL_ADDRESS = "fina:fiRegistryLegalAddressAddress";
    String REGISTRY_PROP_LEGAL_ADDRESS_REGION = "fina:fiRegistryLegalAddressRegion";
    String REGISTRY_PROP_LEGAL_ADDRESS_CITY = "fina:fiRegistryLegalAddressCity";
    String REGISTRY_PROP_BINDER = "fina:fiRegistryBinder";
    String REGISTRY_PROP_ACT_NUMBER = "fina:fiRegistryLegalActNumber";
    String REGISTRY_PROP_ACT_DATE = "fina:fiRegistryLegalActDate";
    String REGISTRY_TASK_NUMBER = "fina:fiRegistryTaskNumber";
    String REGISTRY_TASK_RECIPE_DATE = "fina:fiRegistryTaskReceiptDate";
    String REGISTRY_FORCE_UPDATE_SCRIPT_EXECUTION = "fina:fiRegistryForceUpdateScriptExecution";


    //node common model
    String NODE_PROP_FI_PARENT_REGISTRY_ACTION_ID = "fina:fiParentRegistryActionId";
    String NODE_PROP_FI_REGISTRY_ACTION_ID = "fina:fiRegistryActionId";

    // registry branches
    String BRANCH_TYPE_BRANCH = "fina:fiRegistryBranch";
    String BRANCH_PROP_DELEGATION_PERSON = "fina:fiRegistryBranchDelegationPerson";
    String BRANCH_PROP_PHONE = "fina:fiRegistryBranchPhone";
    String BRANCH_PROP_TYPE = "fina:fiRegistryBranchType";
    String BRANCH_PROP_STATUS = "fina:fiRegistryBranchStatus";
    String BRANCH_PROP_FI_REGISTRY_ACTION_ID = "fina:fiRegistryActionId";
    String BRANCH_PROP_ADDRESS_REGION = "fina:fiRegistryBranchAddressRegion";
    String BRANCH_PROP_ADDRESS_CITY = "fina:fiRegistryBranchAddressCity";
    String BRANCH_PROP_ADDRESS_ADDRESS = "fina:fiRegistryBranchAddress";
    String BRANCH_PROP_LEGAL_ACT_NUMBER = "fina:fiRegistryBranchLegalActNumber";
    String BRANCH_PROP_LEGAL_ACT_DATE = "fina:fiRegistryBranchLegalActDate";
    String BRANCH_PROP_REGISTRATION_DATE = "fina:fiRegistryBranchRegistrationDate";
    String BRANCH_PROP_CANCELLATION_DATE = "fina:fiRegistryBranchCancellationDate";

    // registry placeholders
    String PLACEHOLDER_TYPE_PLACEHOLDER = "fina:fiRegistryPlaceholder";
    String PLACEHOLDER_PROP_ADDRESS = "fina:fiRegistryPlaceholderAddress";
    String PLACEHOLDER_PROP_CAPITAL_PERCENTAGE = "fina:fiRegistryPlaceholderCapitalPercentage";
    String PLACEHOLDER_PROP_CITIZENSHIP = "fina:fiRegistryPlaceholderCitizenship";
    String PLACEHOLDER_PROP_ID_NUMBER = "fina:fiRegistryPlaceholderIdNumber";
    String PLACEHOLDER_PROP_FIRST_NAME = "fina:fiRegistryPlaceholderFirstName";
    String PLACEHOLDER_PROP_LAST_NAME = "fina:fiRegistryPlaceholderLastName";
    String PLACEHOLDER_PROP_LEGAL_NAME = "fina:fiRegistryPlaceholderLegalName";
    String PLACEHOLDER_PROP_LEGAL_FORM_TYPE = "fina:fiRegistryPlaceholderLegalFormType";
    String PLACEHOLDER_PROP_TYPE = "fina:fiRegistryPlaceholderType";

    // nbg registry
    String REGISTRY_PROP_TASK_DATE = "fina:fiRegistryTaskReceiptDate";

    // registry documents
    String DOCUMENT_TYPE_DOCUMENT = "fina:fiDocument";
    String DECREE_DOCUMENT_TYPE = "fina:fiBranchDecreeDocument";
    String REFUSAL_LETTER_DOCUMENT_TYPE = "fina:fiBranchRefusalLetterDocument";
    String DOCUMENT_PROP_DOCUMENT_TYPE = "fina:fiDocumentType";
    String DOCUMENT_PROP_DOCUMENT_PROCESS_ID = "fina:fiDocumentProcessId";
    String DOCUMENT_PROP_DOCUMENT_NUMBER = "fina:fiDocumentNumber";
    String DOCUMENT_PROP_DOCUMENT_DATE = "fina:fiDocumentDate";
    String DOCUMENT_PROP_DOCUMENT_ACTION_ID = "fina:fiDocumentActionId";
    String DOCUMENT_PROP_DOCUMENT_IS_LAST_VERSION = "fina:fiDocumentIsLastVersion";
    String DOCUMENT_TYPE_DOCUMENT_LIST_VALUE_DOCUMENT = "DOCUMENT";
    String DOCUMENT_TYPE_DOCUMENT_LIST_VALUE_MEMORANDUM = "MEMORANDUM";
    String DOCUMENT_TYPE_DOCUMENT_LIST_VALUE_DECREE = "DECREE";
    String DOCUMENT_TYPE_DOCUMENT_LIST_VALUE_REPORT_CARD = "REPORT_CARD";
    String DOCUMENT_TYPE_DOCUMENT_LIST_VALUE_CONFIRMATION_LETTER = "CONFIRMATION_LETTER";
    String DOCUMENT_TYPE_DOCUMENT_LIST_VALUE_GAP_LETTER = "GAP_LETTER";
    String DOCUMENT_TYPE_DOCUMENT_LIST_VALUE_REFUSAL_LETTER = "REFUSAL_LETTER";
    String DOCUMENT_PROP_DOCUMENT_BRANCH_ID = "fina:fiBranchId";
    String DOCUMENT_PROP_CORRECTION_DEADLINE = "fina:fiDocumentCorrectionDeadline";
    String DOCUMENT_PROP_CORRECTION_DEADLINE_DAYS = "fina:fiDocumentCorrectionDeadlineDays";
    String DOCUMENT_PROP_DISPLAY_NAME = "fina:fiDocumentDisplayName";

    String REGISTRY_PERSON_TYPE = "fina:fiPerson";

    //complex structure
    String COMPLEX_STRUCTURE_TYPE = "fina:fiComplexStructure";
    String COMPLEX_STRUCTURE_PROP_TYPE = "fina:fiComplexStructureType";
    String COMPLEX_STRUCTURE_PROP_CAPITAL_PERCENTAGE = "fina:fiComplexStructureCapitalPercentage";
    String COMPLEX_STRUCTURE_STATUS_PHYSICAL = "PHYSICAL";
    String COMPLEX_STRUCTURE_STATUS_LEGAL = "LEGAL";
    String COMPLEX_STRUCTURE_LEGAL_TYPE_FUND = "fund";
    String COMPLEX_STRUCTURE_LEGAL_TYPE_UNION = "union";
    String COMPLEX_STRUCTURE_PROP_LEGAL_TYPE = "fina:fiComplexStructureLegalType";
    String COMPLEX_STRUCTURE_PROP_LEGAL_NAME = "fina:fiComplexStructureLegalName";
    String COMPLEX_STRUCTURE_PROP_IDENTIFICATION_NUMBER = "fina:fiComplexStructureIdentificationNumber";

    //Fi Person
    String FI_PERSON_PROP_FIRSTNAME = "fina:fiPersonFirstName";
    String FI_PERSON_PROP_LASTNAME = "fina:fiPersonLastName";
    String FI_PERSON_PROP_CITIZENSHIP = "fina:fiPersonCitizenship";
    String FI_PERSON_PROP_PERSONAL_NUMBER = "fina:fiPersonPersonalNumber";
    String FI_PERSON_PROP_PHONE = "fina:fiPersonPhone";
    String FI_PERSON_PROP_NON_RESIDENT_DOC_NUMBER = "fina:fiPersonNonResidentDocNumber";

    String COMMON_PROP_STATUS = "fina:status";
    String COMMON_PROP_STATUS_FINAL_STATUS = "fina:finalStatus";
    String COMMON_PROP_STATUS_ACTIVE = "ACTIVE";

    //Branch Changes
    String FI_BRANCH_CHANGE_TYPE = "fina:fiBranchesChange";
    String FI_BRANCH_REFERENCE_ID = "fina:fiBranchesChangeReferenceId";
    String FI_BRANCH_CHANGE_STATUS = "fina:fiBranchesChangeStatus";
    String FI_BRANCH_CHANGE_FINAL_STATUS = "fina:fiBranchesChangeFinalStatus";
    String FI_BRANCH_CHANGE_FINAL_STATUS_NOTE = "fina:fiBranchesChangeFinalStatusNote";
    String FI_BRANCH_CHANGE_DOCUMENT_REFERENCE_ID = "fina:fiBranchesChangeDocumentReferenceId";
    String FI_BRANCH_CHANGE_DECREE_DOCUMENT_REFERENCE_ID = "fina:finaBranchDecreeDocumentReferenceId";
    String FI_BRANCH_CHANGE_REFUSAL_LETTER_DOCUMENT_REFERENCE_ID = "fina:finaBranchRefusalLetterDocumentReferenceId";
    String FI_BRANCH_CHANGE_PRE_CHANGE_VERSION = "fina:fiBranchesChangePreChangeVersion";

    // FI changes
    String FI_MANAGEMENT_CHANGE_TYPE = "fina:fiManagementChange";
    String FI_MANAGEMENT_CHANGE_PROP_CHANGE_FORM_TYPE = "fina:fiChangeFormType";
    String FI_MANAGEMENT_CHANGE_PROP_CHANGE_REF_ID = "fina:fiManagementChangeReferenceId";
    String FI_MANAGEMENT_CHANGE_PROP_CHANGE_STATUS = "fina:fiManagementChangeStatus";

    //common
    String FI_FINE = "fina:fiFine";

    // liquidator
    String FI_LIQUIDATOR_PROP_REPORT_CARD_DOCUMENT = "fina:fiLiquidatorReportCardDocument";
    String FI_LIQUIDATOR_PROP_REFERENCE_ID = "fina:fiActionLiquidatorReferenceId";
    String FI_LIQUIDATOR_PROP_REPORT_CARD_REFERENCE_ID = "fina:fiActionLiquidatorReportCardReferenceId";

    // Fi Gaps
    String FI_GAP_TYPE = "fina:fiGap";
    String FI_GAP_PROP_OBJECT = "fina:fiGapObject";
    String FI_GAP_PROP_REASON = "fina:fiGapReason";
    String FI_GAP_PROP_CORRECTION_STATUS = "fina:fiGapCorrectionStatus";
    String FI_GAP_PROP_CORRECTION_COMMENT = "fina:fiGapCorrectionComment";
    String FI_GAP_PROP_OBJECT_ID = "fina:fiGapObjectId";
    String FI_GAP_PROP_DESCRIPTION = "fina:fiGapDescription";
    String FI_GAP_PROP_QUESTIONNAIRE_PROP_NAME = "fina:fiGapQuestionnairePropertyName";
    String FI_GAP_PROP_EXTRA_QUESTIONNAIRE_ID = "fina:fiGapExtraQuestionnaireId";
    String FI_FAP_PROP_IS_BASED_ON_QUESTIONNAIRE = "fina:fiGapIsBasedOnQuestionnaire";

    // FI Action Liquidator
    String FI_ACTION_LIQUIDATOR_TYPE = "fina:fiActionLiquidator";
    String FI_ACTION_LIQUIDATOR_PROP_LIQUIDATOR_TYPE = "fina:fiActionLiquidatorType";
    String FI_ACTION_LIQUIDATOR_PROP_LIQUIDATOR_ID = "fina:fiActionLiquidatorReferenceId";

    // FI Type
    String TYPE_FI_TYPE = "fina:fiType";
    String PROP_CODE = "fina:fiTypeCode";
    String PROP_DESCRIPTION = "fina:fiTypeDescription";
    String PROP_BRANCH_TYPES = "fina:fiTypeBranchTypes";
    String PROP_REGISTRATION_WORKFLOW_KEY = "fina:registrationWorkflowKey";
    String PROP_CHANGE_WORKFLOW_KEY = "fina:changeWorkflowKey";
    String PROP_DISABLE_WORKFLOW_KEY = "fina:disableWorkflowKey";
    String PROP_BRANCH_CHANGE_WORKFLOW_KEY = "fina:branchChangeWorkflowKey";
    String PROP_BRANCH_EDIT_WORKFLOW_KEY = "fina:branchEditWorkflowKey";
    String PROP_DOCUMENT_WITHDRAWAL_WORKFLOW_KEY = "fina:documentWithdrawalWorkflowKey";

    // FINA Questionnaire
    String QUESTIONNAIRE_TYPE_QUESTIONNAIRE = "fina:questionnaire";
    String QUESTIONNAIRE_PROP_QUESTION = "fina:questionnaireQuestion";
    String QUESTIONNAIRE_PROP_OBLIGATORY = "fina:questionnaireObligatory";
    String QUESTIONNAIRE_ASSOC_GROUP = "fina:questionnaireAssocGroup";
    String QUESTIONNAIRE_ASSOC_FI_TYPE = "fina:questionnaireAssocFiType";
    String QUESTIONNAIRE_PARENT_ID = "fina:questionnaireParentId";
    String QUESTIONNAIRE_GROUP_NAME = "fina:questionnaireGroupName";
    String QUESTIONNAIRE_GROUP_CHECK_SIZE = "fina:questionnaireGroupCheckSize";
    String QUESTIONNAIRE_SEQUENCE = "fina:questionnaireSequence";
    String QUESTIONNAIRE_ASSOCIATED_GROUP_CODE = "fina:questionnaireAssociatedGroupCode";
    String QUESTIONNAIRE_ASSOCIATED_FI_TYPE_CODE = "fina:questionnaireAssociatedFiTypeCode";
    String QUESTIONNAIRE_DEFAULT_VALUE = "fina:questionnaireDefaultValue";
    String QUESTIONNAIRE_CODE = "fina:questionnaireCode";

    String GROUP_TYPE_GROUP = "fina:questionnaireGroup";
    String GROUP_PROP_CODE = "fina:questionnaireGroupCode";
    String GROUP_PROP_DESCRIPTION = "fina:questionnaireGroupDescription";

    String FI_REGISTRY_ACTION_EXTERNAL_IS_SUBMIT_PROP_NAME = "fina:fiRegistryActionExternalInitiatorIsSubmited";
    String FI_REGISTRY_ACTION_EXTERNAL_INITIATOR_PROP_NAME = "fina:fiRegistryActionExternalInitiator";

    String PROPERTY_VALUE_NAME = "fina:propertyValue";

    // license / certificate registry
    String ORGANIZATION_INDIVIDUAL_REGISTRY_TYPE = "fina:organizationIndividualRegistry";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_NAME = "fina:organizationIndividualRegistryName";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_NAME_LATIN = "fina:organizationIndividualRegistryNameLatin";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_SURNAME = "fina:organizationIndividualRegistrySurname";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_TYPE = "fina:organizationIndividualRegistryType";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_TAX_ID = "fina:organizationIndividualRegistryTaxId";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_PERSONAL_ID = "fina:organizationIndividualRegistryPersonalId";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_STATE_REG_OR_DOC_NUMBER = "fina:organizationIndividualRegistryStateRegistryOrDocNumber";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_STATE_REG_OR_BIRTH_DATE = "fina:organizationIndividualRegistryStateRegistryOrBirthDate";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ADDRESS = "fina:organizationIndividualRegistryAddress";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ID_TYPE = "fina:organizationIndividualRegistryIdType";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ORGANIZATIONAL_FORM = "fina:organizationIndividualRegistryIdOrganizationalForm";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_GENDER = "fina:organizationIndividualRegistryGender";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_PHONE = "fina:organizationIndividualRegistryPhone";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_EMAIL = "fina:organizationIndividualRegistryEmail";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_EDUCATION = "fina:organizationIndividualRegistryEducation";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_WEBSITE = "fina:organizationIndividualRegistryWebsite";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ASSIGNMENT_DATE = "fina:organizationIndividualRegistryAssignmentDate";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_DOC_NUMBER = "fina:organizationIndividualRegistryDocNumber";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_POSITION = "fina:organizationIndividualRegistryPosition";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_BIRTH_PLACE = "fina:organizationIndividualRegistryBirthPlace";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_CITIZENSHIP = "fina:organizationIndividualRegistryCitizenship";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_COMMENTS = "fina:organizationIndividualRegistryComments";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_REGISTRY = "fina:organizationIndividualRegistryFromRegistry";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_REGISTRY_BRANCH = "fina:organizationIndividualRegistryFromRegistryBranch";
    String ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ATTESTATION_STATUS = "fina:organizationIndividualRegistryAttestationStatus";

    // license / certificate registry item
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_TYPE = "fina:organizationIndividualLicenseCertificate";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_UNIQUE_NUMBER = "fina:organizationIndividualLicenseCertificateId";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_STATUS = "fina:organizationIndividualLicenseCertificateStatus";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_ISSUE_DATE = "fina:organizationIndividualLicenseCertificateIssueDate";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_RESOLUTION_DOC_NUMBER = "fina:organizationIndividualLicenseCertificateNumberOfResolutionDoc";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_TYPE_ID = "fina:organizationIndividualLicenseCertificateTypeId";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_EXPIRATION_DATE = "fina:organizationIndividualLicenseCertificateExpirationDate";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_SUSPEND_DATE = "fina:organizationIndividualLicenseCertificateWithdrawSuspendDate";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_SUSPEND_REASON = "fina:organizationIndividualLicenseCertificateWithdrawSuspendReason";

    // license / certificate registry item
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_TYPE = "fina:organizationIndividualLicenseCertificateType";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_TYPE = "fina:organizationIndividualLicenseCertificateTypeType";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_NAME = "fina:organizationIndividualLicenseCertificateTypeName";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_IDENTIFIER = "fina:organizationIndividualLicenseCertificateTypeIdentifier";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_DOCUMENT_NUMBER = "fina:organizationIndividualLicenseCertificateTypeDocumentNumber";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_ALLOWED_OPS = "fina:organizationIndividualLicenseCertificateTypeAllowedOps";
    String ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_REGISTRATION_DATE = "fina:organizationIndividualLicenseCertificateTypeRegistrationDate";


    //report template
    String REPORT_TEMPLATE_PROP_REPORTNAME = "fina:reportName";
    String REPORT_TEMPLATE_PROP_STARTROW = "fina:startRow";
    String REPORT_TEMPLATE_PROP_STARTCOLUMN = "fina:startColumn";
    String REPORT_TEMPLATE_PROP_ROW_NUMBERING_ENABLE = "fina:enableRowNumbering";
    String REPORT_TEMPLATE_PROP_DATE_CELL_ADDRESS = "fina:dateCellAddress";

    // FI Document Request
    String FI_DOCUMENT_REQUEST_TYPE = "fina:fiDocumentRequest";
    String FI_DOCUMENT_REQUEST_PROP_NAME = "fina:fiDocumentRequestName";
    String FI_DOCUMENT_REQUEST_PROP_DESCRIPTION = "fina:fiDocumentRequestDescription";
    String FI_DOCUMENT_REQUEST_PROP_DUE_DATE = "fina:fiDocumentRequestDueDate";
    String FI_DOCUMENT_REQUEST_PROP_ASSIGNEE_FI_CODE = "fina:fiDocumentRequestAssigneeFiCode";
    String FI_DOCUMENT_REQUEST_PROP_IS_SUBMITTED = "fina:fiDocumentRequestIsSubmitted";
    String FI_DOCUMENT_REQUEST_PROP_SUBMISSION_DATE = "fina:fiDocumentRequestSubmissionDate";
    String FI_DOCUMENT_REQUEST_PROP_FI_ID = "fina:fiDocumentRequestFiId";
    String FI_DOCUMENT_REQUEST_PROP_COMMENT = "fina:fiDocumentRequestComment";
    String FI_DOCUMENT_REQUEST_PROP_FI_OBJECT = "fina:fiDocumentRequestFiObject";
    String FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPES = "fina:fiDocumentRequestFiObjectTypes";
    String FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE = "fina:fiDocumentRequestFiObjectType";
    String FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_NAME = "fina:fiDocumentRequestFiObjectName";
    String FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE_BRANCH = "BRANCH";
    String FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE_BENEFICIARY = "BENEFICIARY";
    String FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE_COMPLEX_STRUCTURE = "COMPLEX_STRUCTURE";

    // FI Registry Gap Detail
    String FI_REGISTRY_GAP_DETAIL_TYPE = "fina:fiRegistryGapDetail";
    String FI_REGISTRY_GAP_DETAIL_PROP_FI_REGISTRY_VERSION = "fina:fiRegistryGapRegistryVersion";
    String FI_REGISTRY_GAP_DETAIL_PROP_OBJECT_TYPE = "fina:fiRegistryGapDetailObjectType";
    String FI_REGISTRY_GAP_DETAIL_PROP_ACTION_ID = "fina:fiRegistryGapActionId";
    String FI_REGISTRY_GAP_DETAIL_PROP_ACTIVE = "fina:fiRegistryGapDetailActive";
    String FI_REGISTRY_GAP_DETAIL_PROP_CORRECTION_DATE = "fina:fiRegistryGapDetailCorrectionDate";
    String FI_REGISTRY_GAP_DETAIL_PROP_CORRECTION_DAYS = "fina:fiRegistryGapDetailCorrectionDays";
    String FI_REGISTRY_GAP_DETAIL_PROP_CORRECTION_LETTER_NUMBER = "fina:fiRegistryGapDetailCorrectionLetterNumber";
    String FI_REGISTRY_GAP_DETAIL_PROP_IS_DELETED = "fina:fiRegistryGapIsDeleted";
    String FI_REGISTRY_GAP_DETAIL_PROP_DELETE_COMMENT = "fina:fiRegistryGapDetailDeleteComment";
    String FI_REGISTRY_GAP_DETAIL_PROP_REGISTRY_ID = "fina:fiRegistryGapRegistryID";

    //FI SMS
    String FI_REGISTRY_SMS_TYPE = "fina:sms";

    //Regional Structure
    String REGIONAL_STRUCTURE_REGION_NAME = "fina:regionalStructureRegionName";
    String REGIONAL_STRUCTURE_CITY_NAME = "fina:regionalStructureCityName";

    //Notifications
    String NOTIFICATION_TYPE = "fina:notification";
    String NOTIFICATION_PROP_ADDRESSEE = "fina:notificationAddressee";
    String NOTIFICATION_PROP_SCHEDULED_SEND_DATE = "fina:notificationScheduledSendDate";
    String NOTIFICATION_PROP_IS_SENT = "fina:notificationIsSent";
    String NOTIFICATION_PROP_IS_GAP = "fina:notificationIsForGap";
    String NOTIFICATION_PROP_DEADLINE = "fina:notificationDeadline";
    String NOTIFICATION_PROP_ACTION_ID = "fina:notificationActionId";
    String NOTIFICATION_ASSOC_FI_REGISTRY = "fina:notificationFiRegistry";
    String NOTIFICATION_ASSOC_FI_REGISTRY_ACTION = "fina:notificationFiRegistryAction";

    String IGNORE_WARNINGS = "fina:ignoreWarnings";

}
