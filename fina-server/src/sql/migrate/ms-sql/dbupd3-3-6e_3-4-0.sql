/*==============================================================*/
/* Alter SYS_PROPERTIES table. Rename KEY colum to prop_key     */
/*==============================================================*/
sp_rename 'SYS_PROPERTIES.[key]', 'prop_key', 'COLUMN'
go

/*==============================================================*/
/* Insert new record in SYS_PROPERTIES table.                   */
/*==============================================================*/
insert into SYS_PROPERTIES (prop_key, value) values ('fina2.database.schemaVersion', '3.4.0')
go

/*==============================================================*/
/* Add new column 'versionID' to the IN_RETURNS table.          */
/*==============================================================*/
alter table IN_RETURNS add versionID int
go

create index returns_versionID on IN_RETURNS (versionid)
go

create index return_items_retID_verID on IN_RETURN_ITEMS (returnid, versionid DESC)
go

create index return_items_verID on IN_RETURN_ITEMS (versionid)
go

UPDATE IN_RETURNS SET IN_RETURNS.VERSIONID=
(SELECT RV.ID FROM IN_RETURN_VERSIONS RV WHERE  RV.SEQUENCE = 
  (SELECT MAX(RV2.SEQUENCE) FROM IN_RETURN_VERSIONS RV2, IN_RETURN_ITEMS RI 
   WHERE RI.RETURNID=IN_RETURNS.ID AND RV2.ID=RI.VERSIONID)
)
go

/*==============================================================*/
/* Update 'RESULT_VIEW' View                                    */
/*==============================================================*/
DROP VIEW RESULT_VIEW
go

CREATE VIEW RESULT_VIEW AS
SELECT RI.VALUE, RI.NVALUE, RI.NODEID, S.BANKID, P.PERIODTYPEID, P.FROMDATE, P.TODATE, 
P.ID AS PERIODID, RV.CODE AS VERSIONCODE, RV2.CODE AS LATESTVERSIONCODE
     FROM IN_SCHEDULES S,
          IN_PERIODS P,
          IN_RETURNS R,
          IN_RETURN_ITEMS RI,
          IN_RETURN_VERSIONS RV,
          IN_RETURN_VERSIONS RV2
     WHERE
       S.PERIODID = P.ID AND R.SCHEDULEID=S.ID AND RI.RETURNID=R.ID AND RV.ID = RI.VERSIONID AND RV2.ID = R.VERSIONID
go