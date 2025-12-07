/*
 Database: Oracle 11g

 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 0.1
 Date : 11/12/2015
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.1'
WHERE prop_key = 'fina2.database.schemaVersion';
