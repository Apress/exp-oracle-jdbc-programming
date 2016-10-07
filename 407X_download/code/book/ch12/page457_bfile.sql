spool bfile
set echo on
set head on
-- NOTE: You need to replace the directory and file with
-- a real directory in your machine for all of
-- these examples to work.

create or replace directory my_dir as 'C:\TEMP';

declare
  l_blob blob;
  l_bfile bfile;
  l_read_buf varchar2(200);
  l_amount_to_read binary_integer := 100;
  l_offset number := 1;
begin
  dbms_lob.createtemporary( l_blob, true );
  -- create a bfile locator - note that the directory
  -- name has to be in capital letters and inside
  -- single quotes.
  l_bfile := bfilename( directory => 'MY_DIR', filename => 'test_bfile.txt' );
  -- now open the file test_bfile.txt in the directory we created
  dbms_lob.fileopen( l_bfile );
  -- load the file into the clob
  dbms_lob.loadfromfile( l_blob, l_bfile, dbms_lob.getlength( l_bfile) );
  -- close the file
  dbms_lob.fileclose( l_bfile );
  -- now we read and print the blob we loaded just now
  dbms_output.put_line( 'blob contents -------' );
  begin
    loop
      dbms_lob.read( l_blob, l_amount_to_read, 
        l_offset, l_read_buf );
      l_offset := l_offset + l_amount_to_read;

      -- output the line - note that we need to cast the
      -- binary data into varchar2 using the 
      -- utl_raw.cast_to_varchar2 function.
      dbms_output.put_line( utl_raw.cast_to_varchar2(l_read_buf) );
    end loop;
  exception
    when no_data_found then
      null;
  end;

  delete blob_table;
  insert into blob_table( x, id, blob_col ) values( 'blob loaded from text',
    1, l_blob );
  commit;
end;
/
spool off
