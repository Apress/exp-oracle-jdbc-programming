set echo on
spool person
create or replace type person as object
(
  name varchar2(50),
  date_of_birth date,
  constructor function person return self as result, 
  constructor function person( name in varchar2, 
    date_of_birth in date ) 
    return self as result, 
  member function get_name return varchar2, 
  member procedure set_name( p_name in varchar2 ),
  member function get_date_of_birth return date, 
  member procedure set_date_of_birth( p_date_of_birth in date ),
  static procedure describe
)
not final;
/
show errors;
create or replace type body person 
as 
  constructor function person return self as result
  is
  begin
    self.name := null;
    self.date_of_birth := null;
    return;
  end;

  constructor function person( name in varchar2, 
    date_of_birth in date ) 
    return self as result
  is
  begin
    self.name := name;
    self.date_of_birth := date_of_birth;
    return;
  end;

  member function get_name return varchar2 
  is
  begin
    return name;
  end;

  member procedure set_name( p_name in varchar2 )
  is
  begin
    name := p_name;
  end;

  member function get_date_of_birth return date 
  is
  begin
    return date_of_birth;
  end;

  member procedure set_date_of_birth( p_date_of_birth in date )
  is
  begin
    date_of_birth := p_date_of_birth;
  end;

  static procedure describe
  is
  begin
    dbms_output.put_line ( 'This is a simple Oracle object type that encapsulates a person.');
  end;
end;
/
show errors;
spool off
set echo off
