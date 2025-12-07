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
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

--  // SYS_USERS_INDEXES
IF
    NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USERS_FULL_INDEX'
              AND object_id = OBJECT_ID('SYS_USERS'))
CREATE INDEX SYS_USERS_FULL_INDEX ON [fina2nbg].[dbo].[SYS_USERS] ([ID]) INCLUDE ([LOGIN], [PASSWORD], [CHANGEPASSWORD], [NAMESTRID], [TITLESTRID], [PHONE], [EMAIL], [BLOCKED], [LASTLOGINDATE], [LASTPASSWORDCHANGEDATE], [optlock], [USERTYPE], [DELETED], [CONTACTPERSONSTRID], [DISABLED]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USERS_ID_INDEX'
              AND object_id = OBJECT_ID('SYS_USERS'))
CREATE INDEX SYS_USERS_ID_INDEX ON [fina2nbg].[dbo].[SYS_USERS] ([ID]);


IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USERS_LOGIN_INDEX'
              AND object_id = OBJECT_ID('SYS_USERS'))
CREATE INDEX SYS_USERS_LOGIN_INDEX ON [fina2nbg].[dbo].[SYS_USERS] ([ID]) INCLUDE ([LOGIN]);


-- // IN_COMMUNICATOR_USERS_MESSAGE_STATUS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_COMMUNICATOR_STATUS_INDEX_1'
              AND object_id = OBJECT_ID('IN_COMMUNICATOR_USERS_MESSAGE_STATUS'))
CREATE INDEX IN_COMMUNICATOR_STATUS_INDEX_1 ON [fina2nbg].[dbo].[IN_COMMUNICATOR_USERS_MESSAGE_STATUS] ([message_id], [message_user_id]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_COMMUNICATOR_STATUS_INDEX_2'
              AND object_id = OBJECT_ID('IN_COMMUNICATOR_USERS_MESSAGE_STATUS'))
CREATE INDEX IN_COMMUNICATOR_STATUS_INDEX_2 ON [fina2nbg].[dbo].[IN_COMMUNICATOR_USERS_MESSAGE_STATUS] ([message_id], [message_user_id]) INCLUDE ([id], [status], [USER_ID], [LAST_MESSAGE_ID], [READ_DATE]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_COMMUNICATOR_STATUS_INDEX_3'
              AND object_id = OBJECT_ID('IN_COMMUNICATOR_USERS_MESSAGE_STATUS'))
CREATE INDEX IN_COMMUNICATOR_STATUS_INDEX_3 ON [fina2nbg].[dbo].[IN_COMMUNICATOR_USERS_MESSAGE_STATUS] ([status], [USER_ID]) INCLUDE ([message_id], [message_user_id]);



-- // SYS_USER_BANKS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USER_BANKS_INDEX_1'
              AND object_id = OBJECT_ID('SYS_USER_BANKS'))
CREATE INDEX SYS_USER_BANKS_INDEX_1 ON [fina2nbg].[dbo].[SYS_USER_BANKS] ([USERID]) INCLUDE ([BANKID]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USER_BANKS_INDEX_2'
              AND object_id = OBJECT_ID('SYS_USER_BANKS'))
CREATE INDEX SYS_USER_BANKS_INDEX_2 ON [fina2nbg].[dbo].[SYS_USER_BANKS] ([USERID]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_USER_BANKS_INDEX_3'
              AND object_id = OBJECT_ID('SYS_USER_BANKS'))
CREATE INDEX SYS_USER_BANKS_INDEX_3 ON [fina2nbg].[dbo].[SYS_USER_BANKS] ([BANKID]);


-- // IN_BANKS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANKS_INDEX_1'
              AND object_id = OBJECT_ID('IN_BANKS'))
CREATE INDEX IN_BANKS_INDEX_1 ON [fina2nbg].[dbo].[IN_BANKS] ([TYPEID], [DISABLE],[ID]) INCLUDE ([CODE], [SHORTNAMESTRID], [NAMESTRID], [ADDRESSSTRID], [PHONE], [FAX], [EMAIL], [SWIFTCODE], [REGIONID], [optlock], [IDENTIFICATION_CODE], [LEGAL_FORM], [MDT_DATA_NODE_ID], [CREATED_AT], [MODIFIED_AT], [CONTACT_PERSON], [REPRESENTATIVE], [REORGANISATION], [MOBILE_OFFICES], [REGISTRATION_DATE], [CLOSE_DATE], [WEB_SITE], [EMPLOYES], [ADDITIONAL_INFO_ID]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANKS_INDEX_2'
              AND object_id = OBJECT_ID('IN_BANKS'))
