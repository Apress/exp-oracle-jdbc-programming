spool lobs_in_plsql
set echo on
drop table clob_table;
create table clob_table
(
  x varchar2(30),
  id number,
  clob_col clob
);

-- clobs

delete from clob_table;

-- inserting data into clob

insert into clob_table (clob_col)
values ( 'A clob example' );

commit;

-- using lobs in pl/sql
-- implicit conversion from clob to varchar2 and vice versa
-- is possible

declare
  varchar2_data varchar2(4001);
  clob_data clob;
begin
  select clob_col
  into varchar2_data
  from clob_table
  where rownum <= 1;

  dbms_output.put_line( varchar2_data );
end;
/
create or replace procedure print_temporary_lob_info( p_msg in varchar2 default ' ' )
is
begin
  dbms_output.put_line ( p_msg );
  for i in ( select cache_lobs, nocache_lobs from v$temporary_lobs )
  loop
    dbms_output.put_line( 'cache lobs: ' || i.cache_lobs );
    dbms_output.put_line( 'nocache lobs: ' || i.nocache_lobs );
  end loop;
end;
/
show errors;

-- assigning a varchar2 to a clob variable

declare
  varchar2_data varchar2(100) := 'value in varchar2';
  l_clob clob;
begin
  l_clob := varchar2_data;
  print_temporary_lob_info ( 'after assignment' );
end;
/
exec print_temporary_lob_info ( 'after PL/SQL block' );

-- using dbms_lob API
-- dbms_lob pacakge is useful in reading from and writing 
-- to lobs - we use clobs in our examples

-- appending some text to a clob
--
declare
  l_clob clob_table.clob_col%type;
  l_str_to_append varchar2(32760) :=  rpad('*',32760,'*');
begin
  select clob_col into l_clob
  from clob_table
  where rownum <= 1
  for update; -- without this you can not update the clob column

  -- Following shows how you can create lobs with length more than
  -- 32K in PL/SQL.
  for i in 1..3 
  loop
    dbms_lob.writeappend( l_clob,  
      length(l_str_to_append), l_str_to_append );
  end loop;

  update clob_table set clob_col = l_clob
  where rownum <= 1;
  commit;
end;
/
/*
declare
  l_clob clob_table.clob_col%type;
  l_clob_portion_copy clob_table.clob_col%type;
  l_str_to_append varchar2(32000) :=  rpad('*',32000,'*');
  l_length number;
  l_buffer varchar2(4000);
  -- for dbms_lob.copy statement to work, the destination lob
  -- has to be created using the dbms_lob.createtemporary method as
  -- shown below. if you remove the statement below - you will
  -- get ORA-22275 ( invalid LOB locator specified ) error.
  dbms_lob.createtemporary( lob_loc=>l_clob_portion_copy, cache => true );
  
  dbms_lob.copy( dest_lob=>l_clob_portion_copy, 
    src_lob => l_clob, amount => 40 );

  dbms_lob.append( src_lob => l_clob, dest_lob => l_clob_portion_copy);

  -- now erase the clob copy
  l_length := dbms_lob.getlength( l_clob_portion_copy);

  dbms_lob.erase( lob_loc => l_clob_portion_copy, amount => l_length );

  -- print the lob's chunk size
  dbms_output.put_line( 'l_clob chunk size = ' ||
    dbms_lob.getchunksize( l_clob ) );
end;
/
*/
 -- read example
select dbms_lob.getlength( clob_col ) from clob_table;
declare
  l_read_buf varchar2(255);
  l_amount_to_read binary_integer := 255;
  l_clob clob;
  l_offset number := 1;
  begin
    select clob_col
    into l_clob
    from clob_table;

    begin
      loop
        dbms_lob.read( l_clob, l_amount_to_read,
          l_offset, l_read_buf );
        l_offset := l_offset + l_amount_to_read;
 
        dbms_output.put_line( l_read_buf );
      end loop;
     exception
       when no_data_found then
         null;
    end;
  end;
/
spool off
