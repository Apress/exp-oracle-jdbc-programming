spool iot_heap_select_runstats
set echo on
set doc off
delete iot;
delete heap;
exec insert_heap;
exec insert_iot;
exec runstats_pkg.rs_start
exec select_heap
exec runstats_pkg.rs_middle
exec select_iot
exec runstats_pkg.rs_stop( 50 )
set echo off
spool off
