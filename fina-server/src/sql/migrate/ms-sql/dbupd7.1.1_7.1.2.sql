/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.2'
WHERE prop_key = 'fina2.database.schemaVersion';


IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'CONTENT_SIZE' AND Object_ID = Object_ID('IN_COMMUNICATOR_ATTACHEMENTS'))
BEGIN
ALTER TABLE IN_COMMUNICATOR_ATTACHEMENTS
    ADD CONTENT_SIZE int default 0;
END;

exec('update IN_COMMUNICATOR_ATTACHEMENTS set CONTENT_SIZE=(DATALENGTH(CONTENT))')


IF NOT EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG')
create table IN_MDT_CATALOG
(
    ID                   numeric,
    MDT_NODE_ID          numeric,
    ABBREVIATION         nvarchar(150),
    SOURCE               nvarchar(150),
    NORMATIVE_DOCUMENT   image,
    METHODOLOGY_DOCUMENT image,
    CREATION_TIME        datetime null,
    MODIFICATION_TIME    datetime null
);

IF
NOT EXISTS (SELECT *  FROM sys.indexes  WHERE name='IN_MDT_CATALOG_ID_index'
                                              AND object_id = OBJECT_ID('IN_MDT_CATALOG'))
create
    index IN_MDT_CATALOG_ID_index
    on IN_MDT_CATALOG (ID);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[mdt_catalog_seq]') AND type = 'SO')
CREATE SEQUENCE mdt_catalog_seq
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


-- IN_MDT_CATALOG_COLUMNS

IF
NOT EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG_COLUMNS')
create table IN_MDT_CATALOG_COLUMNS
(
    ID         numeric,
    DATA_TYPE  int,
    NAMESTRID  decimal,
    IS_KEY     bit,
    SEQUENCE   int,
    CATALOG_ID numeric
);

IF
NOT EXISTS (SELECT *  FROM sys.indexes  WHERE name='IN_MDT_CATALOG_COLUMNS_ID_index'
                                              AND object_id = OBJECT_ID('IN_MDT_CATALOG_COLUMNS'))
create
    index IN_MDT_CATALOG_COLUMNS_ID_index
    on IN_MDT_CATALOG_COLUMNS (ID);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[mdt_catalog_col_seq]') AND type = 'SO')
CREATE SEQUENCE mdt_catalog_col_seq
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


-- IN_MDT_CATALOG_ITEMS

IF
NOT EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG_ITEMS')
create table IN_MDT_CATALOG_ITEMS
(
    ID          numeric,
    ROW_NUMBER  int,
    COLUMN_ID   numeric,
    CATALOG_ID  numeric,
    MDT_NODE_ID numeric,
    VERSION_ID  numeric,
    VALUETRID   decimal,
    NVALUE      float
);


IF
NOT EXISTS (SELECT *  FROM sys.indexes  WHERE name='IN_MDT_CATALOG_ITEMS_ID_index'
                                              AND object_id = OBJECT_ID('IN_MDT_CATALOG_ITEMS'))
create
    index IN_MDT_CATALOG_ITEMS_ID_index
    on IN_MDT_CATALOG_ITEMS (ID);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[mdt_catalog_item_seq]') AND type = 'SO')
CREATE SEQUENCE mdt_catalog_item_seq
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


-- IN_MDT_CATALOG_ITEM_VERSION

IF
NOT EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG_ITEM_VERSION')
create table IN_MDT_CATALOG_ITEM_VERSION
(
    ID            numeric,
    CATALOG_ID    numeric,
    ROW_NUMBER    int,
    CREATION_TIME datetime,
    VERSION_CODE  varchar(100),
    USER_ID       numeric
);

IF
NOT EXISTS (SELECT *  FROM sys.indexes  WHERE name='IN_MDT_CATALOG_ITEM_VERSION_ID_index'
                                              AND object_id = OBJECT_ID('IN_MDT_CATALOG_ITEM_VERSION'))
create
    index IN_MDT_CATALOG_ITEM_VERSION_ID_index
    on IN_MDT_CATALOG_ITEM_VERSION (ID);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[mdt_catalog_item_ver_seq]') AND type = 'SO')
