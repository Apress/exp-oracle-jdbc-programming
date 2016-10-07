spool item_proc_schema
set echo on
create or replace package item_pkg 
as
  procedure get_item( p_item in out item ) ;
  procedure get_items( p_items out sys_refcursor ) ;
  procedure insert_item( p_item in item ) ;
end item_pkg;
/
show errors;
create or replace package body item_pkg 
as
  procedure get_item( p_item in out item  ) 
  is
  begin
    select value(i)
    into p_item
    from item_table i
    where rownum <= 1;
  end get_item;
  procedure get_items( p_items out sys_refcursor  ) 
  is
  begin
    open p_items for
      select value(c)
      from item_table c;
  end get_items;
  procedure insert_item( p_item in item  ) 
  is
  begin
    insert into item_table values( p_item );
  end insert_item;

end item_pkg;
/
show errors;
declare
  l_item item;
begin
  item_pkg.get_item( l_item);
  dbms_output.put_line( l_item.name );
end;
/
spool off
