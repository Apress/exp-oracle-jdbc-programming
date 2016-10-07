spool prefetch_schema
set echo on
drop table t1;
create table t1 as
select rownum as x from all_objects
union all
select rownum as x from all_objects;
commit;
select count(*) from t1;
spool off
