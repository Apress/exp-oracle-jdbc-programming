spool ref
set echo on
/* drop objects */
drop table emp_table_with_ref;
drop type emp_ref_type ;

/* create an object type emp_ref_type */
create type emp_ref_type as object
(
  emp_no number,
  name varchar2(20),
  manager ref emp_ref_type 
);
/
show errors;
-- without constraint 
--create table emp_table_with_ref of emp_ref_type;
-- with constraint
create table emp_table_with_ref of emp_ref_type
( manager references emp_table_with_ref );
insert into emp_table_with_ref values( 1, 'Larry', null );
/* insert the second row where the manager
   is pointing to the first row object.
   Essentially John reports to Larry.
*/
insert into emp_table_with_ref
select emp_ref_type( 2, 'John', ref(e))
from emp_table_with_ref e
where emp_no = 1;
/* put another guy under John */
insert into emp_table_with_ref
select emp_ref_type( 3, 'Jack', ref(e))
from emp_table_with_ref e
where emp_no = 2;

commit;
set head on
column emp_no format 999999
column name format a5
column manager format a40
-- simple select
select * from emp_table_with_ref;

-- using value()
select value(e)
from emp_table_with_ref e;

--- using deref
select value(e).name Name, deref(value(e).manager) Manager
from emp_table_with_ref e;
--- using deref to get Jack's data
select value(e).name Name, deref(value(e).manager) Manager
from emp_table_with_ref e
where e.name = 'Jack';

delete from emp_table_with_ref e 
where e.name = 'John';
select value(e).name Name, deref(value(e).manager) Manager
from emp_table_with_ref e
where e.name = 'Jack';
-- use of is dangling predicate
select value(e).name Name, deref(value(e).manager) Manager
from emp_table_with_ref e
where e.name = 'Jack'
  and value(e).manager is not dangling;

spool off
