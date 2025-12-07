/*
 Database: Oracle 11g
 
 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 0.1
 Date : 15/11/2012
*/

/*
  Update DB Version
*/
update sys_properties set value='4.2.1' where prop_key='fina2.database.schemaVersion';

/*
 * Table SYS_AUDIT_LOG
 */
CREATE TABLE SYS_AUDIT_LOG
(
  ID                          numeric(10) NOT NULL,
  ENTITY_ID                   VARCHAR(256) NOT NULL,
  ENTITY_NAME                 VARCHAR(256) NOT NULL,
  ENTITY_PROPERTY             VARCHAR(4000) NOT NULL,
  ENTITY_PROPERTY_NEW_VALUE   VARCHAR(4000),
  ENTITY_PROPERTY_OLD_VALUE   VARCHAR(4000),
  OPERATION_TYPE              int,
  ACTOR_ID                    VARCHAR(50) NOT NULL,
  RELEVANCE_TIME              datetime NOT NULL,
  CONSTRAINT PK_AUDIT_TRAIL PRIMARY KEY (ID)
)

/*
 * MDT Release Version
 */
delete from sys_properties where prop_key in('net.fina.mdt.releaseVersion');
insert into sys_properties(prop_key,value) values('net.fina.mdt.releaseVersion','0');