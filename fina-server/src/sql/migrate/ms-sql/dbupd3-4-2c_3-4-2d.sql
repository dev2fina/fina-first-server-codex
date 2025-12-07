/* Without executing this script user wont be able to have returns statuses permission*/
update SYS_PERMISSIONS set IDNAME='fina2.returns.statuses' where IDNAME='fina2.return.statuses'
/*Script is for mssqlserver*/
/*drops web portal permissions*/
delete from sys_strings where value='Web Portal Admin' or value='Web Portal User';
delete from sys_permissions where idname='fina2.web.admin' or idname='fina2.web.user';

/*===================================================*/
/*adding some new rows to SYS_STRINGS Table*/
/*===================================================*/
/*insert into sys_strings values(62,5,'Web Portal Admin')*/
/*insert into sys_strings values(62,1,'Web Portal Admin')*/
/*insert into sys_strings values(63,5,'Web Portal User')*/
/*insert into sys_strings values(63,1,'Web Portal User')*/

/*creates web portal permissions*/
declare @s1 integer;
declare @s2 integer;

set @s1=(select max(id) from sys_strings);
set @s2=(select max(id)+1 from sys_strings);

insert into sys_strings values(@s1,1,'Web Portal Admin');
insert into sys_strings values(@s1,2,'Web Portal Admin');
insert into sys_strings values(@s1,3,'Web Portal Admin');
insert into sys_strings values(@s1,4,'Web Portal Admin');
insert into sys_strings values(@s1,5,'Web Portal Admin');

insert into sys_strings values(@s2,1,'Web Portal User');
insert into sys_strings values(@s2,2,'Web Portal User');
insert into sys_strings values(@s2,3,'Web Portal User');
insert into sys_strings values(@s2,4,'Web Portal User');
insert into sys_strings values(@s2,5,'Web Portal User');

delete from sys_properties where prop_key='fina2.xml.folder.location';
insert into sys_properties values('fina2.xml.folder.location','c:\\fina-server\\xmls\\');

declare @s3 integer;
declare @s4 integer;
declare @s5 integer;

set @s3=(select max(id)+1 from sys_permissions);
set @s4=(select max(id)-1 from sys_strings);
set @s5=(select max(id) from sys_strings);


insert into sys_permissions values(@s3, @s4,'fina2.web.admin');
insert into sys_permissions values(@s3+1, @s5,'fina2.web.user');

/*===================================================*/
/* SYS_UPLOADED_RETURNS Table*/
/*===================================================*/
create table SYS_UPLOADED_RETURNS(uploadedFileId int not null,importedReturnsId int not null)

drop table sys_uploadedfile

/*===================================================*/
/*SYS_UPLOADEDFILE Table*/
/*===================================================*/
create table SYS_UPLOADEDFILE(id int primary key not null,bankCode varchar(20),username varchar(20),fileName varchar(50),status varchar(20),uploadedTime datetime,uploadedFile image)


/*===================================================*/
/*adding some new permissions to SYS_PERMISSIONS Table*/
/*===================================================*/

/*===================================================*/
/*deleting some unused data from SYS_PROPERTIES Table*/
/*===================================================*/
delete from sys_properties where prop_key='fina2.converter.address';/*'http://127.0.0.1:8080/TestService/ConverterServiceImpl?wsdl')*/

/****** for fina2bcc database     Script Date: 07/10/2009 11:51:35, for database : fina2bcc20090619******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[IN_RETURN_ITEMS]') AND name = N'return_complex2')
DROP INDEX [return_complex2] ON [dbo].[IN_RETURN_ITEMS] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[IN_RETURN_ITEMS]') AND name = N'return_items_id')
DROP INDEX [return_items_id] ON [dbo].[IN_RETURN_ITEMS] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[IN_RETURN_ITEMS]') AND name = N'return_items_nodeID')
DROP INDEX [return_items_nodeID] ON [dbo].[IN_RETURN_ITEMS] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[IN_RETURN_ITEMS]') AND name = N'return_items_returnID')
DROP INDEX [return_items_returnID] ON [dbo].[IN_RETURN_ITEMS] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[IN_RETURN_ITEMS]') AND name = N'return_items_rowNumber')
DROP INDEX [return_items_rowNumber] ON [dbo].[IN_RETURN_ITEMS] WITH ( ONLINE = OFF )

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[IN_RETURN_ITEMS]') AND name = N'return_items_tableID')
DROP INDEX [return_items_tableID] ON [dbo].[IN_RETURN_ITEMS] WITH ( ONLINE = OFF )

CREATE NONCLUSTERED INDEX [complex_returns_index] ON [dbo].[IN_RETURN_ITEMS] 
(
	[RETURNID] ASC,
	[TABLEID] ASC,
	[NODEID] ASC,
	[VERSIONID] ASC,
	[ROWNUMBER] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY];
go
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
);

insert into sys_properties values('fina2.mfb.xls.name.pattern','MFB[0-9]{5}m(01|02|03|04|05|06|07|08|09|10|11|12)[1-9][0-9][0-9][0-9].xls');
insert into sys_properties values('fina2.sheet.protection.password','-13753');
