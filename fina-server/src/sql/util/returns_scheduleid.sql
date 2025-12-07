-- DBMS name: SQL Server 2005 and higher                                             
-- Created on: 16/01/2014
-- Version: 1.0          
-- Author: Nick Gochiashvili (nick@fina2.net)                                                   

/****** Object:  Index [returns_scheduleid]    Script Date: 1/19/2014 2:44:33 PM ******/
CREATE NONCLUSTERED INDEX [returns_scheduleid] ON [dbo].[IN_RETURNS]
(
	[SCHEDULEID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO


