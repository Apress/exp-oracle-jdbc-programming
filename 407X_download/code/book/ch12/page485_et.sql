spool et
set echo on
set head on
create or replace directory my_dir as '/private/ora92/book/ch09';

drop table my_emp_et;
create table my_emp_et
(
  empno number,
  ename varchar2(20)
)
organization external
(
  type oracle_loader
  default directory my_dir
  access parameters
  (
    fields terminated by ','
    optionally enclosed by "'"
    missing field values are null
  )
  location( 'et_data.txt' )
);

select * from my_emp_et;

select empno, ename 
from my_emp_et
where empno <= 3;

spool off
