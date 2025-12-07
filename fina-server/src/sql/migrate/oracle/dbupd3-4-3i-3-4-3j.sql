/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             Oracle 10g     and higher                                              */
/* Created on:            20/07/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */   
/*===============================================================================================*/
delete from sys_properties where prop_key in('fina2.mail.notReadMails');
insert into sys_properties(prop_key,value) values('fina2.mail.notReadMails','');

delete from sys_properties where prop_key in('mail.imap.ssl.enable','mail.imap.host','mail.imap.port','mail.imap.connectiontimeout');
insert into sys_properties(prop_key,value) values('mail.imap.ssl.enable','true');
insert into sys_properties(prop_key,value) values('mail.imap.host','imap.gmail.com');
insert into sys_properties(prop_key,value) values('mail.imap.port','993');
insert into sys_properties(prop_key,value) values('mail.imap.connectiontimeout','10000');
commit;