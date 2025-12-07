--Return item value to nvalue converter

--update  IN_RETURN_ITEMS set value='' where value =(select value from IN_RETURN_ITEMS where RETURNID=24262 and nodeid=15927);
--update  IN_RETURN_ITEMS set value='' where value ='.';

Declare @id as decimal(10)
Declare @tableId as decimal(10)
Declare @nodeId as decimal(10)
Declare @rowNumber as decimal(10)
Declare @versionId as decimal(10)
Declare @value as nvarchar(255)
Declare @returnId as decimal(10)

Declare @nvalue as float

Declare @count as int

Declare convert_values CURSOR FOR

select ri.ID,ri.TABLEID,ri.NODEID,ri.ROWNUMBER,ri.VERSIONID,ri.VALUE, ri.RETURNID from in_return_items ri inner join  IN_MDT_NODES mn on ri.nodeId=mn.id where mn.DATATYPE=1 and ri.nvalue = 0 or ri.NVALUE is null
 
OPEN convert_values
    FETCH NEXT FROM convert_values INTO @id,@tableId,@nodeId,@rowNumber,@versionId,@value,@returnId 
        WHILE @@FETCH_STATUS = 0
        BEGIN

				print @returnid;
				print @nodeid;
				print @value;
				print '----------------';

				set @value=REPLACE(@value, ',', '');
				set @value=REPLACE(@value, '-', '');						
				set	@nvalue= CASE WHEN ISNUMERIC(@value)=1 THEN CAST(@value AS float)  ELSE 0 END;

				update IN_RETURN_ITEMS set NVALUE=@nvalue where id=@id and tableid=@tableId and NODEID=@nodeId and rowNumber=@rowNumber and VERSIONID=@versionId and RETURNID=@returnId;

        FETCH NEXT FROM convert_values INTO @id,@tableId,@nodeId,@rowNumber,@versionId,@value,@returnId 
        END
    CLOSE convert_values
DEALLOCATE convert_values