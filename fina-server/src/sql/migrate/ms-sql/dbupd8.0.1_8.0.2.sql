/*
  Update DB Version
*/

UPDATE sys_properties
SET value = '8.0.2'
WHERE prop_key = 'fina2.database.schemaVersion';


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'DELAY_TO_DATE'
                 AND Object_ID = Object_ID('IN_SCHEDULES'))
    BEGIN
        alter table IN_SCHEDULES
            add DELAY_TO_DATE datetime not null default '';
    END;

IF
    EXISTS(SELECT 1
           FROM sys.columns
           WHERE Name = 'DELAY_TO_DATE'
             AND Object_ID = Object_ID('IN_SCHEDULES'))
    BEGIN
        exec (' update IN_SCHEDULES set DELAY_TO_DATE  = DATEADD(d, DELAY, DATEADD(hh, DELAY_HOUR, DATEADD(mi, DELAY_MINUTE, p.TODATE)) )
        from IN_SCHEDULES left join IN_PERIODS p on IN_SCHEDULES.PERIODID = p.ID')
    END;

IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_UPLOAD_FILE_GROUP')
    BEGIN
        create table SYS_UPLOAD_FILE_GROUP
        (
            ID        numeric      not null
                constraint SYS_UPLOAD_FILE_GROUP_pk
                    primary key,
            CODE      varchar(200) not null,
            NAMESTRID numeric
        );
        create unique index SYS_UPLOAD_FILE_GROUP_CODE__uindex
            on SYS_UPLOAD_FILE_GROUP (CODE);
    END;

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[upload_file_group_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE upload_file_group_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_USER_UPLOADFILE_GROUP')
create table SYS_USER_UPLOADFILE_GROUP
(
    USERID              numeric not null,
    UPLOADFILE_GROUP_ID numeric not null
);


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_ROLE_UPLOADFILE_GROUP')
create table SYS_ROLE_UPLOADFILE_GROUP
(
    ROLEID              numeric not null,
    UPLOADFILE_GROUP_ID numeric not null
);

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'FILE_GROUP_ID'
                 AND Object_ID = Object_ID('SYS_UPLOADEDFILE'))
    BEGIN
        alter table SYS_UPLOADEDFILE
            add FILE_GROUP_ID numeric;

        alter table SYS_UPLOADEDFILE
            add constraint FK_SYS_UPLOADEDFILE_FILE_GROUP
                foreign key (FILE_GROUP_ID) references SYS_UPLOAD_FILE_GROUP
    END;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'STATUS'
                 AND Object_ID = Object_ID('IN_RETURNS'))
    BEGIN
        alter table IN_RETURNS
            add STATUS numeric
    END;

exec ('update IN_RETURNS  set IN_RETURNS.status=(select rss.status from IN_RETURN_STATUSES rss where rss.RETURNID=IN_RETURNS.id and rss.ID=(select max(rss1.ID) from IN_RETURN_STATUSES rss1 where rss1.RETURNID=rss.RETURNID))');

--Set the options to support indexed views.
SET NUMERIC_ROUNDABORT OFF;
SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT,
    QUOTED_IDENTIFIER, ANSI_NULLS ON;

--Create view with SCHEMABINDING.
IF OBJECT_ID('dbo.IN_RETURNS_MV', 'view') IS NOT NULL
    DROP VIEW dbo.IN_RETURNS_MV ;
GO
CREATE VIEW dbo.IN_RETURNS_MV
    WITH SCHEMABINDING
AS
select ir.ID         as RETURN_ID,
       ir.SCHEDULEID as SCHEDULEID,
       ir.VERSIONID,
       ir.VERSION    as LATEST_VERSION,
       rd.ID         as DEFINITIONID,
       rd.MANUALINPUT,
       rt.ID         as RETURNTYPEID,
       rt.CODE       as RETURNTYPECODE,
       rt.EXCELTEMPLATE,
       p.id          as PERIODID,
       p.FROMDATE,
       p.TODATE,
       pt.ID         as PERIODTYPEID,
       pt.CODE       as PERIODTYPECODE,
       fi.id         as BANKID,
       fi.code       as BANK_CODE,
       fi.NAMESTRID  as NAMESTRID,
       rv.CODE       as VERSIONCODE,
       ir.STATUS     as STATUS
