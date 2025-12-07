DECLARE @IMNid int 
DECLARE @validationID int, @nodeCODE Nvarchar,@Requation Nvarchar(max);
set @validationID= (select max(id) from IN_MDT_COMPARISON)
DECLARE cursorT CURSOR
FOR
 select id from in_mdt_nodes  where type=3 --add other conditions if any
OPEN cursorT 
FETCH NEXT FROM cursorT INTO @IMNid
WHILE @@FETCH_STATUS = 0
BEGIN         
		  set @Requation=(select equation from in_mdt_nodes where id=@IMNid)
		  set @validationID=@validationID+1;
		  insert into IN_MDT_COMPARISON values (@validationID,@IMNid,'1',@Requation,0,'','','')
		  FETCH NEXT FROM cursorT INTO @IMNid
END
CLOSE cursorT 
DEALLOCATE cursorT 


-- update formulas to inputs 

 update  in_mdt_nodes set type = 2, EQUATION='' where type=3 --add (same) other conditions if any