/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             SQL Server 2005 and higher                                             */
/* Created on:            03/04/2012                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */ 
/* Alters table attributes, adds constraints and 
 * removes Web portal admin and user permissions and inserts security settings permission*/
/*===============================================================================================*/

/* Update DB Version*/
update sys_properties set value='3.4.3m' where prop_key='fina2.database.schemaVersion';

alter table IN_BANKS alter column email nVARCHAR(255);
alter table IN_MDT_NODES alter column code varchar(500);
alter table IN_RETURN_ITEMS alter column value nVARCHAR(400);
alter table SYS_MENUS alter column application nVARCHAR(120);
ALTER TABLE dbo.IN_RETURN_STATUSES ADD CONSTRAINT
    PK_IN_RETURN_STATUSES PRIMARY KEY CLUSTERED 
    (
    ID
    ) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
ALTER TABLE dbo.SYS_PERMISSIONS ADD CONSTRAINT
    PK_SYS_PERMISSIONS PRIMARY KEY CLUSTERED 
    (
    ID
    ) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
ALTER TABLE dbo.Sys_Properties ADD CONSTRAINT
    PK_Sys_Properties PRIMARY KEY CLUSTERED 
    (
    PROP_KEY
    ) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]   


declare @sysPermId integer;
declare @sysStringId integer;
declare @langId integer;
 
set @sysPermId=(select max(id)+1 from sys_permissions) ;
set @sysStringId=(select max(id)+1 from sys_strings);
set @langId=(select top 1 id from SYS_LANGUAGES where lower(ltrim(rtrim(code)))='en_us' or lower(ltrim(rtrim(code)))='en');
delete from sys_permissions where ltrim(rtrim(idname))='fina2.web.admin' or ltrim(rtrim(idname))='fina2.web.user';
if not exists(select ID from sys_permissions where ltrim(rtrim(idname))='fina2.security.settings')
begin;
print 'Inserting security settings permission';
delete from sys_permissions where ltrim(rtrim(idname))='fina2.security.settings';     
insert into sys_Permissions(id,Idname,Namestrid) values(@sysPermId,'fina2.security.settings',@sysStringId);
insert into sys_strings(id,langid,value) values(@sysStringId,@langId,'Security Settings');
print 'Permission inserted';
end;