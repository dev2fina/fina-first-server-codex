/*==============================================================*/
/* CHANGES NEEDED FOR Banks and Report Modules Enhancements     */
/*==============================================================*/

ALTER TABLE IN_BANK_GROUPS ADD CRITERIONID NUMBER(10);

/*==============================================================*/
/* Table: IN_CRITERION                                          */
/*==============================================================*/
CREATE TABLE IN_CRITERION  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(30),
   NAMESTRID            NUMBER(10),
   ISDEFAULT            NUMBER(1),
   CONSTRAINT PK_IN_CRITERION PRIMARY KEY (ID)
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
/* Table: POPULATE DATA                                         */
/*==============================================================*/

declare 
  MAXID NUMBER;
begin
  -- Test statements here
  select max(id) + 1 into MAXID from SYS_STRINGS;
  insert into SYS_STRINGS (id, langID, value) values (MAXID, 1, 'Default Criterion');
  insert into IN_CRITERION (id, code, nameStrID) values (1, 'Def_Criterion', MAXID);
  update IN_BANK_GROUPS set criterionid = 1
  insert into MM_BANK_GROUP (bankid, bankgroupid) select b.id, g.id from IN_BANKS b, IN_BANK_GROUPS g where b.groupid=g.id;
end;
.
run;

/*==============================================================*/
/* Table: TMP_IN_BANKS                                          */
/*==============================================================*/
CREATE TABLE TMP_IN_BANKS  (
   ID                   NUMBER(10)                      NOT NULL,
   CODE                 VARCHAR2(12),
   TYPEID               NUMBER(10),
   GROUPID              NUMBER(10),
   SHORTNAMESTRID       NUMBER(10),
   NAMESTRID            NUMBER(10),
   ADDRESSSTRID         NUMBER(10),
   PHONE                VARCHAR2(40),
   FAX                  VARCHAR2(40),
   EMAIL                VARCHAR2(40),
   TELEX                VARCHAR2(40),
   SWIFTCODE            VARCHAR2(11)
);

INSERT INTO TMP_IN_BANKS (ID, CODE, TYPEID, GROUPID, SHORTNAMESTRID, NAMESTRID, ADDRESSSTRID, PHONE, FAX, EMAIL, TELEX, SWIFTCODE)
SELECT ID, CODE, TYPEID, GROUPID, SHORTNAMESTRID, NAMESTRID, ADDRESSSTRID, PHONE, FAX, EMAIL, TELEX, SWIFTCODE
FROM IN_BANKS;

DROP TABLE IN_BANKS CASCADE CONSTRAINTS;

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

INSERT INTO IN_BANKS (ID, CODE, TYPEID, SHORTNAMESTRID, NAMESTRID, ADDRESSSTRID, PHONE, FAX, EMAIL, TELEX, SWIFTCODE)
SELECT ID, CODE, TYPEID, SHORTNAMESTRID, NAMESTRID, ADDRESSSTRID, PHONE, FAX, EMAIL, TELEX, SWIFTCODE
FROM TMP_IN_BANKS;

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

DROP TABLE TMP_IN_BANKS CASCADE CONSTRAINTS;

/*==============================================================*/
/* Table: OUT_STORED_REPORTS                                    */
/*==============================================================*/

alter table 
   add userID NUMBER(10) null;

alter table OUT_STORED_REPORTS
   add storeDate DATE null;

update OUT_STORED_REPORTS set userID = 1, storeDate=sysdate;

/*==============================================================*/
/* Table: OUT_REPORTS_SCHEDULE                                  */
/*==============================================================*/

drop table OUT_REPORTS_SCHEDULE;

create table OUT_REPORTS_SCHEDULE (
   reportID             NUMBER(10)                  not null,
   langID               NUMBER(10)                  not null,
   info                 BLOB                        not null,
   hashCode             NUMBER(10)                  not null,
   paramsDescription    VARCHAR2(256)               null,
   done                 NUMBER(10)                  null,
   status               NUMBER(10)                  null,
   onDemand             NUMBER(10)                  null,
   scheduleTime         DATE                        null,
   userID               NUMBER(10)                  null
);

insert into SYS_STRINGS (id, langId, value) values (900001, 1, 'Reports Scheduler Manager');
insert into SYS_STRINGS (id, langId, value) values (900002, 1, 'Reports Schedules Add');
insert into SYS_STRINGS (id, langId, value) values (900003, 1, 'Stored Reports Manager');

insert into SYS_PERMISSIONS (id, nameStrId, idName) values (31, 900001, 'fina2.reports.scheduler.manager');
insert into SYS_PERMISSIONS (id, nameStrId, idName) values (32, 900002, 'fina2.reports.scheduler.add');
insert into SYS_PERMISSIONS (id, nameStrId, idName) values (33, 900003, 'fina2.reports.stored.manager');