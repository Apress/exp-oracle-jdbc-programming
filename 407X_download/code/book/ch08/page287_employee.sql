set echo on
spool employee
create or replace type employee under person 
(
  employee_id number,
  member function get_employee_id return number, 
  member procedure set_employee_id( p_employee_id in number ),
  overriding member function get_name return varchar2
)
not final;
/
show errors;

create or replace type body employee 
as 
  member function get_employee_id return number
  is
  begin
    return employee_id;
  end;

  member procedure set_employee_id( p_employee_id in number )
  is
  begin
    employee_id := p_employee_id;
  end;
  -- an example of overriding

  overriding member function get_name return varchar2
  is
  begin
    dbms_output.put_line( 'In employee get_name() method');
    return name;
  end;

end;
/
show errors;
declare 
  l_employee employee;
begin
  l_employee := employee( 'John the King', sysdate - 43*365, 1);
  dbms_output.put_line( l_employee.get_name() );
end;
/
spool off
set echo off
