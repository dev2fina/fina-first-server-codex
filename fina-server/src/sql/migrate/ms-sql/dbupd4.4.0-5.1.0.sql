/*
 Database: SQL Server 2008/2012

 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 17/01/2014
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.1.0'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
	Create FI licence history table
*/
CREATE TABLE IN_LICENCES_HISTORY(
	[id] [decimal](10, 0) NOT NULL,
	[licence_id] [decimal](10, 0) NOT NULL,
	[licence_status] [decimal](10, 0) NOT NULL,
	[change] [nvarchar](max) NULL,
	[change_date] [datetime] NOT NULL
) 

/*
	Add FI licence default column
*/
ALTER TABLE IN_LICENCES ADD isdefault int NULL;

/*
	Create Fis group change history table
*/
CREATE TABLE IN_BANK_GROUP_HISTORY(
	[id] [decimal](10, 0) NOT NULL,
	[bankid] [decimal](10, 0) NOT NULL,
	[bankgroupid] [decimal](10, 0) NOT NULL,
	[changedate] [date] NULL
)

/*
	Create FI type groups join table
*/
CREATE TABLE IN_BANK_TYPE_CRITERION(
	[banktypeid] [decimal](10, 0) NOT NULL,
	[criterionId] [decimal](10, 0) NOT NULL,
	[isdefault] [int] NULL
)

/**
*change IN_CRIETERION Isdefault column to nullable
*/
ALTER TABLE IN_CRITERION
ALTER COLUMN ISDEFAULT decimal(1,0) NULL
GO

/*
	Permission update for undefined banks in DCS
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
--name
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id, @lang_id, 'net.fina.dcs.undefinedBank');
--permission
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id, @ss_id, 'net.fina.dcs.undefinedBank');
GO

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_STRINGS)
WHERE PK_COLUMN_NAME ='SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME ='SYS_PERMISSION_MAXID';

GO

/*
	Add table for responsible users
*/

CREATE TABLE SYS_BANK_USERS (
	[BANKID] [DECIMAL](10, 0) NOT NULL,
	[USERID] [DECIMAL](10, 0) NOT NULL
);




