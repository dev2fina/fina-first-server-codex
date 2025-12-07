/*
 Database: Oracle 11g, 12c

 Author: Baaka Tsutskhvashvili
 E: baaka@fina2.net
 Version: 1.0
 Date : 31/05/2016
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.7'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
    Permissions for Query-Builder
*/
DECLARE
  MAX_SS_ID NUMBER;
  MAX_SP_ID NUMBER;

BEGIN
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;
  select max(id) + 1 into MAX_SP_ID from SYS_PERMISSIONS;

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, 1, 'Query-Builder User');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 1, 1, 'Query-Builder Admin');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.query.builder.user');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.query.builder.admin');

end
commit;

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_STRINGS)
WHERE PK_COLUMN_NAME ='SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME ='SYS_PERMISSION_MAXID';

commit;
