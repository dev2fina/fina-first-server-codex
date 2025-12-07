/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             SQLServer up to 2005                                                   */
/* Created on:            18/03/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */   
/*===============================================================================================*/
delete from sys_properties where rtrim(prop_key) in('fina2.max.returns');
insert into sys_properties(prop_key,value) values('fina2.max.returns','1000');

delete from SYS_PROPERTIES where rtrim(prop_key) in('fina2.returns.import.threadsNumber','fina2.mfb.uploaded.file.unique','mail.user','mail.password','mail.pop3.ssl.enable','mail.pop3.host','mail.pop3.port','mail.pop3.connectiontimeout','mail.smtp.ssl.enable','mail.smtp.host','mail.smtp.port','mail.smtp.connectiontimeout','org.exjello.mail.mailbox','org.exjello.mail.unfiltered','org.exjello.mail.limit','org.exjello.mail.delete','fina2.mail.connectionType','mail.smtp.starttls.enable','fina2.mail.sendResponceEnable','fina2.mail.responceMailsCC','fina2.dcs.mail.check.interval','fina2.mail.last.read.date','fina2.mail.responceUnknownUser');
insert into SYS_PROPERTIES(PROP_KEY,value) values('fina2.returns.import.threadsNumber','1');
insert into SYS_PROPERTIES(PROP_KEY,value) values('fina2.mfb.uploaded.file.unique','1');
insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.user','nick@fina.emea.microsoftonline.com');
insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.password','MagariParoli1');

insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.pop3.ssl.enable','true');
insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.pop3.host','red002.mail.emea.microsoftonline.com');
insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.pop3.port','443');
insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.pop3.connectiontimeout','10000');

insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.smtp.ssl.enable','true');
insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.smtp.starttls.enable','true');
insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.smtp.host','red002.mail.emea.microsoftonline.com');
insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.smtp.port','443');
insert into SYS_PROPERTIES(PROP_KEY,value) values('mail.smtp.connectiontimeout','10000');

insert into SYS_PROPERTIES(PROP_KEY,value) values('org.exjello.mail.mailbox','nick@fina.emea.microsoftonline.com');
insert into SYS_PROPERTIES(PROP_KEY,value) values('org.exjello.mail.unfiltered','true');
insert into SYS_PROPERTIES(PROP_KEY,value) values('org.exjello.mail.limit','10000');
insert into SYS_PROPERTIES(PROP_KEY,value) values('org.exjello.mail.delete','false');

insert into SYS_PROPERTIES(PROP_KEY,value) values('fina2.mail.connectionType','2');

insert into SYS_PROPERTIES(PROP_KEY,value) values('fina2.mail.sendResponceEnable','true');

insert into sys_properties(prop_key,value) values('fina2.dcs.mail.check.interval','300000');

insert into SYS_PROPERTIES(prop_key,value) values('fina2.mail.last.read.date',null);

insert into SYS_PROPERTIES(PROP_KEY,value) values('fina2.mail.responceMailsCC','suport@fina2.net,gochiashvili@fina2.net');

insert into SYS_PROPERTIES(PROP_KEY,VALUE) values('fina2.mail.responceUnknownUser','1');
