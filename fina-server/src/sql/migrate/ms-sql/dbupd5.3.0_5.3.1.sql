/*
 Database: MS SQL Server

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


ALTER TABLE IN_COMMUNICATOR_MESSAGES ADD REJECTION_NOTE nvarchar(1000) NULL
go;


ALTER TABLE IN_COMMUNICATOR_MESSAGES ADD LAST_CONVERSATION_MESSAGE_DATE datetime NULL
go;

create table IN_COMMUNICATOR_USERS_MESSAGE_STATUS
(
  id              bigint,
  status          int,
  USER_ID         int,
  message_id      int,
  message_user_id int
)
go;


CREATE SEQUENCE com_users_messages_sequence
  AS [BIGINT]
  START WITH 1
  INCREMENT BY 1
  CACHE
GO;


ALTER TABLE EMS_SANCTION_FINE_TYPES ADD FI_TYPE_CODE varchar(255) NOT NULL
go;



-- Start procedure

DECLARE @userId int,@messageId int,@messageUserId int,@idCOunter int;
set @idCOunter=1;
DECLARE MU_Cursor CURSOR FOR
  select USER_ID,MESSAGE_ID from IN_MESSAGE_USERS where MESSAGE_ID in(select id from IN_COMMUNICATOR_MESSAGES where REPLYTOID=0) and USER_ID in(select USERID from IN_COMMUNICATOR_MESSAGES where REPLYTOID=0);
open MU_Cursor;
FETCH NEXT FROM MU_Cursor into @messageUserId,@messageId;
WHILE @@FETCH_STATUS = 0
  BEGIN
    FETCH NEXT FROM MU_Cursor into @messageUserId,@messageId ;

    DECLARE user_status_cursos CURSOR FOR
      select u.ID from SYS_USERS u
        left join SYS_USER_PERMISSIONS sup
          ON u.ID=sup.USERID
      where sup.PERMISSIONID in(select id from SYS_PERMISSIONS where IDNAME='fina2.web.internal.user')
      union
      select u.ID from SYS_USERS u
        left join SYS_USERS_ROLES sur
          ON u.ID=sur.USERID
      where sur.ROLEID in (select ROLEID from SYS_USERS_ROLES where roleid in(select ROLEID from SYS_ROLE_PERMISSIONS where PERMISSIONID=(select id from SYS_PERMISSIONS where IDNAME='fina2.web.internal.user')));
    OPEN user_status_cursos;
    FETCH NEXT FROM user_status_cursos into @userId;
    WHILE @@FETCH_STATUS = 0
      BEGIN
        FETCH NEXT FROM user_status_cursos into @userId;
        SET @idCOunter = @idCOunter + 1
        insert into IN_COMMUNICATOR_USERS_MESSAGE_STATUS (id,status,USER_ID,message_id,message_user_id) values (0,@idCOunter,@userId,@messageId,@messageUserId);
      END;
    CLOSE user_status_cursos;
    DEALLOCATE user_status_cursos;
  END;
CLOSE MU_Cursor;
DEALLOCATE MU_Cursor;
GO
-- End procedure


ALTER TABLE IN_COMMUNICATOR_MESSAGES ADD REPLYTOUSERID int DEFAULT 0 NULL;
update IN_COMMUNICATOR_MESSAGES SET REPLYTOUSERID=0;

ALTER TABLE IN_MESSAGE_USERS ADD LAST_MESSAGE_USER int DEFAULT 0 NULL;
update IN_MESSAGE_USERS set LAST_MESSAGE_USER=(select top 1 id from SYS_USERS);

update IN_COMMUNICATOR_USERS_MESSAGE_STATUS set status=3;


-- Start procedure


BEGIN TRANSACTION ;
Declare @msgId int;
Declare @UID int;
Declare @ChildId int;
Declare @ChildTitle varchar;
Declare @textMessage nvarchar(4000);
Declare @Generation int;
Declare @REPLYTOID int;

DECLARE COM_MESS_CURSORS CURSOR FOR
  SELECT ID  From IN_COMMUNICATOR_MESSAGES where REPLYTOID=0;

OPEN COM_MESS_CURSORS
FETCH NEXT FROM COM_MESS_CURSORS INTO @msgId;
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT 'Processing MessgaId: ' + Cast(@msgId as Varchar);
    DECLARE USER_MESS_CURSOR CURSOR FOR
      SELECT USER_ID FROM IN_MESSAGE_USERS Where MESSAGE_ID=@msgId;
    OPEN USER_MESS_CURSOR;
    FETCH NEXT FROM USER_MESS_CURSOR INTO @UID;
    WHILE @@FETCH_STATUS = 0
      BEGIN
        PRINT 'Found UID: ' + Cast(@UID as Varchar);
        FETCH NEXT FROM USER_MESS_CURSOR INTO @UID;
        DECLARE REC_Curos CURSOR FOR
          WITH Hierarchy(ChildId,
              ChildTitle,
              content,
              Generation,
              REPLYTOID)
          AS (SELECT Id, TITLE, content, 0, REPLYTOID
              FROM IN_COMMUNICATOR_MESSAGES AS FirtGeneration
              WHERE REPLYTOID = @msgId
              UNION ALL
              SELECT NextGeneration.id, NextGeneration.title, NextGeneration.CONTENT, Parent.Generation + 1, Parent.ChildId
              FROM IN_COMMUNICATOR_MESSAGES AS NextGeneration
                     INNER JOIN Hierarchy AS Parent ON NextGeneration.REPLYTOID = Parent.ChildId)
          SELECT *
          FROM Hierarchy
          OPTION (MAXRECURSION 32767);
        OPEN REC_Curos;
        FETCH NEXT FROM REC_Curos into @ChildId,@ChildTitle,@textMessage,@Generation,@REPLYTOID;
        WHILE @@FETCH_STATUS = 0
          BEGIN
            print @textMessage + STR(@ChildId);
            UPDATE IN_COMMUNICATOR_MESSAGES set REPLYTOID=@msgId,REPLYTOUSERID=@UID where ID=@ChildId;
            FETCH NEXT FROM REC_Curos into @ChildId,@ChildTitle,@textMessage,@Generation,@REPLYTOID;
          END;
        CLOSE REC_Curos;
        DEALLOCATE REC_Curos;
      END;
    CLOSE USER_MESS_CURSOR;
    DEALLOCATE USER_MESS_CURSOR;
    FETCH NEXT FROM COM_MESS_CURSORS INTO @msgId;
  END;
PRINT 'DONE';
CLOSE COM_MESS_CURSORS;
DEALLOCATE COM_MESS_CURSORS;
COMMIT ;


ALTER TABLE EMS_INSPECTIONS ADD recommendationMailSent bit NOT NULL
go;

CREATE TABLE EMS_SANCTION_FINE_TYPES_PRICES(
	SanctionFineType_id bigint NOT NULL,
	price float NULL
)
GO;

ALTER TABLE EMS_SANCTION_FINES ADD finePrice float NOT NULL
go;


create table EMS_INSPECTION_TYPE_DESCRIPTION_I18N
(
  locale varchar(255) not null,
  id     bigint       not null,
  name   varchar(255),
  primary key (locale, id)
)
go;

alter table EMS_INSPECTION_TYPES
  drop column type
go;


CREATE UNIQUE INDEX EMS_INSPECTION_TYPE_I18N_name_uindex ON EMS_INSPECTION_TYPE_I18N (name);

delete from EMS_IMPORT_FILE;
delete from EMS_IMPORT_FILE_CONFIG_ATTR;
delete from EMS_IMPORT_FILE_CONFIG;

exec sp_rename 'EMS_IMPORT_FILE_CONFIG', 'EMS_FILE_CONFIG';

CREATE SEQUENCE ems_file_con_seq
  AS [BIGINT]
  START WITH 1
  INCREMENT BY 1
  CACHE;

exec sp_rename 'EMS_IMPORT_FILE_CONFIG_ATTR.importFileConfiguration_id', 'fileConfiguration_id', 'COLUMN';

exec sp_rename 'EMS_IMPORT_FILE_CONFIG_ATTR', 'EMS_FILE_CONFIG_ATTR';

ALTER TABLE EMS_FILE_CONFIG ADD exportFileTemplateName nvarchar(255) NOT NULL;
ALTER TABLE EMS_FILE_CONFIG ADD exportFileTemplateContent image NOT NULL;
ALTER TABLE EMS_FILE_CONFIG_ATTR ADD sanctionFinePrice float;
ALTER TABLE EMS_FILE_CONFIG_ATTR ADD cellObjectFieldFormat varchar(255);
ALTER TABLE EMS_FILE_CONFIG_ATTR ADD SanctionFineType_id bigint
ALTER TABLE EMS_SANCTIONS ADD paymentDate datetime;
ALTER TABLE EMS_SANCTION_FINE_TYPES DROP COLUMN price;

insert into SYS_PROPERTIES (PROP_KEY,VALUE) values ('net.fina.ems.exportContactPersonCode','CP');
insert into SYS_PROPERTIES (PROP_KEY,VALUE) values ('net.fina.ems.exportBeneficialOwnerCode','BO');
insert into SYS_PROPERTIES (PROP_KEY,VALUE) values ('net.fina.ems.exportLicenseCode','GL');

CREATE TABLE EMS_INSPECTION_RECOMMENDATIONS(
	id bigint NOT NULL,
	creationDate datetime2(7) NULL,
	deliveryDate datetime2(7) NULL,
	executionDate datetime2(7) NULL,
	reason nvarchar(255) NULL,
	status int NULL,
	userId varchar(255) NULL,
	inspection_id bigint NULL,
);

CREATE SEQUENCE inspection_recomm_seq
  AS [BIGINT]
  START WITH 1
  INCREMENT BY 1
  CACHE
GO;

ALTER TABLE EMS_SANCTION_FINE_SUBSTATUS_I18N ALTER COLUMN name NVARCHAR(255)

ALTER TABLE EMS_INSPECTION_TYPE_DESCRIPTION_I18N ALTER COLUMN name NVARCHAR(500)

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

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id, @lang_id, 'net.fina.ems.recommendation.review');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName)
VALUES (@sp_id, @ss_id, 'net.fina.ems.recommendation.review');
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


delete from  IN_COMMUNICATOR_USERS_MESSAGE_STATUS;



drop SEQUENCE IF EXISTS sms_messages_sequence;
CREATE SEQUENCE sms_messages_sequence
  AS [BIGINT]
START WITH 1
INCREMENT BY 1
CACHE
GO

DROP TABLE IF EXISTS OUT_SMS_MESSAGE;
CREATE TABLE OUT_SMS_MESSAGE (
  id BIGINT PRIMARY KEY,
  recipient NVARCHAR(100),
  address NVARCHAR(100),
  title NVARCHAR(100),
  content NVARCHAR(MAX),
  creationDate DATETIME,
  isProcessing BIT
)
  GO

drop SEQUENCE IF EXISTS sms_messages_status_sequence;
CREATE SEQUENCE sms_messages_status_sequence
  AS [BIGINT]
START WITH 1
INCREMENT BY 1
CACHE
GO

CREATE TABLE OUT_SMS_MESSAGE_STATUS (
  id BIGINT PRIMARY KEY,
  sms_id BIGINT,
  providedMessageId NVARCHAR(100),
  providerName NVARCHAR(100),
  sendDate DATETIME,
  status INT,
  errorCode INT,
  deliveryDate DATETIME
)
  GO


drop SEQUENCE IF EXISTS f_registry_fi_act_com_seq;
CREATE SEQUENCE f_registry_fi_act_com_seq
  AS [BIGINT]
START WITH 1
INCREMENT BY 1
CACHE
GO


CREATE TABLE FIRST_FI_REGISTRY_ACTION_COMMUNICATION (
  id BIGINT PRIMARY KEY,
  communication_type INT,
  communication_id BIGINT,
  fi_registry_action_id BIGINT
)
GO
