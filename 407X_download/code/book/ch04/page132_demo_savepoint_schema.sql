spool demo_savepoint_schema
drop table transaction_log;
create table transaction_log
(
  txn_name varchar2(15),
  log_message varchar2(500)
);

drop table t1;
create table t1
(
  x number primary key
);
spool off
