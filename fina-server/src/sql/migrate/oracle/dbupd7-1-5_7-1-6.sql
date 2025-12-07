/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.1.6'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
FI
*/
DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANKS'
  AND upper(column_name) = 'ADDITIONAL_INFO_ID';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'alter table IN_BANKS add ADDITIONAL_INFO_ID numeric';
END IF;
END;


 DECLARE
l_cnt integer;
BEGIN
SELECT count(*)
into l_cnt
FROM dba_tables
where table_name = 'IN_BANKS_ADDITIONAL_INFO';
IF
( l_cnt = 0 )
  THEN
create table IN_BANKS_ADDITIONAL_INFO
(
    ID              numeric,
    BUSINESS_ENTITY varchar(200),
    ECONOMIC_ENTITY varchar(200),
    EQUITY_FORM     varchar(200),
    MANAGEMENT_FORM varchar(200)
);
END IF;
END;

CREATE SEQUENCE fi_additional_info_sequence MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20;
