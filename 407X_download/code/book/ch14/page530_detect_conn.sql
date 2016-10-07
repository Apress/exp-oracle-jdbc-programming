spool detect_conn
set trimspool on
set echo on
set head on
column USERNAME format a10
column PROGRAM format a30
column TYPE format a10
column SERVER_PID format 99999
column SERVER format a15
select s.program, s.server, p.spid server_pid, s.username 
from v$session s, v$process p
where s.type = 'USER'
  and s.username != 'SYS'
  and p.addr(+) = s.paddr;
spool off
