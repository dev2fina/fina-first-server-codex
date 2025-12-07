/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.1'
WHERE prop_key = 'fina2.database.schemaVersion';

-- permissions
DECLARE
@ss_id INT
DECLARE
@sp_id INT
DECLARE
@lang_id INT

SET @ss_id = (SELECT max(id)
FROM SYS_STRINGS) + 1
SET @sp_id = (SELECT max(id)
FROM SYS_PERMISSIONS) + 1
SET @lang_id = (SELECT id
FROM sys_languages
WHERE code LIKE '%en%')

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.faq.ask.question')
BEGIN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 1, @lang_id, 'net.fina.faq.ask.question');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.faq.ask.question');
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

