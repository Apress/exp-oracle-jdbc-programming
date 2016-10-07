spool or_view
set echo on
delete components_rel;
delete parts_rel;
--populate relational table schema
insert into components_rel( component_id, component_name)
values( 1, 'component1' );
insert into components_rel( component_id, component_name)
values( 2, 'component2' );

insert into parts_rel( component_id, part_id, part_name, part_desc )
values( 1, 1, 'part11', 'part 11 desc' );
insert into parts_rel( component_id, part_id, part_name, part_desc )
values( 1, 2, 'part12', 'part 12 desc' );
insert into parts_rel( component_id, part_id, part_name, part_desc )
values( 2, 3, 'part21', 'part 21 desc' );
insert into parts_rel( component_id, part_id, part_name, part_desc )
values( 2, 4, 'part22', 'part 22 desc' );
set head on
column component_name format a14
column part_name format a10
column part_desc format a20
column component_id format 9999
column part_id format 9999
select * from components_rel;
select * from parts_rel;
/* Object Relational View based solution begins */

/* First create an object type for the OR View */
create or replace type components_nt_tab as object
(
  component_id number,
  component_name varchar2(50),
  parts part_type_tab
);
/

/* Then create the OR View equivalent of the 
   table components_nt. We call this view as 
   components_or_view. Notice how we
   gave the object identifier to be the "component_id" column
   to indicate that the component_id column uniquely identifies one
   row in the object-relational view.
 */
  
create or replace view components_or_view of
 components_nt_tab with object identifier( component_id )
 as
 select component_id, component_name, 
   cast
   (
     multiset
     (
       select component_id, part_id, part_name, part_desc
       from parts_rel p
       where p.component_id = c.component_id
     )
     as part_type_tab
   )
 from components_rel c;

/* now do a simple select - selecting all columns from the OR view*/
select * from components_or_view;
/* and do the same select - this time from the table with nested table column*/
select * from components_nt;
-- select using table syntax
select p.*
from  components_or_view c, TABLE (c.parts) p;

/* Try to update the OR view - this one will work */
update components_or_view
set component_name = component_name || 'modified';

/* inserts, updates and deletes can be done on the child table 
   directly as an alternative. Let us now see if we can
   find Object equivalents of these DMLs.
*/

/* select equivalent - note that we can no longer use the
   nested_table_get_refs hint - as there is no nested table
   in this case! So we have to use the TABLE construct for our
   selects.
  */
select p.*
from  components_or_view c, TABLE (c.parts) p;

/* updating rows corresponding to exactly one parent in case
   of nested table solution can be done as follows (without
   using the nested_table_get_refs hint). The following works
   if and only if the internal select returns exactly one row!
 */
update table
       ( 
         select parts
         from components_nt
         where component_id = 1
       )
set part_desc = part_desc || ' nt';

/* now let us try the above update with the or view. This will fail!*/
update table
       ( 
         select parts
         from components_or_view
         where component_id = 1
       )
set part_desc = part_desc || ' or';
/* To make the above update work, we need to use an instead of trigger
   on the OR view. Following is the trigger on the nested table parts_rel
   of the OR view.
*/

create or replace trigger parts_io_update
instead of update on nested table parts of components_or_view
begin
  if( :new.part_id = :old.part_id ) then
    update parts_rel
    set part_name = :new.part_name, part_desc =  :new.part_desc
    where part_id = :new.part_id;
  else
    raise_application_error( -20001, 
      'Updating the primary key part_id is not allowed' );
  end if;
end;
/
show errors;

/* Now do the same update - it should work */
update table
       (
         select parts
         from components_or_view
         where component_id = 1
       )
set part_desc = part_desc || ' or';

/* However, the following update that changes the entire parts 
   of the view components_or_view will not work! This is because
   we still do not have a trigegr on the OR view per se.
*/
declare
  l_parts part_type_tab;
begin
  select parts 
  into l_parts
  from components_or_view
  where component_id = 1;

  for i in 1 .. l_parts.count 
  loop
    l_parts(i).part_desc := l_parts(i).part_desc || 'changed';
  end loop;

  update components_or_view
  set parts = l_parts
  where component_id = 1;
end;
/
/* So now we create the trigger that finally completes
   enabling of any updates correctly on components_or_view.
   NOTE: The logic of this trigger is based on the information
   obtained from Chapter "Using Object Relational Features" of
   the book "Expert one on one Oracle", written by Tom Kyte. 
 */
   
