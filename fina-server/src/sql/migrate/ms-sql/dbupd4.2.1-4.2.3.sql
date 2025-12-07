/*
 Database: SQL Server 2008/2012
 
 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 1.1
 Date : 07/11/2013
*/

/*
  Update DB Version
*/
update sys_properties set value='4.2.3' where prop_key='fina2.database.schemaVersion';

/*
	Add OUT_REPORTS code and optlock columns
*/
ALTER TABLE OUT_REPORTS ADD code varchar(12) NULL;
ALTER TABLE OUT_REPORTS ADD optlock INT DEFAULT 0 NOT NULL;

/*
  Add IN_MANAGING_BODIES code column
 */
ALTER TABLE IN_MANAGING_BODIES ADD code varchar(12) NULL;

/*
  Add IN_LICENCE_TYPES code column
 */
ALTER TABLE IN_LICENCE_TYPES ADD code varchar(12) NULL;


/*
Add table IN_IMPORTED_XML_RETURN
 */
CREATE TABLE [IN_IMPORTED_XML_RETURN](
	[ImportedReturnId] [int] NULL,
	[returnId] [decimal](10, 0) NULL,
	[importTime] [datetime] NULL,
	[status] [int] NULL
) ON [PRIMARY]
