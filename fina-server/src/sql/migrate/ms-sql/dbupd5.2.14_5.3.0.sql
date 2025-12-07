/*
 Database: MS SQL Server

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

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id, @lang_id, 'net.fina.communicator.notifications.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 1, @lang_id, 'net.fina.communicator.notifications.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 2, @lang_id, 'net.fina.communicator.notifications.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 3, @lang_id, 'net.fina.communicator.notifications.accept');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 4, @lang_id, 'net.fina.communicator.messages.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 5, @lang_id, 'net.fina.communicator.messages.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 6, @lang_id, 'net.fina.communicator.messages.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 7, @lang_id, 'net.fina.communicator.messages.accept');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 8, @lang_id, 'net.fina.security.token.access');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 9, @lang_id, 'net.fina.legislativeDocument.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 10, @lang_id, 'net.fina.legislativeDocument.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 11, @lang_id, 'net.fina.legislativeDocument.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 12, @lang_id, 'net.fina.ems.inspection.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 13, @lang_id, 'net.fina.ems.inspection.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 14, @lang_id, 'net.fina.ems.inspection.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 15, @lang_id, 'net.fina.ems.inspectionType.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 16, @lang_id, 'net.fina.ems.inspectionType.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 17, @lang_id, 'net.fina.ems.inspectionType.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 18, @lang_id, 'net.fina.ems.sanctionType.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 19, @lang_id, 'net.fina.ems.sanctionType.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 20, @lang_id, 'net.fina.ems.sanctionType.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 21, @lang_id, 'net.fina.ems.inspection.columns.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 22, @lang_id, 'net.fina.ems.inspection.columns.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 23, @lang_id, 'net.fina.ems.inspection.columns.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 24, @lang_id, 'net.fina.postbox.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 25, @lang_id, 'net.fina.postbox.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 26, @lang_id, 'net.fina.postbox.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 27, @lang_id, 'net.fina.ems.fi.export');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 28, @lang_id, 'net.fina.ems.import.configuration.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 29, @lang_id, 'net.fina.ems.import.file');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 30, @lang_id, 'net.fina.ems.sanctionFineType.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 31, @lang_id, 'net.fina.ems.sanctionFineType.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 32, @lang_id, 'net.fina.ems.sanctionFineType.delete');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 33, @lang_id, 'net.fina.ems.inspection.export');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 34, @lang_id, 'net.fina.ems.import.configuration.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 35, @lang_id, 'net.fina.ems.import.configuration.delete');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 36, @lang_id, 'net.fina.ems.import.file.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 37, @lang_id, 'net.fina.ems.import.file.import');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 38, @lang_id, 'net.fina.communicator.file.sign');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id, @ss_id, 'net.fina.communicator.notifications.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.communicator.notifications.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 2, @ss_id + 2, 'net.fina.communicator.notifications.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 3, @ss_id + 3, 'net.fina.communicator.notifications.accept');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 4, @ss_id + 4, 'net.fina.communicator.messages.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 5, @ss_id + 5, 'net.fina.communicator.messages.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 6, @ss_id + 6, 'net.fina.communicator.messages.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 7, @ss_id + 7, 'net.fina.communicator.messages.accept');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 8, @ss_id + 8, 'net.fina.security.token.access');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 9, @ss_id + 9, 'net.fina.legislativeDocument.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 10, @ss_id + 10, 'net.fina.legislativeDocument.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 11, @ss_id + 11, 'net.fina.legislativeDocument.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 12, @ss_id + 12, 'net.fina.ems.inspection.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 13, @ss_id + 13, 'net.fina.ems.inspection.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 14, @ss_id + 14, 'net.fina.ems.inspection.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 15, @ss_id + 15, 'net.fina.ems.inspectionType.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 16, @ss_id + 16, 'net.fina.ems.inspectionType.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 17, @ss_id + 17, 'net.fina.ems.inspectionType.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 18, @ss_id + 18, 'net.fina.ems.sanctionType.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 19, @ss_id + 19, 'net.fina.ems.sanctionType.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 20, @ss_id + 20, 'net.fina.ems.sanctionType.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 21, @ss_id + 21, 'net.fina.ems.inspection.columns.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 22, @ss_id + 22, 'net.fina.ems.inspection.columns.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 23, @ss_id + 23, 'net.fina.ems.inspection.columns.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 24, @ss_id + 24, 'net.fina.postbox.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 25, @ss_id + 25, 'net.fina.postbox.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 26, @ss_id + 26, 'net.fina.postbox.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 27, @ss_id + 27, 'net.fina.ems.fi.export');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 28, @ss_id + 28, 'net.fina.ems.import.configuration.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 29, @ss_id + 29, 'net.fina.ems.import.file');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 30, @ss_id + 30, 'net.fina.ems.sanctionFineType.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 31, @ss_id + 31, 'net.fina.ems.sanctionFineType.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 32, @ss_id + 32, 'net.fina.ems.sanctionFineType.delete');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 33, @ss_id + 33, 'net.fina.ems.inspection.export');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 34, @ss_id + 34, 'net.fina.ems.import.configuration.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 35, @ss_id + 35, 'net.fina.ems.import.configuration.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 36, @ss_id + 36, 'net.fina.ems.import.file.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 37, @ss_id + 37, 'net.fina.ems.import.file.import');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id + 38, @ss_id + 38, 'net.fina.communicator.file.sign');

GO

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

/*
   Create tables
 */


