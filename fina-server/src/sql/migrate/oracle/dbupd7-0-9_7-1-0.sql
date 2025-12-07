/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.0'
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
WHERE idName = 'net.fina.feedback.review';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, LANG_ID, 'net.fina.feedback.review');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.feedback.review');
END IF;

SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.feedback.amend';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 1, LANG_ID, 'net.fina.feedback.amend');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.feedback.amend');
END IF;

SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.feedbackCategory.amend';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 2, LANG_ID, 'net.fina.feedbackCategory.amend');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 2, MAX_SS_ID + 2, 'net.fina.feedbackCategory.amend');
END IF;


SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.first.config.amend';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 3, LANG_ID, 'net.fina.first.config.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 3, MAX_SS_ID + 3, 'net.fina.first.config.amend');
END IF;


SELECT COUNT(*)
INTO CNT
FROM SYS_PERMISSIONS
WHERE idName = 'net.fina.first.config.delete';
IF
( CNT = 0 ) THEN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 4, LANG_ID, 'net.fina.first.config.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 4, MAX_SS_ID + 4, 'net.fina.first.config.delete');
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

-- FEEDBACK
CREATE SEQUENCE feedback_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;
CREATE SEQUENCE feedback_categories_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;


DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_FEEDBACK';
IF
( l_cnt = 0 )
  THEN
create table IN_FEEDBACK
(
    ID               number(19) not null,
    DESCRIPTIONSTRID number(19),
    CATEGORY_ID      number(19),
    RATING           number(19),
);
END IF;
END;



DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_FEEDBACK_CATEGORY';
IF
( l_cnt = 0 )
  THEN
create table IN_FEEDBACK_CATEGORY
(
    ID        number(19) not null,
    NAMESTRID number(19),
);
END IF;
END;

ALTER TABLE SYS_USERS
ALTER COLUMN login  NVARCHAR(40) ;


DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'EMS_SANCTIONS'
  AND upper(column_name) = 'ADMIN_ID';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE EMS_SANCTIONS ADD ADMIN_ID nvarchar(50)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'EMS_SANCTIONS'
  AND upper(column_name) = 'ADMIN_NAME';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE EMS_SANCTIONS ADD ADMIN_NAME nvarchar(50)';
END IF;
END;

