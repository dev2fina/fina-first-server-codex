/*==============================================================*/
/* DBMS name:      ORACLE Version 10gR2                         */
/* Created on:     12/22/2008 4:47:42 PM                        */
/*==============================================================*/


DROP view RESULT_VIEW;

DROP INDEX BANKS_TYPEID;

DROP INDEX BANKS_CODE;

DROP TABLE IN_BANKS CASCADE CONSTRAINTS;

DROP TABLE IN_BANK_BRANCHES CASCADE CONSTRAINTS;

DROP INDEX BANKGROUPS_CODE;

DROP TABLE IN_BANK_GROUPS CASCADE CONSTRAINTS;

DROP TABLE IN_BANK_MANAGEMENT CASCADE CONSTRAINTS;

DROP INDEX BANKTYPES_NAME;

DROP INDEX BANKTYPES_CODE;

DROP TABLE IN_BANK_TYPES CASCADE CONSTRAINTS;

DROP TABLE IN_BRANCH_MANAGEMENT CASCADE CONSTRAINTS;

DROP TABLE IN_CRITERION CASCADE CONSTRAINTS;

DROP INDEX DEFTABLES_TYPE;

DROP INDEX DEFTABLES_NODEID;

DROP INDEX DEFTABLES_ID;

DROP INDEX DEFTABLES_DEFINITIONID;

DROP TABLE IN_DEFINITION_TABLES CASCADE CONSTRAINTS;

DROP TABLE IN_IMPORTED_RETURNS CASCADE CONSTRAINTS;

DROP TABLE IN_LICENCES CASCADE CONSTRAINTS;

DROP TABLE IN_LICENCE_TYPES CASCADE CONSTRAINTS;

DROP TABLE IN_MANAGING_BODIES CASCADE CONSTRAINTS;

DROP INDEX MDT_COMP_NODEID;

DROP INDEX MDT_COMP_ID;

DROP TABLE IN_MDT_COMPARISON CASCADE CONSTRAINTS;

DROP INDEX DEPEND_NODEID;

DROP INDEX DEPEND_DEPNODEID;

DROP TABLE IN_MDT_DEPENDENT_NODES CASCADE CONSTRAINTS;

DROP INDEX MDT_PARENTID;

DROP INDEX MDT_CODE;

DROP TABLE IN_MDT_NODES CASCADE CONSTRAINTS;

DROP TABLE IN_PERIODS CASCADE CONSTRAINTS;

DROP TABLE IN_PERIOD_TYPES CASCADE CONSTRAINTS;

DROP TABLE IN_REGIONS CASCADE CONSTRAINTS;

DROP INDEX RETURNS_SCHEDULEID;

DROP TABLE IN_RETURNS CASCADE CONSTRAINTS;

DROP INDEX DEF_CODE;

DROP TABLE IN_RETURN_DEFINITIONS CASCADE CONSTRAINTS;

DROP INDEX RETURN_ITEMS_TABLEID;

DROP INDEX RETURN_ITEMS_ROWNUMBER;

DROP INDEX RETURN_ITEMS_RETURNID;

DROP INDEX RETURN_ITEMS_NODEID;

DROP INDEX RETURN_ITEMS_ID;

DROP INDEX RETURN_COMPLEX2;

DROP TABLE IN_RETURN_ITEMS CASCADE CONSTRAINTS;

DROP TABLE IN_RETURN_STATUSES CASCADE CONSTRAINTS;

DROP INDEX RETTYPES_NAME;

DROP INDEX RETTYPES_CODE;

DROP TABLE IN_RETURN_TYPES CASCADE CONSTRAINTS;

DROP TABLE IN_RETURN_VERSIONS CASCADE CONSTRAINTS;

DROP INDEX SCHEDULES_PERIODID;

DROP INDEX SCHEDULES_DEFINITIONID;

DROP INDEX SCHEDULES_COMPLEX1;

DROP INDEX SCHEDULES_BANKID;

DROP TABLE IN_SCHEDULES CASCADE CONSTRAINTS;

DROP TABLE MM_BANK_GROUP CASCADE CONSTRAINTS;

DROP TABLE OUT_REPORTS CASCADE CONSTRAINTS;

DROP TABLE OUT_REPORTS_LANG CASCADE CONSTRAINTS;

DROP TABLE OUT_REPORTS_SCHEDULE CASCADE CONSTRAINTS;

