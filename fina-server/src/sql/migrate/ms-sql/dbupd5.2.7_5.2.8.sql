/*
 Database: SQL Server 2014

 Author: Nikoloz Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 25/07/2016
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.8'
WHERE prop_key = 'fina2.database.schemaVersion';

ALTER TABLE IN_MDT_COMPARISON ADD NUMBER_PATTERN NVARCHAR(512);