spool redo
set echo on
set head on
create or replace function get_redo_size
return number
as
  l_redo_size number;
begin
  select value 
  into l_redo_size
  from v$mystat m, v$statname s
  where s.name like 'redo size'
    and m.statistic# = s.statistic#;
  return l_redo_size;
end;
/
create or replace procedure single_stmt_insert
as
begin
  insert into t1
  select rownum
  from all_objects, all_users
  where rownum <= 100000;
end;
/
show errors;
variable value number;
exec :value := get_redo_size;
exec single_stmt_insert
exec dbms_output.put_line( 'redo consumed = ' || (get_redo_size - :value) );
exec :value := get_redo_size;
exec row_by_row
exec dbms_output.put_line( 'redo consumed = ' || (get_redo_size - :value) );
spool off