from dbo.IN_RETURNS ir
         join
     dbo.IN_SCHEDULES s on ir.SCHEDULEID = s.id
         join
     dbo.IN_RETURN_DEFINITIONS rd on s.DEFINITIONID = rd.ID
         join
     dbo.IN_RETURN_TYPES rt on rd.TYPEID = rt.ID
         join
     dbo.IN_PERIODS p on s.PERIODID = p.ID
         join
     dbo.IN_PERIOD_TYPES pt on p.PERIODTYPEID = pt.ID
         join
     dbo.IN_BANKS fi on s.BANKID = fi.ID
         join
     dbo.IN_RETURN_VERSIONS rv on ir.VERSIONID = rv.ID
where ir.SCHEDULEID = s.ID
  and s.DEFINITIONID = rd.ID
  and rd.TYPEID = rt.ID
  and s.PERIODID = p.ID
  and pt.ID = p.PERIODTYPEID
  and s.BANKID = fi.ID
GO

--Create an index on the view.
CREATE UNIQUE CLUSTERED INDEX IN_RETURNS_MV_CLUSTER_INDEX
    ON dbo.IN_RETURNS_MV (VERSIONID, DEFINITIONID, BANKID, PERIODID, FROMDATE, TODATE, PERIODTYPEID, RETURNTYPEID,
                          STATUS);
GO


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_MATRIX')
    begin

        CREATE TABLE SYS_MATRIX
        (
            ID             numeric not null
                CONSTRAINT MATRIX_PK primary key,
            FI_TYPE_ID     numeric
                CONSTRAINT FK_MATRIX_FI_TYPE_ID REFERENCES IN_BANK_TYPES (ID),
            PATTERN        NVARCHAR(255) unique,
            PERIOD_TYPE_ID numeric
                CONSTRAINT FK_MATRIX_PERIOD_TYPE_ID REFERENCES IN_PERIOD_TYPES (ID),
            VERSION_ID     decimal(10)
                CONSTRAINT FK_MATRIX_VERSION_ID REFERENCES IN_RETURN_VERSIONS (ID),
            PASSWORD       VARCHAR(255),
            SIGNATURE      BIT,
            ENGINE         INT,
            REG_FILE_TYPE  INT,
            FILE_GROUP_ID  numeric
                CONSTRAINT FK_MATRIX_FILE_GROUP_ID REFERENCES SYS_UPLOAD_FILE_GROUP (ID),
            ENABLED        BIT,

        );

    end

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[matrix_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE matrix_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


-- retrun scheduler permissions
IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.return.scheduler.review')
    BEGIN
        DECLARE
            @rsr_ss_id INT
        DECLARE
            @rsr_sp_id INT
        DECLARE
            @rsr_lang_id INT

        SET @rsr_ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @rsr_sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @rsr_lang_id = (SELECT id
                            FROM sys_languages
                            WHERE code LIKE '%en%')

        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@rsr_ss_id, @rsr_lang_id, 'Return scheduler review');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@rsr_sp_id, @rsr_ss_id, 'net.fina.return.scheduler.review');
    END

IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.return.scheduler.amend')
    BEGIN
        DECLARE
            @rsa_ss_id INT
        DECLARE
            @rsa_sp_id INT

        DECLARE
            @rsa_lang_id INT

        SET @rsa_ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @rsa_sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @rsa_lang_id = (SELECT id
                            FROM sys_languages
                            WHERE code LIKE '%en%')

        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@rsa_ss_id, @rsa_lang_id, 'Return scheduler amend');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@rsa_sp_id, @rsa_ss_id, 'net.fina.return.scheduler.amend');
    END

IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.return.scheduler.delete')
    BEGIN
        DECLARE
            @rsd_ss_id INT
        DECLARE
            @rsd_sp_id INT

        DECLARE
            @rsd_lang_id INT

        SET @rsd_ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @rsd_sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @rsd_lang_id = (SELECT id
                            FROM sys_languages
                            WHERE code LIKE '%en%')

        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@rsd_ss_id, @rsd_lang_id, 'Return scheduler delete');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@rsd_sp_id, @rsd_ss_id, 'net.fina.return.scheduler.delete');
    END

IF EXISTS (SELECT *
           FROM sys.objects
           WHERE name = N'PK_IN_BANK_BRANCHES_AUD_REV')
