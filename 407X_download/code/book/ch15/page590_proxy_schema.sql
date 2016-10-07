spool proxy_schema
set echo on
conn admin/admin
drop user midtier;
create user midtier identified by midtier;
grant create session to midtier;
-- alter the end users to disable all roles except the roles 
-- required for the application to work
-- restrict all roles except the ones required
alter user db_manager1 default role all except manager_role, clerk_role;
alter user db_clerk1 default role all except clerk_role;
-- allow connecting through proxy account
alter user db_manager1 grant connect through midtier with role manager_role, clerk_role;
alter user db_clerk1 grant connect through midtier with role clerk_role;

-- show proxy user info
column proxy format a10
column client format a15
set head on
select * from proxy_users;
spool off
