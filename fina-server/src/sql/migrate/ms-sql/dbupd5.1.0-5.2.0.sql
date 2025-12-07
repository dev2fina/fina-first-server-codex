/*
 Database: SQL Server 2012

 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 31/08/2015
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.0'
WHERE prop_key = 'fina2.database.schemaVersion';


