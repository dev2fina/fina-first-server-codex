/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.4'
WHERE prop_key = 'fina2.database.schemaVersion';


IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'CLIENT_NAME' AND Object_ID = Object_ID('SYS_AUDIT_LOG'))
BEGIN
alter table SYS_AUDIT_LOG
    add CLIENT_NAME varchar(200)
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'GENERAL_INFO' AND Object_ID = Object_ID('IN_RETURN_DEFINITIONS'))
BEGIN
alter table IN_RETURN_DEFINITIONS
    add GENERAL_INFO nvarchar(500)
END;


IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.reg.string.column.maxLength')
BEGIN
insert into sys_properties
values ('net.fina.reg.string.column.maxLength', '4000');
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'ATTACHMENT' AND Object_ID = Object_ID('IN_MDT_CATALOG'))
BEGIN
alter table IN_MDT_CATALOG
    add ATTACHMENT image
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'ATTACHMENT_NAME' AND Object_ID = Object_ID('IN_MDT_CATALOG'))
BEGIN
alter table IN_MDT_CATALOG
    add ATTACHMENT_NAME nvarchar(200)
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'ANCESTOR_CATALOG_INFO' AND Object_ID = Object_ID('IN_MDT_CATALOG'))
BEGIN
alter table IN_MDT_CATALOG
    add ANCESTOR_CATALOG_INFO nvarchar(200)
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'IN_LAW_DOCUMENT_ID' AND Object_ID = Object_ID('IN_MDT_CATALOG'))
BEGIN
alter table IN_MDT_CATALOG
    add IN_LAW_DOCUMENT_ID numeric
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'VALID_TO' AND Object_ID = Object_ID('IN_MDT_CATALOG'))
BEGIN
alter table IN_MDT_CATALOG
    add VALID_TO datetime null
END;

IF
EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'NORMATIVE_DOCUMENT' AND Object_ID = Object_ID('IN_MDT_CATALOG'))
BEGIN
alter table IN_MDT_CATALOG
DROP
COLUMN NORMATIVE_DOCUMENT;
END;

IF
EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'METHODOLOGY_DOCUMENT' AND Object_ID = Object_ID('IN_MDT_CATALOG'))
BEGIN
alter table IN_MDT_CATALOG
DROP
COLUMN METHODOLOGY_DOCUMENT;
END;

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


IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.active.users.review')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value)
VALUES (@ss_id + 1, @lang_id, 'net.fina.active.users.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.active.users.review');
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
            WHERE Name = 'CODE' AND Object_ID = Object_ID('IN_MDT_CATALOG'))
BEGIN
alter table IN_MDT_CATALOG
    add CODE nvarchar(255);
END;

IF
NOT EXISTS(SELECT *
               FROM sys_properties
               WHERE PROP_KEY = 'net.fina.catalog.trash.folder.node.code')
BEGIN
insert into sys_properties
values ('net.fina.catalog.trash.folder.node.code', 'V01.CATALOG.TRASH');
END;


IF
NOT EXISTS(SELECT *
               FROM IN_MDT_NODES
               WHERE CODE = 'V01.CATALOG.TRASH')
BEGIN
insert into IN_MDT_NODES (ID, CODE, CATALOG, TYPE, DISABLED, REQUIRED, SEQUENCE, PARENTID, NAMESTRID, DATATYPE)
values ((select VALUE from SYS_ID_GENERATOR where PK_COLUMN_NAME = 'IN_MDT_NODES_MAXID') + 1,
        'V01.CATALOG.TRASH', 1, 1, 0, 0, (select max(sequence) + 1 from IN_MDT_NODES where PARENTID = 0), 0, 0, 0);


-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1 FROM IN_MDT_NODES)
WHERE PK_COLUMN_NAME = 'IN_MDT_NODES_MAXID';
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'DATETIMEFORMAT' AND Object_ID = Object_ID('SYS_LANGUAGES'))
BEGIN
alter table SYS_LANGUAGES
    add DATETIMEFORMAT nvarchar(24)
END;

exec('update SYS_LANGUAGES set DATETIMEFORMAT=''dd/MM/yyyy HH:mm'' where DATETIMEFORMAT is null');



IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'DELAY_MINUTE' AND Object_ID = Object_ID('IN_SCHEDULES'))
BEGIN
alter table IN_SCHEDULES
    add DELAY_MINUTE decimal(10, 0);
END;


exec('update IN_SCHEDULES set DELAY_MINUTE = 0 where DELAY_MINUTE is null')
