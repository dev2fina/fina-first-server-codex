/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             Oracle                                                                 */
/* Created on:            25/11/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */ 
/* Inserts process timeout and two additiional permissions: Stored reports DELETE,Stored reports review*/
/*===============================================================================================*/
alter table IN_MDT_NODES modify code VARCHAR2(500);
DELETE FROM SYS_PROPERTIES WHERE RTRIM(PROP_KEY) IN('fina2.process.timeout','fina2.default.language.id','mail.address');
INSERT INTO SYS_PROPERTIES(PROP_KEY,VALUE) VALUES('fina2.process.timeout','3600000');
INSERT INTO SYS_PROPERTIES(PROP_KEY,VALUE) VALUES('fina2.default.language.id','en_US');
INSERT INTO SYS_PROPERTIES(PROP_KEY,VALUE) values('mail.address',(select value from sys_properties where rtrim(prop_key)='mail.user'));
commit;

delete from sys_properties where prop_key in('fina2.mail.limit.timeout');
insert into SYS_PROPERTIES(PROP_KEY,VALUE) values('fina2.mail.limit.timeout','-1');
commit;

DECLARE
  VIEWID      SYS_PERMISSIONS.ID%TYPE;
  VIEWSTRID   SYS_STRINGS.ID%TYPE;
  DELETEID     SYS_PERMISSIONS.ID%TYPE;
  DELETESTRID  SYS_STRINGS.ID%TYPE;
  LANGID      SYS_LANGUAGES.ID%TYPE;
  DELETECODE   SYS_PERMISSIONS.IDNAME%TYPE := 'fina2.stored.reports.delete';
  DELETESTRING SYS_STRINGS.VALUE%TYPE := 'Stored reports delete';
  VIEWCODE    SYS_PERMISSIONS.IDNAME%TYPE := 'fina2.stored.reports.view';
  VIEWSTRING  SYS_STRINGS.VALUE%TYPE := 'Stored reports review';
BEGIN
  SELECT ID INTO LANGID FROM SYS_LANGUAGES WHERE RTRIM(CODE) = 'en_US';
  SELECT MAX(ID) + 1 INTO VIEWID FROM SYS_PERMISSIONS;
  SELECT MAX(ID) + 1 INTO VIEWSTRID FROM SYS_STRINGS;
  DELETEID    := VIEWID + 1;
  DELETESTRID := VIEWSTRID + 1;
  DELETE FROM SYS_PERMISSIONS
   WHERE RTRIM(IDNAME) IN (RTRIM(DELETECODE), RTRIM(VIEWCODE));

  INSERT INTO SYS_PERMISSIONS
    (ID, IDNAME, NAMESTRID)
  VALUES
    (VIEWID, VIEWCODE, VIEWSTRID);
  INSERT INTO SYS_STRINGS
    (ID, LANGID, VALUE)
  VALUES
    (VIEWSTRID, LANGID, VIEWSTRING);
  INSERT INTO SYS_PERMISSIONS
    (ID, IDNAME, NAMESTRID)
  VALUES
    (DELETEID, DELETECODE, DELETESTRID);
  INSERT INTO SYS_STRINGS
    (ID, LANGID, VALUE)
  VALUES
    (DELETESTRID, LANGID, DELETESTRING);
  COMMIT;
END;

