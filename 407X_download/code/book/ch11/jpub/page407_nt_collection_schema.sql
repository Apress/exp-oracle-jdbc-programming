spool nt_collection_schema
set echo on
drop table emp_table;
drop type nested_table_of_addresses;
drop type address;
create or replace type address as object
(
  line1 varchar2(50),
  line2 varchar2(50),
  street varchar2(50),
  city varchar2(30),
  state varchar2(2),
  zip   varchar2(10)
)
/
show errors;
create or replace type nested_table_of_addresses as table of address;
/
show errors;
create table emp_table
(
  empno number,
  ename varchar2(50),
  hiredate date,
  emp_address_list nested_table_of_addresses
)
nested table emp_address_list store as emp_address_list_table;
declare
  l_address_list nested_table_of_addresses;
begin
  l_address_list := nested_table_of_addresses
               (
                 address( '145 Apt # 7','', 'Wander St',
                       'Mountain View', 'CA', '94055' ),
                 address( '333 Apt # 11','', 'Wonder St',
                       'Cupertino', 'CA', '94666' )
               );
  insert into emp_table values ( 1, 'King', sysdate-47*365, l_address_list );
  commit;
end;
/
show errors;
commit;
set head on
column empno format 99999
column ename format a6
column emp_address_list format a40
column line1 format a15
column line2 format a6
column street format a12
column city format a15
column state format a3
select e.empno, e.ename, e.hiredate, 
       e.emp_address_list as emp_address_list
from emp_table e;
select e.empno, e.ename, e.hiredate, a.*
from emp_table e, table( e.emp_address_list ) a;
spool off
