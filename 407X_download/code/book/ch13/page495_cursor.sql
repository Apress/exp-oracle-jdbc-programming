spool cursor
set echo on
drop table t1;
create table t1 ( x number);
insert into t1
select rownum
from all_objects
where rownum <= 5;
commit;
select * from t1;

-- now open a cursor and select from it
-- print the results

declare
  cursor l_cursor is select x from t1;
  l_dummy number;
begin
  open l_cursor;
  loop
    fetch l_cursor into l_dummy;
    exit when l_cursor%notfound;
    dbms_output.put_line( l_dummy );
  end loop;
  close l_cursor;
end;
/

-- now use implicit cursor
begin
  for l_cursor in( select x from t1 )
  loop
    dbms_output.put_line( l_cursor.x );
  end loop;
end;
/

spool off
