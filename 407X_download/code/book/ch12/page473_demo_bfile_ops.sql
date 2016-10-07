spool demo_bfile_ops
set echo on
drop table bfile_table;
create table bfile_table
(
  x varchar2(30),
  id number,
  bfile_col bfile
);
-- NOTE: You need to replace the directory and file with
-- a real directory in your machine for all of
-- these examples to work.

create or replace directory my_dir as 'C:\TEMP';

-- insert data - make sure that the directory object is
-- in all caps.

insert into bfile_table( x, id, bfile_col )
values ( 'Ascii text file', 1, bfilename( 'MY_DIR', 'test_bfile.txt' ));
insert into bfile_table( x, id, bfile_col )
values ( 'Binary Gif File', 2, bfilename( 'MY_DIR', 'image.gif' ));
commit;
spool off
