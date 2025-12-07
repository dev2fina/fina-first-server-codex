/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.5'
WHERE prop_key = 'fina2.database.schemaVersion';

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
  AND upper(column_name) = 'CONTACT_PERSON';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add CONTACT_PERSON varchar(255)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'CREATED_AT';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add CREATED_AT date';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'MODIFIED_AT';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add MODIFIED_AT date';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'CLOSE_DATE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add CLOSE_DATE date';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'SUSPENSION_DATE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add SUSPENSION_DATE date';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'RENEWAL_DATE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add RENEWAL_DATE date';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'EMAIL';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add EMAIL varchar(255)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'PHONE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add PHONE varchar(255)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'REG_NUMBER';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add REG_NUMBER bigint';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'OKPO_CODE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add OKPO_CODE bigint';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'STORAGE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add STORAGE bit';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'TYPEID';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add TYPEID bigint';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_BANK_BRANCH_TYPES';
IF
( l_cnt = 0 )
  THEN
create table IN_BANK_BRANCH_TYPES
(
    ID          numeric not null,
    code        varchar(30),
    NAMESTRID   numeric,
    JSON_CONFIG varchar(250),
);
END IF;
END;

begin
execute immediate 'drop sequence fi_branch_types_sequence';
exception
when others then null;
end;

CREATE SEQUENCE fi_branch_types_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'NUMBER_IN_REGISTER';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add NUMBER_IN_REGISTER bigint';
END IF;
END;



DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'REPRESENTATIVE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add REPRESENTATIVE varchar(255)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'REORGANISATION';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add REORGANISATION int';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'MOBILE_OFFICES';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add MOBILE_OFFICES int';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'REGISTRATION_DATE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add REGISTRATION_DATE datetime';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'CLOSE_DATE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add CLOSE_DATE datetime';
END IF;
END;

commit;

update IN_BANKS
set MOBILE_OFFICES=0
where MOBILE_OFFICES is null;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'WEB_SITE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add WEB_SITE varchar(100)';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'EMPLOYES';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add EMPLOYES int';
END IF;
END;
commit;

update IN_BANKS
set EMPLOYES=0
where EMPLOYES is null;

drop table IN_REG_FILE_ERRORS;

begin
execute immediate 'drop sequence calendar_event_sequence';
exception
when others then null;
end;

CREATE SEQUENCE calendar_event_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_CALENDAR_EVENTS';
IF
( l_cnt = 0 )
  THEN
create table IN_CALENDAR_EVENTS
(
    ID         numeric not null,
    DATE       date,
    EVENT_TYPE numeric,
    COMMENT    varchar(255),
    GROUP_UUID varchar(64)
);
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'MANAGER_APPOINTMENT_DATE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add MANAGER_APPOINTMENT_DATE date';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'MANAGER_ID';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add MANAGER_ID numeric';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'CHIEF_ACCOUNTANT_APPOINTMENT_DATE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add CHIEF_ACCOUNTANT_APPOINTMENT_DATE date';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'CHIEF_ACCOUNTANT_ID';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_BRANCHES add CHIEF_ACCOUNTANT_ID numeric';
END IF;
END;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_MANAGEMENT'
  AND upper(column_name) = 'PORTFOLIO';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_MANAGEMENT add PORTFOLIO varchar(200)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_MANAGEMENT'
  AND upper(column_name) = 'DATEOFAPPROVAL';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANK_MANAGEMENT add DATEOFAPPROVAL date';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_MDT_NODES'
  AND upper(column_name) = 'IS_KEY';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_MDT_NODES add IS_KEY bit';
END IF;
END;

update IN_MDT_NODES
set IS_KEY=0
where IS_KEY is null;