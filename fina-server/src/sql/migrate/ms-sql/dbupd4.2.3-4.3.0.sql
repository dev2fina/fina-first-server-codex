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
SET value = '4.3.0'
WHERE prop_key = 'fina2.database.schemaVersion';

GO

/*
  Add permissions
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

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id, @lang_id, 'net.fina.tool.auditTrailLog');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 1, @lang_id, 'net.fina.tool.mailLog');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 2, @lang_id, 'net.fina.tool.cacheManager');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 3, @lang_id, 'net.fina.tool.ostRelease');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 4, @lang_id, 'net.fina.tool.mdtToXml');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 5, @lang_id, 'net.fina.tool.returnToXml');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 6, @lang_id, 'net.fina.tool.mdtGenerator');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 7, @lang_id, 'net.fina.tool.finaFileDecryption');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 8, @lang_id, 'net.fina.import.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 9, @lang_id, 'net.fina.import.upload');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 10, @lang_id, 'net.fina.import.reImport');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id, @ss_id, 'net.fina.tool.auditTrailLog');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.tool.mailLog');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 2, @ss_id + 2, 'net.fina.tool.cacheManager');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 3, @ss_id + 3, 'net.fina.tool.ostRelease');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 4, @ss_id + 4, 'net.fina.tool.mdtToXml');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 5, @ss_id + 5, 'net.fina.tool.returnToXml');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 6, @ss_id + 6, 'net.fina.tool.mdtGenerator');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 7, @ss_id + 7, 'net.fina.tool.finaFileDecryption');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 8, @ss_id + 8, 'net.fina.import.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 9, @ss_id + 9, 'net.fina.import.upload');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 10, @ss_id + 10, 'net.fina.import.reImport');
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
  Increase length of code column in out_reports
*/
ALTER TABLE Out_reports ALTER COLUMN code VARCHAR(1000);
GO

/*
  add column report_type  in out_reports
*/
ALTER TABLE OUT_REPORTS
ADD REPORT_TYPE int NOT NULL DEFAULT 0

--fix last version delete problem
UPDATE IN_RETURNS
SET VERSION = VERSIONID
WHERE id IN (SELECT
               r.ID
             FROM IN_RETURNS r
             WHERE r.VERSION != r.VERSIONID AND r.VERSION NOT IN (SELECT
                                                                    VERSIONID
                                                                  FROM IN_RETURNS
                                                                  WHERE SCHEDULEID = r.SCHEDULEID AND r.ID != id));
GO

/*==============================================================*/
/* View: RESULT_VIEW                                            */
/*==============================================================*/
DROP VIEW RESULT_VIEW;
GO

CREATE VIEW RESULT_VIEW AS
  SELECT
    RI.VALUE,
    RI.NVALUE,
    RI.NODEID,
    S.BANKID,
    P.PERIODTYPEID,
    P.FROMDATE,
    P.TODATE,
    P.ID     AS PERIODID,
    RV.CODE  AS VERSIONCODE,
    RV2.CODE AS LATESTVERSIONCODE
  FROM IN_SCHEDULES S,
    IN_PERIODS P,
    IN_RETURNS R,
    IN_RETURN_ITEMS RI,
    IN_RETURN_VERSIONS RV,
    IN_RETURN_VERSIONS RV2
  WHERE
    S.PERIODID = P.ID AND R.SCHEDULEID = S.ID AND RI.RETURNID = R.ID AND RV.ID = R.VERSIONID AND RV2.ID = R.VERSION;

GO


--Correct return items
UPDATE IN_RETURNS
SET VERSION = VERSIONID;
GO

DECLARE db_cursor CURSOR FOR SELECT DISTINCT
                               r.id
                             FROM IN_RETURN_ITEMS ri, IN_RETURNS r
                             WHERE r.ID = ri.RETURNID AND ri.VERSIONID <> r.VERSIONID;
DECLARE @returnId DECIMAL(10);
OPEN db_cursor;
FETCH NEXT FROM db_cursor
INTO @returnId;
WHILE @@FETCH_STATUS = 0
  BEGIN

    DECLARE @id              DECIMAL(10);
    DECLARE @scheduleId      DECIMAL(10);
    DECLARE @versionId       DECIMAL(10);
    DECLARE @latestVersionId DECIMAL(10);

    SET @id = (SELECT
                 max(id) + 1
               FROM IN_RETURNS);


    SELECT
      @scheduleId = r.scheduleId,
      @versionId = r.versionId
    FROM IN_RETURNS r
    WHERE id = @returnId;
    SELECT
      @latestVersionId = ri.VERSIONID
    FROM IN_RETURN_ITEMS ri
    WHERE RETURNID = @returnId AND VERSIONID <> @versionId;
    INSERT INTO IN_RETURNS (id, SCHEDULEID, VERSION, versionId) VALUES (@id, @scheduleId, @versionId, @latestVersionId);
    UPDATE IN_RETURN_ITEMS
    SET RETURNID = @id
    WHERE RETURNID = @returnId AND VERSIONID = @latestVersionId;
    UPDATE IN_RETURN_STATUSES
    SET RETURNID = @id
    WHERE RETURNID = @returnId AND VERSIONID = @latestVersionId;

    FETCH NEXT FROM db_cursor
    INTO @returnId;
  END;
CLOSE db_cursor;
DEALLOCATE db_cursor;
