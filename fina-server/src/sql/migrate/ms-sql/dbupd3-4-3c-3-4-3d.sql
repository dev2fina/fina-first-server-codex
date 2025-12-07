
/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             SQLServer up to 2005                                                   */
/* Created on:            11/02/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */
/* This script is for increasing setting identity id attribute which is used to increment this field automatically*/    
/*===============================================================================================*/

insert into SYS_PROPERTIES(prop_key,value) values('fina2.regionstructuretree.maxlevel','3');
insert into SYS_PROPERTIES(prop_key) values('fina2.regionstructuretree.levelname');
update SYS_PROPERTIES set VALUE='' where prop_key='fina2.regionstructuretree.levelname';

/* To prevent any potential data loss issues, you should review this script in detail before running it outside the context of the database designer.*/
ALTER TABLE IN_IMPORTED_RETURNS ADD type int;
BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT

DECLARE @table nvarchar(50)

set @table = 'SYS_UPLOADEDFILE'

DECLARE @sql nvarchar(255)
WHILE EXISTS(select * from INFORMATION_SCHEMA.TABLE_CONSTRAINTS where table_name = @table)
BEGIN
    select    @sql = 'ALTER TABLE ' + @table + ' DROP CONSTRAINT ' + CONSTRAINT_NAME 
    from    INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    where     
            table_name = @table
    exec    sp_executesql @sql
END

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
BEGIN TRANSACTION
GO
CREATE TABLE dbo.Tmp_SYS_UPLOADEDFILE
    (
    bankCode varchar(20) NULL,
    username varchar(20) NULL,
    fileName varchar(50) NULL,
    status varchar(20) NULL,
    uploadedTime datetime NULL,
    uploadedFile image NULL,
    nameValid bit NULL,
    versionvalid bit NULL,
    protectioninfo nvarchar(255) NULL,
    passwordInfo nvarchar(255) NULL,
    hasUserBank bit NULL,
    id numeric(18,0) NOT NULL IDENTITY (1, 1),
    type int NULL
    )  ON [PRIMARY]
     TEXTIMAGE_ON [PRIMARY]
GO
SET IDENTITY_INSERT dbo.Tmp_SYS_UPLOADEDFILE ON
GO
IF EXISTS(SELECT * FROM dbo.SYS_UPLOADEDFILE)
     EXEC('INSERT INTO dbo.Tmp_SYS_UPLOADEDFILE (bankCode, username, fileName, status, uploadedTime, uploadedFile, nameValid, versionvalid, protectioninfo, passwordInfo, hasUserBank, id, type)
        SELECT bankCode, username, fileName, status, uploadedTime, uploadedFile, nameValid, versionvalid, protectioninfo, passwordInfo, hasUserBank, CONVERT(numeric(18, 0), id), type FROM dbo.SYS_UPLOADEDFILE WITH (HOLDLOCK TABLOCKX)')
GO
SET IDENTITY_INSERT dbo.Tmp_SYS_UPLOADEDFILE OFF
GO
DROP TABLE dbo.SYS_UPLOADEDFILE
GO
EXECUTE sp_rename N'dbo.Tmp_SYS_UPLOADEDFILE', N'SYS_UPLOADEDFILE', 'OBJECT' 
GO
ALTER TABLE dbo.SYS_UPLOADEDFILE ADD CONSTRAINT
    PK_SYS_UPLOADEDFILE PRIMARY KEY CLUSTERED 
    (
    id
    ) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO
COMMIT