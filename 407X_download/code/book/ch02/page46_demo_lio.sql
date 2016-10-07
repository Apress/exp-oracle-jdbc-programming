spool demo_lio
set echo on
set doc off
set autotrace traceonly statistics
select count(*) 
from all_users;
set autotrace off
spool off
