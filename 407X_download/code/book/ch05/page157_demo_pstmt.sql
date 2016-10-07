set echo on
set verify off
spool demo_pstmt
drop table t1;
create table t1 ( x number primary key, y varchar2(20), z date );
insert into t1 values ( 1, 'string 1', sysdate+1 );
insert into t1 values ( 2, 'string 2', sysdate+2 );
insert into t1 values ( 3, 'string 3', sysdate+3 );
insert into t1 values ( 4, 'string 4', sysdate+4 );
commit;
spool off