CREATE SEQUENCE mdt_catalog_item_ver_seq
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

-- UPDATE MDT NODES

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'CATALOG' AND Object_ID = Object_ID('IN_MDT_NODES'))
BEGIN
alter table IN_MDT_NODES
    add CATALOG bit;
END;

exec('update IN_MDT_NODES set CATALOG=0 where CATALOG is null')



-- permissions
DECLARE
@ss_id INT
DECLARE
@sp_id INT
DECLARE
@lang_id INT

SET @ss_id = (SELECT max(id)
              FROM SYS_STRINGS) + 1
SET @sp_id = (SELECT max(id)
              FROM SYS_PERMISSIONS) + 1
SET @lang_id = (SELECT id
                FROM sys_languages
                WHERE code LIKE '%en%')


IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.catalog.item.review')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value)
VALUES (@ss_id + 1, @lang_id, 'net.fina.catalog.item.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.catalog.item.review');
END;

IF
NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.catalog.item.amend')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value)
VALUES (@ss_id + 2, @lang_id, 'net.fina.catalog.item.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 2, @ss_id + 2, 'net.fina.catalog.item.amend');
END;

IF
NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.catalog.item.delete')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value)
VALUES (@ss_id + 3, @lang_id, 'net.fina.catalog.item.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 3, @ss_id + 3, 'net.fina.catalog.item.delete');
END;

-- SURVEY

IF
NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.survey.review')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value)
VALUES (@ss_id + 4, @lang_id, 'net.fina.survey.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 4, @ss_id + 4, 'net.fina.survey.review');
END;

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.survey.upload.public')
    BEGIN
        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@ss_id + 5, @lang_id, 'net.fina.survey.upload.public');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@sp_id + 5, @ss_id + 5, 'net.fina.survey.upload.public');
    END;

IF
    NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.survey.upload.private')
    BEGIN
        INSERT INTO SYS_STRINGS (id, langID, value)
        VALUES (@ss_id + 6, @lang_id, 'net.fina.survey.upload.private');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
        VALUES (@sp_id + 6, @ss_id + 6, 'net.fina.survey.upload.private');
    END;

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[survey_sequence]') AND type = 'SO')
CREATE SEQUENCE survey_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

if
(object_id('IN_SURVEY', 'U') is null)
begin
create table IN_SURVEY
(
    ID      numeric not null,
    SURVEY  nvarchar(500),
    NAME    nvarchar(100),
    USER_ID numeric,
);
end;



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
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.parent.folder.node.code')
BEGIN
insert into sys_properties
values ('net.fina.catalog.parent.folder.node.code', 'V01.CATALOG.LIST');
END;

-- create returns in fina from creg imported files, change [fina2nbg] db name to current fina db name and [fina2creg] db name to current creg dbname
begin
transaction ;

DECLARE
@CursorScheduleId INT;
DECLARE
@VersionId INT;

SET
@VersionId = (select top 1 id
                  from fina2nbg.dbo.IN_RETURN_VERSIONS
                  order by ID asc);

DECLARE
CUR_TEST CURSOR FAST_FORWARD FOR
SELECT distinct SCHEDULE_ID
FROM fina2creg.dbo.IN_STAT_FILE_SCHEDULES
where FILE_ID in (select id from fina2creg.dbo.IN_STAT_FILES where EXTERNAL_ID is not null);

OPEN CUR_TEST FETCH NEXT FROM CUR_TEST INTO @CursorScheduleId

WHILE @@FETCH_STATUS = 0
BEGIN
        IF
NOT EXISTS(SELECT * FROM fina2nbg.dbo.IN_RETURNS WHERE SCHEDULEID = @CursorScheduleId)
BEGIN
                PRINT
@CursorScheduleId;
insert into fina2nbg.dbo.IN_RETURNS (ID, VERSION, VERSIONID, SCHEDULEID)
values (NEXT VALUE FOR fina2nbg.dbo.return_sequence, @VersionId, @VersionId, @CursorScheduleId);
insert into fina2nbg.dbo.IN_RETURN_STATUSES (ID, RETURNID, STATUS, STATUSDATE, USERID, NOTE, VERSIONID)
values (NEXT VALUE FOR fina2nbg.dbo.return_status_sequence,
             (select ID from fina2nbg.dbo.IN_RETURNS where SCHEDULEID = @CursorScheduleId),
             3,
             (select top 1 sf.F_UPLOAD_TIME
              from fina2creg.dbo.IN_STAT_FILES sf
                       left join fina2creg.dbo.IN_STAT_FILE_SCHEDULES sfs on sf.ID = sfs.FILE_ID
              where sfs.SCHEDULE_ID = @CursorScheduleId),
             (select top 1 sf.F_UPLOADER_ID
              from fina2creg.dbo.IN_STAT_FILES sf
                       left join fina2creg.dbo.IN_STAT_FILE_SCHEDULES sfs on sf.ID = sfs.FILE_ID
              where sfs.SCHEDULE_ID = @CursorScheduleId),
             'Created From CREG',
             @VersionId);
END;
FETCH NEXT FROM CUR_TEST INTO @CursorScheduleId
END
CLOSE CUR_TEST DEALLOCATE CUR_TEST;
commit transaction;

IF
EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG_ITEM_VERSION')
drop table IN_MDT_CATALOG_ITEM_VERSION;

IF
EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG_ITEM_ROW_VERSION')
drop table IN_MDT_CATALOG_ITEM_ROW_VERSION;

IF
EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG_ITEMS')
drop table IN_MDT_CATALOG_ITEMS;

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[mdt_catalog_item_row_seq]') AND type = 'SO')
CREATE SEQUENCE mdt_catalog_item_row_seq
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG_ITEMS')
create table IN_MDT_CATALOG_ITEMS
(
    ID         bigint,
    NVALUE     float,
    VALUETRID  bigint,
    COLUMN_ID  numeric,
    ROW_ID     bigint,
    VERSION_ID bigint
);


IF
NOT EXISTS (SELECT *  FROM sys.indexes  WHERE name='IN_MDT_CATALOG_ITEMS_ID_index'
                                              AND object_id = OBJECT_ID('IN_MDT_CATALOG_ITEMS'))
create
    index IN_MDT_CATALOG_ITEMS_ID_index
    on IN_MDT_CATALOG_ITEMS (ID);

IF
NOT EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG_ITEM_ROWS')
create table IN_MDT_CATALOG_ITEM_ROWS
(
    ID          bigint,
    LEAF        bit,
    PARENT_ID   bigint,
    ROW_NUMBER  int,
    CATALOG_ID  numeric,
    MDT_NODE_ID bigint
);


IF
NOT EXISTS (SELECT *  FROM sys.indexes  WHERE name='IN_MDT_CATALOG_ITEM_ROWS_ID_index'
                                              AND object_id = OBJECT_ID('IN_MDT_CATALOG_ITEM_ROWS'))
create
    index IN_MDT_CATALOG_ITEM_ROWS_ID_index
    on IN_MDT_CATALOG_ITEM_ROWS (ID);

IF
NOT EXISTS (select * from sys.tables where name = 'IN_MDT_CATALOG_ITEM_ROW_VERSION')
create table IN_MDT_CATALOG_ITEM_ROW_VERSION
(
    ID            bigint,
    CREATION_TIME datetime2(7),
    VERSION_CODE  varchar(255),
    ROW_ID        bigint,
    USER_ID       numeric
);


IF
NOT EXISTS (SELECT *  FROM sys.indexes  WHERE name='IN_MDT_CATALOG_ITEM_ROW_VERSION_ID_index'
                                              AND object_id = OBJECT_ID('IN_MDT_CATALOG_ITEM_ROW_VERSION'))
create
    index IN_MDT_CATALOG_ITEM_ROW_VERSION_ID_index
    on IN_MDT_CATALOG_ITEM_ROW_VERSION (ID);



IF
    NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'REFERENCE_NUMBER' AND Object_ID = Object_ID('IN_MDT_CATALOG'))
    BEGIN
        alter table IN_MDT_CATALOG
            add REFERENCE_NUMBER nvarchar(255);
    END;

