/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.2'
WHERE prop_key = 'fina2.database.schemaVersion';


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_COMMUNICATOR_ATTACHEMENTS'
  AND upper(column_name) = 'CONTENT_SIZE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_COMMUNICATOR_ATTACHEMENTS add CONTENT_SIZE int default 0';
END IF;
END;

update IN_COMMUNICATOR_ATTACHEMENTS
set CONTENT_SIZE=(DBMS_LOB.getlength(CONTENT));


DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_MDT_CATALOG';
IF
( l_cnt = 0 )
  THEN
create table IN_MDT_CATALOG
(
    ID                   numeric,
    MDT_NODE_ID          numeric,
    ABBREVIATION         nvarchar2 (150),
    SOURCE               nvarchar2(150),
    NORMATIVE_DOCUMENT   blob,
    METHODOLOGY_DOCUMENT blob,
    CREATION_TIME        TIMESTAMP null,
    MODIFICATION_TIME    TIMESTAMP null
);
END IF;
END;

begin
execute immediate 'drop sequence mdt_catalog_seq';
exception
when others then null;
end;

CREATE SEQUENCE mdt_catalog_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;


-- IN_MDT_CATALOG_COLUMNS

DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_MDT_CATALOG_COLUMNS';
IF
( l_cnt = 0 )
  THEN
create table IN_MDT_CATALOG_COLUMNS
(
    ID         numeric not null,
    DATA_TYPE  numeric,
    NAMESTRID  numeric,
    IS_KEY     numeric,
    SEQUENCE   numeric,
    CATALOG_ID numeric
);
END IF;
END;

begin
execute immediate 'drop sequence mdt_catalog_col_seq';
exception
when others then null;
end;

CREATE SEQUENCE mdt_catalog_col_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

-- IN_MDT_CATALOG_ITEMS

DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_MDT_CATALOG_ITEMS';
IF
( l_cnt = 0 )
  THEN
create table IN_MDT_CATALOG_ITEMS
(
    ID          numeric,
    ROW_NUMBER  numeric,
    COLUMN_ID   numeric,
    CATALOG_ID  numeric,
    MDT_NODE_ID numeric,
    VERSION_ID  numeric,
    VALUETRID   numeric,
    NVALUE      float
);
END IF;
END;

begin
execute immediate 'drop sequence mdt_catalog_item_seq';
exception
when others then null;
end;

CREATE SEQUENCE mdt_catalog_item_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

-- IN_MDT_CATALOG_ITEM_VERSION

DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_MDT_CATALOG_ITEM_VERSION';
IF
( l_cnt = 0 )
  THEN
create table IN_MDT_CATALOG_ITEM_VERSION
(
    ID            numeric,
    CATALOG_ID    numeric,
    ROW_NUMBER    numeric,
    CREATION_TIME timestamp,
    VERSION_CODE  varchar(100),
    USER_ID       numeric
);
END IF;
END;

begin
execute immediate 'drop sequence mdt_catalog_item_ver_seq';
exception
when others then null;
end;

CREATE SEQUENCE mdt_catalog_item_ver_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

-- UPDATE MDT NODES

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_NODES'
  AND upper(column_name) = 'CATALOG';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_MDT_NODES ADD CATALOG numeric';
END IF;
END;

update IN_MDT_NODES
set CATALOG=0 when CATALOG is null;
commit;


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
WHERE idName = 'net.fina.catalog.item.review';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, LANG_ID, 'net.fina.catalog.item.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.catalog.item.review');
END IF;

SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.catalog.item.amend';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.catalog.item.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.catalog.item.amend');
END IF;

SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.catalog.item.delete';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+2, LANG_ID, 'net.fina.catalog.item.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (MAX_SP_ID + 2, MAX_SS_ID + 2, 'net.fina.catalog.item.delete');
END IF;

-- SURVEY

SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.survey.review';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+3, LANG_ID, 'net.fina.survey.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (MAX_SP_ID + 3, MAX_SS_ID + 3, 'net.fina.survey.review');
END IF;

SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.survey.upload.public';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 4, LANG_ID, 'net.fina.survey.upload.public');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (MAX_SP_ID + 4, MAX_SS_ID + 4, 'net.fina.survey.upload.public');
END IF;

SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.survey.upload.private';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 5, LANG_ID, 'net.fina.survey.upload.private');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (MAX_SP_ID + 5, MAX_SS_ID + 5, 'net.fina.survey.upload.private');
END IF;

begin
execute immediate 'drop sequence survey_sequence';
exception
when others then null;
end;

