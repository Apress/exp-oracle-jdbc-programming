spool demo_clob_operations
set echo on
drop table clob_table;
create table clob_table
(
  x varchar2(30),
  id number,
  clob_col clob
);

insert into clob_table( x, id, clob_col ) 
values( 'Insert from SQL', 1, rpad( 'x', 4000, 'x' ) );
declare
  l_string long := rpad( 'x', 32000, 'x' );
  l_clob_col clob;
begin
  insert into clob_table( x, id, clob_col ) 
  values( 'Insert from PL/SQL', 2, l_string );
  insert into clob_table( x, id, clob_col ) 
  values( 'From PL/SQL Using chunks', 3, l_string ) 
  returning clob_col into l_clob_col;
  
  for i in 1..1 
  loop
    dbms_lob.writeappend( l_clob_col,  
      dbms_lob.getlength(l_string), l_string );
  end loop;
end;
/
select x, id, dbms_lob.getlength( clob_col ) 
from clob_table;
show errors;

