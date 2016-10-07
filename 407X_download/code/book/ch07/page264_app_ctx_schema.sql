spool app_ctx_schema
set echo on
set head on
-- create application context
create or replace context hr_app_ctx using hr_app_ctx_pkg;

create or replace package hr_app_ctx_pkg
as
  procedure execute_dynamic_query( p_ename in varchar2, p_deptno in number, p_cursor in out sys_refcursor );
end;
/
show errors;
create or replace package body hr_app_ctx_pkg
as
  procedure execute_dynamic_query( p_ename in varchar2, p_deptno in number, p_cursor in out sys_refcursor )
  is
    l_query long;
  begin
    l_query := 'select ename, deptno, job, sal from emp where 0 = 0';
    if( p_ename is not null ) then
      dbms_session.set_context( 'HR_APP_CTX', 'ENAME', p_ename ||'%');
      l_query := l_query ||
                 ' and ename like sys_context( ''HR_APP_CTX'', ''ENAME'')';
    end if;
    if( p_deptno is not null ) then
      dbms_session.set_context( 'HR_APP_CTX', 'DEPT_NO', p_deptno );
      l_query := l_query ||
                 ' and deptno = to_number(sys_context( ''HR_APP_CTX'', ''DEPT_NO''))';
    end if;
    dbms_output.put_line( l_query );
    open p_cursor for l_query;
  end;
end;
/
show errors;
set serveroutput on
begin
  dbms_session.set_context( 'HR_APP_CTX', 'ENAME', 'BLAKE');
end;
/
variable x refcursor;
variable y refcursor;
variable z refcursor;
variable a refcursor;
exec hr_app_ctx_pkg.execute_dynamic_query( null, null, :x );
print x
exec hr_app_ctx_pkg.execute_dynamic_query( 'A', null, :y );
print y
exec hr_app_ctx_pkg.execute_dynamic_query( null, 10, :z );
print z
exec hr_app_ctx_pkg.execute_dynamic_query( 'A', 20, :a );
print a
spool off
