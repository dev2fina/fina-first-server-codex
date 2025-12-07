
/*
 Database: Oracle 11g, 12c

 Author: Vamekh Goiati
 E: goiati@fina2.net
 Version: 0.1
 Date : 14/04/2016
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.6'
WHERE prop_key = 'fina2.database.schemaVersion';


-- Create table SYS_USER_MDT
create table SYS_USER_MDT (
  USER_ID NUMBER(10,0),
  NODE_ID NUMBER(10,0),
  CAN_AMEND NUMBER(1,0) default 0,
  FOREIGN KEY ("NODE_ID")
	  REFERENCES "IN_MDT_NODES" ("ID") ENABLE, 
	 FOREIGN KEY ("USER_ID")
	  REFERENCES "SYS_USERS" ("ID") ENABLE
);

/*
 max number of days to perform audit log sort
*/
DELETE FROM SYS_PROPERTIES WHERE PROP_KEY = 'net.fina.audit.log.sort.max.days';
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('net.fina.audit.log.sort.max.days', 31);
