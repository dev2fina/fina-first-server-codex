/*
  Update DB Version
*/

UPDATE sys_properties
SET value = '8.0.2'
WHERE prop_key = 'fina2.database.schemaVersion';


alter table IN_RETURNS add STATUS NUMBER;

update IN_RETURNS r set r.status=(select rss.status from IN_RETURN_STATUSES rss where rss.RETURNID=r.id and rss.ID=(select max(rss1.ID) from IN_RETURN_STATUSES rss1 where rss1.RETURNID=rss.RETURNID)) ;
create MATERIALIZED VIEW LOG ON IN_SCHEDULES with ROWID;
create MATERIALIZED VIEW LOG ON IN_RETURN_DEFINITIONS with ROWID;
create MATERIALIZED VIEW LOG ON IN_RETURN_TYPES with ROWID;
create MATERIALIZED VIEW LOG ON IN_BANKS with ROWID;
create MATERIALIZED VIEW LOG ON IN_RETURN_VERSIONS with ROWID;
create MATERIALIZED VIEW LOG ON IN_PERIOD_TYPES with ROWID;
create MATERIALIZED VIEW LOG ON IN_PERIODS with ROWID;
create MATERIALIZED VIEW LOG ON IN_RETURNS with ROWID;
CREATE MATERIALIZED VIEW IN_RETURNS_MV
 NOLOGGING
  CACHE
BUILD IMMEDIATE
REFRESH FAST ON COMMIT
    AS
select ir.ID as RETURN_ID,ir.SCHEDULEID as SCHEDULEID,
       ir.VERSIONID,ir.VERSION as LATEST_VERSION,
       rd.ID as DEFINITIONID, rd.MANUALINPUT,
       rt.ID as RETURNTYPEID, rt.CODE as RETURNTYPECODE,rt.EXCELTEMPLATE,
       p.id as PERIODID,p.FROMDATE,p.TODATE,
       pt.ID as PERIODTYPEID,pt.CODE as PERIODTYPECODE,
       fi.id as BANKID,fi.code as BANK_CODE,fi.NAMESTRID,
       rv.CODE as VERSIONCODE,
       ir.STATUS as STATUS,
       ir.ROWID as R_ROWID,rd.ROWID as RD_ROWID,s.ROWID as S_ROWID,rt.ROWID as RT_WORID,p.ROWID as P_ROWID, pt.ROWID as PT_ROWID,fi.ROWID as FI_ROWID,rv.ROWID as RV_ROWID
from IN_RETURNS ir,
     IN_SCHEDULES s,
     IN_RETURN_DEFINITIONS rd,
     IN_RETURN_TYPES rt,
     IN_PERIODS p ,
     IN_PERIOD_TYPES pt,
     IN_BANKS fi,
     IN_RETURN_VERSIONS rv
where
    ir.SCHEDULEID=s.ID
  and  s.DEFINITIONID=rd.ID
  and rd.TYPEID=rt.ID
  and s.PERIODID=p.ID
  and pt.ID=p.PERIODTYPEID
  and s.BANKID=fi.ID
  and ir.VERSIONID=rv.ID;

-- Drop IN_IMPORTED_QUEUE Table
Drop  table  IN_IMPORTED_QUEUE;



DECLARE
    v_count NUMBER;
BEGIN
    -- Check if the table exists
    SELECT COUNT(*)
    INTO v_count
    FROM ALL_TABLES
    WHERE TABLE_NAME = 'IN_IMPORTED_RETURNS_GROUP'
      AND OWNER = USER;

    -- If the table does not exist, create it
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE IN_IMPORTED_RETURNS_GROUP (
                id          NUMBER NOT NULL,
                returnCode  NVARCHAR2(50),
                bankCode    NVARCHAR2(50),
                versionCode NVARCHAR2(50),
                periodStart DATE,
                periodEnd   DATE,
                userId      NUMBER,
                type        NUMBER,
                xlsId       NUMBER
            )';
END IF;
END;
/

DECLARE
    v_count NUMBER;
