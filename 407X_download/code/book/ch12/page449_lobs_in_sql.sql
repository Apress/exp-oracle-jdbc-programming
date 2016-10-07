spool lobs_in_sql
set echo on
set head on
drop table clob_table;
create table clob_table
(
  x varchar2(30),
  id number,
  clob_col clob
);

-- clobs
-- using lobs in sql
-- You can access clobs (or nclobs) using the sql varchar2
-- semantics - This is typically useful when performing operations
-- with relatively smaller lobs (e.g. upto 100K bytes.)
-- Many sql functions that take varchar2 as a parameter also
-- accept a clob instead. 

-- inserting data into clob

insert into clob_table (clob_col)
values ( 'A clob example' );

commit;

-- selecting data from clob
select clob_col from clob_table;

-- now let us apply some of the known functions that
-- work on varchar2 to our clob column

-- substr
select substr( clob_col, 1, 3) from clob_table;

-- instr
select instr( clob_col, 'clob') from clob_table;

-- concatenation

select clob_col || '( concatenated to clob)' 
from clob_table;

--length
select length(clob_col) clob_length
from clob_table;
-- using "like"
select clob_col || '( concatenated to clob)' 
from clob_table
where clob_col like '%clob%';

update clob_table set clob_col = empty_clob();
commit;
select length('') char_length,
  length( clob_col) length_clob from clob_table;

-- now for blobs
drop table blob_table;
create table blob_table
(
  x varchar2(30),
  id number,
  blob_col blob
);

insert into blob_table( blob_col) values ( 'abc' );
commit;

-- the following does not work as sqlplus does not
-- have the capability to show binary data...
-- we will see how to do this using PL/SQL or JDBC though.

select * from blob_table;
spool off
