spool bm_files
set echo on
-- NOTE: You need to replace the directory and file with
-- a real directory in your machine for all of
-- these examples to work.

create or replace directory my_dir as 'C:\TEMP';
-- the following writes to a file creating the 
-- files that will be used in benchmarking reads
declare
  l_buffer varchar2(32767);
  l_file_handle utl_file.file_type;
begin
  -- open the file in write mode -- create one if 
  -- file does not exist.
  l_file_handle := utl_file.fopen( 'MY_DIR', 'benchmark_input.txt', 'W',
    256 );
  for i in 1 .. 10000
  loop
    utl_file.put_line( l_file_handle, 'my line number ' || i );
  end loop;
  utl_file.fclose( l_file_handle );
exception 
  when others then
    raise;
end;
/
drop table bfile_table;
create table bfile_table
(
  x varchar2(30),
  id number,
  bfile_col bfile
);
insert into bfile_table( x, id, bfile_col )
values ( 'benchmark text file', 1, 
  bfilename( 'MY_DIR', 'benchmark_input.txt' ));
commit;

-- create an external table that points to the
-- benchmark_input.txt file - note that 
-- we specify that records are delimited
-- by newlines top indicate a "free format
-- text file

drop table et_table;
create table et_table
(
  data varchar2(4000)
)
organization external
(
  type oracle_loader
  default directory my_dir
  access parameters
  (
    records delimited by newline
  )
  location( 'benchmark_input.txt' )
);

-- verify the count of records in external table

select count(*) from et_table;
spool off
