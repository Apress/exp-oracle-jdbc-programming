spool schema
set echo on
drop table t1;
create table t1
(
  x number
);

create or replace procedure p2( p_x in number )
as
begin
  insert into t1 values( p_x );
end;
/
show errors;
spool off
