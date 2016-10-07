import java.util.Date;
class Person
{
  private String _name;
  private Date _dateOfBirth;
  public Person()
  {
  }
  public Person( String name, Date dateOfBirth )
  {
    _name = name;
    _dateOfBirth = dateOfBirth;
  }
  public String getName()
  {
    return _name;
  }
  public void setName( String name )
  {
    _name = name;
  }
  public Date getDateOfBirth()
  {
    return _dateOfBirth;
  }
  public void setDateOfBirth( Date dateOfBirth )
  {
    _dateOfBirth = dateOfBirth;
  }
  public static void describe()
  {
    System.out.println ( "This is a simple Java class that encapsulates a person." ); 
  }
  public static void main( String[] args )
  {
    Person person = new Person( "Varun", new Date() );
    System.out.println ( person.getName() + ", " + person.getDateOfBirth() ); 
  }
}
