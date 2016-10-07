spool audit_proxy
set echo on
audit connect by midtier on behalf of db_clerk1, db_manager1;
spool off
