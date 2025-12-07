/*
 Database: SQL Server 2012

 Author: Nick Gochiashvili
 E: nick@fina2.net
 Version: 1.0
 Date : 31/08/2015
*/

/*
  Update DB Version
*/
UPDATE sys_properties
SET value = '5.1.6'
WHERE prop_key = 'fina2.database.schemaVersion';

/*
	Create queue files table
 */
CREATE TABLE IN_UPLOADFILE_QUEUE (
  [user_id]   [numeric](18, 0) NOT NULL,
  [file_id]   [numeric](18, 0) NOT NULL,
  [status]    [int]            NOT NULL,
  [file_name] [nvarchar](256)  NOT NULL,
  CONSTRAINT PK_IN_UPLOADFILE_QUEUE PRIMARY KEY
    (
      [file_id] ASC
    )
)


/*
 Return id sequence
 */
declare @rs int
set @rs=(select max(id)+1 from IN_RETURNS) ;
exec('CREATE SEQUENCE return_sequence  START WITH ' + @rs +'   INCREMENT BY 1;')

/*
  Return  status sequence
 */

 declare @rss int
set @rss=(select max(id)+1 from IN_RETURN_STATUSES) ;
exec('CREATE SEQUENCE return_status_sequence  START WITH ' + @rss +'   INCREMENT BY 1;')


