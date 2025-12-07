/*
 Database: Oracle

 Author: Lado Melikidze
 E: lado@fina2.net
 Version: 1.0
 Date : 11/04/2019
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '6.0.0'
WHERE prop_key = 'fina2.database.schemaVersion';


ALTER TABLE IN_RETURN_ITEMS MODIFY VALUE nvarchar2(4000);

/*
  Insert permissions
*/
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
select id INTO LANG_ID
from SYS_LANGUAGES
where code like('%en%');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, LANG_ID, 'net.fina.dcs.fileReview');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 1, LANG_ID, 'net.fina.inputManager.review');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.dcs.fileReview');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.inputManager.review');

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


/*  NBG-243 */
ALTER TABLE IN_COMMUNICATOR_USERS_MESSAGE_STATUS
  ADD (LAST_MESSAGE_ID number(10, 0),
        READ_DATE TIMESTAMP );

/* Replace NULLs with 0's in messages that were stored in database before these changes */
UPDATE IN_COMMUNICATOR_USERS_MESSAGE_STATUS
SET LAST_MESSAGE_ID = 0
WHERE LAST_MESSAGE_ID IS NULL;


ALTER TABLE SYS_NOTIFICATION_USERS
  ADD READ_DATE TIMESTAMP;

--  NBG-234: set LAST_CONVERSATION_MESSAGE_DATEs for root messages where absent
CREATE  VIEW root_messages as
  SELECT ID
  FROM IN_COMMUNICATOR_MESSAGES
  WHERE REPLYTOID = 0 AND LAST_CONVERSATION_MESSAGE_DATE is NULL
  ORDER BY ID


DECLARE
row_count number(10, 0);
send_date timestamp;
cur_msg_id number(10, 0);

BEGIN
  SELECT MIN(ID) INTO cur_msg_id FROM root_messages;
  SELECT COUNT(*) INTO row_count FROM root_messages;

  WHILE row_count <> 0
    LOOP
  SELECT MAX(SENDDATE) INTO send_date
  FROM IN_COMMUNICATOR_MESSAGES
  WHERE ID = cur_msg_id OR REPLYTOID = cur_msg_id;

  UPDATE IN_COMMUNICATOR_MESSAGES
  SET LAST_CONVERSATION_MESSAGE_DATE = send_date
  WHERE ID = cur_msg_id;

  DELETE FROM root_messages WHERE ID=cur_msg_id;

  SELECT MIN(ID) INTO cur_msg_id FROM root_messages;

  row_count := row_count - 1;
END LOOP;
END;

-- NBG-312 add USERTYPE column to SYS_USERS and set value to 0
alter table SYS_USERS
  add USERTYPE DECIMAL(10)

update SYS_USERS set USERTYPE = 0;

ALTER TABLE IN_SCHEDULES
    ADD COMMENT nvarchar2(200) NULL;

--NBG-246 Translate net.fina.dcs.undefinedBank
update SYS_STRINGS set VALUE=N'Incorrect Financial institutions.'
  from SYS_PERMISSIONS s
  left join SYS_STRINGS on SYS_STRINGS.ID=s.NAMESTRID
where s.IDNAME like '%net.fina.dcs.undefinedBank%'and LANGID=1;

update SYS_STRINGS set VALUE=N'áá áá¡á¬áá á á¤ááááá¡á£á á ááá¡á¢áá¢á£á¢á.'
  from SYS_PERMISSIONS s
  left join SYS_STRINGS on SYS_STRINGS.ID=s.NAMESTRID
where s.IDNAME like '%net.fina.dcs.undefinedBank%'and LANGID=2;

