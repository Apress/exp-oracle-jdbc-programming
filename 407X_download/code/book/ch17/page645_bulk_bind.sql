spool bulk_bind
set echo on
set head on
set serveroutput on
--schema
create or replace type object_name_list as table of varchar2(30);
/
show errors;
create or replace type object_id_list as table of number;
/
show errors;
drop table t4;
create table t4 as
select object_name, object_id
from all_objects
where 1 != 1;
drop table t5;
create table t5 as
select object_name, object_id
from all_objects
where 1 != 1;

create or replace procedure no_bulk_bind( p_object_name_list in object_name_list, p_object_id_list in object_id_list ) 
as
begin
  for i in 1..p_object_name_list.count loop
    insert into t4( object_name, object_id) values( p_object_name_list(i), p_object_id_list(i));
  end loop;
end;
/
show errors;

create or replace procedure bulk_bind( p_object_name_list in object_name_list, p_object_id_list in object_id_list ) 
as
begin
  forall i in 1..p_object_name_list.count 
    insert into t5( object_name, object_id) values( p_object_name_list(i), p_object_id_list(i));
end;
/
show errors;

declare
  l_object_name_list object_name_list;
  l_object_id_list object_id_list;
begin
  select object_name, object_id 
  bulk collect into l_object_name_list, l_object_id_list
  from all_objects
  where rownum <= 35000;

  begin
    runstats_pkg.rs_start;
  end;
  begin
    no_bulk_bind( l_object_name_list, l_object_id_list );
  end;
  begin
    runstats_pkg.rs_middle;
  end;
  begin
    bulk_bind( l_object_name_list, l_object_id_list );
  end;
  begin
    runstats_pkg.rs_stop(10);
  end;
end;
/
spool off
