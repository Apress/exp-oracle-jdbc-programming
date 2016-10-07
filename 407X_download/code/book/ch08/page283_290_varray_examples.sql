spool varray_examples
set echo on
create or replace type varray_of_numbers as varray(25) of number;
/
show errors;
create or replace type varray_of_persons as varray(10) of person;
/
show errors;
-- Use the varray_of_numbers
declare
  l_varray_of_numbers varray_of_numbers; 
begin
  -- initialize the varray variable
  l_varray_of_numbers := varray_of_numbers();
  -- extend it to store 3 numbers.
  l_varray_of_numbers.extend(3);
  l_varray_of_numbers(1) := 1;
  l_varray_of_numbers(2) := 2;
  for i in 1..l_varray_of_numbers.count
  loop
    dbms_output.put_line( l_varray_of_numbers(i) );
  end loop;
end;
/
  -- another way of storing values in a varray.
declare
  l_varray_of_persons varray_of_persons; 
begin
  l_varray_of_persons := 
    varray_of_persons( person( 'Joe', sysdate-23*365),
                      person( 'John Doe', sysdate-25*365),
                      person( 'Tim Drake', sysdate-27*365) 
                    );
  for i in(
            select vp.name, vp.date_of_birth
            from table( l_varray_of_persons ) vp
            where vp.name like 'J%'
          )
  loop
    dbms_output.put_line( i.name || ', ' || i.date_of_birth );
  end loop;
end;
/
drop table dep_email_addresses;
drop type varray_of_varchars;
create or replace type varray_of_varchars as 
  varray(10) of varchar2(50);
/
show errors;
create table dep_email_addresses
(
  dep_no number,
  email_addresses  varray_of_varchars
);

  insert into dep_email_addresses( dep_no, email_addresses)
  values( 10, varray_of_varchars('king@mycompany.com', 
    'joe@mycompany.com', 'john@mycompany.com') );
column dep_no format 9999
select * from dep_email_addresses;

select c.dep_no, a.column_value
from dep_email_addresses c, TABLE(c.email_addresses) a;
-- altering size
-- alter type varray_of_varchars modify limit 70 cascade;
-- mention describe error
-- altering datatype
-- alter type varray_of_varchars modify element type varchar2(80) cascade;
 /* insert by selecting from a table.
    In the example below, we insert a row into dep_email_addresses
    that we select from the same table dep_email_addresses.
    MULTISET keyword is used to specify that the subquery
    inside can return one row ( without it Oracle will give
    an error if the subquery returns more than one row.)
 
    CAST keyword casts a value from one type to another; we use
    here to cast the resulting query value to our varray type.
 */
 insert into dep_email_addresses
 select 20,
 cast
 (
   multiset
   (
     select a.column_value
     from dep_email_addresses c, TABLE(c.email_addresses) a
   )
   as varray_of_varchars
 )
 from dep_email_addresses
 where dep_no = 10;

create or replace procedure add_email_address( p_dep_no in number, 
  p_email_address in varchar2)
is
  l_prev_email_addresses dep_email_addresses.email_addresses%type;
begin

  /* First select the varray column */
  select c.email_addresses
  into l_prev_email_addresses
  from dep_email_addresses c
  where dep_no = p_dep_no;

  /* extend the varray to store the new email address */
  l_prev_email_addresses.extend(1);
  /* Store the new email address as the last element */
  l_prev_email_addresses( l_prev_email_addresses.count)
    := p_email_address;

  /* update the table with the new email address */
  update dep_email_addresses d
  set d.email_addresses = l_prev_email_addresses
  where dep_no = p_dep_no;
end add_email_address;
/
show errors;
exec add_email_address( 10, 'new_contact@mycompany.com')
select a.column_value
from dep_email_addresses c, TABLE(c.email_addresses) a
where dep_no=10;
set echo off
spool off

