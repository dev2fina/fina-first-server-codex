/*
 Database: Oracle

 Author: Lado Melikidze
 E: lado@fina2.net
 Version: 1.0
 Date : 20/10/2020
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.5'
WHERE prop_key = 'fina2.database.schemaVersion';


DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'IN_COMMUNICATOR_NOTIFICATIONS' AND upper(column_name) = 'AUTOMATIC';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_COMMUNICATOR_NOTIFICATIONS ADD AUTOMATIC bit';
    EXECUTE IMMEDIATE 'update IN_COMMUNICATOR_NOTIFICATIONS set AUTOMATIC = 0';
  END IF;
END;


DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'IN_RETURN_DEFINITIONS' AND upper(column_name) = 'MANUALINPUT';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_RETURN_DEFINITIONS ADD MANUALINPUT bit';
    EXECUTE IMMEDIATE 'update IN_RETURN_DEFINITIONS set MANUALINPUT = 0';
  END IF;
END;


DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'IN_COMMUNICATOR_MESSAGES' AND upper(column_name) = 'IS_BOOKMARKED';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_COMMUNICATOR_MESSAGES ADD IS_BOOKMARKED bit';
    EXECUTE IMMEDIATE 'update IN_COMMUNICATOR_MESSAGES set IS_BOOKMARKED = 0';
  END IF;
END;

DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'IN_COMMUNICATOR_MESSAGES' AND upper(column_name) = 'IS_ROOT_BOOKMARKED';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_COMMUNICATOR_MESSAGES ADD IS_ROOT_BOOKMARKED bit';
    EXECUTE IMMEDIATE 'update IN_COMMUNICATOR_MESSAGES set IS_ROOT_BOOKMARKED = 0';
  END IF;
END;

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

SELECT COUNT(*) INTO CNT FROM SYS_PERMISSIONS WHERE idName = 'net.fina.communicator.messages.bookmarks.review';
IF( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.communicator.messages.bookmarks.review');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+1, MAX_SS_ID+1, 'net.fina.communicator.messages.bookmarks.review');
END IF;

SELECT COUNT(*) INTO CNT FROM SYS_PERMISSIONS WHERE idName = 'net.fina.communicator.messages.bookmarks.amend';
IF( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.communicator.messages.bookmarks.amend');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+1, MAX_SS_ID+1, 'net.fina.communicator.messages.bookmarks.amend');
END IF;

SELECT COUNT(*) INTO CNT FROM SYS_PERMISSIONS WHERE idName = 'net.fina.web.userFileRepository';
IF( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.web.userFileRepository');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+1, MAX_SS_ID+1, 'net.fina.web.userFileRepository');
END IF;

SELECT COUNT(*) INTO CNT FROM SYS_PERMISSIONS WHERE idName = 'net.fina.dcs.userFileRepository';
IF( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.dcs.userFileRepository');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+1, MAX_SS_ID+1, 'net.fina.dcs.userFileRepository');
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


-- increase SYS_NOTIFICATIONS notification column size
ALTER TABLE SYS_NOTIFICATIONS ALTER COLUMN notification NVARCHAR2(4000)


DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'SYS_USERS' AND upper(column_name) = 'CONTACTPERSONSTRID';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE SYS_USERS ADD CONTACTPERSONSTRID NUMBER(10)';
  END IF;
END;

-- Modify IN_OVERDUE_RETURN_NOTIFICATIONS table
--remove constraints
begin
    for r in ( select constraint_name from user_constraints
       where  table_name = 'IN_OVERDUE_RETURN_NOTIFICATIONS' and (constraint_type = 'U' or constraint_type = 'P' ))
    loop
        execute immediate 'ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS DROP CONSTRAINT ' ||  r.constraint_name;
    end loop;
end;

ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS alter column NOTIFICATION_TYPE int NOT NULL
ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS DROP COLUMN NOTIFICATION_ID;
ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS ADD CONSTRAINT PK__IN_OVERD__D18C4ECCEB76984D PRIMARY KEY (SCHEDULE_ID, NOTIFICATION_TYPE);


DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'OUT_STORED_REPORTS' AND upper(column_name) = 'REPOSITORY_FILE_ID';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE OUT_STORED_REPORTS ADD REPOSITORY_FILE_ID varchar(255)';
  END IF;
END;

DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'SYS_UPLOADEDFILE' AND upper(column_name) = 'REPOSITORY_FILE_ID';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE SYS_UPLOADEDFILE ADD REPOSITORY_FILE_ID varchar(255)';
  END IF;
END;


DECLARE
  l_cnt integer;
BEGIN
  SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
   WHERE upper(table_name) = 'IN_IMPORTED_RETURNS' AND upper(column_name) = 'REPOSITORY_FILE_ID';

  IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_IMPORTED_RETURNS ADD REPOSITORY_FILE_ID varchar(255)';
  END IF;
END;


