
IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'cellcolumn')
    BEGIN
        insert into SYS_TRANSLATIONS values ('cellcolumn', 'en_US', N'Cell Column');
        insert into SYS_TRANSLATIONS values ('cellcolumn', 'ka_GE', N'Cell Column');
        insert into SYS_TRANSLATIONS values ('cellcolumn', 'kg_KG', N'Cell Column');
        insert into SYS_TRANSLATIONS values ('cellcolumn', 'ru_RU', N'Cell Column');
        insert into SYS_TRANSLATIONS values ('cellcolumn', 'tj_TJ', N'Cell Column');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'cellobject')
    BEGIN
        insert into SYS_TRANSLATIONS values ('cellobject', 'en_US', N'Cell Object');
        insert into SYS_TRANSLATIONS values ('cellobject', 'ka_GE', N'Cell Object');
        insert into SYS_TRANSLATIONS values ('cellobject', 'kg_KG', N'Cell Object');
        insert into SYS_TRANSLATIONS values ('cellobject', 'ru_RU', N'Cell Object');
        insert into SYS_TRANSLATIONS values ('cellobject', 'tj_TJ', N'Cell Object');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'cellobjectfield')
    BEGIN
        insert into SYS_TRANSLATIONS values ('cellobjectfield', 'en_US', N'Cell Object Field');
        insert into SYS_TRANSLATIONS values ('cellobjectfield', 'ka_GE', N'Cell Object Field');
        insert into SYS_TRANSLATIONS values ('cellobjectfield', 'kg_KG', N'Cell Object Field');
        insert into SYS_TRANSLATIONS values ('cellobjectfield', 'ru_RU', N'Cell Object Field');
        insert into SYS_TRANSLATIONS values ('cellobjectfield', 'tj_TJ', N'Cell Object Field');
    END;


IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'finetype')
    BEGIN
        insert into SYS_TRANSLATIONS values ('finetype', 'en_US', N'Fine Type');
        insert into SYS_TRANSLATIONS values ('finetype', 'ka_GE', N'Fine Type');
        insert into SYS_TRANSLATIONS values ('finetype', 'kg_KG', N'Fine Type');
        insert into SYS_TRANSLATIONS values ('finetype', 'ru_RU', N'Fine Type');
        insert into SYS_TRANSLATIONS values ('finetype', 'tj_TJ', N'Fine Type');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'fineprice')
    BEGIN
        insert into SYS_TRANSLATIONS values ('fineprice', 'en_US', N'Fine Price');
        insert into SYS_TRANSLATIONS values ('fineprice', 'ka_GE', N'Fine Price');
        insert into SYS_TRANSLATIONS values ('fineprice', 'kg_KG', N'Fine Price');
        insert into SYS_TRANSLATIONS values ('fineprice', 'ru_RU', N'Fine Price');
        insert into SYS_TRANSLATIONS values ('fineprice', 'tj_TJ', N'Fine Price');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'type')
    BEGIN
        insert into SYS_TRANSLATIONS values ('type', 'en_US', N'Type');
        insert into SYS_TRANSLATIONS values ('type', 'ka_GE', N'Type');
        insert into SYS_TRANSLATIONS values ('type', 'kg_KG', N'Type');
        insert into SYS_TRANSLATIONS values ('type', 'ru_RU', N'Type');
        insert into SYS_TRANSLATIONS values ('type', 'tj_TJ', N'Type');
    END;


IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'mandatory')
    BEGIN
        insert into SYS_TRANSLATIONS values ('mandatory', 'en_US', N'Mandatory');
        insert into SYS_TRANSLATIONS values ('mandatory', 'ka_GE', N'Mandatory');
        insert into SYS_TRANSLATIONS values ('mandatory', 'kg_KG', N'Mandatory');
        insert into SYS_TRANSLATIONS values ('mandatory', 'ru_RU', N'Mandatory');
        insert into SYS_TRANSLATIONS values ('mandatory', 'tj_TJ', N'Mandatory');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'optional')
    BEGIN
        insert into SYS_TRANSLATIONS values ('optional', 'en_US', N'Optional');
        insert into SYS_TRANSLATIONS values ('optional', 'ka_GE', N'Optional');
        insert into SYS_TRANSLATIONS values ('optional', 'kg_KG', N'Optional');
        insert into SYS_TRANSLATIONS values ('optional', 'ru_RU', N'Optional');
        insert into SYS_TRANSLATIONS values ('optional', 'tj_TJ', N'Optional');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'cellFormat')
    BEGIN
        insert into SYS_TRANSLATIONS values ('cellFormat', 'en_US', N'Cell Format');
        insert into SYS_TRANSLATIONS values ('cellFormat', 'ka_GE', N'Cell Format');
        insert into SYS_TRANSLATIONS values ('cellFormat', 'kg_KG', N'Cell Format');
        insert into SYS_TRANSLATIONS values ('cellFormat', 'ru_RU', N'Cell Format');
        insert into SYS_TRANSLATIONS values ('cellFormat', 'tj_TJ', N'Cell Format');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'imposed')
    BEGIN
        insert into SYS_TRANSLATIONS values ('imposed', 'en_US', N'IMPOSED');
        insert into SYS_TRANSLATIONS values ('imposed', 'ka_GE', N'IMPOSED');
        insert into SYS_TRANSLATIONS values ('imposed', 'kg_KG', N'IMPOSED');
        insert into SYS_TRANSLATIONS values ('imposed', 'ru_RU', N'IMPOSED');
        insert into SYS_TRANSLATIONS values ('imposed', 'tj_TJ', N'IMPOSED');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'fulfilled')
    BEGIN
        insert into SYS_TRANSLATIONS values ('fulfilled', 'en_US', N'FULFILLED');
        insert into SYS_TRANSLATIONS values ('fulfilled', 'ka_GE', N'FULFILLED');
        insert into SYS_TRANSLATIONS values ('fulfilled', 'kg_KG', N'FULFILLED');
        insert into SYS_TRANSLATIONS values ('fulfilled', 'ru_RU', N'FULFILLED');
        insert into SYS_TRANSLATIONS values ('fulfilled', 'tj_TJ', N'FULFILLED');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'appealed')
    BEGIN
        insert into SYS_TRANSLATIONS values ('appealed', 'en_US', N'APPEALED');
        insert into SYS_TRANSLATIONS values ('appealed', 'ka_GE', N'APPEALED');
        insert into SYS_TRANSLATIONS values ('appealed', 'kg_KG', N'APPEALED');
        insert into SYS_TRANSLATIONS values ('appealed', 'ru_RU', N'APPEALED');
        insert into SYS_TRANSLATIONS values ('appealed', 'tj_TJ', N'APPEALED');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'paid')
    BEGIN
        insert into SYS_TRANSLATIONS values ('paid', 'en_US', N'PAID');
        insert into SYS_TRANSLATIONS values ('paid', 'ka_GE', N'PAID');
        insert into SYS_TRANSLATIONS values ('paid', 'kg_KG', N'PAID');
        insert into SYS_TRANSLATIONS values ('paid', 'ru_RU', N'PAID');
        insert into SYS_TRANSLATIONS values ('paid', 'tj_TJ', N'PAID');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'unpaid')
    BEGIN
        insert into SYS_TRANSLATIONS values ('unpaid', 'en_US', N'UNPAID');
        insert into SYS_TRANSLATIONS values ('unpaid', 'ka_GE', N'UNPAID');
        insert into SYS_TRANSLATIONS values ('unpaid', 'kg_KG', N'UNPAID');
        insert into SYS_TRANSLATIONS values ('unpaid', 'ru_RU', N'UNPAID');
        insert into SYS_TRANSLATIONS values ('unpaid', 'tj_TJ', N'UNPAID');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'annueld')
    BEGIN
        insert into SYS_TRANSLATIONS values ('annueld', 'en_US', N'ANNULLED');
        insert into SYS_TRANSLATIONS values ('annueld', 'ka_GE', N'ANNULLED');
        insert into SYS_TRANSLATIONS values ('annueld', 'kg_KG', N'ANNULLED');
        insert into SYS_TRANSLATIONS values ('annueld', 'ru_RU', N'ANNULLED');
        insert into SYS_TRANSLATIONS values ('annueld', 'tj_TJ', N'ANNULLED');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'deliverydate')
    BEGIN
        insert into SYS_TRANSLATIONS values ('deliverydate', 'en_US', N'Delivery Date');
        insert into SYS_TRANSLATIONS values ('deliverydate', 'ka_GE', N'Delivery Date');
        insert into SYS_TRANSLATIONS values ('deliverydate', 'kg_KG', N'Delivery Date');
        insert into SYS_TRANSLATIONS values ('deliverydate', 'ru_RU', N'Delivery Date');
        insert into SYS_TRANSLATIONS values ('deliverydate', 'tj_TJ', N'Delivery Date');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'executiondate')
    BEGIN
        insert into SYS_TRANSLATIONS values ('executiondate', 'en_US', N'Execution Date');
        insert into SYS_TRANSLATIONS values ('executiondate', 'ka_GE', N'Execution Date');
        insert into SYS_TRANSLATIONS values ('executiondate', 'kg_KG', N'Execution Date');
        insert into SYS_TRANSLATIONS values ('executiondate', 'ru_RU', N'Execution Date');
        insert into SYS_TRANSLATIONS values ('executiondate', 'tj_TJ', N'Execution Date');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'finame')
    BEGIN
        insert into SYS_TRANSLATIONS values ('finame', 'en_US', N'Fi Name');
        insert into SYS_TRANSLATIONS values ('finame', 'ka_GE', N'Fi Name');
        insert into SYS_TRANSLATIONS values ('finame', 'kg_KG', N'Fi Name');
        insert into SYS_TRANSLATIONS values ('finame', 'ru_RU', N'Fi Name');
        insert into SYS_TRANSLATIONS values ('finame', 'tj_TJ', N'Fi Name');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'selectfi')
    BEGIN
        insert into SYS_TRANSLATIONS values ('selectfi', 'en_US', N'Select Fi');
        insert into SYS_TRANSLATIONS values ('selectfi', 'ka_GE', N'Select Fi');
        insert into SYS_TRANSLATIONS values ('selectfi', 'kg_KG', N'Select Fi');
        insert into SYS_TRANSLATIONS values ('selectfi', 'ru_RU', N'Select Fi');
        insert into SYS_TRANSLATIONS values ('selectfi', 'tj_TJ', N'Select Fi');
    END;


IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'deadlinefrom')
    BEGIN
        insert into SYS_TRANSLATIONS values ('deadlinefrom', 'en_US', N'Deadline From');
        insert into SYS_TRANSLATIONS values ('deadlinefrom', 'ka_GE', N'Deadline From');
        insert into SYS_TRANSLATIONS values ('deadlinefrom', 'kg_KG', N'Deadline From');
        insert into SYS_TRANSLATIONS values ('deadlinefrom', 'ru_RU', N'Deadline From');
        insert into SYS_TRANSLATIONS values ('deadlinefrom', 'tj_TJ', N'Deadline From');
    END;


IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'deadlineto')
    BEGIN
        insert into SYS_TRANSLATIONS values ('deadlineto', 'en_US', N'Deadline To');
        insert into SYS_TRANSLATIONS values ('deadlineto', 'ka_GE', N'Deadline To');
        insert into SYS_TRANSLATIONS values ('deadlineto', 'kg_KG', N'Deadline To');
        insert into SYS_TRANSLATIONS values ('deadlineto', 'ru_RU', N'Deadline To');
        insert into SYS_TRANSLATIONS values ('deadlineto', 'tj_TJ', N'Deadline To');
    END;


IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'decreenumber')
    BEGIN
        insert into SYS_TRANSLATIONS values ('decreenumber', 'en_US', N'Decree Number');
        insert into SYS_TRANSLATIONS values ('decreenumber', 'ka_GE', N'Decree Number');
        insert into SYS_TRANSLATIONS values ('decreenumber', 'kg_KG', N'Decree Number');
        insert into SYS_TRANSLATIONS values ('decreenumber', 'ru_RU', N'Decree Number');
        insert into SYS_TRANSLATIONS values ('decreenumber', 'tj_TJ', N'Decree Number');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'reclamationletternumber')
    BEGIN
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'en_US', N'Reclamation Latter Number');
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'ka_GE', N'Reclamation Latter Number');
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'kg_KG', N'Reclamation Latter Number');
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'ru_RU', N'Reclamation Latter Number');
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'tj_TJ', N'Reclamation Latter Number');
    END;

-- For key 'conclusion' and value 'Conclusion'
IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'conclusion')
    BEGIN
        insert into SYS_TRANSLATIONS values ('conclusion', 'en_US', N'Conclusion');
        insert into SYS_TRANSLATIONS values ('conclusion', 'ka_GE', N'Conclusion');
        insert into SYS_TRANSLATIONS values ('conclusion', 'kg_KG', N'Conclusion');
        insert into SYS_TRANSLATIONS values ('conclusion', 'ru_RU', N'Conclusion');
        insert into SYS_TRANSLATIONS values ('conclusion', 'tj_TJ', N'Conclusion');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'termextension')
    BEGIN
        insert into SYS_TRANSLATIONS values ('termextension', 'en_US', N'Term Extension');
        insert into SYS_TRANSLATIONS values ('termextension', 'ka_GE', N'Term Extension');
        insert into SYS_TRANSLATIONS values ('termextension', 'kg_KG', N'Term Extension');
        insert into SYS_TRANSLATIONS values ('termextension', 'ru_RU', N'Term Extension');
        insert into SYS_TRANSLATIONS values ('termextension', 'tj_TJ', N'Term Extension');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'fined')
    BEGIN
        insert into SYS_TRANSLATIONS values ('fined', 'en_US', N'Fined');
        insert into SYS_TRANSLATIONS values ('fined', 'ka_GE', N'Fined');
        insert into SYS_TRANSLATIONS values ('fined', 'kg_KG', N'Fined');
        insert into SYS_TRANSLATIONS values ('fined', 'ru_RU', N'Fined');
        insert into SYS_TRANSLATIONS values ('fined', 'tj_TJ', N'Fined');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'sentletter')
    BEGIN
        insert into SYS_TRANSLATIONS values ('sentletter', 'en_US', N'Sent Letter');
        insert into SYS_TRANSLATIONS values ('sentletter', 'ka_GE', N'Sent Letter');
        insert into SYS_TRANSLATIONS values ('sentletter', 'kg_KG', N'Sent Letter');
        insert into SYS_TRANSLATIONS values ('sentletter', 'ru_RU', N'Sent Letter');
        insert into SYS_TRANSLATIONS values ('sentletter', 'tj_TJ', N'Sent Letter');
    END;


IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'meeting')
    BEGIN
        insert into SYS_TRANSLATIONS values ('meeting', 'en_US', N'Meeting');
        insert into SYS_TRANSLATIONS values ('meeting', 'ka_GE', N'Meeting');
        insert into SYS_TRANSLATIONS values ('meeting', 'kg_KG', N'Meeting');
        insert into SYS_TRANSLATIONS values ('meeting', 'ru_RU', N'Meeting');
        insert into SYS_TRANSLATIONS values ('meeting', 'tj_TJ', N'Meeting');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'onsiteinspection')
    BEGIN
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'en_US', N'Onsite Inspection');
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'ka_GE', N'Onsite Inspection');
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'kg_KG', N'Onsite Inspection');
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'ru_RU', N'Onsite Inspection');
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'tj_TJ', N'Onsite Inspection');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'pending')
    BEGIN
        insert into SYS_TRANSLATIONS values ('pending', 'en_US', N'Pending');
        insert into SYS_TRANSLATIONS values ('pending', 'ka_GE', N'Pending');
        insert into SYS_TRANSLATIONS values ('pending', 'kg_KG', N'Pending');
        insert into SYS_TRANSLATIONS values ('pending', 'ru_RU', N'Pending');
        insert into SYS_TRANSLATIONS values ('pending', 'tj_TJ', N'Pending');
    END;


IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'other')
    BEGIN
        insert into SYS_TRANSLATIONS values ('other', 'en_US', N'Other');
        insert into SYS_TRANSLATIONS values ('other', 'ka_GE', N'Other');
        insert into SYS_TRANSLATIONS values ('other', 'kg_KG', N'Other');
        insert into SYS_TRANSLATIONS values ('other', 'ru_RU', N'Other');
        insert into SYS_TRANSLATIONS values ('other', 'tj_TJ', N'Other');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'notaccepted')
    BEGIN
        insert into SYS_TRANSLATIONS values ('notaccepted', 'en_US', N'Not Accepted');
        insert into SYS_TRANSLATIONS values ('notaccepted', 'ka_GE', N'Not Accepted');
        insert into SYS_TRANSLATIONS values ('notaccepted', 'kg_KG', N'Not Accepted');
        insert into SYS_TRANSLATIONS values ('notaccepted', 'ru_RU', N'Not Accepted');
        insert into SYS_TRANSLATIONS values ('notaccepted', 'tj_TJ', N'Not Accepted');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'done')
    BEGIN
        insert into SYS_TRANSLATIONS values ('done', 'en_US', N'Done');
        insert into SYS_TRANSLATIONS values ('done', 'ka_GE', N'Done');
        insert into SYS_TRANSLATIONS values ('done', 'kg_KG', N'Done');
        insert into SYS_TRANSLATIONS values ('done', 'ru_RU', N'Done');
        insert into SYS_TRANSLATIONS values ('done', 'tj_TJ', N'Done');
    END;


IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'fiprofile')
    BEGIN
        insert into SYS_TRANSLATIONS values ('fiprofile', 'en_US', N'FI Profile');
        insert into SYS_TRANSLATIONS values ('fiprofile', 'ka_GE', N'FI Profile');
        insert into SYS_TRANSLATIONS values ('fiprofile', 'kg_KG', N'FI Profile');
        insert into SYS_TRANSLATIONS values ('fiprofile', 'ru_RU', N'FI Profile');
        insert into SYS_TRANSLATIONS values ('fiprofile', 'tj_TJ', N'FI Profile');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'inspectiontypes')
    BEGIN
        insert into SYS_TRANSLATIONS values ('inspectiontypes', 'en_US', N'Inspection Types');
        insert into SYS_TRANSLATIONS values ('inspectiontypes', 'ka_GE', N'Inspection Types');
        insert into SYS_TRANSLATIONS values ('inspectiontypes', 'kg_KG', N'Inspection Types');
        insert into SYS_TRANSLATIONS values ('inspectiontypes', 'ru_RU', N'Inspection Types');
        insert into SYS_TRANSLATIONS values ('inspectiontypes', 'tj_TJ', N'Inspection Types');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'sanctionandrecommendationtypes')
    BEGIN
        insert into SYS_TRANSLATIONS
        values ('sanctionandrecommendationtypes', 'en_US', N'Sanction And Recommendation Types');
        insert into SYS_TRANSLATIONS
        values ('sanctionandrecommendationtypes', 'ka_GE', N'Sanction And Recommendation Types');
        insert into SYS_TRANSLATIONS
        values ('sanctionandrecommendationtypes', 'kg_KG', N'Sanction And Recommendation Types');
        insert into SYS_TRANSLATIONS
        values ('sanctionandrecommendationtypes', 'ru_RU', N'Sanction And Recommendation Types');
        insert into SYS_TRANSLATIONS
        values ('sanctionandrecommendationtypes', 'tj_TJ', N'Sanction And Recommendation Types');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'finetypes')
    BEGIN
        insert into SYS_TRANSLATIONS values ('finetypes', 'en_US', N'Fine Types');
        insert into SYS_TRANSLATIONS values ('finetypes', 'ka_GE', N'Fine Types');
        insert into SYS_TRANSLATIONS values ('finetypes', 'kg_KG', N'Fine Types');
        insert into SYS_TRANSLATIONS values ('finetypes', 'ru_RU', N'Fine Types');
        insert into SYS_TRANSLATIONS values ('finetypes', 'tj_TJ', N'Fine Types');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'inspectioncolumns')
    BEGIN
        insert into SYS_TRANSLATIONS values ('inspectioncolumns', 'en_US', N'Inspection Columns');
        insert into SYS_TRANSLATIONS values ('inspectioncolumns', 'ka_GE', N'Inspection Columns');
        insert into SYS_TRANSLATIONS values ('inspectioncolumns', 'kg_KG', N'Inspection Columns');
        insert into SYS_TRANSLATIONS values ('inspectioncolumns', 'ru_RU', N'Inspection Columns');
        insert into SYS_TRANSLATIONS values ('inspectioncolumns', 'tj_TJ', N'Inspection Columns');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'recommendations')
    BEGIN
        insert into SYS_TRANSLATIONS values ('recommendations', 'en_US', N'Recommendations');
        insert into SYS_TRANSLATIONS values ('recommendations', 'ka_GE', N'Recommendations');
        insert into SYS_TRANSLATIONS values ('recommendations', 'kg_KG', N'Recommendations');
        insert into SYS_TRANSLATIONS values ('recommendations', 'ru_RU', N'Recommendations');
        insert into SYS_TRANSLATIONS values ('recommendations', 'tj_TJ', N'Recommendations');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'fileconfiguration')
    BEGIN
        insert into SYS_TRANSLATIONS values ('fileconfiguration', 'en_US', N'File Configuration');
        insert into SYS_TRANSLATIONS values ('fileconfiguration', 'ka_GE', N'File Configuration');
        insert into SYS_TRANSLATIONS values ('fileconfiguration', 'kg_KG', N'File Configuration');
        insert into SYS_TRANSLATIONS values ('fileconfiguration', 'ru_RU', N'File Configuration');
        insert into SYS_TRANSLATIONS values ('fileconfiguration', 'tj_TJ', N'File Configuration');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'importfile')
    BEGIN
        insert into SYS_TRANSLATIONS values ('importfile', 'en_US', N'Import File');
        insert into SYS_TRANSLATIONS values ('importfile', 'ka_GE', N'Import File');
        insert into SYS_TRANSLATIONS values ('importfile', 'kg_KG', N'Import File');
        insert into SYS_TRANSLATIONS values ('importfile', 'ru_RU', N'Import File');
        insert into SYS_TRANSLATIONS values ('importfile', 'tj_TJ', N'Import File');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'followup')
    BEGIN
        insert into SYS_TRANSLATIONS values ('followup', 'en_US', N'Follow Up');
        insert into SYS_TRANSLATIONS values ('followup', 'ka_GE', N'Follow Up');
        insert into SYS_TRANSLATIONS values ('followup', 'kg_KG', N'Follow Up');
        insert into SYS_TRANSLATIONS values ('followup', 'ru_RU', N'Follow Up');
        insert into SYS_TRANSLATIONS values ('followup', 'tj_TJ', N'Follow Up');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'reclamationletternumber')
    BEGIN
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'en_US', N'Reclamation Letter Number');
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'ka_GE', N'Reclamation Letter Number');
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'kg_KG', N'Reclamation Letter Number');
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'ru_RU', N'Reclamation Letter Number');
        insert into SYS_TRANSLATIONS values ('reclamationletternumber', 'tj_TJ', N'Reclamation Letter Number');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'inspectionperiodstart')
    BEGIN
        insert into SYS_TRANSLATIONS values ('inspectionperiodstart', 'en_US', N'Inspection Period Start');
        insert into SYS_TRANSLATIONS values ('inspectionperiodstart', 'ka_GE', N'Inspection Period Start');
        insert into SYS_TRANSLATIONS values ('inspectionperiodstart', 'kg_KG', N'Inspection Period Start');
        insert into SYS_TRANSLATIONS values ('inspectionperiodstart', 'ru_RU', N'Inspection Period Start');
        insert into SYS_TRANSLATIONS values ('inspectionperiodstart', 'tj_TJ', N'Inspection Period Start');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'inspectionperiodend')
    BEGIN
        insert into SYS_TRANSLATIONS values ('inspectionperiodend', 'en_US', N'Inspection Period End');
        insert into SYS_TRANSLATIONS values ('inspectionperiodend', 'ka_GE', N'Inspection Period End');
        insert into SYS_TRANSLATIONS values ('inspectionperiodend', 'kg_KG', N'Inspection Period End');
        insert into SYS_TRANSLATIONS values ('inspectionperiodend', 'ru_RU', N'Inspection Period End');
        insert into SYS_TRANSLATIONS values ('inspectionperiodend', 'tj_TJ', N'Inspection Period End');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'inspectedperiodstart')
    BEGIN
        insert into SYS_TRANSLATIONS values ('inspectedperiodstart', 'en_US', N'Inspected Period Start');
        insert into SYS_TRANSLATIONS values ('inspectedperiodstart', 'ka_GE', N'Inspected Period Start');
        insert into SYS_TRANSLATIONS values ('inspectedperiodstart', 'kg_KG', N'Inspected Period Start');
        insert into SYS_TRANSLATIONS values ('inspectedperiodstart', 'ru_RU', N'Inspected Period Start');
        insert into SYS_TRANSLATIONS values ('inspectedperiodstart', 'tj_TJ', N'Inspected Period Start');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'inspectedperiodend')
    BEGIN
        insert into SYS_TRANSLATIONS values ('inspectedperiodend', 'en_US', N'Inspected Period End');
        insert into SYS_TRANSLATIONS values ('inspectedperiodend', 'ka_GE', N'Inspected Period End');
        insert into SYS_TRANSLATIONS values ('inspectedperiodend', 'kg_KG', N'Inspected Period End');
        insert into SYS_TRANSLATIONS values ('inspectedperiodend', 'ru_RU', N'Inspected Period End');
        insert into SYS_TRANSLATIONS values ('inspectedperiodend', 'tj_TJ', N'Inspected Period End');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'decreenumber')
    BEGIN
        insert into SYS_TRANSLATIONS values ('decreenumber', 'en_US', N'Decree Number');
        insert into SYS_TRANSLATIONS values ('decreenumber', 'ka_GE', N'Decree Number');
        insert into SYS_TRANSLATIONS values ('decreenumber', 'kg_KG', N'Decree Number');
        insert into SYS_TRANSLATIONS values ('decreenumber', 'ru_RU', N'Decree Number');
        insert into SYS_TRANSLATIONS values ('decreenumber', 'tj_TJ', N'Decree Number');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'decreedate')
    BEGIN
        insert into SYS_TRANSLATIONS values ('decreedate', 'en_US', N'Decree Date');
        insert into SYS_TRANSLATIONS values ('decreedate', 'ka_GE', N'Decree Date');
        insert into SYS_TRANSLATIONS values ('decreedate', 'kg_KG', N'Decree Date');
        insert into SYS_TRANSLATIONS values ('decreedate', 'ru_RU', N'Decree Date');
        insert into SYS_TRANSLATIONS values ('decreedate', 'tj_TJ', N'Decree Date');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'types')
    BEGIN
        insert into SYS_TRANSLATIONS values ('types', 'en_US', N'Types');
        insert into SYS_TRANSLATIONS values ('types', 'ka_GE', N'Types');
        insert into SYS_TRANSLATIONS values ('types', 'kg_KG', N'Types');
        insert into SYS_TRANSLATIONS values ('types', 'ru_RU', N'Types');
        insert into SYS_TRANSLATIONS values ('types', 'tj_TJ', N'Types');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'recommendationmailsent')
    BEGIN
        insert into SYS_TRANSLATIONS values ('recommendationmailsent', 'en_US', N'Recommendation Mail Sent');
        insert into SYS_TRANSLATIONS values ('recommendationmailsent', 'ka_GE', N'Recommendation Mail Sent');
        insert into SYS_TRANSLATIONS values ('recommendationmailsent', 'kg_KG', N'Recommendation Mail Sent');
        insert into SYS_TRANSLATIONS values ('recommendationmailsent', 'ru_RU', N'Recommendation Mail Sent');
        insert into SYS_TRANSLATIONS values ('recommendationmailsent', 'tj_TJ', N'Recommendation Mail Sent');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'ongoing')
    BEGIN
        insert into SYS_TRANSLATIONS values ('ongoing', 'en_US', N'On Going');
        insert into SYS_TRANSLATIONS values ('ongoing', 'ka_GE', N'On Going');
        insert into SYS_TRANSLATIONS values ('ongoing', 'kg_KG', N'On Going');
        insert into SYS_TRANSLATIONS values ('ongoing', 'ru_RU', N'On Going');
        insert into SYS_TRANSLATIONS values ('ongoing', 'tj_TJ', N'On Going');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'totalsize')
    BEGIN
        insert into SYS_TRANSLATIONS values ('totalsize', 'en_US', N'Total Size');
        insert into SYS_TRANSLATIONS values ('totalsize', 'ka_GE', N'Total Size');
        insert into SYS_TRANSLATIONS values ('totalsize', 'kg_KG', N'Total Size');
        insert into SYS_TRANSLATIONS values ('totalsize', 'ru_RU', N'Total Size');
        insert into SYS_TRANSLATIONS values ('totalsize', 'tj_TJ', N'Total Size');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'menu')
    BEGIN
        insert into SYS_TRANSLATIONS values ('menu', 'en_US', N'Menu');
        insert into SYS_TRANSLATIONS values ('menu', 'ka_GE', N'Menu');
        insert into SYS_TRANSLATIONS values ('menu', 'kg_KG', N'Menu');
        insert into SYS_TRANSLATIONS values ('menu', 'ru_RU', N'Menu');
        insert into SYS_TRANSLATIONS values ('menu', 'tj_TJ', N'Menu');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'main')
    BEGIN
        insert into SYS_TRANSLATIONS values ('main', 'en_US', N'Main');
        insert into SYS_TRANSLATIONS values ('main', 'ka_GE', N'Main');
        insert into SYS_TRANSLATIONS values ('main', 'kg_KG', N'Main');
        insert into SYS_TRANSLATIONS values ('main', 'ru_RU', N'Main');
        insert into SYS_TRANSLATIONS values ('main', 'tj_TJ', N'Main');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'simpleareachart')
    BEGIN
        insert into SYS_TRANSLATIONS values ('simpleareachart', 'en_US', N'Simple Area Chart');
        insert into SYS_TRANSLATIONS values ('simpleareachart', 'ka_GE', N'Simple Area Chart');
        insert into SYS_TRANSLATIONS values ('simpleareachart', 'kg_KG', N'Simple Area Chart');
        insert into SYS_TRANSLATIONS values ('simpleareachart', 'ru_RU', N'Simple Area Chart');
        insert into SYS_TRANSLATIONS values ('simpleareachart', 'tj_TJ', N'Simple Area Chart');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'simplebarchart')
    BEGIN
        insert into SYS_TRANSLATIONS values ('simplebarchart', 'en_US', N'Simple Bar Chart');
        insert into SYS_TRANSLATIONS values ('simplebarchart', 'ka_GE', N'Simple Bar Chart');
        insert into SYS_TRANSLATIONS values ('simplebarchart', 'kg_KG', N'Simple Bar Chart');
        insert into SYS_TRANSLATIONS values ('simplebarchart', 'ru_RU', N'Simple Bar Chart');
        insert into SYS_TRANSLATIONS values ('simplebarchart', 'tj_TJ', N'Simple Bar Chart');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'simplelinechart')
    BEGIN
        insert into SYS_TRANSLATIONS values ('simplelinechart', 'en_US', N'Simple Line Chart');
        insert into SYS_TRANSLATIONS values ('simplelinechart', 'ka_GE', N'Simple Line Chart');
        insert into SYS_TRANSLATIONS values ('simplelinechart', 'kg_KG', N'Simple Line Chart');
        insert into SYS_TRANSLATIONS values ('simplelinechart', 'ru_RU', N'Simple Line Chart');
        insert into SYS_TRANSLATIONS values ('simplelinechart', 'tj_TJ', N'Simple Line Chart');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'stackedareachart')
    BEGIN
        insert into SYS_TRANSLATIONS values ('stackedareachart', 'en_US', N'Stacked Area Chart');
        insert into SYS_TRANSLATIONS values ('stackedareachart', 'ka_GE', N'Stacked Area Chart');
        insert into SYS_TRANSLATIONS values ('stackedareachart', 'kg_KG', N'Stacked Area Chart');
        insert into SYS_TRANSLATIONS values ('stackedareachart', 'ru_RU', N'Stacked Area Chart');
        insert into SYS_TRANSLATIONS values ('stackedareachart', 'tj_TJ', N'Stacked Area Chart');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'stackedbarchart')
    BEGIN
        insert into SYS_TRANSLATIONS values ('stackedbarchart', 'en_US', N'Stacked Bar Chart');
        insert into SYS_TRANSLATIONS values ('stackedbarchart', 'ka_GE', N'Stacked Bar Chart');
        insert into SYS_TRANSLATIONS values ('stackedbarchart', 'kg_KG', N'Stacked Bar Chart');
        insert into SYS_TRANSLATIONS values ('stackedbarchart', 'ru_RU', N'Stacked Bar Chart');
        insert into SYS_TRANSLATIONS values ('stackedbarchart', 'tj_TJ', N'Stacked Bar Chart');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'inprogress')
    BEGIN
        insert into SYS_TRANSLATIONS values ('inprogress', 'en_US', N'In Progress');
        insert into SYS_TRANSLATIONS values ('inprogress', 'ka_GE', N'In Progress');
        insert into SYS_TRANSLATIONS values ('inprogress', 'kg_KG', N'In Progress');
        insert into SYS_TRANSLATIONS values ('inprogress', 'ru_RU', N'In Progress');
        insert into SYS_TRANSLATIONS values ('inprogress', 'tj_TJ', N'In Progress');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'completed')
    BEGIN
        insert into SYS_TRANSLATIONS values ('completed', 'en_US', N'Completed');
        insert into SYS_TRANSLATIONS values ('completed', 'ka_GE', N'Completed');
        insert into SYS_TRANSLATIONS values ('completed', 'kg_KG', N'Completed');
        insert into SYS_TRANSLATIONS values ('completed', 'ru_RU', N'Completed');
        insert into SYS_TRANSLATIONS values ('completed', 'tj_TJ', N'Completed');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'unfulfilled')
    BEGIN
        insert into SYS_TRANSLATIONS values ('unfulfilled', 'en_US', N'Unfulfilled');
        insert into SYS_TRANSLATIONS values ('unfulfilled', 'ka_GE', N'Unfulfilled');
        insert into SYS_TRANSLATIONS values ('unfulfilled', 'kg_KG', N'Unfulfilled');
        insert into SYS_TRANSLATIONS values ('unfulfilled', 'ru_RU', N'Unfulfilled');
        insert into SYS_TRANSLATIONS values ('unfulfilled', 'tj_TJ', N'Unfulfilled');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'partiallycompleted')
    BEGIN
        insert into SYS_TRANSLATIONS values ('partiallycompleted', 'en_US', N'Partially Completed');
        insert into SYS_TRANSLATIONS values ('partiallycompleted', 'ka_GE', N'Partially Completed');
        insert into SYS_TRANSLATIONS values ('partiallycompleted', 'kg_KG', N'Partially Completed');
        insert into SYS_TRANSLATIONS values ('partiallycompleted', 'ru_RU', N'Partially Completed');
        insert into SYS_TRANSLATIONS values ('partiallycompleted', 'tj_TJ', N'Partially Completed');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'conclusion')
    BEGIN
        insert into SYS_TRANSLATIONS values ('conclusion', 'en_US', N'Conclusion');
        insert into SYS_TRANSLATIONS values ('conclusion', 'ka_GE', N'Conclusion');
        insert into SYS_TRANSLATIONS values ('conclusion', 'kg_KG', N'Conclusion');
        insert into SYS_TRANSLATIONS values ('conclusion', 'ru_RU', N'Conclusion');
        insert into SYS_TRANSLATIONS values ('conclusion', 'tj_TJ', N'Conclusion');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'termextension')
    BEGIN
        insert into SYS_TRANSLATIONS values ('termextension', 'en_US', N'Term Extension');
        insert into SYS_TRANSLATIONS values ('termextension', 'ka_GE', N'Term Extension');
        insert into SYS_TRANSLATIONS values ('termextension', 'kg_KG', N'Term Extension');
        insert into SYS_TRANSLATIONS values ('termextension', 'ru_RU', N'Term Extension');
        insert into SYS_TRANSLATIONS values ('termextension', 'tj_TJ', N'Term Extension');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'fined')
    BEGIN
        insert into SYS_TRANSLATIONS values ('fined', 'en_US', N'Fined');
        insert into SYS_TRANSLATIONS values ('fined', 'ka_GE', N'Fined');
        insert into SYS_TRANSLATIONS values ('fined', 'kg_KG', N'Fined');
        insert into SYS_TRANSLATIONS values ('fined', 'ru_RU', N'Fined');
        insert into SYS_TRANSLATIONS values ('fined', 'tj_TJ', N'Fined');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'sentletter')
    BEGIN
        insert into SYS_TRANSLATIONS values ('sentletter', 'en_US', N'Sent Letter');
        insert into SYS_TRANSLATIONS values ('sentletter', 'ka_GE', N'Sent Letter');
        insert into SYS_TRANSLATIONS values ('sentletter', 'kg_KG', N'Sent Letter');
        insert into SYS_TRANSLATIONS values ('sentletter', 'ru_RU', N'Sent Letter');
        insert into SYS_TRANSLATIONS values ('sentletter', 'tj_TJ', N'Sent Letter');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'meeting')
    BEGIN
        insert into SYS_TRANSLATIONS values ('meeting', 'en_US', N'Meeting');
        insert into SYS_TRANSLATIONS values ('meeting', 'ka_GE', N'Meeting');
        insert into SYS_TRANSLATIONS values ('meeting', 'kg_KG', N'Meeting');
        insert into SYS_TRANSLATIONS values ('meeting', 'ru_RU', N'Meeting');
        insert into SYS_TRANSLATIONS values ('meeting', 'tj_TJ', N'Meeting');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'onsiteinspection')
    BEGIN
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'en_US', N'Onsite Inspection');
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'ka_GE', N'Onsite Inspection');
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'kg_KG', N'Onsite Inspection');
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'ru_RU', N'Onsite Inspection');
        insert into SYS_TRANSLATIONS values ('onsiteinspection', 'tj_TJ', N'Onsite Inspection');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'needsrespond')
    BEGIN
        insert into SYS_TRANSLATIONS values ('needsrespond', 'en_US', N'Needs Respond');
        insert into SYS_TRANSLATIONS values ('needsrespond', 'ka_GE', N'Needs Respond');
        insert into SYS_TRANSLATIONS values ('needsrespond', 'kg_KG', N'Needs Respond');
        insert into SYS_TRANSLATIONS values ('needsrespond', 'ru_RU', N'Needs Respond');
        insert into SYS_TRANSLATIONS values ('needsrespond', 'tj_TJ', N'Needs Respond');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'pending')
    BEGIN
        insert into SYS_TRANSLATIONS values ('pending', 'en_US', N'Pending');
        insert into SYS_TRANSLATIONS values ('pending', 'ka_GE', N'Pending');
        insert into SYS_TRANSLATIONS values ('pending', 'kg_KG', N'Pending');
        insert into SYS_TRANSLATIONS values ('pending', 'ru_RU', N'Pending');
        insert into SYS_TRANSLATIONS values ('pending', 'tj_TJ', N'Pending');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'other')
    BEGIN
        insert into SYS_TRANSLATIONS values ('other', 'en_US', N'Other');
        insert into SYS_TRANSLATIONS values ('other', 'ka_GE', N'Other');
        insert into SYS_TRANSLATIONS values ('other', 'kg_KG', N'Other');
        insert into SYS_TRANSLATIONS values ('other', 'ru_RU', N'Other');
        insert into SYS_TRANSLATIONS values ('other', 'tj_TJ', N'Other');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'notaccepted')
    BEGIN
        insert into SYS_TRANSLATIONS values ('notaccepted', 'en_US', N'Not Accepted');
        insert into SYS_TRANSLATIONS values ('notaccepted', 'ka_GE', N'Not Accepted');
        insert into SYS_TRANSLATIONS values ('notaccepted', 'kg_KG', N'Not Accepted');
        insert into SYS_TRANSLATIONS values ('notaccepted', 'ru_RU', N'Not Accepted');
        insert into SYS_TRANSLATIONS values ('notaccepted', 'tj_TJ', N'Not Accepted');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'done')
    BEGIN
        insert into SYS_TRANSLATIONS values ('done', 'en_US', N'Done');
        insert into SYS_TRANSLATIONS values ('done', 'ka_GE', N'Done');
        insert into SYS_TRANSLATIONS values ('done', 'kg_KG', N'Done');
        insert into SYS_TRANSLATIONS values ('done', 'ru_RU', N'Done');
        insert into SYS_TRANSLATIONS values ('done', 'tj_TJ', N'Done');
    END;


IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'existingperoddefinitions')
    BEGIN
        insert into SYS_TRANSLATIONS
        values ('existingperoddefinitions', 'en_US', N'Following period definitions already exists...');
        insert into SYS_TRANSLATIONS
        values ('existingperoddefinitions', 'ka_GE', N'Following period definitions already exists...');
        insert into SYS_TRANSLATIONS
        values ('existingperoddefinitions', 'kg_KG', N'Following period definitions already exists...');
        insert into SYS_TRANSLATIONS
        values ('existingperoddefinitions', 'ru_RU', N'Following period definitions already exists...');
        insert into SYS_TRANSLATIONS
        values ('existingperoddefinitions', 'tj_TJ', N'Following period definitions already exists...');
    END;
IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'created')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('created', 'en_US', N'Created');
        INSERT INTO SYS_TRANSLATIONS VALUES ('created', 'ka_GE', N'Created');
        INSERT INTO SYS_TRANSLATIONS VALUES ('created', 'kg_KG', N'Created');
        INSERT INTO SYS_TRANSLATIONS VALUES ('created', 'ru_RU', N'Created');
        INSERT INTO SYS_TRANSLATIONS VALUES ('created', 'tj_TJ', N'Created');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'inbox')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('inbox', 'en_US', N'Inbox');
        INSERT INTO SYS_TRANSLATIONS VALUES ('inbox', 'ka_GE', N'Inbox');
        INSERT INTO SYS_TRANSLATIONS VALUES ('inbox', 'kg_KG', N'Inbox');
        INSERT INTO SYS_TRANSLATIONS VALUES ('inbox', 'ru_RU', N'Inbox');
        INSERT INTO SYS_TRANSLATIONS VALUES ('inbox', 'tj_TJ', N'Inbox');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'outbox')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('outbox', 'en_US', N'Outbox');
        INSERT INTO SYS_TRANSLATIONS VALUES ('outbox', 'ka_GE', N'Outbox');
        INSERT INTO SYS_TRANSLATIONS VALUES ('outbox', 'kg_KG', N'Outbox');
        INSERT INTO SYS_TRANSLATIONS VALUES ('outbox', 'ru_RU', N'Outbox');
        INSERT INTO SYS_TRANSLATIONS VALUES ('outbox', 'tj_TJ', N'Outbox');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'rejected')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('rejected', 'en_US', N'Rejected');
        INSERT INTO SYS_TRANSLATIONS VALUES ('rejected', 'ka_GE', N'Rejected');
        INSERT INTO SYS_TRANSLATIONS VALUES ('rejected', 'kg_KG', N'Rejected');
        INSERT INTO SYS_TRANSLATIONS VALUES ('rejected', 'ru_RU', N'Rejected');
        INSERT INTO SYS_TRANSLATIONS VALUES ('rejected', 'tj_TJ', N'Rejected');
    END;


IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'subparagraph')
    BEGIN
        insert into SYS_TRANSLATIONS values ('subparagraph', 'en_US', N'Sub-Paragraph');
        insert into SYS_TRANSLATIONS values ('subparagraph', 'ka_GE', N'ქვე-პუნქტი');
        insert into SYS_TRANSLATIONS values ('subparagraph', 'kg_KG', N'Sub-Paragraph');
        insert into SYS_TRANSLATIONS values ('subparagraph', 'ru_RU', N'Sub-Paragraph');
        insert into SYS_TRANSLATIONS values ('subparagraph', 'tj_TJ', N'Sub-Paragraph');
    END;
IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'paragraph')
    BEGIN
        insert into SYS_TRANSLATIONS values ('paragraph', 'en_US', N'Paragraph');
        insert into SYS_TRANSLATIONS values ('paragraph', 'ka_GE', N'პუნქტი');
        insert into SYS_TRANSLATIONS values ('paragraph', 'kg_KG', N'Paragraph');
        insert into SYS_TRANSLATIONS values ('paragraph', 'ru_RU', N'Paragraph');
        insert into SYS_TRANSLATIONS values ('paragraph', 'tj_TJ', N'Paragraph');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'article')
    BEGIN
        insert into SYS_TRANSLATIONS values ('article', 'en_US', N'Article');
        insert into SYS_TRANSLATIONS values ('article', 'ka_GE', N'მუხლი');
        insert into SYS_TRANSLATIONS values ('article', 'kg_KG', N'Article');
        insert into SYS_TRANSLATIONS values ('article', 'ru_RU', N'Article');
        insert into SYS_TRANSLATIONS values ('article', 'tj_TJ', N'Article');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'rule')
    BEGIN
        insert into SYS_TRANSLATIONS values ('rule', 'en_US', N'Rule');
        insert into SYS_TRANSLATIONS values ('rule', 'ka_GE', N'წესი');
        insert into SYS_TRANSLATIONS values ('rule', 'kg_KG', N'Rule');
        insert into SYS_TRANSLATIONS values ('rule', 'ru_RU', N'Rule');
        insert into SYS_TRANSLATIONS values ('rule', 'tj_TJ', N'Rule');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'fineprice')
    BEGIN
        insert into SYS_TRANSLATIONS values ('fineprice', 'en_US', N'Price');
        insert into SYS_TRANSLATIONS values ('fineprice', 'ka_GE', N'ჯარიმის ფასი');
        insert into SYS_TRANSLATIONS values ('fineprice', 'kg_KG', N'Price');
        insert into SYS_TRANSLATIONS values ('fineprice', 'ru_RU', N'Price');
        insert into SYS_TRANSLATIONS values ('fineprice', 'tj_TJ', N'Price');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'finetypes')
    BEGIN
        insert into SYS_TRANSLATIONS values ('finetypes', 'en_US', N'Fine Types');
        insert into SYS_TRANSLATIONS values ('finetypes', 'ka_GE', N'ჯარიმის ტიპები');
        insert into SYS_TRANSLATIONS values ('finetypes', 'kg_KG', N'Fine Types');
        insert into SYS_TRANSLATIONS values ('finetypes', 'ru_RU', N'Fine Types');
        insert into SYS_TRANSLATIONS values ('finetypes', 'tj_TJ', N'Fine Types');
    END;

