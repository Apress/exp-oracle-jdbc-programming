package book.ch11.jpub;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import oracle.jpub.runtime.MutableStruct;

public class Address implements ORAData, ORADataFactory
{
  public static final String _SQL_NAME = "BENCHMARK.ADDRESS";
  public static final int _SQL_TYPECODE = OracleTypes.STRUCT;

  protected MutableStruct _struct;

  private static int[] _sqlType =  { 12,12,12,12,12,12 };
  private static ORADataFactory[] _factory = new ORADataFactory[6];
  protected static final Address _AddressFactory = new Address();

  public static ORADataFactory getORADataFactory()
  { return _AddressFactory; }
  /* constructors */
  protected void _init_struct(boolean init)
  { if (init) _struct = new MutableStruct(new Object[6], _sqlType, _factory); }
  public Address()
  { _init_struct(true); }
  public Address(String line1, String line2, String street, String city, String state, String zip) throws SQLException
  { _init_struct(true);
    setLine1(line1);
    setLine2(line2);
    setStreet(street);
    setCity(city);
    setState(state);
    setZip(zip);
  }

  /* ORAData interface */
  public Datum toDatum(Connection c) throws SQLException
  {
    return _struct.toDatum(c, _SQL_NAME);
  }


  /* ORADataFactory interface */
  public ORAData create(Datum d, int sqlType) throws SQLException
  { return create(null, d, sqlType); }
  protected ORAData create(Address o, Datum d, int sqlType) throws SQLException
  {
    if (d == null) return null; 
    if (o == null) o = new Address();
    o._struct = new MutableStruct((STRUCT) d, _sqlType, _factory);
    return o;
  }
  /* accessor methods */
  public String getLine1() throws SQLException
  { return (String) _struct.getAttribute(0); }

  public void setLine1(String line1) throws SQLException
  { _struct.setAttribute(0, line1); }


  public String getLine2() throws SQLException
  { return (String) _struct.getAttribute(1); }

  public void setLine2(String line2) throws SQLException
  { _struct.setAttribute(1, line2); }


  public String getStreet() throws SQLException
  { return (String) _struct.getAttribute(2); }

  public void setStreet(String street) throws SQLException
  { _struct.setAttribute(2, street); }


  public String getCity() throws SQLException
  { return (String) _struct.getAttribute(3); }

  public void setCity(String city) throws SQLException
  { _struct.setAttribute(3, city); }


  public String getState() throws SQLException
  { return (String) _struct.getAttribute(4); }

  public void setState(String state) throws SQLException
  { _struct.setAttribute(4, state); }


  public String getZip() throws SQLException
  { return (String) _struct.getAttribute(5); }

  public void setZip(String zip) throws SQLException
  { _struct.setAttribute(5, zip); }

}
