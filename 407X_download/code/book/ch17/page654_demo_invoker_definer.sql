spool demo_invoker_definer
set echo on
set head on
set serveroutput on

conn / as sysdba;
-- app objects schema
drop user db_app_data cascade;
create user db_app_data identified by db_app_data default tablespace users quota
unlimited on users;
grant create session,
      create table,
      create public synonym,
      drop public synonym
      to db_app_data;
conn db_app_data/db_app_data
drop table t1;
create table t1 ( x number );
insert into t1 select rownum from all_objects where rownum <= 5;
drop public synonym t1;
create public synonym t1 for t1;
conn / as sysdba;
drop role demo_role;
create role demo_role;
grant select on t1 to demo_role;
-- create definer user
drop user definer cascade;
create user definer identified by definer default tablespace users quota
unlimited on users;
grant create session,
      create table,
      create procedure
      to definer;
grant demo_role to definer;
conn definer/definer
-- select from t1 in anonymous block.
declare
  l_count number;
begin
  select count(*) 
  into l_count 
  from t1;
  dbms_output.put_line( 'Count is : ' || l_count );
end;
/
create or replace procedure definer_mode_proc
is
  l_count number;
begin
  select count(*) 
  into l_count 
  from t1;
  dbms_output.put_line( 'Count is : ' || l_count );
end;
/
show errors;
create or replace procedure show_roles
is
begin
  dbms_output.put_line( 'printing enabled roles' );
  for i in ( select role from session_roles )
  loop
    dbms_output.put_line( i.role );
  end loop;
end;
/
show errors;
exec show_roles;
conn / as sysdba;
grant select on t1 to definer;
conn definer/definer;
-- now the following will compile
create or replace procedure definer_mode_proc
is
  l_count number;
begin
  select count(*) 
  into l_count 
  from t1;
  dbms_output.put_line( 'Count is : ' || l_count );
end;
/
show errors;
conn / as sysdba;
drop user invoker cascade;
create user invoker identified by invoker default tablespace users quota
unlimited on users;
grant create session,
      create table,
      create procedure
      to invoker;
grant demo_role to invoker;
conn invoker/invoker
-- the following will not compile since compilation time object resolution
-- is still based on definer rights
create or replace procedure invoker_mode_proc
authid current_user
is
  l_count number;
begin
  select count(*) 
  into l_count 
  from t1;
  dbms_output.put_line( 'Count is : ' || l_count );
end;
/
show errors;
-- However, if we use dynamic SQL, the following will compile.
create or replace procedure invoker_mode_proc
authid current_user
is
  l_count number;
begin
  execute immediate 'select count(*) from t1' into l_count;
  dbms_output.put_line( 'Count is : ' || l_count );
end;
/
show errors;
exec invoker_mode_proc
conn / as sysdba
revoke select on t1 from definer;
conn definer/definer
create or replace procedure definer_mode_proc
is
  l_count number;
begin
  execute immediate 'select count(*) from t1' into l_count;
  dbms_output.put_line( 'Count is : ' || l_count );
end;
/
show errors;
execute definer_mode_proc
--demo object resolution differences between invokers mode and definers mode
conn / as sysdba
drop user utils cascade;
create user utils identified by utils;
grant create session,
      create procedure,
      create public synonym,
      drop public synonym
      to utils;
conn utils/utils
create or replace function count_rows( p_table_name in varchar2 )
return number
is
  l_count number;
begin
  execute immediate 'select count(*) from ' || p_table_name into l_count;
  return l_count;
end;
/
show errors;
grant execute on count_rows to public;
drop public synonym count_rows;
create public synonym count_rows for count_rows;
exec dbms_output.put_line( count_rows( 'dual' ) )
exec dbms_output.put_line( count_rows( 'all_users' ) )
conn db_app_data/db_app_data
select * from t1;
exec dbms_output.put_line( count_rows( 't1' ) )
-- recreate the procedure in invoker mode
conn utils/utils
create or replace function count_rows( p_table_name in varchar2 )
return number
authid current_user
is
  l_count number;
begin
  execute immediate 'select count(*) from ' || p_table_name into l_count;
  return l_count;
end;
/
show errors;
conn db_app_data/db_app_data
exec dbms_output.put_line( count_rows( 't1' ) )
spool off
