/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.3'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
FI Branch
*/
DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'CODE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_BANK_BRANCHES ADD CODE varchar2(100)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'DELETED';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_BANK_BRANCHES ADD DELETED NUMBER(3)';
END IF;
END;

update IN_BANK_BRANCHES
set DELETED=0 when DELETED is null;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.fi.branch.enable';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.fi.branch.enable',0)
END IF;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.fi.branch.parent.folder.node.code';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.fi.branch.parent.folder.node.code','V01.CATALOG.FI.BRANCH.LIST')
END IF;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'MDT_DATA_NODE_ID';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_BANK_BRANCHES ADD MDT_DATA_NODE_ID number(19)';
END IF;
END;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.fi.branch.deleted.parent.folder.node.code';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.fi.branch.deleted.parent.folder.node.code','V01.CATALOG.FI.BRANCH.LIST.DELETED')
END IF;

/*
REGIONAL STRUCTURE
*/

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_COUNTRY_DATA'
  AND upper(column_name) = 'DELETED';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_COUNTRY_DATA ADD DELETED NUMBER(3)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_COUNTRY_DATA'
  AND upper(column_name) = 'MDT_DATA_NODE_ID';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_COUNTRY_DATA ADD MDT_DATA_NODE_ID number(19)';
END IF;
END;

update IN_COUNTRY_DATA
set DELETED=0 when DELETED is null;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.reg.str.enable';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.reg.str.enable',0)
END IF;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.reg.str.parent.folder.node.code';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.reg.str.parent.folder.node.code','V01.CATALOG.REG.STR.LIST')
END IF;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.reg.str.deleted.parent.folder.node.code';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.reg.str.deleted.parent.folder.node.code','V01.CATALOG.REG.STR.LIST.DELETED')
END IF;

/*
FI
*/

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'MDT_DATA_NODE_ID';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_BANKS ADD MDT_DATA_NODE_ID number(19)';
END IF;
END;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.fi.enable';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.fi.enable',0)
END IF;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.fi.parent.folder.node.code';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.fi.parent.folder.node.code','V01.CATALOG.FI.LIST')
END IF;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.fi.deleted.parent.folder.node.code';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.fi.deleted.parent.folder.node.code','V01.CATALOG.FI.LIST.DELETED')
END IF;

-- permissions
DECLARE
MAX_SS_ID NUMBER;
MAX_SP_ID
NUMBER;
LANG_ID
NUMBER;

BEGIN
SELECT max(id) + 1
INTO MAX_SS_ID
FROM SYS_STRINGS;
SELECT max(id) + 1
INTO MAX_SP_ID
FROM SYS_PERMISSIONS;
select id
INTO LANG_ID
from SYS_LANGUAGES
where code like ('%en%');

SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.powerBi.user';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.powerBi.user');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.powerBi.user');
END IF;

SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'fina.returns.reg.table.generation';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+2, LANG_ID, 'fina.returns.reg.table.generation');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (MAX_SP_ID + 2, MAX_SS_ID + 2, 'fina.returns.reg.table.generation');
END IF;

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1
             FROM SYS_STRINGS)
WHERE PK_COLUMN_NAME = 'SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1
             FROM SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME = 'SYS_PERMISSION_MAXID';
COMMIT;

-- // Reg

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'SYS_UPLOADEDFILE'
  AND upper(column_name) = 'PROCESS_ENGINE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE SYS_UPLOADEDFILE ADD PROCESS_ENGINE numeric';
END IF;
END;

update SYS_UPLOADEDFILE
set PROCESS_ENGINE=0;


DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_REG_FILE_ERRORS';
IF
( l_cnt = 0 )
  THEN
create table IN_REG_FILE_ERRORS
(
    ID      numeric not null,
    F_ERROR nvarchar2(500),
    F_ID    numeric
);
END IF;
END;

begin
execute immediate 'drop sequence reg_file_error_sequence';
exception
when others then null;
end;

CREATE SEQUENCE reg_file_error_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;



DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_REG_FILE_SCHEDULES';
IF
( l_cnt = 0 )
  THEN
create table IN_REG_FILE_SCHEDULES
(
    ID          numeric not null,
    FILE_ID     numeric,
    SCHEDULE_ID numeric
);
END IF;
END;

begin
execute immediate 'drop sequence reg_file_schedules_sequence';
exception
when others then null;
end;

CREATE SEQUENCE reg_file_schedules_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'SYS_USERS'
  AND upper(column_name) = 'DISABLED';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE SYS_USERS ADD DISABLED NUMBER(1) default 0';
END IF;
END;

update SYS_USERS
set set DISABLED = BLOCKEDBYSYSTEM;

ALTER TABLE SYS_USERS DROP CONSTRAINT DF__SYS_USERS__BLOCK__1D864D1D;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'SYS_USERS'
  AND upper(column_name) = 'BLOCKEDBYSYSTEM';

IF
( l_cnt > 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table SYS_USERS DROP COLUMN BLOCKEDBYSYSTEM';
END IF;
END;


SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'fina2.security.passwordWithNums';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('fina2.security.passwordWithNums','0')
END IF;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'fina2.security.passwordWithLetters';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('fina2.security.passwordWithLetters','0')
END IF;


SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'fina2.security.passwordWithLettersUpperCase';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('fina2.security.passwordWithLettersUpperCase','0')
END IF;



SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'fina2.security.passwordWithSpecialCharacters';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('fina2.security.passwordWithSpecialCharacters','0')
END IF;


INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'fina2.security.passwordWithNumsChars';
IF
( CNT > 0 ) THEN
update sys_properties set VALUE=(select value from SYS_PROPERTIES where PROP_KEY='fina2.security.passwordWithNumsChars') where PROP_KEY = 'fina2.security.passwordWithNums'
update sys_properties set VALUE=(select value from SYS_PROPERTIES where PROP_KEY='fina2.security.passwordWithNumsChars') where PROP_KEY = 'fina2.security.passwordWithLetters'
delete from SYS_PROPERTIES where PROP_KEY='fina2.security.passwordWithNumsChars';
END IF;

