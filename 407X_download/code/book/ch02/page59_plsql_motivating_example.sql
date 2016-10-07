spool plsql_motivating_example
set echo on
set doc off
drop table source_table;
create table source_table
as select rownum x
from all_objects, all_users
where rownum <= 100000
--10 bad records - a negative number
union all
select rownum * -1 
from all_objects 
where rownum <= 10;
desc source_table;
drop table destination_table;
create table destination_table (x number constraint check_nonnegative check( x > 0 ) );
drop table bad_records_table;
create table bad_records_table (x number, error_message varchar2(4000 ));
create or replace procedure row_by_row
as
  cursor c is select x from source_table;
  l_x source_table.x%type;
  l_error_message long;
begin
  open c;
  loop
    fetch c into l_x;
    exit when c%notfound;
    begin
      insert into destination_table(x) values( l_x );
    exception
      when others then
        l_error_message := sqlerrm;
        insert into bad_records_table( x, error_message ) 
        values( l_x, l_error_message );
    end;
  end loop;
  commit;
end;
/
show errors;
create or replace type number_table as table of number;
/
create or replace procedure bulk_bind
as
  l_number_table number_table;
  l_error_message long;
  l_error_row_number number;
  l_error_code number;
begin
  select x
  bulk collect into l_number_table
  from source_table;
  begin
    forall i in 1..l_number_table.count save exceptions
      insert into destination_table( x ) values( l_number_table(i) );
  exception
   when others then
      for j in 1..sql%bulk_exceptions.count loop
        l_error_row_number := sql%bulk_exceptions(j).error_index;
        l_error_code := sql%bulk_exceptions(j).error_code;
        l_error_message := sqlerrm( -1 * l_error_code );
        insert into bad_records_table( x, error_message ) 
        values( l_number_table(l_error_row_number),
                l_error_message );
      end loop;
  end;
  commit;
end;
/
show errors;
exec row_by_row;
select * from bad_records_table;
delete destination_table;
delete bad_records_table;
select * from bad_records_table;
exec bulk_bind;
select * from bad_records_table;
exec runstats_pkg.rs_start;
exec row_by_row;
exec runstats_pkg.rs_middle;
exec bulk_insert;
exec runstats_pkg.rs_stop(50);
set echo off
spool off
