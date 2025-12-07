/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             SQL Server 2005 and higher                                             */
/* Created on:            25/11/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */ 
/* Inserts process timeout and two additiional permissions: Stored reports delete,Stored reports review*/
/*===============================================================================================*/
DELETE FROM SYS_PROPERTIES WHERE PROP_KEY IN('fina2.process.timeout','fina2.default.language.id','mail.address');
INSERT INTO SYS_PROPERTIES(PROP_KEY,VALUE) VALUES('fina2.process.timeout','3600000');
INSERT INTO SYS_PROPERTIES(PROP_KEY,VALUE) VALUES('fina2.default.language.id','en_US');
delete from sys_properties where prop_key in('fina2.mail.limit.timeout');
insert into SYS_PROPERTIES(PROP_KEY,VALUE) values('fina2.mail.limit.timeout','-1');

declare @viewId integer;
declare @viewStrId integer;
declare @deleteId integer;
declare @deleteStrId integer;
declare @viewCode varchar(50);
declare @viewString varchar(100);
declare @deleteCode varchar(50);
declare @deleteString varchar(100);
declare @langId integer;
declare @mailAdd varchar(100);

set @langId=(select ID from SYS_LANGUAGES where CODE='en_US');
set @mailAdd=(select value from sys_properties where prop_key='mail.user');
INSERT INTO SYS_PROPERTIES(PROP_KEY,VALUE) values('mail.address',@mailAdd);
if @langId=null
    set @langId=(select ID from SYS_LANGUAGES where CODE='en');

if @langId=null
    set @langId=(select MAX(ID) from SYS_LANGUAGES);
   
set @viewCode='fina2.stored.reports.view';
set @viewString='Stored reports review';
set @deleteCode='fina2.stored.reports.delete';
set @deleteString='Stored reports delete';

set @viewId=(select MAX(ID)+1 from SYS_PERMISSIONS);
set @viewStrId=(select MAX(ID)+1 from SYS_STRINGS);
set @deleteId=@viewId+1;
set @deleteStrId=@viewStrId+1;

delete from SYS_PERMISSIONS where IDNAME in(@viewCode,@deleteCode);
insert into SYS_PERMISSIONS(ID,NAMESTRID,IDNAME) values(@viewId,@viewStrId,@viewCode);
insert into SYS_STRINGS(ID,LANGID,VALUE) values(@viewStrId,@langId,@viewString);

insert into SYS_PERMISSIONS(ID,NAMESTRID,IDNAME) values(@deleteId,@deleteStrId,@deleteCode);
insert into SYS_STRINGS(ID,LANGID,VALUE) values(@deleteStrId,@langId,@deleteString);

alter table in_mdt_nodes alter column code varchar(500);