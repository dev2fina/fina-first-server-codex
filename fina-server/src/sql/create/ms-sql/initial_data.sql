insert into sys_languages (id, code, name, dateformat, numberformat, fontface, fontsize, htmlcharset, xmlencoding) values (1, 'en_US', 'English', 'dd/MM/yyyy', '#0.00', 'Arial', 12, 'ASCII', 'ASCII') 
go

insert into sys_strings (id, langid, value) values (1, 1, 'admin')
go

insert into sys_strings (id, langid, value) values (2, 1, 'System Administrator')
go

insert into sys_strings (id, langid, value) values (3, 1, 'File')
go

insert into sys_strings (id, langid, value) values (4, 1, 'Menu Tree')
go

insert into sys_strings (id, langid, value) values (5, 1, 'Exit')
go

insert into sys_strings (id, langid, value) values (6, 1, 'Languages')
go

insert into sys_strings (id, langid, value) values (7, 1, 'User Manager')
go

insert into sys_strings (id, langid, value) values (8, 1, 'Banks')
go

insert into sys_strings (id, langid, value) values (9, 1, 'License Types')
go

insert into sys_strings (id, langid, value) values (10, 1, 'Bank Types')
go

insert into sys_strings (id, langid, value) values (11, 1, 'Peer Groups')
go

insert into sys_strings (id, langid, value) values (12, 1, 'Bank List')
go

insert into sys_strings (id, langid, value) values (13, 1, 'Meta Data')
go

insert into sys_strings (id, langid, value) values (14, 1, 'Meta Data Tree')
go

insert into sys_strings (id, langid, value) values (15, 1, 'Comparison Rules')
go

insert into sys_strings (id, langid, value) values (16, 1, 'Period Types')
go

insert into sys_strings (id, langid, value) values (17, 1, 'Period Definition')
go

insert into sys_strings (id, langid, value) values (18, 1, 'Period Auto Insert')
go

insert into sys_strings (id, langid, value) values (19, 1, 'Return Types')
go

insert into sys_strings (id, langid, value) values (20, 1, 'Return Definition')
go

insert into sys_strings (id, langid, value) values (21, 1, 'Schedule Definition')
go

insert into sys_strings (id, langid, value) values (22, 1, 'Schedule Auto Insert')
go

insert into sys_strings (id, langid, value) values (23, 1, 'Processing')
go

insert into sys_strings (id, langid, value) values (24, 1, 'Return Manager')
go

insert into sys_strings (id, langid, value) values (25, 1, 'Import')
go

insert into sys_strings (id, langid, value) values (26, 1, 'File Robot')
go

insert into sys_strings (id, langid, value) values (27, 1, 'Returns Statuses')
go

insert into sys_strings (id, langid, value) values (28, 1, 'Reporting')
go

insert into sys_strings (id, langid, value) values (29, 1, 'Report Manager')
go

insert into sys_strings (id, langid, value) values (30, 1, 'Metadata Tree Amend')
go

insert into sys_strings (id, langid, value) values (31, 1, 'Metadata Tree Delete')
go

insert into sys_strings (id, langid, value) values (32, 1, 'Metadata Tree Review')
go

insert into sys_strings (id, langid, value) values (33, 1, 'Banks Amend')
go

insert into sys_strings (id, langid, value) values (34, 1, 'Banks Delete')
go

insert into sys_strings (id, langid, value) values (35, 1, 'Banks Review')
go

insert into sys_strings (id, langid, value) values (36, 1, 'Return Definitions Amend')
go

insert into sys_strings (id, langid, value) values (37, 1, 'Return Definitions Delete')
go

insert into sys_strings (id, langid, value) values (38, 1, 'Return Definitions Review')
go

insert into sys_strings (id, langid, value) values (39, 1, 'Return Definitions Format')
go

insert into sys_strings (id, langid, value) values (40, 1, 'Menu Amend')
go

insert into sys_strings (id, langid, value) values (41, 1, 'Users Amend')
go

insert into sys_strings (id, langid, value) values (42, 1, 'Schedules Amend')
go

insert into sys_strings (id, langid, value) values (43, 1, 'Schedules Delete')
go

