/*
  Update DB Version
*/

UPDATE sys_properties
SET value = '8.0.1'
WHERE prop_key = 'fina2.database.schemaVersion';


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_CONNECTED_COMPANIES_INFO')
create table IN_CONNECTED_COMPANIES_INFO
(
    ID                numeric,
    SOURCE_ID         numeric,
    DESTINATION_ID    numeric,
    BUSINESS_ACTIVITY nvarchar(max),
    STRATEGIC_PLAN    nvarchar(max),
    CONNECTION_TYPE   int
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[connected_company_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE connected_company_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'PROCESS_ENGINE'
                 AND Object_ID = Object_ID('IN_UPLOADFILE_QUEUE'))
    BEGIN
        alter table IN_UPLOADFILE_QUEUE
            add PROCESS_ENGINE int not null;
    END;

-- CEMS
IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_INSPECTIONS')
create table CEMS_INSPECTIONS
(
    ID                      varchar(200) not null unique,
    FI_ID                   numeric,
    TYPE                    int,
    FOUNDATION              nvarchar(500),
    MANAGER_ID              numeric,
    MANAGER_POSITION        nvarchar(200),
    START_DATE              datetime,
    END_DATE                datetime,
    INFO                    nvarchar(500),
    PURPOSE                 nvarchar(500),
    VERIFICATION_FOUNDATION nvarchar(500),
    LAST_INSPECTION_DATE    datetime,
    ONGOING_INSPECTION_DATE datetime,
    REPORTING_YEAR_ID       numeric,
    META_INFO_ID            numeric,
    RECORD_CREATE_DATE      datetime

);


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_INSPECTIONS_META_INFO')
create table CEMS_INSPECTIONS_META_INFO
(
    ID                          numeric unique,
    PRELIMINARY_DISCUSSION_DATE datetime,
    FINAL_DISCUSSION_DATE       datetime,
    REPORT_SENT_DATE            datetime,
    PROBLEM                     nvarchar(500),
    NUMBER_OF_ORDERS            int,
    NUMBER_OF_ORDERS_AML        int
);


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_inspection_meta_info]')
                 AND type = 'SO')
CREATE SEQUENCE cems_inspection_meta_info
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_REPORTING_YEAR_INSPECTIONS')
create table CEMS_REPORTING_YEAR_INSPECTIONS
(
    ID         numeric unique,
    START_DATE datetime,
    END_DATE   datetime,
    MANAGER_ID numeric
);


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_repo_year_inspection]')
                 AND type = 'SO')