CREATE INDEX IN_BANKS_INDEX_2 ON [fina2nbg].[dbo].[IN_BANKS] ([TYPEID], [DISABLE]) INCLUDE ([ID], [CODE], [NAMESTRID]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANKS_INDEX_3'
              AND object_id = OBJECT_ID('IN_BANKS'))
CREATE INDEX IN_BANKS_INDEX_3 ON [fina2nbg].[dbo].[IN_BANKS] ([TYPEID]) INCLUDE ([CODE]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_BANKS_INDEX_4'
              AND object_id = OBJECT_ID('IN_BANKS'))
CREATE INDEX IN_BANKS_INDEX_4 ON [fina2nbg].[dbo].[IN_BANKS] ([DISABLE]) INCLUDE ([TYPEID]);


-- // IN_IMPORTED_RETURNS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_IMPORTED_RETURNS_INDEX_1'
              AND object_id = OBJECT_ID('IN_IMPORTED_RETURNS'))
CREATE INDEX IN_IMPORTED_RETURNS_INDEX_1 ON [fina2nbg].[dbo].[IN_IMPORTED_RETURNS] ([bankCode], [xlsId],[versionCode], [periodStart], [periodEnd]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_IMPORTED_RETURNS_INDEX_2'
              AND object_id = OBJECT_ID('IN_IMPORTED_RETURNS'))
CREATE INDEX IN_IMPORTED_RETURNS_INDEX_2 ON [fina2nbg].[dbo].[IN_IMPORTED_RETURNS] ([bankCode]) INCLUDE ([versionCode], [periodStart], [periodEnd], [xlsId]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_IMPORTED_RETURNS_INDEX_3'
              AND object_id = OBJECT_ID('IN_IMPORTED_RETURNS'))
CREATE INDEX IN_IMPORTED_RETURNS_INDEX_3 ON [fina2nbg].[dbo].[IN_IMPORTED_RETURNS] ([xlsId]) INCLUDE ([bankCode], [versionCode], [periodStart], [periodEnd]);



-- // SYS_UPLOADEDFILE
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_UPLOADEDFILE_INDEX_1'
              AND object_id = OBJECT_ID('SYS_UPLOADEDFILE'))
CREATE INDEX SYS_UPLOADEDFILE_INDEX_1 ON [fina2nbg].[dbo].[SYS_UPLOADEDFILE] ([uploadedTime], [type]) INCLUDE ([bankCode], [status]);
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_UPLOADEDFILE_INDEX_2'
              AND object_id = OBJECT_ID('SYS_UPLOADEDFILE'))
CREATE INDEX SYS_UPLOADEDFILE_INDEX_2 ON [fina2nbg].[dbo].[SYS_UPLOADEDFILE] ([username]) INCLUDE ([bankCode], [fileName], [status], [uploadedTime], [nameValid], [versionvalid], [protectioninfo], [hasUserBank], [id], [type], [matrixValid], [reason], [PROCESS_ENGINE]);

IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'SYS_UPLOADEDFILE_INDEX_3'
              AND object_id = OBJECT_ID('SYS_UPLOADEDFILE'))
CREATE INDEX SYS_UPLOADEDFILE_INDEX_3 ON [fina2nbg].[dbo].[SYS_UPLOADEDFILE] ([username]) INCLUDE ([bankCode], [fileName], [status], [uploadedTime], [nameValid], [versionvalid], [protectioninfo], [hasUserBank], [id], [type], [matrixValid], [reason], [PROCESS_ENGINE]);



-- // IN_LEGAL_PERSONS
IF
NOT EXISTS(
            SELECT *
            FROM sys.indexes
            WHERE name = 'IN_LEGAL_PERSONS_INDEX_1'
              AND object_id = OBJECT_ID('IN_LEGAL_PERSONS'))
CREATE INDEX IN_LEGAL_PERSONS_INDEX_1 ON [fina2nbg].[dbo].[IN_LEGAL_PERSONS] ([FI_ID]);