ALTER TABLE IN_BANK_BRANCHES_AUD
    DROP CONSTRAINT PK_IN_BANK_BRANCHES_AUD_REV;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_SUB_MATRIX')
    begin
        create table SYS_SUB_MATRIX
        (
            ID             numeric     not null
                constraint PK_SYS_SUB_MATRIX
                    primary key,
            RETURN_ID      decimal(10) not null
                constraint FK_SYS_SUB_MATRIX_RETURN
                    references IN_RETURN_DEFINITIONS (ID),
            IS_PROTECTED   bit,
            TABLE_TYPE     varchar(20) not null,
            SHEET_NAME     nvarchar(200),
            MAIN_MATRIX_ID NUMERIC     not null,
            CONSTRAINT UK_SHEET_NAME UNIQUE (SHEET_NAME, MAIN_MATRIX_ID)
        )
    END;

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[sub_matrix_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE sub_matrix_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_SUB_MATRIX_TABLE')
    begin
        create table SYS_SUB_MATRIX_TABLE
        (
            ID                          numeric
                constraint PK_SYS_SUB_MATRIX_TABLE
                    primary key,
            SUB_MATRIX_ID               numeric
                constraint FK_SYS_SUB_MATRIX
                    references SYS_SUB_MATRIX,
            MIXED_TABLE_TYPE            varchar(20),
            START_COLUMN                varchar(20),
            START_ROW                   int,
            OFFSET                      int,
            VCT_TABLE_HEADER            nvarchar(200),
            VCT_TABLE_AFTER_HEADER_ROWS int
        )
    END;

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[sub_matrix_table_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE sub_matrix_table_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_MATRIX_TABLE_CONDITION')
    begin
        create table SYS_MATRIX_TABLE_CONDITION
        (
            ID          NUMERIC
                constraint PK_SYS_MATRIX_TABLE_CONDITION
                    primary key,
            COLUMN_NAME varchar(20) not null,
            CONDITION   nvarchar(200),
            TABLE_ID    numeric
                constraint FK_SYS_MATRIX_TABLE
                    references SYS_SUB_MATRIX_TABLE
        )
    END;

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[matrix_table_condition_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE matrix_table_condition_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'SYS_SUB_MATRIX_TABLE_MAPPING')
    begin
        create table SYS_SUB_MATRIX_TABLE_MAPPING
        (
            ID                  numeric
                constraint PK_SYS_SUB_MATRIX_TABLE_MAPPING
                    primary key,
            NODE_ID             numeric(10)
                constraint FK_SYS_SUB_MATRIX_TABLE_MAPPING_NODE
                    references IN_MDT_NODES (ID),
            CELL                varchar(20) not null,
            DATA_TYPE           varchar(20),
            SUB_MATRIX_TABLE_ID numeric
                constraint FK_SYS_SUB_MATRIX_TABLE
                    references SYS_SUB_MATRIX_TABLE
        );

        create unique index SYS_SUB_MATRIX_TABLE_MAPPING_SUB_MATIX_TABLE_ID_NODE_ID_CELL_uindex
            on SYS_SUB_MATRIX_TABLE_MAPPING (SUB_MATRIX_TABLE_ID, NODE_ID, CELL);
    END;


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[sub_matrix_table_mapping_sequence]')
                 AND type = 'SO')
CREATE SEQUENCE sub_matrix_table_mapping_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.menu.matrix')
    BEGIN
        DECLARE
            @rsd_ss_id INT
        DECLARE
            @rsd_sp_id INT

        DECLARE
            @rsd_lang_id INT

        SET @rsd_ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @rsd_sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @rsd_lang_id = (SELECT id
                            FROM sys_languages
                            WHERE code LIKE '%en%')

        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@rsd_ss_id, @rsd_lang_id, 'Menu Matrix');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@rsd_sp_id, @rsd_ss_id, 'net.fina.menu.matrix');
    END

IF EXISTS (
    SELECT 1  from INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    where TABLE_NAME = 'SYS_SUB_MATRIX_TABLE'
    and CONSTRAINT_NAME = 'FK_SYS_SUB_MATRIX_DT'
)
    begin
        alter table SYS_SUB_MATRIX_TABLE drop constraint FK_SYS_SUB_MATRIX_DT
    end


IF EXISTS(SELECT 1
          FROM sys.columns
          WHERE Name = 'MIXED_TABLE_TYPE'
            AND Object_ID = Object_ID('SYS_SUB_MATRIX_TABLE'))
alter table SYS_SUB_MATRIX_TABLE
    drop column MIXED_TABLE_TYPE;

