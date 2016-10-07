spool bulk_collect_perf
set echo on
-- bulk fetch and insert versus simple fetch and insert
drop table t1;
create table t1( x number );
insert into t1 select rownum from all_objects, all_users where rownum <= 500000;
commit;
-- create directory object
create or replace directory temp_dir as 'C:\TEMP';
create or replace procedure no_bulk_collect 
as
  l_file_handle utl_file.file_type;
begin
  l_file_handle := utl_file.fopen( 'TEMP_DIR', 'no_bulk_collect.txt', 'W' );
  for i in( select x from t1 )
  loop
    utl_file.put_line( l_file_handle, i.x );
  end loop;
  utl_file.fclose( l_file_handle );
end;
/
show errors;
create or replace procedure bulk_collect ( p_limit_per_fetch in number default 100)
as
  cursor c_x is
    select x
    from t1;
  l_file_handle utl_file.file_type;
  type number_table is table of number index by binary_integer;
  l_x_list number_table;
begin
  l_file_handle := utl_file.fopen( 'TEMP_DIR', 'bulk_collect.txt', 'W' );
  open c_x;
  loop
    fetch c_x bulk collect into l_x_list limit p_limit_per_fetch;
    for i in 1..l_x_list.count 
    loop
      utl_file.put_line( l_file_handle, l_x_list(i));
    end loop;
    exit when c_x%notfound;
  end loop;
  close c_x;
end;
/
show errors;
-- benchmark
exec runstats_pkg.rs_start;
exec no_bulk_collect;
exec runstats_pkg.rs_middle;
exec bulk_collect(500);
exec runstats_pkg.rs_stop( 50 );
spool off
