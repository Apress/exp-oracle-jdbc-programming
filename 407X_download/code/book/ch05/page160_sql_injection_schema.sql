spool sql_injection_schema
set head on
set echo on
drop table user_info;
create table user_info
(
  username varchar2(15) not null,
  password varchar2(15 ) not null
);
begin
  for i in 1..10 
  loop
    insert into user_info( username, password ) 
    values( 'user'||i, 'password'||i );
  end loop;
end;
/
select username, password from user_info;
commit;
spool off