CREATE SEQUENCE cems_repo_year_inspection
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_RECOMMENDATIONS')
create table CEMS_RECOMMENDATIONS
(
    ID                 varchar(200) not null unique,
    TYPE               numeric,
    INSPECTION_ID      varchar(100),
    CREATION_DATE      datetime,
    LETTER_DATE        datetime,
    LETTER_INFO        nvarchar(500),
    NUMBER             numeric,
    ORDER_CONTENT      nvarchar(500),
    EXECUTION_PERIOD   datetime,
    FI_ACTION          nvarchar(500),
    NOTE               nvarchar(500),
    STATUS             numeric,
    RECORD_CREATE_DATE datetime
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_RESPONSIBLE_PERSONS')
create table CEMS_RESPONSIBLE_PERSONS
(
    ID                numeric unique,
    FULL_NAME         nvarchar(200),
    POSITION          nvarchar(200),
    RECOMMENDATION_ID varchar(200)
);


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_responsible_persons_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE cems_responsible_persons_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_RECOMMENDATIONS_STATUS_HISTORY')
create table CEMS_RECOMMENDATIONS_STATUS_HISTORY
(
    ID                numeric unique,
    FI_ACTION         nvarchar(500),
    NOTE              nvarchar(500),
    STATUS            numeric,
    version           int,
    RECORD_DATE       datetime,
    RECOMMENDATION_ID varchar(200)
);


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_recomm_history_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE cems_recomm_history_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(SELECT *
               FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS
               WHERE CONSTRAINT_NAME = 'FK_BRANCH_PERSON_MANAGER_ID')
alter table IN_BANK_BRANCHES
    add constraint FK_BRANCH_PERSON_MANAGER_ID
        foreign key (MANAGER_ID) references IN_BANK_PERSONS (ID);

IF
    NOT EXISTS(SELECT *
               FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS
               WHERE CONSTRAINT_NAME = 'FK_BRANCH_PERSON_ACCOUNTANT_ID')
alter table IN_BANK_BRANCHES
    add constraint FK_BRANCH_PERSON_ACCOUNTANT_ID
        foreign key (CHIEF_ACCOUNTANT_ID) references IN_BANK_PERSONS (ID);

alter table IN_BANK_BRANCHES
    alter
        column REG_NUMBER nvarchar(100) null;


-- HIBERNATE ENVERS AUDITING TABLES
IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'REVINFO')
create table REVINFO
(
    REV       int identity
        primary key,
    REVTSTMP  bigint,
    USER_NAME varchar(200)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_BRANCHES_AUD')
create table IN_BANK_BRANCHES_AUD
(
    id                                int not null,
    REV                               int not null,
    REVTYPE                           int,
    BANKREGIONSTRID                   decimal(10),
    NAMESTRID                         decimal(10),
    SHORTNAMESTRID                    decimal(10),
    ADDRESSSTRID                      decimal(10),
    COMMENTSSTRID                     nvarchar(40),
    CREATIONDATE                      datetime,
    DATEOFCHANGE                      datetime,
    BANKID                            decimal(10),
    DISABLE                           bit,
    CODE                              varchar(100),
    DELETED                           bit,
    MDT_DATA_NODE_ID                  bigint,
    CLOSE_DATE                        date,
    SUSPENSION_DATE                   date,
    RENEWAL_DATE                      date,
    EMAIL                             varchar(255),
    PHONE                             varchar(255),
    REG_NUMBER                        nvarchar(100),
    STORAGE                           bit,
    TYPEID                            bigint,
    NUMBER_IN_REGISTER                bigint,
    MANAGER_APPOINTMENT_DATE          date,
    MANAGER_ID                        numeric
        constraint FK_BRANCH_AUD_PERSON_MANAGER_ID
            references IN_BANK_PERSONS (ID),
    CHIEF_ACCOUNTANT_APPOINTMENT_DATE date,
    CHIEF_ACCOUNTANT_ID               numeric
        constraint FK_BRANCH_AUD_PERSON_ACCOUNTANT_ID
            references IN_BANK_PERSONS (ID),
    primary key (id, REV)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_CRIMINAL_RECORDS_AUD')
create table IN_BANK_CRIMINAL_RECORDS_AUD
(
    REV                numeric not null,
    REVTYPE            numeric not null,
    BANK_ID            numeric not null,
    CRIMINAL_RECORD_ID numeric not null
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_MANAGEMENT_AUD')
create table IN_BANK_MANAGEMENT_AUD
(
    ID                  decimal(10)   not null,
    REV                 decimal(10)   not null,
    REVTYPE             int           not null,
    NAMESTRID           decimal(10),
    LASTNAMESTRID       decimal(10),
    MANAGINGBODYID      decimal(10),
    POSTSTRID           decimal(10),
    PHONE               nvarchar(25),
    DATEOFAPPOINTMENT   datetime,
    CANCELDATE          datetime,
    REGISTRATIONSTRID1  decimal(10),
    REGISTRATIONSTRID2  decimal(10),
    REGISTRATIONSTRID3  decimal(10),
    COMMENTSSTRID1      decimal(10),
    COMMENTSSTRID2      decimal(10),
    BANKID              decimal(10),
    RESIDENT            bit default 0 null,
    DISABLE             bit,
    FI_PERSON_ID        numeric,
    MAIL                varchar(100),
    ADDRESS             varchar(500),
    DEPENDENCY_STATUS   bit,
    POSITION            nvarchar(200),
    REPORTING_EMPLOYEES int,
    PORTFOLIO           nvarchar(200),
    DATEOFAPPROVAL      datetime,
    primary key (ID, REV)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_MANAGEMENT_COMMITTEE_AUD')
create table IN_BANK_MANAGEMENT_COMMITTEE_AUD
(
    ID               int,
    REV              int not null,
    REVTYPE          int not null,
    FI_MANAGEMENT_ID int
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANKS_AUD')
create table IN_BANKS_AUD
(
    ID                  int not null,
    REV                 int not null,
    CODE                varchar(100),
    PHONE               varchar(100),
    NAMESTRID           decimal(10),
    REVTYPE             int,
    TYPEID              decimal(10),
    SHORTNAMESTRID      decimal(10),
    ADDRESSSTRID        decimal(10),
    FAX                 nvarchar(40),
    EMAIL               nvarchar(400),
    TELEX               nvarchar(40),
    SWIFTCODE           nvarchar(11),
    REGIONID            decimal,
    IDENTIFICATION_CODE nvarchar(400),
    LEGAL_FORM          nvarchar(400),
    DISABLE             bit default 0,
    MDT_DATA_NODE_ID    bigint,
    CREATED_AT          datetime,
    MODIFIED_AT         datetime,
    CONTACT_PERSON      nvarchar(255),
    REPRESENTATIVE      nvarchar(255),
    REORGANISATION      int,
    MOBILE_OFFICES      int,
    REGISTRATION_DATE   datetime,
    CLOSE_DATE          datetime,
    WEB_SITE            nvarchar(100),
    EMPLOYES            int,
    ADDITIONAL_INFO_ID  int,
    primary key (ID, REV)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BENEFICIARIES_AUD')
create table IN_BENEFICIARIES_AUD
(
    ID                 numeric not null,
    REV                int     not null,
    REVTYPE            int     not null,
    PHYSICAL_PERSON_ID int,
    LEGAL_PERSON_ID    int,
    BANK_ID            numeric,
    CAPITAL_SHARE      float,
    NOMINAL            float,
    CREATION_DATE      datetime,
    ACTIVE             bit,
    CURRENCY           varchar(30)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BENEFICIARIES_JOIN_TABLE_AUD')
create table IN_BENEFICIARIES_JOIN_TABLE_AUD
(
    ID             int,
    REV            int not null,
    REVTYPE        int not null,
    BENEFICIARY_ID int
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_DASHBOARDS')
create table IN_DASHBOARDS
(
    ID         numeric not null,
    NAMESTRID  int,
    COLUMNS    int,
    IS_DEFAULT bit
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[dashboards_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE dashboards_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_DASHLETS')
create table IN_DASHLETS
(
    ID           numeric not null,
    NAMESTRID    int,
    DATA_QUERY   varchar(max),
    META_INFO    nvarchar(max),
    DASHBOARD_ID int
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[dashlets_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE dashlets_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_USER_DASHBOARDS')
create table SYS_USER_DASHBOARDS
(
    USER_LOGIN   varchar(200) not null,
    DASHBOARD_ID int          not null
);

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CONFIG'
                 AND Object_ID = Object_ID('IN_DASHBOARDS'))
    BEGIN
        alter table IN_DASHBOARDS
            add CONFIG nvarchar(max);
    END;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_DASHBOARD_DASHLETS')
create table IN_DASHBOARD_DASHLETS
(
    DASHBOARD_ID numeric not null,
    DASHLET_ID   numeric not null,
);

Alter table IN_BANK_MANAGEMENT
    Alter
        column ADDRESS Nvarchar(500);



IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTIONS')
create table CEMS_SANCTIONS
(
    ID                            numeric,
    ORGANIZATION_TYPE             VARCHAR(100),
    SUBJECT_LEGAL_NAME            nvarchar(max),
    SUBJECT_ID                    VARCHAR(200),
    LICENSE_NUMBER                NVARCHAR(200),
    REG_LETTER_NUMBER             NVARCHAR(200),
    ADDRESS                       NVARCHAR(500),
    NOTE                          NVARCHAR(max),
    MEASURE_OF_INFLUENCE          VARCHAR(100),
    DECISION_MAKING_BODY          VARCHAR(100),
    ACTION_DATE                   datetime,
    DOCUMENT_NUMBER               nvarchar(200),
    EXECUTION_PERIOD              datetime,
    VALIDITY_PERIOD               datetime,
    INITIAL_COURT_APPEAL_DATE     datetime,
    INITIAL_COURT_APPEAL_DECISION nvarchar(max),
    FINAl_COURT_APPEAL_DATE       datetime,
    COURT_DECISION                nvarchar(max),
    RESPONSIBLE_PERSONS           nvarchar(max),
    STATUS                        varchar(100),
    ADDITIONAL_NOTE               nvarchar(max),
    INSPECTION_ID                 VARCHAR(200),
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_sanction_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE cems_sanction_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTIONED_EMPLOYEES')
create table CEMS_SANCTIONED_EMPLOYEES
(
    ID                numeric,
    EMPLOYEE_NAME     NVARCHAR(400),
    EMPLOYEE_ID       NVARCHAR(200),
    EMPLOYEE_POSITION NVARCHAR(400),
    SANCTION_ID       numeric,
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_sanctioned_empl_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE cems_sanctioned_empl_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTION_REGULATIONS')
create table CEMS_SANCTION_REGULATIONS
(
    ID           numeric,
    REGULATION   VARCHAR(100),
    VALUE        NVARCHAR(400),
    ACTUAL_VALUE NVARCHAR(400),
    SANCTION_ID  numeric,
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_sanction_reg_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE cems_sanction_reg_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTION_MEASURE_OF_INFLUENCE')
create table CEMS_SANCTION_MEASURE_OF_INFLUENCE
(
    MEASURE_OF_INFLUENCE VARCHAR(100),
    SANCTION_ID          numeric,
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTION_MEASURE_REASONS')
create table CEMS_SANCTION_MEASURE_REASONS
(
    MEASURE_REASON VARCHAR(100),
    SANCTION_ID    numeric,
);

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'PHASE'
                 AND Object_ID = Object_ID('CEMS_INSPECTIONS'))
    BEGIN
        alter table CEMS_INSPECTIONS
            add PHASE int
    END;

exec ('update CEMS_INSPECTIONS set PHASE=1 where PHASE is null');


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'VALIDITY_PERIOD_FROM'
                 AND Object_ID = Object_ID('CEMS_SANCTIONS'))
    BEGIN
        exec sp_rename 'CEMS_SANCTIONS.VALIDITY_PERIOD', VALIDITY_PERIOD_FROM, 'COLUMN'
    END;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'VALIDITY_PERIOD_TO'
                 AND Object_ID = Object_ID('CEMS_SANCTIONS'))
    BEGIN
        alter table CEMS_SANCTIONS
            add VALIDITY_PERIOD_TO datetime
    END;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_CRIMINAL_RECORDS_AUD')
create table IN_CRIMINAL_RECORDS_AUD
(
    id                       bigint not null,
    REV                      bigint not null,
    REVTYPE                  smallint,
    DECISION_STR_ID          bigint,
    DATE_OF_COURT_DECISION   datetime2,
    NUMBER_OF_COURT_DECISION varchar(255),
    FINE_AMOUNT              float,
    PUNISHMENT_DATE          datetime2,
    PUNISHMENT_START_DATE    datetime2,
    TYPE_STR_ID              bigint,
    primary key (id, REV)
)
IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LICENSE_BANKING_OPERATIONS_JOIN_AUD')
create table IN_LICENSE_BANKING_OPERATIONS_JOIN_AUD
(
    REV          decimal(10) not null,
    REVTYPE      int         not null,
    OPERATION_ID numeric,
    LICENSE_ID   numeric,
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LICENCES_AUD')
create table IN_LICENCES_AUD
(
    ID           decimal(10) not null,
    REV          decimal(10) not null,
    REVTYPE      int         not null,
    TYPEID       decimal(10),
    CODE         nvarchar(12),
    CREATIONDATE datetime,
    DATEOFCHANGE datetime,
    REASONSTRID  decimal(10),
    OPERATIONAL  decimal(10),
    BANKID       decimal(10),
    isdefault    int,
    primary key (ID, REV)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LEGAL_PERSON_META_INFO_AUD')
create table IN_LEGAL_PERSON_META_INFO_AUD
(
    ID                      int not null,
    REV                     int not null,
    REVTYPE                 int not null,
    BUSINESS_ENTITY_TYPE_ID numeric,
    ECONOMIC_ENTITY_TYPE_ID numeric,
    EQUITY_FORM_TYPE_ID     numeric,
    MANAGEMENT_FORM_TYPE_ID numeric
        primary key (ID, REV)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LICENSE_COMMENTS_AUD')
create table IN_LICENSE_COMMENTS_AUD
(
    id          bigint not null,
    REV         bigint not null,
    REVTYPE     smallint,
    COMMENT     varchar(255),
    MODIFIED_AT datetime2,
    primary key (id, REV)
)
IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANK_LICENSE_COMMENTS_AUD')
create table IN_BANK_LICENSE_COMMENTS_AUD
(
    REV        bigint not null,
    LICENSE_ID bigint not null,
    COMMENT_ID bigint not null,
    REVTYPE    smallint,
    primary key (REV, LICENSE_ID, COMMENT_ID)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_LICENSE_BANKING_OPERATIONS_AUD')
create table IN_LICENSE_BANKING_OPERATIONS_AUD
(
    id           bigint not null,
    REV          bigint not null,
    REVTYPE      smallint,
    ACTIVE       bit,
    CHANGE_DATE  datetime2,
    OPERATION_ID bigint,
    primary key (id, REV)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANKING_OPERATION_COMMENTS_AUD')
create table IN_BANKING_OPERATION_COMMENTS_AUD
(
    REV                  bigint not null,
    LICENSE_OPERATION_ID bigint not null,
    COMMENT_ID           bigint not null,
    REVTYPE              smallint,
    primary key (REV, LICENSE_OPERATION_ID, COMMENT_ID)
);

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_BANKING_OPERATIONS_COMMENTS_AUD')
create table IN_BANKING_OPERATIONS_COMMENTS_AUD
(
    id          bigint not null,
    REV         bigint not null,
    REVTYPE     smallint,
    COMMENT     varchar(255),
    MODIFIED_AT datetime2,
    primary key (id, REV)
);

update IN_MDT_NODES
set DATATYPE=0
where DATATYPE is null;


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CONTACT_PERSON_POSITION_STRID'
                 AND Object_ID = Object_ID('SYS_USERS'))
    BEGIN
        alter table SYS_USERS
            add CONTACT_PERSON_POSITION_STRID int;
    END;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'EMS_INSPECTION_TYPES_TABLE')
create table EMS_INSPECTION_TYPES_TABLE
(
    INSPECTION_ID numeric,
    TYPE_ID       numeric
);

IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Name = 'inspectionType_id'
             AND Object_ID = Object_ID('EMS_INSPECTIONS'))
    begin
        insert into EMS_INSPECTION_TYPES_TABLE (INSPECTION_ID, TYPE_ID)
        select id, inspectionType_id
        from EMS_INSPECTIONS
        where inspectionType_id in (select id from EMS_INSPECTION_TYPES)
          and inspectionType_id not in (select TYPE_ID from EMS_INSPECTION_TYPES_TABLE);
    end;

IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Name = 'inspectionType_id'
             AND Object_ID = Object_ID('EMS_INSPECTIONS'))
    BEGIN
        alter table EMS_INSPECTIONS
            drop
                column inspectionType_id;
    END;


IF
    not EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'INSPECTION_SUBJECT'
                 AND Object_ID = Object_ID('EMS_INSPECTIONS'))
    BEGIN
        alter table EMS_INSPECTIONS
            add INSPECTION_SUBJECT nvarchar(500);
    END;

IF
    not EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'RECOMMENDATION'
                 AND Object_ID = Object_ID('EMS_INSPECTIONS'))
    BEGIN
        alter table EMS_INSPECTIONS
            add RECOMMENDATION nvarchar(500);
    END;

IF
    not EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'RECLAMATION_NUMBER'
                 AND Object_ID = Object_ID('EMS_INSPECTIONS'))
    BEGIN
        alter table EMS_INSPECTIONS
            add RECLAMATION_NUMBER nvarchar(500);
    END;

IF
    not EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'RECLAMATION_DATE'
                 AND Object_ID = Object_ID('EMS_INSPECTIONS'))
    BEGIN
        alter table EMS_INSPECTIONS
            add RECLAMATION_DATE datetime;
    END;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'EMS_INSPECTION_FOLLOWUP')
create table EMS_INSPECTION_FOLLOWUP
(
    ID            numeric not null,
    INSPECTION_ID numeric not null,
    TYPE          tinyint,
    STATUS        tinyint,
    RESULT        tinyint,
    DEADLINE      datetime,
    NOTE          nvarchar(500),
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[followUp_seq]')
                 AND type = 'SO')
CREATE SEQUENCE followUp_seq
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'EMS_INSPECTION_FOLLOWUP_RECOMMENDATIONS')
create table EMS_INSPECTION_FOLLOWUP_RECOMMENDATIONS
(
    ID             numeric not null,
    FOLLOWUP_ID    numeric,
    RECOMMENDATION nvarchar(500),
    TYPE           tinyint,
    STATUS         tinyint,
    RESULT         tinyint,
    DEADLINE       datetime,
);

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[followUp_recommendation_seq]')
                 AND type = 'SO')
CREATE SEQUENCE followUp_recommendation_seq
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


-- permissions
DECLARE
    @ss_id   INT
DECLARE
    @sp_id   INT
DECLARE
    @lang_id INT

SET @ss_id = (SELECT max(id)
              FROM SYS_STRINGS) + 1
SET @sp_id = (SELECT max(id)
              FROM SYS_PERMISSIONS) + 1
SET @lang_id = (SELECT id
                FROM sys_languages
                WHERE code LIKE '%en%')

IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.ems.followup.review')
    BEGIN
        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@ss_id + 1, @lang_id, 'net.fina.ems.followup.review');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.ems.followup.review');
    END;

IF
    NOT EXISTS (SELECT *
                FROM SYS_PERMISSIONS
                WHERE idName = 'net.fina.ems.followup.amend')
    BEGIN
        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@ss_id + 2, @lang_id, 'net.fina.ems.followup.amend');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@sp_id + 2, @ss_id + 2, 'net.fina.ems.followup.amend');
    END;

IF
    NOT EXISTS (SELECT *
                FROM SYS_PERMISSIONS
                WHERE idName = 'net.fina.ems.followup.delete')
    BEGIN
        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@ss_id + 3, @lang_id, 'net.fina.ems.followup.delete');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@sp_id + 3, @ss_id + 3, 'net.fina.ems.followup.delete');
    END;

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1
             FROM SYS_STRINGS)
WHERE PK_COLUMN_NAME = 'SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1
             FROM SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME = 'SYS_PERMISSION_MAXID';

IF
    NOT EXISTS (SELECT *
                FROM sys_properties
                WHERE PROP_KEY = 'net.fina.ems.system.impl.name')
    BEGIN
        insert into sys_properties
        values ('net.fina.ems.system.impl.name', 'AML');
    END;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'EMS_FOLLOWUP_NOTIFICATIONS')
create table EMS_FOLLOWUP_NOTIFICATIONS
(
    notificationId numeric not null,
    entity_id      numeric,
    TYPE           varchar(500),
    DEADLINE       datetime,
);


IF
    not EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'DECREE_NUMBER'
                 AND Object_ID = Object_ID('IN_BANKS'))
    BEGIN
        alter table IN_BANKS
            add DECREE_NUMBER varchar(200);
    END

IF
    not EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'INSPECTION_END_DATE'
                 AND Object_ID = Object_ID('IN_BANKS'))
    BEGIN
        alter table IN_BANKS
            add INSPECTION_END_DATE datetime;
    END;


IF
    not EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'DECREE_NUMBER'
                 AND Object_ID = Object_ID('IN_BANKS_AUD'))
    BEGIN
        alter table IN_BANKS_AUD
            add DECREE_NUMBER varchar(200);
    END

