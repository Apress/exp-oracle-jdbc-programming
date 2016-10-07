spool demo_refcursor
set echo on
create or replace procedure demo_refcursor( p_query_selector in varchar2,
  p_criterion in varchar2, p_ref_cursor in out sys_refcursor )
is
  l_empno emp.empno%type;
  l_ename emp.ename%type;
  l_job emp.job%type;
begin
  if( 'ename' = p_query_selector ) then
    open p_ref_cursor for
      select empno, ename, job
      from emp
      where ename like '%'||p_criterion||'%';
  elsif( 'job' = p_query_selector ) then
    open p_ref_cursor for
      select empno, ename, job
      from emp
      where job like '%'||p_criterion||'%';
  end if;
end;
/
show errors;
variable rc_var refcursor;
exec demo_refcursor( 'ename', 'KING', :rc_var )
print rc_var
exec demo_refcursor( 'job', 'CLERK', :rc_var )
print rc_var
spool off

