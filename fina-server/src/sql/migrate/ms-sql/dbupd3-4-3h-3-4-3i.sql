/*===============================================================================================*/
/* Author:                David Chokhonelidze                                                    */
/* DBMS name:             SQLServer 2005 and higher                                              */
/* Created on:            27/06/2011                                                             */
/* Revision History       n/a                                                                    */
/*                        David Chokhonelidze                                                    */   
/*===============================================================================================*/
delete from sys_properties where prop_key in('fina2.dcs.matrix.path','fina2.converted.xmls');
insert into sys_properties(prop_key,value) values('fina2.dcs.matrix.path','C:/fina-server');
insert into sys_properties(prop_key,value) values('fina2.converted.xmls','C:/fina-server/xmls');