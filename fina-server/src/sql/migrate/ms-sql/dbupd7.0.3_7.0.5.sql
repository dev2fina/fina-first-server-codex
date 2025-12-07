/*
 Database: MS SQL Server

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


IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'AUTOMATIC' AND Object_ID = Object_ID('IN_COMMUNICATOR_NOTIFICATIONS'))
BEGIN
ALTER TABLE IN_COMMUNICATOR_NOTIFICATIONS ADD AUTOMATIC bit;
EXEC('update IN_COMMUNICATOR_NOTIFICATIONS set AUTOMATIC = 0');
END;


IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'MANUALINPUT' AND Object_ID = Object_ID('IN_RETURN_DEFINITIONS'))
BEGIN
ALTER TABLE IN_RETURN_DEFINITIONS ADD MANUALINPUT bit;
EXEC('update IN_RETURN_DEFINITIONS set MANUALINPUT = 0');
END;


IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'IS_BOOKMARKED' AND Object_ID = Object_ID('IN_COMMUNICATOR_MESSAGES'))
BEGIN
ALTER TABLE IN_COMMUNICATOR_MESSAGES ADD IS_BOOKMARKED bit;
EXEC('update IN_COMMUNICATOR_MESSAGES set IS_BOOKMARKED = 0');
END;

IF NOT EXISTS(SELECT 1 FROM sys.columns
          WHERE Name = 'IS_ROOT_BOOKMARKED' AND Object_ID = Object_ID('IN_COMMUNICATOR_MESSAGES'))
BEGIN
ALTER TABLE IN_COMMUNICATOR_MESSAGES ADD IS_ROOT_BOOKMARKED bit;
EXEC('update IN_COMMUNICATOR_MESSAGES set IS_ROOT_BOOKMARKED = 0');
END;

-- Create Permissions
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

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.communicator.messages.bookmarks.review')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+1, @lang_id, 'net.fina.communicator.messages.bookmarks.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+1, @ss_id+1, 'net.fina.communicator.messages.bookmarks.review');
END;

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.communicator.messages.bookmarks.amend')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+2, @lang_id, 'net.fina.communicator.messages.bookmarks.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+2, @ss_id+2, 'net.fina.communicator.messages.bookmarks.amend');
END;

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.web.userFileRepository')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+3, @lang_id, 'net.fina.web.userFileRepository');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+3, @ss_id+3, 'net.fina.web.userFileRepository');
END;

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.dcs.userFileRepository')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+3, @lang_id, 'net.fina.dcs.userFileRepository');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+3, @ss_id+3, 'net.fina.dcs.userFileRepository');
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
GO

-- increase SYS_NOTIFICATIONS notification column size
alter table SYS_NOTIFICATIONS ALTER COLUMN notification NVARCHAR(MAX)

IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'CONTACTPERSONSTRID' AND Object_ID = Object_ID('SYS_USERS'))
BEGIN
ALTER TABLE SYS_USERS ADD CONTACTPERSONSTRID int null;
END;


IF NOT EXISTS (SELECT * FROM SYS_PROPERTIES WHERE prop_key = 'fina2.authentication.ldap.attributeNames')
BEGIN
insert into SYS_PROPERTIES(prop_key,value) values('fina2.authentication.ldap.attributeNames','{"firstNameAttribute":"givenName","lastNameAttribute":"sn","titleAttribute":"description","mailAttribute":"mail","phoneAttribute":"telephoneNumber"}')
END;


-- Modify IN_OVERDUE_RETURN_NOTIFICATIONS table
--remove constraints
DECLARE @sql nvarchar(255)
WHILE EXISTS(select *
             from INFORMATION_SCHEMA.TABLE_CONSTRAINTS
             where table_name = 'IN_OVERDUE_RETURN_NOTIFICATIONS')
BEGIN
select @sql = 'ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS DROP CONSTRAINT ' + CONSTRAINT_NAME
from INFORMATION_SCHEMA.TABLE_CONSTRAINTS
where table_name = 'IN_OVERDUE_RETURN_NOTIFICATIONS'
    exec (@sql)
END

ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS alter column NOTIFICATION_TYPE int NOT NULL
ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS DROP COLUMN NOTIFICATION_ID;
ALTER TABLE IN_OVERDUE_RETURN_NOTIFICATIONS ADD CONSTRAINT PK__IN_OVERD__D18C4ECCEB76984D PRIMARY KEY (SCHEDULE_ID, NOTIFICATION_TYPE);


IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'REPOSITORY_FILE_ID' AND Object_ID = Object_ID('OUT_STORED_REPORTS'))
BEGIN
alter table OUT_STORED_REPORTS add REPOSITORY_FILE_ID varchar(255);
END;

alter table out_stored_reports alter column reportResult image null ;

IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'REPOSITORY_FILE_ID' AND Object_ID = Object_ID('SYS_UPLOADEDFILE'))
BEGIN
alter table SYS_UPLOADEDFILE add REPOSITORY_FILE_ID varchar(255);
END;

IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'REPOSITORY_FILE_ID' AND Object_ID = Object_ID('IN_IMPORTED_RETURNS'))
BEGIN
alter table IN_IMPORTED_RETURNS add REPOSITORY_FILE_ID varchar(255);
END;


