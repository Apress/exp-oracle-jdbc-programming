spool demo_index_access_vs_full_table_scan
set echo on
set doc off
-- full scan is not evil
drop table t;
create table t as select object_name as x, mod( rownum, 2) as y, owner as z from all_objects where rownum <= 5000;
create index t_idx on t(y);
drop table t1;
create table t1 as select object_name as x, mod( rownum, 2) as y, owner as z from all_objects where rownum <= 5000;
commit;
begin
  dbms_stats.gather_table_stats( 
    ownname => 'BENCHMARK',
    tabname => 'T' );
  dbms_stats.gather_index_stats( 
    ownname => 'BENCHMARK',
    indname => 'T_IDX' );
  dbms_stats.gather_table_stats( 
    ownname => 'BENCHMARK',
    tabname => 'T1' );
end;
/
set autotrace traceonly explain;
select *
from t, t1
where t.y = t1.y
  and t.y = 0;
select /*+ RULE*/ *
from t, t1
where t.y = t1.y
  and t.y = 0;
set autotrace traceonly ;
set timing on
select *
from t, t1
where t.y = t1.y
  and t.y = 0;
select /*+ RULE*/*
from t, t1
where t.y = t1.y
  and t.y = 0;
set timing off
spool off
