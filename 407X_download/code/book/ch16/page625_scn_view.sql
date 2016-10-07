spool scn_view
set head on
set echo on
create or replace view v1 as
select x
from t1;
select ora_rowscn from v1;
create or replace view v2 as
select x, ora_rowscn
from t1;
select ora_rowscn from v2;
spool off

