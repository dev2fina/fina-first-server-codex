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
  ID                          NUMBER NOT NULL,
  ENTITY_ID                   VARCHAR2(256) NOT NULL,
  ENTITY_NAME                 VARCHAR2(256) NOT NULL,
  ENTITY_PROPERTY             VARCHAR2(4000) NOT NULL,
  ENTITY_PROPERTY_NEW_VALUE   VARCHAR2(4000),
  ENTITY_PROPERTY_OLD_VALUE   VARCHAR2(4000),
  OPERATION_TYPE              NUMBER,
  ACTOR_ID                    VARCHAR2(50) NOT NULL,
  RELEVANCE_TIME              TIMESTAMP(6) NOT NULL,
  CONSTRAINT PK_AUDIT_TRAIL PRIMARY KEY (ID)
)

/*
 * MDT Release Version
 */
delete from sys_properties where prop_key in('net.fina.mdt.releaseVersion');
insert into sys_properties(prop_key,value) values('net.fina.mdt.releaseVersion','0');