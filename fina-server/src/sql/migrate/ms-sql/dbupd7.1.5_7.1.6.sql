/*
  Update DB Version
*/

UPDATE sys_properties
SET value = '7.1.6'
WHERE prop_key = 'fina2.database.schemaVersion';


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'ADDITIONAL_INFO_ID'
                 AND Object_ID = Object_ID('IN_BANKS'))
    BEGIN
        alter table IN_BANKS
            add ADDITIONAL_INFO_ID numeric
    END;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANKS_ADDITIONAL_INFO')
create table IN_BANKS_ADDITIONAL_INFO
(
    ID              numeric,
    BUSINESS_ENTITY varchar(200),
    ECONOMIC_ENTITY varchar(200),
    EQUITY_FORM     varchar(200),
    MANAGEMENT_FORM varchar(200)
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[fi_additional_info_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE fi_additional_info_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_IMPORTED_RETURN_ERRORS')
create table IN_IMPORTED_RETURN_ERRORS
(
    ID                 numeric,
    IMPORTED_RETURN_ID numeric,
    ERROR_MESSAGE      image
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[imported_return_error_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE imported_return_error_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'IN_MDT_CATALOG_ITEMS'
                 AND Object_ID = Object_ID('LATEST_VERSION'))
    BEGIN
        alter table IN_MDT_CATALOG_ITEMS
            add LATEST_VERSION bit
    END;

exec ('update IN_MDT_CATALOG_ITEMS set LATEST_VERSION=0');
exec ('update  IN_MDT_CATALOG_ITEMS set LATEST_VERSION=1 where VERSION_ID in(select max(rv.ID) from IN_MDT_CATALOG_ITEM_ROW_VERSION rv group by rv.ROW_ID )');


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANKING_OPERATIONS')
create table IN_BANKING_OPERATIONS
(
    ID                numeric,
    PARENT_ID         numeric,
    CODE              nvarchar(200),
    NAMESTRID         numeric,
    LICENSE_TYPE_ID   numeric,
    NATIONAL_CURRENCY bit,
    FOREIGN_CURRENCY  bit
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[banking_operation_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE banking_operation_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


--     =================================================================

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LICENSE_BANKING_OPERATIONS')
create table IN_LICENSE_BANKING_OPERATIONS
(
    ID           numeric,
    OPERATION_ID numeric,
    LICENSE_ID   numeric,
    ACTIVE       bit,
    CHANGE_DATE  datetime
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[lic_banking_operation_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE lic_banking_operation_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;
--     =================================================================

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANKING_OPERATIONS_COMMENTS')
create table IN_BANKING_OPERATIONS_COMMENTS
(
    ID                   numeric,
    COMMENT              nvarchar(4000),
    LICENSE_OPERATION_ID numeric,
    MODIFIED_AT          datetime
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[banking_operation_sequence_comm]')
                 AND type = 'SO')
CREATE SEQUENCE banking_operation_sequence_comm
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LICENSE_COMMENTS')
create table IN_LICENSE_COMMENTS
(
    ID          numeric,
    COMMENT     nvarchar(4000),
    LICENSE_ID  numeric,
    MODIFIED_AT datetime
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[license_comment_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE license_comment_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


DROP TABLE IF EXISTS IN_BANK_BENEFICIARIES;
DROP TABLE IF EXISTS IN_LEGAL_PERSON_BENEFICIARIES;
DROP TABLE IF EXISTS IN_LEGAL_PERSON_FINAL_BENEFICIARIES;
DROP TABLE IF EXISTS IN_BANK_FINAL_BENEFICIARIES;
DROP TABLE IF EXISTS IN_BANK_SHARES;
DROP TABLE IF EXISTS IN_LEGAL_PERSONS_SHARES;
DROP table IF EXISTS IN_PERSONS_SHARES;
DROP table IF EXISTS IN_ENTITY_SHARE;


select *
from IN_LEGAL_PERSON_BENEFICIARIES_TABLE

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BENEFICIARIES')
create table IN_BENEFICIARIES
(
    ID                 numeric not null,
    PHYSICAL_PERSON_ID int,
    LEGAL_PERSON_ID    int,
    CAPITAL_SHARE      float,
    NOMINAL            float,
    CREATION_DATE      datetime,
    ACTIVE             bit,
    CURRENCY           varchar(30)
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[beneficiaries_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE beneficiaries_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'BENEFICIARY_ID'
                 AND Object_ID = Object_ID('IN_FINAL_BENEFICIARIES'))
    BEGIN
        alter table IN_FINAL_BENEFICIARIES
            add BENEFICIARY_ID numeric
    END;


IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANK_LEGAL_PERSONS_FI_ID_LEGAL_PERSON_ID_uindex'
              AND object_id = OBJECT_ID('IN_BANK_LEGAL_PERSONS'))
create unique index IN_BANK_LEGAL_PERSONS_FI_ID_LEGAL_PERSON_ID_uindex
    on IN_BANK_LEGAL_PERSONS (FI_ID, LEGAL_PERSON_ID);


/*
 Run this query only once, it creates legal persons from fis
INSERT INTO IN_LEGAL_PERSONS  (Id,IDENTIFICATION_NUMBER,FI_ID,NAMESTRID)
SELECT (NEXT VALUE FOR legal_persons_sequence),IDENTIFICATION_CODE, ID, NAMESTRID
FROM IN_BANKS b where b.ID not in(select FI_ID from IN_LEGAL_PERSONS);
*/

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'FI_ID'
                 AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
    BEGIN
        alter table IN_LEGAL_PERSONS
            add FI_ID numeric
    END;


IF EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'IS_BANK'
            AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
    BEGIN
        alter table IN_LEGAL_PERSONS
            drop column IS_BANK
    END;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'REGISTRATION_NUMBER'
                 AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
    BEGIN
        alter table IN_LEGAL_PERSONS
            add REGISTRATION_NUMBER varchar(200)
    END;


IF EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'OKPO_CODE'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
    BEGIN
        alter table IN_BANK_BRANCHES
            drop column OKPO_CODE;
    END;


IF EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'NUMBER_IN_REGISTER'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
    BEGIN
        alter table IN_BANK_BRANCHES
            drop column NUMBER_IN_REGISTER;
    END;

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANK_PERSONS_FI_ID_PERSON_ID_index'
              AND object_id = OBJECT_ID('IN_BANK_PERSONS'))
create unique index IN_BANK_PERSONS_FI_ID_PERSON_ID_index
    on IN_BANK_PERSONS (FI_ID, PERSON_ID);


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_PERSON_CONNECTIONS')
create table IN_BANK_PERSON_CONNECTIONS
(
    ID              numeric,
    CONNECTION_TYPE int,
    FI_PERSON_ID    numeric
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[bank_person_connection_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE bank_person_connection_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSON_CONNECTIONS')
create table IN_LEGAL_PERSON_CONNECTIONS
(
    ID              numeric,
    CONNECTION_TYPE int,
    LEGAL_PERSON_ID numeric
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[lp_connection_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE lp_connection_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'IS_DELETED'
                 AND Object_ID = Object_ID('IN_COMMUNICATOR_MESSAGES'))
    BEGIN
        alter table IN_COMMUNICATOR_MESSAGES
            add IS_DELETED bit
    END;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'IS_DELETED'
                 AND Object_ID = Object_ID('IN_COMMUNICATOR_NOTIFICATIONS'))
    BEGIN
        alter table IN_COMMUNICATOR_NOTIFICATIONS
            add IS_DELETED bit
    END;

exec ('update IN_COMMUNICATOR_NOTIFICATIONS set IS_DELETED=0');
exec ('update IN_COMMUNICATOR_MESSAGES set IS_DELETED=0');

IF EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'RECOMMENDER_STR_ID'
            AND Object_ID = Object_ID('IN_RECOMMENDATIONS'))
    BEGIN
        alter table IN_RECOMMENDATIONS
            drop column RECOMMENDER_STR_ID
    END;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'RECOMMENDER_ID'
                 AND Object_ID = Object_ID('IN_RECOMMENDATIONS'))
    BEGIN
        alter table IN_RECOMMENDATIONS
            add RECOMMENDER_ID numeric
    END;


-- // SYS_USERS_INDEXES
IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USERS_FULL_INDEX'
              AND object_id = OBJECT_ID('SYS_USERS'))
CREATE INDEX SYS_USERS_FULL_INDEX ON [SYS_USERS] ([ID]) INCLUDE ([LOGIN], [PASSWORD], [CHANGEPASSWORD], [NAMESTRID],
                                                                 [TITLESTRID], [PHONE], [EMAIL], [BLOCKED],
                                                                 [LASTLOGINDATE], [LASTPASSWORDCHANGEDATE], [optlock],
                                                                 [USERTYPE], [DELETED], [CONTACTPERSONSTRID],
                                                                 [DISABLED]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USERS_ID_INDEX'
              AND object_id = OBJECT_ID('SYS_USERS'))
CREATE INDEX SYS_USERS_ID_INDEX ON [SYS_USERS] ([ID]);


IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USERS_LOGIN_INDEX'
              AND object_id = OBJECT_ID('SYS_USERS'))
CREATE INDEX SYS_USERS_LOGIN_INDEX ON [SYS_USERS] ([ID]) INCLUDE ([LOGIN]);


-- // IN_COMMUNICATOR_USERS_MESSAGE_STATUS
IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_COMMUNICATOR_STATUS_INDEX_1'
              AND object_id = OBJECT_ID('IN_COMMUNICATOR_USERS_MESSAGE_STATUS'))
CREATE INDEX IN_COMMUNICATOR_STATUS_INDEX_1 ON [IN_COMMUNICATOR_USERS_MESSAGE_STATUS] ([message_id], [message_user_id]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_COMMUNICATOR_STATUS_INDEX_2'
              AND object_id = OBJECT_ID('IN_COMMUNICATOR_USERS_MESSAGE_STATUS'))
CREATE INDEX IN_COMMUNICATOR_STATUS_INDEX_2 ON [IN_COMMUNICATOR_USERS_MESSAGE_STATUS] ([message_id], [message_user_id]) INCLUDE ([id], [status], [USER_ID], [LAST_MESSAGE_ID], [READ_DATE]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_COMMUNICATOR_STATUS_INDEX_3'
              AND object_id = OBJECT_ID('IN_COMMUNICATOR_USERS_MESSAGE_STATUS'))
CREATE INDEX IN_COMMUNICATOR_STATUS_INDEX_3 ON [IN_COMMUNICATOR_USERS_MESSAGE_STATUS] ([status], [USER_ID]) INCLUDE ([message_id], [message_user_id]);


-- // SYS_USER_BANKS
IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USER_BANKS_INDEX_1'
              AND object_id = OBJECT_ID('SYS_USER_BANKS'))
CREATE INDEX SYS_USER_BANKS_INDEX_1 ON [SYS_USER_BANKS] ([USERID]) INCLUDE ([BANKID]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USER_BANKS_INDEX_2'
              AND object_id = OBJECT_ID('SYS_USER_BANKS'))
CREATE INDEX SYS_USER_BANKS_INDEX_2 ON [SYS_USER_BANKS] ([USERID]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USER_BANKS_INDEX_3'
              AND object_id = OBJECT_ID('SYS_USER_BANKS'))
CREATE INDEX SYS_USER_BANKS_INDEX_3 ON [SYS_USER_BANKS] ([BANKID]);


-- // IN_BANKS
IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANKS_INDEX_1'
              AND object_id = OBJECT_ID('IN_BANKS'))
CREATE INDEX IN_BANKS_INDEX_1 ON [IN_BANKS] ([TYPEID], [DISABLE], [ID]) INCLUDE ([CODE], [SHORTNAMESTRID], [NAMESTRID],
                                                                                 [ADDRESSSTRID], [PHONE], [FAX],
                                                                                 [EMAIL], [SWIFTCODE], [REGIONID],
                                                                                 [optlock], [IDENTIFICATION_CODE],
                                                                                 [LEGAL_FORM], [MDT_DATA_NODE_ID],
                                                                                 [CREATED_AT], [MODIFIED_AT],
                                                                                 [CONTACT_PERSON], [REPRESENTATIVE],
                                                                                 [REORGANISATION], [MOBILE_OFFICES],
                                                                                 [REGISTRATION_DATE], [CLOSE_DATE],
                                                                                 [WEB_SITE], [EMPLOYES],
                                                                                 [ADDITIONAL_INFO_ID]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANKS_INDEX_2'
              AND object_id = OBJECT_ID('IN_BANKS'))
CREATE INDEX IN_BANKS_INDEX_2 ON [IN_BANKS] ([TYPEID], [DISABLE]) INCLUDE ([ID], [CODE], [NAMESTRID]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANKS_INDEX_3'
              AND object_id = OBJECT_ID('IN_BANKS'))
CREATE INDEX IN_BANKS_INDEX_3 ON [IN_BANKS] ([TYPEID]) INCLUDE ([CODE]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANKS_INDEX_4'
              AND object_id = OBJECT_ID('IN_BANKS'))
CREATE INDEX IN_BANKS_INDEX_4 ON [IN_BANKS] ([DISABLE]) INCLUDE ([TYPEID]);


-- // IN_IMPORTED_RETURNS
IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_IMPORTED_RETURNS_INDEX_1'
              AND object_id = OBJECT_ID('IN_IMPORTED_RETURNS'))
CREATE INDEX IN_IMPORTED_RETURNS_INDEX_1 ON [IN_IMPORTED_RETURNS] ([bankCode], [xlsId], [versionCode], [periodStart], [periodEnd]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_IMPORTED_RETURNS_INDEX_2'
              AND object_id = OBJECT_ID('IN_IMPORTED_RETURNS'))
CREATE INDEX IN_IMPORTED_RETURNS_INDEX_2 ON [IN_IMPORTED_RETURNS] ([bankCode]) INCLUDE ([versionCode], [periodStart], [periodEnd], [xlsId]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_IMPORTED_RETURNS_INDEX_3'
              AND object_id = OBJECT_ID('IN_IMPORTED_RETURNS'))
CREATE INDEX IN_IMPORTED_RETURNS_INDEX_3 ON [IN_IMPORTED_RETURNS] ([xlsId]) INCLUDE ([bankCode], [versionCode], [periodStart], [periodEnd]);


-- // SYS_UPLOADEDFILE
IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_UPLOADEDFILE_INDEX_1'
              AND object_id = OBJECT_ID('SYS_UPLOADEDFILE'))
CREATE INDEX SYS_UPLOADEDFILE_INDEX_1 ON [SYS_UPLOADEDFILE] ([uploadedTime], [type]) INCLUDE ([bankCode], [status]);
IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_UPLOADEDFILE_INDEX_2'
              AND object_id = OBJECT_ID('SYS_UPLOADEDFILE'))
