/*
 Database: MS SQL Server

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

IF
NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'ft_rule' AND Object_ID = Object_ID('EMS_SANCTION_FINE_TYPES'))
BEGIN
alter table EMS_SANCTION_FINE_TYPES
    add ft_rule nvarchar(255)
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'DISABLE' AND Object_ID = Object_ID('IN_BANK_MANAGEMENT'))
BEGIN
ALTER TABLE IN_BANK_MANAGEMENT
    ADD DISABLE bit;
EXEC('update IN_BANK_MANAGEMENT set DISABLE = 0');
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'DISABLE' AND Object_ID = Object_ID('IN_BANK_BRANCHES'))
BEGIN
ALTER TABLE IN_BANK_BRANCHES
    ADD DISABLE bit;
EXEC('update IN_BANK_BRANCHES set DISABLE = 0');
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns
              WHERE Name = 'decreeDate' AND Object_ID = Object_ID('EMS_INSPECTIONS'))
BEGIN
ALTER TABLE EMS_INSPECTIONS
    ADD decreeDate datetime;
END;
