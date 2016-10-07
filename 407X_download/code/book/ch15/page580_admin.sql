spool admin
set echo on
drop user admin cascade;
create user admin identified by admin default tablespace users;
grant dba to admin;
spool off
