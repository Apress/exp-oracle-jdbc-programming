spool implicit_demo_func
set echo on
create or replace function f return sys_refcursor
as
  l_cursor sys_refcursor;
begin
  open l_cursor for
  select /*+ to be called using callable statement */ dummy from dual;
  return l_cursor;
end;
/

show errors;
spool off
