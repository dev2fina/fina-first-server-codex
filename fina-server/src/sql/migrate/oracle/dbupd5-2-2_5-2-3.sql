/*
 Database: Oracle 11g, 12c

 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 0.1
 Date : 11/12/2015
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.2.3'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
	Add Mail messages original subject
*/
ALTER TABLE IN_MAIL_MESSAGE ADD SUBJECT NVARCHAR2(512);

/*
	insert max cycled mail size property <=0 means disabled;
*/
insert into SYS_PROPERTIES values('fina2.mail.blockCycledMail.size','-1');

/*
  FI fax label (* also used in FiPrintServlet as fax column header)
*/
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('net.fina.fi.fax.label', 'Fax');

/*  
    Author: Vamekh Goiati
    Permissions for calendar
*/
DECLARE
  MAX_SS_ID NUMBER;
  MAX_SP_ID NUMBER;

BEGIN         
  select max(id) + 1 into MAX_SS_ID from SYS_STRINGS;
  select max(id) + 1 into MAX_SP_ID from SYS_PERMISSIONS;

INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID, 1, 'net.fina.calendar.review');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 1, 1, 'net.fina.calendar.amend');
INSERT INTO SYS_STRINGS (id, langID, value) VALUES (MAX_SS_ID + 2, 1, 'net.fina.calendar.delete');

INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID, MAX_SS_ID, 'net.fina.calendar.review');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 1, MAX_SS_ID + 1, 'net.fina.calendar.amend');
INSERT INTO SYS_PERMISSIONS (id, nameStrId, idName) VALUES (MAX_SP_ID + 2, MAX_SS_ID + 2, 'net.fina.calendar.delete');

end;
commit;

-- update SYS_ID_GENERATOR
UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_STRINGS)
WHERE PK_COLUMN_NAME ='SYS_STRING_MAXID';

UPDATE SYS_ID_GENERATOR
SET VALUE = (select max(id) + 1 from SYS_PERMISSIONS)
WHERE PK_COLUMN_NAME ='SYS_PERMISSION_MAXID';

commit;
