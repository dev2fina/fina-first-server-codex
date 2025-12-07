alter table SYS_USERS alter column PASSWORD varchar(40)
go

alter table SYS_USERS add CHANGEPASSWORD int
go

update SYS_USERS set PASSWORD = 'b8c6f827a3f7a6f15d5f971920f79dd20d712889'
go

update SYS_USERS set CHANGEPASSWORD = 1
go
