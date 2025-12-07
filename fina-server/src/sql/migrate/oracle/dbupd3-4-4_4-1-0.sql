/*
 Database: Oracle 11g
 
 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 0.1
 Date : 15/11/2012
 
 Author: Alexander Dolidze
 E: sasha@fina2.net
 Version: 0.2
 Date : 10/12/2012
*/

/*
  Update DB Version
*/
update sys_properties set value='4.1.0' where prop_key='fina2.database.schemaVersion';

DROP TABlE SYS_ID_GENERATOR;

CREATE TABLE SYS_ID_GENERATOR (
        PK_COLUMN_NAME VARCHAR2(255) NOT NULL,
        VALUE NUMBER(19),
        PRIMARY KEY(PK_COLUMN_NAME)
      );


DECLARE 

	-- GENERAL PURPOSE PROCEDURES
	
	-- Prints one VARCHAR2 string to console.
	PROCEDURE putLine (line IN VARCHAR2 DEFAULT '')
	IS
	BEGIN
		DBMS_OUTPUT.PUT_LINE(line);
	END;
	
	PROCEDURE putSomeSpace
	IS
	BEGIN
    putLine();
	--putLine('-----------------------------------------');
    --putLine();
	END;
	
	PROCEDURE sayTableName (name IN VARCHAR2)
	IS
	BEGIN
		putLine('Table: '||name);
	END;

	PROCEDURE sayColumnName (name IN VARCHAR2)
	IS
	BEGIN
		putLine('Column: '||name);
	END;
	
	PROCEDURE sayConstraintName (name IN VARCHAR2)
	IS
	BEGIN
		putLine('Constraint: '||name);
	END;
	
	PROCEDURE sayTriggerDropped (name IN VARCHAR2)
	IS
	BEGIN
	  putLine('Trigger dropped: '||name);    
	END;
	
	PROCEDURE sayTriggerCreated (name IN VARCHAR2)
	IS
	BEGIN
	  putLine('Trigger created: '||name); 
	END;

	PROCEDURE sayIfExists (exist IN BOOLEAN)
	IS
	BEGIN
		IF exist THEN
			putLine('Exists.');
		ELSE
			putLine('Doesn''t exist.');
		END IF;
	END;
	
	-- TABLE RELATED PROCEDURES / FUNCTIONS
	
	PROCEDURE sayTableCreated (name IN VARCHAR2)
	IS
	BEGIN
		putLine('Table '''||name||''' created.');
	END;
	
	FUNCTION checkIfTableExists(name IN VARCHAR2)
	RETURN BOOLEAN
	IS
		rowCount INT;
		retVal BOOLEAN;
	BEGIN
		putSomeSpace();
		putLine('checkIfTableExists('||name||');');
		sayTableName(name);
		SELECT COUNT(*) INTO rowCount FROM USER_TABLES WHERE TABLE_NAME=UPPER(name);
		IF rowCount > 0 THEN
			retVal := TRUE;
		ELSE
			retVal := FALSE;
		END IF;
		sayIfExists(retVal);
		RETURN retVal;
	END;

	-- COLUMN RELATED PROCEDURES / FUNCTIONS

	FUNCTION checkIfColumnExists (tableName IN VARCHAR2, columnName IN VARCHAR2)
	RETURN BOOLEAN
	IS
		rowCount INT;
		retVal BOOLEAN;
	BEGIN
		putSomeSpace();
		putLine('checkIfColumnExists('||tableName||', '||columnName||');');
		sayTableName(tableName);
		sayColumnName(columnName);
		
		SELECT COUNT(*) INTO rowCount 
		FROM USER_TAB_COLS 
		WHERE TABLE_NAME=UPPER(tableName) 
		AND COLUMN_NAME=UPPER(columnName);
		
		IF rowCount > 0 THEN
			retVal := TRUE;
		ELSE
			retVal := FALSE;
		END IF;
		
		sayIfExists(retVal);
		RETURN retVal;
	END;

	PROCEDURE sayColumnCreated (tableName IN VARCHAR2, columnName IN VARCHAR2)
	IS
	BEGIN
		putLine('Column: '''||columnName||''' in Table: '''||tableName||''' created.');
	END;
	
	-- CONSTRAINT RELATED PROCEDURES / FUNCTIONS

	FUNCTION checkIfConstraintExists (constraintName IN VARCHAR2)
  RETURN BOOLEAN
	IS
		rowCount INT;
		retVal BOOLEAN;
	BEGIN
		putSomeSpace();
		putLine('checkIfConstraintExists('||constraintName||');');
		sayConstraintName(constraintName);
				
		SELECT COUNT(*) INTO rowCount
		FROM USER_CONSTRAINTS
		WHERE CONSTRAINT_NAME=constraintName;
		
		IF rowCount > 0 THEN
			retVal := TRUE;
		ELSE
			retVal := FALSE;
		END IF;
		
		sayIfExists(retVal);
		RETURN retVal;	
	END;

	-- FINA PERMISSIONS RELATED PROCEDURES / FUNCTIONS

	FUNCTION checkIfPermissionExists(permissionIdName IN VARCHAR2)
	RETURN BOOLEAN
	IS
		rowCount INT;
		retVal BOOLEAN;
	BEGIN
		putLine('checkIfPermissionExists('||permissionIdName||');');
    SELECT COUNT(*) INTO rowCount FROM SYS_PERMISSIONS WHERE IDNAME = permissionIdName;
    IF rowCount > 0 THEN
      retVal:=TRUE;
    ELSE
      retVal:=FALSE;
    END IF;
		sayIfExists(retVal);
		RETURN retVal;
	END;
	
  /*  
    Creates permission with given IdName and Description if permission with same IdName cannot be found in the system.
    Grants the created permission to user 'sa'.
  */
	PROCEDURE createPermissionIfNotExists(permissionIdName IN VARCHAR2, permissionDescription IN VARCHAR2)
	IS
		PERM_ALRDY_EXISTS EXCEPTION;
		
		saUserId INT;
		maxPermId INT;
		maxSysStringId INT;
		langId INT;
    
    CURSOR langIdsCursor
		IS
			SELECT ID FROM SYS_LANGUAGES;
	BEGIN
		putSomeSpace();
		putLine('createPermissionIfNotExists('||permissionIdName||', '||permissionDescription||');');
		IF checkIfPermissionExists(permissionIdName) THEN
			RAISE PERM_ALRDY_EXISTS;
		END IF;
		
		SELECT ID INTO saUserId FROM SYS_USERS WHERE LOWER(LTRIM(RTRIM(LOGIN)))='sa';
		SELECT MAX(ID) INTO maxPermId FROM SYS_PERMISSIONS;
		SELECT MAX(ID) INTO maxSysStringId FROM SYS_STRINGS;
		
		putLine('INSERTING PERMISSION DESCRIPTION INTO SYS_STRINGS TABLE');
		
		FOR langId in langIdsCursor
		LOOP
			INSERT 
			INTO SYS_STRINGS(ID, LANGID, VALUE)
			VALUES (maxSysStringId+1, langId.id, permissionDescription);
		END LOOP;
			
		putLine('INSERTING PERMISSION INTO SYS_PERMISSIONS TABLE');
		INSERT 
		INTO SYS_PERMISSIONS(ID, IDNAME, NAMESTRID)
		VALUES (maxPermId+1, permissionIdname, maxSysStringId+1);
		
		putLine('GRANTING PERMISSION TO USER SA');
		INSERT
		INTO SYS_USER_PERMISSIONS (USERID, PERMISSIONID)
		VALUES (saUserId, maxPermId+1);
		
		putLine('Permission created.');		
	EXCEPTION
		WHEN PERM_ALRDY_EXISTS THEN
			putLine('Permission already exists.');
			-- DO NOTHING. JUST CATCH IT.
	END;

	-- SYS_ID_GENERATOR ITEMS INSERT PROCEDUREs / FUNCTIONS
	PROCEDURE addSysIdGenRec(pkColumnName IN VARCHAR2, tableName IN VARCHAR2)
	IS
		rowCount INT;
		maxId INT;
	BEGIN  
		SELECT COUNT(*) INTO rowCount FROM SYS_ID_GENERATOR WHERE PK_COLUMN_NAME=pkColumnName;
		IF rowCount = 0 THEN
			INSERT 
			INTO SYS_ID_GENERATOR (PK_COLUMN_NAME, VALUE)
			VALUES (pkColumnName, 1);
      
			putLine('INSERTED row ('||pkColumnName||',1) into SYS_ID_GENERATOR table.');
		END IF;
		
		maxId := 0;
    execute immediate 'select max(ID) from '||tableName into maxId;
    
		UPDATE SYS_ID_GENERATOR SET VALUE=maxId+1 WHERE PK_COLUMN_NAME=pkColumnName;
		
		putLine('Updated '||pkColumnName||' in SYS_ID_GENERATOR table. New value is '||CAST(maxId+1 AS VARCHAR)||'.');
	END;

  -- TRIGGERS RELATED PROCEDURES / FUNCTIONS

	FUNCTION checkIfTriggerExists(triggerName IN VARCHAR2)
	RETURN BOOLEAN
	IS
		rowCount INT;
		retVal BOOLEAN;
	BEGIN
		putLine('checkIfTriggerExists('||UPPER(triggerName)||');');
    SELECT COUNT(*) INTO rowCount FROM USER_OBJECTS WHERE OBJECT_TYPE = 'TRIGGER' and OBJECT_NAME = UPPER(triggerName);
    IF rowCount > 0 THEN
      retVal:=TRUE;
    ELSE
      retVal:=FALSE;
    END IF;
		sayIfExists(retVal);
		RETURN retVal;
	END;
  
	PROCEDURE dropTrigger(triggerName IN VARCHAR2)
	IS
	BEGIN
	  putSomeSpace();
	  IF checkIfTriggerExists(UPPER(triggerName))  THEN
	     EXECUTE IMMEDIATE 'DROP TRIGGER '||UPPER(triggerName);
	     sayTriggerDropped(UPPER(triggerName));
	  END IF;
	END;

BEGIN

	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');
	putLine('-CREATED-PROCEDURES-AND-FUNCTIONS-----------------------');
	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');
	
	putSomeSpace();
	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');
	putLine('-CREATING-TABLES----------------------------------------');
	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');
	
	/*
		IN_MAIL_MESSAGE
	*/
	IF NOT checkIfTableExists('IN_MAIL_MESSAGE') THEN
		EXECUTE IMMEDIATE 'CREATE TABLE IN_MAIL_MESSAGE(
			ID NUMBER(10,0) NOT NULL,
			ADDRESS VARCHAR2(255 BYTE) NULL,
			FROM_ADDRESS VARCHAR2(255 BYTE) NULL,
			MAIL_USER VARCHAR2(255 BYTE) NULL,
			MESSAGE_ID VARCHAR2(512 BYTE) NULL,
			NOTE VARCHAR2(2000 BYTE) NULL,
			RECIVE_DATE DATE NULL,
			STATUS int NULL,
			MESSAGE_REPLAY_ID NUMBER(10,0) NULL,
			READ_DATE DATE NULL
		 )';
     sayTableCreated('IN_MAIL_MESSAGE');
	END IF;

	/*
		IN_MAIL_MESSAGE_UPLOADFILES
	*/
	IF NOT checkIfTableExists('IN_MAIL_MESSAGE_UPLOADFILES') THEN
		EXECUTE IMMEDIATE 'CREATE TABLE IN_MAIL_MESSAGE_UPLOADFILES(
			IN_MAIL_MESSAGE_ID NUMBER(10,0) NOT NULL,
			uploadFiles_ID NUMBER(10,0) NOT NULL
		)';
    sayTableCreated('IN_MAIL_MESSAGE_UPLOADFILES');
	END IF;
	
	/*
		OUT_MAIL_MESSAGE_REPLAY
	*/
	IF NOT checkIfTableExists('OUT_MAIL_MESSAGE_REPLY') THEN
		EXECUTE IMMEDIATE 'CREATE TABLE OUT_MAIL_MESSAGE_REPLY(
			ID NUMBER(10,0) NOT NULL,
			MAIL_BCC VARCHAR2(2000 BYTE) NULL,
			MAIL_CC VARCHAR2(2000 BYTE) NULL,
			MAIL_CONTENT VARCHAR2(2000 BYTE) NULL,
			MAIL_DATE DATE NOT NULL,
			MAIL_FROM VARCHAR2(255 BYTE) NOT NULL,
			MAIL_SEND_STATUS int NOT NULL,
			MAIL_SENDER VARCHAR2(255 BYTE) NOT NULL,
			MAIL_TO VARCHAR2(512 BYTE) NOT NULL
		)';
    sayTableCreated('OUT_MAIL_MESSAGE_REPLY');
	END IF;
  
  /*
    SYS_ID_GENERATOR
  */
--  IF NOT checkIfTableExists('SYS_ID_GENERATOR') THEN
--    EXECUTE IMMEDIATE 'CREATE TABLE SYS_ID_GENERATOR (
--      PK_COLUMN_NAME VARCHAR2(255) NOT NULL,
--      VALUE NUMBER(19),
--      PRIMARY KEY(PK_COLUMN_NAME)
--    )';
--    sayTableCreated('SYS_ID_GENERATOR');
--  END IF;

  /*
    SYS_USER_STATES
  */
  IF NOT checkIfTableExists('SYS_USER_STATES') THEN
    EXECUTE IMMEDIATE 'CREATE TABLE SYS_USER_STATES(
      id numeric primary key not null,
      user_id numeric,
      code varchar(100) not null,
      value CLOB
    )';
    sayTableCreated('SYS_USER_STATES');
  END IF;

	putSomeSpace();
	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');
	putLine('-ADDING-COLUMNS-----------------------------------------');
	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');
	
	IF NOT checkIfColumnExists('IN_PERIOD_TYPES', 'periodType') THEN
		EXECUTE IMMEDIATE 'ALTER TABLE IN_PERIOD_TYPES ADD periodType INT';
		sayColumnCreated('IN_PERIOD_TYPES', 'periodType');
	END IF;

	IF NOT checkIfColumnExists('OUT_STORED_REPORTS', 'reportHtmlResult') THEN
		EXECUTE IMMEDIATE 'ALTER TABLE OUT_STORED_REPORTS ADD reportHtmlResult CLOB';
		sayColumnCreated('OUT_STORED_REPORTS', 'reportHtmlResult');
	END IF;

	IF NOT checkIfColumnExists('IN_BANK_TYPES', 'version') THEN
		EXECUTE IMMEDIATE 'ALTER TABLE IN_BANK_TYPES ADD version INT';
		sayColumnCreated('IN_BANK_TYPES', 'version');
	END IF;
	
	putSomeSpace();
	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');
	putLine('-ADDING-OPTLOCK-COLUMNS---------------------------------');
	putLine('--------------------------------------------------------');
	putLine('--------------------------------------------------------');
	
	DECLARE
		CURSOR optlockTablesCursor
		IS
			SELECT DISTINCT(TABLE_NAME) FROM USER_TABLES
			WHERE
				TABLE_NAME in (
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
				);
		optlockColumnName VARCHAR2(255) := 'optlock';
		exist BOOLEAN;
	BEGIN
		FOR optlockTable in optlockTablesCursor
		LOOP
			exist := checkIfColumnExists(optlockTable.TABLE_NAME, optlockColumnName);
			IF NOT exist THEN
				EXECUTE IMMEDIATE 'ALTER TABLE '||optlockTable.TABLE_NAME||' ADD '||optlockColumnName||' INT DEFAULT 0';
				sayColumnCreated(optlockTable.TABLE_NAME, optlockColumnName);
			END IF;
			EXECUTE IMMEDIATE 'UPDATE '||optlockTable.TABLE_NAME||' SET '||optlockColumnName||'=0 WHERE '||optlockColumnName||' IS NULL';
		END LOOP;
	END;

  putSomeSpace();
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('-DROPPING-CONSTRAINTS-----------------------------------');
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');

--TODO

  putSomeSpace();
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('-ADDING-PERMISSIONS-------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');

  putSomeSpace();
  createPermissionIfNotExists('fina2.web.user', 'Web Portal User');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.regCity.amend', 'Regions/Cities Management');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.web.internal.user', 'Internal User');
  createPermissionIfNotExists('fina2.web.external.user', 'External user');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.stored.reports.view', 'Stored Reports Review');
  createPermissionIfNotExists('fina2.stored.reports.delete', 'Stored Reports Delete');
  --
  putSomeSpace();
  createPermissionIfNotExists('net.fina.language.review', 'Language Review');
  createPermissionIfNotExists('net.fina.language.amend', 'Language Amend');
  createPermissionIfNotExists('net.fina.language.delete', 'Language Delete');
  --
  putSomeSpace();
  createPermissionIfNotExists('net.fina.user.review', 'User Manager Review');
  createPermissionIfNotExists('net.fina.user.amend', 'User Manager Amend');
  createPermissionIfNotExists('net.fina.user.delete', 'User Manager Delete');
  --
  putSomeSpace();
  createPermissionIfNotExists('net.fina.lictype.review', 'License Type Review');
  createPermissionIfNotExists('net.fina.lictype.amend', 'License Type Amend');
  createPermissionIfNotExists('net.fina.lictype.delete', 'License Type Delete');
  --
  putSomeSpace();
  createPermissionIfNotExists('net.fina.dcs.fileUpload', 'DCS File Upload');
  createPermissionIfNotExists('net.fina.dcs.fileUpload.delete', 'DCS File Upload Delete');
  createPermissionIfNotExists('net.fina.dcs.manualInput', 'Manual Input');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.metadata.review', 'MetaData Tree Review');
  createPermissionIfNotExists('fina2.metadata.amend', 'MetaData Tree Amend');
  createPermissionIfNotExists('fina2.metadata.delete', 'MetaData Tree Delete');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.bank.review', 'FI Review');
  createPermissionIfNotExists('fina2.bank.amend', 'FI Amend');
  createPermissionIfNotExists('fina2.bank.delete', 'FI Delete');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.returns.definition.review', 'Return Definition Review');
  createPermissionIfNotExists('fina2.returns.definition.amend', 'Return Definition Amend');
  createPermissionIfNotExists('fina2.returns.definition.format', 'Return Definition Format');
  createPermissionIfNotExists('fina2.returns.definition.delete', 'Return Definition Delete');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.menu.amend', 'Menu Amend');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.security.settings', 'Security Settings');
  createPermissionIfNotExists('fina2.security.amend', 'Users Amend'); ----------------------------------- WTF PERM?
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.returns.schedule.review', 'Schedules Review');
  createPermissionIfNotExists('fina2.returns.schedule.amend', 'Schedules Amend');
  createPermissionIfNotExists('fina2.returns.schedule.delete', 'Schedules Delete');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.returns.review', 'Returns Review');
  createPermissionIfNotExists('fina2.returns.amend', 'Returns Amend');
  createPermissionIfNotExists('fina2.returns.process', 'Returns Process');
  createPermissionIfNotExists('fina2.returns.delete', 'Returns Delete');
  createPermissionIfNotExists('fina2.returns.accept', 'Returns Accept');
  createPermissionIfNotExists('fina2.returns.reset', 'Returns Reset');
  createPermissionIfNotExists('fina2.returns.reject', 'Returns Reject');
  createPermissionIfNotExists('fina2.returns.statuses', 'Returns Statuses');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.periods.review', 'Periods Review');
  createPermissionIfNotExists('fina2.periods.amend', 'Periods Amend');
  createPermissionIfNotExists('fina2.periods.delete', 'Periods Delete');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.report.amend', 'Reports Amend');
  createPermissionIfNotExists('fina2.report.generate', 'Reports Generate');  
  createPermissionIfNotExists('fina2.report.scheduler', 'Report Scheduler');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.reports.scheduler.manager', 'Reports Scheduler Manager');
  createPermissionIfNotExists('fina2.reports.scheduler.add', 'Reports Scheduler Add');
  createPermissionIfNotExists('fina2.reports.stored.manager', 'Stored Reports Manager');
  --
  putSomeSpace();
  createPermissionIfNotExists('fina2.returns.version.review', 'Return Version Review');
  createPermissionIfNotExists('fina2.returns.version.amend', 'Return Version Amend');
  createPermissionIfNotExists('fina2.returns.version.delete', 'Return Version Delete');
  
  putSomeSpace();
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('-FILLING-SYS_ID_GENERATOR-TABLE-------------------------');
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  
  DELETE FROM SYS_ID_GENERATOR;
  addSysIdGenRec('IN_CRITERION_MAXID', 'IN_CRITERION');
  addSysIdGenRec('IN_BANKS_MAXID', 'IN_BANKS');
  addSysIdGenRec('IN_BANK_BRANCHES_MAXID', 'IN_BANK_BRANCHES');
  addSysIdGenRec('IN_BANK_MANAGEMENT_MAXID', 'IN_BANK_MANAGEMENT');
  addSysIdGenRec('IN_BANK_TYPES_MAXID', 'IN_BANK_TYPES');
  addSysIdGenRec('IN_LICENSE_MAXID', 'IN_LICENCES');
  addSysIdGenRec('IN_LICENSE_TYPES_MAXID', 'IN_LICENCE_TYPES');
  addSysIdGenRec('IN_MANAGING_BODIES_MAXID', 'IN_MANAGING_BODIES');
  addSysIdGenRec('IN_BANK_GROUPS_MAXID', 'IN_BANK_GROUPS');
  addSysIdGenRec('SYS_LANGUAGE_MAXID', 'SYS_LANGUAGES');
  addSysIdGenRec('IN_MDT_COMPARISON_MAXID', 'IN_MDT_COMPARISON');
  addSysIdGenRec('IN_MDT_NODES_MAXID', 'IN_MDT_NODES');
  addSysIdGenRec('IN_PERIODS_MAXID', 'IN_PERIODS');
  addSysIdGenRec('IN_PERIOD_TYPES_MAXID', 'IN_PERIOD_TYPES');
  addSysIdGenRec('IN_COUNTRY_DATA_MAXID', 'IN_COUNTRY_DATA');
  addSysIdGenRec('OUT_REPORTS_MAXID', 'OUT_REPORTS');
  addSysIdGenRec('IN_RETURN_MAXID', 'IN_RETURNS');
  addSysIdGenRec('IN_DEFINITION_MAXID', 'IN_RETURN_DEFINITIONS');
  addSysIdGenRec('IN_RETURN_STATUSE_MAXID', 'IN_RETURN_STATUSES');
  addSysIdGenRec('IN_RETURN_TYPE_MAXID', 'IN_RETURN_TYPES');
  addSysIdGenRec('IN_RETURN_VERSION_MAXID', 'IN_RETURN_VERSIONS');
  addSysIdGenRec('IN_SCHEDULES_MAXID', 'IN_SCHEDULES');
  addSysIdGenRec('SYS_PERMISSION_MAXID', 'SYS_PERMISSIONS');
  addSysIdGenRec('SYS_ROLE_MAXID', 'SYS_ROLES');
  addSysIdGenRec('SYS_USER_MAXID', 'SYS_USERS');
  addSysIdGenRec('SYS_USER_STATES_MAXID', 'SYS_USER_STATES');
  addSysIdGenRec('SYS_UPLOADEDFILE_MAXID', 'SYS_UPLOADEDFILE');
  addSysIdGenRec('IN_IMPORTED_RETURNS_MAXID', 'IN_IMPORTED_RETURNS');
  addSysIdGenRec('IN_MAIL_MESSAGE_MAXID', 'IN_MAIL_MESSAGE');
  addSysIdGenRec('IN_RETURN_ITEM_MAXID', 'IN_RETURN_ITEMS');
  addSysIdGenRec('SYS_STRING_MAXID', 'SYS_STRINGS');
  addSysIdGenRec('OUT_MAIL_MESSAGE_REPLY_MAXID', 'OUT_MAIL_MESSAGE_REPLY');
  UPDATE SYS_ID_GENERATOR SET VALUE=1 WHERE VALUE IS NULL;
  
  putSomeSpace();
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('-UPDATING-PROPERTIES------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  
  DELETE FROM SYS_PROPERTIES WHERE PROP_KEY IN ('fina2.mail.searchType', 'fina2.mail.messages.limit');
  INSERT INTO SYS_PROPERTIES (prop_key, value) VALUES ('fina2.mail.searchType', 1);
  INSERT INTO SYS_PROPERTIES (prop_key, value) VALUES ('fina2.mail.messages.limit', 200);
   
  putSomeSpace();
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('-ADDING-TRIGGERS--------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  dropTrigger('returnsSafeIdTrigger');
  dropTrigger('returnItemsSafeIdTrigger');
  dropTrigger('inImportedReturnsSafeIdTrigger');
  dropTrigger('inReturnStatusesSafeIdTrigger');
  
  putSomeSpace();
  
  EXECUTE IMMEDIATE 'CREATE OR REPLACE TRIGGER returnsSafeIdTrigger
  AFTER INSERT 
  ON IN_RETURNS
  FOR EACH ROW
  DECLARE
    ID INT;
  BEGIN
    ID := :NEW.ID + 1;
    UPDATE SYS_ID_GENERATOR SET VALUE=ID WHERE PK_COLUMN_NAME=''IN_RETURN_MAXID'';
  END;';
  sayTriggerCreated('returnsSafeIdTrigger');

  EXECUTE IMMEDIATE 'CREATE OR REPLACE TRIGGER returnItemsSafeIdTrigger
  AFTER INSERT
  ON IN_RETURN_ITEMS
  FOR EACH ROW
  DECLARE
    ID INT;
  BEGIN
    ID := :NEW.ID + 1;
    UPDATE SYS_ID_GENERATOR SET VALUE=ID WHERE PK_COLUMN_NAME=''IN_RETURN_ITEM_MAXID'';
  END;';
  sayTriggerCreated('returnItemsSafeIdTrigger');
  
  EXECUTE IMMEDIATE 'CREATE OR REPLACE TRIGGER inImportedReturnsSafeIdTrigger
  AFTER INSERT
  ON IN_IMPORTED_RETURNS
  FOR EACH ROW
  DECLARE
    ID INT;
  BEGIN
    ID := :NEW.ID + 1;
    UPDATE SYS_ID_GENERATOR SET VALUE=ID WHERE PK_COLUMN_NAME=''IN_IMPORTED_RETURNS_MAXID'';
  END;';
  sayTriggerCreated('inImportedReturnsSafeIdTrigger');
  
  EXECUTE IMMEDIATE 'CREATE OR REPLACE TRIGGER inReturnStatusesSafeIdTrigger
  AFTER INSERT
  ON IN_RETURN_STATUSES
  FOR EACH ROW
  DECLARE
    ID INT;
  BEGIN
    ID := :NEW.ID + 1;
    UPDATE SYS_ID_GENERATOR SET VALUE=ID WHERE PK_COLUMN_NAME=''IN_RETURN_STATUSE_MAXID'';
  END;';
  sayTriggerCreated('inReturnStatusesSafeIdTrigger');
   
  putSomeSpace();
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('-SCRIPT-FINISHED----------------------------------------');
  putLine('--------------------------------------------------------');
  putLine('--------------------------------------------------------');
commit;
END;
