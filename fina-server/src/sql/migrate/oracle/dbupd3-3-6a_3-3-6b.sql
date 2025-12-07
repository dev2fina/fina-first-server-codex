declare 
  MAX_SS_ID NUMBER;
  MAX_SP_ID NUMBER;
begin
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;
  select max(id) + 1 into MAX_SP_ID from SYS_PERMISSIONS;
  
  insert into SYS_STRINGS (id, langID, value) values (MAX_SS_ID, 1, 'Security Settings');
  insert into SYS_PERMISSIONS (id, nameStrId, idName) values (MAX_SP_ID, MAX_SS_ID, 'fina2.security.settings');

end;
/

alter table IN_RETURN_STATUSES add ID NUMBER(10)
/

declare
  i integer;
  CURSOR c IS 
         SELECT ID FROM IN_RETURN_STATUSES ORDER BY STATUSDATE FOR UPDATE;
begin
  i := 1;
  FOR status IN c LOOP
    UPDATE IN_RETURN_STATUSES
      SET id = i
    WHERE CURRENT OF c;
    i := i + 1;
  END LOOP;  
end;
/

alter table IN_RETURN_STATUSES modify ID not null
/

alter table IN_RETURN_STATUSES add constraint PK_IN_RETURN_STATUSES primary key (ID)
/