CREATE TABLE IN_COMMUNICATOR_NOTIFICATIONS
(
  ID           INT            NOT NULL,
  TITLE        NVARCHAR(1000) NOT NULL,
  CONTENT      NVARCHAR(4000) NOT NULL,
  PUBLISHDATE  DATETIME,
  CREATIONDATE DATETIME,
  STATUS       INT            NOT NULL,
  USERID       INT
);

CREATE TABLE SYS_NOTIFICATION_USERS
(
  NOTIFICATION_ID BIGINT NOT NULL,
  USER_ID         BIGINT NOT NULL,
  STATUS          INT
);

CREATE SEQUENCE com_notification_sequence
  AS [BIGINT]
  START WITH 1
  INCREMENT BY 1
  CACHE
GO


CREATE TABLE IN_COMMUNICATOR_MESSAGES
(
  ID           INT            NOT NULL,
  TITLE        NVARCHAR(1000) NOT NULL,
  CONTENT      NVARCHAR(4000) NOT NULL,
  SENDDATE     DATETIME,
  CREATIONDATE DATETIME,
  STATUS       INT            NOT NULL,
  USERID       INT,
  REPLYTOID    INT DEFAULT 0,
  ACCEPTORID   INT
);

CREATE TABLE IN_MESSAGE_USERS
(
  MESSAGE_ID BIGINT NOT NULL,
  USER_ID    BIGINT NOT NULL,
  STATUS     INT
);

CREATE SEQUENCE com_message_sequence
  AS [BIGINT]
  START WITH 1
  INCREMENT BY 1
  CACHE
GO

CREATE TABLE IN_COMMUNICATOR_ATTACHEMENTS
(
  ID         INT            NOT NULL,
  MESSAGE_ID BIGINT         NOT NULL,
  FILENAME   NVARCHAR(4000) NOT NULL,
  CONTENT    IMAGE,
  TYPE       INT            NULL
);

CREATE SEQUENCE com_attachement_sequence
  AS [BIGINT]
  START WITH 1
  INCREMENT BY 1
  CACHE
GO


CREATE TABLE IN_LAW_DOC_CATEGORY
(
  ID        INT NOT NULL,
  OPTLOCK   INT,
  NAMESTRID DECIMAL(10)
);

CREATE SEQUENCE law_doc_cat_seq
  AS [BIGINT]
  START WITH 1
  INCREMENT BY 1
  CACHE
GO

CREATE TABLE IN_LAW_DOCUMENT
(
  ID          INT            NOT NULL,
  OPTLOCK     INT,
  FILENAME    NVARCHAR(4000) NOT NULL,
  CONTENT     IMAGE,
  NAMESTRID   DECIMAL(10),
  PUBLISH     DATETIME,
  USER_ID     DECIMAL(10),
  FI_TYPE_ID  DECIMAL(10),
  CATEGORY_ID INT
);

