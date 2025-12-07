/*
 Database: Oracle

 Author: Otar Iantbelidze
 E: oto@fina2.net
 Version: 1.0
 Date : 20/07/2018
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.3.1'
WHERE prop_key = 'fina2.database.schemaVersion';


alter table IN_COMMUNICATOR_MESSAGES ADD REJECTION_NOTE varchar2(1000);

alter table IN_COMMUNICATOR_MESSAGES ADD LAST_CONVERSATION_MESSAGE_DATE date default null;

alter table EMS_SANCTION_FINE_TYPES ADD FI_TYPE_CODE  varchar2(255) not null;

CREATE TABLE IN_COMMUNICATOR_USERS_MESSAGE_STATUS
(
  ID           NUMBER(10, 0)  NOT NULL,
  STATUS       NUMBER(1, 0),
  USER_ID       NUMBER(10, 0),
  message_id       NUMBER(10, 0),
  message_user_id       NUMBER(10, 0)
);

CREATE SEQUENCE com_users_messages_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;



ALTER TABLE IN_COMMUNICATOR_MESSAGES ADD REPLYTOUSERID int DEFAULT 0 NULL;
update IN_COMMUNICATOR_MESSAGES SET REPLYTOUSERID=0;

ALTER TABLE IN_MESSAGE_USERS ADD LAST_MESSAGE_USER int DEFAULT 0 NULL;
update IN_MESSAGE_USERS set LAST_MESSAGE_USER=(select max 1 id from SYS_USERS);

update IN_COMMUNICATOR_USERS_MESSAGE_STATUS set status=3;

commit;

ALTER TABLE EMS_INSPECTIONS ADD recommendationMailSent NUMBER(3) NOT NULL;

CREATE TABLE EMS_SANCTION_FINE_TYPES_PRICES(
	SanctionFineType_id number(19) NOT NULL,
	price binary_double NULL
);

ALTER TABLE EMS_SANCTION_FINES ADD finePrice float NOT NULL;

create table EMS_INSPECTION_TYPE_DESCRIPTION_I18N
(
  locale varchar2(255) not null,
  id     number(19)       not null,
  name   varchar2(255),
  primary key (locale, id)
);

alter table EMS_INSPECTION_TYPES drop column type;

CREATE UNIQUE INDEX EMS_INSPECTION_TYPE_I18N_name_uindex ON EMS_INSPECTION_TYPE_I18N (name);

delete from EMS_IMPORT_FILE;
delete from EMS_IMPORT_FILE_CONFIG_ATTR;
delete from EMS_IMPORT_FILE_CONFIG;

alter table EMS_IMPORT_FILE_CONFIG rename to EMS_FILE_CONFIG;

 CREATE SEQUENCE ems_file_con_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

alter table EMS_IMPORT_FILE_CONFIG_ATTR RENAME column importFileConfiguration_id TO fileConfiguration_id;

alter table EMS_IMPORT_FILE_CONFIG_ATTR rename to EMS_FILE_CONFIG_ATTR;

ALTER TABLE EMS_FILE_CONFIG ADD exportFileTemplateName varchar2(255) NOT NULL;
ALTER TABLE EMS_FILE_CONFIG ADD exportFileTemplateContent BLOB NOT NULL;
ALTER TABLE EMS_FILE_CONFIG_ATTR ADD sanctionFinePrice float;
ALTER TABLE EMS_FILE_CONFIG_ATTR ADD cellObjectFieldFormat varchar(255);
ALTER TABLE EMS_FILE_CONFIG_ATTR ADD SanctionFineType_id bigint;
ALTER TABLE EMS_SANCTIONS ADD paymentDate datetime;
ALTER TABLE EMS_SANCTION_FINE_TYPES DROP COLUMN price;

insert into SYS_PROPERTIES (PROP_KEY,VALUE) values ('net.fina.ems.exportContactPersonCode','CP');
insert into SYS_PROPERTIES (PROP_KEY,VALUE) values ('net.fina.ems.exportBeneficialOwnerCode','BO');
insert into SYS_PROPERTIES (PROP_KEY,VALUE) values ('net.fina.ems.exportLicenseCode','GL');

commit;

CREATE TABLE EMS_INSPECTION_RECOMMENDATIONS(
	id number(19) NOT NULL,
	creationDate date NULL,
	deliveryDate date NULL,
	executionDate date NULL,
	reason nvarchar2(255) NULL,
	status number(10) NULL,
	userId varchar2(255) NULL,
	inspection_id number(19) NULL
);

CREATE SEQUENCE inspection_recomm_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

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

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, LANG_ID, 'net.fina.ems.recommendation.review');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.ems.recommendation.review');

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

delete from  IN_COMMUNICATOR_USERS_MESSAGE_STATUS;
commit;



CREATE SEQUENCE sms_messages_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

CREATE TABLE OUT_SMS_MESSAGE(
  id number(19) NOT NULL,
  recipient nvarchar2(100),
  address nvarchar2(100),
  title nvarchar2(100),
  content nvarchar2(MAX),
  creationDate date,
  isProcessing NUMBER(1)
);

CREATE SEQUENCE sms_messages_status_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

CREATE TABLE OUT_SMS_MESSAGE_STATUS (
  id number(19) NOT NULL,
  sms_id number(19),
  providedMessageId nvarchar2(100),
  providerName nvarchar2(100),
  sendDate date,
  status INTEGER,
  errorCode INTEGER,
  deliveryDate date
)

CREATE SEQUENCE f_registry_fi_act_com_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

CREATE TABLE FIRST_FI_REGISTRY_ACTION_COMMUNICATION (
  id number(19) NOT NULL,
  communication_type INTEGER,
  communication_id number(19),
  fi_registry_action_id number(19)
)
