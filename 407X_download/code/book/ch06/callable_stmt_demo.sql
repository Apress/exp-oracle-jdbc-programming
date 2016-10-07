spool callable_stmt_demo
set echo on
create or replace package callable_stmt_demo
as
  function get_emp_details_func( p_empno in number )
     return sys_refcursor;
  procedure get_emp_details_proc( p_empno in number, 
     p_emp_details_cursor out sys_refcursor );
  procedure get_emps_with_high_sal( p_deptno in number,
    p_sal_limit in number default 2000 , 
    p_emp_details_cursor out sys_refcursor );
  procedure give_raise( p_deptno in number );
end;
/
show errors;

create or replace package body callable_stmt_demo
as
  function get_emp_details_func( p_empno in number )
     return sys_refcursor 
  is
    l_emp_details_cursor sys_refcursor;
  begin
    open l_emp_details_cursor for
    select empno, ename, job 
    from emp
    where empno = p_empno;

    return l_emp_details_cursor;
  end;
  procedure get_emp_details_proc( p_empno in number, 
     p_emp_details_cursor out sys_refcursor )
  is
  begin
    p_emp_details_cursor := get_emp_details_func( 
      p_empno => p_empno );
  end;
  procedure get_emps_with_high_sal( p_deptno in number,
    p_sal_limit in number default 2000 , 
    p_emp_details_cursor out sys_refcursor )
  is
  begin
    open p_emp_details_cursor for
    select empno, ename, job, sal
    from emp
    where deptno = p_deptno
      and sal > p_sal_limit;
  end;
  procedure give_raise( p_deptno in number )
  is
  begin
    update emp 
    set sal = sal * 1.5
    where deptno = p_deptno;
  end;
end;
/
show errors;
spool off
