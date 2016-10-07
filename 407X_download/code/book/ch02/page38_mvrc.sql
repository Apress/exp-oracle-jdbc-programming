spool mvrc
set echo on
drop table t1;
create table t1
(
  x number
);

insert into t1 values ( 1 );
commit;
create or replace procedure p ( p_cursor out sys_refcursor ) is
begin
  open p_cursor for 
  select * from t1;
  insert into t1 values ( 5 );
  insert into t1 values ( 2 );
  insert into t1 values ( 3 );
  insert into t1 values ( 4 );
  commit;
end;
/
show errors;
variable c  refcursor;
exec p( :c )
print c;
spool off
