/*
 Database: MS SQL Server

 Author: Lado Melikidze
 E: lado@fina2.net
 Version: 1.0
 Date : 23/12/2020
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.6'
WHERE prop_key = 'fina2.database.schemaVersion';


IF NOT EXISTS (SELECT * FROM SYS_PROPERTIES WHERE prop_key = 'net.fina.contentStorage.provider')
    BEGIN
        insert into SYS_PROPERTIES(prop_key,value) values('net.fina.contentStorage.provider','DATABASE')
    END;

IF NOT EXISTS (SELECT * FROM SYS_PROPERTIES WHERE prop_key = 'net.fina.contentStorage.repository.provider')
    BEGIN
        insert into SYS_PROPERTIES(prop_key,value) values('net.fina.contentStorage.repository.provider','JCR')
    END;


IF NOT EXISTS (select * from sys.tables where name = 'SYS_ROLE_MDT')
    CREATE TABLE SYS_ROLE_MDT (
        ROLE_ID [DECIMAL](10, 0) NOT NULL,
        NODE_ID [DECIMAL](10, 0) NOT NULL,
        CAN_AMEND BIT NOT NULL DEFAULT 0
    )
GO;

IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'REPOSITORY_FILE_VERSION_ID' AND Object_ID = Object_ID('OUT_STORED_REPORTS'))
BEGIN
alter table OUT_STORED_REPORTS add REPOSITORY_FILE_VERSION_ID varchar(250)
END;

IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'REPOSITORY_FILE_VERSION_ID' AND Object_ID = Object_ID('SYS_UPLOADEDFILE'))
BEGIN
alter table SYS_UPLOADEDFILE add REPOSITORY_FILE_VERSION_ID varchar(250)
END;


IF NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'REPOSITORY_FILE_VERSION_ID' AND Object_ID = Object_ID('IN_IMPORTED_RETURNS'))
BEGIN
alter table IN_IMPORTED_RETURNS add REPOSITORY_FILE_VERSION_ID varchar(250)
END;
