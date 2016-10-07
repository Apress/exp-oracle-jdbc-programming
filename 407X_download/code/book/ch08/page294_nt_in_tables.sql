spool nt_in_tables
set echo on

drop table parts_rel;
drop table components_rel;

/* create the parent table */
create table components_rel
(
  component_id number primary key,
  component_name varchar2(50)
);

/* create the child table  - for simplicity, assume one part can
   belong to utmost one component.*/
create table parts_rel
(
  component_id number references components_rel on delete cascade,
  part_id   number primary key,
  part_name varchar2(50),
  part_desc varchar2(500)
);

/* index the foreign key. Almost always this
   index is required in real life applications -
   see http://asktom.oracle.com/~tkyte/unindex/index.html
   for more details
 */
create index parts_id_rel_idx on parts_rel(component_id );

/* Solution using nested tables */
drop table components_nt;
drop type part_type_tab;
drop type part_type;

/* create the object type for the nested table type */

create or replace type part_type as object
(
  component_id number,
  part_id number,
  part_name varchar2(50),
  part_desc varchar2(500)
);
/
/* create the nested table type */
create or replace type part_type_tab as table of part_type;
/

/* Finally, create the components table called components_nt.
  This time we store the parts as a nested table in the column
  parts. The child table itself is given a name of "parts_nt"
  in the nested table clause at the end of the table creation
  statement
 */
  
create table components_nt
(
  component_id number primary key,
  component_name varchar2(50),
  parts part_type_tab
)
nested table parts store as parts_nt;

/* Let us try to create a foreign key constraint on the nested
   table. This won't work because nested tables do not allow
   referential integrity constraints.
*/
alter table parts_nt add constraint parts_nt_fk foreign key(component_id) 
references components_nt(component_id);

/* simple insert into a table with a nested table column */
insert into components_nt values
( 1, 'component 1', 
  part_type_tab( (part_type(1,1, 'part1', 'part1 description')),
                 (part_type(1,2, 'part2', 'part2 description') ) 
               ) 
);
set head on
/* now do a simple select - selecting all columns */
select * from components_nt;

/* now for a select that "unnests" the nested table column 
   and gives its individual column values. This one needs to
   join with the parent table.
*/
column part_name format a7
column part_desc format a20
select p.*
from components_nt c, TABLE( c.parts ) p;

/* Now for a way to get the nested table column values *without*
   joining with the parent table 
*/

select /*+ nested_table_get_refs */ part_id, part_name, part_desc
from parts_nt; 

/* Alas - the nested_table_get_refs hint does not work (in 10.1.0.2.0)
   if you put the code in an anonymous block or procedure due to a bug. 
   In such cases, till the bug is resolved, you can use the "unnesting" 
   approach.
 */
declare
  l_part_name varchar2(50);
begin
  select /*+ nested_table_get_refs */ part_name
  into l_part_name
  from parts_nt;
end;
/

/* Or you could also use the native dynamic sql */

declare
  l_part_name varchar2(50);
begin
  execute immediate 'select /*+ nested_table_get_refs */ part_name' ||
  ' from parts_nt where part_id = :1' into l_part_name
   using 1;
  dbms_output.put_line('part_name = ' || l_part_name );
end;
/

/* Now to insert with a select clause. We duplicate the
  parts of first component into the second component as
  an insert. 
  MULTISET keyword is used to specify that the subquery
  inside can return one row ( without it Oracle will give
  an error if the subquery returns more than one row.)

  CAST keyword casts a value from one type to another; we use
  here to cast the resulting query value to our varray type.
*/

insert into components_nt 
select 2, 'component 2',
  cast
  ( 
    multiset
    (
      select /*+ nested_table_get_refs */ 2,3, part_name, part_desc
      from parts_nt
      where component_id = 1
    ) as part_type_tab
  )
from components_nt c
where c.component_id = 1;

/* Another insert example: Adding one more part to 
   the parts of component_id 1
   Again this does not work in an anonymous block or
   procedure as of 10.1.0.2.0. and you can use native
   dynamic sql as an alternative.
*/

insert /*+ nested_table_get_refs */ into parts_nt
values ( 1,4, 'part3', 'part3 description');

/* Updating a column in nested table 
   Again this does not work in an anonymous block or
   procedure as of 10.1.0.2.0. and you can use native
   dynamic sql as an alternative.
*/
update /*+ nested_table_get_refs */ parts_nt
set part_desc = 'part3 description updated'
where part_id = 4;
*/

/* Deleting a row from the nested table 
   Again this does not work in an anonymous block or
   procedure as of 10.1.0.2.0. and you can use native
   dynamic sql as an alternative.
*/

delete /*+ nested_table_get_refs */ 
from parts_nt
where component_id = 1
and part_name = 'part1';

spool off
