spool benchmark_numeric_extension
set echo on
/* 
  drop objects.
*/
drop table number_table_nt;
drop type number_table_type;
/* create a nested table type of numbers*/
create or replace type number_table_type as table of number;
/
show errors;
/* create a table with NT column */
create table number_table_nt
(
  nt_col number_table_type
)
nested table nt_col store as number_nt;
-- populate random data for benchmarking
insert into number_table_nt
select
  cast
  (
    multiset
    (
      select  round( dbms_random.value(), 2)*100 
      from all_objects
      where rownum <= 10000
    ) as number_table_type
  )
from dual;
show errors;
commit;
select count(*) from number_table_nt t,  table( t.nt_col) v;
spool off
