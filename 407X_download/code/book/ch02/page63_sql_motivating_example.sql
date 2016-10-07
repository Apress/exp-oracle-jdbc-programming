/*
drop table t;
create table t( x number, y number );
drop table t1;
create table t1( x number );

insert all
  into t( x, y) values( a, b )
  into t1(x) values ( a )
select rownum a, rownum b
from all_users;
*/
spool sql_motivating_example
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
create or replace procedure single_sql_statement
as
begin
  insert all
    when ( x > 0 ) then
  into destination_table(x) values (x)
    when ( x <= 0 ) then
  into bad_records_table( x, error_message ) values ( x, error_message )
  select x, case 
              when x > 0 then null
              when x <= 0 then 'invalid record: negative or zero number'
            end error_message
  from source_table;
  commit;
end;
/
show errors;
delete destination_table;
delete bad_records_table;
exec single_sql_statement;
select * from bad_records_table;
delete destination_table;
delete bad_records_table;
commit;
exec runstats_pkg.rs_start;
exec bulk_bind;
exec runstats_pkg.rs_middle;
exec single_sql_statement;
exec runstats_pkg.rs_stop(50);
set echo off
spool off

