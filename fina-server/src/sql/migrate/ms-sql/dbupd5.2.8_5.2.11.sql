/*
 Database: SQL Server 2014

 Author: Nikoloz Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 02/08/2016
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.11'
WHERE prop_key = 'fina2.database.schemaVersion';

--Period from-to
CREATE INDEX IDX_IN_PERIODS_FROM_TO ON IN_PERIODS (FROMDATE, TODATE);

--Return version indexes
CREATE INDEX IDX_IN_RETURN_VERSION_ID ON IN_RETURN_VERSIONS (ID);

