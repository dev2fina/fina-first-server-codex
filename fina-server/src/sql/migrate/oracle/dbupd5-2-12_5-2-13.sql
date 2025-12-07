/*
 Database: Oracle 11g, 12c

 Author: Vamekh Goiati
 E: goiati@fina2.net
 Version: 1.0
 Date : 17/10/2016
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.13'
WHERE prop_key = 'fina2.database.schemaVersion';

CREATE TABLE OUT_REPORT_PROPERTIES
(
  report_id number(10, 0),
  prop_key VARCHAR2(1024),
  name VARCHAR2(2048),
  value VARCHAR2(2048),
  renewable number DEFAULT 0
);

DROP TABLE SYS_SCHEDULED_TASKS;
--DROP TABLE IN_RETURNS_SCHEDULE;
CREATE TABLE IN_RETURNS_SCHEDULE(
	id number(10,0) NOT NULL,
	parentid number(10,0) NOT NULL,
	taskname nvarchar2(256),
	scheduletime timestamp,
	ondemand number,
	userid number(10,0) NOT NULL,
	scheduleid number(10,0) NOT NULL,
	versionid number(10,0) NOT NULL,
	status number NOT NULL,
	message nvarchar2(2000),
	CONSTRAINT PK_IN_RETURNS_SCHEDULE PRIMARY KEY (id)
);

ALTER TABLE IN_MDT_COMPARISON RENAME COLUMN EQUATION to RIGHT_EQUATION;
ALTER TABLE IN_MDT_COMPARISON ADD LEFT_EQUATION varchar2(1000);
update IN_MDT_COMPARISON set LEFT_EQUATION = '';