IF
    NOT EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'inpectioncolumnalreadyexists')
    BEGIN
        insert into SYS_TRANSLATIONS
        values ('inpectioncolumnalreadyexists', 'en_US', N'Inspection Column Already Exists');
        insert into SYS_TRANSLATIONS
        values ('inpectioncolumnalreadyexists', 'ka_GE', N'Inspection Column Already Exists');
        insert into SYS_TRANSLATIONS
        values ('inpectioncolumnalreadyexists', 'kg_KG', N'Inspection Column Already Exists');
        insert into SYS_TRANSLATIONS
        values ('inpectioncolumnalreadyexists', 'ru_RU', N'Inspection Column Already Exists');
        insert into SYS_TRANSLATIONS
        values ('inpectioncolumnalreadyexists', 'tj_TJ', N'Inspection Column Already Exists');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'fine')
    BEGIN
        insert into SYS_TRANSLATIONS values ('fine', 'en_US', N'Fine');
        insert into SYS_TRANSLATIONS values ('fine', 'ka_GE', N'ჯარიმა');
        insert into SYS_TRANSLATIONS values ('fine', 'kg_KG', N'Fine');
        insert into SYS_TRANSLATIONS values ('fine', 'ru_RU', N'Fine');
        insert into SYS_TRANSLATIONS values ('fine', 'tj_TJ', N'Fine');
    END;
IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'fines')
    BEGIN
        insert into SYS_TRANSLATIONS values ('fines', 'en_US', N'Fines');
        insert into SYS_TRANSLATIONS values ('fines', 'ka_GE', N'ჯარიმა');
        insert into SYS_TRANSLATIONS values ('fines', 'kg_KG', N'Fines');
        insert into SYS_TRANSLATIONS values ('fines', 'ru_RU', N'Fines');
        insert into SYS_TRANSLATIONS values ('fines', 'tj_TJ', N'Fines');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'amount')
    BEGIN
        insert into SYS_TRANSLATIONS values ('amount', 'en_US', N'Amount');
        insert into SYS_TRANSLATIONS values ('amount', 'ka_GE', N'რაოდენობა');
        insert into SYS_TRANSLATIONS values ('amount', 'kg_KG', N'Amount');
        insert into SYS_TRANSLATIONS values ('amount', 'ru_RU', N'Amount');
        insert into SYS_TRANSLATIONS values ('amount', 'tj_TJ', N'Amount');
    END;


IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'inspectiondocumentnumber')
    BEGIN
        insert into SYS_TRANSLATIONS values ('inspectiondocumentnumber', 'en_US', N'Inspection Document Number');
        insert into SYS_TRANSLATIONS values ('inspectiondocumentnumber', 'ka_GE', N'Inspection Document Number');
        insert into SYS_TRANSLATIONS values ('inspectiondocumentnumber', 'kg_KG', N'Inspection Document Number');
        insert into SYS_TRANSLATIONS values ('inspectiondocumentnumber', 'ru_RU', N'Inspection Document Number');
        insert into SYS_TRANSLATIONS values ('inspectiondocumentnumber', 'tj_TJ', N'Inspection Document Number');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'selectinspection')
    BEGIN
        insert into SYS_TRANSLATIONS values ('selectinspection', 'en_US', N'Select Inspection');
        insert into SYS_TRANSLATIONS values ('selectinspection', 'ka_GE', N'Select Inspection');
        insert into SYS_TRANSLATIONS values ('selectinspection', 'kg_KG', N'Select Inspection');
        insert into SYS_TRANSLATIONS values ('selectinspection', 'ru_RU', N'Select Inspection');
        insert into SYS_TRANSLATIONS values ('selectinspection', 'tj_TJ', N'Select Inspection');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'entersanctiondocnumber')
    BEGIN
        insert into SYS_TRANSLATIONS values ('entersanctiondocnumber', 'en_US', N'Enter Sanction Document Number');
        insert into SYS_TRANSLATIONS values ('entersanctiondocnumber', 'ka_GE', N'Enter Sanction Document Number');
        insert into SYS_TRANSLATIONS values ('entersanctiondocnumber', 'kg_KG', N'Enter Sanction Document Number');
        insert into SYS_TRANSLATIONS values ('entersanctiondocnumber', 'ru_RU', N'Enter Sanction Document Number');
        insert into SYS_TRANSLATIONS values ('entersanctiondocnumber', 'tj_TJ', N'Enter Sanction Document Number');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'selectsanctiontype')
    BEGIN
        insert into SYS_TRANSLATIONS values ('selectsanctiontype', 'en_US', N'Select Sanction Type');
        insert into SYS_TRANSLATIONS values ('selectsanctiontype', 'ka_GE', N'Select Sanction Type');
        insert into SYS_TRANSLATIONS values ('selectsanctiontype', 'kg_KG', N'Select Sanction Type');
        insert into SYS_TRANSLATIONS values ('selectsanctiontype', 'ru_RU', N'Select Sanction Type');
        insert into SYS_TRANSLATIONS values ('selectsanctiontype', 'tj_TJ', N'Select Sanction Type');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'choosesynchronizedtype')
    BEGIN
        insert into SYS_TRANSLATIONS values ('choosesynchronizedtype', 'en_US', N'Choose Synchronized Type');
        insert into SYS_TRANSLATIONS values ('choosesynchronizedtype', 'ka_GE', N'Choose Synchronized Type');
        insert into SYS_TRANSLATIONS values ('choosesynchronizedtype', 'kg_KG', N'Choose Synchronized Type');
        insert into SYS_TRANSLATIONS values ('choosesynchronizedtype', 'ru_RU', N'Choose Synchronized Type');
        insert into SYS_TRANSLATIONS values ('choosesynchronizedtype', 'tj_TJ', N'Choose Synchronized Type');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'reclamationletterdate')
    BEGIN
        insert into SYS_TRANSLATIONS values ('reclamationletterdate', 'en_US', N'Reclamation Letter Date');
        insert into SYS_TRANSLATIONS values ('reclamationletterdate', 'ka_GE', N'Reclamation Letter Date');
        insert into SYS_TRANSLATIONS values ('reclamationletterdate', 'kg_KG', N'Reclamation Letter Date');
        insert into SYS_TRANSLATIONS values ('reclamationletterdate', 'ru_RU', N'Reclamation Letter Date');
        insert into SYS_TRANSLATIONS values ('reclamationletterdate', 'tj_TJ', N'Reclamation Letter Date');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'usertitle')
    BEGIN
        insert into SYS_TRANSLATIONS values ('usertitle', 'en_US', N'Title');
        insert into SYS_TRANSLATIONS values ('usertitle', 'ka_GE', N'განყოფილება');
        insert into SYS_TRANSLATIONS values ('usertitle', 'kg_KG', N'Title');
        insert into SYS_TRANSLATIONS values ('usertitle', 'ru_RU', N'Title');
        insert into SYS_TRANSLATIONS values ('usertitle', 'tj_TJ', N'Title');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'moveup')
    BEGIN
        insert into SYS_TRANSLATIONS values ('moveup', 'en_US', N'Move Up');
        insert into SYS_TRANSLATIONS values ('moveup', 'ka_GE', N'Move Up');
        insert into SYS_TRANSLATIONS values ('moveup', 'kg_KG', N'Move Up');
        insert into SYS_TRANSLATIONS values ('moveup', 'ru_RU', N'Move Up');
        insert into SYS_TRANSLATIONS values ('moveup', 'tj_TJ', N'Move Up');
    END;

IF
    Not EXISTS(select KEY_CODE
               from SYS_TRANSLATIONS
               where KEY_CODE = 'movedown')
    BEGIN
        insert into SYS_TRANSLATIONS values ('movedown', 'en_US', N'Move Down');
        insert into SYS_TRANSLATIONS values ('movedown', 'ka_GE', N'Move Down');
        insert into SYS_TRANSLATIONS values ('movedown', 'kg_KG', N'Move Down');
        insert into SYS_TRANSLATIONS values ('movedown', 'ru_RU', N'Move Down');
        insert into SYS_TRANSLATIONS values ('movedown', 'tj_TJ', N'Move Down');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'inspections')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('inspections', 'en_US', N'Inspections');
        INSERT INTO SYS_TRANSLATIONS VALUES ('inspections', 'ka_GE', N'Inspections');
        INSERT INTO SYS_TRANSLATIONS VALUES ('inspections', 'kg_KG', N'Inspections');
        INSERT INTO SYS_TRANSLATIONS VALUES ('inspections', 'ru_RU', N'Inspections');
        INSERT INTO SYS_TRANSLATIONS VALUES ('inspections', 'tj_TJ', N'Inspections');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'fines')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('fines', 'en_US', N'Fines');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fines', 'ka_GE', N'Fines');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fines', 'kg_KG', N'Fines');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fines', 'ru_RU', N'Fines');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fines', 'tj_TJ', N'Fines');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'username')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('username', 'en_US', N'Username');
        INSERT INTO SYS_TRANSLATIONS VALUES ('username', 'ka_GE', N'Username');
        INSERT INTO SYS_TRANSLATIONS VALUES ('username', 'kg_KG', N'Username');
        INSERT INTO SYS_TRANSLATIONS VALUES ('username', 'ru_RU', N'Username');
        INSERT INTO SYS_TRANSLATIONS VALUES ('username', 'tj_TJ', N'Username');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'documents')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('documents', 'en_US', N'Documents');
        INSERT INTO SYS_TRANSLATIONS VALUES ('documents', 'ka_GE', N'Documents');
        INSERT INTO SYS_TRANSLATIONS VALUES ('documents', 'kg_KG', N'Documents');
        INSERT INTO SYS_TRANSLATIONS VALUES ('documents', 'ru_RU', N'Documents');
        INSERT INTO SYS_TRANSLATIONS VALUES ('documents', 'tj_TJ', N'Documents');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'time')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('time', 'en_US', N'Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('time', 'ka_GE', N'Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('time', 'kg_KG', N'Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('time', 'ru_RU', N'Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('time', 'tj_TJ', N'Time');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'deliverydate')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('deliverydate', 'en_US', N'Delivery Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('deliverydate', 'ka_GE', N'Delivery Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('deliverydate', 'kg_KG', N'Delivery Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('deliverydate', 'ru_RU', N'Delivery Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('deliverydate', 'tj_TJ', N'Delivery Date');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'paymentdate')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('paymentdate', 'en_US', N'Payment Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('paymentdate', 'ka_GE', N'Payment Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('paymentdate', 'kg_KG', N'Payment Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('paymentdate', 'ru_RU', N'Payment Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('paymentdate', 'tj_TJ', N'Payment Date');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'totalsize')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('totalsize', 'en_US', N'Total Size');
        INSERT INTO SYS_TRANSLATIONS VALUES ('totalsize', 'ka_GE', N'Total Size');
        INSERT INTO SYS_TRANSLATIONS VALUES ('totalsize', 'kg_KG', N'Total Size');
        INSERT INTO SYS_TRANSLATIONS VALUES ('totalsize', 'ru_RU', N'Total Size');
        INSERT INTO SYS_TRANSLATIONS VALUES ('totalsize', 'tj_TJ', N'Total Size');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'substatus')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatus', 'en_US', N'SubStatus');
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatus', 'ka_GE', N'SubStatus');
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatus', 'kg_KG', N'SubStatus');
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatus', 'ru_RU', N'SubStatus');
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatus', 'tj_TJ', N'SubStatus');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'document')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('document', 'en_US', N'Document');
        INSERT INTO SYS_TRANSLATIONS VALUES ('document', 'ka_GE', N'Document');
        INSERT INTO SYS_TRANSLATIONS VALUES ('document', 'kg_KG', N'Document');
        INSERT INTO SYS_TRANSLATIONS VALUES ('document', 'ru_RU', N'Document');
        INSERT INTO SYS_TRANSLATIONS VALUES ('document', 'tj_TJ', N'Document');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'documenttime')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('documenttime', 'en_US', N'Document Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('documenttime', 'ka_GE', N'Document Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('documenttime', 'kg_KG', N'Document Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('documenttime', 'ru_RU', N'Document Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('documenttime', 'tj_TJ', N'Document Time');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'duedate')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('duedate', 'en_US', N'Due Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('duedate', 'ka_GE', N'Due Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('duedate', 'kg_KG', N'Due Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('duedate', 'ru_RU', N'Due Date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('duedate', 'tj_TJ', N'Due Date');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'substatusvalue')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatusvalue', 'en_US', N'SubStatus Value');
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatusvalue', 'ka_GE', N'SubStatus Value');
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatusvalue', 'kg_KG', N'SubStatus Value');
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatusvalue', 'ru_RU', N'SubStatus Value');
        INSERT INTO SYS_TRANSLATIONS VALUES ('substatusvalue', 'tj_TJ', N'SubStatus Value');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'responsibleperson')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('responsibleperson', 'en_US', N'Responsible Person');
        INSERT INTO SYS_TRANSLATIONS VALUES ('responsibleperson', 'ka_GE', N'Responsible Person');
        INSERT INTO SYS_TRANSLATIONS VALUES ('responsibleperson', 'kg_KG', N'Responsible Person');
        INSERT INTO SYS_TRANSLATIONS VALUES ('responsibleperson', 'ru_RU', N'Responsible Person');
        INSERT INTO SYS_TRANSLATIONS VALUES ('responsibleperson', 'tj_TJ', N'Responsible Person');
    END;
IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'administratorname')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorname', 'en_US', N'Administrator Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorname', 'ka_GE', N'Administrator Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorname', 'kg_KG', N'Administrator Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorname', 'ru_RU', N'Administrator Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorname', 'tj_TJ', N'Administrator Name');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'administratorid')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorid', 'en_US', N'Administrator ID');
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorid', 'ka_GE', N'Administrator ID');
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorid', 'kg_KG', N'Administrator ID');
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorid', 'ru_RU', N'Administrator ID');
        INSERT INTO SYS_TRANSLATIONS VALUES ('administratorid', 'tj_TJ', N'Administrator ID');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'listvalue')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('listvalue', 'en_US', N'List Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('listvalue', 'ka_GE', N'List Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('listvalue', 'kg_KG', N'List Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('listvalue', 'ru_RU', N'List Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('listvalue', 'tj_TJ', N'List Values');
    END;
IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'visible')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('visible', 'en_US', N'Visible');
        INSERT INTO SYS_TRANSLATIONS VALUES ('visible', 'ka_GE', N'Visible');
        INSERT INTO SYS_TRANSLATIONS VALUES ('visible', 'kg_KG', N'Visible');
        INSERT INTO SYS_TRANSLATIONS VALUES ('visible', 'ru_RU', N'Visible');
        INSERT INTO SYS_TRANSLATIONS VALUES ('visible', 'tj_TJ', N'Visible');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'exportfiletemplatename')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportfiletemplatename', 'en_US', N'Export File Template');
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportfiletemplatename', 'ka_GE', N'Export File Template');
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportfiletemplatename', 'kg_KG', N'Export File Template');
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportfiletemplatename', 'ru_RU', N'Export File Template');
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportfiletemplatename', 'tj_TJ', N'Export File Template');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'cellobjectfieldtypename')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldtypename', 'en_US', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldtypename', 'ka_GE', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldtypename', 'kg_KG', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldtypename', 'ru_RU', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldtypename', 'tj_TJ', N'Type');
    END;
IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'sanctionfinetypename')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfinetypename', 'en_US', N'Fine Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfinetypename', 'ka_GE', N'Fine Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfinetypename', 'kg_KG', N'Fine Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfinetypename', 'ru_RU', N'Fine Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfinetypename', 'tj_TJ', N'Fine Type');
    END;
IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'sanctionfineprice')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfineprice', 'en_US', N'Fine Price');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfineprice', 'ka_GE', N'Fine Price');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfineprice', 'kg_KG', N'Fine Price');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfineprice', 'ru_RU', N'Fine Price');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctionfineprice', 'tj_TJ', N'Fine Price');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'cellobjectfieldformat')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldformat', 'en_US', N'Cell Format');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldformat', 'ka_GE', N'Cell Format');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldformat', 'kg_KG', N'Cell Format');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldformat', 'ru_RU', N'Cell Format');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cellobjectfieldformat', 'tj_TJ', N'Cell Format');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'filename')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('filename', 'en_US', N'File Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('filename', 'ka_GE', N'File Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('filename', 'kg_KG', N'File Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('filename', 'ru_RU', N'File Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('filename', 'tj_TJ', N'File Name');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'passive')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('passive', 'en_US', N'Passive');
        INSERT INTO SYS_TRANSLATIONS VALUES ('passive', 'ka_GE', N'Passive');
        INSERT INTO SYS_TRANSLATIONS VALUES ('passive', 'kg_KG', N'Passive');
        INSERT INTO SYS_TRANSLATIONS VALUES ('passive', 'ru_RU', N'Passive');
        INSERT INTO SYS_TRANSLATIONS VALUES ('passive', 'tj_TJ', N'Passive');
    END;
IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'uploadtime')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('uploadtime', 'en_US', N'Upload Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('uploadtime', 'ka_GE', N'Upload Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('uploadtime', 'kg_KG', N'Upload Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('uploadtime', 'ru_RU', N'Upload Time');
        INSERT INTO SYS_TRANSLATIONS VALUES ('uploadtime', 'tj_TJ', N'Upload Time');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'uploadfiles')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('uploadfiles', 'en_US', N'Upload Files');
        INSERT INTO SYS_TRANSLATIONS VALUES ('uploadfiles', 'ru_RU', N'Upload Files');
        INSERT INTO SYS_TRANSLATIONS VALUES ('uploadfiles', 'kg_KG', N'Upload Files');
        INSERT INTO SYS_TRANSLATIONS VALUES ('uploadfiles', 'ka_GE', N'Upload Files');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'file')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('file', 'en_US', N'File');
        INSERT INTO SYS_TRANSLATIONS VALUES ('file', 'ru_RU', N'File');
        INSERT INTO SYS_TRANSLATIONS VALUES ('file', 'ka_GE', N'File');
        INSERT INTO SYS_TRANSLATIONS VALUES ('file', 'tj_TJ', N'File');
        INSERT INTO SYS_TRANSLATIONS VALUES ('file', 'kg_KG', N'File');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'ficode')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('ficode', 'en_US', N'FI Code');
        INSERT INTO SYS_TRANSLATIONS VALUES ('ficode', 'ru_RU', N'FI Code');
        INSERT INTO SYS_TRANSLATIONS VALUES ('ficode', 'ka_GE', N'FI Code');
        INSERT INTO SYS_TRANSLATIONS VALUES ('ficode', 'tj_TJ', N'FI Code');
        INSERT INTO SYS_TRANSLATIONS VALUES ('ficode', 'kg_KG', N'FI Code');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'finame')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('finame', 'en_US', N'FI Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('finame', 'ru_RU', N'FI Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('finame', 'ka_GE', N'FI Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('finame', 'tj_TJ', N'FI Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('finame', 'kg_KG', N'FI Name');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'editinspection')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('editinspection', 'en_US', N'Edit Inspection');
        INSERT INTO SYS_TRANSLATIONS VALUES ('editinspection', 'ru_RU', N'Edit Inspection');
        INSERT INTO SYS_TRANSLATIONS VALUES ('editinspection', 'kg_KG', N'Edit Inspection');
        INSERT INTO SYS_TRANSLATIONS VALUES ('editinspection', 'ka_GE', N'Edit Inspection');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'newinspection')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('newinspection', 'en_US', N'New Inspection');
        INSERT INTO SYS_TRANSLATIONS VALUES ('newinspection', 'ru_RU', N'New Inspection');
        INSERT INTO SYS_TRANSLATIONS VALUES ('newinspection', 'kg_KG', N'New Inspection');
        INSERT INTO SYS_TRANSLATIONS VALUES ('newinspection', 'ka_GE', N'New Inspection');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'perioddefinition')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('perioddefinition', 'en_US', N'Period Definition');
        INSERT INTO SYS_TRANSLATIONS VALUES ('perioddefinition', 'ru_RU', N'Period Definition');
        INSERT INTO SYS_TRANSLATIONS VALUES ('perioddefinition', 'ka_GE', N'Period Definition');
        INSERT INTO SYS_TRANSLATIONS VALUES ('perioddefinition', 'tj_TJ', N'Period Definition');
        INSERT INTO SYS_TRANSLATIONS VALUES ('perioddefinition', 'kg_KG', N'Period Definition');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'periodtype')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodtype', 'en_US', N'Period Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodtype', 'ru_RU', N'Period Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodtype', 'ka_GE', N'Period Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodtype', 'tj_TJ', N'Period Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodtype', 'kg_KG', N'Period Type');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'messagereview')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('messagereview', 'en_US', N'Message Review');
        INSERT INTO SYS_TRANSLATIONS VALUES ('messagereview', 'ru_RU', N'Message Review');
        INSERT INTO SYS_TRANSLATIONS VALUES ('messagereview', 'ka_GE', N'Message Review');
        INSERT INTO SYS_TRANSLATIONS VALUES ('messagereview', 'tj_TJ', N'Message Review');
        INSERT INTO SYS_TRANSLATIONS VALUES ('messagereview', 'kg_KG', N'Message Review');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'removeuserrelations')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('removeuserrelations', 'en_US', N'Remove User Relations');
        INSERT INTO SYS_TRANSLATIONS VALUES ('removeuserrelations', 'ru_RU', N'Remove User Relations');
        INSERT INTO SYS_TRANSLATIONS VALUES ('removeuserrelations', 'ka_GE', N'Remove User Relations');
        INSERT INTO SYS_TRANSLATIONS VALUES ('removeuserrelations', 'tj_TJ', N'Remove User Relations');
        INSERT INTO SYS_TRANSLATIONS VALUES ('removeuserrelations', 'kg_KG', N'Remove User Relations');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'markallmessagesasread')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallmessagesasread', 'en_US', N'Mark all Messages As Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallmessagesasread', 'ru_RU', N'Mark all Messages As Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallmessagesasread', 'ka_GE', N'Mark all Messages As Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallmessagesasread', 'tj_TJ', N'Mark all Messages As Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallmessagesasread', 'kg_KG', N'Mark all Messages As Read');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'markallthreadsasread')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallthreadsasread', 'en_US', N'Mark all Threads As Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallthreadsasread', 'ru_RU', N'Mark all Threads As Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallthreadsasread', 'ka_GE', N'Mark all Threads As Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallthreadsasread', 'tj_TJ', N'Mark all Threads As Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markallthreadsasread', 'kg_KG', N'Mark all Threads As Read');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'markbodytext')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('markbodytext', 'en_US', N'Are You Sure You Want To Do That');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markbodytext', 'ru_RU', N'Are You Sure You Want To Do That');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markbodytext', 'ka_GE', N'Are You Sure You Want To Do That');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markbodytext', 'tj_TJ', N'Are You Sure You Want To Do That');
        INSERT INTO SYS_TRANSLATIONS VALUES ('markbodytext', 'kg_KG', N'Are You Sure You Want To Do That');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'taskprocessed')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('taskprocessed', 'en_US', N'Task Processed');
        INSERT INTO SYS_TRANSLATIONS VALUES ('taskprocessed', 'ru_RU', N'Task Processed');
        INSERT INTO SYS_TRANSLATIONS VALUES ('taskprocessed', 'ka_GE', N'Task Processed');
        INSERT INTO SYS_TRANSLATIONS VALUES ('taskprocessed', 'tj_TJ', N'Task Processed');
        INSERT INTO SYS_TRANSLATIONS VALUES ('taskprocessed', 'kg_KG', N'Task Processed');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'rootmessage')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('rootmessage', 'en_US', N'Root Message');
        INSERT INTO SYS_TRANSLATIONS VALUES ('rootmessage', 'ru_RU', N'Root Message');
        INSERT INTO SYS_TRANSLATIONS VALUES ('rootmessage', 'ka_GE', N'Root Message');
        INSERT INTO SYS_TRANSLATIONS VALUES ('rootmessage', 'tj_TJ', N'Root Message');
        INSERT INTO SYS_TRANSLATIONS VALUES ('rootmessage', 'kg_KG', N'Root Message');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'addedsuccessfully')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('addedsuccessfully', 'en_US', N'Added Successfully');
        INSERT INTO SYS_TRANSLATIONS VALUES ('addedsuccessfully', 'ru_RU', N'Added Successfully');
        INSERT INTO SYS_TRANSLATIONS VALUES ('addedsuccessfully', 'ka_GE', N'Added Successfully');
        INSERT INTO SYS_TRANSLATIONS VALUES ('addedsuccessfully', 'tj_TJ', N'Added Successfully');
        INSERT INTO SYS_TRANSLATIONS VALUES ('addedsuccessfully', 'kg_KG', N'Added Successfully');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'editedsuccessfully')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('editedsuccessfully', 'en_US', N'Edited Successfully');
        INSERT INTO SYS_TRANSLATIONS VALUES ('editedsuccessfully', 'ru_RU', N'Edited Successfully');
        INSERT INTO SYS_TRANSLATIONS VALUES ('editedsuccessfully', 'ka_GE', N'Edited Successfully');
        INSERT INTO SYS_TRANSLATIONS VALUES ('editedsuccessfully', 'tj_TJ', N'Edited Successfully');
        INSERT INTO SYS_TRANSLATIONS VALUES ('editedsuccessfully', 'kg_KG', N'Edited Successfully');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'dateread')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('dateread', 'en_US', N'Date Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('dateread', 'ru_RU', N'Date Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('dateread', 'ka_GE', N'წაკითხვის დრო');
        INSERT INTO SYS_TRANSLATIONS VALUES ('dateread', 'tj_TJ', N'Date Read');
        INSERT INTO SYS_TRANSLATIONS VALUES ('dateread', 'kg_KG', N'Date Read');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'exportusersandpermissions')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportusersandpermissions', 'en_US', N'Export Users And Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportusersandpermissions', 'ru_RU', N'Export Users And Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportusersandpermissions', 'ka_GE', N'Export Users And Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportusersandpermissions', 'tj_TJ', N'Export Users And Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('exportusersandpermissions', 'kg_KG', N'Export Users And Permissions');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'translatepermissions')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'en_US', N'Translate Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'ru_RU', N'Translate Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'ka_GE', N'Translate Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'tj_TJ', N'Translate Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'kg_KG', N'Translate Permissions');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'templateview')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('templateview', 'en_US', N'Template View');
        INSERT INTO SYS_TRANSLATIONS VALUES ('templateview', 'ru_RU', N'Просмотр шаблона');
        INSERT INTO SYS_TRANSLATIONS VALUES ('templateview', 'ka_GE', N'შაბლონის ნახვა');
        INSERT INTO SYS_TRANSLATIONS VALUES ('templateview', 'tj_TJ', N'Template View');
        INSERT INTO SYS_TRANSLATIONS VALUES ('templateview', 'kg_KG', N'Template View');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'translatepermissions')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'en_US', N'Translate Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'ru_RU', N'Права доступа на перевод');
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'ka_GE', N'უფლებების თარგმანი');
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'tj_TJ', N'Translate Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('translatepermissions', 'kg_KG', N'Translate Permissions');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'catalogdependantnodecodes')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalogdependantnodecodes', 'en_US', N'Catalog has dependant nodes:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalogdependantnodecodes', 'ru_RU', N'Catalog has dependant nodes:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalogdependantnodecodes', 'ka_GE', N'Catalog has dependant nodes:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalogdependantnodecodes', 'tj_TJ', N'Catalog has dependant nodes:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalogdependantnodecodes', 'kg_KG', N'Catalog has dependant nodes:');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'areusurelogout')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('areusurelogout', 'en_US', N'Are you sure that you want to log out');
        INSERT INTO SYS_TRANSLATIONS VALUES ('areusurelogout', 'ru_RU', N'Вы уверены, что хотите выйти из системы?');
        INSERT INTO SYS_TRANSLATIONS VALUES ('areusurelogout', 'ka_GE', N'დარწმუნებული ხართ რომ გსურთ სისტემიდან გასვლა');
        INSERT INTO SYS_TRANSLATIONS VALUES ('areusurelogout', 'tj_TJ', N'Are you sure that you want to log out');
        INSERT INTO SYS_TRANSLATIONS VALUES ('areusurelogout', 'kg_KG', N'Are you sure that you want to log out');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'cataloghascomparisons')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('cataloghascomparisons', 'en_US', N'Catalog has comparisons:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cataloghascomparisons', 'ru_RU', N'Catalog has comparisons:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cataloghascomparisons', 'ka_GE', N'Catalog has comparisons:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cataloghascomparisons', 'tj_TJ', N'Catalog has comparisons:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('cataloghascomparisons', 'kg_KG', N'Catalog has comparisons:');
    END;


 IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'menu_about')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_about', 'en_US', N'About');
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_about', 'ru_RU', N'About');
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_about', 'ka_GE', N'About');
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_about', 'tj_TJ', N'About');
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_about', 'kg_KG', N'About');
    END;

 IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'software')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('software', 'en_US', N'Software');
        INSERT INTO SYS_TRANSLATIONS VALUES ('software', 'ru_RU', N'Software');
        INSERT INTO SYS_TRANSLATIONS VALUES ('software', 'ka_GE', N'Software');
        INSERT INTO SYS_TRANSLATIONS VALUES ('software', 'tj_TJ', N'Software');
        INSERT INTO SYS_TRANSLATIONS VALUES ('software', 'kg_KG', N'Software');
    END;

 IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'about')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('about', 'en_US', N'About');
        INSERT INTO SYS_TRANSLATIONS VALUES ('about', 'ru_RU', N'About');
        INSERT INTO SYS_TRANSLATIONS VALUES ('about', 'ka_GE', N'About');
        INSERT INTO SYS_TRANSLATIONS VALUES ('about', 'tj_TJ', N'About');
        INSERT INTO SYS_TRANSLATIONS VALUES ('about', 'kg_KG', N'About');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'menu_first')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_first', 'en_US', N'Financial Institutions Registry');
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_first', 'ru_RU', N'Financial Institutions Registry');
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_first', 'ka_GE', N'ფ.ი. რეესტრი');
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_first', 'tj_TJ', N'Financial Institutions Registry');
        INSERT INTO SYS_TRANSLATIONS VALUES ('menu_first', 'kg_KG', N'Financial Institutions Registry');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'entityprogramaticallydeleted')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('entityprogramaticalldeleted', 'en_US', N'Entity is programatically deleted, Do you want to restore it');
        INSERT INTO SYS_TRANSLATIONS VALUES ('entityprogramaticallydeleted', 'ru_RU', N'Entity is programatically deleted, Do you want to restore it');
        INSERT INTO SYS_TRANSLATIONS VALUES ('entityprogramaticallydeleted', 'ka_GE', N'Entity is programatically deleted, Do you want to restore it');
        INSERT INTO SYS_TRANSLATIONS VALUES ('entityprogramaticallydeleted', 'tj_TJ', N'Entity is programatically deleted, Do you want to restore it');
        INSERT INTO SYS_TRANSLATIONS VALUES ('entityprogramaticallydeleted', 'kg_KG', N'Entity is programatically deleted, Do you want to restore ity');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'processstatuschange')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('processstatuschange', 'en_US', N'Process Status Change List');
        INSERT INTO SYS_TRANSLATIONS VALUES ('processstatuschange', 'ru_RU', N'Process Status Change List');
        INSERT INTO SYS_TRANSLATIONS VALUES ('processstatuschange', 'ka_GE', N'Process Status Change List');
        INSERT INTO SYS_TRANSLATIONS VALUES ('processstatuschange', 'tj_TJ', N'Process Status Change List');
        INSERT INTO SYS_TRANSLATIONS VALUES ('processstatuschange', 'kg_KG', N'Process Status Change List');
    END;

    IF NOT EXISTS (SELECT KEY_CODE
                   FROM SYS_TRANSLATIONS
                   WHERE KEY_CODE = 'reportprocessed')
        BEGIN
            INSERT INTO SYS_TRANSLATIONS VALUES ('reportprocessed', 'en_US', N'Report Processed');
            INSERT INTO SYS_TRANSLATIONS VALUES ('reportprocessed', 'ru_RU', N'Report Processed');
            INSERT INTO SYS_TRANSLATIONS VALUES ('reportprocessed', 'ka_GE', N'Report Processed');
            INSERT INTO SYS_TRANSLATIONS VALUES ('reportprocessed', 'tj_TJ', N'Report Processed');
            INSERT INTO SYS_TRANSLATIONS VALUES ('reportprocessed', 'kg_KG', N'Report Processed');
        END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'fileGroups')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('fileGroups', 'en_US', N'File Groups');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fileGroups', 'ru_RU', N'File Groups');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fileGroups', 'ka_GE', N'File Groups');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fileGroups', 'tj_TJ', N'File Groups');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fileGroups', 'kg_KG', N'File Groups');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'pattern')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'en_US', N'Pattern');
        INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'ru_RU', N'Pattern');
        INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'ka_GE', N'Pattern');
        INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'tj_TJ', N'Pattern');
        INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'kg_KG', N'Pattern');
    END;

