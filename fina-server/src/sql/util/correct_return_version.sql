/*
 Database: SQL Server 2008/2012
 
 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 22/01/2014
*/
--Correct return items

--TODO
--update IN_RETURNS set VERSION=VERSIONID;

DECLARE db_cursor CURSOR FOR select distinct r.id from IN_RETURN_ITEMS ri,IN_RETURNS r where r.ID=ri.RETURNID and ri.VERSIONID<>r.VERSIONID;
DECLARE @returnId decimal(10);
OPEN db_cursor;
FETCH NEXT FROM db_cursor INTO @returnId;
WHILE @@FETCH_STATUS = 0  
BEGIN  

declare @id decimal(10);
declare @scheduleId decimal(10);
declare @versionId decimal(10);
declare @latestVersionId decimal(10);

set @id=(select max(id)+1 from IN_RETURNS);


select @scheduleId=r.scheduleId, @versionId=r.versionId from IN_RETURNS r where id=@returnId;
select @latestVersionId=ri.VERSIONID from IN_RETURN_ITEMS ri where RETURNID=@returnId and VERSIONID<>@versionId;
insert into IN_RETURNS (id,SCHEDULEID,VERSION,versionId) values(@id,@scheduleId,@versionId,@latestVersionId);
update IN_RETURN_ITEMS set RETURNID=@id where RETURNID=@returnId and VERSIONID=@latestVersionId;
update IN_RETURN_STATUSES set RETURNID=@id where RETURNID=@returnId and VERSIONID=@latestVersionId;

FETCH NEXT FROM db_cursor INTO @returnId;
END;
CLOSE db_cursor;
DEALLOCATE db_cursor;