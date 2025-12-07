/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.0'
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

IF NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.feedback.review')
BEGIN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 1, @lang_id, 'net.fina.feedback.review');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.feedback.review');
END;

IF
NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.feedback.amend')
BEGIN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 2, @lang_id, 'net.fina.feedback.amend');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 2, @ss_id + 2, 'net.fina.feedback.amend');
END;

IF
NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.feedbackCategory.amend')
BEGIN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 3, @lang_id, 'net.fina.feedbackCategory.amend');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 3, @ss_id + 3, 'net.fina.feedbackCategory.amend');
END;

IF
NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.first.config.amend')
BEGIN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 4, @lang_id, 'net.fina.first.config.amend');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 4, @ss_id + 4, 'net.fina.first.config.amend');
END;

IF
NOT EXISTS (SELECT * FROM SYS_PERMISSIONS WHERE idName = 'net.fina.first.config.delete')
BEGIN
    INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 5, @lang_id, 'net.fina.first.config.delete');
    INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 5, @ss_id + 5, 'net.fina.first.config.delete');
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

-- FEEDBACK
CREATE SEQUENCE feedback_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

CREATE SEQUENCE feedback_categories_sequence
    AS [BIGINT]
    START WITH 1
    INCREMENT BY 1
    CACHE;

if
(object_id('IN_FEEDBACK', 'U') is null)
begin
create table IN_FEEDBACK
(
    ID               int not null,
    DESCRIPTIONSTRID int,
    CATEGORY_ID      int,
    RATING           int,
);
end;

if
(object_id('IN_FEEDBACK_CATEGORY', 'U') is null)
begin
create table IN_FEEDBACK_CATEGORY
(
    ID        int not null,
    NAMESTRID int,
);
end;

ALTER TABLE SYS_USERS
ALTER COLUMN login  NVARCHAR(40) ;


IF
NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'ADMIN_NAME' AND Object_ID = Object_ID('EMS_SANCTIONS'))
BEGIN
ALTER TABLE EMS_SANCTIONS
    ADD ADMIN_NAME nvarchar(50);
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'ADMIN_ID' AND Object_ID = Object_ID('EMS_SANCTIONS'))
BEGIN
ALTER TABLE EMS_SANCTIONS
    ADD ADMIN_ID nvarchar(50);
END;