IF NOT EXISTS (SELECT KEY_CODE
              FROM SYS_TRANSLATIONS
              WHERE KEY_CODE = 'password')
   BEGIN
       INSERT INTO SYS_TRANSLATIONS VALUES ('password', 'en_US', N'Password');
       INSERT INTO SYS_TRANSLATIONS VALUES ('password', 'ru_RU', N'Password');
       INSERT INTO SYS_TRANSLATIONS VALUES ('password', 'ka_GE', N'Password');
       INSERT INTO SYS_TRANSLATIONS VALUES ('password', 'tj_TJ', N'Password');
       INSERT INTO SYS_TRANSLATIONS VALUES ('password', 'kg_KG', N'Password');
   END;

IF NOT EXISTS (SELECT KEY_CODE
              FROM SYS_TRANSLATIONS
              WHERE KEY_CODE = 'signaturecheck')
   BEGIN
       INSERT INTO SYS_TRANSLATIONS VALUES ('signaturecheck', 'en_US', N'Signature Check');
       INSERT INTO SYS_TRANSLATIONS VALUES ('signaturecheck', 'ru_RU', N'Signature Check');
       INSERT INTO SYS_TRANSLATIONS VALUES ('signaturecheck', 'ka_GE', N'Signature Check');
       INSERT INTO SYS_TRANSLATIONS VALUES ('signaturecheck', 'tj_TJ', N'Signature Check');
       INSERT INTO SYS_TRANSLATIONS VALUES ('signaturecheck', 'kg_KG', N'Signature Check');
   END;

IF NOT EXISTS (SELECT KEY_CODE
              FROM SYS_TRANSLATIONS
              WHERE KEY_CODE = 'engine')
   BEGIN
       INSERT INTO SYS_TRANSLATIONS VALUES ('engine', 'en_US', N'Engine');
       INSERT INTO SYS_TRANSLATIONS VALUES ('engine', 'ru_RU', N'Engine');
       INSERT INTO SYS_TRANSLATIONS VALUES ('engine', 'ka_GE', N'Engine');
       INSERT INTO SYS_TRANSLATIONS VALUES ('engine', 'tj_TJ', N'Engine');
       INSERT INTO SYS_TRANSLATIONS VALUES ('engine', 'kg_KG', N'Engine');
   END;

IF NOT EXISTS (SELECT KEY_CODE
              FROM SYS_TRANSLATIONS
              WHERE KEY_CODE = 'regadvanced')
   BEGIN
       INSERT INTO SYS_TRANSLATIONS VALUES ('regadvanced', 'en_US', N'Reg Advanced');
       INSERT INTO SYS_TRANSLATIONS VALUES ('regadvanced', 'ru_RU', N'Reg Advanced');
       INSERT INTO SYS_TRANSLATIONS VALUES ('regadvanced', 'ka_GE', N'Reg Advanced');
       INSERT INTO SYS_TRANSLATIONS VALUES ('regadvanced', 'tj_TJ', N'Reg Advanced');
       INSERT INTO SYS_TRANSLATIONS VALUES ('regadvanced', 'kg_KG', N'Reg Advanced');
   END;

   IF NOT EXISTS (SELECT KEY_CODE
                 FROM SYS_TRANSLATIONS
                 WHERE KEY_CODE = 'enable')
      BEGIN
          INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'en_US', N'Enable');
          INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'ru_RU', N'Enable');
          INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'ka_GE', N'Enable');
          INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'tj_TJ', N'Enable');
          INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'kg_KG', N'Enable');
      END;

   IF NOT EXISTS (SELECT KEY_CODE
                    FROM SYS_TRANSLATIONS
                    WHERE KEY_CODE = 'pattern')
         BEGIN
             INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'en_US', N'Pattern');
             INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'ru_RU', N'Pattern');
             INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'ka_GE', N'Pattern');
             INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'tj_TJ', N'Pattern');
             INSERT INTO SYS_TRANSLATIONS VALUES ('pattern', 'kg_KG', N'Pattern');
         END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'testpattern')
BEGIN
    INSERT INTO SYS_TRANSLATIONS VALUES ('testpattern', 'en_US', N'Test Pattern');
    INSERT INTO SYS_TRANSLATIONS VALUES ('testpattern', 'ru_RU', N'Test Pattern');
    INSERT INTO SYS_TRANSLATIONS VALUES ('testpattern', 'ka_GE', N'Test Pattern');
    INSERT INTO SYS_TRANSLATIONS VALUES ('testpattern', 'tj_TJ', N'Test Pattern');
    INSERT INTO SYS_TRANSLATIONS VALUES ('testpattern', 'kg_KG', N'Test Pattern');
END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'processengine')
BEGIN
    INSERT INTO SYS_TRANSLATIONS VALUES ('processengine', 'en_US', N'Process Engine');
    INSERT INTO SYS_TRANSLATIONS VALUES ('processengine', 'ru_RU', N'Process Engine');
    INSERT INTO SYS_TRANSLATIONS VALUES ('processengine', 'ka_GE', N'Process Engine');
    INSERT INTO SYS_TRANSLATIONS VALUES ('processengine', 'tj_TJ', N'Process Engine');
    INSERT INTO SYS_TRANSLATIONS VALUES ('processengine', 'kg_KG', N'Process Engine');
END;
IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'filepermissiongroup')
BEGIN
    INSERT INTO SYS_TRANSLATIONS VALUES ('filepermissiongroup', 'en_US', N'File Permission Group');
    INSERT INTO SYS_TRANSLATIONS VALUES ('filepermissiongroup', 'ru_RU', N'File Permission Group');
    INSERT INTO SYS_TRANSLATIONS VALUES ('filepermissiongroup', 'ka_GE', N'File Permission Group');
    INSERT INTO SYS_TRANSLATIONS VALUES ('filepermissiongroup', 'tj_TJ', N'File Permission Group');
    INSERT INTO SYS_TRANSLATIONS VALUES ('filepermissiongroup', 'kg_KG', N'File Permission Group');
END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'regulaexpression')
BEGIN
    INSERT INTO SYS_TRANSLATIONS VALUES ('regulaexpression', 'en_US', N'Regular Expression');
    INSERT INTO SYS_TRANSLATIONS VALUES ('regulaexpression', 'ru_RU', N'Regular Expression');
    INSERT INTO SYS_TRANSLATIONS VALUES ('regulaexpression', 'ka_GE', N'Regular Expression');
    INSERT INTO SYS_TRANSLATIONS VALUES ('regulaexpression', 'tj_TJ', N'Regular Expression');
    INSERT INTO SYS_TRANSLATIONS VALUES ('regulaexpression', 'kg_KG', N'Regular Expression');
END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'teststring')
BEGIN
    INSERT INTO SYS_TRANSLATIONS VALUES ('teststring', 'en_US', N'Test String');
    INSERT INTO SYS_TRANSLATIONS VALUES ('teststring', 'ru_RU', N'Test String');
    INSERT INTO SYS_TRANSLATIONS VALUES ('teststring', 'ka_GE', N'Test String');
    INSERT INTO SYS_TRANSLATIONS VALUES ('teststring', 'tj_TJ', N'Test String');
    INSERT INTO SYS_TRANSLATIONS VALUES ('teststring', 'kg_KG', N'Test String');
END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'protected')
BEGIN
    INSERT INTO SYS_TRANSLATIONS VALUES ('protected', 'en_US', N'Protected');
    INSERT INTO SYS_TRANSLATIONS VALUES ('protected', 'ru_RU', N'Protected');
    INSERT INTO SYS_TRANSLATIONS VALUES ('protected', 'ka_GE', N'Protected');
    INSERT INTO SYS_TRANSLATIONS VALUES ('protected', 'tj_TJ', N'Protected');
    INSERT INTO SYS_TRANSLATIONS VALUES ('protected', 'kg_KG', N'Protected');
END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'returndefinition')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinition', 'en_US', N'Return Definition');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinition', 'ru_RU', N'Return Definition');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinition', 'ka_GE', N'Return Definition');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinition', 'tj_TJ', N'Return Definition');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinition', 'kg_KG', N'Return Definition');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'returndefinitionname')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitionname', 'en_US', N'Return Definition Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitionname', 'ru_RU', N'Return Definition Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitionname', 'ka_GE', N'Return Definition Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitionname', 'tj_TJ', N'Return Definition Name');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitionname', 'kg_KG', N'Return Definition Name');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'returndefinitiontype')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitiontype', 'en_US', N'Return Definition Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitiontype', 'ru_RU', N'Return Definition Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitiontype', 'ka_GE', N'Return Definition Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitiontype', 'tj_TJ', N'Return Definition Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitiontype', 'kg_KG', N'Return Definition Type');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'isprotected')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('isprotected', 'en_US', N'Is Protected');
        INSERT INTO SYS_TRANSLATIONS VALUES ('isprotected', 'ru_RU', N'Is Protected');
        INSERT INTO SYS_TRANSLATIONS VALUES ('isprotected', 'ka_GE', N'Is Protected');
        INSERT INTO SYS_TRANSLATIONS VALUES ('isprotected', 'tj_TJ', N'Is Protected');
        INSERT INTO SYS_TRANSLATIONS VALUES ('isprotected', 'kg_KG', N'Is Protected');
    END;


IF NOT EXISTS (SELECT KEY_CODE
              FROM SYS_TRANSLATIONS
              WHERE KEY_CODE = 'enable')
   BEGIN
       INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'en_US', N'Enable');
       INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'ru_RU', N'Enable');
       INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'ka_GE', N'Enable');
       INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'tj_TJ', N'Enable');
       INSERT INTO SYS_TRANSLATIONS VALUES ('enable', 'kg_KG', N'Enable');
   END;


IF NOT EXISTS (SELECT KEY_CODE
              FROM SYS_TRANSLATIONS
              WHERE KEY_CODE = 'cell')
   BEGIN
       INSERT INTO SYS_TRANSLATIONS VALUES ('cell', 'en_US', N'Cell');
       INSERT INTO SYS_TRANSLATIONS VALUES ('cell', 'ru_RU', N'Cell');
       INSERT INTO SYS_TRANSLATIONS VALUES ('cell', 'ka_GE', N'Cell');
       INSERT INTO SYS_TRANSLATIONS VALUES ('cell', 'tj_TJ', N'Cell');
       INSERT INTO SYS_TRANSLATIONS VALUES ('cell', 'kg_KG', N'Cell');
   END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'importedFis')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('importedFis', 'en_US', N'Imported Fis');
        INSERT INTO SYS_TRANSLATIONS VALUES ('importedFis', 'ru_RU', N'Imported Fis');
        INSERT INTO SYS_TRANSLATIONS VALUES ('importedFis', 'ka_GE', N'Imported Fis');
        INSERT INTO SYS_TRANSLATIONS VALUES ('importedFis', 'tj_TJ', N'Imported Fis');
        INSERT INTO SYS_TRANSLATIONS VALUES ('importedFis', 'kg_KG', N'Imported Fis');
    END;
IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'modifiedFis')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('modifiedFis', 'en_US', N'Modified Fis');
        INSERT INTO SYS_TRANSLATIONS VALUES ('modifiedFis', 'ru_RU', N'Modified Fis');
        INSERT INTO SYS_TRANSLATIONS VALUES ('modifiedFis', 'ka_GE', N'Modified Fis');
        INSERT INTO SYS_TRANSLATIONS VALUES ('modifiedFis', 'tj_TJ', N'Modified Fis');
        INSERT INTO SYS_TRANSLATIONS VALUES ('modifiedFis', 'kg_KG', N'Modified Fis');
    END;

