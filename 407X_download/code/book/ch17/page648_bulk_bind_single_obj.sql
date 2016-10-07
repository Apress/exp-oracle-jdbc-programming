spool bulk_bind_single_obj
set echo on
set head on
set serveroutput on
--schema
drop type object_data_list;
drop type object_data;
create or replace type object_data as object
(
  object_name varchar2(30),
  object_id number
)
/
show errors;
create or replace type object_data_list as table of object_data;
/
show errors;
declare
  l_object_data_list object_data_list;
begin
  select object_data( object_name, object_id )
  bulk collect into l_object_data_list
  from all_objects
  where rownum <= 5;
  forall i in 1..l_object_data_list.count 
    insert into t5( object_name, object_id) values( l_object_data_list(i).object_name, l_object_data_list(i).object_id);
end;
/
show errors;

declare
  l_object_data_list object_data_list;
begin
  select object_data( object_name, object_id )
  bulk collect into l_object_data_list
  from all_objects
  where rownum <= 5;
  insert into ( select object_name, object_id from t5 )
  select a.object_name, a.object_id
  from table( cast( l_object_data_list as object_data_list ) ) a;
end;
/
show errors;
spool off
