/*
 Database: MS SQL Server

 Author: Nikoloz Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 22/03/2019
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '6.0.0'
WHERE prop_key = 'fina2.database.schemaVersion';


ALTER TABLE IN_RETURN_ITEMS ALTER COLUMN VALUE nvarchar(4000) NULL;
go;


/*
Insert permissions
 */
DECLARE @ss_id INT
DECLARE @sp_id INT
DECLARE @lang_id INT

SET @ss_id = (SELECT max(id)
FROM SYS_STRINGS) + 1
SET @sp_id = (SELECT max(id)
FROM SYS_PERMISSIONS) + 1
SET @lang_id = (SELECT id
FROM sys_languages
WHERE code LIKE '%en%')

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id, @lang_id, 'net.fina.dcs.fileReview');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (@ss_id + 1, @lang_id, 'net.fina.inputManager.review');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id, @ss_id, 'net.fina.dcs.fileReview');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (@sp_id + 1, @ss_id + 1, 'net.fina.inputManager.review');
GO

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1
             FROM SYS_STRINGS)
WHERE PK_COLUMN_NAME = 'SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (SELECT max(id) + 1
             FROM SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME = 'SYS_PERMISSION_MAXID';
GO

/*   NBG-243   */
GO
ALTER TABLE dbo.IN_COMMUNICATOR_USERS_MESSAGE_STATUS
  ADD LAST_MESSAGE_ID BIGINT,
  READ_DATE DATETIME2
GO

/* Replace NULLs with 0's in messages that were stored in database before these changes */
GO
UPDATE dbo.IN_COMMUNICATOR_USERS_MESSAGE_STATUS
SET LAST_MESSAGE_ID = 0
WHERE LAST_MESSAGE_ID IS NULL
GO

GO
ALTER TABLE SYS_NOTIFICATION_USERS
    ADD READ_DATE DATETIME2;
GO

IF
    NOT EXISTS(SELECT *
               FROM sys.objects
               WHERE object_id = OBJECT_ID(N'[IN_IMPORTED_RETURNS_sequence]')
                 AND type = 'SO')
    BEGIN
        DECLARE
            @IN_IMPORTED_RETURNS_START as BIGINT;
        set
            @IN_IMPORTED_RETURNS_START = (select MAX(ID) + 1 FROM IN_IMPORTED_RETURNS)
        DECLARE
            @IN_IMPORTED_RETURNS_SQL NVARCHAR(MAX)

        SET @IN_IMPORTED_RETURNS_SQL =
                    'CREATE SEQUENCE IN_IMPORTED_RETURNS_sequence
                     AS [BIGINT]
                     START WITH ' + cast(@IN_IMPORTED_RETURNS_START as varchar)+
                    'INCREMENT BY 1
                        CACHE'
        EXEC (@IN_IMPORTED_RETURNS_SQL);
    END;

--  NBG-234: compute and set LAST_CONVERSATION_MESSAGE_DATEs for root messages where absent
GO

SELECT ID INTO #root_messages
FROM IN_COMMUNICATOR_MESSAGES
WHERE REPLYTOID = 0 AND LAST_CONVERSATION_MESSAGE_DATE is NULL
ORDER BY ID

DECLARE @row_count int
SELECT @row_count = COUNT(*) FROM #root_messages

DECLARE @send_date datetime

DECLARE @cur_msg_id int
SELECT @cur_msg_id = MIN(ID) FROM #root_messages

WHILE @row_count <> 0
  BEGIN
    -- Get the send date of the last message in the conversation of current root message
    SELECT @send_date = MAX(SENDDATE)
    FROM IN_COMMUNICATOR_MESSAGES
    WHERE ID = @cur_msg_id OR REPLYTOID = @cur_msg_id

    UPDATE IN_COMMUNICATOR_MESSAGES set LAST_CONVERSATION_MESSAGE_DATE = @send_date
    WHERE ID = @cur_msg_id

    DELETE #root_messages WHERE @cur_msg_id = ID

    SELECT @cur_msg_id = MIN(ID) FROM #root_messages
    SET @row_count = @row_count - 1
  END

GO

-- NBG-312 add USERTYPE column to SYS_USERS and set value to 0
alter table SYS_USERS
  add USERTYPE DECIMAL(10)
GO;

update SYS_USERS set USERTYPE = 0;

ALTER TABLE IN_SCHEDULES
    ADD COMMENT NVARCHAR(200)
GO

--NBG-246 Translate net.fina.dcs.undefinedBank
update SYS_STRINGS set VALUE=N'Incorrect Financial institutions.'
from SYS_PERMISSIONS s
       left join SYS_STRINGS on SYS_STRINGS.ID=s.NAMESTRID
where s.IDNAME like '%net.fina.dcs.undefinedBank%'and LANGID=1

update SYS_STRINGS set VALUE=N'áá áá¡á¬áá á á¤ááááá¡á£á á ááá¡á¢áá¢á£á¢á.'
from SYS_PERMISSIONS s
       left join SYS_STRINGS on SYS_STRINGS.ID=s.NAMESTRID
where s.IDNAME like '%net.fina.dcs.undefinedBank%'and LANGID=2
