/*==============================================================*/
/* DBMS name:      Microsoft SQL Server 2000                    */
/* Created on:     12/22/2008 3:34:43 PM                        */
/*==============================================================*/


if exists (select 1
            from  sysobjects
           where  id = object_id('RESULT_VIEW')
            and   type = 'V')
   drop view RESULT_VIEW
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_BANKS')
            and   name  = 'banks_typeID'
            and   indid > 0
            and   indid < 255)
   drop index IN_BANKS.banks_typeID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_BANKS')
            and   name  = 'banks_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_BANKS.banks_id
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_BANKS')
            and   name  = 'banks_code'
            and   indid > 0
            and   indid < 255)
   drop index IN_BANKS.banks_code
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_BANKS')
            and   type = 'U')
   drop table IN_BANKS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_BANK_BRANCHES')
            and   type = 'U')
   drop table IN_BANK_BRANCHES
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_BANK_GROUPS')
            and   name  = 'bankGroups_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_BANK_GROUPS.bankGroups_id
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_BANK_GROUPS')
            and   name  = 'bankGroups_code'
            and   indid > 0
            and   indid < 255)
   drop index IN_BANK_GROUPS.bankGroups_code
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_BANK_GROUPS')
            and   type = 'U')
   drop table IN_BANK_GROUPS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_BANK_MANAGEMENT')
            and   type = 'U')
   drop table IN_BANK_MANAGEMENT
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_BANK_TYPES')
            and   name  = 'bankTypes_name'
            and   indid > 0
            and   indid < 255)
   drop index IN_BANK_TYPES.bankTypes_name
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_BANK_TYPES')
            and   name  = 'bankTypes_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_BANK_TYPES.bankTypes_id
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_BANK_TYPES')
            and   name  = 'bankTypes_code'
            and   indid > 0
            and   indid < 255)
   drop index IN_BANK_TYPES.bankTypes_code
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_BANK_TYPES')
            and   type = 'U')
   drop table IN_BANK_TYPES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_BRANCH_MANAGEMENT')
            and   type = 'U')
   drop table IN_BRANCH_MANAGEMENT
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_CRITERION')
            and   type = 'U')
   drop table IN_CRITERION
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_DEFINITION_TABLES')
            and   name  = 'defTables_type'
            and   indid > 0
            and   indid < 255)
   drop index IN_DEFINITION_TABLES.defTables_type
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_DEFINITION_TABLES')
            and   name  = 'defTables_nodeID'
            and   indid > 0
            and   indid < 255)
   drop index IN_DEFINITION_TABLES.defTables_nodeID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_DEFINITION_TABLES')
            and   name  = 'defTables_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_DEFINITION_TABLES.defTables_id
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_DEFINITION_TABLES')
            and   name  = 'defTables_definitionID'
            and   indid > 0
            and   indid < 255)
   drop index IN_DEFINITION_TABLES.defTables_definitionID
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_DEFINITION_TABLES')
            and   type = 'U')
   drop table IN_DEFINITION_TABLES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_IMPORTED_RETURNS')
            and   type = 'U')
   drop table IN_IMPORTED_RETURNS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_LICENCES')
            and   type = 'U')
   drop table IN_LICENCES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_LICENCE_TYPES')
            and   type = 'U')
   drop table IN_LICENCE_TYPES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_MANAGING_BODIES')
            and   type = 'U')
   drop table IN_MANAGING_BODIES
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_MDT_COMPARISON')
            and   name  = 'mdt_comp_nodeID'
            and   indid > 0
            and   indid < 255)
   drop index IN_MDT_COMPARISON.mdt_comp_nodeID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_MDT_COMPARISON')
            and   name  = 'mdt_comp_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_MDT_COMPARISON.mdt_comp_id
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_MDT_COMPARISON')
            and   type = 'U')
   drop table IN_MDT_COMPARISON
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_MDT_DEPENDENT_NODES')
            and   name  = 'depend_nodeID'
            and   indid > 0
            and   indid < 255)
   drop index IN_MDT_DEPENDENT_NODES.depend_nodeID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_MDT_DEPENDENT_NODES')
            and   name  = 'depend_depNodeID'
            and   indid > 0
            and   indid < 255)
   drop index IN_MDT_DEPENDENT_NODES.depend_depNodeID
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_MDT_DEPENDENT_NODES')
            and   type = 'U')
   drop table IN_MDT_DEPENDENT_NODES
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_MDT_NODES')
            and   name  = 'mdt_parentID'
            and   indid > 0
            and   indid < 255)
   drop index IN_MDT_NODES.mdt_parentID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_MDT_NODES')
            and   name  = 'mdt_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_MDT_NODES.mdt_id
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_MDT_NODES')
            and   name  = 'mdt_code'
            and   indid > 0
            and   indid < 255)
   drop index IN_MDT_NODES.mdt_code
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_MDT_NODES')
            and   type = 'U')
   drop table IN_MDT_NODES
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_PERIODS')
            and   name  = 'periods_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_PERIODS.periods_id
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_PERIODS')
            and   type = 'U')
   drop table IN_PERIODS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_PERIOD_TYPES')
            and   type = 'U')
   drop table IN_PERIOD_TYPES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_REGIONS')
            and   type = 'U')
   drop table IN_REGIONS
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURNS')
            and   name  = 'returns_scheduleid'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURNS.returns_scheduleid
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURNS')
            and   name  = 'returns_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURNS.returns_id
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_RETURNS')
            and   type = 'U')
   drop table IN_RETURNS
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_DEFINITIONS')
            and   name  = 'def_code'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_DEFINITIONS.def_code
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_DEFINITIONS')
            and   name  = 'def_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_DEFINITIONS.def_id
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_RETURN_DEFINITIONS')
            and   type = 'U')
   drop table IN_RETURN_DEFINITIONS
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_ITEMS')
            and   name  = 'return_items_tableID'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_ITEMS.return_items_tableID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_ITEMS')
            and   name  = 'return_items_rowNumber'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_ITEMS.return_items_rowNumber
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_ITEMS')
            and   name  = 'return_items_returnID'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_ITEMS.return_items_returnID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_ITEMS')
            and   name  = 'return_items_nodeID'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_ITEMS.return_items_nodeID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_ITEMS')
            and   name  = 'return_items_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_ITEMS.return_items_id
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_ITEMS')
            and   name  = 'return_complex2'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_ITEMS.return_complex2
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_RETURN_ITEMS')
            and   type = 'U')
   drop table IN_RETURN_ITEMS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_RETURN_STATUSES')
            and   type = 'U')
   drop table IN_RETURN_STATUSES
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_TYPES')
            and   name  = 'retTypes_name'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_TYPES.retTypes_name
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_TYPES')
            and   name  = 'retTypes_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_TYPES.retTypes_id
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_RETURN_TYPES')
            and   name  = 'retTypes_code'
            and   indid > 0
            and   indid < 255)
   drop index IN_RETURN_TYPES.retTypes_code
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_RETURN_TYPES')
            and   type = 'U')
   drop table IN_RETURN_TYPES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_RETURN_VERSIONS')
            and   type = 'U')
   drop table IN_RETURN_VERSIONS
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_SCHEDULES')
            and   name  = 'schedules_periodID'
            and   indid > 0
            and   indid < 255)
   drop index IN_SCHEDULES.schedules_periodID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_SCHEDULES')
            and   name  = 'schedules_definitionID'
            and   indid > 0
            and   indid < 255)
   drop index IN_SCHEDULES.schedules_definitionID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_SCHEDULES')
            and   name  = 'schedules_complex1'
            and   indid > 0
            and   indid < 255)
   drop index IN_SCHEDULES.schedules_complex1
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_SCHEDULES')
            and   name  = 'schedules_bankID'
            and   indid > 0
            and   indid < 255)
   drop index IN_SCHEDULES.schedules_bankID
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('IN_SCHEDULES')
            and   name  = 'schedules_id'
            and   indid > 0
            and   indid < 255)
   drop index IN_SCHEDULES.schedules_id
