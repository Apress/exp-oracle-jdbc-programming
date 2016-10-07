spool nt_storage
set echo on
set head on
column name format a20
column length format 99999999

/* Let us see how Oracle internally stores the table
   components_nt. For this query to work
   you need select privileges on sys.col$.
 */

select name, length
from sys.col$ c, user_objects uo
where uo.object_id = c.obj#
  and uo.object_name = 'COMPONENTS_NT';

column component_name format a20
column component_id format 99999999
select component_id, SYS_NC0000300004$
from components_nt; 

/* Now let us see how Oracle internally stores the
   nested table parts_nt itself.
*/
select name, length
from sys.col$ c, user_objects uo
where uo.object_id = c.obj#
  and uo.object_name = 'PARTS_NT';

/* So what are the hidden columns nested_table_id, and 
   sys_nc_rowinfo$? */
column nested_table_id format a32
column sys_nc_rowinfo$ format a50
select /*+ nested_table_get_refs */ nested_table_id, 
       sys_nc_rowinfo$
from parts_nt; 

/* Can we join the nested table with its parent? */

column part_name format a10
column part_desc format a20
select c.component_id, p.part_name, p.part_desc 
from components_nt c, 
     ( select /*+ nested_table_get_refs */ part_id, part_name, part_desc, nested_table_id
       from parts_nt ) p
where c.SYS_NC0000300004$ = p.nested_table_id;

/* And an equivalent select using the TABLE construct */

select c.component_id, p.part_name, p.part_desc 
from components_nt c, TABLE( parts) p;

column table_name format a13
column constraint_name format a20
column constraint_type_desc format a15
column index_name format a13
column column_name format a13
set head on
/* Now for constraints and index columns for COMPONENTS_NT*/
select c.table_name,
       c.constraint_name, 
       case  
         when c.constraint_type = 'P' 
         then 'Primary Key'
         when c.constraint_type = 'U' 
         then 'Unique Key'
       end constraint_type_desc, 
       i.index_name, i.column_name
from all_ind_columns i, user_constraints c
where i.index_name = c.index_name
  and c.table_name in( 'COMPONENTS_NT', 'PARTS_NT' );

create index parts_nt_idx on parts_nt(nested_table_id);
spool off