CREATE SEQUENCE law_doc_sequence
  AS [BIGINT]
  START WITH 1
  INCREMENT BY 1
  CACHE
GO

CREATE TABLE IN_POSTBOX_FILES
(
  id          BIGINT NOT NULL PRIMARY KEY,
  name        NVARCHAR(255),
  upload_time DATETIME2,
  uploaded_by BIGINT,
  file_size   BIGINT DEFAULT 0,
)
GO

CREATE SEQUENCE post_box_item_sequence
  AS [BIGINT]
  START WITH 1
  INCREMENT BY 1
  CACHE
GO

CREATE TABLE IN_POSTBOX_FILE_USERS
(
  file_id INT NOT NULL,
  user_id INT NOT NULL
)
GO

ALTER TABLE IN_BANKS
  ADD IDENTIFICATION_CODE VARCHAR(64) NULL
GO

ALTER TABLE IN_BANKS
  ADD LEGAL_FORM VARCHAR(1024) NULL
GO




-- for Final version

--File sign columns
ALTER TABLE IN_COMMUNICATOR_ATTACHEMENTS
  ADD sign INT NULL
GO

ALTER TABLE IN_COMMUNICATOR_MESSAGES
  ADD sign INT NULL
GO

ALTER TABLE IN_COMMUNICATOR_NOTIFICATIONS
  ADD sign INT NULL
GO

ALTER TABLE IN_LAW_DOCUMENT
  ADD sign INT NULL
GO

ALTER TABLE IN_LAW_DOCUMENT
  ADD notify INT NULL
GO

create table IN_OVERDUE_RETURN_NOTIFICATIONS
(
	SCHEDULE_ID decimal(10) not null,
	NOTIFICATION_ID int not null
		constraint UK_lvjtoy4vyj0u2arw0re5bro9e
			unique,
	NOTIFICATION_TYPE int,
	primary key (SCHEDULE_ID, NOTIFICATION_ID)
)
go

ALTER TABLE IN_BANK_MANAGEMENT ADD IS_RESIDENT DECIMAL(1) DEFAULT 0 NOT NULL;
GO
EXEC sp_rename 'IN_BANK_MANAGEMENT.IS_RESIDENT', RESIDENT, 'COLUMN';
GO

UPDATE IN_OVERDUE_RETURN_NOTIFICATIONS SET NOTIFICATION_TYPE = 0 WHERE NOTIFICATION_TYPE IS NULL;
GO
ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS DROP CONSTRAINT PK__IN_OVERD__D18C4ECCEB76984D;
GO
ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS ALTER COLUMN NOTIFICATION_TYPE INT NOT NULL;
GO
ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS ADD CONSTRAINT PK__IN_OVERD__D18C4ECCEB76984D PRIMARY KEY (SCHEDULE_ID, NOTIFICATION_ID, NOTIFICATION_TYPE);
GO

alter table SYS_USERS ALTER COLUMN LOGIN nvarchar(50);
go
alter table SYS_ROLES ALTER COLUMN CODE nvarchar(50);
go

ALTER TABLE OUT_REPORTS_SCHEDULE ADD state varchar NULL;

go

-- drop unique index on NOTIFICATION_ID, change 'UK_lvjtoy4vyj0u2arw0re5bro9e'
alter table IN_OVERDUE_RETURN_NOTIFICATIONS drop CONSTRAINT UK_lvjtoy4vyj0u2arw0re5bro9e
go

ALTER TABLE IN_COMMUNICATOR_NOTIFICATIONS ALTER COLUMN CONTENT NVARCHAR(MAX) NOT NULL
go

ALTER TABLE IN_COMMUNICATOR_MESSAGES ALTER COLUMN CONTENT NVARCHAR(MAX) NOT NULL
go


-- EMS

ALTER TABLE EMS_SANCTION_FINE_TYPES ADD subParagraph NVARCHAR(255) NULL;
GO

ALTER TABLE EMS_SANCTIONS DROP COLUMN paragraph;
GO
ALTER TABLE EMS_SANCTIONS DROP COLUMN decree;
GO