IF EXISTS(select KEY_CODE
          FROM SYS_TRANSLATIONS
          WHERE KEY_CODE = 'codeFieldIsNotProvided')
    BEGIN
        UPDATE SYS_TRANSLATIONS SET VALUE = N'Please choose mandatory fields: Code, Region' where LANG_CODE = 'en_US' and KEY_CODE = 'codeFieldIsNotProvided'
        UPDATE SYS_TRANSLATIONS SET VALUE = N'Выберите, пожалуйста, обязательные поля: Код, Регион' where LANG_CODE = 'ru_RU' and KEY_CODE = 'codeFieldIsNotProvided'
        UPDATE SYS_TRANSLATIONS SET VALUE = N'N''გთხოვთ აირჩიოთ სავალდებულო ველები: კოდი, რეგიონი' where LANG_CODE = 'ka_GE' and KEY_CODE = 'codeFieldIsNotProvided'
        UPDATE SYS_TRANSLATIONS SET VALUE = N'Please choose mandatory fields: Code, Region' where LANG_CODE = 'tj_TJ' and KEY_CODE = 'codeFieldIsNotProvided'
        UPDATE SYS_TRANSLATIONS SET VALUE = N'Милдеттүү талааларды тандаңыз: Код, Аймак' where LANG_CODE = 'kg_KG' and KEY_CODE = 'codeFieldIsNotProvided'
    end


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'aggregateBy')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('aggregateBy', 'en_US', N'Aggregate By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('aggregateBy', 'ru_RU', N'Aggregate By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('aggregateBy', 'ka_GE', N'Aggregate By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('aggregateBy', 'tj_TJ', N'Aggregate By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('aggregateBy', 'kg_KG', N'Aggregate By');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'skipRowCondition')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('skipRowCondition', 'en_US', N'Check Empty Rows');
        INSERT INTO SYS_TRANSLATIONS VALUES ('skipRowCondition', 'ru_RU', N'Check Empty Rows');
        INSERT INTO SYS_TRANSLATIONS VALUES ('skipRowCondition', 'ka_GE', N'Check Empty Rows');
        INSERT INTO SYS_TRANSLATIONS VALUES ('skipRowCondition', 'tj_TJ', N'Check Empty Rows');
        INSERT INTO SYS_TRANSLATIONS VALUES ('skipRowCondition', 'kg_KG', N'Check Empty Rows');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'definitionTable')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('definitionTable', 'en_US', N'Definition Table');
        INSERT INTO SYS_TRANSLATIONS VALUES ('definitionTable', 'ru_RU', N'Definition Table');
        INSERT INTO SYS_TRANSLATIONS VALUES ('definitionTable', 'ka_GE', N'Definition Table');
        INSERT INTO SYS_TRANSLATIONS VALUES ('definitionTable', 'tj_TJ', N'Definition Table');
        INSERT INTO SYS_TRANSLATIONS VALUES ('definitionTable', 'kg_KG', N'Definition Table');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'groupedBy')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('groupedBy', 'en_US', N'Grouped By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('groupedBy', 'ru_RU', N'Grouped By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('groupedBy', 'ka_GE', N'Grouped By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('groupedBy', 'tj_TJ', N'Grouped By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('groupedBy', 'kg_KG', N'Grouped By');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'periodParameter')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameter', 'en_US', N'Period Parameter');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameter', 'ru_RU', N'Period Parameter');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameter', 'ka_GE', N'Period Parameter');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameter', 'tj_TJ', N'Period Parameter');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameter', 'kg_KG', N'Period Parameter');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'fiParameter')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameter', 'en_US', N'Fi Parameter');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameter', 'ru_RU', N'Fi Parameter');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameter', 'ka_GE', N'Fi Parameter');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameter', 'tj_TJ', N'Fi Parameter');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameter', 'kg_KG', N'Fi Parameter');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'fiParameterValues')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameterValues', 'en_US', N'Fi Parameter Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameterValues', 'ru_RU', N'Fi Parameter Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameterValues', 'ka_GE', N'Fi Parameter Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameterValues', 'tj_TJ', N'Fi Parameter Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiParameterValues', 'kg_KG', N'Fi Parameter Values');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'periodParameterValues')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameterValues', 'en_US', N'Period Parameter Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameterValues', 'ru_RU', N'Period Parameter Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameterValues', 'ka_GE', N'Period Parameter Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameterValues', 'tj_TJ', N'Period Parameter Values');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodParameterValues', 'kg_KG', N'Period Parameter Values');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'sortby')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('sortby', 'en_US', N'Sort By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sortby', 'ru_RU', N'Sort By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sortby', 'ka_GE', N'Sort By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sortby', 'tj_TJ', N'Sort By');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sortby', 'kg_KG', N'Sort By');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'notification')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('notification', 'en_US', N'Notification');
        INSERT INTO SYS_TRANSLATIONS VALUES ('notification', 'ru_RU', N'Уведомление');
        INSERT INTO SYS_TRANSLATIONS VALUES ('notification', 'ka_GE', N'შეტყობინება');
        INSERT INTO SYS_TRANSLATIONS VALUES ('notification', 'tj_TJ', N'Огоҳинома');
        INSERT INTO SYS_TRANSLATIONS VALUES ('notification', 'kg_KG', N'Эскертме');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'catalog')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalog', 'en_US', N'Catalog');
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalog', 'ru_RU', N'Catalog');
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalog', 'ka_GE', N'ცნობარი');
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalog', 'tj_TJ', N'Catalog');
        INSERT INTO SYS_TRANSLATIONS VALUES ('catalog', 'kg_KG', N'Catalog');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'dashlet')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('dashlet', 'en_US', N'Dashlet');
        INSERT INTO SYS_TRANSLATIONS VALUES ('dashlet', 'ru_RU', N'Dashlet');
        INSERT INTO SYS_TRANSLATIONS VALUES ('dashlet', 'ka_GE', N'დეშლეტი');
        INSERT INTO SYS_TRANSLATIONS VALUES ('dashlet', 'tj_TJ', N'Dashlet');
        INSERT INTO SYS_TRANSLATIONS VALUES ('dashlet', 'kg_KG', N'Dashlet');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'sanctiontype')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctiontype', 'en_US', N'Sanction Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctiontype', 'ru_RU', N'Sanction Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctiontype', 'ka_GE', N'სანქციის ტიპი');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctiontype', 'tj_TJ', N'Sanction Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('sanctiontype', 'kg_KG', N'Sanction Type');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'branchtype')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchtype', 'en_US', N'Branch Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchtype', 'ru_RU', N'Branch Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchtype', 'ka_GE', N'ფილიალის ტიპი');
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchtype', 'tj_TJ', N'Branch Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchtype', 'kg_KG', N'Branch Type');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'legalpersons')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('legalpersons', 'en_US', N'Legal Persons');
        INSERT INTO SYS_TRANSLATIONS VALUES ('legalpersons', 'ru_RU', N'Legal Persons');
        INSERT INTO SYS_TRANSLATIONS VALUES ('legalpersons', 'ka_GE', N'იურიდიული პირები');
        INSERT INTO SYS_TRANSLATIONS VALUES ('legalpersons', 'tj_TJ', N'Legal Persons');
        INSERT INTO SYS_TRANSLATIONS VALUES ('legalpersons', 'kg_KG', N'Legal Persons');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'files')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('files', 'en_US', N'Files');
        INSERT INTO SYS_TRANSLATIONS VALUES ('files', 'ru_RU', N'Files');
        INSERT INTO SYS_TRANSLATIONS VALUES ('files', 'ka_GE', N'ფაილები');
        INSERT INTO SYS_TRANSLATIONS VALUES ('files', 'tj_TJ', N'Files');
        INSERT INTO SYS_TRANSLATIONS VALUES ('files', 'kg_KG', N'Files');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'mainmatrix')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('mainmatrix', 'en_US', N'Main Matrix');
        INSERT INTO SYS_TRANSLATIONS VALUES ('mainmatrix', 'ru_RU', N'Main Matrix');
        INSERT INTO SYS_TRANSLATIONS VALUES ('mainmatrix', 'ka_GE', N'მთავარი მარტრიცა');
        INSERT INTO SYS_TRANSLATIONS VALUES ('mainmatrix', 'tj_TJ', N'Main Matrix');
        INSERT INTO SYS_TRANSLATIONS VALUES ('mainmatrix', 'kg_KG', N'Main Matrix');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'submatrix')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('submatrix', 'en_US', N'Sub Matrix');
        INSERT INTO SYS_TRANSLATIONS VALUES ('submatrix', 'ru_RU', N'Sub Matrix');
        INSERT INTO SYS_TRANSLATIONS VALUES ('submatrix', 'ka_GE', N'ქვე-მარტრიცა');
        INSERT INTO SYS_TRANSLATIONS VALUES ('submatrix', 'tj_TJ', N'Sub Matrix');
        INSERT INTO SYS_TRANSLATIONS VALUES ('submatrix', 'kg_KG', N'Sub Matrix');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'nodemapping')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('nodemapping', 'en_US', N'Node Mapping');
        INSERT INTO SYS_TRANSLATIONS VALUES ('nodemapping', 'ru_RU', N'Node Mapping');
        INSERT INTO SYS_TRANSLATIONS VALUES ('nodemapping', 'ka_GE', N'Node Mapping');
        INSERT INTO SYS_TRANSLATIONS VALUES ('nodemapping', 'tj_TJ', N'Node Mapping');
        INSERT INTO SYS_TRANSLATIONS VALUES ('nodemapping', 'kg_KG', N'Node Mapping');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'period')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('period', 'en_US', N'Period');
        INSERT INTO SYS_TRANSLATIONS VALUES ('period', 'ru_RU', N'Period');
        INSERT INTO SYS_TRANSLATIONS VALUES ('period', 'ka_GE', N'პერიოდი');
        INSERT INTO SYS_TRANSLATIONS VALUES ('period', 'tj_TJ', N'Period');
        INSERT INTO SYS_TRANSLATIONS VALUES ('period', 'kg_KG', N'Period');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'storedreport')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('storedreport', 'en_US', N'Stored Report');
        INSERT INTO SYS_TRANSLATIONS VALUES ('storedreport', 'ru_RU', N'Stored Report');
        INSERT INTO SYS_TRANSLATIONS VALUES ('storedreport', 'ka_GE', N'შენახული ანგარიში');
        INSERT INTO SYS_TRANSLATIONS VALUES ('storedreport', 'tj_TJ', N'Stored Report');
        INSERT INTO SYS_TRANSLATIONS VALUES ('storedreport', 'kg_KG', N'Stored Report');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'returndefinitions')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitions', 'en_US', N'Return Definitions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitions', 'ru_RU', N'Return Definitions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitions', 'ka_GE', N'ანგარიშგებები');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitions', 'tj_TJ', N'Return Definitions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returndefinitions', 'kg_KG', N'Return Definitions');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'return')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('return', 'en_US', N'Return');
        INSERT INTO SYS_TRANSLATIONS VALUES ('return', 'ru_RU', N'Return');
        INSERT INTO SYS_TRANSLATIONS VALUES ('return', 'ka_GE', N'ანგარიშგება');
        INSERT INTO SYS_TRANSLATIONS VALUES ('return', 'tj_TJ', N'Return');
        INSERT INTO SYS_TRANSLATIONS VALUES ('return', 'kg_KG', N'Return');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'titleandcontentrequired')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('titleandcontentrequired', 'en_US', N'Title And Content Are Required');
        INSERT INTO SYS_TRANSLATIONS VALUES ('titleandcontentrequired', 'ru_RU', N'Title And Content Are Required');
        INSERT INTO SYS_TRANSLATIONS VALUES ('titleandcontentrequired', 'ka_GE', N'სათაური და კონტენტი სავალდებულოა');
        INSERT INTO SYS_TRANSLATIONS VALUES ('titleandcontentrequired', 'tj_TJ', N'Title And Content Are Required');
        INSERT INTO SYS_TRANSLATIONS VALUES ('titleandcontentrequired', 'kg_KG', N'Title And Content Are Required');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'selectCardToSeeMessageRecipients')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('selectCardToSeeMessageRecipients', 'en_US', N'Please select one card on the left side to see other message recipients');
        INSERT INTO SYS_TRANSLATIONS VALUES ('selectCardToSeeMessageRecipients', 'ru_RU', N'Please select one card on the left side to see other message recipients');
        INSERT INTO SYS_TRANSLATIONS VALUES ('selectCardToSeeMessageRecipients', 'ka_GE', N'გთხოვთ აირჩიოთ ბარათი მარცხენა მხარეს, რათა ნახოთ სხვა მესიჯის მიმღებები');
        INSERT INTO SYS_TRANSLATIONS VALUES ('selectCardToSeeMessageRecipients', 'tj_TJ', N'Please select one card on the left side to see other message recipients');
        INSERT INTO SYS_TRANSLATIONS VALUES ('selectCardToSeeMessageRecipients', 'kg_KG', N'Please select one card on the left side to see other message recipients');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'emptyNotificationRecipients')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('emptyNotificationRecipients', 'en_US', N'There are no notification recipient');
        INSERT INTO SYS_TRANSLATIONS VALUES ('emptyNotificationRecipients', 'ru_RU', N'There are no notification recipient');
        INSERT INTO SYS_TRANSLATIONS VALUES ('emptyNotificationRecipients', 'ka_GE', N'მიუთითეთ მიმღები');
        INSERT INTO SYS_TRANSLATIONS VALUES ('emptyNotificationRecipients', 'tj_TJ', N'There are no notification recipient');
        INSERT INTO SYS_TRANSLATIONS VALUES ('emptyNotificationRecipients', 'kg_KG', N'There are no notification recipient');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'fiGroup')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiGroup', 'en_US', N'FI Group');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiGroup', 'ru_RU', N'FI Group');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiGroup', 'ka_GE', N'FI ჯგუფი');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiGroup', 'tj_TJ', N'FI Group');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiGroup', 'kg_KG', N'FI Group');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'printreturntype')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('printreturntype', 'en_US', N'Return Types Report');
        INSERT INTO SYS_TRANSLATIONS VALUES ('printreturntype', 'ru_RU', N'Return Types Report');
        INSERT INTO SYS_TRANSLATIONS VALUES ('printreturntype', 'ka_GE', N'ფორმის სტატუსების რეპორტი');
        INSERT INTO SYS_TRANSLATIONS VALUES ('printreturntype', 'tj_TJ', N'Return Types Report');
        INSERT INTO SYS_TRANSLATIONS VALUES ('printreturntype', 'kg_KG', N'Return Types Report');
    END;


IF  EXISTS (SELECT KEY_CODE
            FROM SYS_TRANSLATIONS
            WHERE KEY_CODE = 'shareSumErrorMessage')
    BEGIN
        update SYS_TRANSLATIONS set VALUE =N'Sum of Shares is more than 100%' where key_code = 'shareSumErrorMessage' and lang_code = 'en_US'
        update SYS_TRANSLATIONS set VALUE =N'Сумма Долей больше чем 100%' where key_code = 'shareSumErrorMessage' and lang_code = 'ru_RU'
        update SYS_TRANSLATIONS set VALUE =N'წილების ჯამი აღემატება 100%-ს' where key_code = 'shareSumErrorMessage' and lang_code = 'ka_GE'
        update SYS_TRANSLATIONS set VALUE =N'Sum of Shares is more than 100%' where key_code = 'shareSumErrorMessage' and lang_code = 'tj_TJ'
        update SYS_TRANSLATIONS set VALUE =N'Акциялардын суммасы 100% ашык' where key_code = 'shareSumErrorMessage' and lang_code = 'kg_KG'
    END

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'permissionchangedlogoutconfirm')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('permissionchangedlogoutconfirm', 'en_US', N'Your permissions have been changed. To apply these updates, you need to log out and log in again.' +
                                                                                        N' Would you like to log out now ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('permissionchangedlogoutconfirm', 'ru_RU', N'Your permissions have been changed. To apply these updates, you need to log out and log in again.' +
                                                                                        N' Would you like to log out now ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('permissionchangedlogoutconfirm', 'ka_GE', N'თქვენი უფლებები შეიცვალა. ამ განახლებების გამოსაყენებლად, თქვენ უნდა გამოხვიდეთ და ხელახლა შეხვიდეთ სისტემაში.' +
                                                                                        N' გსურთ გამოხვიდეთ ახლა ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('permissionchangedlogoutconfirm', 'tj_TJ', N'Your permissions have been changed. To apply these updates, you need to log out and log in again.' +
                                                                                        N' Would you like to log out now ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('permissionchangedlogoutconfirm', 'kg_KG', N'Your permissions have been changed. To apply these updates, you need to log out and log in again.' +
                                                                                        N' Would you like to log out now ');
    END;
