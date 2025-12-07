/*==============================================================*/
/* Alter SYS_PROPERTIES table. Rename KEY colum to prop_key     */
/*==============================================================*/
alter table SYS_PROPERTIES rename column "KEY" to prop_key;


/*==============================================================*/
/* Insert new record in SYS_PROPERTIES table.                   */
/*==============================================================*/
insert into SYS_PROPERTIES (prop_key, value) values ('fina2.database.schemaVersion', '3.4.0');

/*==============================================================*/
/* Add new column 'versionID' to the IN_RETURNS table.          */
/*==============================================================*/
alter table IN_RETURNS add versionID NUMBER(10);

create index returns_versionID on IN_RETURNS (versionid);

create index return_items_retID_verID on IN_RETURN_ITEMS (returnid, versionid DESC);

create index return_items_verID on IN_RETURN_ITEMS (versionid);

UPDATE IN_RETURNS SET IN_RETURNS.VERSIONID=
(SELECT RV.ID FROM IN_RETURN_VERSIONS RV WHERE  RV.SEQUENCE = 
  (SELECT MAX(RV2.SEQUENCE) FROM IN_RETURN_VERSIONS RV2, IN_RETURN_ITEMS RI 
   WHERE RI.RETURNID=IN_RETURNS.ID AND RV2.ID=RI.VERSIONID)
);

/*==============================================================*/
/* Update 'RESULT_VIEW' View                                    */
/*==============================================================*/
CREATE OR REPLACE VIEW RESULT_VIEW AS
SELECT RI.VALUE, RI.NVALUE, RI.NODEID, S.BANKID, P.PERIODTYPEID, P.FROMDATE, P.TODATE, 
P.ID AS PERIODID, RV.CODE AS VERSIONCODE, RV2.CODE AS LATESTVERSIONCODE
     FROM IN_SCHEDULES S,
          IN_PERIODS P,
          IN_RETURNS R,
          IN_RETURN_ITEMS RI,
          IN_RETURN_VERSIONS RV,
          IN_RETURN_VERSIONS RV2
     WHERE
       S.PERIODID = P.ID AND R.SCHEDULEID=S.ID AND RI.RETURNID=R.ID AND RV.ID = RI.VERSIONID AND RV2.ID = R.VERSIONID;