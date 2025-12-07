/*
  Update DB Version
*/

/*
FI
*/
UPDATE sys_properties
SET value = '7.1.5'
WHERE prop_key = 'fina2.database.schemaVersion';

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CONTACT_PERSON'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add CONTACT_PERSON nvarchar(255)
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CREATED_AT'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add CREATED_AT date
END;


IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'MODIFIED_AT'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add MODIFIED_AT date
END;


IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CLOSE_DATE'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add CLOSE_DATE date
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'SUSPENSION_DATE'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add SUSPENSION_DATE date
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'RENEWAL_DATE'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add RENEWAL_DATE date
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'EMAIL'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add EMAIL varchar(255)
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'PHONE'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add PHONE varchar(255)
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'REG_NUMBER'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add REG_NUMBER bigint
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'OKPO_CODE'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add OKPO_CODE bigint
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'STORAGE'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add STORAGE bit
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'TYPEID'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add TYPEID bigint;
END;

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_BRANCH_TYPES')
create table IN_BANK_BRANCH_TYPES
(
    ID          numeric,
    code        varchar(30),
    NAMESTRID   numeric,
    JSON_CONFIG nvarchar( max),
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[fi_branch_types_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE fi_branch_types_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'REPRESENTATIVE'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN

alter table IN_BANKS
    add REPRESENTATIVE nvarchar(255)
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'REORGANISATION'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add REORGANISATION int
END;


IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'MOBILE_OFFICES'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add MOBILE_OFFICES int
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'REGISTRATION_DATE'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add REGISTRATION_DATE datetime
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CLOSE_DATE'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add CLOSE_DATE datetime
END;

exec ('update  IN_BANKS set MOBILE_OFFICES=0 where MOBILE_OFFICES is null');

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'WEB_SITE'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add WEB_SITE nvarchar(100)
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'EMPLOYES'
                 AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add EMPLOYES int
END;

exec ('update  IN_BANKS set EMPLOYES=0 where EMPLOYES is null');

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'NUMBER_IN_REGISTER'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add NUMBER_IN_REGISTER bigint
END;

IF
EXISTS(select *
           from sys.tables
           where name = 'IN_REG_FILE_ERRORS')
drop table IN_REG_FILE_ERRORS;

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[calendar_event_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE calendar_event_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_CALENDAR_EVENTS')
create table IN_CALENDAR_EVENTS
(
    ID         numeric not null,
    DATE       date,
    EVENT_TYPE numeric,
    COMMENT    nvarchar(255),
    GROUP_UUID nvarchar(64)
);

--     Persons

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_PERSONS')
create table IN_PERSONS
(
    ID                    numeric not null,
    NAMESTRID             numeric,
    IDENTIFICATION_NUMBER nvarchar(100),
    PASSPORT_NUMBER       nvarchar(100),
    RESIDENT_STATUS       int,
    REGION_ID             numeric,
    STATUS                int

);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[persons_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE persons_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_CRIMINAL_RECORDS')
create table IN_CRIMINAL_RECORDS
(
    ID                       numeric not null,
    NUMBER_OF_COURT_DECISION nvarchar(100),
    DATE_OF_COURT_DECISION   datetime,
    DECISION_STR_ID          numeric,
    PUNISHMENT_DATE          datetime,
    TYPE_STR_ID              numeric,
    FINE_AMOUNT              float,
    PUNISHMENT_START_DATE    datetime,
    PERSON_ID                numeric
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[criminal_record_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE criminal_record_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_PERSON_EDUCATION')
create table IN_PERSON_EDUCATION
(
    ID                       numeric not null,
    INSTITUTE_STR_ID         numeric,
    EDUCATION_LEVEL          int,
    SPECIALITY_STR_ID        numeric,
    COURSE_STR_ID            numeric,
    SEMINAR_ORGANIZER_STR_ID numeric,
    TRAINING_PLACE_STR_ID    numeric,
    COMPLETION_DATE          datetime,
    HAS_CERTIFICATES         bit,
    SUPPORT_DOCS__STR_ID     numeric,
    DEGREE_LEVEL             int,
    PERSON_ID                numeric

);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[person_education_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE person_education_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_PERSON_POSITIONS')
create table IN_PERSON_POSITIONS
(
    ID              numeric not null,
    COMPANY_STR_ID  numeric,
    POSITION_STR_ID numeric,
    ELECTION_DATE   datetime,
    PERSON_ID       numeric
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[person_position_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE person_position_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_PERSON_SHARE')
create table IN_PERSON_SHARE
(
    ID               numeric not null,
    REGION_ID        numeric,
    SHARE_PERCENTAGE float,
    SHARE_DATE       datetime,
    COMPANY_STR_ID   numeric,
    PERSON_ID        numeric
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[person_share_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE person_share_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_RECOMMENDATIONS')
create table IN_RECOMMENDATIONS
(
    ID                    numeric not null,
    RECOMMENDER_STR_ID    numeric,
    IDENTIFICATION_NUMBER nvarchar(100),
    PASSPORT_NUMBER       nvarchar(100),
    WORKSPACE_STR_ID      numeric,
    PLACE_STR_ID          numeric,
    PHONE                 varchar(100),
    RECOMMENDATION_DATE   datetime,
    PERSON_ID             numeric
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[recommendation_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE recommendation_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_PERSONS')
create table IN_BANK_PERSONS
(
    ID        numeric not null,
    FI_ID     numeric not null,
    PERSON_ID numeric not null
);


IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[bank_person_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE bank_person_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'JSON_CONFIG'
                 AND Object_ID = Object_ID('IN_MANAGING_BODIES'))
BEGIN
alter table IN_MANAGING_BODIES
    add JSON_CONFIG nvarchar(max)
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'FI_PERSON_ID'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT'))
BEGIN
alter table IN_BANK_MANAGEMENT
    add FI_PERSON_ID numeric
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'MAIL'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT'))
BEGIN
alter table IN_BANK_MANAGEMENT
    add MAIL varchar(100)
END;


IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'ADDRESS'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT'))
BEGIN
alter table IN_BANK_MANAGEMENT
    add ADDRESS varchar(500)
END;


IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'DEPENDENCY_STATUS'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT'))
BEGIN
alter table IN_BANK_MANAGEMENT
    add DEPENDENCY_STATUS bit
END;


exec ('update IN_BANK_MANAGEMENT set DEPENDENCY_STATUS=0 where DEPENDENCY_STATUS is null');


IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_MANAGEMENT_COMMITTEE')
create table IN_BANK_MANAGEMENT_COMMITTEE
(
    ID               numeric not null,
    NAMESTRID        numeric,
    FI_MANAGEMENT_ID numeric
);


IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[fi_management_committee_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE fi_management_committee_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'MANAGER_APPOINTMENT_DATE'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add MANAGER_APPOINTMENT_DATE date
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'MANAGER_ID'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add MANAGER_ID numeric
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CHIEF_ACCOUNTANT_APPOINTMENT_DATE'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add CHIEF_ACCOUNTANT_APPOINTMENT_DATE date
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CHIEF_ACCOUNTANT_ID'
                 AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add CHIEF_ACCOUNTANT_ID numeric
END;


IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'POSITION'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT'))
BEGIN
alter table IN_BANK_MANAGEMENT
    add POSITION nvarchar(200)
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'REPORTING_EMPLOYEES'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT'))
BEGIN
alter table IN_BANK_MANAGEMENT
    add REPORTING_EMPLOYEES int
END;

exec ('update  IN_BANK_MANAGEMENT set REPORTING_EMPLOYEES=0 where REPORTING_EMPLOYEES is null');


IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'PORTFOLIO'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT'))
BEGIN
alter table IN_BANK_MANAGEMENT
    add PORTFOLIO nvarchar(200)
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'DATEOFAPPROVAL'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT'))
BEGIN
alter table IN_BANK_MANAGEMENT
    add DATEOFAPPROVAL datetime
END;


IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'POSITIONSTRID'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT_COMMITTEE'))
BEGIN
alter table IN_BANK_MANAGEMENT_COMMITTEE
    add POSITIONSTRID numeric
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'ELECTION_DATE'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT_COMMITTEE'))
BEGIN
alter table IN_BANK_MANAGEMENT_COMMITTEE
    add ELECTION_DATE datetime
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'APPROVAL_DATE'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT_COMMITTEE'))
BEGIN
alter table IN_BANK_MANAGEMENT_COMMITTEE
    add APPROVAL_DATE datetime
END;

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'COMMENT'
                 AND Object_ID = Object_ID('IN_BANK_MANAGEMENT_COMMITTEE'))
BEGIN
alter table IN_BANK_MANAGEMENT_COMMITTEE
    add COMMENT nvarchar(500)
END;


    -- Criminal Records
IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_PERSONS_CRIMINAL_RECORDS')
create table IN_PERSONS_CRIMINAL_RECORDS
(
    PERSON_ID          int,
    CRIMINAL_RECORD_ID int
);


IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSONS_CRIMINAL_RECORDS')
create table IN_LEGAL_PERSONS_CRIMINAL_RECORDS
(
    LEGAL_PERSON_ID    int,
    CRIMINAL_RECORD_ID int
);

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_CRIMINAL_RECORDS')
create table IN_BANK_CRIMINAL_RECORDS
(
    BANK_ID            int,
    CRIMINAL_RECORD_ID int
);


IF
EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'PERSON_ID'
            AND Object_ID = Object_ID('IN_CRIMINAL_RECORDS'))
BEGIN
alter table IN_CRIMINAL_RECORDS
drop
column PERSON_ID
END;

-- Shares
IF
EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'PERSON_ID'
            AND Object_ID = Object_ID('IN_PERSON_SHARE'))
BEGIN
alter table IN_PERSON_SHARE
drop
column PERSON_ID
END;


IF
EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'COMPANY_STR_ID'
            AND Object_ID = Object_ID('IN_PERSON_SHARE'))
BEGIN
alter table IN_PERSON_SHARE
drop
column COMPANY_STR_ID
END;

IF
EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'REGION_ID'
            AND Object_ID = Object_ID('IN_PERSON_SHARE'))
BEGIN
alter table IN_PERSON_SHARE
drop
column REGION_ID
END;

IF
NOT EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'COMPANY_ID'
            AND Object_ID = Object_ID('IN_PERSON_SHARE'))
    IF EXISTS(select *
                   from sys.tables
                   where name = 'IN_PERSON_SHARE')
BEGIN
alter table IN_PERSON_SHARE
    add COMPANY_ID numeric
END;


IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_PERSONS_SHARES')
create table IN_PERSONS_SHARES
(
    PERSON_ID int,
    SHARE_ID  int
);

IF
EXISTS(select *
           from sys.tables
           where name = 'IN_PERSON_SHARE')
delete
from IN_PERSON_SHARE
where id > 0;

IF
EXISTS(SELECT *
           FROM sys.objects
           WHERE object_id = OBJECT_ID(N'[person_share_sequence]')
             AND type = 'SO')
drop sequence person_share_sequence;

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[share_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE share_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
EXISTS(select *
          from sys.tables
          where name = 'IN_PERSON_SHARE')
    exec sp_rename 'IN_PERSON_SHARE', 'IN_ENTITY_SHARE';

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSONS_SHARES')
create table IN_LEGAL_PERSONS_SHARES
(
    LEGAL_PERSON_ID int,
    SHARE_ID        int
);

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_SHARES')
create table IN_BANK_SHARES
(
    BANK_ID  int,
    SHARE_ID int
);

-- IN_LEGAL_PERSON_CONTACT_INFO

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSON_CONTACT_INFO')
create table IN_LEGAL_PERSON_CONTACT_INFO
(
    ID        numeric not null,
    REGION_ID int,
    ADDRESS   nvarchar(300),
    PHONE     varchar(100),
    WEB_SITE  varchar(100)
);


IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[legal_person_contact_info_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE legal_person_contact_info_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

-- IN_LEGAL_PERSON_BENEFICIARIES

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSON_BENEFICIARIES')
create table IN_LEGAL_PERSON_BENEFICIARIES
(
    ID                 numeric not null,
    SHARE              float,
    PHYSICAL_PERSON_ID int,
    LEGAL_PERSON_ID    int
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[legal_persons_beneficiaries]')
                 AND type = 'SO')
CREATE SEQUENCE legal_persons_beneficiaries
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSON_BENEFICIARIES_TABLE')
create table IN_LEGAL_PERSON_BENEFICIARIES_TABLE
(
    LEGAL_PERSON_ID numeric,
    BENEFICIARY_ID  numeric
);

-- IN_LEGAL_PERSON_FINAL_BENEFICIARIES

IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSON_FINAL_BENEFICIARIES')
create table IN_LEGAL_PERSON_FINAL_BENEFICIARIES
(
    LEGAL_BENEFICIARY_ID int,
    FINAL_BENEFICIARY_ID int
);

--     IN_FINAL_BENEFICIARIES
IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_FINAL_BENEFICIARIES')
create table IN_FINAL_BENEFICIARIES
(
    ID        numeric not null,
    PERSON_ID int
);
IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[final_beneficiary_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE final_beneficiary_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

--     IN_LEGAL_PERSONS
IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSONS')
create table IN_LEGAL_PERSONS
(
    ID                    numeric not null,
    CONTACT_INFO_ID       numeric,
    BUSINESS_ENTITY       varchar(200),
    ECONOMIC_ENTITY       varchar(200),
    EQUITY_FORM           varchar(200),
    MANAGMENT_FORM        varchar(200),
    IS_BANK               bit,
    NAMESTRID             numeric,
    IDENTIFICATION_NUMBER nvarchar(100),
    RESIDENT_STATUS       int,
    REGION_ID             numeric
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[legal_persons_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE legal_persons_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

--     IN_BANK_LEGAL_PERSONS
IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_LEGAL_PERSONS')
create table IN_BANK_LEGAL_PERSONS
(
    ID              numeric not null,
    FI_ID           numeric,
    LEGAL_PERSON_ID numeric
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[bank_legal_person_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE bank_legal_person_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


--     IN_PERSON_POSITIONS
IF
EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'COMPANY_STR_ID'
            AND Object_ID = Object_ID('IN_PERSON_POSITIONS'))
BEGIN
alter table IN_PERSON_POSITIONS
drop
column COMPANY_STR_ID
END;

IF
NOT EXISTS(SELECT 1
              FROM sys.columns
              WHERE Name = 'LEGAL_PERSON_ID'
                AND Object_ID = Object_ID('IN_PERSON_POSITIONS'))
BEGIN
alter table IN_PERSON_POSITIONS
    add LEGAL_PERSON_ID numeric
END;

IF
NOT EXISTS(SELECT 1
              FROM sys.columns
              WHERE Name = 'PERSON_ID'
                AND Object_ID = Object_ID('IN_PERSON_POSITIONS'))
BEGIN
alter table IN_PERSON_POSITIONS
    add PERSON_ID numeric
END;


--     IN_BANK_BENEFICIARIES
IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_BENEFICIARIES')
create table IN_BANK_BENEFICIARIES
(
    ID              numeric not null,
    BANK_ID         numeric,
    CAPITAL_SHARE   float,
    NOMINAL         float,
    ACTIVE          bit,
    CURRENCY        varchar(30),
    LEGAL_PERSON_ID numeric,
    FI_PERSON_ID    numeric
);


IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[bank_beneficiary_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE bank_beneficiary_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

--     IN_BANK_FINAL_BENEFICIARIES
IF
NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_FINAL_BENEFICIARIES')
create table IN_BANK_FINAL_BENEFICIARIES
(
    BANK_BENEFICIARY_ID  int,
    FINAL_BENEFICIARY_ID int
);


IF
NOT EXISTS(SELECT 1
              FROM sys.columns
              WHERE Name = 'CREATION_DATE'
                AND Object_ID = Object_ID('IN_BANK_BENEFICIARIES'))
BEGIN
alter table IN_BANK_BENEFICIARIES
    add CREATION_DATE datetime
END;


-- =======================================================================

--     Add PK Indexes for tables

-- IN_PERSONS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_PERSONS'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_PERSONS (ID);

-- IN_CRIMINAL_RECORDS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_CRIMINAL_RECORDS'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_CRIMINAL_RECORDS (ID);


--     IN_PERSON_EDUCATION
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_PERSON_EDUCATION'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_PERSON_EDUCATION (ID);

--     IN_PERSON_POSITIONS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_PERSON_POSITIONS'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_PERSON_POSITIONS (ID);

--     IN_ENTITY_SHARE
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_ENTITY_SHARE'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_ENTITY_SHARE (ID);

--     IN_RECOMMENDATIONS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_RECOMMENDATIONS'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_RECOMMENDATIONS (ID);

--     IN_BANK_PERSONS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_BANK_PERSONS'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_BANK_PERSONS (ID);

--     IN_BANK_LEGAL_PERSONS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_BANK_LEGAL_PERSONS'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_BANK_LEGAL_PERSONS (ID);

--     IN_FINAL_BENEFICIARIES
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_FINAL_BENEFICIARIES'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_FINAL_BENEFICIARIES (ID);

--     IN_LEGAL_PERSONS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_LEGAL_PERSONS'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_LEGAL_PERSONS (ID);

--     IN_LEGAL_PERSON_BENEFICIARIES
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_LEGAL_PERSON_BENEFICIARIES'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_LEGAL_PERSON_BENEFICIARIES (ID);

--     IN_LEGAL_PERSON_CONTACT_INFO
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_LEGAL_PERSON_CONTACT_INFO'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_LEGAL_PERSON_CONTACT_INFO (ID);


--IN_BANK_BENEFICIARIES
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'PK_ID_INDEX'
              AND object_id = OBJECT_ID('IN_BANK_BENEFICIARIES'))
CREATE UNIQUE INDEX PK_ID_INDEX on IN_BANK_BENEFICIARIES (ID);

IF
NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'IS_KEY'
                 AND Object_ID = Object_ID('IN_MDT_NODES'))
BEGIN
alter table IN_MDT_NODES
    add IS_KEY bit
END;

exec ('update IN_MDT_NODES set IS_KEY=0 where IS_KEY is null');