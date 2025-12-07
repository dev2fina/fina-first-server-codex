/*
	Database:		MSSQL Server 2005 and higher
	Author:			Alexander Dolidze <alexander.dolidze@gmail.com>
	Version:		1.0
	FINA Version:	5.2.14
	Date:			28/10/2016
*/


PRINT '-SCRIPT-STARTED----------------------------------'
GO

PRINT ''
PRINT '-CREATING-PROCEDURES-----------------------------'
GO

--GENERAL

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

CREATE PROCEDURE SET_SYS_STRING
	@sysStringId INT,
	@langId INT,
	@description NVARCHAR(255)
AS
	DELETE FROM SYS_STRINGS WHERE ID=@sysStringId AND LANGID=@langId
	INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (@sysStringId, @langId, @description)
GO

CREATE PROCEDURE SET_SYS_STRING_ALLANG
	@sysStringId INT,
	@description NVARCHAR(255)
AS
	DECLARE @languageId INT
	DECLARE @languageIds cursor 

	SET @languageIds = CURSOR FOR
		SELECT ID FROM SYS_LANGUAGES
			
	OPEN @languageIds

	FETCH NEXT FROM @languageIds into @languageId

	WHILE (@@FETCH_STATUS = 0) 
	BEGIN 
		EXEC SET_SYS_STRING @sysStringId, @languageId, @description;
			
		FETCH NEXT FROM @languageIds into @languageId				
	END 

	CLOSE @languageIds
	DEALLOCATE @languageIds
GO

--FOR-PERMISSIONS

CREATE PROCEDURE SET_PERMISSION_DESCRIPTION
	@idName NVARCHAR(80),
	@langCode NVARCHAR(50),
	@description NVARCHAR(255)
AS
	PRINT 'SETTING PERMISSION DESCRIPTION '+@idName+'['+@langCode+'] = '+@description

	DECLARE @nameStrId INT = (SELECT NAMESTRID FROM SYS_PERMISSIONS WHERE IDNAME=@idName)
	IF (@nameStrId IS NULL) 
	BEGIN
		PRINT 'ERROR. PERMISSION NOT FOUND FOR CODE '+@idName
		RETURN 
	END

	DECLARE @langId INT = (SELECT ID FROM SYS_LANGUAGES WHERE CODE=@langCode)
	IF (@langId IS NULL) 
	BEGIN
		PRINT 'ERROR. LANGUAGE NOT FOUND FOR CODE '+@langCode
		RETURN 
	END
	
	EXEC SET_SYS_STRING @nameStrId, @langId, @description
GO

CREATE PROCEDURE SET_PERMISSION_DESCRIPTION_ALLANG
	@idName NVARCHAR(80),
	@description NVARCHAR(255)
AS
	PRINT ''
    PRINT 'SETTING_PERMISSION_DESCRIPTION '+@idName+' = '+@description
	DECLARE @nameStrId INT = (SELECT NAMESTRID FROM SYS_PERMISSIONS WHERE IDNAME=@idName)
	IF (@nameStrId IS NULL) 
	BEGIN
		PRINT 'ERROR. PERMISSION NOT FOUND FOR CODE '+@idName
		RETURN 
	END

	EXEC SET_SYS_STRING_ALLANG @nameStrId, @description
GO

CREATE PROCEDURE CREATE_PERMISSION
	@idname NVARCHAR(80),
	@description NVARCHAR(255)
AS
	PRINT ''
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
		
		DECLARE @ssid INT = @maxSysStringId+1;
		EXEC SET_SYS_STRING_ALLANG @ssid, @description;
		
		PRINT 'INSERTING PERMISSION INTO SYS_PERMISSIONS TABLE'
		
		INSERT 
		INTO SYS_PERMISSIONS(ID,IDNAME,NAMESTRID) 
		VALUES(@maxPermId+1, @idname, @maxSysStringId+1);
		
		PRINT 'GRANTING PERMISSION TO USER SA'
		
		INSERT
		INTO SYS_USER_PERMISSIONS(USERID,PERMISSIONID) 
		VALUES(@saUserId, @maxPermId+1);
		
		PRINT 'Permission created.'

		EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'SYS_PERMISSION_MAXID', 'SYS_PERMISSIONS'
		EXEC ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD 'SYS_STRING_MAXID', 'SYS_STRINGS'
	END ELSE
	IF (@count > 0)
	BEGIN
		PRINT 'ERROR. Permission already exists with same code.'
	END