insert into sys_strings (id, langid, value) values (44, 1, 'Schedules Review')
go

insert into sys_strings (id, langid, value) values (45, 1, 'Returns Amend')
go

insert into sys_strings (id, langid, value) values (46, 1, 'Returns Delete')
go

insert into sys_strings (id, langid, value) values (47, 1, 'Returns Review')
go

insert into sys_strings (id, langid, value) values (48, 1, 'Returns Process')
go

insert into sys_strings (id, langid, value) values (49, 1, 'Returns Accept')
go

insert into sys_strings (id, langid, value) values (50, 1, 'Returns Reset')
go

insert into sys_strings (id, langid, value) values (51, 1, 'Returns Reject')
go

insert into sys_strings (id, langid, value) values (52, 1, 'Periods Amend')
go

insert into sys_strings (id, langid, value) values (53, 1, 'Periods Delete')
go

insert into sys_strings (id, langid, value) values (54, 1, 'Periods Review')
go

insert into sys_strings (id, langid, value) values (55, 1, 'Reports Amend')
go

insert into sys_strings (id, langid, value) values (56, 1, 'Reports Generate')
go

insert into sys_strings (id, langid, value) values (57, 1, 'Return Statuses')
go

insert into sys_strings (id, langid, value) values (58, 1, 'Report Scheduler')
go

insert into sys_strings (id, langid, value) values (59, 1, 'Reports Scheduler Manager') 
go

insert into sys_strings (id, langid, value) values (60, 1, 'Reports Schedules Add')
go

insert into sys_strings (id, langid, value) values (61, 1, 'Stored Reports Manager')
go

insert into sys_users (id, login, password, namestrid, titlestrid, phone, email) values (1, 'sa', 'b8c6f827a3f7a6f15d5f971920f79dd20d712889',1 ,2, '<admin phone>', '<admin email>')
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (1, 0, 3, 0, '', '', 0)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (2, 1, 4, 1, 'fina2.actions.menuAmend', '', 1)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (3, 1, 5, 1, 'fina2.actions.exit', '', 4)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (4, 1, 6, 1, 'fina2.actions.languages', '', 3)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (5, 1, 7, 1, 'fina2.actions.users', '', 2)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (6, 0, 8, 0, '', '', 1)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (7, 6, 9, 1, 'fina2.actions.licences', '', 1)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (8, 6, 10, 1, 'fina2.actions.bankTypes', '', 2)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (9, 6, 11, 1, 'fina2.actions.bankGroups', '', 3)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (10, 6, 12, 1, 'fina2.actions.banks', '', 4)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (11, 0, 13, 0, '', '', 2)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (12, 11, 14, 1, 'fina2.actions.MDTAmend', '', 1)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (13, 11, 15, 1, 'fina2.actions.metadataComparisons', '', 2)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (14, 11, 16, 1, 'fina2.actions.periodTypes', '', 3)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (15, 11, 17, 1, 'fina2.actions.periods', '', 4)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (16, 11, 18, 1, 'fina2.period.periodAutoInsert', '', 5)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (17, 11, 19, 1, 'fina2.actions.returnTypes', '', 6)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (18, 11, 20, 1, 'fina2.actions.returnDefinitions', '', 7)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (19, 11, 21, 1, 'fina2.actions.schedules', '', 8)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (20, 11, 22, 1, 'fina2.returns.autoSchedule', '', 9)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (21, 0, 23, 0, '', '', 3)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (22, 21, 24, 1, 'fina2.actions.returnManager', '', 1)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (23, 21, 25, 1, 'fina2.actions.import', '', 2)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (24, 21, 26, 1, 'fina2.actions.fileRobot', '', 3)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (25, 21, 27, 1, 'fina2.actions.returnsStatuses', '', 4)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (26, 0, 28, 0, '', '', 4)
go

insert into sys_menus (id, parentid, namestrid, type, actionkey, application, sequence) values (27, 26, 29, 1, 'fina2.actions.reportManager', '', 1)
go

insert into sys_permissions (id, namestrid, idname) values (1, 30, 'fina2.metadata.amend')
go

insert into sys_permissions (id, namestrid, idname) values (2, 31, 'fina2.metadata.delete')
go

