set echo on
spool struct_schema
drop table item_table;
drop table manufactured_item_table;
--drop type item;
create or replace type item as object
(
  id number,
  name varchar2(20),
  description varchar2(50)
)
/
show errors;
create table item_table of item;
insert into item_table values ( 1, 'item1', 'item1 desc' );
commit;
create table manufactured_item_table
(
  manufactured_item item,
  manufactured_date date
);
insert into manufactured_item_table values ( 
  item(1, 'manu_item1', 'manu_item1 desc'), sysdate -1 );
commit;
spool off
