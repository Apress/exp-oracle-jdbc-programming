spool sc_demo
set echo on
set head on

/*
alter session set session_cached_cursors=10;

-- first create a simple table and insert
-- 10000 rows in it.
*/
drop table t1;
create table t1 as
select rownum as x
from all_objects 
where rownum <= 10000;
select count(*) from t1;

alter session set timed_statistics=true;
alter session set events '10046 trace name context forever, level 12';

alter session set session_cached_cursors=0;
exec runstats_pkg.rs_start

declare 
  l_cursor sys_refcursor;
begin
  for i in 1..10000 
  loop
    open l_cursor for
      select x 
      from t1 
      where x = i;
    close l_cursor;
  end loop;
end;
/
exec runstats_pkg.rs_middle
alter session set session_cached_cursors=500;
declare 
  l_cursor sys_refcursor;
begin
  for i in 1..10000 
  loop
    open l_cursor for
      select x 
      from t1 
      where x = i;
    close l_cursor;
  end loop;
end;
/
      
exec runstats_pkg.rs_stop(80)
spool off
