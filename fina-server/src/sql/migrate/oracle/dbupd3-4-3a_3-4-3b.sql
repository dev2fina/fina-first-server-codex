/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             Oracle                                                                 */
/* Created on:            07/26/2010                                                             */
/* Revision History       n/a                                                                    */
/* This script is for adding some indexes to increase Return Manager,Schedule Manager performance*/    
/*===============================================================================================*/
/* Script is for increasing column VALUE in SYS_STRINGS                   */
alter table SYS_STRINGS modify value VARCHAR2(1000);
create index schedule_id_id on IN_RETURNS (scheduleid, id);
create index type_id_id on IN_RETURN_DEFINITIONS (typeid, id);
create index schedules_1 on IN_SCHEDULES (periodid, bankid, definitionid, id);
create index schedules_2 on IN_SCHEDULES (periodid, definitionid, id);
create index ret_statuses_1 on IN_RETURN_STATUSES (returnid, id, versionid);
create index ret_statuses_2 on IN_RETURN_STATUSES (returnid, versionid);
create index user_banks on SYS_USER_BANKS (userid, bankid);
/*Adding these indexes for increasing report generation performance*/
create index REPORT_INDEX_ONE on IN_RETURN_ITEMS (nodeid, versionid, returnid, nvalue);
create index REPORT_INDEX_SECOND on IN_RETURN_ITEMS (versionid,returnid);
create index REPORT_INDEX_THIRD on IN_RETURNS (id,scheduleid,versionid);
create index REPORT_INDEX_FOURTH on IN_RETURNS (versionid,scheduleid);
create index REPORT_INDEX_FIFTH on IN_RETURNS (id,versionid,scheduleid);
create index REPORT_INDEX_SIXTH on IN_SCHEDULES (id,periodid,bankid);
/*Adding these indexes for increasing processing performance*/
create index PROCESS_INDEX_SECOND on SYS_STRINGS(langid,id);
create index PROCESS_INDEX_THIRD on IN_RETURNS(scheduleid,versionid,id);
create index PROCESS_INDEX_FOURTH on IN_MDT_NODES(id,required,namestrid);
create index PROCESS_INDEX_FIFTH on IN_MDT_DEPENDENT_NODES(dependentnodeid,nodeid);
commit;