go

if exists (select 1
            from  sysobjects
           where  id = object_id('IN_SCHEDULES')
            and   type = 'U')
   drop table IN_SCHEDULES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('MM_BANK_GROUP')
            and   type = 'U')
   drop table MM_BANK_GROUP
go

if exists (select 1
            from  sysobjects
           where  id = object_id('OUT_REPORTS')
            and   type = 'U')
   drop table OUT_REPORTS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('OUT_REPORTS_LANG')
            and   type = 'U')
   drop table OUT_REPORTS_LANG
go

if exists (select 1
            from  sysobjects
           where  id = object_id('OUT_REPORTS_SCHEDULE')
            and   type = 'U')
   drop table OUT_REPORTS_SCHEDULE
go

if exists (select 1
            from  sysobjects
           where  id = object_id('OUT_REPOSITORY')
            and   type = 'U')
   drop table OUT_REPOSITORY
go

if exists (select 1
            from  sysobjects
           where  id = object_id('OUT_REPOSITORY_PARAMS')
            and   type = 'U')
   drop table OUT_REPOSITORY_PARAMS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('OUT_STORED_REPORTS')
            and   type = 'U')
   drop table OUT_STORED_REPORTS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_LANGUAGES')
            and   type = 'U')
   drop table SYS_LANGUAGES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_MENUS')
            and   type = 'U')
   drop table SYS_MENUS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_PERMISSIONS')
            and   type = 'U')
   drop table SYS_PERMISSIONS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_PROPERTIES')
            and   type = 'U')
   drop table SYS_PROPERTIES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_ROLES')
            and   type = 'U')
   drop table SYS_ROLES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_ROLE_MENUS')
            and   type = 'U')
   drop table SYS_ROLE_MENUS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_ROLE_PERMISSIONS')
            and   type = 'U')
   drop table SYS_ROLE_PERMISSIONS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_ROLE_REPORTS')
            and   type = 'U')
   drop table SYS_ROLE_REPORTS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_ROLE_RETURNS')
            and   type = 'U')
   drop table SYS_ROLE_RETURNS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_ROLE_RETURN_VERSIONS')
            and   type = 'U')
   drop table SYS_ROLE_RETURN_VERSIONS
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('SYS_STRINGS')
            and   name  = 'strings_id'
            and   indid > 0
            and   indid < 255)
   drop index SYS_STRINGS.strings_id
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('SYS_STRINGS')
            and   name  = 'strings_complex'
            and   indid > 0
            and   indid < 255)
   drop index SYS_STRINGS.strings_complex
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_STRINGS')
            and   type = 'U')
   drop table SYS_STRINGS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_USERS')
            and   type = 'U')
   drop table SYS_USERS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_USERS_ROLES')
            and   type = 'U')
   drop table SYS_USERS_ROLES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_USER_BANKS')
            and   type = 'U')
   drop table SYS_USER_BANKS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_USER_MENUS')
            and   type = 'U')
   drop table SYS_USER_MENUS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_USER_PASSWORDS')
            and   type = 'U')
   drop table SYS_USER_PASSWORDS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_USER_PERMISSIONS')
            and   type = 'U')
   drop table SYS_USER_PERMISSIONS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_USER_REPORTS')
            and   type = 'U')
   drop table SYS_USER_REPORTS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_USER_RETURNS')
            and   type = 'U')
   drop table SYS_USER_RETURNS
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SYS_USER_RETURN_VERSIONS')
            and   type = 'U')
   drop table SYS_USER_RETURN_VERSIONS
