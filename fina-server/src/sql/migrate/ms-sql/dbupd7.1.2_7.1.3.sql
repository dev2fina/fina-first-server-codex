/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.3'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
FI Branch
*/

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'CODE' AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add CODE varchar(100);
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'DELETED' AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add DELETED bit;
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'MDT_DATA_NODE_ID' AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
alter table IN_BANK_BRANCHES
    add MDT_DATA_NODE_ID bigint;
END;

exec('update IN_BANK_BRANCHES set DELETED=0 where DELETED is null')

IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.fi.branch.enable')
BEGIN
insert into sys_properties
values ('net.fina.catalog.fi.branch.enable', 0);
END;

IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.fi.branch.parent.folder.node.code')
BEGIN
insert into sys_properties
values ('net.fina.catalog.fi.branch.parent.folder.node.code', 'V01.CATALOG.FI.BRANCH.LIST');
END;

IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.fi.branch.deleted.parent.folder.node.code')
BEGIN
insert into sys_properties
values ('net.fina.catalog.fi.branch.deleted.parent.folder.node.code', 'V01.CATALOG.FI.BRANCH.LIST.DELETED');
END;

/*
REGIONAL STRUCTURE
*/

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'MDT_DATA_NODE_ID' AND Object_ID = Object_ID('IN_COUNTRY_DATA'))
BEGIN
alter table IN_COUNTRY_DATA
    add MDT_DATA_NODE_ID bigint;
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'DELETED' AND Object_ID = Object_ID('IN_COUNTRY_DATA'))
BEGIN
alter table IN_COUNTRY_DATA
    add DELETED bit;
END;

exec('update IN_COUNTRY_DATA set DELETED=0 where DELETED is null')

IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.reg.str.enable')
BEGIN
insert into sys_properties
values ('net.fina.catalog.reg.str.enable', 0);
END;

IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.reg.str.parent.folder.node.code')
BEGIN
insert into sys_properties
values ('net.fina.catalog.reg.str.parent.folder.node.code', 'V01.CATALOG.REG.STR.LIST');
END;

IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.reg.str.deleted.parent.folder.node.code')
BEGIN
insert into sys_properties
values ('net.fina.catalog.reg.str.deleted.parent.folder.node.code', 'V01.CATALOG.REG.STR.LIST.DELETED');
END;

/*
FI
*/

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'MDT_DATA_NODE_ID' AND Object_ID = Object_ID('IN_BANKS'))
BEGIN
alter table IN_BANKS
    add MDT_DATA_NODE_ID bigint;
END;

IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.fi.enable')
BEGIN
insert into sys_properties
values ('net.fina.catalog.fi.enable', 0);
END;

IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.fi.parent.folder.node.code')
BEGIN
insert into sys_properties
values ('net.fina.catalog.fi.parent.folder.node.code', 'V01.CATALOG.FI.LIST');
END;

IF
NOT EXISTS (SELECT * FROM sys_properties WHERE PROP_KEY = 'net.fina.catalog.fi.deleted.parent.folder.node.code')
BEGIN
insert into sys_properties
values ('net.fina.catalog.fi.deleted.parent.folder.node.code', 'V01.CATALOG.FI.LIST.DELETED');
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


IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.powerBi.user')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value)
VALUES (@ss_id + 1, @lang_id, 'net.fina.powerBi.user');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.powerBi.user');
END;

IF
NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'fina.returns.reg.table.generation')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value)
VALUES (@ss_id + 2, @lang_id, 'fina.returns.reg.table.generation');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 2, @ss_id + 1, 'fina.returns.reg.table.generation');
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
               WHERE Name = 'PROCESS_ENGINE' AND Object_ID = Object_ID('SYS_UPLOADEDFILE'))
BEGIN
alter table SYS_UPLOADEDFILE
    add PROCESS_ENGINE int;
END;

exec('update SYS_UPLOADEDFILE set PROCESS_ENGINE=0');


IF
NOT EXISTS (select * from sys.tables where name = 'IN_REG_FILE_ERRORS')
create table IN_REG_FILE_ERRORS
(
    ID      numeric not null,
    F_ERROR nvarchar(500),
    F_ID    numeric
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[reg_file_error_sequence]') AND type = 'SO')
CREATE SEQUENCE reg_file_error_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


IF
NOT EXISTS (select * from sys.tables where name = 'IN_REG_FILE_SCHEDULES')
create table IN_REG_FILE_SCHEDULES
(
    ID          numeric not null,
    FILE_ID     numeric,
    SCHEDULE_ID numeric
);

IF
NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[reg_file_schedules_sequence]') AND type = 'SO')
CREATE SEQUENCE reg_file_schedules_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'DISABLED' AND Object_ID = Object_ID('SYS_USERS'))
BEGIN
alter table SYS_USERS
    add DISABLED bit default 0;
END;

exec('update SYS_USERS set DISABLED = BLOCKEDBYSYSTEM')

IF  EXISTS (SELECT * FROM sys.objects WHERE name = N'DF__SYS_USERS__BLOCK__1D864D1D')
ALTER TABLE SYS_USERS DROP CONSTRAINT DF__SYS_USERS__BLOCK__1D864D1D;
GO

IF EXISTS(SELECT 1 FROM sys.columns
               WHERE Name = 'BLOCKEDBYSYSTEM' AND Object_ID = Object_ID('SYS_USERS'))
BEGIN
alter table SYS_USERS
DROP
COLUMN BLOCKEDBYSYSTEM;
END;


IF
NOT EXISTS(SELECT *
               FROM sys_properties
               WHERE PROP_KEY = 'fina2.security.passwordWithNums')
BEGIN
insert into sys_properties
values ('fina2.security.passwordWithNums', '0');
END;

IF
NOT EXISTS(SELECT *
               FROM sys_properties
               WHERE PROP_KEY = 'fina2.security.passwordWithLetters')
BEGIN
insert into sys_properties
values ('fina2.security.passwordWithLetters', '0');
END;

IF
NOT EXISTS(SELECT *
               FROM sys_properties
               WHERE PROP_KEY = 'fina2.security.passwordWithLettersUpperCase')
BEGIN
insert into sys_properties
values ('fina2.security.passwordWithLettersUpperCase', '0');
END;

IF
NOT EXISTS(SELECT *
               FROM sys_properties
               WHERE PROP_KEY = 'fina2.security.passwordWithSpecialCharacters')
BEGIN
insert into sys_properties
values ('fina2.security.passwordWithSpecialCharacters', '0');
END;


IF EXISTS(SELECT *
          FROM sys_properties
          WHERE PROP_KEY = 'fina2.security.passwordWithNumsChars')
BEGIN
update sys_properties set VALUE=(select value from SYS_PROPERTIES where PROP_KEY='fina2.security.passwordWithNumsChars') where PROP_KEY = 'fina2.security.passwordWithNums'
update sys_properties set VALUE=(select value from SYS_PROPERTIES where PROP_KEY='fina2.security.passwordWithNumsChars') where PROP_KEY = 'fina2.security.passwordWithLetters'
delete from SYS_PROPERTIES where PROP_KEY='fina2.security.passwordWithNumsChars';
END;

