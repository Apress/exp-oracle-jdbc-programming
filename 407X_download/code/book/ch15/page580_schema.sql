spool schema
set tab off
set echo on
connect admin/admin
drop user db_app_data cascade;
create user db_app_data identified by db_app_data default tablespace users quota
unlimited on users;

grant create session,
      create table,
      create public synonym,
      drop public synonym
      to db_app_data;

drop user db_data_access_layer cascade;
create user db_data_access_layer identified by db_data_access_layer;

grant create session,
      create public synonym,
      drop public synonym,
      create procedure
      to db_data_access_layer;

conn db_app_data/db_app_data
-- create schema for the application
create table dept 
(
  dept_no number primary key,
  dept_name varchar2(20)
);

create table emp
(
  empno number primary key,
  ename varchar2(20),
  dept_no references dept,
  salary number,
  job varchar2(30)
);

drop public synonym emp;
drop public synonym dept;
create public synonym emp for emp;
create public synonym dept for dept;
conn admin/admin
-- need direct privileges on the objects for the procedures to
-- work
grant select, insert, delete, update on emp to db_data_access_layer;
grant select, insert, delete, update on dept to db_data_access_layer;
conn db_data_access_layer/db_data_access_layer
-- create package that manipulates schema data

create or replace package manager_pkg
as
  procedure add_dept( 
    p_dept_no in number,
    p_dept_name in varchar2 );

  procedure hire_emp( 
    p_empno in number,
    p_ename in varchar2,
    p_dept_no in number,
    p_salary in number,
    p_job in varchar2 );
    
  procedure raise_salary( 
    p_empno in number,
    p_salary_hike_pcnt in number );
    
  procedure fire_emp( 
    p_empno in number );
end manager_pkg;
/
show errors;

create or replace package body manager_pkg
as
  procedure add_dept( 
    p_dept_no in number,
    p_dept_name in varchar2 )
  is
  begin
    insert into dept( dept_no, dept_name )
    values( p_dept_no, p_dept_name );
  end add_dept;

  procedure hire_emp( 
    p_empno in number,
    p_ename in varchar2,
    p_dept_no in number,
    p_salary in number,
    p_job in varchar2 )
  is
  begin
    insert into emp( empno, ename, dept_no, salary, job )
    values( p_empno, p_ename, p_dept_no, p_salary, p_job );
  end hire_emp;
    
  procedure raise_salary( 
    p_empno in number,
    p_salary_hike_pcnt in number )
  is
  begin
    update emp 
    set salary = salary * (p_salary_hike_pcnt/100.00)
    where empno = p_empno;
  end raise_salary;
    
  procedure fire_emp( 
    p_empno in number )
  is
  begin
    delete emp 
    where empno = p_empno;
  end fire_emp;
end manager_pkg;
/
show errors;

-- create package that reports schema data
create or replace package clerk_pkg
as
  function list_dept_details( p_dept_no in number )
    return sys_refcursor;
  function list_emp_details( p_empno in number )
    return sys_refcursor;
end clerk_pkg;
/
show errors;

create or replace package body clerk_pkg
as
  function list_dept_details( p_dept_no in number )
    return sys_refcursor
  is
    l_dept_details sys_refcursor;
  begin
    open l_dept_details for
      select dept_no, dept_name
      from dept
      where dept_no = p_dept_no;
    return l_dept_details;
  end list_dept_details;

  function list_emp_details( p_empno in number )
    return sys_refcursor
  is
    l_emp_details sys_refcursor;
  begin
    open l_emp_details for
      select empno, ename, dept_no, salary, job
      from emp
      where empno = p_empno;
    return l_emp_details;
  end list_emp_details;
end clerk_pkg;
/
show errors;

drop public synonym manager_pkg;
create public synonym manager_pkg for manager_pkg;
drop public synonym clerk_pkg;
create public synonym clerk_pkg for clerk_pkg;

connect admin/admin
-- create manager_role
drop role manager_role;
create role manager_role;
--grant create session to manager_role;
grant execute on manager_pkg to manager_role;
grant execute on clerk_pkg to manager_role;

-- create clerk_role
drop role clerk_role;
create role clerk_role;
--grant create session to clerk_role;
grant execute on clerk_pkg to clerk_role;

--create db_manager1 to access manager_pkg via manager_role.
drop user db_manager1 cascade;
create user db_manager1 identified by db_manager1;
grant create session to db_manager1;
grant manager_role, clerk_role to db_manager1;
--create db_clerk1 to access clerk_pkg via clerk_role.
drop user db_clerk1 cascade;
create user db_clerk1 identified by db_clerk1;
grant create session to db_clerk1;
grant clerk_role to db_clerk1;

conn db_manager1/db_manager1
select * from session_roles;
desc manager_pkg
desc clerk_pkg
conn db_clerk1/db_clerk1
select * from session_roles;
desc manager_pkg
desc clerk_pkg
spool off
