/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.4'
WHERE prop_key = 'fina2.database.schemaVersion';


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'SYS_AUDIT_LOG'
  AND upper(column_name) = 'CLIENT_NAME';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table SYS_AUDIT_LOG add CLIENT_NAME varchar(200)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_RETURN_DEFINITIONS'
  AND upper(column_name) = 'GENERAL_INFO';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_RETURN_DEFINITIONS add GENERAL_INFO varchar(500)';
END IF;
END;

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.reg.string.column.maxLength';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.reg.string.column.maxLength','4000')
END IF;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG'
  AND upper(column_name) = 'ATTACHMENT';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_MDT_CATALOG add ATTACHMENT blob';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG'
  AND upper(column_name) = 'ATTACHMENT_NAME';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_MDT_CATALOG add ATTACHMENT_NAME varchar(200)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG'
  AND upper(column_name) = 'ANCESTOR_CATALOG_INFO';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_MDT_CATALOG add ANCESTOR_CATALOG_INFO varchar(200)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG'
  AND upper(column_name) = 'IN_LAW_DOCUMENT_ID';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_MDT_CATALOG add IN_LAW_DOCUMENT_ID numeric';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG'
  AND upper(column_name) = 'VALID_TO';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_MDT_CATALOG add VALID_TO TIMESTAMP null';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG'
  AND upper(column_name) = 'NORMATIVE_DOCUMENT';

IF
( l_cnt > 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_MDT_CATALOG DROP COLUMN NORMATIVE_DOCUMENT';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG'
  AND upper(column_name) = 'METHODOLOGY_DOCUMENT';

IF
( l_cnt > 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_MDT_CATALOG DROP COLUMN METHODOLOGY_DOCUMENT';
END IF;
END;


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
WHERE idName = 'net.fina.active.users.review';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.active.users.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.active.users.review');
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


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG'
  AND upper(column_name) = 'CODE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_MDT_CATALOG add CODE varchar(255)';
END IF;
END;


SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.trash.folder.node.code';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.trash.folder.node.code','V01.CATALOG.TRASH')
END IF;



SELECT COUNT(*)
INTO CNT
FROM IN_MDT_NODES
WHERE CODE = 'V01.CATALOG.TRASH';
IF
( CNT = 0 ) THEN
    insert into IN_MDT_NODES (ID, CODE, CATALOG, TYPE, DISABLED, REQUIRED,SEQUENCE,PARENTID,NAMESTRID,DATATYPE)
values ((select VALUE from SYS_ID_GENERATOR where PK_COLUMN_NAME = 'IN_MDT_NODES_MAXID') + 1,
        'V01.CATALOG.TRASH', 1, 1, 0, 0,(select max(sequence)+1 from IN_MDT_NODES where PARENTID=0),0,0,0);

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1 FROM IN_MDT_NODES)
WHERE PK_COLUMN_NAME = 'IN_MDT_NODES_MAXID';
END;

END IF;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'SYS_LANGUAGES'
  AND upper(column_name) = 'DATETIMEFORMAT';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table SYS_LANGUAGES add DATETIMEFORMAT varchar(24)';
END IF;
END;

update SYS_LANGUAGES
set DATETIMEFORMAT='dd/MM/yyyy HH:mm' when DATETIMEFORMAT is null;



DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_SCHEDULES'
  AND upper(column_name) = 'DELAY_MINUTE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_SCHEDULES add DELAY_MINUTE int default 0';
END IF;
END;


update IN_SCHEDULES
set DELAY_MINUTE = 0 where DELAY_MINUTE is null;
