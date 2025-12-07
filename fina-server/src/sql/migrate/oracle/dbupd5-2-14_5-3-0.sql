/*
 Database: Oracle 11g, 12c

 Author: Vamekh Goiati
 E: goiati@fina2.net
 Version: 1.0
 Date : 21/06/2017
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.3.0'
WHERE prop_key = 'fina2.database.schemaVersion';

DECLARE
  MAX_SS_ID NUMBER;
  MAX_SP_ID NUMBER;

BEGIN
  SELECT max(id) + 1
  INTO MAX_SS_ID
  FROM SYS_STRINGS;
  SELECT max(id) + 1
  INTO MAX_SP_ID
  FROM SYS_PERMISSIONS;

  INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, 1, 'net.fina.communicator.review');
  INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 1, 1, 'net.fina.communicator.amend');
  INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 2, 1, 'net.fina.communicator.delete');

  INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.communicator.review');
  INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
  VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.communicator.amend');
  INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
  VALUES (MAX_SP_ID + 2, MAX_SS_ID + 2, 'net.fina.communicator.delete');

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

CREATE TABLE IN_COMMUNICATOR_NOTIFICATIONS
(
  ID           NUMBER(10, 0)  NOT NULL,
  TITLE        VARCHAR2(1000) NOT NULL,
  CONTENT      VARCHAR2(4000) NOT NULL,
  PUBLISHDATE  TIMESTAMP,
  CREATIONDATE TIMESTAMP,
  STATUS       NUMBER(1, 0),
  USERID       NUMBER(10, 0)
);

CREATE TABLE SYS_NOTIFICATION_USERS
(
  NOTIFICATION_ID NUMBER(10, 0) NOT NULL,
  USER_ID         NUMBER(10, 0) NOT NULL,
  STATUS          NUMBER(1, 0)
);

CREATE SEQUENCE com_notification_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;



CREATE TABLE IN_COMMUNICATOR_MESSAGES
(
  ID           NUMBER(10, 0)  NOT NULL,
  TITLE        VARCHAR2(1000) NOT NULL,
  CONTENT      VARCHAR2(4000) NOT NULL,
  SENDDATE     TIMESTAMP,
  CREATIONDATE TIMESTAMP,
  STATUS       NUMBER(1, 0),
  USERID       NUMBER(10, 0),
  REPLYTOID    NUMBER(10, 0) DEFAULT 0,
  ACCEPTORID    NUMBER(10, 0)
);

CREATE TABLE IN_MESSAGE_USERS
(
  MESSAGE_ID NUMBER(10, 0) NOT NULL,
  USER_ID    NUMBER(10, 0) NOT NULL,
  STATUS     NUMBER(1, 0)
);

CREATE SEQUENCE com_message_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

CREATE TABLE IN_COMMUNICATOR_ATTACHEMENTS
(
  ID         NUMBER(10, 0)  NOT NULL,
  MESSAGE_ID NUMBER(10, 0)  NOT NULL,
  FILENAME   VARCHAR2(4000) NOT NULL,
  CONTENT    BLOB
);

ALTER TABLE IN_COMMUNICATOR_ATTACHEMENTS
  ADD TYPE NUMBER(1, 0);

CREATE SEQUENCE com_attachement_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;


CREATE TABLE IN_LAW_DOC_CATEGORY
(
  ID        NUMBER(10, 0) NOT NULL,
  OPTLOCK   NUMBER(10, 0),
  NAMESTRID NUMBER(10, 0)
);

CREATE SEQUENCE law_doc_cat_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;


CREATE TABLE IN_LAW_DOCUMENT
(
  ID          NUMBER(10)            NOT NULL,
  OPTLOCK     NUMBER(10),
  FILENAME    NVARCHAR2(2000) NOT NULL,
  CONTENT     BLOB,
  NAMESTRID   NUMBER(10),
  PUBLISH     TIMESTAMP(3),
  USER_ID     NUMBER(10),
  FI_TYPE_ID  NUMBER(10),
  CATEGORY_ID NUMBER(10)
);

CREATE SEQUENCE law_doc_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

CREATE TABLE IN_POSTBOX_FILES
(
  id          NUMBER(19) NOT NULL PRIMARY KEY,
  name        NVARCHAR2(255),
  upload_time TIMESTAMP(3),
  uploaded_by NUMBER(19),
  file_size   NUMBER(19) DEFAULT 0
);

CREATE SEQUENCE post_box_item_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

CREATE TABLE IN_POSTBOX_FILE_USERS
(
  file_id NUMBER(10) NOT NULL,
  user_id NUMBER(10) NOT NULL
);

ALTER TABLE IN_BANKS
  ADD IDENTIFICATION_CODE VARCHAR(64) NULL;


ALTER TABLE IN_BANKS
  ADD LEGAL_FORM VARCHAR(1024) NULL;

--File sign columns
ALTER TABLE IN_COMMUNICATOR_ATTACHEMENTS
  ADD sign INT NULL;


ALTER TABLE IN_COMMUNICATOR_MESSAGES
  ADD sign INT NULL;


ALTER TABLE IN_COMMUNICATOR_NOTIFICATIONS
  ADD sign INT NULL;


ALTER TABLE IN_LAW_DOCUMENT
  ADD sign INT NULL;

ALTER TABLE IN_LAW_DOCUMENT
  ADD notify INT NULL;

create table IN_OVERDUE_RETURN_NOTIFICATIONS
(
	SCHEDULE_ID number(10) not null,
	NOTIFICATION_ID number(10) not null
		constraint UK_lvjtoy4vyj0u2arw0re5bro9e
			unique,
	NOTIFICATION_TYPE number(10),
	primary key (SCHEDULE_ID, NOTIFICATION_ID)
);

ALTER TABLE IN_BANK_MANAGEMENT ADD IS_RESIDENT DECIMAL(1) DEFAULT 0 NOT NULL;

alter table IN_BANK_MANAGEMENT RENAME column IS_RESIDENT TO RESIDENT;

UPDATE IN_OVERDUE_RETURN_NOTIFICATIONS SET NOTIFICATION_TYPE = 0 WHERE NOTIFICATION_TYPE IS NULL;

ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS DROP CONSTRAINT PK__IN_OVERD__D18C4ECCEB76984D;

ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS MODIFY NOTIFICATION_TYPE INT NOT NULL;

ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS ADD CONSTRAINT PK__IN_OVERD__D18C4ECCEB76984D PRIMARY KEY (SCHEDULE_ID, NOTIFICATION_ID, NOTIFICATION_TYPE);

ALTER TABLE OUT_REPORTS_SCHEDULE ADD state  VARCHAR2(4000) NULL;







--remove old permissions
delete from SYS_PERMISSIONS where IDNAME in(

'net.fina.communicator.notifications.review',
'net.fina.communicator.notifications.amend',
'net.fina.communicator.notifications.delete',
'net.fina.communicator.notifications.accept',
'net.fina.communicator.messages.review',
'net.fina.communicator.messages.amend',
'net.fina.communicator.messages.delete',
'net.fina.communicator.messages.accept',
'net.fina.security.token.access',
'net.fina.legislativeDocument.review',
'net.fina.legislativeDocument.amend',
'net.fina.legislativeDocument.delete',
'net.fina.ems.inspection.review',
'net.fina.ems.inspection.amend',
'net.fina.ems.inspection.delete',
'net.fina.ems.inspectionType.review',
'net.fina.ems.inspectionType.amend',
'net.fina.ems.inspectionType.delete',
'net.fina.ems.sanctionType.review',
'net.fina.ems.sanctionType.amend',
'net.fina.ems.sanctionType.delete',
'net.fina.ems.inspection.columns.review',
'net.fina.ems.inspection.columns.amend',
'net.fina.ems.inspection.columns.delete',
'net.fina.postbox.review',
'net.fina.postbox.amend',
'net.fina.postbox.delete',
'net.fina.ems.fi.export',
'net.fina.ems.import.configuration.review',
'net.fina.ems.import.file',
'net.fina.ems.sanctionFineType.amend',
'net.fina.ems.sanctionFineType.review',
'net.fina.ems.sanctionFineType.delete'
);
COMMIT ;

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

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, LANG_ID, 'net.fina.communicator.notifications.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 1, LANG_ID, 'net.fina.communicator.notifications.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 2, LANG_ID, 'net.fina.communicator.notifications.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 3, LANG_ID, 'net.fina.communicator.notifications.accept');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 4, LANG_ID, 'net.fina.communicator.messages.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 5, LANG_ID, 'net.fina.communicator.messages.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 6, LANG_ID, 'net.fina.communicator.messages.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 7, LANG_ID, 'net.fina.communicator.messages.accept');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 8, LANG_ID, 'net.fina.security.token.access');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 9, LANG_ID, 'net.fina.legislativeDocument.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 10, LANG_ID, 'net.fina.legislativeDocument.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 11, LANG_ID, 'net.fina.legislativeDocument.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 12, LANG_ID, 'net.fina.ems.inspection.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 13, LANG_ID, 'net.fina.ems.inspection.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 14, LANG_ID, 'net.fina.ems.inspection.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 15, LANG_ID, 'net.fina.ems.inspectionType.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 16, LANG_ID, 'net.fina.ems.inspectionType.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 17, LANG_ID, 'net.fina.ems.inspectionType.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 18, LANG_ID, 'net.fina.ems.sanctionType.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 19, LANG_ID, 'net.fina.ems.sanctionType.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 20, LANG_ID, 'net.fina.ems.sanctionType.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 21, LANG_ID, 'net.fina.ems.inspection.columns.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 22, LANG_ID, 'net.fina.ems.inspection.columns.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 23, LANG_ID, 'net.fina.ems.inspection.columns.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 24, LANG_ID, 'net.fina.postbox.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 25, LANG_ID, 'net.fina.postbox.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 26, LANG_ID, 'net.fina.postbox.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 27, LANG_ID, 'net.fina.ems.fi.export');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 28, LANG_ID, 'net.fina.ems.import.configuration.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 29, LANG_ID, 'net.fina.ems.import.file');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 30, LANG_ID, 'net.fina.ems.sanctionFineType.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 31, LANG_ID, 'net.fina.ems.sanctionFineType.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 32, LANG_ID, 'net.fina.ems.sanctionFineType.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 33, LANG_ID, 'net.fina.ems.inspection.export');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 34, LANG_ID, 'net.fina.ems.import.configuration.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 35, LANG_ID, 'net.fina.ems.import.configuration.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 36, LANG_ID, 'net.fina.ems.import.file.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 37, LANG_ID, 'net.fina.ems.import.file.import');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 38, LANG_ID, 'net.fina.communicator.file.sign');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.communicator.notifications.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 1, MAX_SS_ID+1, 'net.fina.communicator.notifications.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 2, MAX_SS_ID+2, 'net.fina.communicator.notifications.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 3, MAX_SS_ID+3, 'net.fina.communicator.notifications.accept');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 4, MAX_SS_ID+4, 'net.fina.communicator.messages.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 5, MAX_SS_ID+5, 'net.fina.communicator.messages.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 6, MAX_SS_ID+6, 'net.fina.communicator.messages.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 7, MAX_SS_ID+7, 'net.fina.communicator.messages.accept');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 8, MAX_SS_ID+8, 'net.fina.security.token.access');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 9, MAX_SS_ID+9, 'net.fina.legislativeDocument.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 10, MAX_SS_ID+10, 'net.fina.legislativeDocument.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 11, MAX_SS_ID+11, 'net.fina.legislativeDocument.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 12, MAX_SS_ID+12, 'net.fina.ems.inspection.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 13, MAX_SS_ID+13, 'net.fina.ems.inspection.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 14, MAX_SS_ID+14, 'net.fina.ems.inspection.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 15, MAX_SS_ID+15, 'net.fina.ems.inspectionType.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 16, MAX_SS_ID+16, 'net.fina.ems.inspectionType.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 17, MAX_SS_ID+17, 'net.fina.ems.inspectionType.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 18, MAX_SS_ID+18, 'net.fina.ems.sanctionType.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 19, MAX_SS_ID+19, 'net.fina.ems.sanctionType.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 20, MAX_SS_ID+20, 'net.fina.ems.sanctionType.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 21, MAX_SS_ID+21, 'net.fina.ems.inspection.columns.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 22, MAX_SS_ID+22, 'net.fina.ems.inspection.columns.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 23, MAX_SS_ID+23, 'net.fina.ems.inspection.columns.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 24, MAX_SS_ID+24, 'net.fina.postbox.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 25, MAX_SS_ID+25, 'net.fina.postbox.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 26, MAX_SS_ID+26, 'net.fina.postbox.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 27, MAX_SS_ID+27, 'net.fina.ems.fi.export');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 28, MAX_SS_ID+28, 'net.fina.ems.import.configuration.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 29, MAX_SS_ID+29, 'net.fina.ems.import.file');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 30, MAX_SS_ID+30, 'net.fina.ems.sanctionFineType.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 31, MAX_SS_ID+31, 'net.fina.ems.sanctionFineType.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 32, MAX_SS_ID+32, 'net.fina.ems.sanctionFineType.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 33, MAX_SS_ID+33, 'net.fina.ems.inspection.export');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 34, MAX_SS_ID+34, 'net.fina.ems.import.configuration.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 35, MAX_SS_ID+35, 'net.fina.ems.import.configuration.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 36, MAX_SS_ID+36, 'net.fina.ems.import.file.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 37, MAX_SS_ID+37, 'net.fina.ems.import.file.import');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 38, MAX_SS_ID+38, 'net.fina.communicator.file.sign');

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

alter table IN_COMMUNICATOR_MESSAGES
add ACCEPTORID NUMBER;

alter table SYS_USERS MODIFY LOGIN varchar2(50);
alter table SYS_ROLES MODIFY CODE varchar2(50);

-- drop unique index on NOTIFICATION_ID, change 'UK_lvjtoy4vyj0u2arw0re5bro9e'
alter table IN_OVERDUE_RETURN_NOTIFICATIONS drop CONSTRAINT UK_lvjtoy4vyj0u2arw0re5bro9e;

ALTER TABLE IN_COMMUNICATOR_NOTIFICATIONS drop column CONTENT ;

ALTER TABLE IN_COMMUNICATOR_MESSAGES  drop column CONTENT ;

ALTER TABLE IN_COMMUNICATOR_NOTIFICATIONS add  CONTENT CLOB NOT NULL;

ALTER TABLE IN_COMMUNICATOR_MESSAGES  add  CONTENT CLOB NOT NULL;

-- EMS
ALTER TABLE EMS_SANCTION_FINE_TYPES ADD subParagraph nvarchar2(255) NULL;
ALTER TABLE EMS_SANCTIONS DROP COLUMN paragraph;
ALTER TABLE EMS_SANCTIONS DROP COLUMN decree;
ALTER TABLE EMS_INSPECTIONS DROP COLUMN remark;
ALTER TABLE EMS_INSPECTIONS DROP COLUMN inspectionStartTime;
ALTER TABLE EMS_INSPECTIONS DROP COLUMN inspectionEndTime;
UPDATE EMS_SANCTION_STATUSES SET type = type - 1 WHERE type > 0;

begin
   execute immediate 'drop table EMS_SANCTION_FINE_SUBSTATUS';
exception
   when others then null;
end;
/;

create table EMS_SANCTION_FINE_SUBSTATUS
(
  id number(19) not null
    primary key,
  status number(10),
  hasInput number(1)
);

begin
   execute immediate 'drop table EMS_SANCTION_FINE_SUBSTATUS_I18N';
exception
   when others then null;
end;
/;

create table EMS_SANCTION_FINE_SUBSTATUS_I18N
(
  locale varchar2(255) not null,
  id number(19) not null
    constraint FK_SANCTION_FINE_SUBSTATUSES
    references EMS_SANCTION_FINE_SUBSTATUS,
  name nvarchar2(255),
  input number(1) not null,
  primary key (locale, id, input)
);


ALTER TABLE EMS_SANCTION_STATUSES
  ADD
  (fineStatus int,
fineSubstatusAmount float,
fineSubstatus_id 	NUMBER(19)
constraint FK_SANCTION_FINE_SUBSTATUS
references EMS_SANCTION_FINE_SUBSTATUS);

ALTER TABLE EMS_SANCTIONS
  ADD
  (deliveryDate TIMESTAMP(3),
dueDate int);

begin
   execute immediate 'drop table EMS_SANCTION_SUBMISSION_NOTIFICATIONS';
exception
   when others then null;
end;
/

create table EMS_SANCTION_SUBMISSION_NOTIFICATIONS
(
  dueDate number(10),
  deliveryDate TIMESTAMP(3),
  notificationId number(19) not null,
  sanction_id number(19)
    constraint FK_SANCTION_SUBMISSION_NOTIFICATIONS
    references EMS_SANCTIONS,
  primary key (notificationId)
);

-- PAID
insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (2, 2, 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 2, N'ნებაყოფლობით', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 2, 'Voluntarily', 0);

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (3, 2, 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 3, N'აღსრულების წესით', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 3, 'By execution', 0);

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (4, 2, 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 4, N'სრულად გადახდილი სასამართლოს გადაწყვეტილების შედეგად', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 4, 'Fully paid as a result of court decision', 0);

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (5, 2, 1);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 5, N'ნაწილობრივ გადახდილი სასამართლოს გადაწყვეტილების შედეგად', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 5, 'Partially paid by the court decision', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 5, N'რათანხა შეეფარდა სასამართლოს გადაწყვეტილებით', 1);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 5, 'How much money was imposed by court decision', 1);

-- UNPAID
insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (6, 3, 1);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 6, N'ნაწილობრივ გადახდილი', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 6, 'Partially paid', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 6, N'რათანხა დარჩა გადასახდელი', 1);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 6, 'Money left to pay', 1);

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (7, 3, 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 7, N'აღსრულებისთვის გადაცემული', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 7, 'Transferred to execution', 0);

-- ANNULLED
insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (8, 4, 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 8, N'განთავისუფლებულია გადახდისგან სასამართლოს მიერ', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 8, 'Exempted from payment by court', 0);

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (9, 4, 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 9, N'განთავისუფლებულია გადახდისგან ეროვნული ბანკის მიერ', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 9, 'Dismissed from the payment by the National Bank', 0);

-- APPEALED
insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (10, 1, 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 10, N'რაიონული (საქალაქო) სასამართლო', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 10, 'District (City) Court', 0);

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (11, 1, 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 11, N'სააპელაციო სასამართლო', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 11, 'Court of Appeal', 0);

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (12, 1, 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 12, N'საქართველოს უზენაესი სასამართლო', 0);
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 12, 'Supreme Court of Georgia', 0);

commit;