DROP TABLE OUT_REPOSITORY CASCADE CONSTRAINTS;

DROP TABLE OUT_REPOSITORY_PARAMS CASCADE CONSTRAINTS;

DROP TABLE OUT_STORED_REPORTS CASCADE CONSTRAINTS;

DROP TABLE SYS_LANGUAGES CASCADE CONSTRAINTS;

DROP TABLE SYS_MENUS CASCADE CONSTRAINTS;

DROP TABLE SYS_PERMISSIONS CASCADE CONSTRAINTS;

DROP TABLE SYS_PROPERTIES CASCADE CONSTRAINTS;

DROP TABLE SYS_ROLES CASCADE CONSTRAINTS;

DROP TABLE SYS_ROLE_MENUS CASCADE CONSTRAINTS;

DROP TABLE SYS_ROLE_PERMISSIONS CASCADE CONSTRAINTS;

DROP TABLE SYS_ROLE_REPORTS CASCADE CONSTRAINTS;

DROP TABLE SYS_ROLE_RETURNS CASCADE CONSTRAINTS;

DROP TABLE SYS_ROLE_RETURN_VERSIONS CASCADE CONSTRAINTS;

DROP INDEX STRINGS_COMPLEX;

DROP TABLE SYS_STRINGS CASCADE CONSTRAINTS;

DROP TABLE SYS_USERS CASCADE CONSTRAINTS;

DROP TABLE SYS_USERS_ROLES CASCADE CONSTRAINTS;

DROP TABLE SYS_USER_BANKS CASCADE CONSTRAINTS;

DROP TABLE SYS_USER_MENUS CASCADE CONSTRAINTS;

DROP TABLE SYS_USER_PASSWORDS CASCADE CONSTRAINTS;

DROP TABLE SYS_USER_PERMISSIONS CASCADE CONSTRAINTS;

DROP TABLE SYS_USER_REPORTS CASCADE CONSTRAINTS;

DROP TABLE SYS_USER_RETURNS CASCADE CONSTRAINTS;

DROP TABLE SYS_USER_RETURN_VERSIONS CASCADE CONSTRAINTS;

/*==============================================================*/
/* Table: IN_BANKS                                              */
/*==============================================================*/
CREATE TABLE IN_BANKS  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(12),
   TYPEID               NUMBER(10),
   SHORTNAMESTRID       NUMBER(10),
   NAMESTRID            NUMBER(10),
   ADDRESSSTRID         NUMBER(10),
   PHONE                VARCHAR2(40),
   FAX                  VARCHAR2(40),
   EMAIL                VARCHAR2(40),
   TELEX                VARCHAR2(40),
   SWIFTCODE            VARCHAR2(11),
   CONSTRAINT PK_IN_BANKS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Index: BANKS_CODE                                            */
/*==============================================================*/
CREATE UNIQUE INDEX BANKS_CODE ON IN_BANKS (
   CODE ASC
);

/*==============================================================*/
/* Index: BANKS_TYPEID                                          */
/*==============================================================*/
CREATE INDEX BANKS_TYPEID ON IN_BANKS (
   TYPEID ASC
);

