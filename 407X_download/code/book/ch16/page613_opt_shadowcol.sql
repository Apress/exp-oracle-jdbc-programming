spool opt4
set echo on
drop table emp1;
drop sequence seq1;
create table emp1 as 
select e.*, 1 as row_change_indicator 
from emp e where 1 != 1;

create sequence seq1 cache 100;
insert into emp1( empno, ename, job, mgr, hiredate, sal, comm, deptno, row_change_indicator ) 
select e.*, seq1.nextval from emp e;
create or replace package opt_lock_shadowcol_demo 
as
  procedure get_emp_details( p_empno in number, p_ename in out varchar2,
    p_sal in out number, p_row_change_indicator in out number );
  procedure update_emp_info( p_empno in number, p_new_sal in number, p_new_ename in varchar2,  
    p_row_change_indicator in number, p_num_of_rows_updated in out number );
end;
/
show errors;
create or replace package body opt_lock_shadowcol_demo 
as
  procedure get_emp_details( p_empno in number, p_ename in out varchar2,
    p_sal in out number, p_row_change_indicator in out number )
  is
  begin
    select ename, sal, row_change_indicator
    into p_ename, p_sal, p_row_change_indicator
    from emp1
    where empno = p_empno;
  end;

  procedure update_emp_info( p_empno in number, p_new_sal in number, p_new_ename in varchar2, p_row_change_indicator in number, p_num_of_rows_updated in out number )
  is
  begin
    p_num_of_rows_updated := 0;
    update emp1
      set sal = p_new_sal,
          ename = p_new_ename,
          row_change_indicator = seq1.nextval
    where empno = p_empno 
      and p_row_change_indicator = row_change_indicator;
    p_num_of_rows_updated := sql%rowcount;
  end;
end;
/
show errors;
spool off
