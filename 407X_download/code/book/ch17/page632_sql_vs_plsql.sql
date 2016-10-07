spool sql_vs_plsql
set echo on
set head on
set serveroutput on
--schema
drop table t1;
drop table t2;
drop table t3;
create table t1 ( x number primary key);
create table t2 ( x number primary key);
create table t3 ( x number primary key);
delete t1;
delete t2;
delete t3;
insert into t1 select rownum from all_objects, all_users where rownum <= 500000;
select count(*) from t1;
begin
  dbms_stats.gather_table_stats( 
    ownname => 'BENCHMARK',
    tabname => 'T1',
    cascade => true );
end;
/
commit;
exec runstats_pkg.rs_start;
-- sql solution
insert into t3
select x
from t1;
commit;
exec runstats_pkg.rs_middle;
-- pl/sql solution
declare
begin
  for i in ( select x from t1 )
  loop
    insert into t2 values ( i.x );
  end loop;
end;
/
commit;
exec runstats_pkg.rs_stop( 50 );
spool off