IF NOT EXISTS(SELECT 1
              FROM sys.columns
              WHERE Name = 'DEFINITION_TABLE_ID'
                AND Object_ID = Object_ID('SYS_SUB_MATRIX_TABLE'))
    BEGIN
        alter table SYS_SUB_MATRIX_TABLE
            add DEFINITION_TABLE_ID decimal(10);
        alter table SYS_SUB_MATRIX_TABLE
            add DEFINITIONID decimal(10);
        alter table SYS_SUB_MATRIX_TABLE
            add constraint FK_SYS_SUB_MATRIX_DT
                foreign key (DEFINITION_TABLE_ID, DEFINITIONID) references IN_DEFINITION_TABLES (ID, DEFINITIONID);
        alter table SYS_SUB_MATRIX_TABLE
            add constraint FK_SYS_SUB_MATRIX_DEFINITION
                foreign key (DEFINITIONID) references IN_RETURN_DEFINITIONS (ID);
    END;


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'SEQUENCE'
                 AND Object_ID = Object_ID('SYS_SUB_MATRIX_TABLE_MAPPING'))
    begin

        alter table SYS_SUB_MATRIX_TABLE_MAPPING
            add SEQUENCE numeric default 0;

    end


IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.menu.first.dashboard')
    BEGIN
        DECLARE
            @fd_ss_id INT
        DECLARE
            @fd_sp_id INT

        DECLARE
            @fd_lang_id INT

        SET @fd_ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @fd_sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @fd_lang_id = (SELECT id
                           FROM sys_languages
                           WHERE code LIKE '%en%')

        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@fd_ss_id, @fd_lang_id, 'FIRST Dashboard');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@fd_sp_id, @fd_ss_id, 'net.fina.menu.first.dashboard');
    END


IF NOT EXISTS (SELECT *
               FROM SYS_PERMISSIONS
               WHERE idName = 'net.fina.menu.document.management')
    BEGIN
        DECLARE
            @dm_ss_id INT
        DECLARE
            @dm_sp_id INT

        DECLARE
            @dm_lang_id INT

        SET @dm_ss_id = (NEXT VALUE FOR sys_string_sequence)
        SET @dm_sp_id = (NEXT VALUE FOR sys_permissions_sequence)
        SET @dm_lang_id = (SELECT id
                           FROM sys_languages
                           WHERE code LIKE '%en%')

        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@dm_ss_id, @dm_lang_id, 'Document Management');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@dm_sp_id, @dm_ss_id, 'net.fina.menu.document.management');
    END

IF NOT EXISTS (SELECT *
               FROM sys.objects
               WHERE name = N'SYS_SUB_MATRIX_UK')
alter table SYS_SUB_MATRIX
    add constraint SYS_SUB_MATRIX_UK
        unique (RETURN_ID, MAIN_MATRIX_ID)


IF
    EXISTS(select *
           from sys.tables
           where name = 'IN_BANKS_AUD')
    begin
        update IN_BANKS_AUD
        set TYPEID = (select IN_BANKS.TYPEID from IN_BANKS where IN_BANKS.CODE = IN_BANKS_AUD.CODE)
        where TYPEID is null
    end


IF
    EXISTS(select *
           from sys.tables
           where name = 'IN_LICENSE_BANKING_OPERATIONS_AUD')
    begin
        update IN_LICENSE_BANKING_OPERATIONS_AUD
        set OPERATION_ID = (select IN_LICENSE_BANKING_OPERATIONS.OPERATION_ID
                            from IN_LICENSE_BANKING_OPERATIONS
                            where IN_LICENSE_BANKING_OPERATIONS_AUD.id = IN_LICENSE_BANKING_OPERATIONS.ID)
        where IN_LICENSE_BANKING_OPERATIONS_AUD.OPERATION_ID is null
    end


--     migrate SYS_USER_DASHBOARDS to user_id pk
IF EXISTS (select 1
           from sys.tables
           where object_id = object_id('SYS_USER_DASHBOARDS'))
    declare @constraintName nvarchar(100);
begin
    select @constraintName = CONSTRAINT_NAME
    from INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    where TABLE_NAME = 'SYS_USER_DASHBOARDS'
      and CONSTRAINT_TYPE = 'PRIMARY KEY';

    if @constraintName is not null
        begin
            declare @sqlDropConstraint nvarchar(max);
            set @sqlDropConstraint =
                    'alter table SYS_USER_DASHBOARDS drop constraint ' + quotename(@constraintName);
            exec sp_executesql @sqlDropConstraint;
        end
    if not exists (select 1
                   from sys.columns
                   where object_id = object_id('SYS_USER_DASHBOARDS')
                     and name = 'USER_ID')
        begin

            alter table SYS_USER_DASHBOARDS
                add USER_ID numeric not null default -1;
        end