GO

CREATE PROCEDURE DELETE_PERMISSION
	@idname NVARCHAR(80)
AS
	PRINT 'DELETING PERMISSION '+@idname;

	DECLARE @permissionId INT
	DECLARE @permissionIds cursor 
		
	SET @permissionIds = CURSOR FOR
		SELECT ID FROM SYS_PERMISSIONS WHERE IDNAME = @idname
			
	OPEN @permissionIds

	FETCH NEXT FROM @permissionIds into @permissionId

	WHILE (@@FETCH_STATUS = 0) 
	BEGIN 
		DELETE FROM SYS_ROLE_PERMISSIONS WHERE PERMISSIONID = @permissionId
		DELETE FROM SYS_USER_PERMISSIONS WHERE PERMISSIONID = @permissionId
		DELETE FROM SYS_STRINGS WHERE ID = (SELECT NAMESTRID FROM SYS_PERMISSIONS WHERE ID = @permissionId)
		DELETE FROM SYS_PERMISSIONS WHERE ID = @permissionId
		PRINT 'PERMISSION DELETED';
			
		FETCH NEXT FROM @permissionIds into @permissionId		
	END 

	CLOSE @permissionIds
	DEALLOCATE @permissionIds
GO

CREATE PROCEDURE COLLAPSE_PERMISSION
	@idname NVARCHAR(80)
AS
	PRINT 'COLLAPSING PERMISSION '+@idname;

	DECLARE @firstPermissionId INT
	DECLARE @permissionId INT
	DECLARE @permissionIds cursor 

	SET @permissionIds = CURSOR FOR
		SELECT ID FROM SYS_PERMISSIONS WHERE IDNAME = @idname
			
	OPEN @permissionIds

	FETCH NEXT FROM @permissionIds into @permissionId

	SET @firstPermissionId = @permissionId

	FETCH NEXT FROM @permissionIds into @permissionId

	WHILE (@@FETCH_STATUS = 0) 
	BEGIN 
		
		DECLARE @tmpId INT
		DECLARE @tmpIds cursor 

		-- DELETE ROLE DUPLICATE PERMISSIONS
		SET @tmpIds = CURSOR FOR
			SELECT ROLEID FROM SYS_ROLE_PERMISSIONS WHERE PERMISSIONID = @permissionId
		
		OPEN @tmpIds

		FETCH NEXT FROM @tmpIds into @tmpId

		WHILE (@@FETCH_STATUS = 0) 
		BEGIN 
			DELETE FROM SYS_ROLE_PERMISSIONS WHERE ROLEID = @tmpId AND PERMISSIONID in (@firstPermissionId, @permissionId)
			INSERT INTO SYS_ROLE_PERMISSIONS (ROLEID, PERMISSIONID) VALUES (@tmpId, @firstPermissionId)

			FETCH NEXT FROM @tmpIds into @tmpId
		END
	
		CLOSE @tmpIds
		DEALLOCATE @tmpIds

		-- DELETE USER DUPLICATE PERMISSIONS
		SET @tmpIds = CURSOR FOR
			SELECT USERID FROM SYS_USER_PERMISSIONS WHERE PERMISSIONID = @permissionId
		
		OPEN @tmpIds

		FETCH NEXT FROM @tmpIds into @tmpId

		WHILE (@@FETCH_STATUS = 0) 
		BEGIN 
			DELETE FROM SYS_USER_PERMISSIONS WHERE USERID = @tmpId AND PERMISSIONID in (@firstPermissionId, @permissionId)
			INSERT INTO SYS_USER_PERMISSIONS (USERID, PERMISSIONID) VALUES (@tmpId, @firstPermissionId)

			FETCH NEXT FROM @tmpIds into @tmpId
		END
		CLOSE @tmpIds
		DEALLOCATE @tmpIds
						
		DELETE FROM SYS_STRINGS WHERE ID = (SELECT NAMESTRID FROM SYS_PERMISSIONS WHERE ID = @permissionId)
		DELETE FROM SYS_PERMISSIONS WHERE ID = @permissionId
	END 

	CLOSE @permissionIds
	DEALLOCATE @permissionIds

GO

