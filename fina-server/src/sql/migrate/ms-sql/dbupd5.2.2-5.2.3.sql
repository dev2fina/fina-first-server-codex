/*
 Database: SQL Server 2012

 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 11/12/2015
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.3'
WHERE prop_key = 'fina2.database.schemaVersion';

ALTER TABLE IN_MAIL_MESSAGE ADD SUBJECT NVARCHAR(512) NULL;

/*
	insert max cycled mail size property <=0 means disabled;
*/
insert into SYS_PROPERTIES values('fina2.mail.blockCycledMail.size','-1');

/*
  FI fax label (* also used in FiPrintServlet as fax column header)
*/
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('net.fina.fi.fax.label', 'Fax');

/*  
    Author: Vamekh Goiati
    Permissions for calendar
*/
DECLARE @ss_id INT
DECLARE @sp_id INT
DECLARE @lang_id INT

SET @ss_id = (SELECT
                max(id)
              FROM SYS_STRINGS) + 1
SET @sp_id = (SELECT
                max(id)
              FROM SYS_PERMISSIONS) + 1
SET @lang_id = (SELECT
                  id
                FROM sys_languages
                WHERE code LIKE '%en%')

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id, @lang_id, 'net.fina.calendar.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 1, @lang_id, 'net.fina.calendar.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 2, @lang_id, 'net.fina.calendar.delete');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id, @ss_id, 'net.fina.calendar.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.calendar.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 2, @ss_id + 2, 'net.fina.calendar.delete');
GO

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_STRINGS)
WHERE PK_COLUMN_NAME ='SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME ='SYS_PERMISSION_MAXID';
GO
