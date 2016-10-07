create or replace package opt_lock_chksum_demo 
as
  procedure get_emp_details( p_empno in number,
    p_ename in out varchar2,
    p_sal in out number,
    p_rowid in out rowid,
    p_row_checksum in out number );
  procedure update_emp_salary( p_empno in number, 
    p_new_sal in number, p_rowid in rowid, 
    p_checksum in number, 
    p_num_of_rows_updated in out number );
end;
/
show errors;
create or replace package body opt_lock_chksum_demo 
as
  procedure get_emp_details( p_empno in number,
    p_ename in out varchar2, p_sal in out number,
    p_rowid in out rowid, p_row_checksum in out number )
  is
  begin
    select ename, sal, rowid 
    into p_ename, p_sal, p_rowid
    from emp
    where empno = p_empno;

    p_row_checksum := owa_opt_lock.checksum( 'SCOTT', 'EMP', p_rowid );
  end;

  procedure update_emp_salary( p_empno in number, 
    p_new_sal in number, p_rowid in rowid, 
    p_checksum in number, p_num_of_rows_updated in out number )
  is
    l_new_checksum number;
    l_checksum_exception exception;
  begin
    l_new_checksum := owa_opt_lock.checksum( 'SCOTT', 'EMP', p_rowid );
    dbms_output.put_line( 'new checksum: ' || l_new_checksum );
    dbms_output.put_line( 'old checksum: ' || p_checksum );
    p_num_of_rows_updated := 0;
    if( l_new_checksum = p_checksum ) then
      update emp
      set sal = p_new_sal
      where empno = p_empno;

      p_num_of_rows_updated := sql%rowcount;
    end if;
  end;
end;
/
show errors;