end
go

IF EXISTS (select 1
           from sys.columns
           where object_id = OBJECT_ID('SYS_USER_DASHBOARDS')
             and name in ('USER_ID', 'USER_LOGIN')
           group by object_id
           having count(*) = 2)
    begin
        declare @updateQuery nvarchar(max);
        SET @updateQuery = '
        UPDATE user_dashboard
        SET user_dashboard.USER_ID = u.id
        FROM SYS_USER_DASHBOARDS user_dashboard
        INNER JOIN SYS_USERS u ON user_dashboard.USER_LOGIN = u.LOGIN';

        exec sp_executesql @updateQuery;
    end


IF EXISTS (select *
           from sys.columns
           where object_id = object_id('SYS_USER_DASHBOARDS')
             and name = 'USER_LOGIN')
    begin
        alter table SYS_USER_DASHBOARDS
            drop column USER_LOGIN;
    end

IF NOT EXISTS (SELECT 1
               FROM information_schema.table_constraints
               WHERE constraint_type = 'PRIMARY KEY'
                 AND table_name = 'SYS_USER_DASHBOARDS')
    begin
        alter table SYS_USER_DASHBOARDS
            add constraint PK_SYS_USER_DASHBOARDS primary key (DASHBOARD_ID, USER_ID)
    end


drop table if exists SYS_USER_UPLOADFILE_GROUP;

IF EXISTS(SELECT 1
          FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS
          WHERE CONSTRAINT_NAME = 'FK_MATRIX_FILE_GROUP_ID')
    begin
        alter table SYS_MATRIX
            drop constraint FK_MATRIX_FILE_GROUP_ID;
        alter table SYS_MATRIX
            drop column FILE_GROUP_ID;

        alter table SYS_UPLOADEDFILE
            add MATRIX_ID numeric;
        alter table SYS_UPLOADEDFILE
            drop constraint FK_SYS_UPLOADEDFILE_FILE_GROUP;

        alter table SYS_UPLOADEDFILE
            drop column FILE_GROUP_ID;

        alter table SYS_UPLOADEDFILE
            add constraint SYS_UPLOADEDFILE_MATRIX_FK
                foreign key (MATRIX_ID) references SYS_MATRIX;


        create table SYS_USER_MATRIX_FILE_MAPPING
        (
            USER_ID   numeric(18),
            MATRIX_ID numeric
        );
        create table SYS_ROLE_MATRIX_FILE_MAPPING
        (
            ROLE_ID   decimal(10),
            MATRIX_ID numeric
        )
    end


IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'net.fina.user.requiredFields')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('net.fina.user.requiredFields', 'login,email,phone')
    END;


IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'SYS_AUDIT_LOG_ENTITY_NAME_index'
      AND object_id = OBJECT_ID('SYS_AUDIT_LOG')
)
    BEGIN
        create index SYS_AUDIT_LOG_ENTITY_NAME_index
            on SYS_AUDIT_LOG (ENTITY_NAME)
    END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'SYS_AUDIT_LOG_RELEVANCE_TIME_index'
      AND object_id = OBJECT_ID('SYS_AUDIT_LOG')
)
    BEGIN
        create index SYS_AUDIT_LOG_RELEVANCE_TIME_index
            on SYS_AUDIT_LOG (RELEVANCE_TIME)
    END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'SYS_AUDIT_LOG_ACTOR_ID_index'
      AND object_id = OBJECT_ID('SYS_AUDIT_LOG')
)
    BEGIN
        create index SYS_AUDIT_LOG_ACTOR_ID_index
            on SYS_AUDIT_LOG (ACTOR_ID)
    END;


IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'SYS_AUDIT_LOG_OPERATION_TYPE_index'
      AND object_id = OBJECT_ID('SYS_AUDIT_LOG')
)
    BEGIN
        create index SYS_AUDIT_LOG_OPERATION_TYPE_index
            on SYS_AUDIT_LOG (OPERATION_TYPE)
    END;


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'LAST_CONVERSATION_MESSAGE_DATE'
                 AND Object_ID = Object_ID('IN_MESSAGE_USERS'))
    BEGIN
        alter table IN_MESSAGE_USERS
            add LAST_CONVERSATION_MESSAGE_DATE datetime;
    END;


