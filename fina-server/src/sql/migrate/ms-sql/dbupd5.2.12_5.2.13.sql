/*
 Database: MS SQL Server

 Author: Nikoloz Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 06/09/2016
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.13'
WHERE prop_key = 'fina2.database.schemaVersion';

CREATE TABLE OUT_REPORT_PROPERTIES
(
  report_id INT,
  prop_key VARCHAR(1024),
  name VARCHAR(2048),
  value VARCHAR(2048),
  renewable INT DEFAULT 0
);

DROP TABLE SYS_SCHEDULED_TASKS;
--DROP TABLE IN_RETURNS_SCHEDULE;
CREATE TABLE IN_RETURNS_SCHEDULE(
	[ID] [int] NOT NULL,
	[PARENTID] [int] NOT NULL,
	[TASKNAME] [nvarchar](256) NULL,
	[SCHEDULETIME] [datetime] NULL,
	[ONDEMAND] [int] NULL,
	[USERID] [int] NOT NULL,
	[SCHEDULEID] [decimal](10, 0) NULL,
	[VERSIONID] [decimal](10, 0) NULL,
	[STATUS] [int] NOT NULL,
	[MESSAGE] [nvarchar](max) NULL,
	CONSTRAINT PK_IN_RETURNS_SCHEDULE PRIMARY KEY CLUSTERED (ID ASC)
);


sp_rename "IN_MDT_COMPARISON.EQUATION", "RIGHT_EQUATION", "COLUMN";
ALTER TABLE IN_MDT_COMPARISON ADD LEFT_EQUATION nvarchar(max);
update IN_MDT_COMPARISON set LEFT_EQUATION = '';



