/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             SQLServer up to 2005                                                   */
/* Created on:            11/02/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */
/* This script is for increasing SYS_UPLOADEDFILE username field and insert some configuration data*/    
/*===============================================================================================*/
DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key)='fina2.authenticatedModes';
insert into sys_properties(prop_key,value) values('fina2.authenticatedModes','FINA,LDAP');
DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key)='fina2.current.authentication';
insert into sys_properties(prop_key,value) values('fina2.current.authentication','FINA');
ALTER TABLE SYS_UPLOADEDFILE ALTER COLUMN username varchar(200);
ALTER TABLE SYS_UPLOADEDFILE ADD type int;
ALTER TABLE IN_IMPORTED_RETURNS ADD type int;
ALTER TABLE IN_IMPORTED_RETURNS ADD xlsId numeric(18,0);
UPDATE SYS_UPLOADEDFILE SET type=0;
DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key)='fina2.update.gui.filelocation';
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina2.update.gui.filelocation', 'C:\fina-server\GUI_UPDATE');
DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key)='fina2.update.start';
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina2.update.start', 'TRUE');

DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key) IN('fina2.authentication.ldap.urlIp','fina2.authentication.ldap.urlPort','fina2.authentication.ldap.organizationalUnit','fina2.authentication.ldap.domainComponent');

insert into sys_properties(prop_key,value) values('fina2.authentication.ldap.urlIp','10.131.36.250');
insert into sys_properties(prop_key,value) values('fina2.authentication.ldap.urlPort','389');
insert into sys_properties(prop_key,value) values('fina2.authentication.ldap.organizationalUnit','Fina');
insert into sys_properties(prop_key,value) values('fina2.authentication.ldap.domainComponent','fina,ge');

DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key) IN('fina2.email.address','fina2.email.pwd','fina2.pop.protocol','fina2.smtp.protocol');
insert into sys_properties(prop_key,value) values('fina2.email.address','javamail12@gmail.com');
insert into sys_properties(prop_key,value) values('fina2.email.pwd','javamailjavamail');
insert into sys_properties(prop_key,value) values('fina2.pop.protocol','pop.gmail.com');
insert into sys_properties(prop_key,value) values('fina2.smtp.protocol','smtp.gmail.com');

DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key) IN('fina2.mail.reader.port','fina2.mail.sender.port');
insert into sys_properties(prop_key,value) values('fina2.mail.reader.port','995');
insert into sys_properties(prop_key,value) values('fina2.mail.sender.port','465');

declare @id int;
declare @namestrid int;
set @id=(select max(id) from sys_permissions);
set @namestrid=(select max(id) from sys_strings);
insert into sys_permissions(id,namestrid,idname) values(@id+1,@namestrid+1,'fina2.web.internal.user');
insert into sys_strings(id,langid,value) values(@namestrid+1,1,'Internal User');
insert into sys_permissions(id,namestrid,idname) values(@id+2,@namestrid+2,'fina2.web.external.user');
insert into sys_strings(id,langid,value) values(@namestrid+2,1,'External User');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY IN('fina2.update.addin','fina2.update.runBat','fina2.update.resources','fina2.update.finaUpdate');
INSERT INTO sys_properties(PROP_KEY,VALUE) VALUES('fina2.update.addin','0');
INSERT INTO sys_properties(PROP_KEY,VALUE) VALUES('fina2.update.runBat','0');
INSERT INTO sys_properties(PROP_KEY,VALUE) VALUES('fina2.update.resources','0');
INSERT INTO sys_properties(PROP_KEY,VALUE) VALUES('fina2.update.finaUpdate','0');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY IN('fina2.mail.last.read.date');
INSERT INTO SYS_PROPERTIES(prop_key,value) values('fina2.mail.last.read.date',null);

BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
GO
ALTER TABLE dbo.SYS_UPLOADEDFILE ADD
    matrixValid bit NULL,
    reason nvarchar(255) NULL
GO
COMMIT
UPDATE SYS_UPLOADEDFILE set matrixValid=1;
Alter table in_imported_returns add xlsId numeric(18,0);
CREATE NONCLUSTERED INDEX [PK_IN_IMPORTED_RETURNS_FILTER] ON [dbo].[IN_IMPORTED_RETURNS] 
(
    [id] ASC,
    [returnCode] ASC,
    [bankCode] ASC,
    [periodStart] ASC,
    [periodEnd] ASC,
    [importStart] ASC,
    [importEnd] ASC,
    [status] ASC,
    [versionCode] ASC,
    
    [uploadTime] ASC,
    [type] ASC,
    [xlsId] ASC
)
ALTER TABLE IN_BANKS ADD REGIONID decimal(18,0);


CREATE TABLE IN_COUNTRY_DATA(
    [id] [decimal](18, 0) NOT NULL,
    [code] [nvarchar](40) NULL,
    [namestrid] [decimal](18, 0) NULL,
    [parentid] [decimal](18, 0) NULL,
    [sequence] [decimal](18, 0) NULL,
PRIMARY KEY CLUSTERED 
(
    [id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]



/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