create or replace trigger components_or_view_io_update
instead of update on  components_or_view 
begin
  --dbms_output.put_line( 'old component_id: ' ||:old.component_id );
  --dbms_output.put_line( 'new component_id: ' ||:new.component_id );
  case 
    when( updating('COMPONENT_NAME') ) then
      update components_rel
      set component_name = (:new.component_name )
      where component_id = :old.component_id;
    when( updating( 'PARTS' ) ) then
      /* First remove all records from parts such that they
         were in the :old set but are not there in the :new 
         set
       */
       
      delete from parts_rel
      where part_id in
      ( 
        select part_id
        from TABLE( cast( :old.parts as part_type_tab ) )
        minus
        select part_id
        from TABLE( cast( :new.parts as part_type_tab ) )
      );
      --dbms_output.put_line( 'deleted ' || sql%rowcount );
      /* Now update those records in parts such that their
         part_id is their in both :old and :new set and 
         theyhave undergone a change in the remaining
         columns.
       */
      update parts_rel rp
      set ( component_id, part_name, part_desc ) =
      (
        select :new.component_id, part_name, part_desc 
        from TABLE( cast( :new.parts as part_type_tab ) ) np
        where np.part_id = rp.part_id
      )
      where rp.part_id in
      (
        select part_id
        from
        (
          select *
          from TABLE( cast( :old.parts as part_type_tab ) )
          minus
          select *
          from TABLE( cast( :new.parts as part_type_tab ) )
        )
      );
      --dbms_output.put_line( 'updated ' || sql%rowcount );
      /* Finally insert any records in parts that were newly
         added to the nested table column */
      insert into parts_rel
      select component_id, part_id, part_name, part_desc
      from 
      (
        select * 
        from TABLE( cast( :new.parts as part_type_tab ) ) p
        where part_id in
        (
          select part_id
          from TABLE( cast( :new.parts as part_type_tab ) )
          minus
          select part_id
          from TABLE( cast( :old.parts as part_type_tab ) )
        )
      );
      --dbms_output.put_line( 'inserted ' || sql%rowcount );
  end case;
end;
/
show errors;

/* Now the earlier update should work */
declare
  l_parts part_type_tab;
begin
  select parts
  into l_parts
  from components_or_view
  where component_id = 1;

  for i in 1 .. l_parts.count
  loop
    l_parts(i).part_desc := l_parts(i).part_desc || 'changed';
  end loop;

  update components_or_view
  set parts = l_parts
  where component_id = 1;
end;
/

/* Some more updates to test our trigger components_or_view_io_update
 */
declare
  l_parts part_type_tab;
begin
  select parts
  into l_parts
  from components_or_view
  where component_id = 1;

  l_parts.extend(1);
  l_parts( l_parts.count) := part_type(1, 5, 'part13', 
    'part13 description');
  update components_or_view
  set parts = l_parts
  where component_id = 1;
end;
/

/* Now for inserts */

/* The following insert will fail */
insert into components_or_view values
(
  3, 'component 3', 
  part_type_tab(part_type(3,6,'part 11', 'part 11 description'))
);

/* So we write an instead of insert trigger on the components_or_view
   itself */

create or replace trigger components_or_view_io_insert
instead of insert on  components_or_view 
begin
  -- First insert into the parent table
  insert into components_rel( component_id, component_name )
  values ( :new.component_id, :new.component_name );

  -- then insert into the child table
  insert into parts_rel
  select * 
  from TABLE( cast( :new.parts as part_type_tab ) )
  where part_id not in
  ( select part_id from parts_rel );
end;
/
show errors;
/* Now the insert should work */

insert into components_or_view values
(
  3, 'component 3', 
  part_type_tab(part_type(3,6,'part 11', 'part 11 description'))
);

/* How about if you just want to insert data into the parts
   table? One way is to use the update (our update trigger
   takes care of it. - however what if you want to use the
   insert only on parts?
  */

insert into
  TABLE
  ( 
    select c.parts
    from components_or_view c
    where c.component_id=1
  )
values
(
  1, 7, 'part 17','part 17 description'
);

/* Well, you need another instead of trigger on insert - this 
  time it will be on the nested table column - parts.
*/
create or replace trigger parts_io_insert
instead of insert on nested table parts of components_or_view
begin
  -- Insert into the underlying relational child table 
  insert into parts_rel ( component_id, part_id, part_name, part_desc )
  values( :new.component_id, :new.part_id, :new.part_name, :new.part_desc );
  
end;
/
/* Now the same insert should work */

insert into
  TABLE
  ( 
    select c.parts
    from components_or_view c
    where c.component_id=1
  )
values
(
  1, 7, 'part 17','part 17 description'
);
/* Finally - deletes */

/* Let us try to delete a part from the component 1 */

delete TABLE
  (
    select c.parts
    from components_or_view c
    where c.component_id=1
  ) t
where t.part_id = 1;

/* So we need an instead of trigger on the nested table column 
   parts to make the above work
*/
create or replace trigger parts_io_delete
instead of delete on nested table parts of components_or_view
begin
  -- delete from the underlying relational child table 
  delete parts_rel 
  where part_id = :old.part_id;
  --dbms_output.put_line( sql%rowcount || ' rows deleted');
end;
/
show errors;

/* How about if we issue a delete based on the non-nested table columns?*/
delete components_or_view 
where component_id = 1;

/* Above fails. So we need a trigger on the non-nested table columns
  of the components_or_view
*/

create or replace trigger components_or_view_io_delete
instead of delete on  components_or_view 
begin
  -- First delete from the child table
  delete parts_rel
  where part_id in
  (
    select part_id 
    from TABLE( cast( :old.parts as part_type_tab ) )
  );

  -- then delete from the parent table
  delete components_rel
  where component_id = :old.component_id;
end;
/
show errors;
spool off
