/*
 * Description: 	Script for Oracle 10g to update database from 3.4.0e to 3.4.1a                
 * Database: 		Oracle 10g    
 * FinA version: 	3.4.0e => 3.4.1a                        
 * 
 * Change log: 
 * - User Manager improvements                             
 * - Update OUT_REPORTS table data to support UP & DOWN arrows and sorting in GUI 
 */

/*================================================================================ */
/* User Manager improvements                                                       */
/*=================================================================================*/

/*==============================================================*/
/* Changes in table SYS_ROLES                                   */
/*==============================================================*/
alter table sys_roles add code varchar2(12);
    
/* Initial default data */
update sys_roles a set a.code = ('ROLE' || a.id);

/*==============================================================*/
/* Table sys_role_returns                                       */
/*==============================================================*/
create table sys_role_returns (
    role_id       number(10) referencing sys_roles(id) not null,
    definition_id number(10) referencing in_return_definitions(id) not null
);

/*==============================================================*/
/* Table sys_role_reports                                       */
/*==============================================================*/
create table sys_role_reports (
  role_id  number(10) referencing sys_roles(id),
  report_id number(10) referencing out_reports(id)
);

/*==============================================================*/
/* Table sys_role_return_versions                               */
/*==============================================================*/
create table sys_role_return_versions (
  role_id    number(10) referencing sys_roles(id),
  version_id number(10) referencing in_return_versions(id),
  can_amend  number(1) default 0
);

/*==============================================================*/
/* Table sys_user_returns                                       */
/*==============================================================*/
create table sys_user_returns (
    user_id       number(10) referencing sys_users(id) not null,
    definition_id number(10) referencing in_return_definitions(id) not null
);

/*==============================================================*/
/* Table sys_user_return_versions                               */
/*==============================================================*/
create table sys_user_return_versions (
  user_id    number(10) referencing sys_users(id),
  version_id number(10) referencing in_return_versions(id),
  can_amend  number(1) default 0
);

/*================================================================================ */
/* Update OUT_REPORTS table data to support UP & DOWN arrows and sorting in GUI    */
/*=================================================================================*/

/* 
 * Table out_reports.
 * Add SEQUENCE column to sort reports in GUI.
 */
alter table out_reports add SEQUENCE NUMBER(10) default 0;

/*  
 * Set init sequence of reports
 */
declare
    cursor c is 
        select a.parentid, a.id
        from out_reports a
        order by a.parentid, a.id
        for update;
        
    prev_parent_id integer := -1;
    i integer := 0;
begin
  
    for r in c loop

        if r.parentid <> prev_parent_id then
            i := 0;
        end if;
        
        update out_reports a
        set a.sequence = i
        where a.id = r.id;
        
        i := i + 1;
        prev_parent_id := r.parentid;
        
    end loop;  
    
end;

-- Save changes
commit;