CREATE INDEX SYS_UPLOADEDFILE_INDEX_2 ON [SYS_UPLOADEDFILE] ([username]) INCLUDE ([bankCode], [fileName], [status],
                                                                                  [uploadedTime], [nameValid],
                                                                                  [versionvalid], [protectioninfo],
                                                                                  [hasUserBank], [id], [type],
                                                                                  [matrixValid], [reason],
                                                                                  [PROCESS_ENGINE]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_UPLOADEDFILE_INDEX_3'
              AND object_id = OBJECT_ID('SYS_UPLOADEDFILE'))
CREATE INDEX SYS_UPLOADEDFILE_INDEX_3 ON [SYS_UPLOADEDFILE] ([username]) INCLUDE ([bankCode], [fileName], [status],
                                                                                  [uploadedTime], [nameValid],
                                                                                  [versionvalid], [protectioninfo],
                                                                                  [hasUserBank], [id], [type],
                                                                                  [matrixValid], [reason],
                                                                                  [PROCESS_ENGINE]);


-- // IN_LEGAL_PERSONS
IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_LEGAL_PERSONS_INDEX_1'
              AND object_id = OBJECT_ID('IN_LEGAL_PERSONS'))
CREATE INDEX IN_LEGAL_PERSONS_INDEX_1 ON [IN_LEGAL_PERSONS] ([FI_ID]);

IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_PERSONS_ID_REGION_ID_uindex'
              AND object_id = OBJECT_ID('IN_PERSONS'))
create unique index IN_PERSONS_ID_REGION_ID_uindex on IN_PERSONS (ID, REGION_ID);


IF EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'BUSINESS_ENTITY'
            AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
BEGIN
alter table IN_LEGAL_PERSONS
drop column BUSINESS_ENTITY;
END;


IF EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'ECONOMIC_ENTITY'
            AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
BEGIN
alter table IN_LEGAL_PERSONS
drop column ECONOMIC_ENTITY;
END;

IF EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'EQUITY_FORM'
            AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
BEGIN
alter table IN_LEGAL_PERSONS
drop column EQUITY_FORM;
END;

IF EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'MANAGMENT_FORM'
            AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
BEGIN
alter table IN_LEGAL_PERSONS
drop column MANAGMENT_FORM;
END;


IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'META_INFO_ID'
                 AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
BEGIN
alter table IN_LEGAL_PERSONS
    add META_INFO_ID numeric
END;

IF EXISTS(SELECT *
          FROM sys.objects
          WHERE object_id = OBJECT_ID(N'[fi_additional_info_sequence]')
            AND type = 'SO')
BEGIN
drop sequence fi_additional_info_sequence;
END;

