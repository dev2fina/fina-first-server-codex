/*
 Database: Oracle

 Author: Otar Iantbelidze
 E: oto@fina2.net
 Version: 1.0
 Date : 26/08/2019
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '6.0.2'
WHERE prop_key = 'fina2.database.schemaVersion';


declare rs int;
BEGIN
set rs=(select max(id)+1 from out_mail_message_reply) ;
exec('CREATE SEQUENCE message_replay_sequence  START WITH ' + rs +'   INCREMENT BY 1;');
END;

alter table SYS_USERS add DELETED bit default 0;
update SYS_USERS set DELETED=0;


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

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, LANG_ID, 'net.fina.faq.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 1, LANG_ID, 'net.fina.faq.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 2, LANG_ID, 'net.fina.first.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 3, LANG_ID, 'net.fina.dcs.dashboard.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 4, LANG_ID, 'net.fina.first.blacklist.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 5, LANG_ID, 'net.fina.first.blacklist.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 6, LANG_ID, 'net.fina.first.blacklist.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 7, LANG_ID, 'net.fina.dcs.fiProfile.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 8, LANG_ID, 'net.fina.first.organization.individual.registry.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 9, LANG_ID, 'net.fina.first.organization.individual.registry.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 10, LANG_ID, 'net.fina.first.organization.individual.registry.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 11, LANG_ID, 'net.fina.first.fi.document.request.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 12, LANG_ID, 'net.fina.first.fi.document.request.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 13, LANG_ID, 'net.fina.first.fi.document.request.delete');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.faq.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.faq.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 2, MAX_SS_ID + 2, 'net.fina.first.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 3, MAX_SS_ID + 3, 'net.fina.dcs.dashboard.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 4, MAX_SS_ID + 4, 'net.fina.first.blacklist.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 5, MAX_SS_ID + 5, 'net.fina.first.blacklist.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 6, MAX_SS_ID + 6, 'net.fina.first.blacklist.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 7, MAX_SS_ID + 7, 'net.fina.dcs.fiProfile.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 8, MAX_SS_ID + 8, 'net.fina.first.organization.individual.registry.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 9, MAX_SS_ID + 9, 'net.fina.first.organization.individual.registry.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 10, MAX_SS_ID + 10, 'net.fina.first.organization.individual.registry.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 11, MAX_SS_ID + 11, 'net.fina.first.fi.document.request.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 12, MAX_SS_ID + 12, 'net.fina.first.fi.document.request.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 13, MAX_SS_ID + 13, 'net.fina.first.fi.document.request.delete');

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

-- FAQ
CREATE SEQUENCE faq_items_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;
CREATE SEQUENCE faq_categories_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;

create table IN_FAQ_ITEMS
(
    ID number(19) not null,
    QUESTIONSTRID number(19),
    ANSWERSTRID number(19),
    CATEGORY_ID number(19),
    PUBLISH date,
    USER_ID number(19)
);

create table IN_FAQ_CATEGORIES
(
    ID number(19) not null,
    PARENTID number(19),
    NAMESTRID number(19),
    LEAF NUMBER(1)
);

alter table OUT_REPORTS_SCHEDULE
    add FILESTORAGELOCATION varchar(200);

alter table OUT_REPORTS_SCHEDULE
    add REPOSITORYNODEID varchar(200);

alter table OUT_REPORTS_SCHEDULE
    add REPOSITORYFOLDERNAME nvarchar(200);

alter table OUT_REPORTS_SCHEDULE
    add NOTIFICATIONMAILS varchar(max);

create table EMS_VIOLATIONS
(
    ID number(19) not null,
    VIOLATION nvarchar2(500),
    VIOLATIONDATE timestamp,
    "COMMENT" nvarchar2(500),
    INSPECTIONID number(19)
)

create sequence violation_seq MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;


ALTER TABLE EMS_INSPECTIONS RENAME COLUMN reclamationMailNumber TO decreeNumber;

alter table EMS_SANCTIONS add responsiblePerson varchar(50);