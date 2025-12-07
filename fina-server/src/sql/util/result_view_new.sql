/*
 Database: SQL Server 2008/2012
 
 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 22/01/2014
*/


/****** Object:  View [dbo].[RESULT_VIEW]    Script Date: 1/22/2014 12:16:37 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



/*==============================================================*/
/* View: RESULT_VIEW                                            */
/*==============================================================*/
create view [dbo].[RESULT_VIEW] as
SELECT RI.VALUE, RI.NVALUE, RI.NODEID, S.BANKID, P.PERIODTYPEID, P.FROMDATE, P.TODATE, 
P.ID AS PERIODID, RV.CODE AS VERSIONCODE, RV2.CODE AS LATESTVERSIONCODE
     FROM IN_SCHEDULES S,
          IN_PERIODS P,
          IN_RETURNS R,
          IN_RETURN_ITEMS RI,
          IN_RETURN_VERSIONS RV,
          IN_RETURN_VERSIONS RV2
     WHERE
       S.PERIODID = P.ID AND R.SCHEDULEID=S.ID AND RI.RETURNID=R.ID AND RV.ID = R.VERSIONID AND RV2.ID = R.VERSION

GO


