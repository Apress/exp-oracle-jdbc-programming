spool varray_schema
set echo on
/* 
  drop objects.
*/
drop table varchar_varray_table;
drop table number_varray_table;
drop type  varray_of_varchars;
drop type  varray_of_numbers ;

/* create a simple varray of varchars */
create or replace type varray_of_varchars as 
  varray(20) of varchar2(50);
/

/* create a simple varray of numbers */
create or replace type varray_of_numbers as 
  varray(20) of number;
/
/* create a table that contains the varchar varray as a column */
create table varchar_varray_table
(
  varray_column varray_of_varchars
);

/* create a table that contains the number varray as a column */
create table number_varray_table
(
  varray_column varray_of_numbers
);

/* A package that demos accessing and manipulating
   collections from Java. We will use CallableStatements
   from Java to access collections through this package
   procedures and functions.
*/
create or replace package demo_varray_pkg
as
  procedure demo_passing_varray_param( p_varchar_varray in varray_of_varchars );
end;
/
show errors;
create or replace package body demo_varray_pkg 
as
  /* This method demos passing a varray into a procedure 
     from Java.
     To verify that the array was passed successfully 
     we insert a row into the table that contains
     the varray column - later we can do a select
     from this table from the sqlplus prompt to verify
     that our JDBC program did work. 
  */
  procedure demo_passing_varray_param( p_varchar_varray in varray_of_varchars )
  is
  begin
    insert into varchar_varray_table values ( p_varchar_varray );
  end;
end;
/
show errors;

/* To demo the case of selecting number varray in JDBC, we populate
   our table containing number varray column - number_varray_table - 
   with some sample data
*/

insert into number_varray_table values( varray_of_numbers(1, 2, 3, 4, 5) );
commit;
spool off
