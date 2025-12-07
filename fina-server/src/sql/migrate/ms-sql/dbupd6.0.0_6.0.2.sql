/*
 Database: MS SQL Server

 Author: Nikoloz Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 22/03/2019
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '6.0.2'
WHERE prop_key = 'fina2.database.schemaVersion';


declare @rs int
set @rs=(select max(id)+1 from out_mail_message_reply) ;
exec('CREATE SEQUENCE message_replay_sequence  START WITH ' + @rs +'   INCREMENT BY 1;');


alter table SYS_USERS add DELETED bit default 0;
update SYS_USERS set DELETED=0;


/*
Insert permissions
 */
DECLARE @ss_id INT
DECLARE @sp_id INT
DECLARE @lang_id INT

SET @ss_id = (SELECT max(id)
FROM SYS_STRINGS) + 1
SET @sp_id = (SELECT max(id)
FROM SYS_PERMISSIONS) + 1
SET @lang_id = (SELECT id
FROM sys_languages
WHERE code LIKE '%en%')

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+1, @lang_id, 'net.fina.faq.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+1, @ss_id+1, 'net.fina.faq.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+2, @lang_id, 'net.fina.faq.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+2, @ss_id+2, 'net.fina.faq.amend');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+3, @lang_id, 'net.fina.first.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+3, @ss_id+3, 'net.fina.first.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+4, @lang_id, 'net.fina.dcs.fiProfile.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+4, @ss_id+4, 'net.fina.dcs.fiProfile.review');

--FIRST
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+5, @lang_id, 'net.fina.tag.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+5, @ss_id+5, 'net.fina.tag.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+6, @lang_id, 'net.fina.first.registry.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+6, @ss_id+6, 'net.fina.first.registry.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+7, @lang_id, 'net.fina.first.config.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+7, @ss_id+7, 'net.fina.first.config.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+8, @lang_id, 'net.fina.first.search.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+8, @ss_id+8, 'net.fina.first.search.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+9, @lang_id, 'net.fina.first.document.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+9, @ss_id+9, 'net.fina.first.document.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+10, @lang_id, 'net.fina.first.task.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+10, @ss_id+10, 'net.fina.first.task.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+11, @lang_id, 'net.fina.first.attestation.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+11, @ss_id+11, 'net.fina.first.attestation.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+12, @lang_id, 'net.fina.first.blacklist.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+12, @ss_id+12, 'net.fina.first.blacklist.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+13, @lang_id, 'net.fina.first.blacklist.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+13, @ss_id+13, 'net.fina.first.blacklist.amend');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+14, @lang_id, 'net.fina.first.blacklist.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+14, @ss_id+14, 'net.fina.first.blacklist.delete');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+15, @lang_id, 'net.fina.dcs.dashboard.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+15, @ss_id+15, 'net.fina.dcs.dashboard.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+16, @lang_id, 'net.fina.first.organization.individual.registry.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+16, @ss_id+16, 'net.fina.first.organization.individual.registry.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+17, @lang_id, 'net.fina.first.organization.individual.registry.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+17, @ss_id+17, 'net.fina.first.organization.individual.registry.amend');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+18, @lang_id, 'net.fina.first.organization.individual.registry.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+18, @ss_id+18, 'net.fina.first.organization.individual.registry.delete');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+19, @lang_id, 'net.fina.first.fi.document.request.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+19, @ss_id+19, 'net.fina.first.fi.document.request.review');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+20, @lang_id, 'net.fina.first.fi.document.request.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+20, @ss_id+20, 'net.fina.first.fi.document.request.amend');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+21, @lang_id, 'net.fina.first.fi.document.request.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+21, @ss_id+21, 'net.fina.first.fi.document.request.delete');

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1
             FROM SYS_STRINGS)
WHERE PK_COLUMN_NAME = 'SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1
             FROM SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME = 'SYS_PERMISSION_MAXID';
GO

-- FAQ
CREATE SEQUENCE faq_items_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

CREATE SEQUENCE faq_categories_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

create table IN_FAQ_ITEMS
(
    ID int not null,
    QUESTIONSTRID int,
    ANSWERSTRID int,
    CATEGORY_ID int,
    PUBLISH datetime,
    USER_ID int
);

create table IN_FAQ_CATEGORIES
(
    ID int not null,
    PARENTID int,
    NAMESTRID int,
    LEAF decimal(1)
);

alter table IN_BANKS add DISABLE bit default 0;
update IN_BANKS set DISABLE=0;


alter table OUT_REPORTS_SCHEDULE
    add FILESTORAGELOCATION varchar(200);

alter table OUT_REPORTS_SCHEDULE
    add REPOSITORYNODEID varchar(200);

alter table OUT_REPORTS_SCHEDULE
    add REPOSITORYFOLDERNAME nvarchar(100);

alter table OUT_REPORTS_SCHEDULE
    add NOTIFICATIONMAILS varchar(max);


create table EMS_VIOLATIONS
(
    ID NUMERIC(19) not null,
    VIOLATION NVARCHAR(500),
    VIOLATIONDATE DATETIME2,
    COMMENT NVARCHAR(500),
    INSPECTIONID NUMERIC(19)
)

CREATE SEQUENCE violation_seq
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;


exec sp_rename 'EMS_INSPECTIONS.reclamationMailNumber', decreeNumber, 'COLUMN';

alter table EMS_SANCTIONS add responsiblePerson varchar(50);