DROP TABLE IF EXISTS IN_BANKS_ADDITIONAL_INFO;



IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BUSINESS_ENTITY_TYPE')
create table IN_BUSINESS_ENTITY_TYPE
(
    ID          numeric,
    CODE        nvarchar(200),
    DESCRIPTION nvarchar(500)
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[business_entity_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE business_entity_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_ECONOMIC_ENTITY_TYPE')
create table IN_ECONOMIC_ENTITY_TYPE
(
    ID          numeric,
    CODE        nvarchar(200),
    DESCRIPTION nvarchar(500)
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[economic_entity_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE economic_entity_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_EQUITY_FORM_TYPE')
create table IN_EQUITY_FORM_TYPE
(
    ID          numeric,
    CODE        nvarchar(200),
    DESCRIPTION nvarchar(500)
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[equity_form_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE equity_form_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_MANAGEMENT_FORM_TYPE')
create table IN_MANAGEMENT_FORM_TYPE
(
    ID          numeric,
    CODE        nvarchar(200),
    DESCRIPTION nvarchar(500)
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[management_form_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE management_form_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[equity_form_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE equity_form_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSON_META_INFO')
create table IN_LEGAL_PERSON_META_INFO
(
    ID                      numeric,
    BUSINESS_ENTITY_TYPE_ID numeric,
    ECONOMIC_ENTITY_TYPE_ID numeric,
    EQUITY_FORM_TYPE_ID     numeric,
    MANAGEMENT_FORM_TYPE_ID numeric
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[lp_meta_info_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE lp_meta_info_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;



IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BUSINESS_ENTITY_TYPE_CODE_uindex'
              AND object_id = OBJECT_ID('IN_BUSINESS_ENTITY_TYPE'))

create unique index IN_BUSINESS_ENTITY_TYPE_CODE_uindex
    on IN_BUSINESS_ENTITY_TYPE (CODE);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_ECONOMIC_ENTITY_TYPE_CODE_uindex'
              AND object_id = OBJECT_ID('IN_ECONOMIC_ENTITY_TYPE'))

create unique index IN_ECONOMIC_ENTITY_TYPE_CODE_uindex
    on IN_ECONOMIC_ENTITY_TYPE (CODE);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_EQUITY_FORM_TYPE_uindex'
              AND object_id = OBJECT_ID('IN_EQUITY_FORM_TYPE'))

create unique index IN_EQUITY_FORM_TYPE_uindex
    on IN_EQUITY_FORM_TYPE (CODE);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_MANAGEMENT_FORM_TYPE_uindex'
              AND object_id = OBJECT_ID('IN_MANAGEMENT_FORM_TYPE'))
create unique index IN_MANAGEMENT_FORM_TYPE_uindex
    on IN_MANAGEMENT_FORM_TYPE (CODE);


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'DELETED'
                 AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
    BEGIN
        alter table IN_LEGAL_PERSONS
            add DELETED bit
    END;


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'DELETED'
                 AND Object_ID = Object_ID('IN_PERSONS'))
    BEGIN
        alter table IN_PERSONS
            add DELETED bit
    END;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'STATUS'
                 AND Object_ID = Object_ID('IN_LEGAL_PERSONS'))
    BEGIN
        alter table IN_LEGAL_PERSONS
            add STATUS int
    END;

exec ('update IN_PERSONS set deleted=0 where deleted is null')
exec ('update IN_LEGAL_PERSONS set deleted=0 where deleted is null')
exec ('update IN_LEGAL_PERSONS set status=0 where status is null')