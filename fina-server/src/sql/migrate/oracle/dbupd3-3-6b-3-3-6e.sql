DROP TABLE IN_RETURN_VERSIONS CASCADE CONSTRAINTS
/

/*==============================================================*/
/* Table: IN_RETURN_VERSIONS                                    */
/*==============================================================*/
CREATE TABLE IN_RETURN_VERSIONS  (
   "ID"                 NUMBER(10)                      NOT NULL,
   "CODE"               VARCHAR(12),
   "SEQUENCE"           NUMBER(10),
   "DESCSTRID"          NUMBER(10),
   CONSTRAINT PK_IN_RETURN_VERSIONS PRIMARY KEY ("ID")
)
/

declare 
  MAX_SS_ID NUMBER;
  MAX_SP_ID NUMBER;
begin
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;

  select max(id) + 1 into MAX_SP_ID from SYS_PERMISSIONS;
  
  insert into SYS_STRINGS (id, langID, value) values (MAX_SS_ID, 1, 'Return Version Amend');
  insert into SYS_STRINGS (id, langID, value) values (MAX_SS_ID + 1, 1, 'Return Version Delete');
  insert into SYS_STRINGS (id, langID, value) values (MAX_SS_ID + 2, 1, 'Return Version Review');

  insert into SYS_PERMISSIONS (id, nameStrId, idName) values (MAX_SP_ID, MAX_SS_ID, 'fina2.returns.version.amend');
  insert into SYS_PERMISSIONS (id, nameStrId, idName) values (MAX_SP_ID + 1, MAX_SS_ID + 1, 'fina2.returns.version.delete');
  insert into SYS_PERMISSIONS (id, nameStrId, idName) values (MAX_SP_ID + 2, MAX_SS_ID + 2, 'fina2.returns.version.review');

end;
/

truncate table IN_RETURNS
/

truncate table IN_RETURN_ITEMS
/

alter table IN_RETURN_ITEMS drop column VERSION
/

alter table IN_RETURN_ITEMS add VERSIONID NUMBER(10)
/

truncate table IN_RETURN_STATUSES
/

alter table IN_RETURN_STATUSES add VERSIONID NUMBER(10)
/


CREATE OR REPLACE VIEW RESULT_VIEW AS
SELECT RI.VALUE, RI.NVALUE, RI.NODEID, S.BANKID, P.PERIODTYPEID, P.FROMDATE, P.TODATE, 
P.ID AS PERIODID, RV.CODE AS VERSIONCODE,
(SELECT RV2.CODE FROM IN_RETURN_VERSIONS RV2 WHERE RV2.ID = (SELECT MAX(RI2.VERSIONID) FROM IN_RETURNS R2, IN_RETURN_ITEMS RI2 where R2.ID=R.ID and R2.ID = RI2.RETURNID)) AS LATESTVERSIONCODE
     FROM IN_SCHEDULES S,
          IN_PERIODS P,
          IN_RETURNS R,
          IN_RETURN_ITEMS RI,
          IN_RETURN_VERSIONS RV
     WHERE
       S.PERIODID = P.ID AND R.SCHEDULEID=S.ID AND RI.RETURNID=R.ID AND RV.ID = RI.VERSIONID
/