ALTER TABLE EMS_INSPECTIONS DROP COLUMN remark;
GO

ALTER TABLE EMS_INSPECTIONS DROP COLUMN inspectionStartTime;
GO
ALTER TABLE EMS_INSPECTIONS DROP COLUMN inspectionEndTime;
GO

UPDATE EMS_SANCTION_STATUSES SET type = type - 1 WHERE type > 0;
GO


drop table if exists EMS_SANCTION_FINE_SUBSTATUS
go

create table EMS_SANCTION_FINE_SUBSTATUS
(
  id bigint not null
    primary key,
  status int,
  hasInput bit
)
  go

drop table if exists EMS_SANCTION_FINE_SUBSTATUS_I18N
go

create table EMS_SANCTION_FINE_SUBSTATUS_I18N
(
  locale varchar(255) not null,
  id bigint not null
    constraint FK_SANCTION_FINE_SUBSTATUSES
    references EMS_SANCTION_FINE_SUBSTATUS,
  name nvarchar(255),
  input bit not null,
  primary key (locale, id, input)
)
  go

ALTER TABLE EMS_SANCTION_STATUSES
  ADD
  fineStatus int,
fineSubstatusAmount float,
fineSubstatus_id bigint
constraint FK_SANCTION_FINE_SUBSTATUS
references EMS_SANCTION_FINE_SUBSTATUS
go


ALTER TABLE EMS_SANCTIONS
  ADD
  deliveryDate datetime2,
dueDate int
go

drop table if EXISTS EMS_SANCTION_SUBMISSION_NOTIFICATIONS

create table EMS_SANCTION_SUBMISSION_NOTIFICATIONS
(
  dueDate int,
  deliveryDate datetime2,
  notificationId bigint not null,
  sanction_id bigint
    constraint FK_SANCTION_SUBMISSION_NOTIFICATIONS
    references EMS_SANCTIONS,
  primary key (notificationId)
)
  go


-- PAID
insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (2, 2, 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 2, N'ნებაყოფლობით', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 2, 'Voluntarily', 0)

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (3, 2, 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 3, N'აღსრულების წესით', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 3, 'By execution', 0)

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (4, 2, 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 4, N'სრულად გადახდილი სასამართლოს გადაწყვეტილების შედეგად', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 4, 'Fully paid as a result of court decision', 0)

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (5, 2, 1)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 5, N'ნაწილობრივ გადახდილი სასამართლოს გადაწყვეტილების შედეგად', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 5, 'Partially paid by the court decision', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 5, N'რათანხა შეეფარდა სასამართლოს გადაწყვეტილებით', 1)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 5, 'How much money was imposed by court decision', 1)

-- UNPAID
insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (6, 3, 1)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 6, N'ნაწილობრივ გადახდილი', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 6, 'Partially paid', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 6, N'რათანხა დარჩა გადასახდელი', 1)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 6, 'Money left to pay', 1)

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (7, 3, 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 7, N'აღსრულებისთვის გადაცემული', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 7, 'Transferred to execution', 0)

-- ANNULLED
insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (8, 4, 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 8, N'განთავისუფლებულია გადახდისგან სასამართლოს მიერ', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 8, 'Exempted from payment by court', 0)

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (9, 4, 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 9, N'განთავისუფლებულია გადახდისგან ეროვნული ბანკის მიერ', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 9, 'Dismissed from the payment by the National Bank', 0)

-- APPEALED
insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (10, 1, 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 10, N'რაიონული (საქალაქო) სასამართლო', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 10, 'District (City) Court', 0)

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (11, 1, 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 11, N'სააპელაციო სასამართლო', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 11, 'Court of Appeal', 0)

insert into EMS_SANCTION_FINE_SUBSTATUS (id, status, hasInput) values (12, 1, 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('ka_GE', 12, N'საქართველოს უზენაესი სასამართლო', 0)
insert into EMS_SANCTION_FINE_SUBSTATUS_I18N (locale, id, name, input) values ('en_US', 12, 'Supreme Court of Georgia', 0)

