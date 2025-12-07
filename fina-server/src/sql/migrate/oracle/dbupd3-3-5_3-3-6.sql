/*==============================================================*/
/* Add/Populate version column for IN_RETURN_ITEMS table        */
/*==============================================================*/
alter table IN_RETURN_ITEMS add version NUMBER(10);

update IN_RETURN_ITEMS ri set ri.version = (select r.version from IN_RETURNS r where ri.returnid = r.id);

/*==============================================================*/
/* Replace RESULT_VIEW view                                     */
/*==============================================================*/
CREATE OR REPLACE VIEW RESULT_VIEW AS
SELECT RI.VALUE, RI.NVALUE, RI.NODEID, S.BANKID, P.PERIODTYPEID, P.FROMDATE, P.TODATE, P.ID AS PERIODID, RI.VERSION, R.VERSION AS RETURNVERSION
     FROM IN_SCHEDULES S,
          IN_PERIODS P,
          IN_RETURNS R,
          IN_RETURN_ITEMS RI
     WHERE
       S.PERIODID = P.ID AND R.SCHEDULEID=S.ID AND RI.RETURNID=R.ID;
