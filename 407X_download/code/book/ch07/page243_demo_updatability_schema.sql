spool demo_updatability_schema
set echo on
drop table t1;
create table t1 as
select rownum as x
from all_objects
where rownum <= 30;
select count(*) from t1;
commit;
spool off
