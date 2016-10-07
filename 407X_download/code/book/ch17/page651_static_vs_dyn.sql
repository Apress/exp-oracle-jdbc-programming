spool static_vs_dyn
set echo on
delete t1;
insert into t1 select rownum from all_objects where rownum <= 30;
commit;
--alter session  set timed_statistics=true;
--alter session set events '10046 trace name context forever, level 12';
--static sql
create or replace procedure static_proc( p_count in out number )
is
begin
  select count(*) 
  into p_count
  from t1 static;
end;
/
show errors;
--dynamic sql
create or replace procedure dynamic_proc( p_count in out number )
is
begin
  execute immediate 'select count(*) from t1 dynamic ' into p_count;
end;
/
declare
l_count number;
begin
  begin
    runstats_pkg.rs_start;
  end;
  for i in 1..1000
  loop
    static_proc( l_count );
  end loop;
  begin
    runstats_pkg.rs_middle;
  end;
  for i in 1..1000
  loop
    dynamic_proc( l_count );
  end loop;
  begin
    runstats_pkg.rs_stop( 50 );
  end;
end;
/
show errors;
spool off