IF
    NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'DATA_FORMAT' AND Object_ID = Object_ID('IN_MDT_CATALOG_COLUMNS'))
    BEGIN
        alter table IN_MDT_CATALOG_COLUMNS
            add DATA_FORMAT varchar(255);
    END;

IF
    NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'PROGRESSION' AND Object_ID = Object_ID('IN_SURVEY'))
    BEGIN
        alter table IN_SURVEY
            add PROGRESSION varchar(255);
    END;

IF
    NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'IS_COMPLETED' AND Object_ID = Object_ID('IN_SURVEY'))
    BEGIN
        alter table IN_SURVEY
            add IS_COMPLETED BIT;
    END;

IF
    NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'IS_DELETED' AND Object_ID = Object_ID('IN_MDT_CATALOG_ITEMS'))
    BEGIN
        alter table IN_MDT_CATALOG_ITEMS
            add IS_DELETED BIT;
    END;

IF
    NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'IS_DELETED' AND Object_ID = Object_ID('IN_MDT_CATALOG_ITEM_ROW_VERSION'))
    BEGIN
        alter table IN_MDT_CATALOG_ITEM_ROW_VERSION
            add IS_DELETED BIT;
    END;

IF
    NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'IS_DELETED' AND Object_ID = Object_ID('IN_MDT_CATALOG_ITEM_ROWS'))
    BEGIN
        alter table IN_MDT_CATALOG_ITEM_ROWS
            add IS_DELETED BIT;
    END;