IF
    not EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'INSPECTION_END_DATE'
                 AND Object_ID = Object_ID('IN_BANKS_AUD'))
    BEGIN
        alter table IN_BANKS_AUD
            add INSPECTION_END_DATE datetime;
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[sys_string_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @SYS_STRING_START as BIGINT;
        set
            @SYS_STRING_START = COALESCE((SELECT MAX(ID) + 1 FROM SYS_STRINGS), 1)
        DECLARE
            @SYS_STRING_SQL NVARCHAR(MAX)

        SET @SYS_STRING_SQL =
                    'CREATE SEQUENCE sys_string_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@SYS_STRING_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@SYS_STRING_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_banks_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @BANKS_START as BIGINT;
        set
            @BANKS_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_BANKS), 1)
        DECLARE
            @BANKS_SQL NVARCHAR(MAX)

        SET @BANKS_SQL =
                    'CREATE SEQUENCE in_banks_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@BANKS_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@BANKS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_bank_branches_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @BANK_BRANCHES_START as BIGINT;
        set
            @BANK_BRANCHES_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_BANK_BRANCHES), 1)
        DECLARE
            @BANK_BRANCHES_SQL NVARCHAR(MAX)

        SET @BANK_BRANCHES_SQL =
                    'CREATE SEQUENCE in_bank_branches_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@BANK_BRANCHES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@BANK_BRANCHES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_bank_groups_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @BANK_GROUPS_START as BIGINT;
        set
            @BANK_GROUPS_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_BANK_GROUPS), 1)
        DECLARE
            @BANK_GROUPS_SQL NVARCHAR(MAX)

        SET @BANK_GROUPS_SQL =
                    'CREATE SEQUENCE in_bank_groups_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@BANK_GROUPS_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@BANK_GROUPS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_bank_group_history_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @BANK_GROUP_HISTORY_START as BIGINT;
        set
            @BANK_GROUP_HISTORY_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_BANK_GROUP_HISTORY), 1)
        DECLARE
            @BANK_GROUP_HISTORY_SQL NVARCHAR(MAX)

        SET @BANK_GROUP_HISTORY_SQL =
                    'CREATE SEQUENCE in_bank_group_history_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@BANK_GROUP_HISTORY_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@BANK_GROUP_HISTORY_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_bank_management_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @BANK_MANAGEMENT_START as BIGINT;
        set
            @BANK_MANAGEMENT_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_BANK_MANAGEMENT), 1)
        DECLARE
            @BANK_MANAGEMENT_SQL NVARCHAR(MAX)

        SET @BANK_MANAGEMENT_SQL =
                    'CREATE SEQUENCE in_bank_management_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@BANK_MANAGEMENT_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@BANK_MANAGEMENT_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_bank_types_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @BANK_TYPES_START as BIGINT;
        set
            @BANK_TYPES_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_BANK_TYPES), 1)
        DECLARE
            @BANK_TYPES_SQL NVARCHAR(MAX)

        SET @BANK_TYPES_SQL =
                    'CREATE SEQUENCE in_bank_types_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@BANK_TYPES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@BANK_TYPES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_country_data_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @COUNTRY_DATA_START as BIGINT;
        set
            @COUNTRY_DATA_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_COUNTRY_DATA), 1)
        DECLARE
            @COUNTRY_DATAS_SQL NVARCHAR(MAX)

        SET @COUNTRY_DATAS_SQL =
                    'CREATE SEQUENCE in_country_data_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@COUNTRY_DATA_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@COUNTRY_DATAS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_criterion_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @CRITERION_START as BIGINT;
        set
            @CRITERION_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_CRITERION), 1)
        DECLARE
            @CRITERION_SQL NVARCHAR(MAX)

        SET @CRITERION_SQL =
                    'CREATE SEQUENCE in_criterion_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@CRITERION_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@CRITERION_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_definition_tables_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @IN_DEFINITION_START as BIGINT;
        set
            @IN_DEFINITION_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_DEFINITION_TABLES), 1)
        DECLARE
            @IN_DEFINITION_SQL NVARCHAR(MAX)

        SET @IN_DEFINITION_SQL =
                    'CREATE SEQUENCE in_definition_tables_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@IN_DEFINITION_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@IN_DEFINITION_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_licences_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @IN_LICENSES_START as BIGINT;
        set
            @IN_LICENSES_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_LICENCES), 1)
        DECLARE
            @IN_LICENSES_SQL NVARCHAR(MAX)

        SET @IN_LICENSES_SQL =
                    'CREATE SEQUENCE in_licences_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@IN_LICENSES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@IN_LICENSES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_licenses_history_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @LICENSES_HISTORY_START as BIGINT;
        set
            @LICENSES_HISTORY_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_LICENCES_HISTORY), 1)
        DECLARE
            @LICENSES_HISTORY_SQL NVARCHAR(MAX)

        SET @LICENSES_HISTORY_SQL =
                    'CREATE SEQUENCE in_licenses_history_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@LICENSES_HISTORY_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@LICENSES_HISTORY_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_license_types_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @LICENSE_TYPE_START as BIGINT;
        set
            @LICENSE_TYPE_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_LICENCE_TYPES), 1)
        DECLARE
            @LICENSE_TYPE_SQL NVARCHAR(MAX)

        SET @LICENSE_TYPE_SQL =
                    'CREATE SEQUENCE in_license_types_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@LICENSE_TYPE_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@LICENSE_TYPE_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_mail_message_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @MAIL_MESSAGE_START as BIGINT;
        set
            @MAIL_MESSAGE_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_MAIL_MESSAGE), 1)
        DECLARE
            @MAIL_MESSAGE_SQL NVARCHAR(MAX)

        SET @MAIL_MESSAGE_SQL =
                    'CREATE SEQUENCE in_mail_message_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@MAIL_MESSAGE_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@MAIL_MESSAGE_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_managing_bodies_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @MANAGING_BODIES_START as BIGINT;
        set
            @MANAGING_BODIES_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_MANAGING_BODIES), 1)
        DECLARE
            @MANAGING_BODIES_SQL NVARCHAR(MAX)

        SET @MANAGING_BODIES_SQL =
                    'CREATE SEQUENCE in_managing_bodies_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@MANAGING_BODIES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@MANAGING_BODIES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_mdt_comparison_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @MDT_COMPARISON_START as BIGINT;
        set
            @MDT_COMPARISON_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_MDT_COMPARISON), 1)
        DECLARE
            @MDT_COMPARISON_SQL NVARCHAR(MAX)

        SET @MDT_COMPARISON_SQL =
                    'CREATE SEQUENCE in_mdt_comparison_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@MDT_COMPARISON_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@MDT_COMPARISON_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_mdt_nodes_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @MDT_NODES_START as BIGINT;
        set
            @MDT_NODES_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_MDT_NODES), 1)
        DECLARE
            @MDT_NODES_SQL NVARCHAR(MAX)

        SET @MDT_NODES_SQL =
                    'CREATE SEQUENCE in_mdt_nodes_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@MDT_NODES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@MDT_NODES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_packages_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @PACKAGES_START as BIGINT;
        set
            @PACKAGES_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_PACKAGES), 1)
        DECLARE
            @PACKAGES_SQL NVARCHAR(MAX)

        SET @PACKAGES_SQL =
                    'CREATE SEQUENCE in_packages_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@PACKAGES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@PACKAGES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_periods_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @PERIODS_START as BIGINT;
        set
            @PERIODS_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_PERIODS), 1)
        DECLARE
            @PERIODS_SQL NVARCHAR(MAX)

        SET @PERIODS_SQL =
                    'CREATE SEQUENCE in_periods_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@PERIODS_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@PERIODS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_period_types_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @PERIOD_TYPES_START as BIGINT;
        set
            @PERIOD_TYPES_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_PERIOD_TYPES), 1)
        DECLARE
            @PERIOD_TYPES_SQL NVARCHAR(MAX)

        SET @PERIOD_TYPES_SQL =
                    'CREATE SEQUENCE in_period_types_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@PERIOD_TYPES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@PERIOD_TYPES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_returns_schedule_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @RETURNS_SCHEDULE_START as BIGINT;
        set
            @RETURNS_SCHEDULE_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_RETURNS_SCHEDULE), 1)
        DECLARE
            @RETURNS_SCHEDULE_SQL NVARCHAR(MAX)

        SET @RETURNS_SCHEDULE_SQL =
                    'CREATE SEQUENCE in_returns_schedule_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@RETURNS_SCHEDULE_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@RETURNS_SCHEDULE_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_return_definitions_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @RETURN_DEFINITIONS_START as BIGINT;
        set
            @RETURN_DEFINITIONS_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_RETURNS_SCHEDULE), 1)
        DECLARE
            @RETURN_DEFINITIONS_SQL NVARCHAR(MAX)

        SET @RETURN_DEFINITIONS_SQL =
                    'CREATE SEQUENCE in_return_definitions_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@RETURN_DEFINITIONS_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@RETURN_DEFINITIONS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_return_items_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @RETURN_ITEMS_START as BIGINT;
        set
            @RETURN_ITEMS_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_RETURN_ITEMS), 1)
        DECLARE
            @RETURN_ITEMS_SQL NVARCHAR(MAX)

        SET @RETURN_ITEMS_SQL =
                    'CREATE SEQUENCE in_return_items_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@RETURN_ITEMS_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@RETURN_ITEMS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_return_types_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @RETURN_TYPES_START as BIGINT;
        set
            @RETURN_TYPES_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_RETURN_TYPES), 1)
        DECLARE
            @RETURN_TYPES_SQL NVARCHAR(MAX)

        SET @RETURN_TYPES_SQL =
                    'CREATE SEQUENCE in_return_types_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@RETURN_TYPES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@RETURN_TYPES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_return_versions_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @RETURN_VERSIONS_START as BIGINT;
        set
            @RETURN_VERSIONS_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_RETURN_VERSIONS), 1)
        DECLARE
            @RETURN_VERSIONS_SQL NVARCHAR(MAX)

        SET @RETURN_VERSIONS_SQL =
                    'CREATE SEQUENCE in_return_versions_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@RETURN_VERSIONS_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@RETURN_VERSIONS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[in_schedules_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @SCHEDULES_START as BIGINT;
        set
            @SCHEDULES_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_SCHEDULES), 1)
        DECLARE
            @SCHEDULES_SQL NVARCHAR(MAX)

        SET @SCHEDULES_SQL =
                    'CREATE SEQUENCE in_schedules_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@SCHEDULES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@SCHEDULES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[out_reports_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @OUT_REPORTS_START as BIGINT;
        set
            @OUT_REPORTS_START = COALESCE((SELECT MAX(ID) + 1 FROM OUT_REPORTS), 1)
        DECLARE
            @OUT_REPORTS_SQL NVARCHAR(MAX)

        SET @OUT_REPORTS_SQL =
                    'CREATE SEQUENCE out_reports_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@OUT_REPORTS_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@OUT_REPORTS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[sys_languages_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @LANGUAGES_START as BIGINT;
        set
            @LANGUAGES_START = COALESCE((SELECT MAX(ID) + 1 FROM SYS_LANGUAGES), 1)
        DECLARE
            @LANGUAGES_SQL NVARCHAR(MAX)

        SET @LANGUAGES_SQL =
                    'CREATE SEQUENCE sys_languages_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@LANGUAGES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@LANGUAGES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[sys_permissions_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @PERMISSIONS_START as BIGINT;
        set
            @PERMISSIONS_START = COALESCE((SELECT MAX(ID) + 1 FROM SYS_PERMISSIONS), 1)
        DECLARE
            @PERMISSIONS_SQL NVARCHAR(MAX)

        SET @PERMISSIONS_SQL =
                    'CREATE SEQUENCE sys_permissions_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@PERMISSIONS_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@PERMISSIONS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[sys_roles_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @ROLES_START as BIGINT;
        set
            @ROLES_START = COALESCE((SELECT MAX(ID) + 1 FROM SYS_ROLES), 1)
        DECLARE
            @ROLES_SQL NVARCHAR(MAX)

        SET @ROLES_SQL =
                    'CREATE SEQUENCE sys_roles_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@ROLES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@ROLES_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[sys_users_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @USERS_START as BIGINT;
        set
            @USERS_START = COALESCE((SELECT MAX(ID) + 1 FROM SYS_USERS), 1)
        DECLARE
            @USERS_SQL NVARCHAR(MAX)

        SET @USERS_SQL =
                    'CREATE SEQUENCE sys_users_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@USERS_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@USERS_SQL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[sys_user_states_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @USER_STATES_START as BIGINT;
        set
            @USER_STATES_START = COALESCE((SELECT MAX(ID) + 1 FROM SYS_USER_STATES), 1)
        DECLARE
            @USERS_STATES_SQL NVARCHAR(MAX)

        SET @USERS_STATES_SQL =
                    'CREATE SEQUENCE sys_user_states_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@USER_STATES_START as varchar) +
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@USERS_STATES_SQL);
    END;


IF
    EXISTS(select *
           from sys.tables
           where name = 'SYS_ID_GENERATOR')
    BEGIN
        DROP TABLE SYS_ID_GENERATOR
    END

