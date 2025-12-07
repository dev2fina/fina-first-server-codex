-- DBMS name: SQL Server 2005 and higher                                             
-- Created on: 20/09/2013
-- Version: 1.1                                                             
-- Production to demo abc123

update in_return_items set value='abc' where value is not null;
update in_return_items set nvalue=123  where nvalue!=0;

update sys_strings set value='user name' where id in(select namestrid from sys_users);
update sys_strings set value='user title' where id in(select titlestrid from sys_users);

update SYS_USERS set EMAIL='<EMAIL>', PHONE='<PHONE>';


update IN_BANKS set EMAIL='<EMAIL>', FAX='<FAX>', PHONE='<PHONE>';

TRUNCATE TABLE IN_IMPORTED_RETURNS;
TRUNCATE TABLE SYS_AUDIT_LOG;
TRUNCATE TABLE SYS_UPLOADEDFILE;

delete from IN_MAIL_MESSAGE;
delete from IN_MAIL_MESSAGE_UPLOADFILES;
delete from OUT_MAIL_MESSAGE_REPLY;
delete from OUT_REPORTS_LANG;
delete from OUT_REPORTS_SCHEDULE;
delete from OUT_STORED_REPORTS;