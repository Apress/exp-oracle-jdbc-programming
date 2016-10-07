spool show_blob
set echo on
set head on
select utl_raw.cast_to_varchar2( blob_col) blob_col
from blob_table;
spool off

