/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             SQLServer 2005 and higher                                              */
/* Created on:            07/10/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */   
/*===============================================================================================*/
delete from sys_properties where prop_key in('mail.imap.starttls.enable');
insert into sys_properties(prop_key,value) values('mail.imap.starttls.enable','true');

ALTER TABLE IN_BANKS ALTER COLUMN EMAIL nvarchar(255);