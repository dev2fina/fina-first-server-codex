/*
 Database: Oracle 11g, 12c

 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 0.1
 Date : 11/12/2015
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.2'
WHERE prop_key = 'fina2.database.schemaVersion';

--------------------------------------------------------
--  DDL for Table SYS_AUDIT_LOG
--------------------------------------------------------
CREATE TABLE SYS_AUDIT_LOG
   (	"ID" VARCHAR2(60 CHAR),
      "ENTITY_ID" VARCHAR2(256 BYTE),
      "ENTITY_NAME" VARCHAR2(256 BYTE),
      "ENTITY_PROPERTY" VARCHAR2(4000 BYTE),
      "ENTITY_PROPERTY_NEW_VALUE" VARCHAR2(4000 BYTE),
      "ENTITY_PROPERTY_OLD_VALUE" VARCHAR2(4000 BYTE),
      "OPERATION_TYPE" NUMBER,
      "ACTOR_ID" VARCHAR2(50 BYTE),
      "RELEVANCE_TIME" TIMESTAMP (6)
   );
   
INSERT INTO SYS_PROPERTIES VALUES ('net.fina.auditLog.level', 0);

CREATE SEQUENCE  upload_file_sequence  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH upload_file_sequence CACHE 20;
