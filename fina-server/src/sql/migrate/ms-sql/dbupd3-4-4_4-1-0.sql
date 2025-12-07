/*
 Database: MSSQL Server 2005 and higher
 Author: Alexander Dolidze <alexander.dolidze@gmail.com>
 Version: 0.4
 Date : 10/12/2012
*/

/*
  Update DB Version
*/
update sys_properties set value='4.1.0' where prop_key='fina2.database.schemaVersion';
 	 	 	
--------------------------------------------------------
--------------------------------------------------------
PRINT '-CREATING-PROCEDURES-----------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO
--GENERAL

CREATE PROCEDURE PRINT_SPACE 
AS 
	PRINT ''
GO

CREATE PROCEDURE SAY_TABLE_NAME
	@name VARCHAR(255)
AS
	PRINT 'Table: '+@name
GO

CREATE PROCEDURE SAY_COLUMN_NAME
	@name VARCHAR(255)
AS
	PRINT 'Column: '+@name
GO

CREATE PROCEDURE SAY_CONSTRAINT_NAME
	@name VARCHAR(255)
AS
	PRINT 'Constraint: '+@name
GO

--FOR-TABLES

CREATE PROCEDURE SAY_TABLE_CREATED
	@name VARCHAR(255) = ''
AS
	PRINT 'Table created. '+@name
GO

CREATE PROCEDURE SAY_IF_EXISTS
	@exists int
AS
	IF (@exists > 0) 
		PRINT 'Exists.'
	ELSE
		PRINT 'Doesn''t exist.'
GO

CREATE PROCEDURE CHECK_IF_TABLE_EXISTS
	@tableName varchar(255),
	@result int out
AS
	EXEC PRINT_SPACE
	EXEC SAY_TABLE_NAME @tableName
	SET @result = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME=@tableName)
	EXEC SAY_IF_EXISTS @result
GO

CREATE PROCEDURE CHECK_IF_TABLE_HAS_IDENTITY_COLUMN
	@table VARCHAR(255),
	@exists INT out
AS
	EXEC PRINT_SPACE
	EXEC SAY_TABLE_NAME @table
	SET @exists = (
		SELECT COUNT(*)
		FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_NAME = @table
		AND COLUMNPROPERTY(OBJECT_ID(TABLE_NAME), COLUMN_NAME, 'IsIdentity') = 1
	)
	IF (@exists > 0)
		PRINT 'Identity column exists.'
	ELSE
		PRINT 'Identity column doesn''t exist.'
GO

--FOR-COLUMNS

CREATE PROCEDURE CHECK_IF_COLUMN_EXISTS
	@table VARCHAR(255),
	@column VARCHAR(255),
	@exists int = 0 out
AS
	EXEC PRINT_SPACE
	EXEC SAY_TABLE_NAME @table
	PRINT 'Column: '+@column
	SET @exists = (
		SELECT COUNT(*)
		FROM INFORMATION_SCHEMA.COLUMNS 
		WHERE TABLE_NAME=@table 
		AND COLUMN_NAME=@column
	)
	IF (@exists > 0) 
		PRINT 'Exists.'
	ELSE
		PRINT 'Doesn''t exist.'
GO

CREATE PROCEDURE SAY_COLUMN_CREATED
	@table VARCHAR(255),
	@column VARCHAR(255)
AS
	PRINT 'COLUMN: "' + @column + '" in TABLE: "' + @table + '" created.';
GO

--FOR-CONSTRAINTS

CREATE PROCEDURE CHECK_IF_CONSTRAINT_EXISTS
	@table VARCHAR(255),
	@constraint VARCHAR(255),
	@exists INT = 0 OUT
AS
	EXEC PRINT_SPACE
	EXEC SAY_CONSTRAINT_NAME @constraint
	SET @exists = (
		SELECT COUNT(*)
		FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
		WHERE TABLE_NAME = @table
		AND CONSTRAINT_NAME = @constraint
		)
	IF (@exists > 0)
		PRINT 'Exists.'
	ELSE
		PRINT 'Doesn''t exist.'
