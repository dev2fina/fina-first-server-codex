/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             Oracle 10g and higher                                                  */
/* Created on:            02/04/2012                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */
/* Alters table attributes, adds constraints and 
 * removes Web portal admin and user permissions and inserts security settings permission*/
/*===============================================================================================*/

/* Update DB Version*/
update sys_properties set value='3.4.3m' where prop_key='fina2.database.schemaVersion';

alter table IN_BANKS modify email VARCHAR2(255);
alter table IN_MDT_NODES modify code varchar2(500);
alter table IN_RETURN_ITEMS modify value VARCHAR2(400);
alter table SYS_MENUS modify application VARCHAR2(120);
alter table IN_RETURN_STATUSES add constraint pk_in_return_statuses primary key (ID);
alter table SYS_PERMISSIONS add constraint pk_sys_permissions primary key (ID);
alter table Sys_Properties add constraint pk_sys_property primary key (prop_key);
commit;

declare
  sysPermId   SYS_PERMISSIONS.ID%TYPE;
  sysStringId SYS_STRINGS.Id%TYPE;
  langId      SYS_LANGUAGES.ID%TYPE;
  permCounter SYS_PERMISSIONS.Id%TYPE;
begin
  select max(id) + 1 into sysPermId from sys_permissions;
  select max(id) + 1 into sysStringId from sys_strings;
  select id
    into langId
    from SYS_LANGUAGES
   where lower(ltrim(rtrim(code))) = 'en_us'
      or lower(ltrim(rtrim(code))) = 'en' and rownum<2;
  select count(id)
    into permCounter
    from sys_permissions
   where ltrim(rtrim(idname)) = 'fina2.security.settings';
  delete from sys_permissions
   where ltrim(rtrim(idname)) = 'fina2.web.admin'
      or ltrim(rtrim(idname)) = 'fina2.web.user';
  if (permCounter < 1) then  
    dbms_output.put_line('Inserting security settings permission');
    insert into sys_Permissions
      (id, Idname, Namestrid)
    values
      (sysPermId, 'fina2.security.settings', sysStringId);
    insert into sys_strings
      (id, langid, value)
    values
      (sysStringId, langId, 'Security Settings');
    commit;
    dbms_output.put_line('Permission inserted');
  end if;
end;