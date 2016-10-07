drop table demo_debug_table;
create table demo_debug_table ( text varchar2(15) );
begin
  for i in 1..5
  loop
    insert into demo_debug_table values( 'demo_debug ' || i );
  end loop;
end;
/

create or replace procedure demo_debug
is
  l_counter number := 0;
begin
  debug.f( 'Inside procedure demo_debug ' );
  for i in ( select text from demo_debug_table )
  loop
    l_counter := l_counter + 1;
    debug.f( 'loop counter %d has value %d', l_counter, i.text );
  end loop;
  debug.f( 'Exiting procedure demo_debug ' );
  
end;
/
show errors;
exec debug.init( p_modules => 'ALL' )
exec debug.enable( debug.DUMP_IN_TRACE_FILES_ONLY );
exec demo_debug
