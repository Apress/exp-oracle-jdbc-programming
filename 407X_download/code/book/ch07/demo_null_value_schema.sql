spool null_schema
set echo on
drop table t1;
create table t1 ( x number );
--insert into t1( x ) values ( null );
spool off