GO

--FOR-PERMISSIONS

CREATE PROCEDURE CREATE_PERMISSION
	@idname NVARCHAR(80),
	@description NVARCHAR(255)
AS
	EXEC PRINT_SPACE
	PRINT 'Permission'
	PRINT 'Code: ' + @idname
	PRINT 'Description: ' + @description
	
	DECLARE @count INT = 0
	SET @count = (SELECT COUNT(*) FROM SYS_PERMISSIONS WHERE IDNAME=@idname)
	IF (@count = 0)
	BEGIN
		DECLARE @saUserId INT = (select ID from SYS_USERS where LOWER(LTRIM(RTRIM(LOGIN)))='sa');
		DECLARE @maxPermId INT = (SELECT MAX(ID) FROM SYS_PERMISSIONS)
		DECLARE @maxSysStringId INT = (SELECT MAX(ID) FROM SYS_STRINGS)
		DECLARE @langId INT
		DECLARE @langIds cursor 
		
		PRINT 'INSERTING PERMISSION DESCRIPTION INTO SYS_STRINGS TABLE'
		
		SET @langIds = CURSOR FOR
			SELECT ID FROM SYS_LANGUAGES
			
		OPEN @langIds

		FETCH NEXT FROM @langIds into @langId

		WHILE (@@FETCH_STATUS = 0) 
		BEGIN 
			INSERT 
			INTO SYS_STRINGS(ID,LANGID,VALUE) 
			VALUES(@maxSysStringId+1, @langId, @description);
			
			FETCH NEXT FROM @langIds into @langId				
		END 

		CLOSE @langIds
		DEALLOCATE @langIds
		
		PRINT 'INSERTING PERMISSION INTO SYS_PERMISSIONS TABLE'
		
		INSERT 
		INTO SYS_PERMISSIONS(ID,IDNAME,NAMESTRID) 
		VALUES(@maxPermId+1, @idname, @maxSysStringId+1);
		
		PRINT 'GRANTING PERMISSION TO USER SA'
		
		INSERT
		INTO SYS_USER_PERMISSIONS(USERID,PERMISSIONID) 
		VALUES(@saUserId, @maxPermId+1);
		
		PRINT 'Permission created.'
	END ELSE
	IF (@count = 1)
	BEGIN
		PRINT 'Permission already exists.'
	END ELSE
	IF (@count > 1)
	BEGIN
		PRINT 'ERROR. More than one permission exists with same code.'
	END
GO

--FOR-SYS_ID_GENERATOR

CREATE PROCEDURE ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD
	@pk_column_name VARCHAR(255),
	@table_name NVARCHAR(255)
AS
	IF (NOT EXISTS(SELECT * FROM SYS_ID_GENERATOR WHERE PK_COLUMN_NAME=@pk_column_name))
	BEGIN
		INSERT INTO SYS_ID_GENERATOR(PK_COLUMN_NAME, VALUE) VALUES(@pk_column_name, 1)
		PRINT 'INSERTED row ('+@pk_column_name+',1) into SYS_ID_GENERATOR table.'
	END
	
	DECLARE @max_id INT = 0
	
	DECLARE @query NVARCHAR(1000)
	DECLARE @params NVARCHAR(1000)
	SET @query = N'SELECT @result = MAX(ID) FROM ' + @table_name
	SET @params = N'@result INT OUT'
	EXEC SP_EXECUTESQL @query, @params, @result = @max_id OUT
	
	UPDATE SYS_ID_GENERATOR SET VALUE = @max_id+1 WHERE PK_COLUMN_NAME = @pk_column_name
	PRINT 'UPDATED '+@pk_column_name+' new value = ' + CAST( @max_id+1 AS VARCHAR(50) )
