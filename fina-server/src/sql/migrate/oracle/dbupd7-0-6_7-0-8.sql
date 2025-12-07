/*
 Database: Oracle

 Author: Baaka Tsutskhvashvili
 E: baaka@fina2.net
 Version: 1.0
 Date : 02/03/2021
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '7.0.8'
WHERE prop_key = 'fina2.database.schemaVersion';

DECLARE
l_cnt integer;
BEGIN
SELECT COUNT(*) INTO l_cnt FROM all_tab_cols
WHERE upper(table_name) = 'IN_FAQ_ITEMS' AND upper(column_name) = 'ITEM_SEQUENCE';

IF( l_cnt = 0 )
  THEN
    EXECUTE IMMEDIATE 'ALTER TABLE IN_FAQ_ITEMS ADD ITEM_SEQUENCE integer not null default 0';
END IF;
END;

BEGIN
UPDATE
    IN_FAQ_ITEMS
SET IN_FAQ_ITEMS.ITEM_SEQUENCE = RAN.ITEM_SEQUENCE FROM
    IN_FAQ_ITEMS IFA
INNER JOIN
    (select CATEGORY_ID, ID,
       row_number() over (partition by CATEGORY_ID order by ID) as ITEM_SEQUENCE
from IN_FAQ_ITEMS) RAN
ON
    IFA.ID = RAN.ID
END;
