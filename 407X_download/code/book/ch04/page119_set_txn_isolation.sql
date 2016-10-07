spool set_txn_isolation
set echo on
set transaction isolation level read committed;
set transaction isolation level serializable;
rollback;
set transaction isolation level serializable;
rollback;
set transaction read only;
rollback;
begin
  set transaction read only;
  rollback;
end;
/
--exec dbms_transaction.read_only;
alter session set isolation_level=serializable;
rollback;
alter session set isolation_level=read committed;
begin
  execute immediate 'alter session set isolation_level=serializable';
end;
/
rollback;
spool off
