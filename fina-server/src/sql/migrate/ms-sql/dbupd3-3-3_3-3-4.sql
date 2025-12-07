/*==============================================================*/
/* CHANGES NEEDED FOR Banks and Report Modules Enhancements     */
/*==============================================================*/

alter table IN_BANK_GROUPS
   add criterionid int null
go


/*==============================================================*/
/* Table: IN_CRITERION                                          */
/*==============================================================*/
create table IN_CRITERION (
   id                   int                  not null,
   code                 varchar(30)          null,
   nameStrID            int                  null,
   isDefault            int                  not null,
   constraint PK_IN_CRITERION primary key  (id)
)
go


/*==============================================================*/
/* Table: MM_BANK_GROUP                                         */
/*==============================================================*/
create table MM_BANK_GROUP (
   bankid               int                  null,
   bankgroupid          int                  null
)
go

/*==============================================================*/
/* Table: POPULATE DATA                                         */
/*==============================================================*/

declare @id int
set @id = (select max(id) from SYS_STRINGS) + 1
insert into SYS_STRINGS (id, langID, value) values (@id, 1, 'Default Criterion')
insert into IN_CRITERION (id, code, nameStrID, isDefault) values (1, 'Def_Criterion', @id, 1)
update IN_BANK_GROUPS set criterionid = 1
insert into MM_BANK_GROUP (bankid, bankgroupid) select b.id, g.id from IN_BANKS b, IN_BANK_GROUPS g where b.groupid=g.id
go

/*==============================================================*/
/* Table: DROP BANK GROUP COLUMN AND INDEX                      */
/*==============================================================*/

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_BANKS')
            and   name  = 'banks_groupID'
            and   indid > 0
            and   indid < 255)
   drop index IN_BANKS.banks_groupID
go


alter table IN_BANKS
   drop column groupID
go

/*==============================================================*/
/* Table: OUT_STORED_REPORTS                                    */
/*==============================================================*/

alter table OUT_STORED_REPORTS
   add userID int null
go

alter table OUT_STORED_REPORTS
   add storeDate datetime null
go

update OUT_STORED_REPORTS set userID = 1, storeDate=getdate()
go
   
/*==============================================================*/
/* Table: OUT_REPORTS_SCHEDULE                                  */
/*==============================================================*/

drop table OUT_REPORTS_SCHEDULE
go

create table OUT_REPORTS_SCHEDULE (
   reportID             int                  not null,
   langID               int                  not null,
   info                 image                not null,
   hashCode             int                  not null,
   paramsDescription    varchar(256)         null,
   done                 int                  null,
   status               int                  null,
   onDemand             int                  null,
   scheduleTime         datetime             null,
   userID               int                  null
)
go

insert into SYS_STRINGS (id, langId, value) values (900001, 1, 'Reports Scheduler Manager') 
go

insert into SYS_STRINGS (id, langId, value) values (900002, 1, 'Reports Schedules Add')
go

insert into SYS_STRINGS (id, langId, value) values (900003, 1, 'Stored Reports Manager')
go

insert into SYS_PERMISSIONS (id, nameStrId, idName) values (31, 900001, 'fina2.reports.scheduler.manager')
go

insert into SYS_PERMISSIONS (id, nameStrId, idName) values (32, 900002, 'fina2.reports.scheduler.add')
go

insert into SYS_PERMISSIONS (id, nameStrId, idName) values (33, 900003, 'fina2.reports.stored.manager')
go