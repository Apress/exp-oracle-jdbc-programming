spool opt3
set echo on
drop table my_emp;
create table my_emp rowdependencies as select * from emp;
create or replace package opt_lock_scn_demo 
as
  procedure get_emp_details( p_empno in number, p_ename in out varchar2,
    p_sal in out number, p_ora_rowscn in out number );
  procedure update_emp_info( p_empno in number, p_new_sal in number,
    p_new_ename in varchar2, p_ora_rowscn in number, p_num_of_rows_updated in out number );
end;
/
show errors;
create or replace package body opt_lock_scn_demo 
as
  procedure get_emp_details( p_empno in number, p_ename in out varchar2,
    p_sal in out number, p_ora_rowscn in out number )
  is
  begin
    select ename, sal, ora_rowscn
    into p_ename, p_sal, p_ora_rowscn
    from my_emp
    where empno = p_empno;
  end;

  procedure update_emp_info( p_empno in number, p_new_sal in number, p_new_ename in varchar2, p_ora_rowscn in number, p_num_of_rows_updated in out number )
  is
  begin
    p_num_of_rows_updated := 0;
    update my_emp
    set sal = p_new_sal,
        ename = p_new_ename
    where empno = p_empno 
      and p_ora_rowscn = ora_rowscn;
    p_num_of_rows_updated := sql%rowcount;
  end;
end;
/
show errors;
spool off
