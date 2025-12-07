/*
 Database: SQL Server 2008/2012

 Author: Mikheil Tchelidze
 E: chelomisha@fina2.net
 Version: 1.0
 Date : 07/04/2014
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '4.4.0'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
  Add SEQUENCE column at the Table IN_DEFINITION_TABLES
 */
ALTER TABLE IN_DEFINITION_TABLES ADD SEQUENCE NUMERIC(10, 0);
GO
-- because DefinitionTable constructor has primitive type parameter.
UPDATE IN_DEFINITION_TABLES
SET SEQUENCE = 0;

/*
  LDAP properties:
    *** password
    *** authentication security
    *** attribute filter
*/
DELETE FROM sys_properties
WHERE prop_key IN ('fina2.authentication.ldap.password');
INSERT INTO sys_properties (prop_key, value) VALUES ('fina2.authentication.ldap.password', 'secret');

DELETE FROM sys_properties
WHERE prop_key IN ('fina2.authentication.ldap.security');
INSERT INTO sys_properties (prop_key, value) VALUES ('fina2.authentication.ldap.security', 'simple');

DELETE FROM sys_properties
WHERE prop_key IN ('fina2.authentication.ldap.attribute.filter');
INSERT INTO sys_properties (prop_key, value) VALUES ('fina2.authentication.ldap.attribute.filter', 'o=fina')

GO

/*
    IN_ABD_AGENT_STATUSES
 */
CREATE TABLE [dbo].[IN_ABD_AGENT_STATUSES] (
  [ID]                [bigint]       NOT NULL,
  [ACTIVE_STATUS]     [int]          NULL,
  [NOTE]              [varchar](255) NULL,
  [REVISION]          [int]          NULL,
  [STATUS]            [int]          NULL,
  [STATUS_DATE]       [datetime2](7) NULL,
  [TRANSACTION_LIMIT] [float]        NULL,
  [TYPE]              [int]          NULL,
  [ABD_AGENT_ID]      [bigint]       NULL,
  [BANK_ID]           [bigint]       NULL,
  [SN]                [bigint]       NULL,
  PRIMARY KEY CLUSTERED
    (
      [ID] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]

GO

/**
    IN_ABD_AGENTS
 */
CREATE TABLE [dbo].[IN_ABD_AGENTS] (
  [ID]             [bigint]       NOT NULL,
  [address]        [varchar](255) NULL,
  [code]           [varchar](255) NULL,
  [DATE_APPOINTED] [datetime2](7) NULL,
  [email]          [varchar](255) NULL,
  [name]           [varchar](255) NULL,
  [phone]          [varchar](255) NULL,
  [LGA_ID]         [bigint]       NULL,
  [STATE_ID]       [bigint]       NULL,
  [SN]             [bigint]       NULL,
  PRIMARY KEY CLUSTERED
    (
      [ID] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]

GO

/*
  ABD agent hibernate envers table

  http://envers.jboss.org/
 */
CREATE TABLE [dbo].[IN_ABD_AGENTS_AUD] (
  [ID]             [bigint]       NOT NULL,
  [REV]            [int]          NOT NULL,
  [REVTYPE]        [smallint]     NULL,
  [address]        [varchar](255) NULL,
  [code]           [varchar](255) NULL,
  [DATE_APPOINTED] [datetime2](7) NULL,
  [email]          [varchar](255) NULL,
  [name]           [varchar](255) NULL,
  [phone]          [varchar](255) NULL,
  [LGA_ID]         [bigint]       NULL,
  [STATE_ID]       [bigint]       NULL,
  PRIMARY KEY CLUSTERED
    (
      [ID] ASC,
      [REV] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]

/**
  hibernate envers REVINFO table

  http://envers.jboss.org/
 */
CREATE TABLE [dbo].[REVINFO] (
  [REV]      [int] IDENTITY (1, 1) NOT NULL,
  [REVTSTMP] [bigint]              NULL,
  PRIMARY KEY CLUSTERED
    (
      [REV] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY]

/*
	Add permission
*/
DECLARE @ss_id INT
DECLARE @sp_id INT
DECLARE @lang_id INT

SET @ss_id = (SELECT
                max(id)
              FROM SYS_STRINGS) + 1
SET @sp_id = (SELECT
                max(id)
              FROM SYS_PERMISSIONS) + 1
SET @lang_id = (SELECT
                  id
                FROM sys_languages
                WHERE code LIKE '%en%')
--name
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id, @lang_id, 'net.fina.dcs.abd.tab');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 1, @lang_id, 'net.fina.dcs.umfi.tab');
--permission
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id, @ss_id, 'net.fina.dcs.abd.tab');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.dcs.umfi.tab');
GO

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_STRINGS)
WHERE PK_COLUMN_NAME ='SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME ='SYS_PERMISSION_MAXID';

GO
