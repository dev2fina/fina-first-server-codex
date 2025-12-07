/*
 Database: Oracle

 Author: Lado Melikidze
 E: lado@fina2.net
 Version: 1.0
 Date : 23/04/2020
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.2'
WHERE prop_key = 'fina2.database.schemaVersion';



insert into SYS_PROPERTIES(prop_key,value)
select 'net.fina.web.dashboard.default', '{"columnCount":2,"portalColor":"#ffffff","dashletColumn":{"CLOCK":0,"NUMBER_OF_FIS_BY_TYPE":1}}' from dual
where not exists (select 1 from SYS_PROPERTIES where code = 'net.fina.web.dashboard.default');

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

SELECT COUNT(*) INTO CNT FROM SYS_PERMISSIONS WHERE idName = 'net.fina.first.dashboard.review';
IF( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.first.dashboard.review');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+1, MAX_SS_ID+1, 'net.fina.first.dashboard.review');
END IF;

SELECT COUNT(*) INTO CNT FROM SYS_PERMISSIONS WHERE idName = 'net.fina.first.attestation.amend';
IF( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+2, LANG_ID, 'net.fina.first.attestation.amend');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+2, MAX_SS_ID+2, 'net.fina.first.attestation.amend');
END IF;

SELECT COUNT(*) INTO CNT FROM SYS_PERMISSIONS WHERE idName = 'net.fina.first.attestation.delete';
IF( CNT = 0 )
THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+3, LANG_ID, 'net.fina.first.attestation.delete');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+3, MAX_SS_ID+3, 'net.fina.first.attestation.delete');
END IF;

SELECT COUNT(*) INTO CNT FROM SYS_PERMISSIONS WHERE idName = 'net.fina.tool.bundleTranslator';
IF( CNT = 0 )
THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+4, LANG_ID, 'net.fina.tool.bundleTranslator');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+4, MAX_SS_ID+4, 'net.fina.tool.bundleTranslator');
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


DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'SYS_LANGUAGES' AND upper(column_name) = 'DELETED';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE SYS_LANGUAGES ADD DELETED NUMBER(1)';
    EXECUTE IMMEDIATE 'UPDATE SYS_LANGUAGES SET DELETED = 0';
  END IF;
END;
