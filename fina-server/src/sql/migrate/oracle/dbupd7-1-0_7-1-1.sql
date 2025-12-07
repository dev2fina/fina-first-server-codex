/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.1'
WHERE prop_key = 'fina2.database.schemaVersion';

-- permissions
DECLARE
MAX_SS_ID NUMBER;
MAX_SP_ID NUMBER;
LANG_ID NUMBER;

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
WHERE idName = 'net.fina.faq.ask.question';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, LANG_ID, 'net.fina.faq.ask.question');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.faq.ask.question');
END IF;

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