update IN_MESSAGE_USERS set IN_MESSAGE_USERS.LAST_CONVERSATION_MESSAGE_DATE=(select max(IN_COMMUNICATOR_MESSAGES.SENDDATE)
from IN_COMMUNICATOR_MESSAGES where IN_COMMUNICATOR_MESSAGES.REPLYTOID=IN_MESSAGE_USERS.MESSAGE_ID and IN_COMMUNICATOR_MESSAGES.REPLYTOUSERID=IN_MESSAGE_USERS.USER_ID)

IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'net.fina.return.status.notification.providers')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('net.fina.return.status.notification.providers', 'notification,email')
    END;

IF
    EXISTS(select 1
           from sys.tables
           where name = 'OUT_MAIL_MESSAGE_REPLY')
    begin
        alter table OUT_MAIL_MESSAGE_REPLY
            alter column MAIL_SUBJECT nvarchar(255) null

    end

IF  EXISTS (
    SELECT 1
    FROM sys.foreign_keys AS fk
             JOIN sys.tables AS t ON fk.parent_object_id = t.object_id
    WHERE t.name = 'IN_BANK_BRANCHES_AUD'
      AND fk.name = 'FK_BRANCH_AUD_PERSON_MANAGER_ID')
    begin
        alter table IN_BANK_BRANCHES_AUD drop constraint FK_BRANCH_AUD_PERSON_MANAGER_ID
    end



IF  EXISTS (
    SELECT 1
    FROM sys.foreign_keys AS fk
             JOIN sys.tables AS t ON fk.parent_object_id = t.object_id
    WHERE t.name = 'IN_BANK_BRANCHES_AUD'
      AND fk.name = 'FK_BRANCH_AUD_PERSON_ACCOUNTANT_ID')
    begin
        alter table IN_BANK_BRANCHES_AUD drop constraint FK_BRANCH_AUD_PERSON_ACCOUNTANT_ID
    end

IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'fina2.auditlog.service.interval')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('fina2.auditlog.service.interval', '20')
    END;


IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'net.fina.processing.cross.file.validation.enable')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('net.fina.processing.cross.file.validation.enable', 'false')
    END;


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CURRENCY'
                 AND Object_ID = Object_ID('IN_CRIMINAL_RECORDS'))
    begin
        alter table IN_CRIMINAL_RECORDS
            add CURRENCY varchar(10);
    end;

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CURRENCY'
                 AND Object_ID = Object_ID('IN_CRIMINAL_RECORDS_AUD'))
    begin
        alter table IN_CRIMINAL_RECORDS_AUD
            add CURRENCY varchar(10);
    end

IF NOT EXISTS (SELECT 1
               FROM SYS_PROPERTIES
               WHERE prop_key = 'net.fina.ost.taxonomy.encrypt.enable')
    BEGIN
        insert into SYS_PROPERTIES(prop_key, value) values ('net.fina.ost.taxonomy.encrypt.enable', 'true')
    END;

drop TABLE if exists IN_IMPORTED_QUEUE;

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IN_IMPORTED_XML_RETURN_returnId_idx'
      AND object_id = OBJECT_ID('IN_IMPORTED_XML_RETURN')
)
    BEGIN
        CREATE INDEX IN_IMPORTED_XML_RETURN_returnId_idx ON IN_IMPORTED_XML_RETURN (returnId)
    END;


IF
    NOT EXISTS(select *
               from sys.tables
               where name = 'IN_IMPORTED_RETURNS_GROUP')
    begin
        CREATE TABLE dbo.IN_IMPORTED_RETURNS_GROUP
        (
            id          int not null,
            returnCode  nvarchar(50),
            bankCode    nvarchar(50),
            versionCode nvarchar(50),
            periodStart datetime,
            periodEnd   datetime,
            userId      int,
            type        int,
            xlsId       numeric
        )
    END;