IF
    NOT EXISTS(SELECT *
               FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
               WHERE CONSTRAINT_NAME = 'IN_BANKS_CODE_UNIQUE')
    BEGIN
        ALTER TABLE IN_BANKS
            ADD CONSTRAINT IN_BANKS_CODE_UNIQUE UNIQUE (CODE);
    END;


IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Name = 'IS_DELETED'
             AND Object_ID = Object_ID('IN_MDT_CATALOG_ITEMS'))
    BEGIN
        alter table IN_MDT_CATALOG_ITEMS
            drop
                column IS_DELETED;
    END;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_COMMUNICATOR_NOTIFICATION_USERS')
    exec sp_rename 'SYS_NOTIFICATION_USERS', 'IN_COMMUNICATOR_NOTIFICATION_USERS';


GO

IF
        (SELECT DATA_TYPE
         FROM INFORMATION_SCHEMA.COLUMNS
         WHERE table_name = 'CEMS_SANCTIONS'
           and COLumn_NAME = 'STATUS') = 'varchar'
    BEGIN
        alter table CEMS_SANCTIONS
            add STATUS_TMP int;
    End
GO


IF
        (SELECT DATA_TYPE
         FROM INFORMATION_SCHEMA.COLUMNS
         WHERE table_name = 'CEMS_SANCTIONS'
           and COLumn_NAME = 'STATUS') = 'varchar'
    BEGIN
        UPDATE CEMS_SANCTIONS
        SET STATUS_TMP = CASE
                             WHEN STATUS = 'IN_PROGRESS' THEN 0
                             WHEN STATUS = 'HALF_REALIZED' THEN 1
                             WHEN STATUS = 'COMPLETED_OUT_OF_DATE' THEN 2
                             WHEN STATUS = 'COMPLETED' THEN 3
                             WHEN STATUS = 'NOT_COMPLETED' THEN 4
                             WHEN STATUS = 'DISMISSED_BY_CHAIRMAN' THEN 5
                             WHEN STATUS = 'DISMISSED_BY_BOARD' THEN 6
                             WHEN STATUS = 'DISMISSED_BY_COMMITTEE' THEN 7
                             WHEN STATUS = 'COURT_CASE' THEN 8
                             WHEN STATUS = 'POSTPONED' THEN 9
                             WHEN STATUS is null THEN 0
            END
        WHERE STATUS is not null;

        alter table CEMS_SANCTIONS
            drop column STATUS;
        exec sp_rename 'CEMS_SANCTIONS.STATUS_TMP', STATUS, 'COLUMN';
    END;

GO

IF EXISTS (SELECT 1
           FROM sys.columns
           WHERE Name = 'DESCRIPTION'
             AND Object_ID = Object_ID('IN_MANAGEMENT_FORM_TYPE'))
    IF EXISTS (SELECT 1
               FROM sys.columns
               WHERE Name = 'DESCRIPTION'
                 AND Object_ID = Object_ID('IN_MANAGEMENT_FORM_TYPE'))
        begin
            -- IN_MANAGEMENT_FORM_TYPE
            IF NOT EXISTS (SELECT 1
                           FROM sys.columns
                           WHERE Name = 'NAMESTRID'
                             AND Object_ID = Object_ID('IN_MANAGEMENT_FORM_TYPE'))
                BEGIN
                    ALTER TABLE IN_MANAGEMENT_FORM_TYPE
                        ADD NAMESTRID DECIMAL;
                END
            IF OBJECT_ID('ManagementFormTypeProcedure', 'P') IS NOT NULL
                DROP PROCEDURE ManagementFormTypeProcedure;
            EXEC ('CREATE PROCEDURE ManagementFormTypeProcedure
AS
BEGIN
    DECLARE @CursorID INT;
    DECLARE @RowCnt BIGINT = 0;
    DECLARE @langId INT;
    DECLARE @value VARCHAR(255);
    DECLARE @NextValue BIGINT;
    CREATE TABLE #InsertedIDs (ID INT);
    SELECT @RowCnt = COUNT(*) FROM IN_MANAGEMENT_FORM_TYPE;
    SELECT @langId = id FROM SYS_LANGUAGES WHERE CODE = ''en_US'';
    SET @CursorID = (SELECT MIN(ID) FROM IN_MANAGEMENT_FORM_TYPE);
    WHILE @CursorID IS NOT NULL
    BEGIN
        SELECT @value = description FROM IN_MANAGEMENT_FORM_TYPE WHERE ID = @CursorID;
        SET @NextValue = NEXT VALUE FOR sys_string_sequence;
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        OUTPUT inserted.ID INTO #InsertedIDs
        VALUES (@NextValue, @langId, @value);
        DECLARE @InsertedID INT;
        SELECT TOP 1 @InsertedID = ID FROM #InsertedIDs ORDER BY ID DESC;
        UPDATE IN_MANAGEMENT_FORM_TYPE
        SET NAMESTRID = @InsertedID
        WHERE description = @value;
        SET @CursorID = (SELECT MIN(ID) FROM IN_MANAGEMENT_FORM_TYPE WHERE ID > @CursorID);
    END
    DROP TABLE #InsertedIDs;
END;
')
            EXEC ('EXEC ManagementFormTypeProcedure;')
            EXEC ('DROP PROCEDURE ManagementFormTypeProcedure;')
            IF EXISTS (SELECT 1
                       FROM INFORMATION_SCHEMA.COLUMNS
                       WHERE TABLE_NAME = 'IN_MANAGEMENT_FORM_TYPE'
                         AND COLUMN_NAME = 'DESCRIPTION')
                BEGIN
                    ALTER TABLE IN_MANAGEMENT_FORM_TYPE
                        DROP COLUMN DESCRIPTION;
                END
        end


-- IN_ECONOMIC_ENTITY_TYPE
IF EXISTS (SELECT 1
           FROM sys.columns
           WHERE Name = 'DESCRIPTION'
             AND Object_ID = Object_ID('IN_ECONOMIC_ENTITY_TYPE'))
    IF EXISTS (SELECT 1
               FROM sys.columns
               WHERE Name = 'DESCRIPTION'
                 AND Object_ID = Object_ID('IN_ECONOMIC_ENTITY_TYPE'))
        begin

            IF NOT EXISTS (SELECT 1
                           FROM sys.columns
                           WHERE Name = 'NAMESTRID'
                             AND Object_ID = Object_ID('IN_ECONOMIC_ENTITY_TYPE'))
                BEGIN
                    ALTER TABLE IN_ECONOMIC_ENTITY_TYPE
                        ADD NAMESTRID DECIMAL;
                END
            IF OBJECT_ID('EconomicEntityTypeProcedure', 'P') IS NOT NULL
                DROP PROCEDURE EconomicEntityTypeProcedure;
            EXEC ('CREATE PROCEDURE EconomicEntityTypeProcedure
AS
BEGIN
    DECLARE @CursorID INT;
    DECLARE @RowCnt BIGINT = 0;
    DECLARE @langId INT;
    DECLARE @value VARCHAR(255);
    DECLARE @NextValue BIGINT;
    CREATE TABLE #InsertedIDs (ID INT);
    SELECT @RowCnt = COUNT(*) FROM IN_ECONOMIC_ENTITY_TYPE;
    SELECT @langId = id FROM SYS_LANGUAGES WHERE CODE = ''en_US'';
    SET @CursorID = (SELECT MIN(ID) FROM IN_ECONOMIC_ENTITY_TYPE);
    WHILE @CursorID IS NOT NULL
    BEGIN
        SELECT @value = description FROM IN_ECONOMIC_ENTITY_TYPE WHERE ID = @CursorID;
        SET @NextValue = NEXT VALUE FOR sys_string_sequence;
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        OUTPUT inserted.ID INTO #InsertedIDs
        VALUES (@NextValue, @langId, @value);
        DECLARE @InsertedID INT;
        SELECT TOP 1 @InsertedID = ID FROM #InsertedIDs ORDER BY ID DESC;
        UPDATE IN_ECONOMIC_ENTITY_TYPE
        SET NAMESTRID = @InsertedID
        WHERE description = @value;
        SET @CursorID = (SELECT MIN(ID) FROM IN_ECONOMIC_ENTITY_TYPE WHERE ID > @CursorID);
    END
    DROP TABLE #InsertedIDs;
END;
')
            EXEC ('EXEC EconomicEntityTypeProcedure;')
            EXEC ('DROP PROCEDURE EconomicEntityTypeProcedure;')
            IF EXISTS (SELECT 1
                       FROM INFORMATION_SCHEMA.COLUMNS
                       WHERE TABLE_NAME = 'IN_ECONOMIC_ENTITY_TYPE'
                         AND COLUMN_NAME = 'DESCRIPTION')
                BEGIN
                    ALTER TABLE IN_ECONOMIC_ENTITY_TYPE
                        DROP COLUMN DESCRIPTION;
                END
        end


-- IN_EQUITY_FORM_TYPE
IF EXISTS (SELECT 1
           FROM sys.columns
           WHERE Name = 'DESCRIPTION'
             AND Object_ID = Object_ID('IN_EQUITY_FORM_TYPE'))
    IF EXISTS (SELECT 1
               FROM sys.columns
               WHERE Name = 'DESCRIPTION'
                 AND Object_ID = Object_ID('IN_EQUITY_FORM_TYPE'))
        begin


            IF NOT EXISTS (SELECT 1
                           FROM sys.columns
                           WHERE Name = 'NAMESTRID'
                             AND Object_ID = Object_ID('IN_EQUITY_FORM_TYPE'))
                BEGIN
                    ALTER TABLE IN_EQUITY_FORM_TYPE
                        ADD NAMESTRID DECIMAL;
                END
            IF OBJECT_ID('EquityFormTypeProcedure', 'P') IS NOT NULL
                DROP PROCEDURE EquityFormTypeProcedure;
            EXEC ('CREATE PROCEDURE EquityFormTypeProcedure
AS
BEGIN
    DECLARE @CursorID INT;
    DECLARE @RowCnt BIGINT = 0;
    DECLARE @langId INT;
    DECLARE @value VARCHAR(255);
    DECLARE @NextValue BIGINT;
    CREATE TABLE #InsertedIDs (ID INT);
    SELECT @RowCnt = COUNT(*) FROM IN_EQUITY_FORM_TYPE;
    SELECT @langId = id FROM SYS_LANGUAGES WHERE CODE = ''en_US'';
    SET @CursorID = (SELECT MIN(ID) FROM IN_EQUITY_FORM_TYPE);
    WHILE @CursorID IS NOT NULL
    BEGIN
        SELECT @value = description FROM IN_EQUITY_FORM_TYPE WHERE ID = @CursorID;
        SET @NextValue = NEXT VALUE FOR sys_string_sequence;
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        OUTPUT inserted.ID INTO #InsertedIDs
        VALUES (@NextValue, @langId, @value);
        DECLARE @InsertedID INT;
        SELECT TOP 1 @InsertedID = ID FROM #InsertedIDs ORDER BY ID DESC;
        UPDATE IN_EQUITY_FORM_TYPE
        SET NAMESTRID = @InsertedID
        WHERE description = @value;
        SET @CursorID = (SELECT MIN(ID) FROM IN_EQUITY_FORM_TYPE WHERE ID > @CursorID);
    END
    DROP TABLE #InsertedIDs;
END;
')
            EXEC ('EXEC EquityFormTypeProcedure;')
-- Drop EquityFormTypeProcedure
            EXEC ('DROP PROCEDURE EquityFormTypeProcedure;')
            IF EXISTS (SELECT 1
                       FROM INFORMATION_SCHEMA.COLUMNS
                       WHERE TABLE_NAME = 'IN_EQUITY_FORM_TYPE'
                         AND COLUMN_NAME = 'DESCRIPTION')
                BEGIN
                    ALTER TABLE IN_EQUITY_FORM_TYPE
                        DROP COLUMN DESCRIPTION;
                END
        END


-- IN_BUSINESS_ENTITY_TYPE
IF EXISTS (SELECT 1
           FROM sys.columns
           WHERE Name = 'DESCRIPTION'
             AND Object_ID = Object_ID('IN_BUSINESS_ENTITY_TYPE'))
    IF EXISTS (SELECT 1
               FROM sys.columns
               WHERE Name = 'DESCRIPTION'
                 AND Object_ID = Object_ID('IN_BUSINESS_ENTITY_TYPE'))
        begin


            IF NOT EXISTS (SELECT 1
                           FROM sys.columns
                           WHERE Name = 'NAMESTRID'
                             AND Object_ID = Object_ID('IN_BUSINESS_ENTITY_TYPE'))
                BEGIN
                    ALTER TABLE IN_BUSINESS_ENTITY_TYPE
                        ADD NAMESTRID DECIMAL;
                END
            IF OBJECT_ID('BusinessEntityTypeProcedure', 'P') IS NOT NULL
                DROP PROCEDURE BusinessEntityTypeProcedure;
            EXEC ('CREATE PROCEDURE BusinessEntityTypeProcedure
AS
BEGIN
    DECLARE @CursorID INT;
    DECLARE @RowCnt BIGINT = 0;
    DECLARE @langId INT;
    DECLARE @value VARCHAR(255);
    DECLARE @NextValue BIGINT;
    CREATE TABLE #InsertedIDs (ID INT);
    SELECT @RowCnt = COUNT(*) FROM IN_BUSINESS_ENTITY_TYPE;
    SELECT @langId = id FROM SYS_LANGUAGES WHERE CODE = ''en_US'';
    SET @CursorID = (SELECT MIN(ID) FROM IN_BUSINESS_ENTITY_TYPE);
    WHILE @CursorID IS NOT NULL
    BEGIN
        SELECT @value = description FROM IN_BUSINESS_ENTITY_TYPE WHERE ID = @CursorID;
        SET @NextValue = NEXT VALUE FOR sys_string_sequence;
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        OUTPUT inserted.ID INTO #InsertedIDs
        VALUES (@NextValue, @langId, @value);
        DECLARE @InsertedID INT;
        SELECT TOP 1 @InsertedID = ID FROM #InsertedIDs ORDER BY ID DESC;
        UPDATE IN_BUSINESS_ENTITY_TYPE
        SET NAMESTRID = @InsertedID
        WHERE description = @value;
        SET @CursorID = (SELECT MIN(ID) FROM IN_BUSINESS_ENTITY_TYPE WHERE ID > @CursorID);
    END
    DROP TABLE #InsertedIDs;
END;
')
            EXEC ('EXEC BusinessEntityTypeProcedure;')
            EXEC ('DROP PROCEDURE BusinessEntityTypeProcedure;')
            IF EXISTS (SELECT 1
                       FROM INFORMATION_SCHEMA.COLUMNS
                       WHERE TABLE_NAME = 'IN_BUSINESS_ENTITY_TYPE'
                         AND COLUMN_NAME = 'DESCRIPTION')
                BEGIN
                    ALTER TABLE IN_BUSINESS_ENTITY_TYPE
                        DROP COLUMN DESCRIPTION;
                END
        END


-- permissions insert script
IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.cems.review')
    BEGIN
        DECLARE
            @ss_id INT
        DECLARE
            @sp_id INT
        DECLARE
            @lang_id INT

        SET @ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @lang_id = (SELECT id
                        FROM sys_languages
                        WHERE code LIKE '%en%')

        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@ss_id, @lang_id, 'CEMS Review');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@sp_id, @ss_id, 'net.fina.cems.review');
    END;


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'HAS_ATTACHMENTS'
                 AND Object_ID = Object_ID('IN_COMMUNICATOR_MESSAGES'))
    BEGIN
        alter table IN_COMMUNICATOR_MESSAGES
            add HAS_ATTACHMENTS bit not null default 0;
        exec ('update IN_COMMUNICATOR_MESSAGES set HAS_ATTACHMENTS=1 where ID in(select MESSAGE_ID from IN_COMMUNICATOR_ATTACHEMENTS where TYPE=1)')
    END;


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'HAS_ATTACHMENTS'
                 AND Object_ID = Object_ID('IN_COMMUNICATOR_NOTIFICATIONS'))
    BEGIN
        alter table IN_COMMUNICATOR_NOTIFICATIONS
            add HAS_ATTACHMENTS bit not null default 0;
        exec ('update IN_COMMUNICATOR_NOTIFICATIONS set HAS_ATTACHMENTS=1 where ID in(select MESSAGE_ID from IN_COMMUNICATOR_ATTACHEMENTS where TYPE=0)')
    END;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'RECIPIENTS_SIZE'
                 AND Object_ID = Object_ID('IN_COMMUNICATOR_MESSAGES'))
    BEGIN
        alter table IN_COMMUNICATOR_MESSAGES
            add RECIPIENTS_SIZE int;
        exec ('update IN_COMMUNICATOR_MESSAGES set RECIPIENTS_SIZE=(select count(*) from IN_MESSAGE_USERS where MESSAGE_ID=IN_COMMUNICATOR_MESSAGES.ID)')
    END;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'RECIPIENT_READ_DATE'
                 AND Object_ID = Object_ID('IN_COMMUNICATOR_MESSAGES'))
    BEGIN
        alter table IN_COMMUNICATOR_MESSAGES
            add RECIPIENT_READ_DATE datetime;
        exec ('update IN_COMMUNICATOR_MESSAGES set RECIPIENT_READ_DATE=(select max(READ_DATE) from IN_COMMUNICATOR_USERS_MESSAGE_STATUS where LAST_MESSAGE_ID=IN_COMMUNICATOR_MESSAGES.ID group by LAST_MESSAGE_ID)')
    END;

IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IN_COMMUNICATOR_STATUS_INDEX_2'
            AND object_id = OBJECT_ID('IN_COMMUNICATOR_USERS_MESSAGE_STATUS'))
    drop index IN_COMMUNICATOR_STATUS_INDEX_2 on IN_COMMUNICATOR_USERS_MESSAGE_STATUS;

IF
    NOT EXISTS(SELECT *
               FROM sys.indexes
               WHERE name = 'IN_COMMUNICATOR_STATUS_INDEX_2'
                 AND object_id = OBJECT_ID('IN_COMMUNICATOR_USERS_MESSAGE_STATUS'))
CREATE INDEX IN_COMMUNICATOR_STATUS_INDEX_2 ON [IN_COMMUNICATOR_USERS_MESSAGE_STATUS] ([message_id], [message_user_id]) INCLUDE ([id], [status], [USER_ID], [READ_DATE]);


IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Name = 'LAST_MESSAGE_ID'
             AND Object_ID = Object_ID('IN_COMMUNICATOR_USERS_MESSAGE_STATUS'))
    BEGIN
        alter table IN_COMMUNICATOR_USERS_MESSAGE_STATUS
            drop
                column LAST_MESSAGE_ID;
        exec ('truncate  table IN_COMMUNICATOR_USERS_MESSAGE_STATUS');
        ALTER SEQUENCE com_users_messages_sequence
            RESTART WITH 1
            INCREMENT BY 1;
    END;

IF
    not EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'PROCESS_STAGE'
                 AND Object_ID = Object_ID('IN_MDT_COMPARISON'))
    BEGIN
        alter table IN_MDT_COMPARISON
            add PROCESS_STAGE int;
        exec ('update IN_MDT_COMPARISON set PROCESS_STAGE=0 where PROCESS_STAGE is null');
    end;

IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Name = 'RESIDENT'
             AND Object_ID = Object_ID('IN_BANK_MANAGEMENT_AUD'))
    BEGIN
        ALTER TABLE IN_BANK_MANAGEMENT_AUD
            ALTER COLUMN RESIDENT BIT NULL
    END;


IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Name = 'DASHBOARD_ID'
             AND Object_ID = Object_ID('IN_DASHLETS'))
    BEGIN
        ALTER TABLE IN_DASHLETS
            DROP COLUMN DASHBOARD_ID
    END;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_TRANSLATIONS')
create table SYS_TRANSLATIONS
(
    KEY_CODE  nvarchar(300) not null,
    LANG_CODE varchar(50)   not null,
    VALUE     nvarchar(500) not null,
    constraint translations_primary_key
        primary key (KEY_CODE, LANG_CODE)
);


-- permissions insert script
IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.i18n.amend')
    BEGIN
        DECLARE
            @ss_id INT
        DECLARE
            @sp_id INT
        DECLARE
            @lang_id INT

        SET @ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @lang_id = (SELECT id
                        FROM sys_languages
                        WHERE code LIKE '%en%')

        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@ss_id, @lang_id, 'I18n translations ammend');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@sp_id, @ss_id, 'net.fina.i18n.amend');
    END;


--CEMS Move Enums to Database
IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTION_MEASURE_OF_REASONS')
create table CEMS_SANCTION_MEASURE_OF_REASONS
(
    ID        numeric            not null primary key,
    NAMESTRID int,
    CODE      varchar(50) unique not null
);
IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_sanction_measure_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE cems_sanction_measure_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


DECLARE @langIdMeasure INT;
DECLARE @codeMeasure NVARCHAR(255);
DECLARE @valueMeasure NVARCHAR(MAX);
DECLARE @sysStringNextValueMeasure INT;
DECLARE @prevKeyCodeMeasure NVARCHAR(255);
DECLARE @current_key_codeMeasure NVARCHAR(255);
DECLARE @cems_sanct_measure_seq int;



SET @sysStringNextValueMeasure = NEXT VALUE FOR sys_string_sequence;
SET @current_key_codeMeasure = '';
SET @prevKeyCodeMeasure = '';

CREATE TABLE #TempKeyValuePairs
(
    code  NVARCHAR(255),
    value NVARCHAR(MAX)
);

