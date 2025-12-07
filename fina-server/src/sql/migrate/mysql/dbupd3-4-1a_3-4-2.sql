/*==============================================================*/
/* Table: IN_IMPORTED_RETURNS                                   */
/*==============================================================*/
CREATE TABLE IN_IMPORTED_RETURNS
(
   ID                   NUMBER(10) NOT NULL,
   RETURNCODE           VARCHAR(12),
   BANKCODE             VARCHAR(12),
   VERSIONCODE          VARCHAR(12),
   PERIODSTART          DATE,
   PERIODEND            DATE,
   USERID               NUMBER(10),
   LANGID               NUMBER(10),
   UPLOADTIME           DATE,
   IMPORTSTART          DATE,
   IMPORTEND            DATE,
   STATUS               NUMBER(1),
   CONTENT              BLOB,
   MESSAGE              VARCHAR(4000)
);

ALTER TABLE IN_IMPORTED_RETURNS
   ADD PRIMARY KEY (ID);
