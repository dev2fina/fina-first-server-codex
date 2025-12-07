/*
 * Description: 	Script for MS SQL Server to update database from 3.4.0e to 3.4.1a                
 * Database: 		MS SQL Server    
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
alter table sys_roles add code varchar(12);
go

/* Initial default data */
update sys_roles set code = str(id);
go

/*==============================================================*/
/* Table: SYS_ROLE_RETURNS                                      */
/*==============================================================*/
create table SYS_ROLE_RETURNS (
   role_id              int                  not null,
   definition_id        int                  not null
)
go

/*==============================================================*/
/* Table: SYS_ROLE_REPORTS                                      */
/*==============================================================*/
create table SYS_ROLE_REPORTS (
   role_id              int                  null,
   report_id            int                  null
)
go

/*==============================================================*/
/* Table: SYS_ROLE_RETURN_VERSIONS                              */
/*==============================================================*/
create table SYS_ROLE_RETURN_VERSIONS (
   role_id              int                  null,
   version_id           int                  null,
   can_amend            int                  null default 0
)
go

/*==============================================================*/
/* Table: SYS_USER_RETURNS                                      */
/*==============================================================*/
create table SYS_USER_RETURNS (
   user_id              int                  null,
   definition_id        int                  null
)
go

/*==============================================================*/
/* Table: SYS_USER_RETURN_VERSIONS                              */
/*==============================================================*/
create table SYS_USER_RETURN_VERSIONS (
   user_id              int                  null,
   version_id           int                  null,
   can_amend            int                  null default 0
)
go

/*================================================================================ */
/* Update OUT_REPORTS table data to support UP & DOWN arrows and sorting in GUI    */
/*=================================================================================*/

/* 
 * Table out_reports.
 * Add SEQUENCE column to sort reports in GUI.
 */
alter table out_reports add SEQUENCE int default 0;
go

/*  
 * Set init sequence of reports
 */
declare cur cursor for	
	select parentid, id 
	from out_reports 
	order by parentid, id;

declare @id integer
declare @parent_id integer
declare @prev_parent_id integer
declare @i integer

set @prev_parent_id = -1
set @i = -1

open cur
FETCH NEXT FROM cur INTO @parent_id, @id

while @@FETCH_STATUS = 0
begin
	
	if @parent_id <> @prev_parent_id 
	begin
		set @i = 0
	end
  
	update out_reports 
	set sequence = @i
	where id = @id

	set @i = @i + 1
	set @prev_parent_id = @parent_id

	FETCH NEXT FROM cur INTO @parent_id, @id
end

close cur
deallocate cur
go