GO

--------------------------------------------------------
--------------------------------------------------------
PRINT '-CREATING-TABLES---------------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO

DECLARE @tableName VARCHAR(255) = 'IN_MAIL_MESSAGE'
DECLARE @exists int = 0
EXEC CHECK_IF_TABLE_EXISTS @tableName, @exists OUTPUT
IF (@exists = 0) 
BEGIN
	CREATE TABLE IN_MAIL_MESSAGE(
		[ID] [bigint] NOT NULL,
		[ADDRESS] [varchar](255) NULL,
		[FROM_ADDRESS] [varchar](255) NULL,
		[MAIL_USER] [varchar](255) NULL,
		[MESSAGE_ID] [varchar](512) NULL,
		[NOTE] [varchar](max) NULL,
		[RECIVE_DATE] [datetime2](7) NULL,
		[STATUS] [int] NULL,
		[MESSAGE_REPLAY_ID] [bigint] NULL,
		[READ_DATE] [datetime2](7) NULL
	)
	EXEC SAY_TABLE_CREATED @tableName 
END

GO

DECLARE @tableName VARCHAR(255) = 'IN_MAIL_MESSAGE_UPLOADFILES'
DECLARE @exists int = 0
EXEC CHECK_IF_TABLE_EXISTS @tableName, @exists OUTPUT
IF (@exists = 0) 
BEGIN
	CREATE TABLE IN_MAIL_MESSAGE_UPLOADFILES(
		[IN_MAIL_MESSAGE_ID] [bigint] NOT NULL,
		[uploadFiles_ID] [bigint] NOT NULL
	)
	EXEC SAY_TABLE_CREATED @tableName
END

GO

DECLARE @tableName VARCHAR(255) = 'OUT_MAIL_MESSAGE_REPLY'
DECLARE @exists int = 0
EXEC CHECK_IF_TABLE_EXISTS @tableName, @exists OUTPUT
IF (@exists = 0) 
BEGIN
	CREATE TABLE OUT_MAIL_MESSAGE_REPLY(
		[ID] [bigint] NOT NULL,
		[MAIL_BCC] [varchar](max) NULL,
		[MAIL_CC] [varchar](max) NULL,
		[MAIL_CONTENT] [varchar](max) NULL,
		[MAIL_DATE] [datetime2](7) NOT NULL,
		[MAIL_FROM] [varchar](255) NOT NULL,
		[MAIL_SEND_STATUS] [int] NOT NULL,
		[MAIL_SENDER] [varchar](255) NOT NULL,
		[MAIL_TO] [varchar](512) NOT NULL
	)
	EXEC SAY_TABLE_CREATED @tableName
END

GO

DECLARE @tableName VARCHAR(255) = 'SYS_ID_GENERATOR'
DECLARE @exists int = 0
EXEC CHECK_IF_TABLE_EXISTS @tableName, @exists OUTPUT
IF (@exists = 0) 
BEGIN
	CREATE TABLE SYS_ID_GENERATOR (
		PK_COLUMN_NAME VARCHAR(255) NOT NULL,
		VALUE bigint,
		PRIMARY KEY(PK_COLUMN_NAME)
	)
	EXEC SAY_TABLE_CREATED @tableName
END

GO

DECLARE @tableName VARCHAR(255) = 'SYS_USER_STATES'
DECLARE @exists int = 0
EXEC CHECK_IF_TABLE_EXISTS @tableName, @exists OUTPUT
IF (@exists = 0) 
BEGIN
	create table SYS_USER_STATES(
		id numeric primary key not null,
		user_id numeric,
		code varchar(100) not null,
		value text
	);
	EXEC SAY_TABLE_CREATED @tableName
END

GO

EXEC PRINT_SPACE

GO

--------------------------------------------------------
--------------------------------------------------------
PRINT '-ADDING-COLUMNS----------------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO

