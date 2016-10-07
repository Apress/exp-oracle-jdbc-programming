package book.ch11.jpub;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.Datum;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.jpub.runtime.MutableArray;

public class AddressList implements ORAData, ORADataFactory
{
  public static final String _SQL_NAME = "BENCHMARK.NESTED_TABLE_OF_ADDRESSES";
  public static final int _SQL_TYPECODE = OracleTypes.ARRAY;

  MutableArray _array;

private static final AddressList _AddressListFactory = new AddressList();

  public static ORADataFactory getORADataFactory()
  { return _AddressListFactory; }
  /* constructors */
  public AddressList()
  {
    this((Address[])null);
  }

  public AddressList(Address[] a)
  {
    _array = new MutableArray(2002, a, Address.getORADataFactory());
  }

  /* ORAData interface */
  public Datum toDatum(Connection c) throws SQLException
  {
    return _array.toDatum(c, _SQL_NAME);
  }

  /* ORADataFactory interface */
  public ORAData create(Datum d, int sqlType) throws SQLException
  {
    if (d == null) return null; 
    AddressList a = new AddressList();
    a._array = new MutableArray(2002, (ARRAY) d, Address.getORADataFactory());
    return a;
  }

  public int length() throws SQLException
  {
    return _array.length();
  }

  public int getBaseType() throws SQLException
  {
    return _array.getBaseType();
  }

  public String getBaseTypeName() throws SQLException
  {
    return _array.getBaseTypeName();
  }

  public ArrayDescriptor getDescriptor() throws SQLException
  {
    return _array.getDescriptor();
  }

  /* array accessor methods */
  public Address[] getArray() throws SQLException
  {
    return (Address[]) _array.getObjectArray(
      new Address[_array.length()]);
  }

  public Address[] getArray(long index, int count) throws SQLException
  {
    return (Address[]) _array.getObjectArray(index,
      new Address[_array.sliceLength(index, count)]);
  }

  public void setArray(Address[] a) throws SQLException
  {
    _array.setObjectArray(a);
  }

  public void setArray(Address[] a, long index) throws SQLException
  {
    _array.setObjectArray(a, index);
  }

  public Address getElement(long index) throws SQLException
  {
    return (Address) _array.getObjectElement(index);
  }

  public void setElement(Address a, long index) throws SQLException
  {
    _array.setObjectElement(a, index);
  }

}
