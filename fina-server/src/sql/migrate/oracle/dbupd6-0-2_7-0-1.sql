/*
 Database: Oracle

 Author: Otar Iantbelidze
 E: oto@fina2.net
 Version: 1.0
 Date : 13/03/2020
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.1'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
Insert permissions
 */
DECLARE
MAX_SS_ID NUMBER
MAX_SP_ID NUMBER
LANG_ID NUMBER

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

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+1, LANG_ID, 'net.fina.first.registry.import');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+1, MAX_SS_ID+1, 'net.fina.first.registry.import');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID+2, LANG_ID, 'net.fina.web.notifications');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID+2, MAX_SS_ID+2, 'net.fina.web.notifications');

END;
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

CREATE SEQUENCE notifications_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

CREATE TABLE SYS_NOTIFICATIONS (
    ID number not null,
    notify number not null,
    notification nvarchar(500),
    datetime_added date,
    datetime_read date,
);

alter table EMS_INSPECTIONS
	add SYNCRONIZED bit;

alter table EMS_SANCTIONS
	add SYNCRONIZED bit;


alter table EMS_SANCTION_FINES
	add SYNCRONIZED bit;


update  EMS_SANCTION_FINES set SYNCRONIZED=0;
update  EMS_SANCTIONs set SYNCRONIZED=0;
update  EMS_INSPECTIONS set SYNCRONIZED=0;


create table SYS_ROLE_BANKS(
            ROLE_ID decimal(10),
            BANK_ID decimal(10)
);

insert  into SYS_PROPERTIES(prop_key,value) values('net.fina.dcs.fileUpload.restrictWhileProcessing','-1');

alter table SYS_USERS add BLOCKEDBYSYSTEM NUMBER(1) default 0;
update SYS_USERS set BLOCKEDBYSYSTEM = 0;