IF NOT EXISTS (SELECT 1
               FROM IN_IMPORTED_RETURNS_GROUP)
    begin
        insert INTO IN_IMPORTED_RETURNS_GROUP WITH (TABLOCK) (id,returnCode, bankCode, versionCode, periodStart, periodEnd,
                                                              userId, type, xlsId)
        SELECT
            ROW_NUMBER() OVER (ORDER BY i1_0.periodEnd),
            max(i1_0.returnCode),
            max(i1_0.bankCode),
            max(i1_0.versionCode),
            max(i1_0.periodStart),
            max(i1_0.periodEnd),
            max(i1_0.userId),
            max(i1_0.type),
            max(i1_0.xlsId)
        FROM dbo.IN_IMPORTED_RETURNS i1_0

        group by i1_0.BANKCODE,
                 i1_0.PERIODSTART,
                 i1_0.PERIODEND,
                 i1_0.VERSIONCODE,
                 i1_0.xlsId,
                 i1_0.userId
        order by 4 desc;
    end


IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[IN_IMPORTED_RETURNS_GROUP_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @IMP_RET_GROUP_MAXID_START as BIGINT;
        set
            @IMP_RET_GROUP_MAXID_START = COALESCE((SELECT MAX(ID) + 1 FROM IN_IMPORTED_RETURNS_GROUP), 1)
        DECLARE
            @IMP_RET_GROUP_MAXID_SQL NVARCHAR(MAX)

        SET @IMP_RET_GROUP_MAXID_SQL =
                'CREATE SEQUENCE IN_IMPORTED_RETURNS_GROUP_sequence
                 AS [BIGINT]
                 START WITH ' + cast(@IMP_RET_GROUP_MAXID_START as varchar) +
                'INCREMENT BY 1
                    CACHE'
        EXEC (@IMP_RET_GROUP_MAXID_SQL)

    END;


IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'SYS_UPLOADEDFILE_ID_FILENAME'
      AND object_id = OBJECT_ID('dbo.SYS_UPLOADEDFILE')
)
    BEGIN
        CREATE NONCLUSTERED INDEX SYS_UPLOADEDFILE_ID_FILENAME
            ON dbo.SYS_UPLOADEDFILE (id)
            INCLUDE (fileName);
    END;


IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IN_IMPORTED_RETURNS_GROUP_PERIODEND'
      AND object_id = OBJECT_ID('dbo.IN_IMPORTED_RETURNS_GROUP')
)
    BEGIN
        create NONCLUSTERED index IN_IMPORTED_RETURNS_GROUP_PERIODEND on dbo.IN_IMPORTED_RETURNS_GROUP (periodEnd) include(bankcode,versioncode,periodstart,xlsid,userid)
    END;


-- dcs sign prop
IF  EXISTS (select 1
            from SYS_PERMISSIONS
            where idName = 'net.fina.dcs.security.sign')
    begin
        delete from SYS_USER_PERMISSIONS
        where PERMISSIONID = (select id
                              from SYS_PERMISSIONS
                              where idName = 'net.fina.dcs.security.sign');

        delete from SYS_ROLE_PERMISSIONS
        where PERMISSIONID = (select id
                              from SYS_PERMISSIONS
                              where idName = 'net.fina.dcs.security.sign');

        delete from SYS_STRINGS
        where id = (select namestrid
                    from SYS_PERMISSIONS
                    where idName = 'net.fina.dcs.security.sign');

        delete from SYS_PERMISSIONS
        where idName = 'net.fina.dcs.security.sign';
    end

-- dcs encrypt prop
IF  EXISTS (select 1
            from SYS_PERMISSIONS
            where idName = 'net.fina.dcs.security.encrypt')
    begin
        delete from SYS_USER_PERMISSIONS
        where PERMISSIONID = (select id
                              from SYS_PERMISSIONS
                              where idName = 'net.fina.dcs.security.encrypt');

        delete from SYS_ROLE_PERMISSIONS
        where PERMISSIONID = (select id
                              from SYS_PERMISSIONS
                              where idName = 'net.fina.dcs.security.encrypt');

        delete from SYS_STRINGS
        where id = (select namestrid
                    from SYS_PERMISSIONS
                    where idName = 'net.fina.dcs.security.encrypt');

        delete from SYS_PERMISSIONS
        where idName = 'net.fina.dcs.security.encrypt';
    end



CREATE INDEX R_VERSION
    ON IN_RETURNS (version);
CREATE INDEX R_VERSION_ID
    ON IN_RETURNS (ID,version);


IF NOT EXISTS(SELECT 1
              FROM sys.columns
              WHERE Name = 'CODE'
                AND Object_ID = Object_ID('IN_DASHBOARDS'))
    BEGIN
        ALTER TABLE dbo.IN_DASHBOARDS
            ADD CODE nvarchar(40) UNIQUE NOT NULL DEFAULT (
                'DASH-' +
                FORMAT(NEXT VALUE FOR dbo.dashboards_sequence, '000')
                )
    END;

