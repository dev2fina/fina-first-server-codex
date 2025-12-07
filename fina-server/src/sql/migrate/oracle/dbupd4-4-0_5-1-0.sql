/*
  Database: Oracle 11g

 Author: Vamekh Goiati
 E: goiati@fina2.net
 Version: 1.0
 Date : 11/06/2015

 Review:

 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 1.1
 Date : 31/08/2015
*/

/*
  Update DB Version
*/

UPDATE sys_properties
SET value = '5.1.0'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
	Create FI licence history table
*/
CREATE TABLE IN_LICENCES_HISTORY(
	ID NUMBER(10, 0) NOT NULL,
	LICENCE_ID NUMBER(10, 0) NOT NULL,
	LICENCE_STATUS NUMBER(10, 0) NOT NULL,
	CHANGE VARCHAR2(4000),
	CHANGE_DATE TIMESTAMP(6) NOT NULL
);

/*
	Add FI licence default column
*/
ALTER TABLE IN_LICENCES ADD ISDEFAULT NUMBER(10, 0);

/*
	Create Fis group change history table
*/
CREATE TABLE IN_BANK_GROUP_HISTORY(
	ID NUMBER(10, 0) NOT NULL,
	BANKID NUMBER(10, 0) NOT NULL,
	BANKGROUPID NUMBER(10, 0) NOT NULL,
	CHANGEDATE DATE
);

/*
	Create FI type groups join table
*/
CREATE TABLE IN_BANK_TYPE_CRITERION(
	BANKTYPEID NUMBER(10, 0) NOT NULL,
	CRITERIONID NUMBER(10, 0) NOT NULL,
	ISDEFAULT NUMBER(10, 0)
);

/*
	Permission update for undefined banks in DCS
*/

DECLARE
 MAX_SS_ID NUMBER;
 MAX_SP_ID NUMBER;
 
 BEGIN         
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;
  select max(id) + 1 into MAX_SP_ID from SYS_PERMISSIONS;
  
  INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, 1, 'net.fina.dcs.undefinedBank');
  
  INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.dcs.undefinedBank');

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_STRINGS)
WHERE PK_COLUMN_NAME ='SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME ='SYS_PERMISSION_MAXID';

commit;
end;

/*
	Add table for responsible users
*/
CREATE TABLE SYS_BANK_USERS (
	BANKID NUMBER(10, 0) NOT NULL,
	USERID NUMBER(10, 0) NOT NULL
);