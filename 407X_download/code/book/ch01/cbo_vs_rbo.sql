spool cbo_vs_rbo
set echo on
set doc off
drop table t;
create table t ( x number );
insert into t select mod(rownum, 2 ) from all_objects, all_users where rownum <= 20000;
select count(*) from t where x = 0;
select count(*) from t where x = 1;
create index t_idx on t(x);
commit;
drop table t1;
create table t1 ( x number );
insert into t1 select mod(rownum, 2 ) from all_objects, all_users where rownum <= 20000;
select count(*) from t1 where x = 0;
select count(*) from t1 where x = 1;
create index t1_idx on t1(x);
commit;
begin
  dbms_stats.gather_table_stats( 
    ownname => 'BENCHMARK',
    tabname => 'T',
    cascade => true );
  dbms_stats.gather_table_stats( 
    ownname => 'BENCHMARK',
    tabname => 'T1',
    cascade => true );
end;
/
select count(*)
from t1, t
where t1.x = t.x
and t1.x = 0;
select /*+ RULE */count(*)
from t1, t
where t1.x = t.x
and t1.x = 0;
exec runstats_pkg.rs_start;
select count(*)
from t1, t
where t1.x = t.x
and t1.x = 0;
exec runstats_pkg.rs_middle;
select /*+ RULE */count(*)
from t1, t
where t1.x = t.x
and t1.x = 0;
exec runstats_pkg.rs_stop;
spool off