insert into sys_permissions (id, namestrid, idname) values (3, 32, 'fina2.metadata.review')
go

insert into sys_permissions (id, namestrid, idname) values (4, 33, 'fina2.bank.amend')
go

insert into sys_permissions (id, namestrid, idname) values (5, 34, 'fina2.bank.delete')
go

insert into sys_permissions (id, namestrid, idname) values (6, 35, 'fina2.bank.review')
go

insert into sys_permissions (id, namestrid, idname) values (7, 36, 'fina2.returns.definition.amend')
go

insert into sys_permissions (id, namestrid, idname) values (8, 37, 'fina2.returns.definition.delete')
go

insert into sys_permissions (id, namestrid, idname) values (9, 38, 'fina2.returns.definition.review')
go

insert into sys_permissions (id, namestrid, idname) values (10, 39, 'fina2.returns.definition.format')
go

insert into sys_permissions (id, namestrid, idname) values (11, 40, 'fina2.menu.amend')
go

insert into sys_permissions (id, namestrid, idname) values (12, 41, 'fina2.security.amend')
go

insert into sys_permissions (id, namestrid, idname) values (13, 42, 'fina2.returns.schedule.amend')
go

insert into sys_permissions (id, namestrid, idname) values (14, 43, 'fina2.returns.schedule.delete')
go

insert into sys_permissions (id, namestrid, idname) values (15, 44, 'fina2.returns.schedule.review')
go

insert into sys_permissions (id, namestrid, idname) values (16, 45, 'fina2.returns.amend')
go

insert into sys_permissions (id, namestrid, idname) values (17, 46, 'fina2.returns.delete')
go

insert into sys_permissions (id, namestrid, idname) values (18, 47, 'fina2.returns.review')
go

insert into sys_permissions (id, namestrid, idname) values (19, 48, 'fina2.returns.process')
go

insert into sys_permissions (id, namestrid, idname) values (20, 49, 'fina2.returns.accept')
go

insert into sys_permissions (id, namestrid, idname) values (21, 50, 'fina2.returns.reset')
go

insert into sys_permissions (id, namestrid, idname) values (22, 51, 'fina2.returns.reject')
go

insert into sys_permissions (id, namestrid, idname) values (23, 52, 'fina2.periods.amend')
go

insert into sys_permissions (id, namestrid, idname) values (24, 53, 'fina2.periods.delete')
go

insert into sys_permissions (id, namestrid, idname) values (25, 54, 'fina2.periods.review')
go

insert into sys_permissions (id, namestrid, idname) values (26, 55, 'fina2.report.amend')
go

insert into sys_permissions (id, namestrid, idname) values (27, 56, 'fina2.report.generate')
go

insert into sys_permissions (id, namestrid, idname) values (28, 57, 'fina2.returns.statuses')
go

insert into sys_permissions (id, namestrid, idname) values (29, 58, 'fina2.report.scheduler')
go

insert into sys_permissions (id, namestrid, idname) values (30, 59, 'fina2.reports.scheduler.manager')
go

insert into sys_permissions (id, namestrid, idname) values (31, 60, 'fina2.reports.scheduler.add')
go

insert into sys_permissions (id, namestrid, idname) values (32, 61, 'fina2.reports.stored.manager')
go

insert into sys_properties (prop_key, value) values ('fina2.security.allowedNumberLoginAttempt', '3')
go

insert into sys_properties (prop_key, value) values ('fina2.security.allowedAccountInactivityPerioed', '60')
go

insert into sys_properties (prop_key, value) values ('fina2.security.numberOfStoredOldPasswords', '13')
go

insert into sys_properties (prop_key, value) values ('fina2.security.passwordMinimalLen', '8')
go

insert into sys_properties (prop_key, value) values ('fina2.security.passwordWithNumsChars', '1')
go

insert into sys_properties (prop_key, value) values ('fina2.security.passwordValidityPeriod', '90')
go

insert into SYS_PROPERTIES (prop_key, value) values ('fina2.database.schemaVersion', '3.4.1')
go

insert into SYS_PROPERTIES (prop_key, value) values ('fina2.gui.version', '3.4.2c')
go