CREATE SEQUENCE survey_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_SURVEY';
IF
( l_cnt = 0 )
  THEN
create table IN_SURVEY
(
    ID      numeric,
    SURVEY  nvarchar2(500),
    NAME    nvarchar2(100),
    USER_ID numeric,
);
END IF;
END;

END
commit;

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

SELECT COUNT(*)
INTO CNT
FROM sys_properties
WHERE PROP_KEY = 'net.fina.catalog.parent.folder.node.code';
IF
( CNT = 0 ) THEN
    insert into sys_properties values('net.fina.catalog.parent.folder.node.code','V01.CATALOG.LIST')
END IF;


drop table IN_MDT_CATALOG_ITEM_VERSION;
drop table IN_MDT_CATALOG_ITEM_ROW_VERSION;
drop table IN_MDT_CATALOG_ITEMS;

begin
execute immediate 'drop sequence mdt_catalog_item_row_seq';
exception
when others then null;
end;

CREATE SEQUENCE mdt_catalog_item_row_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_MDT_CATALOG_ITEMS';
IF
( l_cnt = 0 )
  THEN
create table IN_MDT_CATALOG_ITEMS
(
    ID         number(19),
    NVALUE     double,
    VALUETRID  number(19),
    COLUMN_ID  number(18,0),
    ROW_ID     number(19),
    VERSION_ID number(19)
);
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_MDT_CATALOG_ITEM_ROWS';
IF
( l_cnt = 0 )
  THEN
create table IN_MDT_CATALOG_ITEM_ROWS
(
    ID          number(19),
    LEAF        number(1),
    PARENT_ID   number(19),
    ROW_NUMBER  number(10),
    CATALOG_ID  number(18,0),
    MDT_NODE_ID number(19)
);
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_MDT_CATALOG_ITEM_ROW_VERSION';
IF
( l_cnt = 0 )
  THEN
create table IN_MDT_CATALOG_ITEM_ROW_VERSION
(
    ID            number(19),
    CREATION_TIME datetime2(7),
    VERSION_CODE [ varchar](255),
    ROW_ID        bigint,
    USER_ID       numeric
);
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG'
  AND upper(column_name) = 'REFERENCE_NUMBER';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_MDT_CATALOG ADD REFERENCE_NUMBER nvarchar2(255)';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG_COLUMNS'
  AND upper(column_name) = 'DATA_FORMAT';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_MDT_CATALOG_COLUMNS ADD DATA_FORMAT varchar2(255)';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_SURVEY'
  AND upper(column_name) = 'PROGRESSION';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_SURVEY ADD PROGRESSION nvarchar2(255)';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_SURVEY'
  AND upper(column_name) = 'IS_COMPLETED';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_SURVEY ADD IS_COMPLETED numeric';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG_ITEMS'
  AND upper(column_name) = 'IS_DELETED';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_MDT_CATALOG_ITEMS ADD IS_DELETED numeric';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG_ITEM_ROW_VERSION'
  AND upper(column_name) = 'IS_COMPLETED';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_MDT_CATALOG_ITEM_ROW_VERSION ADD IS_DELETED numeric';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG_ITEM_ROWS'
  AND upper(column_name) = 'IS_COMPLETED';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_MDT_CATALOG_ITEM_ROWS ADD IS_DELETED numeric';
END IF;
END;

update IN_MDT_CATALOG_ITEMS
set IS_DELETED=0 when IS_DELETED is null;

update IN_MDT_CATALOG_ITEM_ROW_VERSION
set IS_DELETED=0 when IS_DELETED is null;

update IN_MDT_CATALOG_ITEM_ROWS
set IS_DELETED=0 when IS_DELETED is null;

update IN_SURVEY
set IS_COMPLETED=1 when IS_COMPLETED is null;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_SCHEDULES'
  AND upper(column_name) = 'DELAY_HOUR';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_SCHEDULES add DELAY_HOUR int default 0';
END IF;
END;


update IN_SCHEDULES
set DELAY_HOUR = 0 where DELAY_HOUR is null;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'EMS_INSPECTION_COLUMNS'
  AND upper(column_name) = 'LIST_VALUES';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table EMS_INSPECTION_COLUMNS add LIST_VALUES nvarchar2(200)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_CATALOG_COLUMNS'
  AND upper(column_name) = 'IS_REQUIRED';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_MDT_CATALOG_COLUMNS ADD IS_REQUIRED numeric';
END IF;
END;

update IN_MDT_CATALOG_COLUMNS
set IS_REQUIRED=0 when IS_REQUIRED is null;