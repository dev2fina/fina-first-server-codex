/*
 Database: MS SQL Server

 Author: Nikoloz Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 15/01/2019
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

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+1, @lang_id, 'net.fina.first.registry.import');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+1, @ss_id+1, 'net.fina.first.registry.import');

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+2, @lang_id, 'net.fina.web.notifications');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+2, @ss_id+2, 'net.fina.web.notifications');

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

CREATE SEQUENCE notifications_sequence  START WITH 1   INCREMENT BY 1;

CREATE TABLE SYS_NOTIFICATIONS (
    ID int not null,
    notify int not null,
    notification NVARCHAR(500),
    datetime_added DATETIME2,
    datetime_read DATETIME2,
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



if (object_id('SYS_ROLE_BANKS', 'U') is null)
    begin
        create table SYS_ROLE_BANKS(
            ROLE_ID decimal(10),
            BANK_ID decimal(10)
        );
    end;

IF NOT EXISTS (SELECT * FROM SYS_PROPERTIES WHERE prop_key = 'net.fina.dcs.fileUpload.restrictWhileProcessing')
BEGIN
    insert  into SYS_PROPERTIES(prop_key,value) values('net.fina.dcs.fileUpload.restrictWhileProcessing','-1')
END;

alter table SYS_USERS add BLOCKEDBYSYSTEM bit default 0;
update SYS_USERS set BLOCKEDBYSYSTEM = 0;