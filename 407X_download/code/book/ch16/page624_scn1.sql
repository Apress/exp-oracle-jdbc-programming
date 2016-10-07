spool scn1
set head on
set echo on
drop table t1;
create table t1 ( x number ) rowdependencies;
insert into t1 values ( 1 );
insert into t1 values ( 2 );
insert into t1 values ( 22);
commit;
select x, ora_rowscn from t1;
update t1 set x = 3 where x =22;
commit;
select x, ora_rowscn, dbms_rowid.rowid_block_number( rowid ) block_number from t1;
spool off