go

execute sp_revokedbaccess dbo
go

/*==============================================================*/
/* User: dbo                                                    */
/*==============================================================*/
execute sp_grantdbaccess dbo
go

/*==============================================================*/
/* Table: IN_BANKS                                              */
/*==============================================================*/
create table IN_BANKS (
   id                   int                  not null,
   code                 varchar(12)          null,
   typeID               int                  null,
   shortNameStrID       int                  null,
   nameStrID            int                  null,
   addressStrID         int                  null,
   phone                varchar(40)          null,
   fax                  varchar(40)          null,
   email                varchar(40)          null,
   telex                varchar(40)          null,
   swiftCode            varchar(11)          null,
   constraint PK_IN_BANKS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Index: banks_code                                            */
/*==============================================================*/
create unique  index banks_code on IN_BANKS (
code ASC
)
go

/*==============================================================*/
/* Index: banks_id                                              */
/*==============================================================*/
create unique  index banks_id on IN_BANKS (
id ASC
)
go

/*==============================================================*/
/* Index: banks_typeID                                          */
/*==============================================================*/
create   index banks_typeID on IN_BANKS (
typeID ASC
)
go

/*==============================================================*/
/* Table: IN_BANK_BRANCHES                                      */
/*==============================================================*/
create table IN_BANK_BRANCHES (
   id                   int                  not null,
   bankRegionStrId      int                  null,
   nameStrId            int                  null,
   shortNameStrId       int                  null,
   addressStrID         int                  null,
   commentsStrId        varchar(40)          null,
   creationDate         datetime             null,
   dateOfChange         datetime             null,
   bankId               int                  null,
   constraint PK_IN_BANK_BRANCHES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: IN_BANK_GROUPS                                        */
/*==============================================================*/
create table IN_BANK_GROUPS (
   id                   int                  not null,
   code                 varchar(12)          null,
   nameStrID            int                  null,
   criterionid          int                  null,
   constraint PK_IN_BANK_GROUPS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Index: bankGroups_code                                       */
/*==============================================================*/
create unique  index bankGroups_code on IN_BANK_GROUPS (
code ASC
)
go

/*==============================================================*/
/* Index: bankGroups_id                                         */
/*==============================================================*/
create unique  index bankGroups_id on IN_BANK_GROUPS (
id ASC
)
go

/*==============================================================*/
/* Table: IN_BANK_MANAGEMENT                                    */
/*==============================================================*/
create table IN_BANK_MANAGEMENT (
   Id                   int                  not null,
   nameStrId            int                  null,
   lastNameStrId        int                  null,
   managingBodyID       int                  null,
   postStrID            int                  null,
   phone                varchar(25)          null,
   dateOfAppointment    datetime             null,
   cancelDate           datetime             null,
   registrationStrId1   int                  null,
   registrationStrId2   int                  null,
   registrationStrId3   int                  null,
   commentsStrId1       int                  null,
   commentsStrId2       int                  null,
   bankId               int                  null,
   constraint PK_IN_BANK_MANAGEMENT primary key nonclustered (Id)
)
go

/*==============================================================*/
/* Table: IN_BANK_TYPES                                         */
/*==============================================================*/
create table IN_BANK_TYPES (
   id                   int                  not null,
   code                 varchar(12)          null,
   nameStrID            int                  null,
   constraint PK_IN_BANK_TYPES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Index: bankTypes_code                                        */
/*==============================================================*/
create unique  index bankTypes_code on IN_BANK_TYPES (
code ASC
)
go

/*==============================================================*/
/* Index: bankTypes_id                                          */
/*==============================================================*/
create unique  index bankTypes_id on IN_BANK_TYPES (
id ASC
)
go

/*==============================================================*/
/* Index: bankTypes_name                                        */
/*==============================================================*/
create unique  index bankTypes_name on IN_BANK_TYPES (
nameStrID ASC
)
go

/*==============================================================*/
/* Table: IN_BRANCH_MANAGEMENT                                  */
/*==============================================================*/
create table IN_BRANCH_MANAGEMENT (
   Id                   int                  not null,
   nameStrId            int                  null,
   lastNameStrId        int                  null,
   managingBodyID       int                  null,
   postStrID            int                  null,
   phone                varchar(25)          null,
   dateOfAppointment    datetime             null,
   cancelDate           datetime             null,
   registrationStrId1   int                  null,
   registrationStrId2   int                  null,
   registrationStrId3   int                  null,
   commentsStrId1       int                  null,
   commentsStrId2       int                  null,
   branchId             int                  null,
   constraint PK_IN_BRANCH_MANAGEMENT primary key nonclustered (Id)
)
go

/*==============================================================*/
/* Table: IN_CRITERION                                          */
/*==============================================================*/
create table IN_CRITERION (
   id                   int                  not null,
   code                 varchar(30)          null,
   nameStrID            int                  null,
   isDefault            int                  not null,
   constraint PK_IN_CRITERION primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: IN_DEFINITION_TABLES                                  */
/*==============================================================*/
create table IN_DEFINITION_TABLES (
   id                   int                  null,
   code                 varchar(12)          null,
   definitionID         int                  null,
   nodeID               int                  null,
   nodeVisible          int                  null,
   visibleLevel         int                  null,
   type                 int                  null,
   evalType             int                  null
)
go

/*==============================================================*/
/* Index: defTables_definitionID                                */
/*==============================================================*/
create   index defTables_definitionID on IN_DEFINITION_TABLES (
definitionID ASC
)
go

/*==============================================================*/
/* Index: defTables_id                                          */
/*==============================================================*/
create   index defTables_id on IN_DEFINITION_TABLES (
id ASC
)
go

/*==============================================================*/
/* Index: defTables_nodeID                                      */
/*==============================================================*/
create   index defTables_nodeID on IN_DEFINITION_TABLES (
nodeID ASC
)
go

/*==============================================================*/
/* Index: defTables_type                                        */
/*==============================================================*/
create   index defTables_type on IN_DEFINITION_TABLES (
type ASC
)
go

/*==============================================================*/
/* Table: IN_IMPORTED_RETURNS                                   */
/*==============================================================*/
create table IN_IMPORTED_RETURNS (
   id                   int           not null,
   returnCode           VARCHAR(12)          null,
   bankCode             VARCHAR(12)          null,
   versionCode          VARCHAR(12)          null,
   periodStart          DATETIME                 null,
   periodEnd            DATETIME                 null,
   userId               int           null,
   langId               int           null,
   uploadTime           DATETIME                 null,
   importStart          DATETIME                 null,
   importEnd            DATETIME                 null,
   status               int            null,
   content              IMAGE                 null,
   message              VARCHAR(4000)        null,
   constraint PK_IN_IMPORTED_RETURNS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: IN_LICENCES                                           */
/*==============================================================*/
create table IN_LICENCES (
   id                   int                  not null,
   typeID               int                  null,
   code                 varchar(12)          null,
   creationDate         datetime             null,
   dateOfChange         datetime             null,
   reasonStrID          int                  null,
   operational          int                  null,
   bankID               int                  null,
   constraint PK_IN_LICENCES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: IN_LICENCE_TYPES                                      */
/*==============================================================*/
create table IN_LICENCE_TYPES (
   id                   int                  not null,
   nameStrID            int                  null,
   constraint PK_IN_LICENCE_TYPES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: IN_MANAGING_BODIES                                    */
/*==============================================================*/
create table IN_MANAGING_BODIES (
   Id                   int                  not null,
   postStrId            int                  null,
   constraint PK_IN_MANAGING_BODIES primary key nonclustered (Id)
)
go

/*==============================================================*/
/* Table: IN_MDT_COMPARISON                                     */
/*==============================================================*/
create table IN_MDT_COMPARISON (
   id                   int                  null,
   nodeID               int                  null,
   condition            int                  null,
   equation             varchar(600)         null
)
go

/*==============================================================*/
/* Index: mdt_comp_id                                           */
/*==============================================================*/
create   index mdt_comp_id on IN_MDT_COMPARISON (
id ASC
)
go

/*==============================================================*/
/* Index: mdt_comp_nodeID                                       */
/*==============================================================*/
create   index mdt_comp_nodeID on IN_MDT_COMPARISON (
nodeID ASC
)
go

/*==============================================================*/
/* Table: IN_MDT_DEPENDENT_NODES                                */
/*==============================================================*/
create table IN_MDT_DEPENDENT_NODES (
   nodeID               int                  null,
   dependentNodeID      int                  null
)
go

/*==============================================================*/
/* Index: depend_depNodeID                                      */
/*==============================================================*/
create   index depend_depNodeID on IN_MDT_DEPENDENT_NODES (
dependentNodeID ASC
)
go

/*==============================================================*/
/* Index: depend_nodeID                                         */
/*==============================================================*/
create   index depend_nodeID on IN_MDT_DEPENDENT_NODES (
nodeID ASC
)
go

/*==============================================================*/
/* Table: IN_MDT_NODES                                          */
/*==============================================================*/
create table IN_MDT_NODES (
   id                   int                  not null,
   code                 varchar(30)          null,
   nameStrID            int                  null,
   parentID             int                  null,
   type                 int                  null,
   dataType             int                  null,
   equation             varchar(3700)        null,
   sequence             int                  null,
   evalMethod           int                  null,
   disabled             int                  null,
   required             int                  null,
   constraint PK_IN_MDT_NODES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Index: mdt_code                                              */
/*==============================================================*/
create unique  index mdt_code on IN_MDT_NODES (
code ASC
)
go

/*==============================================================*/
/* Index: mdt_id                                                */
/*==============================================================*/
create unique  index mdt_id on IN_MDT_NODES (
id ASC
)
go

/*==============================================================*/
/* Index: mdt_parentID                                          */
/*==============================================================*/
create   index mdt_parentID on IN_MDT_NODES (
parentID ASC
)
go

/*==============================================================*/
/* Table: IN_PERIODS                                            */
/*==============================================================*/
create table IN_PERIODS (
   id                   int                  not null,
   periodNumber         int                  null,
   fromDate             datetime             null,
   toDate               datetime             null,
   periodTypeID         int                  null,
   constraint PK_IN_PERIODS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Index: periods_id                                            */
/*==============================================================*/
create unique  index periods_id on IN_PERIODS (
id ASC
)
go

/*==============================================================*/
/* Table: IN_PERIOD_TYPES                                       */
/*==============================================================*/
create table IN_PERIOD_TYPES (
   id                   int                  not null,
   code                 varchar(12)          null,
   nameStrID            int                  null,
   constraint PK_IN_PERIOD_TYPES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: IN_REGIONS                                            */
/*==============================================================*/
create table IN_REGIONS (
   Id                   int                  not null,
   cityStrId            int                  null,
   regionStrId          int                  null,
   constraint PK_IN_REGIONS primary key nonclustered (Id)
)
go

/*==============================================================*/
/* Table: IN_RETURNS                                            */
/*==============================================================*/
create table IN_RETURNS (
   id                   int                  not null,
   scheduleID           int                  null,
   versionID            int                  null,
   constraint PK_IN_RETURNS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Index: returns_id                                            */
/*==============================================================*/
create unique  index returns_id on IN_RETURNS (
id ASC
)
go

/*==============================================================*/
/* Index: returns_scheduleid                                    */
/*==============================================================*/
create unique  index returns_scheduleid on IN_RETURNS (
scheduleID ASC
)
go

/*==============================================================*/
/* Table: IN_RETURN_DEFINITIONS                                 */
/*==============================================================*/
create table IN_RETURN_DEFINITIONS (
   id                   int                  not null,
   code                 varchar(12)          null,
   nameStrID            int                  null,
   typeID               int                  null,
   FORMAT               image                null,
   constraint PK_IN_RETURN_DEFINITIONS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Index: def_id                                                */
/*==============================================================*/
create unique  index def_id on IN_RETURN_DEFINITIONS (
id ASC
)
go

/*==============================================================*/
/* Index: def_code                                              */
/*==============================================================*/
create   index def_code on IN_RETURN_DEFINITIONS (
code ASC
)
go

/*==============================================================*/
/* Table: IN_RETURN_ITEMS                                       */
/*==============================================================*/
create table IN_RETURN_ITEMS (
   id                   int                  null,
   returnID             int                  null,
   tableID              int                  null,
   nodeID               int                  null,
   rowNumber            int                  null,
   value                varchar(255)         null,
   nvalue               float                null,
   versionID            int                  null
)
go

/*==============================================================*/
/* Index: return_complex2                                       */
/*==============================================================*/
create   index return_complex2 on IN_RETURN_ITEMS (
returnID ASC,
nodeID ASC,
tableID ASC
)
go

/*==============================================================*/
/* Index: return_items_id                                       */
/*==============================================================*/
create   index return_items_id on IN_RETURN_ITEMS (
id ASC
)
go

/*==============================================================*/
/* Index: return_items_nodeID                                   */
/*==============================================================*/
create   index return_items_nodeID on IN_RETURN_ITEMS (
nodeID ASC
)
go

/*==============================================================*/
/* Index: return_items_returnID                                 */
/*==============================================================*/
create   index return_items_returnID on IN_RETURN_ITEMS (
returnID ASC
)
go

/*==============================================================*/
/* Index: return_items_rowNumber                                */
/*==============================================================*/
create   index return_items_rowNumber on IN_RETURN_ITEMS (
rowNumber ASC
)
go

/*==============================================================*/
/* Index: return_items_tableID                                  */
/*==============================================================*/
create   index return_items_tableID on IN_RETURN_ITEMS (
tableID ASC
)
go

/*==============================================================*/
/* Table: IN_RETURN_STATUSES                                    */
/*==============================================================*/
create table IN_RETURN_STATUSES (
   returnID             int                  null,
   status               int                  null,
   statusDate           datetime             null,
   userID               int                  null,
   note                 varchar(4096)        null,
   versionID            int                  null,
   id                   int                  not null,
   constraint PK_IN_RETURN_STATUSES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: IN_RETURN_TYPES                                       */
/*==============================================================*/
create table IN_RETURN_TYPES (
   id                   int                  not null,
   code                 varchar(12)          null,
   nameStrID            int                  null,
   constraint PK_IN_RETURN_TYPES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Index: retTypes_code                                         */
/*==============================================================*/
create unique  index retTypes_code on IN_RETURN_TYPES (
code ASC
)
go

/*==============================================================*/
/* Index: retTypes_id                                           */
/*==============================================================*/
create unique  index retTypes_id on IN_RETURN_TYPES (
id ASC
)
go

/*==============================================================*/
/* Index: retTypes_name                                         */
/*==============================================================*/
create unique  index retTypes_name on IN_RETURN_TYPES (
nameStrID ASC
)
go

/*==============================================================*/
/* Table: IN_RETURN_VERSIONS                                    */
/*==============================================================*/
create table IN_RETURN_VERSIONS (
   id                   int                  not null,
   code                 varchar(12)          null,
   sequence             int                  null,
   descStrID            int                  null,
   constraint PK_IN_RETURN_VERSIONS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: IN_SCHEDULES                                          */
/*==============================================================*/
create table IN_SCHEDULES (
   id                   int                  not null,
   bankID               int                  null,
   definitionID         int                  null,
   periodID             int                  null,
   delay                int                  null,
   constraint PK_IN_SCHEDULES2 primary key nonclustered (id)
)
go

/*==============================================================*/
/* Index: schedules_id                                          */
/*==============================================================*/
create unique  index schedules_id on IN_SCHEDULES (
id ASC
)
go

/*==============================================================*/
/* Index: schedules_bankID                                      */
/*==============================================================*/
create   index schedules_bankID on IN_SCHEDULES (
bankID ASC
)
go

/*==============================================================*/
/* Index: schedules_complex1                                    */
/*==============================================================*/
create   index schedules_complex1 on IN_SCHEDULES (
bankID ASC,
periodID ASC
)
go

/*==============================================================*/
/* Index: schedules_definitionID                                */
/*==============================================================*/
create   index schedules_definitionID on IN_SCHEDULES (
definitionID ASC
)
go

/*==============================================================*/
/* Index: schedules_periodID                                    */
/*==============================================================*/
create   index schedules_periodID on IN_SCHEDULES (
periodID ASC
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
/* Table: OUT_REPORTS                                           */
/*==============================================================*/
create table OUT_REPORTS (
   id                   int                  not null,
   parentID             int                  null,
   nameStrID            int                  null,
   type                 int                  null,
   template             image                null,
   info                 image                null,
   sequence             int                  null,
   constraint PK_OUT_REPORTS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: OUT_REPORTS_LANG                                      */
/*==============================================================*/
create table OUT_REPORTS_LANG (
   reportID             int                  null,
   langID               int                  null,
   template             image                null
)
go

/*==============================================================*/
/* Table: OUT_REPORTS_SCHEDULE                                  */
/*==============================================================*/
create table OUT_REPORTS_SCHEDULE (
   reportID             int                  not null,
   langID               int                  not null,
   info                 image                not null,
   hashCode             int                  not null,
   status               int                  null,
   onDemand             int                  null,
   scheduleTime         datetime             null,
   userID               int                  not null
)
go

/*==============================================================*/
/* Table: OUT_REPOSITORY                                        */
/*==============================================================*/
create table OUT_REPOSITORY (
   id                   int                  not null,
   parentID             int                  null,
   name                 varchar(50)          not null,
   script               varchar(2048)        null,
   type                 int                  not null,
   constraint PK_OUT_REPOSITORY primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: OUT_REPOSITORY_PARAMS                                 */
/*==============================================================*/
create table OUT_REPOSITORY_PARAMS (
   id                   int                  not null,
   formulaID            int                  not null,
   type                 int                  not null,
   name                 varchar(50)          null
)
go

/*==============================================================*/
/* Table: OUT_STORED_REPORTS                                    */
/*==============================================================*/
create table OUT_STORED_REPORTS (
   reportID             int                  not null,
   langID               int                  not null,
   info                 image                not null,
   reportResult         image                not null,
   hashCode             int                  not null,
   userID               int                  not null,
   storeDate            datetime             not null
)
go

/*==============================================================*/
/* Table: SYS_LANGUAGES                                         */
/*==============================================================*/
create table SYS_LANGUAGES (
   id                   int                  not null,
   code                 varchar(12)          null,
   name                 varchar(24)          null,
   dateFormat           varchar(24)          null,
   numberFormat         varchar(24)          null,
   fontFace             varchar(24)          null,
   fontSize             int                  null,
   htmlCharset          varchar(20)          null,
   xmlEncoding          varchar(20)          null,
   constraint PK_SYS_LANGUAGES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: SYS_MENUS                                             */
/*==============================================================*/
create table SYS_MENUS (
   id                   int                  not null,
   parentID             int                  null,
   nameStrID            int                  null,
   type                 int                  null,
   actionKey            varchar(40)          null,
   application          varchar(80)          null,
   sequence             int                  null,
   constraint PK_SYS_MENUS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: SYS_PERMISSIONS                                       */
/*==============================================================*/
create table SYS_PERMISSIONS (
   id                   int                  not null,
   nameStrID            int                  null,
   idName               varchar(80)          null,
   constraint PK_SYS_PERMISSIONS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: SYS_PROPERTIES                                        */
/*==============================================================*/
create table SYS_PROPERTIES (
   prop_key             varchar(64)          not null,
   value                varchar(256)         null,
   constraint PK_SYS_PROPERTIES primary key nonclustered (prop_key)
)
go

/*==============================================================*/
/* Table: SYS_ROLES                                             */
/*==============================================================*/
create table SYS_ROLES (
   id                   int                  not null,
   nameStrID            int                  null,
   code                 varchar(12)          null,
   constraint PK_SYS_ROLES primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: SYS_ROLE_MENUS                                        */
/*==============================================================*/
create table SYS_ROLE_MENUS (
   roleID               int                  null,
   menuID               int                  null
)
go

/*==============================================================*/
/* Table: SYS_ROLE_PERMISSIONS                                  */
/*==============================================================*/
create table SYS_ROLE_PERMISSIONS (
   roleID               int                  null,
   permissionID         int                  null
)
go

/*==============================================================*/
/* Table: SYS_ROLE_REPORTS                                      */
/*==============================================================*/
create table SYS_ROLE_REPORTS (
   role_id              int                  null,
   report_id            int                  null
)
go

/*==============================================================*/
/* Table: SYS_ROLE_RETURNS                                      */
/*==============================================================*/
create table SYS_ROLE_RETURNS (
   role_id              int                  null,
   definition_id        int                  null
)
go

/*==============================================================*/
/* Table: SYS_ROLE_RETURN_VERSIONS                              */
/*==============================================================*/
create table SYS_ROLE_RETURN_VERSIONS (
   role_id              int                  null,
   version_id           int                  null,
   can_amend            int                  null default 0
)
go

/*==============================================================*/
/* Table: SYS_STRINGS                                           */
/*==============================================================*/
create table SYS_STRINGS (
   id                   int                  not null,
   langID               int                  null,
   value                varchar(255)         null
)
go

/*==============================================================*/
/* Index: strings_complex                                       */
/*==============================================================*/
create   index strings_complex on SYS_STRINGS (
id ASC,
langID ASC
)
go

/*==============================================================*/
/* Index: strings_id                                            */
/*==============================================================*/
create   index strings_id on SYS_STRINGS (
id ASC
)
go

/*==============================================================*/
/* Table: SYS_USERS                                             */
/*==============================================================*/
create table SYS_USERS (
   id                   int                  not null,
   login                varchar(15)          null,
   password             varchar(40)          null,
   changePassword       int                  null,
   nameStrID            int                  null,
   titleStrID           int                  null,
   phone                varchar(40)          null,
   email                varchar(40)          null,
   blocked              int                  null,
   lastLoginDate        datetime             null,
   lastPasswordChangeDate datetime             null,
   constraint PK_SYS_USERS primary key nonclustered (id)
)
go

/*==============================================================*/
/* Table: SYS_USERS_ROLES                                       */
/*==============================================================*/
create table SYS_USERS_ROLES (
   userID               int                  null,
   roleID               int                  null
)
go

/*==============================================================*/
/* Table: SYS_USER_BANKS                                        */
/*==============================================================*/
create table SYS_USER_BANKS (
   userID               int                  null,
   bankID               int                  null,
   canAmend             int                  null
)
go

/*==============================================================*/
/* Table: SYS_USER_MENUS                                        */
/*==============================================================*/
create table SYS_USER_MENUS (
   userID               int                  null,
   menuID               int                  null
)
go

/*==============================================================*/
/* Table: SYS_USER_PASSWORDS                                    */
/*==============================================================*/
create table SYS_USER_PASSWORDS (
   userId               int                  not null,
   password             varchar(40)          not null,
   storedate            datetime             not null
)
go

/*==============================================================*/
/* Table: SYS_USER_PERMISSIONS                                  */
/*==============================================================*/
create table SYS_USER_PERMISSIONS (
   userID               int                  null,
   permissionID         int                  null
)
go

/*==============================================================*/
/* Table: SYS_USER_REPORTS                                      */
/*==============================================================*/
create table SYS_USER_REPORTS (
   userID               int                  null,
   reportID             int                  null,
   canAmend             int                  null
)
go

/*==============================================================*/
/* Table: SYS_USER_RETURNS                                      */
/*==============================================================*/
create table SYS_USER_RETURNS (
   user_id              int                  null,
   definition_id        int                  null
)
go

/*==============================================================*/
/* Table: SYS_USER_RETURN_VERSIONS                              */
/*==============================================================*/
create table SYS_USER_RETURN_VERSIONS (
   user_id              int                  null,
   version_id           int                  null,
   can_amend            int                  null default 0
)
go

/*==============================================================*/
/* View: RESULT_VIEW                                            */
/*==============================================================*/
create view RESULT_VIEW as
SELECT RI.VALUE, RI.NVALUE, RI.NODEID, S.BANKID, P.PERIODTYPEID, P.FROMDATE, P.TODATE, 
P.ID AS PERIODID, RV.CODE AS VERSIONCODE, RV2.CODE AS LATESTVERSIONCODE
     FROM IN_SCHEDULES S,
          IN_PERIODS P,
          IN_RETURNS R,
          IN_RETURN_ITEMS RI,
          IN_RETURN_VERSIONS RV,
          IN_RETURN_VERSIONS RV2
     WHERE
       S.PERIODID = P.ID AND R.SCHEDULEID=S.ID AND RI.RETURNID=R.ID AND RV.ID = RI.VERSIONID AND RV2.ID = R.VERSIONID
go

