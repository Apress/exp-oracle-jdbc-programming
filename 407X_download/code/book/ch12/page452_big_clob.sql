spool big_clob
set echo on
set head on
drop table clob_table;
create table clob_table
(
  x varchar2(30),
  id number,
  clob_col clob
);


/* Now, when you use sql to insert data into
   the clob column as a string, regardless of actual size
   of the clob, you will be able to insert no
   more than 4000 bytes - the sql limit for 
   varchar2. This is shown below.
*/

insert into clob_table( x, id, clob_col) 
values ( 'Insert from SQL', 1, rpad( '*',32000, '*' ) );

select x "Description", id, 
       dbms_lob.getlength( clob_col ) "Length of clob"
from clob_table;

/* However, if you use PL/SQL, the varchar2 variable 
   size limit is 32K. Thus using PL/SQL you can insert
   a clob column as a string of up to 32K length. 
   This is shown below.
*/
declare
  l_big_string varchar2(32760) := rpad( '*',32760, '*' );
begin
  insert into clob_table(x, id, clob_col) 
  values ('Insert from PL/SQL', 2, l_big_string );
end;
/
commit;
select x "Description", id, 
       dbms_lob.getlength( clob_col ) "Length of clob"
from clob_table;

/* Thus beyond 32K, in PL/SQL, you will need to insert in chunks
   using dbms_lob.write() and dbms_lob.writeAppend() as
   shown below
 */

declare
  l_clob_value varchar2(32001) := rpad('*',32000, '*');
  l_clob clob;
begin
  insert into clob_table(x, id, clob_col) 
  values ( 'From PL/SQL Using chunks', 3, empty_clob() )
  returning clob_col into l_clob;

  dbms_lob.write( l_clob, length( l_clob_value), 1, l_clob_value );
  dbms_lob.writeappend( l_clob, length( l_clob_value), l_clob_value );
end;
/
commit;
select x "Description", id, 
       dbms_lob.getlength( clob_col ) "Length of clob"
from clob_table;
spool off
