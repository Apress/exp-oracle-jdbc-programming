spool bulk_bind_error
set echo on
set head on
set serveroutput on
--schema
drop table t6;
create table t6( x number primary key constraint check_nonnegative_lt_10 check( x > 0  and x <= 10 )) ;
insert into t6(x) values( null );
insert into t6(x) values( -5 );
--create table t6( x number primary key check( x > 0 and x <= 10 ) );
declare
  type number_table is table of number;
  l_number_table number_table;
begin
  l_number_table := number_table( 1, 2, 3, null, -5, 6, 7, 8, 9, 11);
  forall i in 1..l_number_table.count
    insert into t6( x ) values ( l_number_table(i) );
end;
/
select * from t6;
declare
  type number_table is table of number;
  l_number_table number_table;
begin
  l_number_table := number_table( 1, 2, 3, null, -5, 6, 7, 8, 9, 11);
  begin
    forall i in 1..l_number_table.count save exceptions
      insert into t6( x ) values ( l_number_table(i) );
  exception 
    when others then
      dbms_output.put_line( 'number of exceptions raised: ' || sql%bulk_exceptions.count );
      for i in 1..sql%bulk_exceptions.count loop
        dbms_output.put_line( 'row number : ' || sql%bulk_exceptions(i).error_index );
        dbms_output.put_line( 'error code: ' || sql%bulk_exceptions(i).error_code );
        dbms_output.put_line( 'message: ' || sqlerrm( -sql%bulk_exceptions(i).error_code ));
        
      end loop;
  end;
end;
/
select * from t6;
spool off
