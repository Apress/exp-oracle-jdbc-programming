package book.ch10.jpub;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import java.sql.SQLData;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import oracle.sql.STRUCT;
import oracle.jpub.runtime.MutableStruct;

public class Address implements SQLData
{
  public static final String _SQL_NAME = "BENCHMARK.ADDRESS";
  public static final int _SQL_TYPECODE = OracleTypes.STRUCT;

  private String m_line1;
  private String m_line2;
  private String m_street;
  private String m_city;
  private String m_state;
  private String m_zip;

  /* constructor */
  public Address()
  {
  }

  public Address(String line1, String line2, String street, String city, String state, String zip) throws SQLException
  {
    setLine1(line1);
    setLine2(line2);
    setStreet(street);
    setCity(city);
    setState(state);
    setZip(zip);
  }
  public void readSQL(SQLInput stream, String type)
  throws SQLException
  {
      setLine1(stream.readString());
      setLine2(stream.readString());
      setStreet(stream.readString());
      setCity(stream.readString());
      setState(stream.readString());
      setZip(stream.readString());
  }

  public void writeSQL(SQLOutput stream)
  throws SQLException
  {
      stream.writeString(getLine1());
      stream.writeString(getLine2());
      stream.writeString(getStreet());
      stream.writeString(getCity());
      stream.writeString(getState());
      stream.writeString(getZip());
  }

  public String getSQLTypeName() throws SQLException
  {
    return _SQL_NAME;
  }

  /* accessor methods */
  public String getLine1()
  { return m_line1; }

  public void setLine1(String line1)
  { m_line1 = line1; }


  public String getLine2()
  { return m_line2; }

  public void setLine2(String line2)
  { m_line2 = line2; }


  public String getStreet()
  { return m_street; }

  public void setStreet(String street)
  { m_street = street; }


  public String getCity()
  { return m_city; }

  public void setCity(String city)
  { m_city = city; }


  public String getState()
  { return m_state; }

  public void setState(String state)
  { m_state = state; }


  public String getZip()
  { return m_zip; }

  public void setZip(String zip)
  { m_zip = zip; }

}