--
--  Upload File FILESTREAM MIGRATION 
-- ALTER DATABASE [DB_NAME]
--     ADD FILEGROUP UPLOAD_FILES_FileStreamGroup CONTAINS FILESTREAM;
--
--
--
-- ALTER DATABASE [DB_NAME]
--     ADD FILE
--         (
--             NAME = FileStreamData,
--             FILENAME = 'C:\Program Files\Microsoft SQL Server\FILE_STREAM\UPLOAD_FILES\DATA' -- The directory where FILESTREAM data will be stored
--             )
--         TO FILEGROUP UPLOAD_FILES_FileStreamGroup;
--
--
-- EXEC sp_rename 'SYS_UPLOADEDFILE.UPLOADEDFILE', 'UPLOADEDFILE_OLD', 'COLUMN';
--
--
--
-- ALTER TABLE SYS_UPLOADEDFILE
--     ADD FileGuid UNIQUEIDENTIFIER ROWGUIDCOL NOT NULL DEFAULT NEWID();
--
--
-- ALTER TABLE SYS_UPLOADEDFILE
--     ADD UPLOADEDFILE VARBINARY(MAX) FILESTREAM  NULL
--         CONSTRAINT PK_FileGUID UNIQUE (FileGUID);
--
--
--
-- UPDATE SYS_UPLOADEDFILE
-- SET UPLOADEDFILE = CAST(UPLOADEDFILE_OLD AS VARBINARY(MAX))
-- WHERE UPLOADEDFILE_OLD is not null;


alter table dbo.OUT_MAIL_MESSAGE_REPLY
    alter column MAIL_SUBJECT nvarchar(255) null;


IF NOT EXISTS(SELECT 1
              FROM sys.columns
              WHERE Name = 'CODE'
                AND Object_ID = Object_ID('IN_DASHLETS'))
    BEGIN
        ALTER TABLE dbo.IN_DASHLETS
            ADD CODE nvarchar(40) UNIQUE NOT NULL DEFAULT (
                'DASH-' +
                FORMAT(NEXT VALUE FOR dbo.dashlets_sequence, '000')
                )
    END;


IF NOT EXISTS(SELECT 1
              FROM sys.columns
              WHERE Name = 'MARK_TYPE'
                AND Object_ID = Object_ID('IN_COMMUNICATOR_MESSAGES'))
    BEGIN
        alter table dbo.IN_COMMUNICATOR_MESSAGES
            add MARK_TYPE int
    end


go

CREATE PROCEDURE migrateMessagesBookmarkToMarkType
AS
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_NAME = 'IN_COMMUNICATOR_MESSAGES'
          AND COLUMN_NAME = 'MARK_TYPE'
    )
        BEGIN
            PRINT 'Column MARK_TYPE does not exist.';
            RETURN;
        END

    UPDATE IN_COMMUNICATOR_MESSAGES
    SET MARK_TYPE = 0
    WHERE IS_BOOKMARKED = 1;

    IF NOT EXISTS (
        SELECT 1 FROM IN_COMMUNICATOR_MESSAGES
        WHERE MARK_TYPE = 0 AND IS_BOOKMARKED != 1
    )
        BEGIN
            ALTER TABLE IN_COMMUNICATOR_MESSAGES DROP COLUMN IS_BOOKMARKED;
            PRINT 'Column IS_BOOKMARKED dropped successfully.';
        END
    ELSE
        BEGIN
            PRINT 'Verification failed: Not all MARK_TYPE = 1 rows had IS_BOOKMARKED = 1.';
        END
END;

    go

    EXEC migrateMessagesBookmarkToMarkType;


    IF EXISTS (select 1
               from sys.tables
               where object_id = object_id('IN_DASHBOARDS'))
        declare @constraintName nvarchar(100);
    begin
        select @constraintName = CONSTRAINT_NAME
        from INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        where TABLE_NAME = 'IN_DASHBOARDS'
          and CONSTRAINT_TYPE = 'UNIQUE';

        if @constraintName is not null
            begin
                declare @sqlDropConstraint nvarchar(max);
                set @sqlDropConstraint =
                        'alter table IN_DASHBOARDS drop constraint ' + quotename(@constraintName);
                exec sp_executesql @sqlDropConstraint;
            end
    END