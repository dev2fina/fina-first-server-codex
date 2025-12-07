
/* Without executing this script user wont be able to have returns statuses permission*/
update SYS_PERMISSIONS set IDNAME='fina2.returns.statuses' where IDNAME='fina2.return.statuses';

delete from sys_strings where value='Web Portal Admin' or value='Web Portal User';
/*===================================================*/
/*adding some new rows to SYS_STRINGS Table*/
/*===================================================*/
/*insert into sys_strings values(62,5,'Web Portal Admin');*/
/*insert into sys_strings values(62,1,'Web Portal Admin');*/
/*insert into sys_strings values(63,5,'Web Portal User');*/
/*insert into sys_strings values(63,1,'Web Portal User');*/

insert into sys_strings values((select max(id)+1 from sys_strings),1,'Web Portal Admin');
insert into sys_strings values((select max(id) from sys_strings),2,'Web Portal Admin');
insert into sys_strings values((select max(id) from sys_strings),3,'Web Portal Admin');
insert into sys_strings values((select max(id) from sys_strings),4,'Web Portal Admin');
insert into sys_strings values((select max(id) from sys_strings),5,'Web Portal Admin');

insert into sys_strings values((select max(id)+1 from sys_strings),1,'Web Portal User');
insert into sys_strings values((select max(id) from sys_strings),2,'Web Portal User');
insert into sys_strings values((select max(id) from sys_strings),3,'Web Portal User');
insert into sys_strings values((select max(id) from sys_strings),4,'Web Portal User');
insert into sys_strings values((select max(id) from sys_strings),5,'Web Portal User');



delete from sys_properties where prop_key='fina2.xml.folder.location';
insert into sys_properties values('fina2.xml.folder.location','c:\\fina-server\\xmls\\');

/*===================================================*/
/* SYS_UPLOADED_RETURNS Table*/
/*===================================================*/
create table SYS_UPLOADED_RETURNS(uploadedFileId int not null,importedReturnsId int not null);

/*===================================================*/
/*SYS_UPLOADEDFILE Table*/
/*===================================================*/
create table SYS_UPLOADEDFILE(id int primary key not null,bankCode varchar(20),username number(18),fileName varchar(50),status varchar(20),uploadedTime timestamp,uploadedFile LONG RAW);


/*===================================================*/
/*adding some new permissions to SYS_PERMISSIONS Table*/
/*===================================================*/
delete from sys_permissions where idname='fina2.web.admin' or idname='fina2.web.user';
insert into sys_permissions values((select count(id)+1 from sys_permissions),(select max(id)-1 from sys_strings),'fina2.web.admin');
insert into sys_permissions values((select count(id)+1 from sys_permissions),(select max(id) from sys_strings),'fina2.web.user');


/*insert into sys_properties values('fina2.converter.address','http://127.0.0.1:8080/TestService/ConverterServiceImpl?wsdl');*/
/*===================================================*/
/*deleting some unused data from SYS_PROPERTIES Table*/
/*===================================================*/
delete from sys_properties where prop_key='fina2.converter.address';
/*==================================================*/
/*droping indexes*/
/*On oracle some indexes have different names*/
/*==================================================*/

drop index RETURN_ITEMS_ID;
drop index RETURN_ITEMS_NODEID;
drop index RETURN_ITEMS_RETURNID;
drop index RETURN_ITEMS_VERID;
drop index RETURN_ITEM_ROWNUMBER;
drop index RETURN_ITEM_TABLEID;
drop index RETURN_ITEMS_RETID_VERID;
drop index RETURN_COMPLEX2;


/*creating new index for processing performance*/
create index RETURN_COMPLEX2 on IN_RETURN_ITEMS (RETURNID, NODEID,VERSIONID,TABLEID,ROWNUMBER)
  tablespace SYSTEM
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 7M
    minextents 1
    maxextents unlimited
  );
  
insert into sys_properties values('fina2.mfb.xls.name.pattern','MFB[0-9]{5}m(01|02|03|04|05|06|07|08|09|10|11|12)[1-9][0-9][0-9][0-9].xls');
insert into sys_properties values('fina2.sheet.protection.password','-13753');

commit;