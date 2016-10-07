set echo on
spool address
drop table address_table;
drop type address;
create or replace type address as object
(
  line1 varchar2(50),
  line2 varchar2(50),
  street varchar2(50),
  city varchar2(30),
  state varchar2(2),
  zip   varchar2(10),
  map member function get_address return varchar2 
)
not final;
/
show errors;

create or replace type body address 
as 
  map member function get_address
    return varchar2
  is
    l_address varchar2(200);
  begin
    l_address := line1|| ' ' ||
                 line2 || ' ' ||
                 street || ' ' ||
                 city || ' ' ||
                 state || ' ' ||
                 zip;
    return l_address;
  end;
end;
/
show errors;
create table address_table of address;
declare
  l_address address;
begin
  l_address := address( '145 Apt # 7','', 'Wander St',
                       'Mountain View', 'CA', '94055' );
  insert into address_table values ( l_address );
  commit;
  dbms_output.put_line ( l_address.get_address() );
end;
/
spool off
set echo off
