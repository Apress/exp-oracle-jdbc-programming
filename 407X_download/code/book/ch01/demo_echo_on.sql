spool demo_echo_on
set echo on
drop table t1;
create table t1
(
  x number,
  y varchar2(30),
  z date
);
spool off
