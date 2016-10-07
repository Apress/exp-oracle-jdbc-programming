-- as system user
-- enable auditing

alter system set audit_trail=db scope=spfile;
-- shut down
-- check 
show parameter audit_trail;
-- audit connection
audit session;
select * from dba_audit_trail;