/*==============================================================*/
/* Table: IN_BANK_BRANCHES                                      */
/*==============================================================*/
CREATE TABLE IN_BANK_BRANCHES  (
   ID                   NUMBER(10)                      NOT NULL,
   BANKREGIONSTRID      NUMBER(10),
   NAMESTRID            NUMBER(10),
   SHORTNAMESTRID       NUMBER(10),
   ADDRESSSTRID         NUMBER(10),
   COMMENTSSTRID        VARCHAR2(40),
   CREATIONDATE         DATE,
   DATEOFCHANGE         DATE,
   BANKID               NUMBER(10),
   CONSTRAINT PK_IN_BANK_BRANCHES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_BANK_GROUPS                                        */
/*==============================================================*/
CREATE TABLE IN_BANK_GROUPS  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(12),
   NAMESTRID            NUMBER(10),
   CRITERIONID          NUMBER(10),
   CONSTRAINT PK_IN_BANK_GROUPS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Index: BANKGROUPS_CODE                                       */
/*==============================================================*/
CREATE UNIQUE INDEX BANKGROUPS_CODE ON IN_BANK_GROUPS (
   CODE ASC
);

/*==============================================================*/
/* Table: IN_BANK_MANAGEMENT                                    */
/*==============================================================*/
CREATE TABLE IN_BANK_MANAGEMENT  (
   ID                   NUMBER(10)                      NOT NULL,
   NAMESTRID            NUMBER(10),
   LASTNAMESTRID        NUMBER(10),
   MANAGINGBODYID       NUMBER(10),
   POSTSTRID            NUMBER(10),
   PHONE                VARCHAR2(25),
   DATEOFAPPOINTMENT    DATE,
   CANCELDATE           DATE,
   REGISTRATIONSTRID1   NUMBER(10),
   REGISTRATIONSTRID2   NUMBER(10),
   REGISTRATIONSTRID3   NUMBER(10),
   COMMENTSSTRID1       NUMBER(10),
   COMMENTSSTRID2       NUMBER(10),
   BANKID               NUMBER(10),
   CONSTRAINT PK_IN_BANK_MANAGEMENT PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_BANK_TYPES                                         */
/*==============================================================*/
CREATE TABLE IN_BANK_TYPES  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(12),
   NAMESTRID            NUMBER(10),
   CONSTRAINT PK_IN_BANK_TYPES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Index: BANKTYPES_CODE                                        */
/*==============================================================*/
CREATE UNIQUE INDEX BANKTYPES_CODE ON IN_BANK_TYPES (
   CODE ASC
);

/*==============================================================*/
/* Index: BANKTYPES_NAME                                        */
/*==============================================================*/
CREATE UNIQUE INDEX BANKTYPES_NAME ON IN_BANK_TYPES (
   NAMESTRID ASC
);

/*==============================================================*/
/* Table: IN_BRANCH_MANAGEMENT                                  */
/*==============================================================*/
CREATE TABLE IN_BRANCH_MANAGEMENT  (
   ID                   NUMBER(10)                      NOT NULL,
   NAMESTRID            NUMBER(10),
   LASTNAMESTRID        NUMBER(10),
   MANAGINGBODYID       NUMBER(10),
   POSTSTRID            NUMBER(10),
   PHONE                VARCHAR2(25),
   DATEOFAPPOINTMENT    DATE,
   CANCELDATE           DATE,
   REGISTRATIONSTRID1   NUMBER(10),
   REGISTRATIONSTRID2   NUMBER(10),
   REGISTRATIONSTRID3   NUMBER(10),
   COMMENTSSTRID1       NUMBER(10),
   COMMENTSSTRID2       NUMBER(10),
   BRANCHID             NUMBER(10),
   CONSTRAINT PK_IN_BRANCH_MANAGEMENT PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_CRITERION                                          */
/*==============================================================*/
CREATE TABLE IN_CRITERION  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(30),
   NAMESTRID            NUMBER(10),
   ISDEFAULT            NUMBER(1)                       NOT NULL,
   CONSTRAINT PK_IN_CRITERION PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_DEFINITION_TABLES                                  */
/*==============================================================*/
CREATE TABLE IN_DEFINITION_TABLES  (
   ID                   NUMBER(10),
   CODE                 VARCHAR2(12),
   DEFINITIONID         NUMBER(10),
   NODEID               NUMBER(10),
   NODEVISIBLE          NUMBER(10),
   VISIBLELEVEL         NUMBER(10),
   TYPE                 NUMBER(10),
   EVALTYPE             NUMBER(10)
);

/*==============================================================*/
/* Index: DEFTABLES_DEFINITIONID                                */
/*==============================================================*/
CREATE INDEX DEFTABLES_DEFINITIONID ON IN_DEFINITION_TABLES (
   DEFINITIONID ASC
);

/*==============================================================*/
/* Index: DEFTABLES_ID                                          */
/*==============================================================*/
CREATE INDEX DEFTABLES_ID ON IN_DEFINITION_TABLES (
   ID ASC
);

/*==============================================================*/
/* Index: DEFTABLES_NODEID                                      */
/*==============================================================*/
CREATE INDEX DEFTABLES_NODEID ON IN_DEFINITION_TABLES (
   NODEID ASC
);

/*==============================================================*/
/* Index: DEFTABLES_TYPE                                        */
/*==============================================================*/
CREATE INDEX DEFTABLES_TYPE ON IN_DEFINITION_TABLES (
   TYPE ASC
);

/*==============================================================*/
/* Table: IN_IMPORTED_RETURNS                                   */
/*==============================================================*/
CREATE TABLE IN_IMPORTED_RETURNS  (
   ID                   NUMBER(10)                      NOT NULL,
   RETURNCODE           VARCHAR(12),
   BANKCODE             VARCHAR(12),
   VERSIONCODE          VARCHAR(12),
   PERIODSTART          DATE,
   PERIODEND            DATE,
   USERID               NUMBER(10),
   LANGID               NUMBER(10),
   UPLOADTIME           DATE,
   IMPORTSTART          DATE,
   IMPORTEND            DATE,
   STATUS               NUMBER(1),
   CONTENT              BLOB,
   MESSAGE              VARCHAR(4000),
   CONSTRAINT PK_IN_IMPORTED_RETURNS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_LICENCES                                           */
/*==============================================================*/
CREATE TABLE IN_LICENCES  (
   ID                   NUMBER(10)                      NOT NULL,
   TYPEID               NUMBER(10),
   CODE                 VARCHAR2(12),
   CREATIONDATE         DATE,
   DATEOFCHANGE         DATE,
   REASONSTRID          NUMBER(10),
   OPERATIONAL          NUMBER(10),
   BANKID               NUMBER(10),
   CONSTRAINT PK_IN_LICENCES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_LICENCE_TYPES                                      */
/*==============================================================*/
CREATE TABLE IN_LICENCE_TYPES  (
   ID                   NUMBER(10)                      NOT NULL,
   NAMESTRID            NUMBER(10),
   CONSTRAINT PK_IN_LICENCE_TYPES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_MANAGING_BODIES                                    */
/*==============================================================*/
CREATE TABLE IN_MANAGING_BODIES  (
   ID                   NUMBER(10)                      NOT NULL,
   POSTSTRID            NUMBER(10),
   CONSTRAINT PK_IN_MANAGING_BODIES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_MDT_COMPARISON                                     */
/*==============================================================*/
CREATE TABLE IN_MDT_COMPARISON  (
   ID                   NUMBER(10),
   NODEID               NUMBER(10),
   CONDITION            NUMBER(10),
   EQUATION             VARCHAR2(600)
);

/*==============================================================*/
/* Index: MDT_COMP_ID                                           */
/*==============================================================*/
CREATE INDEX MDT_COMP_ID ON IN_MDT_COMPARISON (
   ID ASC
);

/*==============================================================*/
/* Index: MDT_COMP_NODEID                                       */
/*==============================================================*/
CREATE INDEX MDT_COMP_NODEID ON IN_MDT_COMPARISON (
   NODEID ASC
);

/*==============================================================*/
/* Table: IN_MDT_DEPENDENT_NODES                                */
/*==============================================================*/
CREATE TABLE IN_MDT_DEPENDENT_NODES  (
   NODEID               NUMBER(10),
   DEPENDENTNODEID      NUMBER(10)
);

/*==============================================================*/
/* Index: DEPEND_DEPNODEID                                      */
/*==============================================================*/
CREATE INDEX DEPEND_DEPNODEID ON IN_MDT_DEPENDENT_NODES (
   DEPENDENTNODEID ASC
);

/*==============================================================*/
/* Index: DEPEND_NODEID                                         */
/*==============================================================*/
CREATE INDEX DEPEND_NODEID ON IN_MDT_DEPENDENT_NODES (
   NODEID ASC
);

/*==============================================================*/
/* Table: IN_MDT_NODES                                          */
/*==============================================================*/
CREATE TABLE IN_MDT_NODES  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(30),
   NAMESTRID            NUMBER(10),
   PARENTID             NUMBER(10),
   TYPE                 NUMBER(10),
   DATATYPE             NUMBER(10),
   EQUATION             VARCHAR2(3700),
   SEQUENCE             NUMBER(10),
   EVALMETHOD           NUMBER(10),
   DISABLED             NUMBER(10),
   REQUIRED             NUMBER(10),
   CONSTRAINT PK_IN_MDT_NODES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Index: MDT_CODE                                              */
/*==============================================================*/
CREATE UNIQUE INDEX MDT_CODE ON IN_MDT_NODES (
   CODE ASC
);

/*==============================================================*/
/* Index: MDT_PARENTID                                          */
/*==============================================================*/
CREATE INDEX MDT_PARENTID ON IN_MDT_NODES (
   PARENTID ASC
);

/*==============================================================*/
/* Table: IN_PERIODS                                            */
/*==============================================================*/
CREATE TABLE IN_PERIODS  (
   ID                   NUMBER(10)                      NOT NULL,
   PERIODNUMBER         NUMBER(10),
   FROMDATE             DATE,
   TODATE               DATE,
   PERIODTYPEID         NUMBER(10),
   CONSTRAINT PK_IN_PERIODS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_PERIOD_TYPES                                       */
/*==============================================================*/
CREATE TABLE IN_PERIOD_TYPES  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(12),
   NAMESTRID            NUMBER(10),
   CONSTRAINT PK_IN_PERIOD_TYPES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_REGIONS                                            */
/*==============================================================*/
CREATE TABLE IN_REGIONS  (
   ID                   NUMBER(10)                      NOT NULL,
   CITYSTRID            NUMBER(10),
   REGIONSTRID          NUMBER(10),
   CONSTRAINT PK_IN_REGIONS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_RETURNS                                            */
/*==============================================================*/
CREATE TABLE IN_RETURNS  (
   ID                   NUMBER(10)                      NOT NULL,
   SCHEDULEID           NUMBER(10),
   VERSIONID            NUMBER(10),
   CONSTRAINT PK_IN_RETURNS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Index: RETURNS_SCHEDULEID                                    */
/*==============================================================*/
CREATE UNIQUE INDEX RETURNS_SCHEDULEID ON IN_RETURNS (
   SCHEDULEID ASC
);

/*==============================================================*/
/* Table: IN_RETURN_DEFINITIONS                                 */
/*==============================================================*/
CREATE TABLE IN_RETURN_DEFINITIONS  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(12),
   NAMESTRID            NUMBER(10),
   TYPEID               NUMBER(10),
   FORMAT               LONG RAW,
   CONSTRAINT PK_IN_RETURN_DEFINITIONS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Index: DEF_CODE                                              */
/*==============================================================*/
CREATE INDEX DEF_CODE ON IN_RETURN_DEFINITIONS (
   CODE ASC
);

/*==============================================================*/
/* Table: IN_RETURN_ITEMS                                       */
/*==============================================================*/
CREATE TABLE IN_RETURN_ITEMS  (
   ID                   NUMBER(10),
   RETURNID             NUMBER(10),
   TABLEID              NUMBER(10),
   NODEID               NUMBER(10),
   ROWNUMBER            NUMBER(10),
   VALUE                VARCHAR2(255),
   NVALUE               FLOAT,
   VERSIONID            NUMBER(10)
);

/*==============================================================*/
/* Index: RETURN_COMPLEX2                                       */
/*==============================================================*/
CREATE INDEX RETURN_COMPLEX2 ON IN_RETURN_ITEMS (
   RETURNID ASC,
   NODEID ASC,
   TABLEID ASC
);

/*==============================================================*/
/* Index: RETURN_ITEMS_ID                                       */
/*==============================================================*/
CREATE INDEX RETURN_ITEMS_ID ON IN_RETURN_ITEMS (
   ID ASC
);

/*==============================================================*/
/* Index: RETURN_ITEMS_NODEID                                   */
/*==============================================================*/
CREATE INDEX RETURN_ITEMS_NODEID ON IN_RETURN_ITEMS (
   NODEID ASC
);

/*==============================================================*/
/* Index: RETURN_ITEMS_RETURNID                                 */
/*==============================================================*/
CREATE INDEX RETURN_ITEMS_RETURNID ON IN_RETURN_ITEMS (
   RETURNID ASC
);

/*==============================================================*/
/* Index: RETURN_ITEMS_ROWNUMBER                                */
/*==============================================================*/
CREATE INDEX RETURN_ITEMS_ROWNUMBER ON IN_RETURN_ITEMS (
   ROWNUMBER ASC
);

/*==============================================================*/
/* Index: RETURN_ITEMS_TABLEID                                  */
/*==============================================================*/
CREATE INDEX RETURN_ITEMS_TABLEID ON IN_RETURN_ITEMS (
   TABLEID ASC
);

/*==============================================================*/
/* Table: IN_RETURN_STATUSES                                    */
/*==============================================================*/
CREATE TABLE IN_RETURN_STATUSES  (
   ID                   NUMBER(10),
   RETURNID             NUMBER(10),
   STATUS               NUMBER(10),
   STATUSDATE           DATE,
   USERID               NUMBER(10),
   NOTE                 LONG,
   VERSIONID            NUMBER(10)
);

/*==============================================================*/
/* Table: IN_RETURN_TYPES                                       */
/*==============================================================*/
CREATE TABLE IN_RETURN_TYPES  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(12),
   NAMESTRID            NUMBER(10),
   CONSTRAINT PK_IN_RETURN_TYPES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Index: RETTYPES_CODE                                         */
/*==============================================================*/
CREATE UNIQUE INDEX RETTYPES_CODE ON IN_RETURN_TYPES (
   CODE ASC
);

/*==============================================================*/
/* Index: RETTYPES_NAME                                         */
/*==============================================================*/
CREATE UNIQUE INDEX RETTYPES_NAME ON IN_RETURN_TYPES (
   NAMESTRID ASC
);

/*==============================================================*/
/* Table: IN_RETURN_VERSIONS                                    */
/*==============================================================*/
CREATE TABLE IN_RETURN_VERSIONS  (
   ID                 NUMBER(10)                      NOT NULL,
   CODE               VARCHAR(12),
   SEQUENCE           NUMBER(10),
   DESCSTRID          NUMBER(10),
   CONSTRAINT PK_IN_RETURN_VERSIONS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: IN_SCHEDULES                                          */
/*==============================================================*/
CREATE TABLE IN_SCHEDULES  (
   ID                   NUMBER(10)                      NOT NULL,
   BANKID               NUMBER(10),
   DEFINITIONID         NUMBER(10),
   PERIODID             NUMBER(10),
   DELAY                NUMBER(10),
   CONSTRAINT PK_IN_SCHEDULES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Index: SCHEDULES_BANKID                                      */
/*==============================================================*/
CREATE INDEX SCHEDULES_BANKID ON IN_SCHEDULES (
   BANKID ASC
);

/*==============================================================*/
/* Index: SCHEDULES_COMPLEX1                                    */
/*==============================================================*/
CREATE INDEX SCHEDULES_COMPLEX1 ON IN_SCHEDULES (
   BANKID ASC,
   PERIODID ASC
);

/*==============================================================*/
/* Index: SCHEDULES_DEFINITIONID                                */
/*==============================================================*/
CREATE INDEX SCHEDULES_DEFINITIONID ON IN_SCHEDULES (
   DEFINITIONID ASC
);

/*==============================================================*/
/* Index: SCHEDULES_PERIODID                                    */
/*==============================================================*/
CREATE INDEX SCHEDULES_PERIODID ON IN_SCHEDULES (
   PERIODID ASC
);

/*==============================================================*/
/* Table: MM_BANK_GROUP                                         */
/*==============================================================*/
CREATE TABLE MM_BANK_GROUP  (
   BANKID               NUMBER(10)                      NOT NULL,
   BANKGROUPID          NUMBER(10)                      NOT NULL,
   CONSTRAINT PK_MM_BANK_GROUP PRIMARY KEY (BANKID, BANKGROUPID)
);

/*==============================================================*/
/* Table: OUT_REPORTS                                           */
/*==============================================================*/
CREATE TABLE OUT_REPORTS  (
   ID                   NUMBER(10)                      NOT NULL,
   PARENTID             NUMBER(10),
   NAMESTRID            NUMBER(10),
   TYPE                 NUMBER(10),
   TEMPLATE             LONG RAW,
   INFO                 RAW(2000),
   SEQUENCE             NUMBER(10),
   CONSTRAINT PK_OUT_REPORTS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: OUT_REPORTS_LANG                                      */
/*==============================================================*/
CREATE TABLE OUT_REPORTS_LANG  (
   REPORTID             NUMBER(10),
   LANGID               NUMBER(10),
   TEMPLATE             LONG RAW
);

/*==============================================================*/
/* Table: OUT_REPORTS_SCHEDULE                                  */
/*==============================================================*/
CREATE TABLE OUT_REPORTS_SCHEDULE  (
   REPORTID             NUMBER(10)                      NOT NULL,
   LANGID               NUMBER(10)                      NOT NULL,
   INFO                 LONG RAW                        NOT NULL,
   HASHCODE             NUMBER(10)                      NOT NULL,
   STATUS               NUMBER(10),
   ONDEMAND             NUMBER(10),
   SCHEDULETIME         DATE,
   USERID               NUMBER(10)                      NOT NULL
);

/*==============================================================*/
/* Table: OUT_REPOSITORY                                        */
/*==============================================================*/
CREATE TABLE OUT_REPOSITORY  (
   ID                   NUMBER(10)                      NOT NULL,
   PARENTID             NUMBER(10),
   NAME                 VARCHAR2(50)                    NOT NULL,
   SCRIPT               VARCHAR2(2048),
   TYPE                 NUMBER(10)                      NOT NULL,
   CONSTRAINT PK_OUT_REPOSITORY PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: OUT_REPOSITORY_PARAMS                                 */
/*==============================================================*/
CREATE TABLE OUT_REPOSITORY_PARAMS  (
   ID                   NUMBER(10)                      NOT NULL,
   FORMULAID            NUMBER(10)                      NOT NULL,
   TYPE                 NUMBER(10)                      NOT NULL,
   NAME                 VARCHAR2(50)
);

/*==============================================================*/
/* Table: OUT_STORED_REPORTS                                    */
/*==============================================================*/
CREATE TABLE OUT_STORED_REPORTS  (
   REPORTID             NUMBER(10)                      NOT NULL,
   LANGID               NUMBER(10)                      NOT NULL,
   INFO                 BLOB                            NOT NULL,
   REPORTRESULT         BLOB                            NOT NULL,
   HASHCODE             NUMBER(10)                      NOT NULL,
   USERID               NUMBER(10)                      NOT NULL,
   STOREDATE            DATE
);

/*==============================================================*/
/* Table: SYS_LANGUAGES                                         */
/*==============================================================*/
CREATE TABLE SYS_LANGUAGES  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(12),
   NAME                 VARCHAR2(24),
   DATEFORMAT           VARCHAR2(24),
   NUMBERFORMAT         VARCHAR2(24),
   FONTFACE             VARCHAR2(24),
   FONTSIZE             NUMBER(10),
   HTMLCHARSET          VARCHAR2(20),
   XMLENCODING          VARCHAR2(20),
   CONSTRAINT PK_SYS_LANGUAGES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: SYS_MENUS                                             */
/*==============================================================*/
CREATE TABLE SYS_MENUS  (
   ID                   NUMBER(10)                      NOT NULL,
   PARENTID             NUMBER(10),
   NAMESTRID            NUMBER(10),
   TYPE                 NUMBER(10),
   ACTIONKEY            VARCHAR2(40),
   APPLICATION          VARCHAR2(80),
   SEQUENCE             NUMBER(10),
   CONSTRAINT PK_SYS_MENUS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: SYS_PERMISSIONS                                       */
/*==============================================================*/
CREATE TABLE SYS_PERMISSIONS  (
   ID                   NUMBER(10)                      NOT NULL,
   NAMESTRID            NUMBER(10),
   IDNAME               VARCHAR2(80),
   CONSTRAINT PK_SYS_PERMISSIONS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: SYS_PROPERTIES                                        */
/*==============================================================*/
CREATE TABLE SYS_PROPERTIES  (
   PROP_KEY             VARCHAR2(64)                    NOT NULL,
   VALUE                VARCHAR2(256),
   CONSTRAINT PK_SYS_PROPERTIES PRIMARY KEY (PROP_KEY)
);

/*==============================================================*/
/* Table: SYS_ROLES                                             */
/*==============================================================*/
CREATE TABLE SYS_ROLES  (
   ID                   NUMBER(10)                      NOT NULL,
   NAMESTRID            NUMBER(10),
   CODE                 VARCHAR2(12),
   CONSTRAINT PK_SYS_ROLES PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: SYS_ROLE_MENUS                                        */
/*==============================================================*/
CREATE TABLE SYS_ROLE_MENUS  (
   ROLEID               NUMBER(10),
   MENUID               NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_ROLE_PERMISSIONS                                  */
/*==============================================================*/
CREATE TABLE SYS_ROLE_PERMISSIONS  (
   ROLEID               NUMBER(10),
   PERMISSIONID         NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_ROLE_REPORTS                                      */
/*==============================================================*/
CREATE TABLE SYS_ROLE_REPORTS  (
   ROLE_ID            NUMBER(10),
   REPORT_ID          NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_ROLE_RETURNS                                      */
/*==============================================================*/
CREATE TABLE SYS_ROLE_RETURNS  (
   ROLE_ID            NUMBER(10),
   DEFINITION_ID      NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_ROLE_RETURN_VERSIONS                              */
/*==============================================================*/
CREATE TABLE SYS_ROLE_RETURN_VERSIONS  (
   ROLE_ID            NUMBER(10),
   VERSION_ID         NUMBER(10),
   CAN_AMEND            NUMBER(1)                      DEFAULT 0
);

/*==============================================================*/
/* Table: SYS_STRINGS                                           */
/*==============================================================*/
CREATE TABLE SYS_STRINGS  (
   ID                   NUMBER(10)                      NOT NULL,
   LANGID               NUMBER(10),
   VALUE                VARCHAR2(255)
);

/*==============================================================*/
/* Index: STRINGS_COMPLEX                                       */
/*==============================================================*/
CREATE INDEX STRINGS_COMPLEX ON SYS_STRINGS (
   ID ASC,
   LANGID ASC
);

/*==============================================================*/
/* Table: SYS_USERS                                             */
/*==============================================================*/
CREATE TABLE SYS_USERS  (
   ID                   NUMBER(10)                      NOT NULL,
   LOGIN                VARCHAR2(15),
   PASSWORD             VARCHAR2(40),
   CHANGEPASSWORD       NUMBER(10),
   NAMESTRID            NUMBER(10),
   TITLESTRID           NUMBER(10),
   PHONE                VARCHAR2(40),
   EMAIL                VARCHAR2(40),
   BLOCKED              NUMBER(10),
   LASTLOGINDATE        DATE,
   LASTPASSWORDCHANGEDATE DATE,
   CONSTRAINT PK_SYS_USERS PRIMARY KEY (ID)
);

/*==============================================================*/
/* Table: SYS_USERS_ROLES                                       */
/*==============================================================*/
CREATE TABLE SYS_USERS_ROLES  (
   USERID               NUMBER(10),
   ROLEID               NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_USER_BANKS                                        */
/*==============================================================*/
CREATE TABLE SYS_USER_BANKS  (
   USERID               NUMBER(10),
   BANKID               NUMBER(10),
   CANAMEND             NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_USER_MENUS                                        */
/*==============================================================*/
CREATE TABLE SYS_USER_MENUS  (
   USERID               NUMBER(10),
   MENUID               NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_USER_PASSWORDS                                    */
/*==============================================================*/
CREATE TABLE SYS_USER_PASSWORDS  (
   USERID               NUMBER(10)                      NOT NULL,
   PASSWORD             VARCHAR(40)                     NOT NULL,
   STOREDATE            DATE                            NOT NULL
);

/*==============================================================*/
/* Table: SYS_USER_PERMISSIONS                                  */
/*==============================================================*/
CREATE TABLE SYS_USER_PERMISSIONS  (
   USERID               NUMBER(10),
   PERMISSIONID         NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_USER_REPORTS                                      */
/*==============================================================*/
CREATE TABLE SYS_USER_REPORTS  (
   USERID               NUMBER(10),
   REPORTID             NUMBER(10),
   CANAMEND             NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_USER_RETURNS                                      */
/*==============================================================*/
CREATE TABLE SYS_USER_RETURNS  (
   USER_ID            NUMBER(10),
   DEFINITION_ID      NUMBER(10)
);

/*==============================================================*/
/* Table: SYS_USER_RETURN_VERSIONS                              */
/*==============================================================*/
CREATE TABLE SYS_USER_RETURN_VERSIONS  (
   USER_ID            NUMBER(10),
   VERSION_ID         NUMBER(10),
   CAN_AMEND            NUMBER(1)                      DEFAULT 0
);

/*==============================================================*/
/* View: RESULT_VIEW                                            */
/*==============================================================*/
CREATE OR REPLACE VIEW RESULT_VIEW AS
SELECT RI.VALUE, RI.NVALUE, RI.NODEID, S.BANKID, P.PERIODTYPEID, P.FROMDATE, P.TODATE, 
P.ID AS PERIODID, RV.CODE AS VERSIONCODE, RV2.CODE AS LATESTVERSIONCODE
     FROM IN_SCHEDULES S,
          IN_PERIODS P,
          IN_RETURNS R,
          IN_RETURN_ITEMS RI,
          IN_RETURN_VERSIONS RV,
          IN_RETURN_VERSIONS RV2
     WHERE
       S.PERIODID = P.ID AND R.SCHEDULEID=S.ID AND RI.RETURNID=R.ID AND RV.ID = RI.VERSIONID AND RV2.ID = R.VERSIONID;

