spool nt_examples
set echo on
create or replace type varchar_nt as table of varchar2(50);
/
create or replace type person_nt as table of person;
/
declare
  l_nt_variable varchar_nt;
begin
  /* initializing the table with some values */
  l_nt_variable := varchar_nt( 'A', 'B','C');
  /* An implicit select that uses the TABLE construct
     to select and then display it - notice the use of
     column_value to get the value of the built-in
     type varchar2
   */
  for i in ( select nt.column_value
             from table( l_nt_variable) nt )
  loop
    dbms_output.put_line( 'i = ' || i.column_value );
  end loop;
end;
/
declare
  l_person_nt person_nt;
begin
  /* initializing the table with some values */
  l_person_nt := person_nt
                 ( person( 'Joe', sysdate-23*365),
                   person( 'John Doe', sysdate-25*365),
                   person( 'Tim Drake', sysdate-27*365) 
                 );
  for i in(
            select nt.name, nt.date_of_birth
            from table( l_person_nt ) nt
            where nt.name like 'J%'
          )
  loop
    dbms_output.put_line( i.name || ', ' || i.date_of_birth );
  end loop;
end;
/
spool off
