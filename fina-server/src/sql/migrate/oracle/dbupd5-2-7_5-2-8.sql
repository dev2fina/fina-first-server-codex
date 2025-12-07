/*
 Database: Oracle 11g, 12c

 Author: Alexander Dolidze
 E: sasha@fina2.net
 Version: 1.0
 Date : 07/07/2016
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.8'
WHERE prop_key = 'fina2.database.schemaVersion';

COMMIT;

ALTER TABLE IN_MDT_COMPARISON ADD NUMBER_PATTERN NVARCHAR2(512);