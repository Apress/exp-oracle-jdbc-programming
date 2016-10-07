spool exception_fts
set echo on
set serveroutput on
-- getting stack trace of exeception
create or replace procedure p3
is
  l_x number := 0;
begin
  dbms_output.put_line( 'p3' );
  l_x := 1/ l_x; -- divide by zero
end;
/
show errors;
create or replace procedure p2
is
begin
  dbms_output.put_line( 'p2' );
  p3;
end;
/
show errors;
-- if you dont handle exception, whole stack trace is preserved
create or replace procedure p1
is
begin
  dbms_output.put_line( 'p1' );
  p2;
end;
/
show errors;
exec p1

create or replace procedure p1
is
  l_error_backtrace long;
begin
  dbms_output.put_line( 'p1' );
  p2;
exception when others then
  l_error_backtrace := dbms_utility.format_call_stack;
  dbms_output.put_line( l_error_backtrace );
  raise;
end;
/
show errors;
exec p1;
spool off
