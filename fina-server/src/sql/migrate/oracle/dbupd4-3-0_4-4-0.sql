/*
 Author: Mikheil Tchelidze
 E: chelomisha@fina2.net
 Version: 1.0
 Date : 07/04/2014
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '4.4.0'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
  Add SEQUENCE column at the Table IN_DEFINITION_TABLES
 */
ALTER TABLE IN_DEFINITION_TABLES ADD SEQUENCE NUMERIC(10, 0);
COMMIT;
-- because DefinitionTable constructor has primitive type parameter.
UPDATE IN_DEFINITION_TABLES
SET SEQUENCE = 0;


CREATE TABLE IN_ABD_AGENT_STATUSES
(
    ID                NUMBER(19, 0) NOT NULL
  , ACTIVE_STATUS     NUMBER(10, 0)
  , STATUS            NUMBER(10, 0) NOT NULL
  , STATUS_DATE       TIMESTAMP(6)
  , ABD_AGENT_ID      NUMBER(19, 0)
  , NOTE              VARCHAR2(1000 BYTE)
  , BANK_ID           NUMBER(10, 0)
  , TRANSACTION_LIMIT FLOAT(126)
);

CREATE TABLE IN_ABD_AGENTS
(
    ID             NUMBER(19, 0) NOT NULL
  , ADDRESS        VARCHAR2(255 CHAR)
  , CODE           VARCHAR2(255 CHAR)
  , DATE_APPOINTED TIMESTAMP(6)
  , EMAIL          VARCHAR2(255 CHAR)
  , LGA_ID         NUMBER(18, 0)
  , TYPE           NUMBER(10, 0)
  , NAME           VARCHAR2(255 CHAR)
  , PHONE          VARCHAR2(255 CHAR)
  , STATE_ID       NUMBER(18, 0)
  , SN             NUMBER(19, 0)
);

/**
  ABD agent hibernate envers table

  http://envers.jboss.org/
 */
CREATE TABLE IN_ABD_AGENTS_AUD
(
    ID             NUMBER(19, 0) NOT NULL
  , REV            NUMBER(10, 0) NOT NULL
  , REVTYPE        NUMBER(3, 0)
  , ADDRESS        VARCHAR2(255 CHAR)
  , CODE           VARCHAR2(255 CHAR) 
  , DATE_APPOINTED TIMESTAMP(6)
  , EMAIL          VARCHAR2(255 CHAR)
  , NAME           VARCHAR2(255 CHAR)
  , PHONE          VARCHAR2(255 CHAR)
  , TYPE           NUMBER(10, 0)
  , LGA_ID         NUMBER(19, 0)
  , STATE_ID       NUMBER(19, 0)
  , SN             NUMBER(19, 0)
);

/**
  hibernate envers REVINFO table

  http://envers.jboss.org/
 */
CREATE TABLE REVINFO
(
    REV      NUMBER(10, 0) NOT NULL
  , REVTSTMP NUMBER(19, 0)
);

CREATE TABLE IN_COUNTRY_DATA_AUD
(
    ID        NUMBER(19, 0) NOT NULL
  , REV       NUMBER(10, 0) NOT NULL
  , REVTYPE   NUMBER(3, 0)
  , CODE      VARCHAR2(255 CHAR)
  , NAMESTRID NUMBER(19, 0)
  , PARENTID  NUMBER(19, 0)
  , SEQUENCE  NUMBER(19, 0)
);
COMMIT;

CREATE TABLE IN_PACKAGES
(
  ID NUMBER(19, 0) NOT NULL
, CODE VARCHAR2(255 CHAR)
, NOTE VARCHAR2(255 CHAR)
, CONSTRAINT SYS_C0012328 PRIMARY KEY ( ID )
);

CREATE TABLE IN_PACKAGE_FITYPES
(
  PACKAGE_ID NUMBER(19, 0) NOT NULL
, FI_TYPE_ID NUMBER(19, 0) NOT NULL
);

CREATE TABLE IN_PACKAGE_RETURNS
(
  PACKAGE_ID NUMBER(19, 0) NOT NULL
, RETURN_DEFINITION_ID NUMBER(19, 0) NOT NULL
);

/*
commit
 */
COMMIT;
                                2