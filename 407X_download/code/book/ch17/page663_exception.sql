spool exception
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
-- p1 with raise
create or replace procedure p1
is
begin
  dbms_output.put_line( 'p1 (with "raise"' );
  p2;
exception when others then
  raise;
end;
/
show errors;
exec p1;

-- use raise_application_error
create or replace procedure p1
is
begin
  dbms_output.put_line( 'p1 (raise_application_error with false parameter)' );
  p2;
exception when others then
  raise_application_error( -20001, 'An exception occured' );
end;
/
show errors;
exec p1
-- use raise_application_error (with true parameter)
create or replace procedure p1
is
begin
  dbms_output.put_line( 'p1 (raise_application_error with true parameter)' );
  p2;
exception when others then
  raise_application_error( -20001, 'An exception occured', true );
end;
/
show errors;
exec p1
-- use dbms_trace
-- procedure for printing dbms_trace info
-- requires select on  plsql_trace_events and  plsql_trace_runnumber
create or replace procedure print_dbms_trace
is
  l_runid binary_integer;
begin
  select sys.plsql_trace_runnumber.currval
  into l_runid
  from dual;
  for x in ( select *
             from sys.plsql_trace_events
             where runid = l_runid
             and event_kind = 52
             order by event_seq DESC )
  loop
    dbms_output.put_line( 'Exception occured in source ' || 
      x.event_unit ||
      ' on line ' || x.event_line );
    exit;
  end loop;
end;
/
show errors;
create or replace procedure p1
is
begin
  dbms_output.put_line( 'p1 (using dbms_trace - trace_all_exceptions)' );
  dbms_trace.set_plsql_trace( dbms_trace.trace_all_exceptions );
  p2;
  dbms_trace.clear_plsql_trace;
exception when others then
  print_dbms_trace;
  raise;
end;
/
show errors;
--alter session set plsql_debug = true;
--alter procedure p1 compile debug;
--exec dbms_trace.set_plsql_trace( dbms_trace.trace_all_exceptions );
exec p1
--exec dbms_trace.clear_plsql_trace;

-- format_call_stack
create or replace procedure p1
is
  l_format_call_stack long;
begin
  dbms_output.put_line( 'p1' );
  p2;
exception when others then
  l_format_call_stack := dbms_utility.format_call_stack;
  dbms_output.put_line( l_format_call_stack );
  raise;
end;
/
show errors;
exec p1

-- 10G only solution using dbms_utility.format_error_backtrace
create or replace procedure p1
is
  l_error_backtrace long;
begin
  dbms_output.put_line( 'p1' );
  p2;
exception when others then
  l_error_backtrace := dbms_utility.format_error_backtrace;
  dbms_output.put_line( l_error_backtrace );
  raise;
end;
/
show errors;
exec p1;
spool off
