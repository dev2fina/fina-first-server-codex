/*
  Update DB Version
*/

UPDATE sys_properties
SET value = '8.0.3'
WHERE prop_key = 'fina2.database.schemaVersion';

BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM USER_CONSTRAINTS
        WHERE CONSTRAINT_NAME = 'UK_SCHEDULES'
    ) THEN
        EXECUTE IMMEDIATE '
            ALTER TABLE IN_SCHEDULES
            ADD CONSTRAINT UK_SCHEDULES UNIQUE (BANKID, PERIODID, DEFINITIONID)
        ';
END IF;
END;