INSERT INTO #TempKeyValuePairs (code, value)
VALUES ('CODE_1_1',
        N'Наличие одного или нескольких признаков деятельности, действий (бездействия) банка, которые могут относиться к нездоровой и небезопасной банковской практике:'),
       ('CODE_1_2',
        N'Неспособность Совета директоров и Правления банка обеспечить адекватный контроль и руководство в целях предотвращения нездоровой и небезопасной банковской практики и нарушения законодательства.'),
       ('CODE_1_3',
        N'Создание неадекватного резерва на покрытие потенциальных потерь и убытков по активам и забалансовым обязательствам'),
       ('CODE_1_4',
        N'Неспособность вести учет в соответствии с МСФО и нормативными правовыми актами Национального банка.'),
       ('CODE_1_5',
        N'Осуществление операций, подпадающих под критерии подозрительных банковских операций и/или нарушения законодательства по противодействию финансированию терроризма и легализации (отмыванию) доходов полученных преступным путем'),
       ('CODE_1_6', N'Осуществление деятельности без надлежащего уровня внутреннего контроля.'),
       ('CODE_1_7', N'Отсутствие адекватного внутреннего аудита.'),
       ('CODE_1_8',
        N'Осуществление операций с инсайдерами и аффилированными лицами банка в нарушение требований Национального банка'),
       ('CODE_1_9',
        N'Осуществление деятельности, для которой в банке отсутствуют квалифицированные и опытные сотрудники.'),
       ('CODE_1_10',
        N'Осуществление деятельности при отсутствии соответствующих политик и/или процедур, или когда они не соответствуют масштабам деятельности и уровню рисков банка.'),
       ('CODE_1_11', N'Проведение операций, не соответствующих стандартам Шариата (только для исламского банка).'),
       ('CODE_1_12',
        N'Предоставление кредитов заемщикам, которые не выполнили свои обязательства перед этим банком или каким-либо другим банком, или предоставление кредитов заемщикам с неоднократной пролонгацией кредитов и нестабильным финансовым положением.'),
       ('CODE_1_13', N'Представление несвоевременных, неполных или недостоверных отчетов в Национальный банк:'),
       ('CODE_1_14', N'Неисполнение банком условий письменного соглашения'),
       ('CODE_1_15',
        N'Отсутствие, либо не соблюдение внутренней политики банка по выдачи кредитов и, как следствие,  рост невозврата кредитов'),
       ('CODE_1_16', N'Наличие финансовых проблем, не разрешаемых банком в течение предписанного времени'),
       ('CODE_1_17',
        N'Действия должностных лиц банка, которые расцениваются как рискованные и могут повлиять на благополучное состояние банка'),
       ('CODE_1_18', N'Потенциальная угроза интересам вкладчиков и других кредиторов'),
       ('CODE_1_19', N'Выявление фактов нарушений законодательства и нормативных актов Национального банка.'),
       ('CODE_1_20', N'Нарушение принципа беспристрастности'),
       ('CODE_1_21',
        N'Наличие действий, которые подвергают риску потерь средства вкладчиков и финансовую стабильность банка'),
       ('CODE_1_22',
        N'Должностное лицо уличено в мошенничестве, злоупотреблении полномочиями или подобными действиями'),
       ('CODE_1_23',
        N'Установления фактов нарушений в деятельности банка, связанных с неспособностью администрации банка обеспечить работу банка в соответствии с банковским законодательством'),
       ('CODE_1_24', N'Неудовлетворительное финансовое состояние банка'),
       ('CODE_1_25', N'Возникновение разногласий между органами управления банка, дезорганизующих его работу'),
       ('CODE_1_26', N'Потеря руководства банком в связи со сменой руководства'),
       ('CODE_1_27', N'Нарушения должностными лицами банка законодательства'),
       ('CODE_1_28',
        N'Возбуждения уголовного дела в отношении руководящих и должностных лиц или начала уголовного расследования в их отношении'),
       ('CODE_1_29', N'Риск признания банка банкротом'),
       ('CODE_1_30',
        N'Если требования по исправлению нарушений не осуществляются в течение предписанного периода времени'),
       ('CODE_1_31',
        N'Капитал банка в течение предшествующих 6 месяцев составляет более 50%, но менее 100% от установленного уровня капитала'),
       ('CODE_1_32', N'Банк не выполняет распоряжения, указания, требования и/или предписания Национального банка'),
       ('CODE_1_33',
        N'В соответствии с законодательством Кыргызской Республики имеются основания для отзыва лицензии банка'),
       ('CODE_1_34',
        N'Банк нарушает законодательство и/или нормативные правовые акты по противодействию финансированию терроризма и легализации (отмыванию) доходов полученных преступным путем.'),
       ('CODE_1_35',
        N'Капитал банка составляет менее 50% капитала, требуемого в соответствии с нормативными актами Национального банка'),
       ('CODE_1_36',
        N'Банк прекратил оплачивать свои обязательства после наступления сроков платежа, даже если по этим платежам не существует судебных споров'),
       ('CODE_1_37',
        N'Получено извещение суда о принятии им заявления о возбуждении процесса банкротства или процедуры ликвидации в отношении банка'),
       ('CODE_1_38',
        N'Существует необходимость завершения добровольной ликвидации банка консерватором в соответствии с Законом Кыргызской Республики "О консервации, ликвидации и банкротстве банков"'),
       ('CODE_1_39',
        N'В отношении должностных лиц банка возбуждено уголовное дело (по обвинению в совершении экономических и должностных преступлений) в связи с исполнением служебных обязанностей.'),
       ('CODE_1_40',
        N'Систематическое (два и более раза в течение 12 последовательных календарных месяцев) невыполнение обязательных предписаний Национального банка'),
       ('CODE_1_41',
        N'Коэффициент адекватности суммарного капитала банка составляет два процента или будет находиться ниже необходимого уровня в ближайшее время по расчетам Национального банка и учредители, несмотря на предварительное предупреждение, не пополняют капитал и/или не предпринимают меры по его пополнению сверх этого уровня в течение срока, определенного Национальным банком'),
       ('CODE_1_42',
        N'Систематическое (два и более раза в течение 12 последовательных календарных месяцев) ненадлежащее выполнения или невыполнения нормативных актов Банка Кыргызстана'),
       ('CODE_1_43',
        N'Систематическое предоставлении недостоверной информации или обнаружении в течение года со дня выдачи лицензии недостоверных сведений, на основании которых она была выдана'),
       ('CODE_1_44',
        N'Принятие участия в операциях, запрещенных законодательством Кыргызской Республики, либо занятия деятельностью, не предусмотренной лицензией'),
       ('CODE_1_45',
        N'Неплатежеспособности или признания Банком Кыргызстана неплатежеспособным в соответствии с законодательством Кыргызской Республики'),
       ('CODE_1_46',
        N'Банк откладывает начало деятельности по принятию депозитов и выдаче кредитов на срок более одного года после его регистрации и получения банковской лицензии'),
       ('CODE_1_47', N'Невыполнении банком требований антимонопольного законодательства'),
       ('CODE_1_48', N'Другое - заполняется в свободной форме'),
       ('CODE_2_1', N'сокрытие конфликта интересов и/или заинтересованности при заключении сделок банка'),
       ('CODE_2_2',
        N'сокрытие информации, которая свидетельствует о несоответствии должностного лица квалификационным требованиям, требованиям по независимости и безупречной деловой репутации'),
       ('CODE_2_3',
        N'действие или бездействие члена Совета директоров банка, повлекшее угрозу финансовой стабильности банка, по оценке Национального банка'),
       ('CODE_2_4',
        N'нарушение банковского законодательства и/или нормативных правовых актов Национального банка, повлекшие существенные финансовые потери и/или репутационные риски'),
       ('CODE_2_5',
        N'участие в деятельности (действия/бездействие), которая характеризуется как нездоровая и небезопасная банковская практика'),
       ('CODE_2_6',
        N'осуществление или вовлечение в деятельность, нарушающую нормативные правовые акты Кыргызской Республики по вопросам противодействия финансированию террористической деятельности и легализации (отмыванию) преступных доходов'),
       ('CODE_2_7', N'злоупотребление своими полномочиями/ должностным положением'),
       ('CODE_2_8', N'привлечение должностного лица в качестве подозреваемого/обвиняемого в уголовном деле'),
       ('CODE_2_9',
        N'действие или бездействие должностных лиц операторов платежных систем/платежных организаций, которые влекут за собой приостановление/отзыв лицензии, принудительную ликвидацию или банкротство оператора платежной системы/платежной организации'),
       ('CODE_2_10', N'невыполнения требований/предписаний Национального банка'),
       ('CODE_2_11',
        N'систематического (два и более раз в течение 12 последовательных календарных месяцев) непредставления финансовой или другой отчетности либо представления несвоевременной/недостоверной/неполной информации, финансовой или другой отчетности'),
       ('CODE_2_12',
        N'нарушение банковского законодательства Кыргызской Республики, антимонопольного законодательства'),
       ('CODE_2_13',
        N'несоответствие должностных лиц требованиям нормативных правовых актов Национального банка или невыполнения/ненадлежащего выполнения должностных функций;'),
       ('CODE_2_14',
        N'осуществление ненадлежащего уровня внутреннего контроля и управления рисками деятельности оператора платежных систем/платежной организации в соответствии с Политикой по управлению рисками в платежной системе Кыргызской Республики, утвержденной постановлением Правления Национального банка Кыргызской Республики от 15 июня 2016 года № 25/8;'),
       ('CODE_2_15',
        N'неспособность должностных лиц обеспечить исполнение распоряжений органов управления самой организации и регулирующих органов по осуществлению деятельности оператора платежной системы/платежной организации, согласно уставу'),
       ('CODE_2_16',
        N'осуществление операций с аффилированными и связанными с ними лицами в нарушение требований Национального банка или законодательства Кыргызской Республики'),
       ('CODE_2_17',
        N'непредоставление в Национальный банк информации либо предоставления несвоевременной/недостоверной/неполной информации операторами платежных систем/платежных организаций или их органами управления/должностными лицами'),
       ('CODE_2_18',
        N'инициирование или осуществление действий, в том числе наличия внутренних конфликтных ситуаций или разбирательств, в том числе с привлечением третьих лиц, правоохранительных или судебных органов, а также между участниками/акционерами операторов платежных систем/платежных организаций, угрожающих стабильности платежной системы, ее бесперебойному, безопасному функционированию, своевременному осуществлению платежей, защите прав потребителей платежных услуг, финансовой стабильности оператора платежной системы/платежной организации'),
       ('CODE_2_19',
        N'Нарушение требований статей  209-215, статьи 216 ( в части 1 и 2), 243, 264-266 Кодекса Кыргызской Республики о нарушениях'),
       ('CODE_2_20', N'Другое - заполняется в свободной форме');

DECLARE cursorCodes CURSOR FOR
    SELECT code, value
    FROM #TempKeyValuePairs;

OPEN cursorCodes;

FETCH NEXT FROM cursorCodes INTO @codeMeasure, @valueMeasure;

WHILE @@FETCH_STATUS = 0
    BEGIN
        DECLARE cursorLangIds CURSOR FOR
            SELECT DISTINCT id
            FROM sys_languages;

        OPEN cursorLangIds;

        FETCH NEXT FROM cursorLangIds INTO @langIdMeasure;

        WHILE @@FETCH_STATUS = 0
            BEGIN
                IF @current_key_codeMeasure <> @codeMeasure
                    BEGIN
                        SET @sysStringNextValueMeasure = NEXT VALUE FOR sys_string_sequence;
                        SET @current_key_codeMeasure = @codeMeasure;
                        SET @cems_sanct_measure_seq = NEXT VALUE FOR cems_sanction_measure_sequence;

                        INSERT INTO CEMS_SANCTION_MEASURE_OF_REASONS (ID, NAMESTRID, CODE)
                        VALUES (@cems_sanct_measure_seq, @sysStringNextValueMeasure, @current_key_codeMeasure);
                    END

                INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
                VALUES (@sysStringNextValueMeasure, @langIdMeasure, @valueMeasure);

                FETCH NEXT FROM cursorLangIds INTO @langIdMeasure;
            END;

        CLOSE cursorLangIds;
        DEALLOCATE cursorLangIds;

        FETCH NEXT FROM cursorCodes INTO @codeMeasure, @valueMeasure;
    END;

CLOSE cursorCodes;
DEALLOCATE cursorCodes;

DROP TABLE #TempKeyValuePairs;

go
IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTION_MEASURE_REASONS_TABLE')
create table CEMS_SANCTION_MEASURE_REASONS_TABLE
(
    SANCTION_ID numeric,
    REASON_ID   numeric
);

INSERT INTO CEMS_SANCTION_MEASURE_REASONS_TABLE (sanction_id, reason_id)
SELECT m.SANCTION_ID, r.ID
FROM CEMS_SANCTION_MEASURE_REASONS m
         INNER JOIN CEMS_SANCTION_MEASURE_OF_REASONS r ON m.MEASURE_REASON = r.code;

go

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTION_MEASURE_INFLUENCE')
create table CEMS_SANCTION_MEASURE_INFLUENCE
(
    ID        numeric            not null primary key,
    NAMESTRID int,
    CODE      varchar(50) unique not null
);
IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_sanction_measure_infl_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE cems_sanction_measure_infl_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


DECLARE @langIdMeasureInfl INT;
DECLARE @codeMeasureInfl NVARCHAR(255);
DECLARE @valueMeasureInfl NVARCHAR(MAX);
DECLARE @sysStringNextValueMeasureInfl INT;
DECLARE @prevKeyCodeMeasureInfl NVARCHAR(255);
DECLARE @current_key_codeMeasureInfl NVARCHAR(255);
DECLARE @cems_sanct_measure_infl_seq int;


SET @sysStringNextValueMeasureInfl = NEXT VALUE FOR sys_string_sequence;
SET @current_key_codeMeasureInfl = '';
SET @prevKeyCodeMeasureInfl = '';

CREATE TABLE #TempKeyValuePairs
(
    code  NVARCHAR(255),
    value NVARCHAR(MAX)
);

INSERT INTO #TempKeyValuePairs (code, value)
VALUES ('CODE_1_1', N'устранение замечаний в рамках предварительных действий органа надзора в отношении банка'),
       ('CODE_1_2', N'направление в Национальный банк письма-обязательства банка'),
       ('CODE_1_3', N'подписание письменного соглашения'),
       ('CODE_2_1', N'предписание'),
       ('CODE_2_2', N'Повышение экономических нормативов и требований'),
       ('CODE_2_3', N'повышение норм обязательных резервов'),
       ('CODE_2_4', N'взыскание денежного штрафа с банка'),
       ('CODE_2_5', N'взыскание штрафа с отдельных должностных лиц'),
       ('CODE_2_6',
        N'требование по проведению мероприятий по финансовому оздоровлению, реструктуризации и/или реорганизации банка'),
       ('CODE_2_7', N'требование по проведению повторного/специального внешнего аудита банка'),
       ('CODE_2_8', N'требование по продаже акций банка в течение определенного Национальным банком срока'),
       ('CODE_2_9', N'требование по сокращению административных расходов'),
       ('CODE_2_10', N'требование по изменению организационной структуры банка'),
       ('CODE_2_11', N'ограничения и запреты'),
       ('CODE_2_12', N'Отстранение или освобождение должностных лиц, смена органов управления'),
       ('CODE_2_13', N'введение прямого банковского надзора'),
       ('CODE_2_14', N'введение Временной администрации'),
       ('CODE_2_15', N'назначение квалифицированного консультанта и проведение внепланового внешнего аудита'),
       ('CODE_2_16', N'временное приостановление действия лицензии'),
       ('CODE_2_17', N'отзыв лицензии'),
       ('CODE_3_1', N'Письменное предупреждение'),
       ('CODE_3_2', N'Другое - заполняется в свободной форме'),
       ('CODE_4_1', N'Предписание'),
       ('CODE_4_2', N'Штраф/Штраф по Кодексу Кыргызской Республики о нарушениях'),
       ('CODE_4_3', N'отстранение должностного лица от занимаемой должности'),
       ('CODE_4_4', N'освобождение должностного лица от занимаемой должности'),
       ('CODE_4_5', N'Другое - заполняется в свободной форме');



