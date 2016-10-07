spool utl_file
set echo on
-- NOTE: You need to replace the directory and file with
-- a real directory in your machine for all of
-- these examples to work.

create or replace directory my_dir as 'C:\TEMP';

-- the following reads and prints out all the file contents

declare
  l_buffer varchar2(32767);
  l_file_handle utl_file.file_type;
begin
  l_file_handle := utl_file.fopen( 'MY_DIR', 'test_bfile.txt', 'R', 256 );
  loop
    utl_file.get_line( l_file_handle, l_buffer );
    dbms_output.put_line( l_buffer );
  end loop;
exception 
  when no_data_found then
    utl_file.fclose( l_file_handle );
end;
/
-- the following writes to a file
declare
  l_buffer varchar2(32767);
  l_file_handle utl_file.file_type;
begin
  -- open the file in write mode -- create one if 
  -- file does not exist.
  l_file_handle := utl_file.fopen( 'MY_DIR', 'my_file.txt', 'W', 256 );
  for i in 1 .. 10 
  loop
    utl_file.put_line( l_file_handle, 'my line number ' || i );
    dbms_output.put_line( l_buffer );
  end loop;
  utl_file.fclose( l_file_handle );
exception 
  when others then
    raise;
end;
/
-- reading from and writing to a binary file.
-- we read from image1.gif and write the contents to
-- image1.gif - thus creating a copy of the image.gif in
-- image1.gif
declare
  l_buffer raw(32767);
  l_input_file utl_file.file_type;
  l_output_file utl_file.file_type;
begin
  l_input_file := utl_file.fopen( 'MY_DIR', 'image.gif', 'RB', 256 );
  l_output_file := utl_file.fopen( 'MY_DIR', 'image1.gif', 'WB', 256 );
  loop
    utl_file.get_raw( l_input_file, l_buffer );
    utl_file.put_raw( l_output_file, l_buffer );
    --dbms_output.put_line( utl_raw.cast_to_varchar2(l_buffer) );
  end loop;
exception 
  when no_data_found then
    utl_file.fclose( l_input_file );
    utl_file.fclose( l_output_file );
end;
/
spool off
