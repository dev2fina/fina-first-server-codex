/*
 Database: Oracle

 Author: Lado Melikidze
 E: lado@fina2.net
 Version: 1.0
 Date : 23/12/2020
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.6'
WHERE prop_key = 'fina2.database.schemaVersion';


insert into SYS_PROPERTIES(prop_key,value)
select 'net.fina.contentStorage.provider', 'DATABASE' from dual
where not exists (select 1 from SYS_PROPERTIES where code = 'net.fina.contentStorage.provider');

insert into SYS_PROPERTIES(prop_key,value)
select 'net.fina.contentStorage.repository.provider', 'JCR' from dual
where not exists (select 1 from SYS_PROPERTIES where code = 'net.fina.contentStorage.repository.provider');


DECLARE
  l_cnt integer;
BEGIN
   SELECT count(*) into l_cnt FROM dba_tables where table_name = 'SYS_ROLE_MDT';

  IF( l_cnt = 0 )
  THEN
    CREATE TABLE SYS_ROLE_MDT (
        ROLE_ID NUMBER(19) NOT NULL,
        NODE_ID NUMBER(19) NOT NULL,
        CAN_AMEND NUMBER(1) NOT NULL DEFAULT 0
    )
  END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
WHERE upper(table_name) = 'OUT_STORED_REPORTS' AND upper(column_name) = 'REPOSITORY_FILE_VERSION_ID';

IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE OUT_STORED_REPORTS ADD REPOSITORY_FILE_ID varchar(255)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
WHERE upper(table_name) = 'SYS_UPLOADEDFILE' AND upper(column_name) = 'REPOSITORY_FILE_VERSION_ID';

IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE SYS_UPLOADEDFILE ADD REPOSITORY_FILE_ID varchar(255)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
WHERE upper(table_name) = 'IN_IMPORTED_RETURNS' AND upper(column_name) = 'REPOSITORY_FILE_VERSION_ID';

IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_IMPORTED_RETURNS ADD REPOSITORY_FILE_ID varchar(255)';
END IF;
END;
