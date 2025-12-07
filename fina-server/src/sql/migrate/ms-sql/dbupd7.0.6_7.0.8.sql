/*
 Database: MS SQL Server

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

IF
NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'ITEM_SEQUENCE' AND Object_ID = Object_ID('IN_FAQ_ITEMS'))
BEGIN
alter table IN_FAQ_ITEMS
    add ITEM_SEQUENCE int not null default 0
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
