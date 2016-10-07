spool demo_pagination_schema
set echo on
create table t2 as
select rownum as x, sysdate+rownum as y
from all_objects;
commit;
desc t2
select count(*) from t2;
spool off
