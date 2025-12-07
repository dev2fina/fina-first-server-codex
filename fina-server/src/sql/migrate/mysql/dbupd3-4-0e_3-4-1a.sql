/*
 * Description: 	Updates database from 3.4.0e to 3.4.1a                
 * Database: 		MySQL 5.0  
 * FinA version: 	3.4.0e => 3.4.1a                        
 * 
 * Change log: 
 * - User Manager improvements                             
 * - Update OUT_REPORTS table data to support UP & DOWN arrows and sorting in GUI 
 */

/*==============================================================*/
/* Changes in table SYS_ROLES                                   */
/*==============================================================*/
alter table sys_roles add code varchar(12);

/* Initial default data */
update sys_roles a
set a.code = concat('role', a.id);


/*==============================================================*/
/* table sys_role_returns                                       */
/*==============================================================*/
create table sys_role_returns (
    role_id       numeric(10) not null references sys_roles(id),
    definition_id numeric(10) not null references in_return_definitions(id)
);


/*==============================================================*/
/* Table sys_role_reports                                       */
/*==============================================================*/
create table sys_role_reports
(
  role_id   numeric(10) references  sys_roles(id),
  report_id numeric(10) references  out_reports(id)
);

/*==============================================================*/
/* Table sys_role_return_versions                               */
/*==============================================================*/
create table sys_role_return_versions
(
  role_id    numeric(10) references  sys_roles(id),
  version_id numeric(10) references  in_return_versions(id),
  can_amend  numeric(1) default 0
);

/*==============================================================*/
/* Table sys_user_returns                                       */
/*==============================================================*/
create table sys_user_returns
(
    user_id       numeric(10)  not null references  sys_users(id) ,
    definition_id numeric(10)  not null references in_return_definitions(id)
);

/*==============================================================*/
/* Table sys_user_return_versions                               */
/*==============================================================*/
create table sys_user_return_versions
(
  user_id    numeric(10) references  sys_users(id),
  version_id numeric(10) references  in_return_versions(id),
  can_amend  numeric(1) default 0
);


/*================================================================================*/
/*                          UP & DOWN arrows and Sorting issue                    */
/*                                   04 JULY 2007                                 */
/*================================================================================*/

/*
 * Table out_reports.
 * Add SEQUENCE column to sort reports in GUI.
 */
alter table out_reports add SEQUENCE numeric(10) default 0;
 
drop procedure if exists temp_proc;
delimiter $$

create procedure temp_proc()
begin

  declare no_data int default 0;

  declare id int;
  declare parent_id int;
  declare prev_parent_id int default -1;
  declare i int default 0;

  declare c cursor for
    select a.parentid, a.id
    from out_reports a
    order by a.parentid, a.id;

  declare continue handler for not found set no_data = 1;

  -- --------------------------------------------------------------------------

  set no_data = 0;

  open c;

  cursor_loop: while (no_data = 0) do

    fetch c into parent_id, id;

    if no_data = 1 then
      leave cursor_loop;
    end if;

    if parent_id <> prev_parent_id then
      set i = 0;
    end if;

    update out_reports a
    set a.sequence = i
    where a.id = id;

    set i = i + 1;
    set prev_parent_id = parent_id;

   end while cursor_loop;

  close c;

  commit;

end $$

delimiter ;
call temp_proc();
drop procedure if exists temp_proc;