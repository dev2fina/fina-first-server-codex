/*
 Database: MS SQL Server

 Author: Lado Melikidze
 E: lado@fina2.net
 Version: 1.0
 Date : 23/04/2020
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.2'
WHERE prop_key = 'fina2.database.schemaVersion';



IF NOT EXISTS (SELECT * FROM SYS_PROPERTIES WHERE prop_key = 'net.fina.web.dashboard.default')
BEGIN
    insert into SYS_PROPERTIES(prop_key,value) values('net.fina.web.dashboard.default',
    '{"columnCount":2,"portalColor":"#ffffff","dashletColumn":{"CLOCK":0,"NUMBER_OF_FIS_BY_TYPE":1}}')
END;

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

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.first.dashboard.review')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+1, @lang_id, 'net.fina.first.dashboard.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+1, @ss_id+1, 'net.fina.first.dashboard.review');
END;

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.first.attestation.amend')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+2, @lang_id, 'net.fina.first.attestation.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+2, @ss_id+2, 'net.fina.first.attestation.amend');
END;

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.first.attestation.delete')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+3, @lang_id, 'net.fina.first.attestation.delete');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+3, @ss_id+3, 'net.fina.first.attestation.delete');
END;

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.tool.bundleTranslator')
BEGIN
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id+4, @lang_id, 'net.fina.tool.bundleTranslator');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id+4, @ss_id+4, 'net.fina.tool.bundleTranslator');
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


IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'DELETED' AND Object_ID = Object_ID('SYS_LANGUAGES'))
    BEGIN
        alter table SYS_LANGUAGES add DELETED bit default 0;
        EXEC('update SYS_LANGUAGES set DELETED = 0 ');
    END