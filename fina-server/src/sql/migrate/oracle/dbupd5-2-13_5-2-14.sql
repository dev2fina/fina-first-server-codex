/*
 Database: Oracle 11g, 12c

 Author: Vamekh Goiati
 E: goiati@fina2.net
 Version: 1.0
 Date : 14/11/2016
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.14'
WHERE prop_key = 'fina2.database.schemaVersion';

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'net.fina.serviceMonitor.mailService.status.curr';
INSERT INTO SYS_PROPERTIES VALUES ('net.fina.serviceMonitor.mailService.status.curr', '');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'net.fina.serviceMonitor.mailService.status.last';
INSERT INTO SYS_PROPERTIES VALUES ('net.fina.serviceMonitor.mailService.status.last', '');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'net.fina.serviceMonitor.mailService.nextFireTime';
INSERT INTO SYS_PROPERTIES VALUES ('net.fina.serviceMonitor.mailService.nextFireTime', '');

commit;


DECLARE
  MAX_SS_ID NUMBER;
  MAX_SP_ID NUMBER;

BEGIN
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;
  select max(id) + 1 into MAX_SP_ID from SYS_PERMISSIONS;

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, 1, 'Service Monitor');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.tool.serviceMonitor');

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



