/*
 Database: Oracle 11g
 Author: Nick Gochiashvili(nick@fina2.net), Oleg Dolidze(oleg@fina2.net), Mikheil Tchelidze(chelomisha@fina2.net)
 Version: 0.1
 Date : 17/01/2014
 version:0.2	add column report_type  in OUT_STORED_REPORTS
 Date: 10/10/2014 
*/

/*
  Update DB Version
*/
update sys_properties set value='4.3.0' where prop_key='fina2.database.schemaVersion';

/*
  add column report_type  in out_reports
*/
ALTER TABLE OUT_REPORTS
ADD REPORT_TYPE int DEFAULT 0  NOT NULL;
/*
  add column report_type  in OUT_STORED_REPORTS
*/
ALTER TABLE OUT_STORED_REPORTS
ADD REPORT_TYPE int DEFAULT 0  NOT NULL;

/*
	Add Permissions
*/
DECLARE
  MAX_SS_ID NUMBER;
  MAX_SP_ID NUMBER;

BEGIN         
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;
  select max(id) + 1 into MAX_SP_ID from SYS_PERMISSIONS;

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, 1, 'net.fina.tool.auditTrailLog');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 1, 1, 'net.fina.tool.mailLog');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 2, 1, 'net.fina.tool.cacheManager');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 3, 1, 'net.fina.tool.ostRelease');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 4, 1, 'net.fina.tool.mdtToXml');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 5, 1, 'net.fina.tool.returnToXml');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 6, 1, 'net.fina.tool.mdtGenerator');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 7, 1, 'net.fina.tool.finaFileDecryption');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 8, 1, 'net.fina.import.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 9, 1, 'net.fina.import.upload');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 10, 1, 'net.fina.import.reImport');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 11, 1, 'net.fina.dcs.umfi.tab');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 12, 1, 'net.fina.dcs.abd.tab');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.tool.auditTrailLog');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.tool.mailLog');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 2, MAX_SS_ID + 2, 'net.fina.tool.cacheManager');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 3, MAX_SS_ID + 3, 'net.fina.tool.ostRelease');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 4, MAX_SS_ID + 4, 'net.fina.tool.mdtToXml');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 5, MAX_SS_ID + 5, 'net.fina.tool.returnToXml');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 6, MAX_SS_ID + 6, 'net.fina.tool.mdtGenerator');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 7, MAX_SS_ID + 7, 'net.fina.tool.finaFileDecryption');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 8, MAX_SS_ID + 8, 'net.fina.import.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 9, MAX_SS_ID + 9, 'net.fina.import.upload');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 10, MAX_SS_ID + 10, 'net.fina.import.reImport');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 11, MAX_SS_ID + 11, 'net.fina.dcs.umfi.tab');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 12, MAX_SS_ID + 12, 'net.fina.dcs.abd.tab');

end;
commit;

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_STRINGS)
WHERE PK_COLUMN_NAME ='SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME ='SYS_PERMISSION_MAXID';

commit;

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

/*==============================================================*/
/* View: RESULT_VIEW                                            */
/*==============================================================*/
DROP VIEW RESULT_VIEW;

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

--Correct return items
UPDATE IN_RETURNS
SET VERSION = VERSIONID;

DECLARE 
     tmp_return_id  IN_RETURNS.ID%TYPE;   
     tmp_scheduleId IN_RETURNS.scheduleid %TYPE;  
     tmp_versionId IN_RETURNS.versionId %TYPE;  
     tmp_lastVersionId IN_RETURNS.version %TYPE;  
     cur_versions SYS_REFCURSOR;
  BEGIN
  for  cur_returns in (SELECT DISTINCT r.id FROM IN_RETURN_ITEMS ri, IN_RETURNS r WHERE r.ID = ri.RETURNID AND ri.VERSIONID <> r.VERSIONID) 
  loop        
        SELECT  r.scheduleId, r.versionId into tmp_scheduleId,tmp_lastVersionId  FROM IN_RETURNS r WHERE r.id = cur_returns.id;                
        
          OPEN cur_versions for SELECT DISTINCT ri.VERSIONID FROM IN_RETURN_ITEMS ri  WHERE ri.RETURNID = cur_returns.id AND ri.VERSIONID <> tmp_lastVersionId;
           LOOP
             FETCH cur_versions INTO tmp_versionId;
             EXIT WHEN cur_versions%NOTFOUND;
                 
                  select max(id)+1 into tmp_return_id from IN_RETURNS;          
        
                  INSERT INTO IN_RETURNS (id, SCHEDULEID, VERSION, versionId) VALUES (tmp_return_id, tmp_scheduleId,tmp_lastVersionId, tmp_versionId);        
                  
                  UPDATE IN_RETURN_ITEMS SET RETURNID = tmp_return_id WHERE RETURNID =  cur_returns.id AND VERSIONID = tmp_versionId;  
                        
                  UPDATE IN_RETURN_STATUSES SET RETURNID = tmp_return_id WHERE RETURNID = cur_returns.id AND VERSIONID = tmp_versionId;
        
                  DBMS_OUTPUT.put_line('Return Id:'||cur_returns.id||' ,id:'||tmp_return_id||' ,schedule id:'||tmp_scheduleId||' ,version id:'||tmp_versionId||', last version id:'||tmp_lastVersionId);                    
        
         END LOOP;
         CLOSE cur_versions;
     end loop;
END; 

UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from IN_RETURNS)
WHERE PK_COLUMN_NAME ='IN_RETURN_MAXID';

COMMIT;

