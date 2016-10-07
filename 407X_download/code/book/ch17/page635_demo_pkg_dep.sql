spool demo_pkg_dep
alter session set nls_date_format='YYYY-MM-DD HH24:MI:SS';
set echo on
set head on
set serveroutput on
create or replace procedure p1 
as
begin
  dbms_output.put_line( 'p1' );
end;
/
show errors;
create or replace procedure p2 
as
begin
  p1;
  dbms_output.put_line( 'p2' );
end;
/
show errors;
create or replace procedure p3 
as
begin
  p2;
  dbms_output.put_line( 'p3' );
end;
/
show errors;
select object_name, object_type, status 
from all_objects
where object_name in( 'P1', 'P2', 'P3' )
  and owner = 'BENCHMARK';

-- modify p1 and recompile it
create or replace procedure p1 
as
begin
  dbms_output.put_line( 'p1 modified' );
end;
/
show errors;
select object_name, object_type, status 
from all_objects
where object_name in( 'P1', 'P2', 'P3' )
  and owner = 'BENCHMARK';

-- execute p2, p2 will be recompiled, p3 will still remain invalid
exec p2
select object_name, object_type, status 
from all_objects
where object_name in( 'P1', 'P2', 'P3' )
  and owner = 'BENCHMARK';

-- putting the procedures in a package
create or replace package pkg1 as
  procedure p1;
end;
/
show errors;
create or replace package body pkg1 as
  procedure p1
  as
  begin
    dbms_output.put_line( 'p1' );
  end;
end;
/
show errors;
create or replace package pkg2 as
  procedure p2;
end;
/
show errors;
create or replace package body pkg2 as
  procedure p2
  as
  begin
    dbms_output.put_line( 'p2' );
    pkg1.p1;
  end;
end;
/
show errors;
create or replace package pkg3 as
  procedure p3;
end;
/
show errors;
create or replace package body pkg3 as
  procedure p3
  as
  begin
    dbms_output.put_line( 'p3' );
    pkg2.p2;
  end;
end;
/
show errors;
select object_name, object_type, status 
from all_objects
where object_name in( 'PKG1', 'PKG2', 'PKG3' )
  and owner = 'BENCHMARK';

-- now modify p1 and recompile the package body for p1.
create or replace package body pkg1 as
  procedure p1
  as
  begin
    dbms_output.put_line( 'p1 modified' );
    pkg1.p1;
  end;
end;
/
show errors;
select object_name, object_type, status 
from all_objects
where object_name in( 'PKG1', 'PKG2', 'PKG3' )
  and owner = 'BENCHMARK';
-- finally modify p1 package specification and recompile 
create or replace package pkg1 as
  procedure p1;
  procedure p11;
end;
/
show errors
create or replace package body pkg1 as
  procedure p1
  as
  begin
    dbms_output.put_line( 'p1 pacakge and body modified' );
  end;
  procedure p11
  as
  begin
    dbms_output.put_line( 'p11 pacakge and body modified' );
  end;
end;
/
show errors;
select object_name, object_type, status , last_ddl_time
from all_objects
where object_name in( 'PKG1', 'PKG2', 'PKG3' )
  and owner = 'BENCHMARK';


spool off
