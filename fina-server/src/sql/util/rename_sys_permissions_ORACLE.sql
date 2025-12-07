/*
 Database: Oracle 11g
 
 Author: Alexander Dolidze
 E: sasha@fina2.net
 Version: 0.1
 Date : 14/11/2016
*/

DECLARE 

	-- GENERAL PURPOSE PROCEDURES
	
	-- Prints one VARCHAR2 string to console.
	PROCEDURE putLine (line IN VARCHAR2 DEFAULT '')
	IS
	BEGIN
		DBMS_OUTPUT.PUT_LINE(line);
	END;
  
  PROCEDURE SYS_STRING_SET(sysStringId IN INT, languageId IN INT, description IN VARCHAR2)
  AS
  BEGIN
    putLine('SYS_STRING_SET('||sysStringId||', '||languageId||', '''||description||''');');
    DELETE FROM SYS_STRINGS WHERE ID=sysStringId and LANGID=languageId;
    INSERT INTO SYS_STRINGS (ID, LANGID, VALUE) VALUES (sysStringId, languageId, description);
  END;
  
  PROCEDURE SYS_STRING_ALLANG_SET(sysStringId IN INT, description IN VARCHAR2)
  AS
    langId INT;
    CURSOR langIdsCursor IS SELECT ID FROM SYS_LANGUAGES;
  BEGIN
    putLine('SYS_STRING_ALLANG_SET('||sysStringId||', '''||description||''');');
    FOR langId in langIdsCursor LOOP
      SYS_STRING_SET(sysStringID, langId.id, description);
		END LOOP;
  END;

  PROCEDURE PERM_DESC_SET(permissionIdName VARCHAR2, langCode VARCHAR2, description VARCHAR2)
  AS
    nameStrId INT;
    langId INT;
  BEGIN
    putLine('PERM_DESC_SET('||permissionIdName||', '''||description||''');');
    
    FOR nameStrId IN (SELECT NAMESTRID FROM SYS_PERMISSIONS WHERE IDNAME=permissionIdName)
    LOOP    
      SELECT ID INTO langId FROM SYS_LANGUAGES WHERE CODE=langCode;
      IF langId is null THEN
        putLine('ERROR. LANGUAGE NOT FOUND FOR CODE '||langCode);
        RETURN;
      END IF;
      
      SYS_STRING_SET(nameStrId.NAMESTRID, langId, description);
    END LOOP;
  END;
  
  PROCEDURE PERM_DESC_ALLANG_SET(permIdName VARCHAR2, description VARCHAR2)
  AS
    nameStringId INT;
  BEGIN
    putLine('PERM_DESC_ALLANG_SET('||permIdName||', '''||description||''');');
    
    FOR nameStringId IN (SELECT NAMESTRID FROM SYS_PERMISSIONS WHERE IDNAME=permIdName)
    LOOP      
      SYS_STRING_ALLANG_SET(nameStringId.NAMESTRID, description);
    END LOOP;
    putLine();
    putLine();
  END;
BEGIN

	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');
	putLine('-CREATED-PROCEDURES-AND-FUNCTIONS-----------------------');
	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');

  putLine();
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('-RENAMING-PERMISSIONS-------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');

  PERM_DESC_ALLANG_SET('fina2.web.admin', 'Web Admin');
  PERM_DESC_ALLANG_SET('fina2.bank.amend', 'FI Amend');
  PERM_DESC_ALLANG_SET('fina2.bank.delete', 'FI Delete');
  PERM_DESC_ALLANG_SET('fina2.bank.review', 'FI Review');
  PERM_DESC_ALLANG_SET('fina2.menu.amend', 'Menu Amend');
  PERM_DESC_ALLANG_SET('fina2.metadata.amend', 'MetaData Tree Amend');
  PERM_DESC_ALLANG_SET('fina2.metadata.delete', 'MetaData Tree Delete');
  PERM_DESC_ALLANG_SET('fina2.metadata.review', 'MetaData Tree Review');
  PERM_DESC_ALLANG_SET('fina2.periods.amend', 'Periods Amend');
  PERM_DESC_ALLANG_SET('fina2.periods.delete', 'Periods Delete');
  PERM_DESC_ALLANG_SET('fina2.periods.review', 'Periods Review');
  PERM_DESC_ALLANG_SET('fina2.regCity.amend', 'Regions/Cities Management');
  PERM_DESC_ALLANG_SET('fina2.report.amend', 'Reports Amend');
  PERM_DESC_ALLANG_SET('fina2.report.generate', 'Reports Generate');
  PERM_DESC_ALLANG_SET('fina2.report.scheduler', 'Report Scheduler');
  PERM_DESC_ALLANG_SET('fina2.reports.scheduler.add', 'Reports Schedules Add');
  PERM_DESC_ALLANG_SET('fina2.reports.scheduler.manager', 'Reports Scheduler Manager');
  PERM_DESC_ALLANG_SET('fina2.reports.stored.manager', 'Stored Reports Manager');
  PERM_DESC_ALLANG_SET('fina2.returns.accept', 'Returns Accept');
  PERM_DESC_ALLANG_SET('fina2.returns.amend', 'Returns Amend');
  PERM_DESC_ALLANG_SET('fina2.returns.definition.amend', 'Return Definition Amend');
  PERM_DESC_ALLANG_SET('fina2.returns.definition.delete', 'Return Definition Delete');
  PERM_DESC_ALLANG_SET('fina2.returns.definition.format', 'Return Definition Format');
  PERM_DESC_ALLANG_SET('fina2.returns.definition.review', 'Return Definition Review');
  PERM_DESC_ALLANG_SET('fina2.returns.delete', 'Returns Delete');
  PERM_DESC_ALLANG_SET('fina2.returns.process', 'Returns Process');
  PERM_DESC_ALLANG_SET('fina2.returns.reject', 'Returns Reject');
  PERM_DESC_ALLANG_SET('fina2.returns.reset', 'Returns Reset');
  PERM_DESC_ALLANG_SET('fina2.returns.review', 'Returns Review');
  PERM_DESC_ALLANG_SET('fina2.returns.schedule.amend', 'Schedules Amend');
  PERM_DESC_ALLANG_SET('fina2.returns.schedule.delete', 'Schedules Delete');
  PERM_DESC_ALLANG_SET('fina2.returns.schedule.review', 'Schedules Review');
  PERM_DESC_ALLANG_SET('fina2.returns.statuses', 'Return Statuses');
  PERM_DESC_ALLANG_SET('fina2.returns.version.amend', 'Return Version Amend');
  PERM_DESC_ALLANG_SET('fina2.returns.version.delete', 'Return Version Delete');
  PERM_DESC_ALLANG_SET('fina2.returns.version.review', 'Return Version Review');
  PERM_DESC_ALLANG_SET('fina2.security.amend', 'Users Amend');
  PERM_DESC_ALLANG_SET('fina2.security.settings', 'Security Settings');
  PERM_DESC_ALLANG_SET('fina2.stored.reports.delete', 'Stored Reports Delete');
  PERM_DESC_ALLANG_SET('fina2.stored.reports.view', 'Stored Reports Review');
  PERM_DESC_ALLANG_SET('fina2.web.external.user', 'External User');
  PERM_DESC_ALLANG_SET('fina2.web.internal.user', 'Internal User');
  PERM_DESC_ALLANG_SET('net.fina.auditTrailLog', 'Audit trail log');
  PERM_DESC_ALLANG_SET('net.fina.calendar.amend', 'Calendar Amend');
  PERM_DESC_ALLANG_SET('net.fina.calendar.delete', 'Calendar Delete');
  PERM_DESC_ALLANG_SET('net.fina.calendar.review', 'Calendar Review');
  PERM_DESC_ALLANG_SET('net.fina.dcs.fileUpload', 'DCS File Upload');
  PERM_DESC_ALLANG_SET('net.fina.dcs.fileUpload.delete', 'DCS File Upload Delete');
  PERM_DESC_ALLANG_SET('net.fina.dcs.manualInput', 'DCS Manual Input');
  PERM_DESC_ALLANG_SET('net.fina.dcs.undefinedBank', 'DCS Undefined FI Review');
  PERM_DESC_ALLANG_SET('net.fina.dcs.security.encrypt', 'DCS File Encrypt');
  PERM_DESC_ALLANG_SET('net.fina.dcs.security.sign', 'DCS File Sign');
  PERM_DESC_ALLANG_SET('net.fina.import.reImport', 'Import Manager Re-Import');
  PERM_DESC_ALLANG_SET('net.fina.import.review', 'Import Manager Review');
  PERM_DESC_ALLANG_SET('net.fina.import.upload', 'Import Manager Upload');
  PERM_DESC_ALLANG_SET('net.fina.language.amend', 'Language Amend');
  PERM_DESC_ALLANG_SET('net.fina.language.delete', 'Language Delete');
  PERM_DESC_ALLANG_SET('net.fina.language.review', 'Language Review');
  PERM_DESC_ALLANG_SET('net.fina.lictype.amend', 'License Type Amend');
  PERM_DESC_ALLANG_SET('net.fina.lictype.delete', 'License Type Delete');
  PERM_DESC_ALLANG_SET('net.fina.lictype.review', 'License Type Review');
  PERM_DESC_ALLANG_SET('net.fina.query.builder.admin', 'Query-Builder Admin');
  PERM_DESC_ALLANG_SET('net.fina.query.builder.user', 'Query-Builder User');
  PERM_DESC_ALLANG_SET('net.fina.tool.auditTrailLog', 'Audit Trail Review');
  PERM_DESC_ALLANG_SET('net.fina.tool.cacheManager', 'Cache Manager Tool');
  PERM_DESC_ALLANG_SET('net.fina.tool.finaFileDecryption', 'FinA File Decryption');
  PERM_DESC_ALLANG_SET('net.fina.tool.mailLog', 'Mail Log Review');
  PERM_DESC_ALLANG_SET('net.fina.tool.mdtGenerator', 'MDT Generator Tool');
  PERM_DESC_ALLANG_SET('net.fina.tool.mdtToXml', 'MDT to XML Tool');
  PERM_DESC_ALLANG_SET('net.fina.tool.ostRelease', 'OST Release Tool');
  PERM_DESC_ALLANG_SET('net.fina.tool.returnToXml', 'Return To XML Tool');
  PERM_DESC_ALLANG_SET('net.fina.user.amend', 'User Manager Amend');
  PERM_DESC_ALLANG_SET('net.fina.user.delete', 'User Manager Delete');
  PERM_DESC_ALLANG_SET('net.fina.user.review', 'User Manager Review');


  putLine();
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('-SCRIPT-FINISHED----------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
commit;
END;