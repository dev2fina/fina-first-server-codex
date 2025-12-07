/*==============================================================*/
/* Alter SYS_USERS table and populate it with default values    */
/*==============================================================*/
alter table SYS_USERS add blocked int
go

alter table SYS_USERS add lastLoginDate DATETIME
go

alter table SYS_USERS add lastPasswordChangeDate DATETIME
go

update SYS_USERS set blocked = 0, lastLoginDate = getdate(), lastPasswordChangeDate = getdate()
go

/*==============================================================*/
/* Add SYS_USER_PASSWORDS table                                 */
/*==============================================================*/
create table SYS_USER_PASSWORDS
(
  USERID    int not null,
  PASSWORD  VARCHAR(256) not null,
  STOREDATE DATETIME not null
)
go

/*==============================================================*/
/* Add SYS_PROPERTIES table and populate it                     */
/*==============================================================*/
create table SYS_PROPERTIES (
   "key"                varchar(64)          not null,
   value                varchar(256)         null,
   constraint PK_SYS_PROPERTIES primary key  ("key")
)
go

insert into sys_properties ("key", value) values ('fina2.security.allowedNumberLoginAttempt', '3')
go

insert into sys_properties ("key", value) values ('fina2.security.allowedAccountInactivityPerioed', '60')
go

insert into sys_properties ("key", value) values ('fina2.security.numberOfStoredOldPasswords', '13')
go

insert into sys_properties ("key", value) values ('fina2.security.passwordMinimalLen', '8')
go

insert into sys_properties ("key", value) values ('fina2.security.passwordWithNumsChars', '1')
go

insert into sys_properties ("key", value) values ('fina2.security.passwordValidityPeriod', '90')
go

/*==============================================================*/
/* Add permission for Security Settings action                  */
/*==============================================================*/
declare @ss_id int
declare @sp_id int

set @ss_id = (select max(id) from SYS_STRINGS) + 1
set @sp_id = (select max(id) from SYS_PERMISSIONS) + 1

insert into SYS_STRINGS (id, langID, value) values (@ss_id, 1, 'Security Settings')
insert into SYS_PERMISSIONS (id, nameStrId, idName) values (@sp_id, @ss_id, 'fina2.security.settings');

go

/*==============================================================*/
/* Add and populate IN_RETURN_VERSIONS                          */
/*==============================================================*/
drop TABLE IN_RETURN_VERSIONS
go

create table IN_RETURN_VERSIONS (
   id                   int                  not null,
   code                 varchar(12)          null,
   sequence             int                  null,
   descStrID            int                  null,
   constraint PK_IN_RETURN_VERSIONS primary key  (id)
)
go

declare @ss_id int

set @ss_id = (select max(id) from SYS_STRINGS) + 1

insert into SYS_STRINGS (id, langID, value) values (@ss_id, 1, 'Original Version');

insert into IN_RETURN_VERSIONS (id, code, sequence, descStrID) values (1, 'ORIG', 1, @ss_id);

go

/*==============================================================*/
/* Alter IN_RETURN_STATUSES table                               */
/*==============================================================*/
alter table IN_RETURN_STATUSES add ID int
go

create index statuses_ind ON IN_RETURN_STATUSES(STATUSDATE)
go


declare @i integer
declare statuses_cursor cursor FOR select ID from IN_RETURN_STATUSES order by STATUSDATE for update of ID

set @i = 1

open statuses_cursor
fetch next from statuses_cursor
while @@FETCH_STATUS = 0
begin
  update IN_RETURN_STATUSES set ID = @i from IN_RETURN_STATUSES where current of statuses_cursor
  fetch next from statuses_cursor
  set @i = @i + 1
end

close statuses_cursor
deallocate statuses_cursor
go

drop index IN_RETURN_STATUSES.statuses_ind
go

alter table IN_RETURN_STATUSES alter column ID int not null
go

alter table IN_RETURN_STATUSES add constraint PK_IN_RETURN_STATUSES primary key (ID)
go


/*==============================================================*/
/* Alter IN_RETURN_ITEMS and IN_RETURN_STATUSES tables          */
/*==============================================================*/
alter table IN_RETURN_ITEMS add versionID int
go

alter table IN_RETURN_STATUSES add versionID int
go

update IN_RETURN_ITEMS set versionID = 1
go

update IN_RETURN_STATUSES set versionID = 1
go

/*==============================================================*/
/* Add permission for Return Version actions                    */
/*==============================================================*/
declare @ss_id int
declare @sp_id int

set @ss_id = (select max(id) from SYS_STRINGS) + 1
set @sp_id = (select max(id) from SYS_PERMISSIONS) + 1
  
insert into SYS_STRINGS (id, langID, value) values (@ss_id, 1, 'Return Version Amend');
insert into SYS_STRINGS (id, langID, value) values (@ss_id + 1, 1, 'Return Version Delete');
insert into SYS_STRINGS (id, langID, value) values (@ss_id + 2, 1, 'Return Version Review');

insert into SYS_PERMISSIONS (id, nameStrId, idName) values (@sp_id, @ss_id, 'fina2.returns.version.amend');
insert into SYS_PERMISSIONS (id, nameStrId, idName) values (@sp_id + 1, @ss_id + 1, 'fina2.returns.version.delete');
insert into SYS_PERMISSIONS (id, nameStrId, idName) values (@sp_id + 2, @ss_id + 2, 'fina2.returns.version.review');
go

/*==============================================================*/
/* Replace RESULT_VIEW view                                     */
/*==============================================================*/
DROP VIEW RESULT_VIEW
go

CREATE VIEW RESULT_VIEW AS
SELECT RI.VALUE, RI.NVALUE, RI.NODEID, S.BANKID, P.PERIODTYPEID, P.FROMDATE, P.TODATE, 
P.ID AS PERIODID, RV.CODE AS VERSIONCODE,
(SELECT RV2.CODE FROM IN_RETURN_VERSIONS RV2 WHERE RV2.ID = (SELECT MAX(RI2.VERSIONID) FROM IN_RETURNS R2, IN_RETURN_ITEMS RI2 where R2.ID=R.ID and R2.ID = RI2.RETURNID)) AS LATESTVERSIONCODE
     FROM IN_SCHEDULES S,
          IN_PERIODS P,
          IN_RETURNS R,
          IN_RETURN_ITEMS RI,
          IN_RETURN_VERSIONS RV
     WHERE
       S.PERIODID = P.ID AND R.SCHEDULEID=S.ID AND RI.RETURNID=R.ID AND RV.ID = RI.VERSIONID
go