DECLARE @table VARCHAR(255) = 'IN_PERIOD_TYPES';
DECLARE @column VARCHAR(255) = 'periodType';
DECLARE @exists int = 0
EXEC CHECK_IF_COLUMN_EXISTS @table, @column, @exists output
IF (@exists = 0) 
BEGIN
   ALTER TABLE IN_PERIOD_TYPES ADD periodType INT
   EXEC SAY_COLUMN_CREATED @table, @column
END

GO

DECLARE @table VARCHAR(255) = 'OUT_STORED_REPORTS';
DECLARE @column VARCHAR(255) = 'reportHtmlResult';
DECLARE @exists int = 0
EXEC CHECK_IF_COLUMN_EXISTS @table, @column, @exists output
IF (@exists = 0) 
BEGIN
   ALTER TABLE OUT_STORED_REPORTS ADD reportHtmlResult TEXT
   EXEC SAY_COLUMN_CREATED @table, @column
END

GO

DECLARE @table VARCHAR(255) = 'IN_BANK_TYPES';
DECLARE @column VARCHAR(255) = 'version';
DECLARE @exists int = 0
EXEC CHECK_IF_COLUMN_EXISTS @table, @column, @exists output
IF (@exists = 0) 
BEGIN
   ALTER TABLE IN_BANK_TYPES ADD version INT
   EXEC SAY_COLUMN_CREATED @table, @column
END

GO

EXEC PRINT_SPACE
--------------------------------------------------------
--------------------------------------------------------
PRINT '-ADDING-OPTLOCK-COLUMNS--------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO

declare @optlockColumnName varchar(50) = 'optlock';

DECLARE @table VARCHAR(255)
DECLARE @tables cursor 

