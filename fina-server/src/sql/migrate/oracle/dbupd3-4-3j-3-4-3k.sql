/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             Oracle                                                                 */
/* Created on:            07/10/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */   
/*===============================================================================================*/
delete from sys_properties where prop_key in('mail.imap.starttls.enable');
insert into sys_properties(prop_key,value) values('mail.imap.starttls.enable','true');
alter table in_banks modify (email varchar2(255));
commit;