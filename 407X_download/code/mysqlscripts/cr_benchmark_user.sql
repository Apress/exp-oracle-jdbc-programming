create user benchmark identified by benchmark default tablespace users quota
unlimited on users;

grant create any directory,
      create session,
      create table,
      create view,
      create synonym,
      create materialized view,
      create procedure,
      create trigger,
      create sequence,
      create type to benchmark;

grant select on v_$session_cursor_cache  to benchmark;
grant select on v_$sesstat to benchmark;
grant select on v_$open_cursor  to benchmark;
grant select on v_$sql  to benchmark;
grant select on  v_$sqlarea to benchmark;
grant create any context to benchmark;
grant drop any context to benchmark;
grant select on sys.col$ to benchmark; -- query data dict
grant select on sys.dba_segments to benchmark; -- for block dump
grant select on v_$process to benchmark;
grant select on v_$session to benchmark;
grant select on v_$statname to benchmark;
grant select on v_$mystat to benchmark;
grant select on v_$latch to benchmark;
grant select on v_$waitstat to benchmark;
grant select on dba_segments to benchmark;
grant select on dba_tablespaces to benchmark;
grant select on dba_objects to benchmark;
grant select on v_$transaction to benchmark;

grant alter session to benchmark; -- to enable trace
grant alter system to benchmark; -- for block dump
grant execute on dbms_alert to benchmark;
grant execute on dbms_lock to benchmark;
grant execute on dbms_metadata to benchmark;
grant execute on dbms_alert to benchmark;
grant execute on dbms_pipe to benchmark;
grant select on v_$timer to benchmark; -- for rs package
grant select on v_$parameter to benchmark; -- for debug package
grant drop any directory to benchmark;
-- for dbms_trace
grant select on plsql_trace_runnumber to benchmark; 
grant select on plsql_trace_events to benchmark; 

