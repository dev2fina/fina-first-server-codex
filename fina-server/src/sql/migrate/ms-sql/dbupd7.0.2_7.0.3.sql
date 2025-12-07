/*
 Database: MS SQL Server

 Author: Nikoloz Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 23/04/2020
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.3'
WHERE prop_key = 'fina2.database.schemaVersion';

CREATE INDEX IDX_IN_RETURN_ITEMS_NODEID ON IN_RETURN_ITEMS ([NODEID] DESC);

IF NOT EXISTS (SELECT * FROM SYS_PROPERTIES WHERE prop_key = 'net.fina.dcs.signatureCheckerProperty')
BEGIN
    insert into SYS_PROPERTIES(prop_key,value) values('net.fina.dcs.signatureCheckerProperty','CommonName')
END;


IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'FORMAT' AND Object_ID = Object_ID('IN_RETURN_TYPES'))
    BEGIN
        ALTER TABLE IN_RETURN_TYPES ADD FORMAT VARBINARY(max);
    END


IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'excelTemplate' AND Object_ID = Object_ID('IN_RETURN_TYPES'))
    BEGIN
        ALTER TABLE IN_RETURN_TYPES ADD excelTemplate bit;
        EXEC('update IN_RETURN_TYPES set excelTemplate = 0');
    END;


IF NOT EXISTS (SELECT * FROM SYS_PROPERTIES WHERE prop_key = 'net.fina.returnDefinition.templateGeneration.enable')
    BEGIN
        insert into SYS_PROPERTIES(prop_key,value) values('net.fina.returnDefinition.templateGeneration.enable','1')
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

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.communicator.messages.acceptWithoutAttachment')
    BEGIN
        INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+1, @lang_id, 'net.fina.communicator.messages.acceptWithoutAttachment');
        INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+1, @ss_id+1, 'net.fina.communicator.messages.acceptWithoutAttachment');
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

-- update EMS INSPECTION TYPE
alter table EMS_INSPECTION_TYPE_DESCRIPTION_I18N alter column name nvarchar(255) null
go
