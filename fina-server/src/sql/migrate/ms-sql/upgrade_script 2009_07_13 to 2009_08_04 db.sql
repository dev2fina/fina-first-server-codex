/*Script is for mssqlserver*/
/* The script is used to upgrade fina2bcc_2009_07_13 database to fina2bcc_2009_08_04*/

insert into sys_strings values(62,5,'Web Portal Admin')
insert into sys_strings values(62,1,'Web Portal Admin')
insert into sys_strings values(63,5,'Web Portal User')
insert into sys_strings values(63,1,'Web Portal User')

create table SYS_UPLOADED_RETURNS(uploadedFileId int not null,importedReturnsId int not null)

drop table sys_uploadedfile

create table SYS_UPLOADEDFILE(id int primary key not null,bankCode varchar(20),username varchar(20),fileName varchar(50),status varchar(20),uploadedTime datetime,uploadedFile image)

insert into sys_permissions values(37,62,'fina2.web.admin')
insert into sys_permissions values(38,63,'fina2.web.user')

insert into sys_properties values('fina2.converter.address','http://127.0.0.1:8080/TestService/ConverterServiceImpl?wsdl')
