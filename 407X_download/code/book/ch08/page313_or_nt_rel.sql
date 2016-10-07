spool or_nt_rel
set echo on
/* package to compare OR views, Nested tables and relatonal
   tables based approaches
 */
create or replace package or_nt_rel_pkg
as
  g_num_of_select_runs constant number := 10000;
  g_num_of_child_updates constant number := 10000;
  part_rel_id  number := 1;
  part_or_id  number := 1;
  part_nt_id  number := 1;

  procedure do_or_select;
  procedure do_nt_select;
  procedure do_rel_select;
  procedure do_or_insert( p_num_of_parents in int := 50,
    p_num_of_children in int := 500 );
  procedure do_nt_insert( p_num_of_parents in int := 50,
    p_num_of_children in int := 500 );
  procedure do_or_update;
  procedure do_or_child_update;
  procedure do_rel_update;
  procedure do_rel_child_update;
  procedure do_nt_update;
  procedure do_nt_child_update;
  procedure do_or_delete;
  procedure do_rel_delete;
  procedure do_nt_delete;
  procedure do_rel_bulk_insert( p_num_of_parents in int := 50, 
    p_num_of_children in int := 500 );
end;
/
show errors;

create or replace package body or_nt_rel_pkg
as
  procedure do_or_select
  as
    l_x number := 0;
  begin
    for i in 1..g_num_of_select_runs 
    loop 
      for x in ( select p.*
                 from  components_or_view c, TABLE (c.parts) p 
                 where p.part_id = i
               )
      loop
        l_x := l_x + 1;
      end loop;
    end loop;
    dbms_output.put_line( l_x );
  end do_or_select;

  procedure do_nt_select
  as
    l_x number := 0;
  begin
    for i in 1..g_num_of_select_runs 
    loop 
      for x in ( select p.*
                 from  components_nt c, TABLE (c.parts) p 
                 where p.part_id = i
               )
      loop
        l_x := l_x + 1;
      end loop;
    end loop;
    dbms_output.put_line( l_x );
  end do_nt_select;

  procedure do_rel_select
  as
    l_x number := 0;
  begin
    for i in 1..g_num_of_select_runs 
    loop 
      for x in ( select p.*
                 from  parts_rel p
                 where p.part_id = i
               )
      loop
        l_x := l_x + 1;
      end loop;
    end loop;
    dbms_output.put_line( l_x );
  end do_rel_select;

  procedure do_or_insert ( p_num_of_parents in int := 50,
    p_num_of_children in int := 500 )
  as
    l_part_type_tab part_type_tab;
    l_part_type part_type;
    l_part_or_id number := 1;
  begin
    l_part_type_tab := part_type_tab();
    l_part_type_tab.extend( p_num_of_children );

    for i in 1..p_num_of_parents
    loop
      for j in 1..p_num_of_children
      loop
        l_part_type_tab(j) :=
          part_type( i, l_part_or_id, 'part'||i||j, 'part desc '||i||j );
        l_part_or_id := l_part_or_id + 1;
      end loop;
      insert into components_or_view values
         ( i, 'component'||i, l_part_type_tab );
    end loop;
    commit;
  end;

  procedure do_nt_insert( p_num_of_parents in int := 50, 
    p_num_of_children in int := 500 )
  as
    l_part_type_tab part_type_tab;
    l_part_type part_type;
    l_part_or_id number := 1;
  begin
    l_part_type_tab := part_type_tab();
    l_part_type_tab.extend( p_num_of_children );
   
    for i in 1..p_num_of_parents
    loop
      for j in 1..p_num_of_children
      loop
        l_part_type_tab(j) :=
          part_type( i, l_part_or_id, 'part'||i||j, 'part desc '||i||j );
        l_part_or_id := l_part_or_id + 1;
      end loop;
      insert into components_nt values
      ( i, 'component'||i, l_part_type_tab );
    end loop;
    commit;
  end;

  procedure do_or_update
  as
  begin
    update components_or_view
    set component_name = component_name || ' or update';
  end;

  procedure do_or_child_update
  as
    l_component_id components_or_view.component_id%type;
  begin
    for i in 1..g_num_of_child_updates 
    loop
      l_component_id := mod(i,500);
      update table
        ( select parts
         from components_or_view
         where component_id = l_component_id
       )
      set part_desc = part_desc || ' updated'
      where part_id = i;
    end loop;
  end;

  procedure do_rel_update
  as
  begin
    update components_rel
    set component_name = component_name || ' or update';
  end;

  procedure do_rel_child_update
  as
  begin
    for i in 1..g_num_of_child_updates 
    loop
      update parts_rel
      set part_desc = part_desc || ' updated'
      where part_id = i;
    end loop;
  end;

  procedure do_nt_update
  as
  begin
    update components_nt
    set component_name = component_name || ' or update';
  end;

  procedure do_nt_child_update
  as
  begin
    for i in 1..g_num_of_child_updates 
    loop
      execute immediate 'update /*+ nested_table_get_refs */ parts_nt' ||
      ' set part_desc = part_desc|| :1 ' ||
      ' where part_id = :2 ' 
      using 'updated', i;
    end loop;
  end;

  procedure do_or_delete
  as
  begin
    delete components_or_view;
  end;

  procedure do_rel_delete
  as
  begin
    delete components_rel;
  end;

  procedure do_nt_delete
  as
  begin
    delete components_nt;
  end;

  procedure do_rel_bulk_insert( p_num_of_parents in int := 50, 
    p_num_of_children in int := 500 )
  as
    l_tmp_comp  number;
    type array is table of parts_rel%rowtype index by binary_integer;
    l_childdata array;
    l_part_rel_id  number := 1;
  begin
    for i in 1..p_num_of_parents
    loop
      insert into components_rel values ( i, 'component'||i );
      for j in 1..p_num_of_children
      loop
        l_childdata(j).component_id := i;
        l_childdata(j).part_id := l_part_rel_id;
        l_childdata(j).part_name := 'part'||i||j;
        l_childdata(j).part_desc := 'part desc' || i||j;
        l_part_rel_id := l_part_rel_id + 1;
      end loop;
      forall X in 1 .. p_num_of_children
        insert into parts_rel values l_childdata(X);
     end loop;
     commit;
  end;
end;
/
show errors;
spool off
