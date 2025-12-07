/*
  Update DB Version
*/

UPDATE sys_properties
SET value = '8.0.3'
WHERE prop_key = 'fina2.database.schemaVersion';

ALTER TABLE IN_SCHEDULES
    ADD CONSTRAINT UK_SCHEDULES UNIQUE (BANKID, PERIODID, DEFINITIONID);

IF NOT EXISTS (SELECT 1
               FROM sys.key_constraints
               WHERE name = 'UK_SCHEDULES')
    BEGIN
        ALTER TABLE IN_SCHEDULES
            ADD CONSTRAINT UK_SCHEDULES UNIQUE (BANKID, PERIODID, DEFINITIONID);
    END

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[IN_REG_FILE_STAGE]') AND type = 'U')
    BEGIN
        CREATE TABLE IN_REG_FILE_STAGE (
                                            FILE_ID INT
        );
    END


IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'PRECISION'
                 AND Object_ID = Object_ID('SYS_SUB_MATRIX_TABLE_MAPPING'))
    begin

        alter table SYS_SUB_MATRIX_TABLE_MAPPING
            add PRECISION TINYINT default null;
    end

IF
    NOT EXISTS(SELECT 1
               FROM sys.columns
               WHERE Name = 'CONTENT_SIZE'
                 AND Object_ID = Object_ID('IN_LAW_DOCUMENT'))
    BEGIN
        alter table IN_LAW_DOCUMENT
            add CONTENT_SIZE BIGINT not null default 0;
        exec('UPDATE IN_LAW_DOCUMENT SET CONTENT_SIZE = DATALENGTH(content);')
    END;