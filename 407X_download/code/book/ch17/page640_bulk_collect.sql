spool bulk_collect
set echo on
set head on
set serveroutput on
--schema
create or replace type object_name_list as table of varchar2(30);
/
show errors;
declare
  l_object_name_list object_name_list;
begin
  select object_name 
  bulk collect into l_object_name_list
  from all_objects
  where rownum <= 10;

  for i in 1..l_object_name_list.count 
  loop
    dbms_output.put_line( l_object_name_list( i ) );
  end loop;
end;
/
declare
  cursor c_object_ids is
    select object_id
    from all_objects
    where rownum <= 3000;
  type number_table is table of number index by binary_integer;
  l_object_id_list number_table;
  l_counter number := 0;
begin
  open c_object_ids;
  loop
    fetch c_object_ids bulk collect into l_object_id_list limit 200;
    for i in 1..l_object_id_list.count 
    loop
      l_counter := l_counter + 1;
      dbms_output.put_line( l_object_id_list( i ) );
    end loop;
    exit when c_object_ids%notfound;
  end loop;
  close c_object_ids;
  dbms_output.put_line( 'total fetched: ' || l_counter );
end;
/
spool off
