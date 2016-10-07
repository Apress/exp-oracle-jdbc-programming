spool audit_sel1
set echo on
set head on
column proxied_user format a15
column proxy_user format a10
column action_name format a10
column comment_text format a20
select a.username proxied_user, b.username proxy_user,
       a.action_name, a.comment_text
from dba_audit_trail a, dba_audit_trail b
where a.proxy_sessionid = b.sessionid;
spool off
