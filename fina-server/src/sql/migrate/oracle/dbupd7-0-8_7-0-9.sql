/*
 Database: Oracle

 Author: Baaka Tsutskhvashvili
 E: baaka@fina2.net
 Version: 1.0
 Date : 21/05/2021
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.9'
WHERE prop_key = 'fina2.database.schemaVersion';

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'EMS_SANCTION_FINE_TYPES'
  AND upper(column_name) = 'ft_rule';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE EMS_SANCTION_FINE_TYPES ADD ft_rule varchar(255)';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_MANAGEMENT'
  AND upper(column_name) = 'DISABLE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_BANK_MANAGEMENT ADD DISABLE bit';
EXECUTE IMMEDIATE 'update IN_BANK_MANAGEMENT set DISABLE = 0';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'IN_BANK_BRANCHES'
  AND upper(column_name) = 'DISABLE';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_BANK_BRANCHES ADD DISABLE bit';
EXECUTE IMMEDIATE 'update IN_BANK_BRANCHES set DISABLE = 0';
END IF;
END;

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*)
INTO l_cnt
FROM all_tab_cols
WHERE upper(table_name) = 'EMS_INSPECTIONS'
  AND upper(column_name) = 'decreeDate';

IF
( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE EMS_INSPECTIONS ADD decreeDate date';
END IF;
END;