CREATE PROCEDURE COLLAPSE_PERMISSIONS
AS
	DECLARE @idname NVARCHAR(80)
	DECLARE @idnames cursor 
		
	SET @idnames = CURSOR FOR
		SELECT DISTINCT idname FROM SYS_PERMISSIONS
			
	OPEN @idnames

	FETCH NEXT FROM @idnames into @idname

	WHILE (@@FETCH_STATUS = 0) 
	BEGIN 
		DECLARE @count INT = 0
		SET @count = (SELECT COUNT(*) FROM SYS_PERMISSIONS WHERE IDNAME=@idname)
		IF (@count > 1)
		BEGIN
			EXEC COLLAPSE_PERMISSION @idname
		END

		FETCH NEXT FROM @idnames into @idname				
	END 

	CLOSE @idnames
	DEALLOCATE @idnames
GO

PRINT ''
PRINT '-COLLAPSING-EXISTING-PERMISSIONS-----------------'

EXEC COLLAPSE_PERMISSIONS
GO

PRINT ''
PRINT '-CREATING-PERMISSIONS----------------------------'

EXEC CREATE_PERMISSION N'fina2.web.admin', N'Web Admin'
EXEC CREATE_PERMISSION N'fina2.bank.amend', N'FI Amend'
EXEC CREATE_PERMISSION N'fina2.bank.delete', N'FI Delete'
EXEC CREATE_PERMISSION N'fina2.bank.review', N'FI Review'
EXEC CREATE_PERMISSION N'fina2.menu.amend', N'Menu Amend'
EXEC CREATE_PERMISSION N'fina2.metadata.amend', N'MetaData Tree Amend'
EXEC CREATE_PERMISSION N'fina2.metadata.delete', N'MetaData Tree Delete'
EXEC CREATE_PERMISSION N'fina2.metadata.review', N'MetaData Tree Review'
EXEC CREATE_PERMISSION N'fina2.periods.amend', N'Periods Amend'
EXEC CREATE_PERMISSION N'fina2.periods.delete', N'Periods Delete'
EXEC CREATE_PERMISSION N'fina2.periods.review', N'Periods Review'
EXEC CREATE_PERMISSION N'fina2.regCity.amend', N'Regions/Cities Management'
EXEC CREATE_PERMISSION N'fina2.report.amend', N'Reports Amend'
EXEC CREATE_PERMISSION N'fina2.report.generate', N'Reports Generate'
EXEC CREATE_PERMISSION N'fina2.report.scheduler', N'Report Scheduler'
EXEC CREATE_PERMISSION N'fina2.reports.scheduler.add', N'Reports Schedules Add'
EXEC CREATE_PERMISSION N'fina2.reports.scheduler.manager', N'Reports Scheduler Manager'
EXEC CREATE_PERMISSION N'fina2.reports.stored.manager', N'Stored Reports Manager'
EXEC CREATE_PERMISSION N'fina2.returns.accept', N'Returns Accept'
EXEC CREATE_PERMISSION N'fina2.returns.amend', N'Returns Amend'
EXEC CREATE_PERMISSION N'fina2.returns.definition.amend', N'Return Definition Amend'
EXEC CREATE_PERMISSION N'fina2.returns.definition.delete', N'Return Definition Delete'
EXEC CREATE_PERMISSION N'fina2.returns.definition.format', N'Return Definition Format'
EXEC CREATE_PERMISSION N'fina2.returns.definition.review', N'Return Definition Review'
EXEC CREATE_PERMISSION N'fina2.returns.delete', N'Returns Delete'
EXEC CREATE_PERMISSION N'fina2.returns.process', N'Returns Process'
EXEC CREATE_PERMISSION N'fina2.returns.reject', N'Returns Reject'
EXEC CREATE_PERMISSION N'fina2.returns.reset', N'Returns Reset'
EXEC CREATE_PERMISSION N'fina2.returns.review', N'Returns Review'
EXEC CREATE_PERMISSION N'fina2.returns.schedule.amend', N'Schedules Amend'
EXEC CREATE_PERMISSION N'fina2.returns.schedule.delete', N'Schedules Delete'
EXEC CREATE_PERMISSION N'fina2.returns.schedule.review', N'Schedules Review'
EXEC CREATE_PERMISSION N'fina2.returns.statuses', N'Return Statuses'
EXEC CREATE_PERMISSION N'fina2.returns.version.amend', N'Return Version Amend'
EXEC CREATE_PERMISSION N'fina2.returns.version.delete', N'Return Version Delete'
EXEC CREATE_PERMISSION N'fina2.returns.version.review', N'Return Version Review'
EXEC CREATE_PERMISSION N'fina2.security.amend', N'Users Amend'
EXEC CREATE_PERMISSION N'fina2.security.settings', N'Security Settings'
EXEC CREATE_PERMISSION N'fina2.stored.reports.delete', N'Stored Reports Delete'
EXEC CREATE_PERMISSION N'fina2.stored.reports.view', N'Stored Reports Review'
EXEC CREATE_PERMISSION N'fina2.web.external.user', N'External User'
EXEC CREATE_PERMISSION N'fina2.web.internal.user', N'Internal User'
EXEC CREATE_PERMISSION N'net.fina.auditTrailLog', N'Audit trail log'
EXEC CREATE_PERMISSION N'net.fina.calendar.amend', N'Calendar Amend'
EXEC CREATE_PERMISSION N'net.fina.calendar.delete', N'Calendar Delete'
EXEC CREATE_PERMISSION N'net.fina.calendar.review', N'Calendar Review'
EXEC CREATE_PERMISSION N'net.fina.dcs.fileUpload', N'DCS File Upload'
EXEC CREATE_PERMISSION N'net.fina.dcs.fileUpload.delete', N'DCS File Upload Delete'
EXEC CREATE_PERMISSION N'net.fina.dcs.manualInput', N'DCS Manual Input'
EXEC CREATE_PERMISSION N'net.fina.dcs.undefinedBank', N'DCS Undefined FI Review'
EXEC CREATE_PERMISSION N'net.fina.dcs.security.encrypt', N'DCS File Encrypt'
EXEC CREATE_PERMISSION N'net.fina.dcs.security.sign', N'DCS File Sign'
EXEC CREATE_PERMISSION N'net.fina.import.reImport', N'Import Manager Re-Import'
EXEC CREATE_PERMISSION N'net.fina.import.review', N'Import Manager Review'
EXEC CREATE_PERMISSION N'net.fina.import.upload', N'Import Manager Upload'
EXEC CREATE_PERMISSION N'net.fina.language.amend', N'Language Amend'
EXEC CREATE_PERMISSION N'net.fina.language.delete', N'Language Delete'
EXEC CREATE_PERMISSION N'net.fina.language.review', N'Language Review'
EXEC CREATE_PERMISSION N'net.fina.lictype.amend', N'License Type Amend'
EXEC CREATE_PERMISSION N'net.fina.lictype.delete', N'License Type Delete'
EXEC CREATE_PERMISSION N'net.fina.lictype.review', N'License Type Review'
EXEC CREATE_PERMISSION N'net.fina.query.builder.admin', N'Query-Builder Admin'
EXEC CREATE_PERMISSION N'net.fina.query.builder.user', N'Query-Builder User'
EXEC CREATE_PERMISSION N'net.fina.tool.auditTrailLog', N'Audit Trail Review'
EXEC CREATE_PERMISSION N'net.fina.tool.cacheManager', N'Cache Manager Tool'
EXEC CREATE_PERMISSION N'net.fina.tool.finaFileDecryption', N'FinA File Decryption'
EXEC CREATE_PERMISSION N'net.fina.tool.mailLog', N'Mail Log Review'
EXEC CREATE_PERMISSION N'net.fina.tool.mdtGenerator', N'MDT Generator Tool'
EXEC CREATE_PERMISSION N'net.fina.tool.mdtToXml', N'MDT to XML Tool'
EXEC CREATE_PERMISSION N'net.fina.tool.ostRelease', N'OST Release Tool'
EXEC CREATE_PERMISSION N'net.fina.tool.returnToXml', N'Return To XML Tool'
EXEC CREATE_PERMISSION N'net.fina.user.amend', N'User Manager Amend'
EXEC CREATE_PERMISSION N'net.fina.user.delete', N'User Manager Delete'
EXEC CREATE_PERMISSION N'net.fina.user.review', N'User Manager Review'

