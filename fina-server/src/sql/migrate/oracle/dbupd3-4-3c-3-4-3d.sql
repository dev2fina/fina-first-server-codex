

DROP TABLE SYS_UPLOADEDFILE;
CREATE TABLE SYS_UPLOADEDFILE(
    id number(10) NOT NULL PRIMARY KEY,
    bankCode varchar(20) NULL,
    username varchar(20) NULL,
    fileName varchar(50) NULL,
    status varchar(20) NULL,
    uploadedTime timestamp NULL,
    uploadedFile blob NULL,
    nameValid number(1) ,
    versionvalid number(1) ,
    protectioninfo nvarchar2(255) NULL,
    passwordInfo nvarchar2(255) NULL,
    hasUserBank number(1) NULL);



insert into SYS_PROPERTIES(prop_key,value) values('fina2.regionstructuretree.maxlevel','3');
insert into SYS_PROPERTIES(prop_key) values('fina2.regionstructuretree.levelname');
update SYS_PROPERTIES set VALUE='' where prop_key='fina2.regionstructuretree.levelname';

ALTER TABLE IN_BANKS ADD REGIONID decimal(18,0);


CREATE TABLE IN_COUNTRY_DATA(
    id decimal(18, 0) NOT NULL primary key,
    code nvarchar2(40) NULL,
    namestrid decimal(18, 0) NULL,
    parentid decimal(18, 0) NULL,
    sequence decimal(18, 0) NULL);
    

commit;