IF  EXISTS (SELECT KEY_CODE
            FROM SYS_TRANSLATIONS
            WHERE KEY_CODE = 'deleteWarning')
    BEGIN
        update SYS_TRANSLATIONS set VALUE = N'დარწმუნებული ხართ, რომ გსურთ წაშალოთ ' where KEY_CODE = 'deleteWarning' and LANG_CODE = 'ka_GE'
    END

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'saveandprocess')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('saveandprocess', 'en_US', N'Save and process');
        INSERT INTO SYS_TRANSLATIONS VALUES ('saveandprocess', 'ru_RU', N'Save and process');
        INSERT INTO SYS_TRANSLATIONS VALUES ('saveandprocess', 'ka_GE', N'Save and process');
        INSERT INTO SYS_TRANSLATIONS VALUES ('saveandprocess', 'tj_TJ', N'Save and process');
        INSERT INTO SYS_TRANSLATIONS VALUES ('saveandprocess', 'kg_KG', N'Save and process');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'maxcolumnsreached')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('maxcolumnsreached', 'en_US', N'Max columns Reached');
        INSERT INTO SYS_TRANSLATIONS VALUES ('maxcolumnsreached', 'ru_RU', N'Max columns Reached');
        INSERT INTO SYS_TRANSLATIONS VALUES ('maxcolumnsreached', 'ka_GE', N'Max columns Reached');
        INSERT INTO SYS_TRANSLATIONS VALUES ('maxcolumnsreached', 'tj_TJ', N'Max columns Reached');
        INSERT INTO SYS_TRANSLATIONS VALUES ('maxcolumnsreached', 'kg_KG', N'Max columns Reached');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'areYouSureToSyncMail')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('areYouSureToSyncMail', 'en_US', N'Are you sure you want to schedule synchronization?');
        INSERT INTO SYS_TRANSLATIONS VALUES ('areYouSureToSyncMail', 'ru_RU', N'Are you sure you want to schedule synchronization?');
        INSERT INTO SYS_TRANSLATIONS VALUES ('areYouSureToSyncMail', 'ka_GE', N'Are you sure you want to schedule synchronization?');
        INSERT INTO SYS_TRANSLATIONS VALUES ('areYouSureToSyncMail', 'tj_TJ', N'Are you sure you want to schedule synchronization?');
        INSERT INTO SYS_TRANSLATIONS VALUES ('areYouSureToSyncMail', 'kg_KG', N'Are you sure you want to schedule synchronization?');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'synchronized')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('synchronized', 'en_US', N'Synchronized');
        INSERT INTO SYS_TRANSLATIONS VALUES ('synchronized', 'ru_RU', N'Synchronized');
        INSERT INTO SYS_TRANSLATIONS VALUES ('synchronized', 'ka_GE', N'Synchronized');
        INSERT INTO SYS_TRANSLATIONS VALUES ('synchronized', 'tj_TJ', N'Synchronized');
        INSERT INTO SYS_TRANSLATIONS VALUES ('synchronized', 'kg_KG', N'Synchronized');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'codeIsRequired')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeIsRequired', 'en_US', N'Code is required');
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeIsRequired', 'ru_RU', N'Code is required');
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeIsRequired', 'ka_GE', N'Code is required');
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeIsRequired', 'tj_TJ', N'Code is required');
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeIsRequired', 'kg_KG', N'Code is required');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'nameCanNotBeEmpty')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('nameCanNotBeEmpty', 'en_US', N'Name can not be empty');
        INSERT INTO SYS_TRANSLATIONS VALUES ('nameCanNotBeEmpty', 'ru_RU', N'Name can not be empty');
        INSERT INTO SYS_TRANSLATIONS VALUES ('nameCanNotBeEmpty', 'ka_GE', N'Name can not be empty');
        INSERT INTO SYS_TRANSLATIONS VALUES ('nameCanNotBeEmpty', 'tj_TJ', N'Name can not be empty');
        INSERT INTO SYS_TRANSLATIONS VALUES ('nameCanNotBeEmpty', 'kg_KG', N'Name can not be empty');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'codeCanNotBeEmpty')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeCanNotBeEmpty', 'en_US', N'Code can not be empty');
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeCanNotBeEmpty', 'ru_RU', N'Code can not be empty');
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeCanNotBeEmpty', 'ka_GE', N'Code can not be empty');
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeCanNotBeEmpty', 'tj_TJ', N'Code can not be empty');
        INSERT INTO SYS_TRANSLATIONS VALUES ('codeCanNotBeEmpty', 'kg_KG', N'Code can not be empty');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'fiPersonIsNotProvided')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiPersonIsNotProvided', 'en_US', N'Please choose mandatory fields: FI Person');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiPersonIsNotProvided', 'ru_RU', N'Please choose mandatory fields: FI Person');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiPersonIsNotProvided', 'ka_GE', N'Please choose mandatory fields: FI Person');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiPersonIsNotProvided', 'tj_TJ', N'Please choose mandatory fields: FI Person');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fiPersonIsNotProvided', 'kg_KG', N'Please choose mandatory fields: FI Person');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'attachments')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('attachments', 'en_US', N'Attachments');
        INSERT INTO SYS_TRANSLATIONS VALUES ('attachments', 'ru_RU', N'Attachments');
        INSERT INTO SYS_TRANSLATIONS VALUES ('attachments', 'ka_GE', N'Attachments');
        INSERT INTO SYS_TRANSLATIONS VALUES ('attachments', 'tj_TJ', N'Attachments');
        INSERT INTO SYS_TRANSLATIONS VALUES ('attachments', 'kg_KG', N'Attachments');
    END;

IF NOT EXISTS (SELECT KEY_CODE
                FROM SYS_TRANSLATIONS
                WHERE KEY_CODE = 'CANNOT_MAKE_DEFAULT_CRITERION')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('CANNOT_MAKE_DEFAULT_CRITERION', 'en_US', N'Unable to set the criterion as default');
        INSERT INTO SYS_TRANSLATIONS VALUES ('CANNOT_MAKE_DEFAULT_CRITERION', 'ru_RU', N'Unable to set the criterion as default');
        INSERT INTO SYS_TRANSLATIONS VALUES ('CANNOT_MAKE_DEFAULT_CRITERION', 'ka_GE', N'Unable to set the criterion as default');
        INSERT INTO SYS_TRANSLATIONS VALUES ('CANNOT_MAKE_DEFAULT_CRITERION', 'tj_TJ', N'Unable to set the criterion as default');
        INSERT INTO SYS_TRANSLATIONS VALUES ('CANNOT_MAKE_DEFAULT_CRITERION', 'kg_KG', N'Unable to set the criterion as default');
    END;

IF NOT EXISTS (SELECT KEY_CODE
                FROM SYS_TRANSLATIONS
                WHERE KEY_CODE = 'branchFieldfiBranchTypeName')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchFieldfiBranchTypeName', 'en_US', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchFieldfiBranchTypeName', 'ru_RU', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchFieldfiBranchTypeName', 'ka_GE', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchFieldfiBranchTypeName', 'tj_TJ', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('branchFieldfiBranchTypeName', 'kg_KG', N'Type');
    END;

IF NOT EXISTS (SELECT KEY_CODE
                FROM SYS_TRANSLATIONS
                WHERE KEY_CODE = 'managementFieldtype')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('managementFieldtype', 'en_US', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('managementFieldtype', 'ru_RU', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('managementFieldtype', 'ka_GE', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('managementFieldtype', 'tj_TJ', N'Type');
        INSERT INTO SYS_TRANSLATIONS VALUES ('managementFieldtype', 'kg_KG', N'Type');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'metadata')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('metadata', 'en_US', N'Meta Data');
        INSERT INTO SYS_TRANSLATIONS VALUES ('metadata', 'ru_RU', N'Meta Data');
        INSERT INTO SYS_TRANSLATIONS VALUES ('metadata', 'ka_GE', N'Meta Data');
        INSERT INTO SYS_TRANSLATIONS VALUES ('metadata', 'tj_TJ', N'Meta Data');
        INSERT INTO SYS_TRANSLATIONS VALUES ('metadata', 'kg_KG', N'Meta Data');
    END;

IF NOT EXISTS (SELECT KEY_CODE
                FROM SYS_TRANSLATIONS
                WHERE KEY_CODE = 'pastDateDetected')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('pastDateDetected', 'en_US', N'Past date detected. Kindly update it to a valid future date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('pastDateDetected', 'ru_RU', N'Past date detected. Kindly update it to a valid future date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('pastDateDetected', 'ka_GE', N'Past date detected. Kindly update it to a valid future date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('pastDateDetected', 'tj_TJ', N'Past date detected. Kindly update it to a valid future date');
        INSERT INTO SYS_TRANSLATIONS VALUES ('pastDateDetected', 'kg_KG', N'Past date detected. Kindly update it to a valid future date');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'invalidlogin')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('invalidlogin', 'en_US', N'Invalid Login');
        INSERT INTO SYS_TRANSLATIONS VALUES ('invalidlogin', 'ru_RU', N'Invalid Login');
        INSERT INTO SYS_TRANSLATIONS VALUES ('invalidlogin', 'ka_GE', N'Invalid Login');
        INSERT INTO SYS_TRANSLATIONS VALUES ('invalidlogin', 'tj_TJ', N'Invalid Login');
        INSERT INTO SYS_TRANSLATIONS VALUES ('invalidlogin', 'kg_KG', N'Invalid Login');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'returnDeleteError')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('returnDeleteError', 'en_US', N'The following returns cannot be deleted due to their current status:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returnDeleteError', 'ru_RU', N'The following returns cannot be deleted due to their current status:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returnDeleteError', 'ka_GE', N'The following returns cannot be deleted due to their current status:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returnDeleteError', 'tj_TJ', N'The following returns cannot be deleted due to their current status:');
        INSERT INTO SYS_TRANSLATIONS VALUES ('returnDeleteError', 'kg_KG', N'The following returns cannot be deleted due to their current status:');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'tooManyNodesToFixWarning')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('tooManyNodesToFixWarning', 'en_US', N'Too many damaged nodes to fix, please select child folder to reduce node size');
        INSERT INTO SYS_TRANSLATIONS VALUES ('tooManyNodesToFixWarning', 'ru_RU', N'Too many damaged nodes to fix, please select child folder to reduce node size');
        INSERT INTO SYS_TRANSLATIONS VALUES ('tooManyNodesToFixWarning', 'ka_GE', N'Too many damaged nodes to fix, please select child folder to reduce node size');
        INSERT INTO SYS_TRANSLATIONS VALUES ('tooManyNodesToFixWarning', 'tj_TJ', N'Too many damaged nodes to fix, please select child folder to reduce node size');
        INSERT INTO SYS_TRANSLATIONS VALUES ('tooManyNodesToFixWarning', 'kg_KG', N'Too many damaged nodes to fix, please select child folder to reduce node size');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'movetonode')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('movetonode', 'en_US', N'Move to node');
        INSERT INTO SYS_TRANSLATIONS VALUES ('movetonode', 'ru_RU', N'Move to node');
        INSERT INTO SYS_TRANSLATIONS VALUES ('movetonode', 'ka_GE', N'Move to node');
        INSERT INTO SYS_TRANSLATIONS VALUES ('movetonode', 'tj_TJ', N'Move to node');
        INSERT INTO SYS_TRANSLATIONS VALUES ('movetonode', 'kg_KG', N'Move to node');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'loadalldata')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('loadalldata', 'en_US', N'Load All Data');
        INSERT INTO SYS_TRANSLATIONS VALUES ('loadalldata', 'ru_RU', N'Load All Data');
        INSERT INTO SYS_TRANSLATIONS VALUES ('loadalldata', 'ka_GE', N'Load All Data');
        INSERT INTO SYS_TRANSLATIONS VALUES ('loadalldata', 'tj_TJ', N'Load All Data');
        INSERT INTO SYS_TRANSLATIONS VALUES ('loadalldata', 'kg_KG', N'Load All Data');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'gridfilterwillbecleared')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('gridfilterwillbecleared', 'en_US', N'Grid filter will be cleared');
        INSERT INTO SYS_TRANSLATIONS VALUES ('gridfilterwillbecleared', 'ru_RU', N'Grid filter will be cleared');
        INSERT INTO SYS_TRANSLATIONS VALUES ('gridfilterwillbecleared', 'ka_GE', N'Grid filter will be cleared');
        INSERT INTO SYS_TRANSLATIONS VALUES ('gridfilterwillbecleared', 'tj_TJ', N'Grid filter will be cleared');
        INSERT INTO SYS_TRANSLATIONS VALUES ('gridfilterwillbecleared', 'kg_KG', N'Grid filter will be cleared');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'periodScheduleDoesNotExist')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodScheduleDoesNotExist', 'en_US', N'Schedule does not exist. Period ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodScheduleDoesNotExist', 'ru_RU', N'Schedule does not exist. Period ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodScheduleDoesNotExist', 'ka_GE', N'Schedule does not exist. Period ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodScheduleDoesNotExist', 'tj_TJ', N'Schedule does not exist. Period ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('periodScheduleDoesNotExist', 'kg_KG', N'Schedule does not exist. Period ');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'fromEquals')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('fromEquals', 'en_US', N'From = ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fromEquals', 'ru_RU', N'From = ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fromEquals', 'ka_GE', N'From = ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fromEquals', 'tj_TJ', N'From = ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('fromEquals', 'kg_KG', N'From = ');
    END;


IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'toEquals')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('toEquals', 'en_US', N'To = ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('toEquals', 'ru_RU', N'To = ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('toEquals', 'ka_GE', N'To = ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('toEquals', 'tj_TJ', N'To = ');
        INSERT INTO SYS_TRANSLATIONS VALUES ('toEquals', 'kg_KG', N'To = ');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'removefipermissions')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('removefipermissions', 'en_US', N'Remove Fi Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('removefipermissions', 'ru_RU', N'Remove Fi Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('removefipermissions', 'ka_GE', N'Remove Fi Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('removefipermissions', 'tj_TJ', N'Remove Fi Permissions');
        INSERT INTO SYS_TRANSLATIONS VALUES ('removefipermissions', 'kg_KG', N'Remove Fi Permissions');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'updated')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('updated', 'en_US', N'Updated');
        INSERT INTO SYS_TRANSLATIONS VALUES ('updated', 'ru_RU', N'Updated');
        INSERT INTO SYS_TRANSLATIONS VALUES ('updated', 'ka_GE', N'Updated');
        INSERT INTO SYS_TRANSLATIONS VALUES ('updated', 'tj_TJ', N'Updated');
        INSERT INTO SYS_TRANSLATIONS VALUES ('updated', 'kg_KG', N'Updated');
    END;

IF NOT EXISTS (SELECT KEY_CODE
               FROM SYS_TRANSLATIONS
               WHERE KEY_CODE = 'restored')
    BEGIN
        INSERT INTO SYS_TRANSLATIONS VALUES ('restored', 'en_US', N'Restored');
        INSERT INTO SYS_TRANSLATIONS VALUES ('restored', 'ru_RU', N'Restored');
        INSERT INTO SYS_TRANSLATIONS VALUES ('restored', 'ka_GE', N'Restored');
        INSERT INTO SYS_TRANSLATIONS VALUES ('restored', 'tj_TJ', N'Restored');
        INSERT INTO SYS_TRANSLATIONS VALUES ('restored', 'kg_KG', N'Restored');
    END;