GO

PRINT ''
PRINT '-RENAMING-PERMISSIONS----------------------------'

EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.web.admin', N'Web Admin'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.bank.amend', N'FI Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.bank.delete', N'FI Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.bank.review', N'FI Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.menu.amend', N'Menu Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.metadata.amend', N'MetaData Tree Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.metadata.delete', N'MetaData Tree Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.metadata.review', N'MetaData Tree Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.periods.amend', N'Periods Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.periods.delete', N'Periods Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.periods.review', N'Periods Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.regCity.amend', N'Regions/Cities Management'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.report.amend', N'Reports Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.report.generate', N'Reports Generate'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.report.scheduler', N'Report Scheduler'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.reports.scheduler.add', N'Reports Schedules Add'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.reports.scheduler.manager', N'Reports Scheduler Manager'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.reports.stored.manager', N'Stored Reports Manager'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.accept', N'Returns Accept'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.amend', N'Returns Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.definition.amend', N'Return Definition Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.definition.delete', N'Return Definition Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.definition.format', N'Return Definition Format'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.definition.review', N'Return Definition Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.delete', N'Returns Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.process', N'Returns Process'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.reject', N'Returns Reject'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.reset', N'Returns Reset'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.review', N'Returns Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.schedule.amend', N'Schedules Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.schedule.delete', N'Schedules Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.schedule.review', N'Schedules Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.statuses', N'Return Statuses'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.version.amend', N'Return Version Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.version.delete', N'Return Version Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.returns.version.review', N'Return Version Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.security.amend', N'Users Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.security.settings', N'Security Settings'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.stored.reports.delete', N'Stored Reports Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.stored.reports.view', N'Stored Reports Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.web.external.user', N'External User'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'fina2.web.internal.user', N'Internal User'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.auditTrailLog', N'Audit trail log'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.calendar.amend', N'Calendar Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.calendar.delete', N'Calendar Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.calendar.review', N'Calendar Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.dcs.fileUpload', N'DCS File Upload'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.dcs.fileUpload.delete', N'DCS File Upload Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.dcs.manualInput', N'DCS Manual Input'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.dcs.undefinedBank', N'DCS Undefined FI Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.dcs.security.encrypt', N'DCS File Encrypt'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.dcs.security.sign', N'DCS File Sign'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.import.reImport', N'Import Manager Re-Import'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.import.review', N'Import Manager Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.import.upload', N'Import Manager Upload'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.language.amend', N'Language Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.language.delete', N'Language Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.language.review', N'Language Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.lictype.amend', N'License Type Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.lictype.delete', N'License Type Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.lictype.review', N'License Type Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.query.builder.admin', N'Query-Builder Admin'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.query.builder.user', N'Query-Builder User'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.tool.auditTrailLog', N'Audit Trail Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.tool.cacheManager', N'Cache Manager Tool'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.tool.finaFileDecryption', N'FinA File Decryption'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.tool.mailLog', N'Mail Log Review'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.tool.mdtGenerator', N'MDT Generator Tool'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.tool.mdtToXml', N'MDT to XML Tool'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.tool.ostRelease', N'OST Release Tool'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.tool.returnToXml', N'Return To XML Tool'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.user.amend', N'User Manager Amend'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.user.delete', N'User Manager Delete'
EXEC SET_PERMISSION_DESCRIPTION_ALLANG N'net.fina.user.review', N'User Manager Review'

--EXEC SET_PERMISSION_DESCRIPTION N'net.fina.permission.name', N'mn_MN', N'Permission Description'
GO

PRINT ''
PRINT '-REMOVING-PERMISSIONS----------------------------'

--EXEC DELETE_PERMISSION N'fina2.web.user'
--EXEC DELETE_PERMISSION N'fina2.web.admin'

GO

PRINT ''
PRINT '-DROPPING-PROCEDURES-----------------------------'

DROP PROCEDURE CREATE_PERMISSION

DROP PROCEDURE DELETE_PERMISSION

DROP PROCEDURE COLLAPSE_PERMISSIONS

DROP PROCEDURE COLLAPSE_PERMISSION

DROP PROCEDURE SET_SYS_STRING

DROP PROCEDURE SET_SYS_STRING_ALLANG

DROP PROCEDURE ADD_OR_UPDATE_SYS_ID_GENERATOR_TABLE_RECORD

DROP PROCEDURE SET_PERMISSION_DESCRIPTION

DROP PROCEDURE SET_PERMISSION_DESCRIPTION_ALLANG

GO

PRINT ''
PRINT '-SCRIPT-FINISHED---------------------------------'
GO
