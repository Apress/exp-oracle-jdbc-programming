spool person_creation
set echo on
declare
  l_person_obj person;
begin
  /* use constructor that takes all arguments */
  l_person_obj := person('Varun', sysdate - (12*365) );
  /* print out the value in the object created */
  dbms_output.put_line ( 'Name = ' || l_person_obj.name );
  dbms_output.put_line ( 'Name (using getter method) = ' || 
    l_person_obj.get_name() );
  dbms_output.put_line ( 'Date of birth = ' || 
    l_person_obj.date_of_birth );
  person.describe;
end;
/
spool off
