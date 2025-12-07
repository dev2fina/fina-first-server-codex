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

