/*
 Database: Oracle

 Author: Lado Melikidze
 E: nick@fina2.net
 Version: 1.0
 Date : 15/06/2020
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.3'
WHERE prop_key = 'fina2.database.schemaVersion';


CREATE INDEX IDX_IN_RETURN_ITEMS_NODEID ON IN_RETURN_ITEMS(NODEID);


insert into SYS_PROPERTIES(prop_key,value)
select 'net.fina.dcs.signatureCheckerProperty', 'CommonName' from dual
where not exists (select 1 from SYS_PROPERTIES where code = 'net.fina.dcs.signatureCheckerProperty');


DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'IN_RETURN_TYPES' AND upper(column_name) = 'FORMAT';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_RETURN_TYPES ADD FORMAT BLOB';
  END IF;
END;

DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'IN_RETURN_TYPES' AND upper(column_name) = 'excelTemplate';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_RETURN_TYPES ADD excelTemplate bit';
    EXECUTE IMMEDIATE 'update IN_RETURN_TYPES set excelTemplate = 0';
  END IF;
END;

insert into SYS_PROPERTIES(prop_key,value)
select 'net.fina.returnDefinition.templateGeneration.enable', '1' from dual
where not exists (select 1 from SYS_PROPERTIES where code = 'net.fina.returnDefinition.templateGeneration.enable');


-- Create Permissions
DECLARE
MAX_SS_ID NUMBER;
MAX_SP_ID NUMBER;
LANG_ID NUMBER;
CNT integer;

BEGIN
SELECT max(id) + 1
INTO MAX_SS_ID
FROM SYS_STRINGS;
SELECT max(id) + 1
INTO MAX_SP_ID
FROM SYS_PERMISSIONS;
select id INTO LANG_ID
from SYS_LANGUAGES
where code like('%en%');

SELECT COUNT(*) INTO CNT FROM SYS_PERMISSIONS WHERE idName = 'net.fina.communicator.messages.acceptWithoutAttachment';
IF( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.communicator.messages.acceptWithoutAttachment');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+1, MAX_SS_ID+1, 'net.fina.communicator.messages.acceptWithoutAttachment');
END IF;

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
COMMIT;

-- update EMS INSPECTION TYPE
alter table EMS_INSPECTION_TYPE_DESCRIPTION_I18N alter column name nvarchar(255) null
