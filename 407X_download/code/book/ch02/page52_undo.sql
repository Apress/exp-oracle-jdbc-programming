spool undo
set echo on
drop table t1;
create table t1 ( x number );
create or replace function get_undo
return number
is
  l_undo number := 0;
begin
  begin
    select used_ublk * (select value from v$parameter where name='db_block_size')
    into l_undo
    from v$transaction;
  exception
    when no_data_found then
      null; -- ignore - return 0
    when others then
      raise;
  end;
  return l_undo;
end;
/
show errors;
create or replace procedure row_by_row
as
begin
  for i in ( select rownum from all_objects, all_users where rownum <= 100000 )
  loop
    insert into t1(x) values(i.rownum);
  end loop;
end;
/
show errors;
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
dbms_output.put_line( get_undo );
exec row_by_row
dbms_output.put_line( get_undo );
select count(*) from t1;
rollback;
dbms_output.put_line( get_undo );
exec single_stmt_insert
dbms_output.put_line( get_undo );
select count(*) from t1;
rollback;

spool off
