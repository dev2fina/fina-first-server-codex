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
   content              image                 null,
   message              VARCHAR(4000)        null,
   constraint PK_IN_IMPORTED_RETURNS primary key nonclustered (id)
)
go