exec('update IN_MDT_CATALOG_ITEM_ROWS set IS_DELETED=0 where IS_DELETED is null')
exec('update IN_MDT_CATALOG_ITEM_ROW_VERSION set IS_DELETED=0 where IS_DELETED is null')
exec('update IN_MDT_CATALOG_ITEMS set IS_DELETED=0 where IS_DELETED is null')

exec('update IN_SURVEY set IS_COMPLETED=1 where IS_COMPLETED is null')



--Catalog permissions
DECLARE
@catalog_ss_id INT
DECLARE
@catalog_sp_id INT
DECLARE
@lcatalog_lang_id INT

SET @catalog_ss_id = (SELECT max(id)
              FROM SYS_STRINGS) + 1
SET @catalog_sp_id = (SELECT max(id)
              FROM SYS_PERMISSIONS) + 1
SET @lcatalog_lang_id = (SELECT id
                FROM sys_languages
                WHERE code LIKE '%en%')


IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.catalog.import')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value)
VALUES (@catalog_ss_id + 1, @lcatalog_lang_id, 'net.fina.catalog.import');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@catalog_sp_id + 1, @catalog_ss_id + 1, 'net.fina.catalog.import');
END;

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.catalog.export')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value)
VALUES (@catalog_ss_id + 2, @lcatalog_lang_id, 'net.fina.catalog.export');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@catalog_sp_id + 2, @catalog_ss_id + 2, 'net.fina.catalog.export');
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
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'DELAY_HOUR' AND Object_ID = Object_ID('IN_SCHEDULES'))
BEGIN
alter table IN_SCHEDULES
    add DELAY_HOUR decimal(10, 0);
END;


exec('update IN_SCHEDULES set DELAY_HOUR = 0 where DELAY_HOUR is null')

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'LIST_VALUES' AND Object_ID = Object_ID('EMS_INSPECTION_COLUMNS'))
BEGIN
alter table EMS_INSPECTION_COLUMNS
    add LIST_VALUES nvarchar(100);
END;

IF
    NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'IS_REQUIRED' AND Object_ID = Object_ID('IN_MDT_CATALOG_COLUMNS'))
    BEGIN
        alter table IN_MDT_CATALOG_COLUMNS
            add IS_REQUIRED BIT;
    END;

exec('update IN_MDT_CATALOG_COLUMNS set IS_REQUIRED=0 where IS_REQUIRED is null')
