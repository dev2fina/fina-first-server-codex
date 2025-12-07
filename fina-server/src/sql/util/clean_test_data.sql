-- Database: Oracle 11g
--Author: Nick Gochiashvili
-- E: nick@fina2.net
--Version: 1.0.1
--Date : 17/09/2012

--Disable constrains

BEGIN
  FOR c IN
  (SELECT c.owner, c.table_name, c.constraint_name
   FROM user_constraints c, user_tables t
   WHERE c.table_name = t.table_name
   AND c.status = 'ENABLED'
   ORDER BY c.constraint_type DESC)
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" disable constraint ' || c.constraint_name);
  END LOOP;
END;
/
commit;

--Delete Data

TRUNCATE TABLE IN_IMPORTED_RETURNS;
TRUNCATE TABLE IN_RETURNS;
TRUNCATE TABLE IN_RETURN_ITEMS;
TRUNCATE TABLE IN_RETURN_STATUSES;
TRUNCATE TABLE IN_SCHEDULES;
TRUNCATE TABLE SYS_AUDIT_LOG;
TRUNCATE TABLE SYS_UPLOADEDFILE;
delete from IN_MAIL_MESSAGE;
delete from IN_MAIL_MESSAGE_UPLOADFILES;
delete from OUT_MAIL_MESSAGE_REPLY;
delete from OUT_REPORTS_LANG;
delete from OUT_REPORTS_SCHEDULE;
delete from OUT_STORED_REPORTS;
commit;

--Delete users
delete from sys_strings where id in(select u.namestrid from sys_users u where rtrim(u.login)!='sa');
delete from sys_strings where id in(select u.titlestrid from sys_users u where rtrim(u.login)!='sa');
delete sys_users where id!=(select id from sys_users u  where rtrim(u.login)='sa');
delete sys_user_banks where userid!=(select id from sys_users u  where rtrim(u.login)='sa');
delete sys_user_passwords where userid!=(select id from sys_users u  where rtrim(u.login)='sa');
delete sys_user_permissions where userid!=(select id from sys_users u  where rtrim(u.login)='sa');
delete sys_user_reports where userid!=(select id from sys_users u  where rtrim(u.login)='sa');
delete sys_user_return_versions where user_id!=(select id from sys_users u  where rtrim(u.login)='sa');
delete sys_user_states where user_id!=(select id from sys_users u  where rtrim(u.login)='sa');
delete sys_users_roles where userid!=(select id from sys_users u  where rtrim(u.login)='sa');
delete sys_user_returns where user_id!=(select id from sys_users u  where rtrim(u.login)='sa');

--Enable constraints

BEGIN
  FOR c IN
  (SELECT c.owner, c.table_name, c.constraint_name
   FROM user_constraints c, user_tables t
   WHERE c.table_name = t.table_name
   AND c.status = 'DISABLED'
   ORDER BY c.constraint_type)
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" enable constraint ' || c.constraint_name);
  END LOOP;
END;
/
commit;