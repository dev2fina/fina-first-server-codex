/*
 Database: Oracle 11g 
 
 Author: Nick Gochiashvili
 E: nick@fina2.net
 Author: Beso Gagua
 E: dba@fina2.net

 Version: 0.1
 Date : 17/09/2014
*/


/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '4.4.1'
WHERE prop_key = 'fina2.database.schemaVersion';

-- Create IN_IMPORTED_QUEUE table
CREATE TABLE IN_IMPORTED_QUEUE
(
  id     NUMBER(10) NOT NULL,
  status NUMBER(10)
);
ALTER TABLE IN_IMPORTED_QUEUE
ADD PRIMARY KEY (ID);

--Drop constraint PK_AUDIT_TRAIL
ALTER TABLE sys_audit_log
DROP CONSTRAINT PK_AUDIT_TRAIL;

--Drop index PK_AUDIT_TRAIL  
DROP INDEX PK_AUDIT_TRAIL;

--add properties with default values
DELETE FROM SYS_PROPERTIES
WHERE PROP_KEY IN
      ('fina2.mfb.uploaded.file.unique.status', 'fina.auditlog.lock', 'fina2.max.schedules', 'net.fina.converter.vct.emptyLines', 'net.fina.dcs.uploadFile.dueDate.enable','net.fina.dcs.uploadFile.date.enable');
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina.auditlog.lock', '-1');
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE)
VALUES ('fina2.mfb.uploaded.file.unique.status', '0,2,9,10,11,12,13,14,15,16,17,20,21');
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('fina2.max.schedules', '60000');
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('net.fina.converter.vct.emptyLines', '');
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('net.fina.dcs.uploadFile.dueDate.enable', '1');
INSERT INTO SYS_PROPERTIES (PROP_KEY, VALUE) VALUES ('net.fina.dcs.uploadFile.date.enable', '-1');
COMMIT;


/*
  Solution for following problem: 
    SELECT
      id
    FROM in_imported_returns
    WHERE xlsid = :1 AND ((message NOT LIKE :2 ) AND (message NOT LIKE :3 ))
 */
CREATE INDEX IN_IMPORTED_RETURNS_I1 ON IN_IMPORTED_RETURNS (xlsid) TABLESPACE TS_FINA;


/*
  Solution for following problem: 
    SELECT
      i.id,
      i.userId,
      i.langId,
      i.versionCode,
      i.content,
      i.status,
      i.bankCode
    FROM IN_IMPORTED_RETURNS i
    WHERE i.status = 2
    ORDER BY i.importEnd;
    
    SELECT
      i.id,
      i.userId,
      i.langId,
      i.versionCode,
      i.content,
      i.status,
      i.bankCode
    FROM IN_IMPORTED_RETURNS i
    WHERE i.status = 0
    ORDER BY i.importStart;
*/
CREATE INDEX IN_IMPORTED_RETURNS_I2 ON IN_IMPORTED_RETURNS (status) TABLESPACE TS_FINA;

/*
  Solution for following problem: 
    testis dros gamovlinda efassfin.PK_IN_IMPORTED_RETURNS_FILTER - indeqsis araefeqturoba
    (es iko shedgenili indeqsi daaxloebit 7 velze - me samcuxarod ar maqvs chanishnuli ra velebi monacileobda
    am indeqssshi, ratis an nikas sheezlebat naxva sxva satesto garemoze - sadac ar cagvishlia es indeqsi),
    agretve agmovachinet ramdenime indeqsis sachiroeba
*/
DROP INDEX PK_IN_IMPORTED_RETURNS_FILTER;
CREATE INDEX IN_IMPORTED_RETURNS_I3 ON IN_IMPORTED_RETURNS (bankCode, importStart) TABLESPACE TS_FINA;
CREATE INDEX IN_IMPORTED_RETURNS_I4 ON IN_IMPORTED_RETURNS (bankCode, importEnd) TABLESPACE TS_FINA;
CREATE INDEX IN_IMPORTED_RETURNS_I5 ON IN_IMPORTED_RETURNS (bankCode, UploadTime) TABLESPACE TS_FINA;
CREATE INDEX IN_MDT_NODES_I1 ON IN_MDT_NODES (equation) TABLESPACE TS_FINA;

-- Drop Triggers
DROP TRIGGER RETURNSSAFEIDTRIGGER;
DROP TRIGGER RETURNITEMSSAFEIDTRIGGER;
DROP TRIGGER INRETURNSTATUSESSAFEIDTRIGGER;
DROP TRIGGER INIMPORTEDRETURNSSAFEIDTRIGGER;

COMMIT;

-- Create IN_IMPORTED_QUEUE table
CREATE TABLE IN_IMPORTED_QUEUE
(
  ID     NUMBER(10) NOT NULL,
  STATUS NUMBER(10)
);
ALTER TABLE IN_IMPORTED_QUEUE
ADD PRIMARY KEY (ID);

-- Create IN_TASKS table
CREATE TABLE IN_TASKS
(
  ID                NUMBER(19) NOT NULL,
  ACTION            NUMBER(10),
  ACTION_PARAMETERS VARCHAR2(255 CHAR)
);
ALTER TABLE IN_TASKS ADD PRIMARY KEY (ID);
COMMIT;


ALTER TABLE IN_COUNTRY_DATA 
ADD (CODE_1 NVARCHAR2(40) );

ALTER TABLE IN_COUNTRY_DATA_AUD
ADD (CODE_1 NVARCHAR2(40) );
COMMIT;


