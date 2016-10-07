spool iot_heap
set echo on
set doc off
drop table heap;
create table heap 
(
  a varchar2(30),
  b varchar2(30),
  c varchar2(30),
  constraint heap_pk primary key (a, b )
);
drop table iot;
create table iot 
(
  a varchar2(30),
  b varchar2(30),
  c varchar2(30),
  constraint iot_pk primary key (a, b )
)
organization index;

create or replace procedure insert_heap
is
begin
  for i in 1 .. 100 loop
    for j in 1 .. 1000 loop
      insert into heap values ( 'a'||i, 'a'||i||j, 'cccc' );
    end loop;
  end loop;
  commit;
end;
/
show errors;
create or replace procedure insert_iot
is
begin
  for i in 1 .. 100 loop
    for j in 1 .. 1000 loop
      insert into iot values ( 'a'||i, 'a'||i||j, 'cccc' );
    end loop;
  end loop;
  commit;
end;
/
create or replace procedure select_heap
is
  l_a heap.a%type;
begin
  for i in 1 .. 100 loop
    l_a := 'a'||i;
    for x in (select * from heap where a=l_a) loop
      null;
    end loop;
  end loop;
end;
/
create or replace procedure select_iot
is
  l_a iot.a%type;
begin
  for i in 1 .. 100 loop
    l_a := 'a'||i;
    for x in (select * from iot where a=l_a) loop
      null;
    end loop;
  end loop;
end;
/
show errors;
begin
  dbms_stats.gather_table_stats( 
    ownname => 'BENCHMARK',
    tabname => 'HEAP' );
  dbms_stats.gather_table_stats( 
    ownname => 'BENCHMARK',
    tabname => 'IOT' );
end;
/
begin
  insert_heap;
  insert_iot;
end;
/
begin
  select_heap;
  select_iot;
end;
/
begin
  execute immediate 'alter session set timed_statistics=true';
  execute immediate 'alter session set events ''10046 trace name context forever, level 12''';
  select_heap;
  select_iot;
  execute immediate 'alter session set events ''10046 trace name context off''';
end;
/
set echo off