BEGIN
    -- Check if the table IN_IMPORTED_RETURNS_GROUP is empty
    SELECT COUNT(*)
    INTO v_count
    FROM IN_IMPORTED_RETURNS_GROUP;

    -- If the table is empty, execute the insert query
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            INSERT INTO IN_IMPORTED_RETURNS_GROUP (id, returnCode, bankCode, versionCode, periodStart, periodEnd, userId, type, xlsId)
            SELECT
                ROW_NUMBER() OVER (ORDER BY i1_0.periodEnd) AS id,
                MAX(i1_0.returnCode) AS returnCode,
                MAX(i1_0.bankCode) AS bankCode,
                MAX(i1_0.versionCode) AS versionCode,
                MAX(i1_0.periodStart) AS periodStart,
                MAX(i1_0.periodEnd) AS periodEnd,
                MAX(i1_0.userId) AS userId,
                MAX(i1_0.type) AS type,
                MAX(i1_0.xlsId) AS xlsId
            FROM IN_IMPORTED_RETURNS i1_0
            GROUP BY i1_0.BANKCODE, i1_0.PERIODSTART, i1_0.PERIODEND, i1_0.VERSIONCODE, i1_0.xlsId, i1_0.userId
        ';
END IF;
END;
/


DECLARE
    v_sequence_name VARCHAR2(128) := 'IN_IMPORTED_RETURNS_GROUP_sequence'; -- Increased buffer size for sequence name
        v_start_value   NUMBER;
        v_count         NUMBER;
BEGIN
    -- Check if the sequence exists
    SELECT COUNT(*)
    INTO v_count
    FROM USER_SEQUENCES
    WHERE SEQUENCE_NAME = UPPER(v_sequence_name); -- Ensure case-insensitive comparison

    IF v_count = 0 THEN
        -- Get the maximum ID from the table or set default start value
        SELECT COALESCE(MAX(id), 0) + 1
        INTO v_start_value
        FROM IN_IMPORTED_RETURNS_GROUP;

    -- Create the sequence with the start value
    EXECUTE IMMEDIATE 'CREATE SEQUENCE ' || v_sequence_name || '
            START WITH ' || v_start_value || '
            INCREMENT BY 1
            CACHE 20';
END IF;
END;
/

DECLARE
    index_count NUMBER;
BEGIN
    -- Check if the index 'SYS_UPLOADEDFILE_ID_FILENAME' exists
    SELECT COUNT(*)
    INTO index_count
    FROM USER_INDEXES
    WHERE INDEX_NAME = 'SYS_UPLOADEDFILE_ID_FILENAME';

    -- Create the index if it does not exist
    IF index_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE INDEX SYS_UPLOADEDFILE_ID_FILENAME
            ON SYS_UPLOADEDFILE (id,fileName)';
END IF;
END;
/

DECLARE
    index_count NUMBER;
BEGIN
    -- Check if the index 'IN_IMPORTED_RETURNS_GROUP_PERIODEND' exists
    SELECT COUNT(*)
    INTO index_count
    FROM USER_INDEXES
    WHERE INDEX_NAME = 'IN_IMPORTED_RETURNS_GROUP_PERIODEND';

    -- Create the index if it does not exist
    IF index_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE INDEX IN_IMPORTED_RETURNS_GROUP_PERIODEND
            ON IN_IMPORTED_RETURNS_GROUP (periodEnd,bankcode,versioncode,periodstart,xlsid,userid)';
END IF;
END;
/


DECLARE
    index_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO index_count
    FROM USER_INDEXES
    WHERE INDEX_NAME = 'IN_IMPORTED_XML_RETURN_RETURNID_IDX';

    -- Create the index if it does not exist
    IF index_count = 0 THEN
        EXECUTE IMMEDIATE '
                    CREATE INDEX IN_IMPORTED_XML_RETURN_returnId_idx ON IN_IMPORTED_XML_RETURN (returnId)';
END IF;
END;
/


CREATE INDEX R_VERSION
    ON IN_RETURNS (version);
CREATE INDEX R_VERSION_ID
    ON IN_RETURNS (ID,version);
