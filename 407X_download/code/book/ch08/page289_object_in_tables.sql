spool object_in_tables
set echo on
drop table person_table;
create table person_table of person;
desc person_table
insert into person_table( name, date_of_birth)
values( 'Joe Panda', sysdate -20*365);
commit;
select * from person_table;
drop table contacts;
create table contacts
(
  contact_person person,
  contact_type varchar2(20)
);

insert into contacts( contact_person, contact_type )
values( person('Joe Panda', sysdate -20*365), 'PERSONAL' );
select * from contacts;

spool off