DECLARE cursorCodes CURSOR FOR
    SELECT code, value
    FROM #TempKeyValuePairs;

OPEN cursorCodes;

FETCH NEXT FROM cursorCodes INTO @codeMeasureInfl, @valueMeasureInfl;

WHILE @@FETCH_STATUS = 0
    BEGIN
        DECLARE cursorLangIds CURSOR FOR
            SELECT DISTINCT id
            FROM sys_languages;

        OPEN cursorLangIds;

        FETCH NEXT FROM cursorLangIds INTO @langIdMeasureInfl;

        WHILE @@FETCH_STATUS = 0
            BEGIN
                IF @current_key_codeMeasureInfl <> @codeMeasureInfl
                    BEGIN
                        SET @sysStringNextValueMeasureInfl = NEXT VALUE FOR sys_string_sequence;
                        SET @current_key_codeMeasureInfl = @codeMeasureInfl;
                        SET @cems_sanct_measure_infl_seq = NEXT VALUE FOR cems_sanction_measure_infl_sequence;

                        INSERT INTO CEMS_SANCTION_MEASURE_INFLUENCE (ID, NAMESTRID, CODE)
                        VALUES (@cems_sanct_measure_infl_seq, @sysStringNextValueMeasureInfl,
                                @current_key_codeMeasureInfl);
                    END

                INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
                VALUES (@sysStringNextValueMeasureInfl, @langIdMeasureInfl, @valueMeasureInfl);

                FETCH NEXT FROM cursorLangIds INTO @langIdMeasureInfl;
            END;

        CLOSE cursorLangIds;
        DEALLOCATE cursorLangIds;

        FETCH NEXT FROM cursorCodes INTO @codeMeasureInfl, @valueMeasureInfl;
    END;

CLOSE cursorCodes;
DEALLOCATE cursorCodes;

DROP TABLE #TempKeyValuePairs;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTION_MEASURE_INFLUENCE_TABLE')
create table CEMS_SANCTION_MEASURE_INFLUENCE_TABLE
(
    SANCTION_ID  numeric,
    INFLUENCE_ID numeric
);


INSERT INTO CEMS_SANCTION_MEASURE_INFLUENCE_TABLE (sanction_id, INFLUENCE_ID)
SELECT r.SANCTION_ID, m.id
FROM CEMS_SANCTION_MEASURE_INFLUENCE m
         INNER JOIN CEMS_SANCTION_MEASURE_OF_INFLUENCE r ON m.code = r.MEASURE_OF_INFLUENCE;
go
IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTION_REGULATION_TABLE')
create table CEMS_SANCTION_REGULATION_TABLE
(
    ID        numeric            not null primary key,
    NAMESTRID int,
    CODE      varchar(50) unique not null
);
IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_sanction_reg_table_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE cems_sanction_reg_table_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


--CEMS_SANCTION_REGULATION_TABLE
DECLARE @langid_reg INT;
DECLARE @codeReg NVARCHAR(255);
DECLARE @valueReg NVARCHAR(MAX);
DECLARE @sysStringNextValueReg INT;
DECLARE @prevKeyCodeReg NVARCHAR(255);
DECLARE @current_key_codeReg NVARCHAR(255);
DECLARE @cems_sanct_reg_seq int;


SET @sysStringNextValueReg = NEXT VALUE FOR sys_string_sequence;
SET @current_key_codeReg = '';
SET @prevKeyCodeReg = '';

CREATE TABLE #TempKeyValuePairs
(
    code  NVARCHAR(255),
    value NVARCHAR(MAX)
);

INSERT INTO #TempKeyValuePairs (code, value)
VALUES ('CODE_1', N'Максимальный размер риска на одного заемщика (кроме банков), не связанного с банком'),
       ('CODE_2', N'Максимальный размер риска на одного заемщика (банка), не связанного с банком'),
       ('CODE_3', N'Максимальный размер риска на одного заемщика (кроме банков), связанного с банком'),
       ('CODE_4', N'Максимальный размер риска на одного заемщика (банка), связанного с банком'),
       ('CODE_5', N'Коэффициент адекватности суммарного капитала'),
       ('CODE_6', N'коэффициент адекватности капитала Первого уровня'),
       ('CODE_7', N'коэффициент достаточности (адекватности) Базового капитала Первого уровня'),
       ('CODE_8', N'Левераж'),
       ('CODE_9', N'Норматив ликвидности'),
       ('CODE_10', N'Норматив краткосрочной ликвидности'),
       ('CODE_11', N'Норматив мгновенной ликвидности'),
       ('CODE_12',
        N'Норматив максимального размера риска по срочным депозитам и прочим обязательствам перед физическими лицами'),
       ('CODE_13', N'Норматив максимального размера риска по депозитам до востребования физических лиц'),
       ('CODE_14', N'Максимальный размер риска по бланковым кредитам'),
       ('CODE_15',
        N'Максимальный размер любых инвестиций, включая любые финансовые вложения и кредиты в каждую небанковскую организацию'),
       ('CODE_16', N'Максимальный размер общих инвестиций в каждую небанковскую организацию'),
       ('CODE_17', N'Максимальный размер риска по операциям с аффилированными и связанными с банком лицами'),
       ('CODE_18', N'Максимальный размер инвестиций в недвижимое имущество (основные средства)'),
       ('CODE_19',
        N'Общий размер инвестиций банка в ценные бумаги Правительств и Центральных банков государств-членов ОЭСР'),
       ('CODE_20', N'Общий размер инвестиций банка в негосударственные долговые ценные бумаги'),
       ('CODE_21',
        N'Максимальный объем депозитов, привлеченных от физических и юридических лиц, если объем кредитов, выданных членам солидарной ответственности, в кредитном портфеле банка'),
       ('CODE_22',
        N'Максимальный объем депозитов, привлеченных от физических и юридических лиц, если объем кредитов, выданных членам солидарной ответственности, в кредитном портфеле банка'),
       ('CODE_23', N'Общая сумма крупных рисков'),
       ('CODE_24', N'Другое - заполняется в свободной форме');
;

DECLARE cursorCodes CURSOR FOR
    SELECT code, value
    FROM #TempKeyValuePairs;

OPEN cursorCodes;

FETCH NEXT FROM cursorCodes INTO @codeReg, @valueReg;

WHILE @@FETCH_STATUS = 0
    BEGIN
        DECLARE cursorLangIds CURSOR FOR
            SELECT DISTINCT id
            FROM sys_languages;

        OPEN cursorLangIds;

        FETCH NEXT FROM cursorLangIds INTO @langid_reg;

        WHILE @@FETCH_STATUS = 0
            BEGIN
                IF @current_key_codeReg <> @codeReg
                    BEGIN
                        SET @sysStringNextValueReg = NEXT VALUE FOR sys_string_sequence;
                        SET @current_key_codeReg = @codeReg;
                        SET @cems_sanct_reg_seq = NEXT VALUE FOR cems_sanction_reg_table_sequence;

                        INSERT INTO CEMS_SANCTION_REGULATION_TABLE(ID, NAMESTRID, CODE)
                        VALUES (@cems_sanct_reg_seq, @sysStringNextValueReg, @current_key_codeReg);
                    END

                INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
                VALUES (@sysStringNextValueReg, @langid_reg, @valueReg);

                FETCH NEXT FROM cursorLangIds INTO @langid_reg;
            END;

        CLOSE cursorLangIds;
        DEALLOCATE cursorLangIds;

        FETCH NEXT FROM cursorCodes INTO @codeReg, @valueReg;
    END;

CLOSE cursorCodes;
DEALLOCATE cursorCodes;

DROP TABLE #TempKeyValuePairs;



alter table CEMS_SANCTION_REGULATIONS
    add REGULATION_STR varchar(250)

go
update CEMS_SANCTION_REGULATIONS
set REGULATION_STR = REGULATION
update CEMS_SANCTION_REGULATIONS
set REGULATION = null
alter table CEMS_SANCTION_REGULATIONS
    alter column REGULATION numeric
alter table CEMS_SANCTION_REGULATIONS
    add constraint fk_cems_sanction_reg foreign key (REGULATION) references CEMS_SANCTION_REGULATION_TABLE (ID)

IF
        (SELECT DATA_TYPE
         FROM INFORMATION_SCHEMA.COLUMNS
         WHERE table_name = 'CEMS_SANCTION_REGULATIONS'
           and COLUMN_NAME = 'REGULATION_STR') = 'varchar'
    BEGIN
        UPDATE CEMS_SANCTION_REGULATIONS
        SET REGULATION = CASE
                             WHEN REGULATION_STR = 'CODE_1'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_1')
                             WHEN REGULATION_STR = 'CODE_2'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_2')
                             WHEN REGULATION_STR = 'CODE_3'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_3')
                             WHEN REGULATION_STR = 'CODE_4'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_4')
                             WHEN REGULATION_STR = 'CODE_5'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_5')
                             WHEN REGULATION_STR = 'CODE_6'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_6')
                             WHEN REGULATION_STR = 'CODE_7'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_7')
                             WHEN REGULATION_STR = 'CODE_8'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_8')
                             WHEN REGULATION_STR = 'CODE_9'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_9')
                             WHEN REGULATION_STR = 'CODE_10'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_10')
                             WHEN REGULATION_STR = 'CODE_11'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_11')
                             WHEN REGULATION_STR = 'CODE_12'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_12')
                             WHEN REGULATION_STR = 'CODE_13'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_13')
                             WHEN REGULATION_STR = 'CODE_14'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_14')
                             WHEN REGULATION_STR = 'CODE_15'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_15')
                             WHEN REGULATION_STR = 'CODE_16'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_16')
                             WHEN REGULATION_STR = 'CODE_17'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_17')
                             WHEN REGULATION_STR = 'CODE_18'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_18')
                             WHEN REGULATION_STR = 'CODE_19'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_19')
                             WHEN REGULATION_STR = 'CODE_20'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_20')
                             WHEN REGULATION_STR = 'CODE_21'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_21')
                             WHEN REGULATION_STR = 'CODE_22'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_22')
                             WHEN REGULATION_STR = 'CODE_23'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_23')
                             WHEN REGULATION_STR = 'CODE_24'
                                 THEN (select id from CEMS_SANCTION_REGULATION_TABLE where CODE = 'CODE_24')
            END
    END

alter table CEMS_SANCTION_REGULATIONS
    drop column REGULATION_STR

go


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'CEMS_SANCTION_STATUS_TABLE')
create table CEMS_SANCTION_STATUS_TABLE
(
    ID        numeric            not null primary key,
    NAMESTRID int,
    CODE      varchar(50) unique not null
);


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[cems_sanction_status_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE cems_sanction_status_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

--CEMS_SANCTION_STATUS_TABLE


declare @enLangId int;
declare @ruLangId int;
DECLARE @sys_string_next_valueStatus INT;
SET @sys_string_next_valueStatus = NEXT VALUE FOR sys_string_sequence;
set @enLangId = (select id
                 from SYS_LANGUAGES
                 where code = 'en_US');
set @ruLangId = (select id
                 from SYS_LANGUAGES
                 where code = 'ru_RU');

IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'IN_PROGRESS')
    BEGIN
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @enLangId, 'In Progress')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @ruLangId, N'В процессе')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'IN_PROGRESS')
        set @sys_string_next_valueStatus = next value for sys_string_sequence

    END

IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'HALF_REALIZED')
    BEGIN
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @enLangId, 'Half Realized')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @ruLangId, N'Полусозревший')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'HALF_REALIZED')

        set @sys_string_next_valueStatus = next value for sys_string_sequence
    END
IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'COMPLETED_OUT_OF_DATE')
    BEGIN
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        VALUES (@sys_string_next_valueStatus, @enLangId, 'Completed Out of Date')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        VALUES (@sys_string_next_valueStatus, @ruLangId, N'Завершено с истечением срока')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'COMPLETED_OUT_OF_DATE')

        set @sys_string_next_valueStatus = next value for sys_string_sequence
    END
IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'COMPLETED')
    BEGIN
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @enLangId, 'Completed')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @ruLangId, N'Завершено')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'COMPLETED')

        set @sys_string_next_valueStatus = next value for sys_string_sequence
    END
IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'NOT_COMPLETED')
    BEGIN
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @enLangId, 'Not Completed')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @ruLangId, N'Не завершено')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'NOT_COMPLETED')

        set @sys_string_next_valueStatus = next value for sys_string_sequence
    END
IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'DISMISSED_BY_CHAIRMAN')
    BEGIN
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        VALUES (@sys_string_next_valueStatus, @enLangId, 'Dismissed by Chairman')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        VALUES (@sys_string_next_valueStatus, @ruLangId, N'Отклонено председателем')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'DISMISSED_BY_CHAIRMAN')

        set @sys_string_next_valueStatus = next value for sys_string_sequence
    END

IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'DISMISSED_BY_BOARD')
    BEGIN
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        VALUES (@sys_string_next_valueStatus, @enLangId, 'Dismissed by Board')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        VALUES (@sys_string_next_valueStatus, @ruLangId, N'Отклонено правлением')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'DISMISSED_BY_BOARD')

        set @sys_string_next_valueStatus = next value for sys_string_sequence
    END

IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'DISMISSED_BY_COMMITTEE')
    BEGIN
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        VALUES (@sys_string_next_valueStatus, @enLangId, 'Dismissed by Committee')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE)
        VALUES (@sys_string_next_valueStatus, @ruLangId, N'Отклонено комитетом')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'DISMISSED_BY_COMMITTEE')

        set @sys_string_next_valueStatus = next value for sys_string_sequence
    END

IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'COURT_CASE')
    BEGIN
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @enLangId, 'Court Case')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @ruLangId, N'Судебное дело')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'COURT_CASE')

        set @sys_string_next_valueStatus = next value for sys_string_sequence

    END

IF NOT EXISTS(select 1
              from CEMS_SANCTION_STATUS_TABLE
              where CODE = 'POSTPONED')
    BEGIN

        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @enLangId, 'Postponed')
        INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sys_string_next_valueStatus, @ruLangId, N'Отложено')
        INSERT INTO CEMS_SANCTION_STATUS_TABLE (ID, NAMESTRID, CODE)
        values (next value for cems_sanction_status_sequence, @sys_string_next_valueStatus, 'POSTPONED')
    END

go

IF
        (SELECT DATA_TYPE
         FROM INFORMATION_SCHEMA.COLUMNS
         WHERE table_name = 'CEMS_RECOMMENDATIONS'
           and COLUMN_NAME = 'STATUS') = 'numeric'
    BEGIN
        UPDATE CEMS_RECOMMENDATIONS
        SET status = CASE
                         WHEN STATUS = 0 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'IN_PROGRESS')
                         WHEN STATUS = 1 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'HALF_REALIZED')
                         WHEN STATUS = 2 THEN (select id
                                               from CEMS_SANCTION_STATUS_TABLE
                                               where CODE = 'COMPLETED_OUT_OF_DATE')
                         WHEN STATUS = 3 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'COMPLETED')
                         WHEN STATUS = 4 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'NOT_COMPLETED')
                         WHEN STATUS = 5 THEN (select id
                                               from CEMS_SANCTION_STATUS_TABLE
                                               where CODE = 'DISMISSED_BY_CHAIRMAN')
                         WHEN STATUS = 6 THEN (select id
                                               from CEMS_SANCTION_STATUS_TABLE
                                               where CODE = 'DISMISSED_BY_BOARD')
                         WHEN STATUS = 7 THEN (select id
                                               from CEMS_SANCTION_STATUS_TABLE
                                               where CODE = 'DISMISSED_BY_COMMITTEE')
                         WHEN STATUS = 8 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'COURT_CASE')
                         WHEN STATUS = 9 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'POSTPONED')
            END
        WHERE STATUS is not null
    END

IF
        (SELECT DATA_TYPE
         FROM INFORMATION_SCHEMA.COLUMNS
         WHERE table_name = 'CEMS_SANCTIONS'
           and COLUMN_NAME = 'STATUS') = 'int'
    BEGIN
        UPDATE CEMS_SANCTIONS
        SET status = CASE
                         WHEN STATUS = 0 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'IN_PROGRESS')
                         WHEN STATUS = 1 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'HALF_REALIZED')
                         WHEN STATUS = 2 THEN (select id
                                               from CEMS_SANCTION_STATUS_TABLE
                                               where CODE = 'COMPLETED_OUT_OF_DATE')
                         WHEN STATUS = 3 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'COMPLETED')
                         WHEN STATUS = 4 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'NOT_COMPLETED')
                         WHEN STATUS = 5 THEN (select id
                                               from CEMS_SANCTION_STATUS_TABLE
                                               where CODE = 'DISMISSED_BY_CHAIRMAN')
                         WHEN STATUS = 6 THEN (select id
                                               from CEMS_SANCTION_STATUS_TABLE
                                               where CODE = 'DISMISSED_BY_BOARD')
                         WHEN STATUS = 7 THEN (select id
                                               from CEMS_SANCTION_STATUS_TABLE
                                               where CODE = 'DISMISSED_BY_COMMITTEE')
                         WHEN STATUS = 8 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'COURT_CASE')
                         WHEN STATUS = 9 THEN (select id from CEMS_SANCTION_STATUS_TABLE where CODE = 'POSTPONED')
            END
        WHERE STATUS is not null
    end


alter table CEMS_SANCTIONS
    alter column status numeric

IF NOT EXISTS (SELECT 1
               FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
               WHERE TABLE_NAME = 'CEMS_SANCTIONS'
                 AND CONSTRAINT_NAME = 'fk_cems_sanction_status')
    BEGIN
        ALTER TABLE CEMS_SANCTIONS
            ADD CONSTRAINT fk_cems_sanction_status FOREIGN KEY (STATUS) REFERENCES CEMS_SANCTION_STATUS_TABLE (ID);
    END
IF NOT EXISTS (SELECT 1
               FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
               WHERE TABLE_NAME = 'CEMS_RECOMMENDATIONS'
                 AND CONSTRAINT_NAME = 'fk_cems_recommendation_status')
    begin
        alter table CEMS_RECOMMENDATIONS
            add constraint fk_cems_recommendation_status foreign key (STATUS) references CEMS_SANCTION_STATUS_TABLE (ID)
    end


IF
    EXISTS(select *
           from sys.tables
           where name = 'CEMS_SANCTION_MEASURE_OF_INFLUENCE')
    BEGIN
        DROP TABLE CEMS_SANCTION_MEASURE_OF_INFLUENCE
    END


IF
    EXISTS(select *
           from sys.tables
           where name = 'CEMS_SANCTION_MEASURE_REASONS')
    BEGIN
        DROP TABLE CEMS_SANCTION_MEASURE_REASONS
    END


IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.dashboard.manager')
    BEGIN
        DECLARE
            @ss_id INT
        DECLARE
            @sp_id INT
        DECLARE
            @lang_id INT
        SET @ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @lang_id = (SELECT id
                        FROM sys_languages
                        WHERE code LIKE '%en%')
        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@ss_id, @lang_id, 'Dashboard  Manager');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@sp_id, @ss_id, 'net.fina.dashboard.manager');
    END;

IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'net.fina.blocked.email.domains')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('net.fina.blocked.email.domains', '')
    END;

IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'fina2.mail.smtp.auth')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('fina2.mail.smtp.auth', 'true')
    END;

IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'fina2.mail.read.disabled')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('fina2.mail.read.disabled', '')
    END;
IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'fina2.mail.send.disabled')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('fina2.mail.send.disabled', '')
    END;


IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'net.fina.system.module.communicator.version')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('net.fina.system.module.communicator.version', '0')
    END;

IF (SELECT DATA_TYPE
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'IN_COMMUNICATOR_MESSAGES'
      AND COLUMN_NAME = 'STATUS'
      AND EXISTS (SELECT 1
                  FROM SYS_PROPERTIES
                  WHERE PROP_KEY = 'net.fina.system.module.communicator.version'
                    AND value = '0')) = 'int'
    BEGIN

        UPDATE IN_COMMUNICATOR_MESSAGES
        SET STATUS = CASE
                         WHEN STATUS = 0 THEN 0
                         WHEN STATUS = 1 THEN 0
                         WHEN STATUS = 2 THEN 1
                         WHEN STATUS = 3 THEN 2
                         WHEN STATUS = 4 THEN 3
                         WHEN STATUS = 5 THEN 4
                         WHEN STATUS = 6 THEN 5
                         WHEN STATUS = 7 THEN 6
            END;

        UPDATE SYS_PROPERTIES
        SET VALUE = '1'
        WHERE PROP_KEY = 'net.fina.system.module.communicator.version';
    END


update IN_COMMUNICATOR_MESSAGES
set HAS_ATTACHMENTS=1
where id in (select m.REPLYTOID
             from IN_COMMUNICATOR_MESSAGES m
             where m.id in
                   (select a.MESSAGE_ID from IN_COMMUNICATOR_ATTACHEMENTS a where a.TYPE = 1 and m.REPLYTOID <> 0))


IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.communicator.messages.bookmarks.amend')
    BEGIN
        DECLARE
            @ss_id INT
        DECLARE
            @sp_id INT
        DECLARE
            @lang_id INT

        SET @ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @lang_id = (SELECT id
                        FROM sys_languages
                        WHERE code LIKE '%en%')

        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@ss_id, @lang_id, 'Communicator Messages Bookmark amend permission');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@sp_id, @ss_id, 'net.fina.communicator.messages.bookmarks.amend');
    END;


IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Name = 'PARENTID'
             AND Object_ID = Object_ID('IN_FAQ_CATEGORIES'))
    begin
        update IN_FAQ_CATEGORIES set PARENTID = 0 WHERE PARENTID < 0
    end;

IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Object_ID = Object_ID('IN_LEGAL_PERSONS'))
    begin
        update IN_LEGAL_PERSONS set RESIDENT_STATUS = 4 where RESIDENT_STATUS is null
    end;


IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Object_ID = Object_ID('IN_PERSONS'))
    begin
        update IN_PERSONS set RESIDENT_STATUS = 0 where RESIDENT_STATUS is null
    end;

IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Object_ID = Object_ID('IN_MDT_NODES'))
    begin
        update IN_MDT_NODES set DATATYPE = 0 where DATATYPE is null
    end;


IF NOT  EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'NOTE'
                 AND Object_ID = Object_ID('EMS_INSPECTION_FOLLOWUP_RECOMMENDATIONS'))
    begin
        alter table EMS_INSPECTION_FOLLOWUP_RECOMMENDATIONS add NOTE NVARCHAR(500)
    end


IF NOT  EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'READ_DATE'
                 AND Object_ID = Object_ID('IN_MESSAGE_USERS'))
    begin
        alter table IN_MESSAGE_USERS add READ_DATE datetime
    end

IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Object_ID = Object_ID('IN_LEGAL_PERSONS'))
    begin
        UPDATE IN_LEGAL_PERSONS
        SET RESIDENT_STATUS = CASE
                                  WHEN RESIDENT_STATUS = 0 then 4
                                  WHEN RESIDENT_STATUS = 1 then 5
            end
        where RESIDENT_STATUS in (0, 1)
    end;
go
IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Object_ID = Object_ID('IN_PERSONS'))
    begin
        UPDATE IN_PERSONS
        SET RESIDENT_STATUS = CASE
                                  WHEN RESIDENT_STATUS = 5 then 1
                                  WHEN RESIDENT_STATUS = 4 then 0
            end
        where RESIDENT_STATUS in (4, 5)
    end;
go
IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Object_ID = Object_ID('IN_LEGAL_PERSONS'))
    begin
        UPDATE IN_LEGAL_PERSONS
        SET RESIDENT_STATUS = CASE
                                  WHEN RESIDENT_STATUS = 4 then 0
                                  WHEN RESIDENT_STATUS = 5 then 1
            end
        where RESIDENT_STATUS in (4,5)
    end
go

