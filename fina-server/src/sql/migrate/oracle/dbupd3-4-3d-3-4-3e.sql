/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             Oracle 10                                                              */
/* Created on:            14/03/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */
/* */    
/*===============================================================================================*/
create sequence HIBERNATE_SEQUENCE;
DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key)='fina2.authenticatedModes';
insert into sys_properties(prop_key,value) values('fina2.authenticatedModes','FINA,LDAP');
DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key)='fina2.current.authentication';
insert into sys_properties(prop_key,value) values('fina2.current.authentication','FINA');
alter table SYS_UPLOADEDFILE modify username VARCHAR2(200);
ALTER TABLE SYS_UPLOADEDFILE ADD type int;
ALTER TABLE IN_IMPORTED_RETURNS ADD type int;
ALTER TABLE IN_IMPORTED_RETURNS ADD xlsId numeric(18,0);
UPDATE SYS_UPLOADEDFILE SET type=0;
DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key)='fina2.update.gui.filelocation';
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina2.update.gui.filelocation', 'C:\fina-server\GUI_UPDATE');
DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key)='fina2.update.start';
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina2.update.start', 'TRUE');

DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key) IN('fina2.authentication.ldap.urlIp','fina2.authentication.ldap.urlPort','fina2.authentication.ldap.organizationalUnit','fina2.authentication.ldap.domainComponent');

insert into sys_properties(prop_key,value) values('fina2.authentication.ldap.urlIp','10.131.36.250');
insert into sys_properties(prop_key,value) values('fina2.authentication.ldap.urlPort','389');
insert into sys_properties(prop_key,value) values('fina2.authentication.ldap.organizationalUnit','Fina');
insert into sys_properties(prop_key,value) values('fina2.authentication.ldap.domainComponent','fina,ge');

DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key) IN('fina2.email.address','fina2.email.pwd','fina2.pop.protocol','fina2.smtp.protocol');
insert into sys_properties(prop_key,value) values('fina2.email.address','javamail12@gmail.com');
insert into sys_properties(prop_key,value) values('fina2.email.pwd','javamailjavamail');
insert into sys_properties(prop_key,value) values('fina2.pop.protocol','pop.gmail.com');
insert into sys_properties(prop_key,value) values('fina2.smtp.protocol','smtp.gmail.com');

DELETE FROM SYS_PROPERTIES WHERE RTRIM(prop_key) IN('fina2.mail.reader.port','fina2.mail.sender.port');
insert into sys_properties(prop_key,value) values('fina2.mail.reader.port','995');
insert into sys_properties(prop_key,value) values('fina2.mail.sender.port','465');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY IN('fina2.update.addin','fina2.update.runBat','fina2.update.resources','fina2.update.finaUpdate');
INSERT INTO sys_properties(PROP_KEY,VALUE) VALUES('fina2.update.addin','0');
INSERT INTO sys_properties(PROP_KEY,VALUE) VALUES('fina2.update.runBat','0');
INSERT INTO sys_properties(PROP_KEY,VALUE) VALUES('fina2.update.resources','0');
INSERT INTO sys_properties(PROP_KEY,VALUE) VALUES('fina2.update.finaUpdate','0');

DELETE FROM SYS_PROPERTIES WHERE PROP_KEY IN('fina2.mail.last.read.date');
INSERT INTO SYS_PROPERTIES(prop_key,value) values('fina2.mail.last.read.date',null);

ALTER TABLE SYS_UPLOADEDFILE ADD matrixValid number(1);
ALTER TABLE SYS_UPLOADEDFILE ADD reason varchar2(255);
UPDATE SYS_UPLOADEDFILE set matrixValid=1;
CREATE INDEX PK_IN_IMPORTED_RETURNS_FILTER ON IN_IMPORTED_RETURNS(id,returnCode,bankCode,periodStart,periodEnd,importStart,importEnd,status,versionCode,uploadTime,type,xlsId);
commit;

delete from sys_permissions where rtrim(idname)='fina2.regCity.amend';
insert into sys_strings(id,langid,value) values((select max(id)+1 from sys_strings),1,'Region/City Manager');
insert into sys_permissions(id,namestrid,idname) values((select max(id)+1 from sys_permissions),(select max(id) from sys_strings),'fina2.regCity.amend');
commit;

DECLARE sys_perm_id integer; sys_namestrid integer; 
BEGIN 
  select max(id) into sys_perm_id from sys_permissions;
  select max(id) into sys_namestrid from sys_strings;
  insert into sys_permissions(id,namestrid,idname) values(sys_perm_id+1,sys_namestrid+1,'fina2.web.internal.user');
  insert into sys_strings(id,langid,value) values(sys_namestrid+1,1,'Internal User');
  insert into sys_permissions(id,namestrid,idname) values(sys_perm_id+2,sys_namestrid+2,'fina2.web.external.user');
  insert into sys_strings(id,langid,value) values(sys_namestrid+2,1,'External User'); 
  commit;
END;
