/*
 Database: SQL Server 2012

 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 11/12/2015
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.2'
WHERE prop_key = 'fina2.database.schemaVersion';

CREATE TABLE [SYS_AUDIT_LOG] (
  [ID]                        [varchar](60)   NOT NULL,
  [ENTITY_ID]                 [varchar](256)  NOT NULL,
  [ENTITY_NAME]               [varchar](256)  NOT NULL,
  [ENTITY_PROPERTY]           [varchar](4000) NOT NULL,
  [ENTITY_PROPERTY_NEW_VALUE] [varchar](4000) NULL,
  [ENTITY_PROPERTY_OLD_VALUE] [varchar](4000) NULL,
  [OPERATION_TYPE]            [int]           NULL,
  [ACTOR_ID]                  [varchar](50)   NOT NULL,
  [RELEVANCE_TIME]            [datetime]      NOT NULL
);

INSERT INTO SYS_PROPERTIES VALUES ('net.fina.auditLog.level', 0);


/*
  Upload file sequence
 */
CREATE SEQUENCE upload_file_sequence
AS [bigint]
START WITH <SYS_UPLOADEDFILE_MAXID+1>
INCREMENT BY 1
CACHE
GO


/**Change transaction isolation**/
ALTER DATABASE database- NAME SET ALLOW_SNAPSHOT_ISOLATION ON
ALTER DATABASE database- NAME SET SINGLE_USER WITH ROLLBACK IMMEDIATE
ALTER DATABASE database- NAME SET READ_COMMITTED_SNAPSHOT ON
ALTER DATABASE database- NAME SET MULTI_USER


