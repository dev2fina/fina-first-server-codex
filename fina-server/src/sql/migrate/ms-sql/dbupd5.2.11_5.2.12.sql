/*
 Database: MS SQL Server

 Author: Vamekh Goiati
 E: goiati@fina2.net
 Version: 1.0
 Date : 06/09/2016
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.12'
WHERE prop_key = 'fina2.database.schemaVersion';

ALTER TABLE SYS_PROPERTIES ALTER COLUMN VALUE NVARCHAR(3000);


DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'fina2.mail.sync';
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina2.mail.sync', '-1');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'fina2.mail.lastSyncStatus';
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina2.mail.lastSyncStatus', '');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'fina2.authentication.ldap.attribute.login';
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina2.authentication.ldap.attribute.login', 'uid');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'fina2.authentication.ldap.searchScope';
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina2.authentication.ldap.searchScope', 'SUBTREE_SCOPE');