SET @tables = CURSOR FOR
SELECT DISTINCT(tbl.TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES tbl
LEFT JOIN
INFORMATION_SCHEMA.COLUMNS col on tbl.TABLE_NAME = col.TABLE_NAME
WHERE
 tbl.TABLE_NAME in (
	'IN_BANK_BRANCHES',
	'IN_BANK_GROUPS',
	'IN_BANK_TYPES',
	'IN_BANK_MANAGEMENT',
	'IN_BANKS',
	'IN_COUNTRY_DATA',
	'IN_CRITERION',
	'IN_DEFINITION_TABLES',
	'IN_LICENCE_TYPES',
	'IN_LICENCES',
	'IN_MANAGING_BODIES',
	'IN_MDT_COMPARISON',
	'IN_MDT_NODES',
	'IN_PERIOD_TYPES',
	'IN_PERIODS',
	'IN_RETURN_DEFINITIONS',
	'IN_RETURN_TYPES',
	'IN_RETURN_VERSIONS',
	'IN_SCHEDULES',
	'OUT_STORED_REPORTS',
	'SYS_LANGUAGES',
	'SYS_ROLES',
	'SYS_USERS'
)

OPEN @tables

DECLARE @exists INT = 0;
FETCH NEXT FROM @tables into @table

WHILE (@@FETCH_STATUS = 0) 
BEGIN 
	EXEC CHECK_IF_COLUMN_EXISTS @table, @optlockColumnName, @exists out
	IF (@exists = 0) 
	BEGIN
		EXEC ('ALTER TABLE '+@table+' ADD '+@optlockColumnName+' INT DEFAULT 0')
		EXEC SAY_COLUMN_CREATED @table, @optlockColumnName
	END
	
    FETCH NEXT FROM @tables INTO @table
    SET @exists = 0
END 

CLOSE @tables
DEALLOCATE @tables


GO

EXEC PRINT_SPACE
--------------------------------------------------------
--------------------------------------------------------
PRINT '-DROPPING-CONSTRAINTS----------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO

DECLARE @table VARCHAR(255) = 'SYS_UPLOADEDFILE'
DECLARE @constraint VARCHAR(255) = 'PK_SYS_UPLOADEDFILE'
DECLARE @exists INT = 0
EXEC CHECK_IF_CONSTRAINT_EXISTS @table, @constraint, @exists OUT
IF (@exists > 0)
BEGIN
	ALTER TABLE SYS_UPLOADEDFILE DROP CONSTRAINT PK_SYS_UPLOADEDFILE
	PRINT 'Constraint dropped: ' + @constraint
END

GO

EXEC PRINT_SPACE
--------------------------------------------------------
--------------------------------------------------------
PRINT '-REMOVING-IDENTITY-COLUMN------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO

DECLARE @table VARCHAR(255) = 'SYS_UPLOADEDFILE'
DECLARE @exists INT = 0
EXEC CHECK_IF_TABLE_HAS_IDENTITY_COLUMN @table, @exists OUT

IF (@exists > 0) 
BEGIN
PRINT 'Removing identity column from SYS_UPLOADEDFILE, this may take several seconds.'

BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON

CREATE TABLE dbo.Tmp_SYS_UPLOADEDFILE
(
    [bankCode] [varchar](20) NULL,
	[username] [numeric](10, 0) NULL,
	[fileName] [varchar](50) NULL,
	[status] [varchar](20) NULL,
	[uploadedTime] [datetime] NULL,
	[uploadedFile] [image] NULL,
	[nameValid] [bit] NULL,
	[versionvalid] [bit] NULL,
	[protectioninfo] [nvarchar](255) NULL,
	[passwordInfo] [nvarchar](255) NULL,
	[hasUserBank] [bit] NULL,
	[id] [numeric](18, 0) NOT NULL,
	[type] [int] NULL,
	[matrixValid] [bit] NULL,
	[reason] [nvarchar](255) NULL,
) ON [PRIMARY]

IF EXISTS(SELECT * FROM dbo.SYS_UPLOADEDFILE)
BEGIN
	INSERT INTO dbo.Tmp_SYS_UPLOADEDFILE (
		bankCode,
		username,
		fileName,
		status,
		uploadedTime,
		uploadedFile,
		nameValid,
		versionvalid,
		protectioninfo,
		passwordInfo,
		hasUserBank,
		id,
		type,
		matrixValid,
		reason
	)
	SELECT 
		bankCode,
		username,
		fileName,
		status,
		uploadedTime,
		uploadedFile,
		nameValid,
		versionvalid,
		protectioninfo,
		passwordInfo,
		hasUserBank,
		id,
		type,
		matrixValid,
		reason 
	FROM SYS_UPLOADEDFILE WITH (HOLDLOCK TABLOCKX)
END

DROP TABLE dbo.SYS_UPLOADEDFILE

EXECUTE sp_rename N'dbo.Tmp_SYS_UPLOADEDFILE', N'SYS_UPLOADEDFILE', 'OBJECT'

ALTER TABLE SYS_UPLOADEDFILE ALTER COLUMN [fileName] varchar(256)

COMMIT
END

GO

EXEC PRINT_SPACE
--------------------------------------------------------
--------------------------------------------------------
PRINT '-ADDING-PERMISSIONS------------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO
EXEC CREATE_PERMISSION N'fina2.web.user', N'Web Portal User'
EXEC CREATE_PERMISSION N'fina2.regCity.amend', N'Regions/Cities Management'
EXEC CREATE_PERMISSION N'fina2.web.internal.user', 'Internal User'
EXEC CREATE_PERMISSION N'fina2.web.external.user', 'External User'
EXEC CREATE_PERMISSION N'fina2.stored.reports.view', 'Stored Reports Review'
EXEC CREATE_PERMISSION N'fina2.stored.reports.delete', 'Stored Reports Delete'
EXEC CREATE_PERMISSION N'net.fina.language.review', 'Language Review'
EXEC CREATE_PERMISSION N'net.fina.language.amend', 'Language Amend'
EXEC CREATE_PERMISSION N'net.fina.language.delete', 'Language Delete'
EXEC CREATE_PERMISSION N'net.fina.user.review', 'User Manager Review'
EXEC CREATE_PERMISSION N'net.fina.user.amend', 'User Manager Amend'
EXEC CREATE_PERMISSION N'net.fina.user.delete', 'User Manager Delete'
EXEC CREATE_PERMISSION N'net.fina.lictype.review', 'License Type Review'
EXEC CREATE_PERMISSION N'net.fina.lictype.amend', 'License Type Amend'
EXEC CREATE_PERMISSION N'net.fina.lictype.delete', 'License Type Delete'
EXEC CREATE_PERMISSION N'net.fina.dcs.fileUpload', 'DCS File Upload'
EXEC CREATE_PERMISSION N'net.fina.dcs.fileUpload.delete', 'DCS File Upload Delete'
EXEC CREATE_PERMISSION N'net.fina.dcs.manualInput', 'Manual Input'
EXEC CREATE_PERMISSION N'fina2.metadata.amend', 'MetaData Tree Amend'
EXEC CREATE_PERMISSION N'fina2.metadata.delete', 'MetaData Tree Delete'
EXEC CREATE_PERMISSION N'fina2.metadata.review', 'MetaData Tree Review'
EXEC CREATE_PERMISSION N'fina2.bank.amend', 'FI Amend'
EXEC CREATE_PERMISSION N'fina2.bank.delete', 'FI Delete'
EXEC CREATE_PERMISSION N'fina2.bank.review', 'FI Review'
EXEC CREATE_PERMISSION N'fina2.returns.definition.amend', 'Return Definition Amend'
EXEC CREATE_PERMISSION N'fina2.returns.definition.delete', 'Return Definition Delete'
EXEC CREATE_PERMISSION N'fina2.returns.definition.review', 'Return Definition Review'
EXEC CREATE_PERMISSION N'fina2.returns.definition.format', 'Return Definition Format'
EXEC CREATE_PERMISSION N'fina2.menu.amend', 'Menu Amend'
EXEC CREATE_PERMISSION N'fina2.security.amend', 'Users Amend'
EXEC CREATE_PERMISSION N'fina2.returns.schedule.amend', 'Schedules Amend'
EXEC CREATE_PERMISSION N'fina2.returns.schedule.delete', 'Schedules Delete'
EXEC CREATE_PERMISSION N'fina2.returns.schedule.review', 'Schedules Review'
EXEC CREATE_PERMISSION N'fina2.returns.amend', 'Returns Amend'
EXEC CREATE_PERMISSION N'fina2.returns.delete', 'Returns Delete'
EXEC CREATE_PERMISSION N'fina2.returns.review', 'Returns Review'
EXEC CREATE_PERMISSION N'fina2.returns.process', 'Returns Process'
EXEC CREATE_PERMISSION N'fina2.returns.accept', 'Returns Accept'
EXEC CREATE_PERMISSION N'fina2.returns.reset', 'Returns Reset'
EXEC CREATE_PERMISSION N'fina2.returns.reject', 'Returns Reject'
EXEC CREATE_PERMISSION N'fina2.periods.amend', 'Periods Amend'
EXEC CREATE_PERMISSION N'fina2.periods.delete', 'Periods Delete'
EXEC CREATE_PERMISSION N'fina2.periods.review', 'Periods Review'
EXEC CREATE_PERMISSION N'fina2.report.amend', 'Reports Amend'
EXEC CREATE_PERMISSION N'fina2.report.generate', 'Reports Generate'
EXEC CREATE_PERMISSION N'fina2.returns.statuses', 'Return Statuses'
EXEC CREATE_PERMISSION N'fina2.report.scheduler', 'Report Scheduler'
EXEC CREATE_PERMISSION N'fina2.reports.scheduler.manager', 'Reports Scheduler Manager'
EXEC CREATE_PERMISSION N'fina2.reports.scheduler.add', 'Reports Schedules Add'
EXEC CREATE_PERMISSION N'fina2.reports.stored.manager', 'Stored Reports Manager'
EXEC CREATE_PERMISSION N'fina2.security.settings', 'Security Settings'
EXEC CREATE_PERMISSION N'fina2.returns.version.amend', 'Return Version Amend'
EXEC CREATE_PERMISSION N'fina2.returns.version.delete', 'Return Version Delete'
EXEC CREATE_PERMISSION N'fina2.returns.version.review', 'Return Version Review'

GO

EXEC PRINT_SPACE
--------------------------------------------------------
--------------------------------------------------------
PRINT '-FILLING-SYS_ID_GENERATOR-TABLE------------------'
--------------------------------------------------------
--------------------------------------------------------

GO

delete from SYS_ID_GENERATOR;
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_CRITERION_MAXID', 'IN_CRITERION'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_BANKS_MAXID', 'IN_BANKS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_BANK_BRANCHES_MAXID', 'IN_BANK_BRANCHES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_BANK_MANAGEMENT_MAXID', 'IN_BANK_MANAGEMENT'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_BANK_TYPES_MAXID', 'IN_BANK_TYPES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_LICENSE_MAXID', 'IN_LICENCES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_LICENSE_TYPES_MAXID', 'IN_LICENCE_TYPES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_MANAGING_BODIES_MAXID', 'IN_MANAGING_BODIES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_BANK_GROUPS_MAXID', 'IN_BANK_GROUPS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'SYS_LANGUAGE_MAXID', 'SYS_LANGUAGES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_MDT_COMPARISON_MAXID', 'IN_MDT_COMPARISON'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_MDT_NODES_MAXID', 'IN_MDT_NODES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_PERIODS_MAXID', 'IN_PERIODS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_PERIOD_TYPES_MAXID', 'IN_PERIOD_TYPES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_COUNTRY_DATA_MAXID', 'IN_COUNTRY_DATA'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'OUT_REPORTS_MAXID', 'OUT_REPORTS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_RETURN_MAXID', 'IN_RETURNS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_DEFINITION_MAXID', 'IN_RETURN_DEFINITIONS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_RETURN_STATUSE_MAXID', 'IN_RETURN_STATUSES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_RETURN_TYPE_MAXID', 'IN_RETURN_TYPES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_RETURN_VERSION_MAXID', 'IN_RETURN_VERSIONS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_SCHEDULES_MAXID', 'IN_SCHEDULES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'SYS_PERMISSION_MAXID', 'SYS_PERMISSIONS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'SYS_ROLE_MAXID', 'SYS_ROLES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'SYS_USER_MAXID', 'SYS_USERS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'SYS_USER_STATES_MAXID', 'SYS_USER_STATES'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'SYS_UPLOADEDFILE_MAXID', 'SYS_UPLOADEDFILE'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_IMPORTED_RETURNS_MAXID', 'IN_IMPORTED_RETURNS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_MAIL_MESSAGE_MAXID', 'IN_MAIL_MESSAGE'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'IN_RETURN_ITEM_MAXID', 'IN_RETURN_ITEMS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'SYS_STRING_MAXID', 'SYS_STRINGS'
EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'OUT_MAIL_MESSAGE_REPLY_MAXID', 'OUT_MAIL_MESSAGE_REPLY'
update SYS_ID_GENERATOR set value=1 where VALUE is null;
GO

EXEC PRINT_SPACE
--------------------------------------------------------
--------------------------------------------------------
PRINT '-UPDATING-PROPERTIES-----------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO

delete from sys_properties where prop_key in('fina2.mail.searchType','fina2.mail.messages.limit');

GO

insert into sys_properties(prop_key,value) values('fina2.mail.searchType',1);

GO

insert into sys_properties(prop_key,value) values('fina2.mail.messages.limit',200);

GO

GO

EXEC PRINT_SPACE
--------------------------------------------------------
--------------------------------------------------------
PRINT '-ADDING-TRIGGERS---------------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO
IF (EXISTS(select * from sys.triggers where name = 'returnsSafeIdTrigger'))
	DROP TRIGGER returnsSafeIdTrigger
IF (EXISTS(select * from sys.triggers where name = 'returnItemsSafeIdTrigger'))
	DROP TRIGGER returnItemsSafeIdTrigger
IF (EXISTS(select * from sys.triggers where name = 'correctUserOptlock'))
	DROP TRIGGER correctUserOptlock
IF (EXISTS(select * from sys.triggers where name = 'inImportedReturnsSafeIdTrigger'))
	DROP TRIGGER inImportedReturnsSafeIdTrigger
IF (EXISTS(select * from sys.triggers where name = 'inReturnStatusesSafeIdTrigger'))
	DROP TRIGGER inReturnStatusesSafeIdTrigger

GO

--==============================================
CREATE TRIGGER returnsSafeIdTrigger 
   ON  .in_returns 
   AFTER INSERT
AS 
BEGIN

declare @id bigint
select @id=inserted.id+1 from inserted

UPDATE SYS_ID_GENERATOR SET  VALUE=@id WHERE PK_COLUMN_NAME='IN_RETURN_MAXID'

END

GO

-- ============================================
CREATE TRIGGER returnItemsSafeIdTrigger 
   ON  .in_return_items 
   AFTER INSERT
AS 
BEGIN

declare @id bigint
select @id=inserted.id+1 from inserted

UPDATE SYS_ID_GENERATOR SET  VALUE=@id WHERE PK_COLUMN_NAME='IN_RETURN_ITEM_MAXID'
END

GO

--==============================================
CREATE TRIGGER inImportedReturnsSafeIdTrigger 
   ON  .in_imported_returns
   AFTER INSERT
AS 
BEGIN

declare @id bigint
select @id=inserted.id+1 from inserted

UPDATE SYS_ID_GENERATOR SET VALUE=@id WHERE PK_COLUMN_NAME='IN_IMPORTED_RETURNS_MAXID'

END


GO

--==============================================
CREATE TRIGGER inReturnStatusesSafeIdTrigger 
   ON  .in_return_statuses
   AFTER INSERT
AS 
BEGIN

declare @id bigint
select @id=inserted.id+1 from inserted

UPDATE SYS_ID_GENERATOR SET VALUE=@id WHERE PK_COLUMN_NAME='IN_RETURN_STATUSE_MAXID'

END

GO

EXEC PRINT_SPACE
--------------------------------------------------------
--------------------------------------------------------
PRINT '-DROPPING-PROCEDURES-----------------------------'
--------------------------------------------------------
--------------------------------------------------------

GO

DROP PROCEDURE CHECK_IF_TABLE_EXISTS

DROP PROCEDURE SAY_IF_EXISTS

DROP PROCEDURE SAY_TABLE_CREATED

DROP PROCEDURE SAY_TABLE_NAME

DROP PROCEDURE SAY_COLUMN_NAME

DROP PROCEDURE CHECK_IF_COLUMN_EXISTS

DROP PROCEDURE SAY_COLUMN_CREATED

DROP PROCEDURE SAY_CONSTRAINT_NAME

DROP PROCEDURE CHECK_IF_TABLE_HAS_IDENTITY_COLUMN

DROP PROCEDURE CHECK_IF_CONSTRAINT_EXISTS

DROP PROCEDURE CREATE_PERMISSION

DROP PROCEDURE ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD

DROP PROCEDURE PRINT_SPACE

GO

--------------------------------------------------------
--------------------------------------------------------
PRINT '-SCRIPT-FINISHED---------------------------------'
--------------------------------------------------------
--------------------------------------------------------