set echo on
spool std_schema
drop table t1;
create table t1
( 
  x number primary key
);
spool off
