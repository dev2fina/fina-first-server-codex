/*
 Database: MS SQL Server

 Author: Vamekh Goiati
 E: goiati@fina2.net
 Version: 1.0
 Date : 14/11/2016
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.14'
WHERE prop_key = 'fina2.database.schemaVersion';

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'net.fina.serviceMonitor.mailService.status.curr';
INSERT INTO SYS_PROPERTIES VALUES ('net.fina.serviceMonitor.mailService.status.curr', '');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'net.fina.serviceMonitor.mailService.status.last';
INSERT INTO SYS_PROPERTIES VALUES ('net.fina.serviceMonitor.mailService.status.last', '');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'net.fina.serviceMonitor.mailService.nextFireTime';
INSERT INTO SYS_PROPERTIES VALUES ('net.fina.serviceMonitor.mailService.nextFireTime', '');

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

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id, @lang_id, 'Service Monitor');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id, @ss_id, 'net.fina.tool.serviceMonitor');
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




