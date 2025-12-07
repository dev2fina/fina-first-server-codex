/*==============================================================*/
/* Alter SYS_USERS table and populate it with default values    */
/*==============================================================*/
alter table SYS_USERS add blocked NUMBER(10);

alter table SYS_USERS add lastLoginDate DATE;

alter table SYS_USERS add lastPasswordChangeDate DATE;

update SYS_USERS set blocked = 0, lastLoginDate = sysdate, lastPasswordChangeDate = sysdate;

/*==============================================================*/
/* Add SYS_USER_PASSWORDS table                                 */
/*==============================================================*/
create table SYS_USER_PASSWORDS
(
  USERID    NUMBER(10) not null,
  PASSWORD  VARCHAR2(40) not null,
  STOREDATE DATE not null
);

/*==============================================================*/
/* Add SYS_PROPERTIES table and populate it                     */
/*==============================================================*/
create table SYS_PROPERTIES  (
   "KEY"                VARCHAR2(64)                    NOT NULL,
   "VALUE"              VARCHAR2(256),
   CONSTRAINT PK_SYS_PROPERTIES PRIMARY KEY ("KEY")
);

insert into SYS_PROPERTIES (key, value) values ('fina2.security.allowedNumberLoginAttempt', '3');
insert into SYS_PROPERTIES (key, value) values ('fina2.security.allowedAccountInactivityPerioed', '60');
insert into SYS_PROPERTIES (key, value) values ('fina2.security.numberOfStoredOldPasswords', '13');
insert into SYS_PROPERTIES (key, value) values ('fina2.security.passwordMinimalLen', '8');
insert into SYS_PROPERTIES (key, value) values ('fina2.security.passwordWithNumsChars', '1');
insert into SYS_PROPERTIES (key, value) values ('fina2.security.passwordValidityPeriod', '90');


/*==============================================================*/
/* Add permission for Security Settings action                  */
/*==============================================================*/
declare 
  MAX_SS_ID NUMBER;
  MAX_SP_ID NUMBER;
begin
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;
  select max(id) + 1 into MAX_SP_ID from SYS_PERMISSIONS;
  
  insert into SYS_STRINGS (id, langID, value) values (MAX_SS_ID, 1, 'Security Settings');
  insert into SYS_PERMISSIONS (id, nameStrId, idName) values (MAX_SP_ID, MAX_SS_ID, 'fina2.security.settings');

end;
/

/*==============================================================*/
/* Add and populate IN_RETURN_VERSIONS                          */
/*==============================================================*/
CREATE TABLE IN_RETURN_VERSIONS  (
   "ID"                 NUMBER(10)                      NOT NULL,
   "CODE"               VARCHAR(12),
   "SEQUENCE"           NUMBER(10),
   "DESCSTRID"          NUMBER(10),
   CONSTRAINT PK_IN_RETURN_VERSIONS PRIMARY KEY ("ID")
);

declare 
  MAX_SS_ID NUMBER;
begin
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;
  
  insert into SYS_STRINGS (id, langID, value) values (MAX_SS_ID, 1, 'Original Version');
  insert into IN_RETURN_VERSIONS (id, code, sequence, descStrID) values (1, 'ORIG', 1, MAX_SS_ID);

end;
/

/*==============================================================*/
/* Alter IN_RETURN_STATUSES table                               */
/*==============================================================*/
alter table IN_RETURN_STATUSES add ID NUMBER(10);

declare
  i integer;
  CURSOR c IS 
         SELECT ID FROM IN_RETURN_STATUSES ORDER BY STATUSDATE FOR UPDATE;
begin
  i := 1;
  FOR status IN c LOOP
    UPDATE IN_RETURN_STATUSES
      SET id = i
    WHERE CURRENT OF c;
    i := i + 1;
  END LOOP;  
end;
/

alter table IN_RETURN_STATUSES modify ID not null;

alter table IN_RETURN_STATUSES add constraint PK_IN_RETURN_STATUSES primary key (ID);

/*==============================================================*/
/* Alter IN_RETURN_ITEMS and IN_RETURN_STATUSES tables          */
/*==============================================================*/
alter table IN_RETURN_ITEMS add versionID NUMBER(10);

alter table IN_RETURN_STATUSES add versionID NUMBER(10);

update IN_RETURN_ITEMS set versionID = 1;

update IN_RETURN_STATUSES set versionID = 1;

/*==============================================================*/
/* Add permission for Return Version actions                    */
/*==============================================================*/
declare 
  MAX_SS_ID NUMBER;
  MAX_SP_ID NUMBER;
begin
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;
  select max(id) + 1 into MAX_SP_ID from SYS_PERMISSIONS;
  
  insert into SYS_STRINGS (id, langID, value) values (MAX_SS_ID, 1, 'Return Version Amend');
  insert into SYS_STRINGS (id, langID, value) values (MAX_SS_ID + 1, 1, 'Return Version Delete');
  insert into SYS_STRINGS (id, langID, value) values (MAX_SS_ID + 2, 1, 'Return Version Review');

  insert into SYS_PERMISSIONS (id, nameStrId, idName) values (MAX_SP_ID, MAX_SS_ID, 'fina2.returns.version.amend');
  insert into SYS_PERMISSIONS (id, nameStrId, idName) values (MAX_SP_ID + 1, MAX_SS_ID + 1, 'fina2.returns.version.delete');
  insert into SYS_PERMISSIONS (id, nameStrId, idName) values (MAX_SP_ID + 2, MAX_SS_ID + 2, 'fina2.returns.version.review');

end;
/

/*==============================================================*/
/* Replace RESULT_VIEW view                                     */
/*==============================================================*/
CREATE OR REPLACE VIEW RESULT_VIEW AS
SELECT RI.VALUE, RI.NVALUE, RI.NODEID, S.BANKID, P.PERIODTYPEID, P.FROMDATE, P.TODATE, 
P.ID AS PERIODID, RV.CODE AS VERSIONCODE,
(SELECT RV2.CODE FROM IN_RETURN_VERSIONS RV2 WHERE RV2.ID = (SELECT MAX(RI2.VERSIONID) FROM IN_RETURNS R2, IN_RETURN_ITEMS RI2 where R2.ID=R.ID and R2.ID = RI2.RETURNID)) AS LATESTVERSIONCODE
     FROM IN_SCHEDULES S,
          IN_PERIODS P,
          IN_RETURNS R,
          IN_RETURN_ITEMS RI,
          IN_RETURN_VERSIONS RV
     WHERE
       S.PERIODID = P.ID AND R.SCHEDULEID=S.ID AND RI.RETURNID=R.ID AND RV.ID = RI.VERSIONID;

commit;

