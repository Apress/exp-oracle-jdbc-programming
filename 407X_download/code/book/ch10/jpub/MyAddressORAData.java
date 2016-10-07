/*@lineinfo:filename=MyAddressORAData*//*@lineinfo:user-code*//*@lineinfo:1^1*/package book.ch10.jpub;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import oracle.jpub.runtime.MutableStruct;
import sqlj.runtime.ref.DefaultContext;
import sqlj.runtime.ConnectionContext;

public class MyAddressORAData extends AddressORAData implements ORAData, ORADataFactory
{
   private static final MyAddressORAData _MyAddressORADataFactory = new MyAddressORAData();
   public static ORADataFactory getORADataFactory()
   { return _MyAddressORADataFactory; }

   public MyAddressORAData() { super(); }
   public MyAddressORAData(Connection conn) throws SQLException { super(conn); } 
   public MyAddressORAData(DefaultContext ctx) throws SQLException { super(ctx); } 
  public MyAddressORAData(String line1, String line2, String street, String city, String state, String zip) throws SQLException
  {
    setLine1(line1);
    setLine2(line2);
    setStreet(street);
    setCity(city);
    setState(state);
    setZip(zip);
  }
   /* ORAData interface */
   protected ORAData createExact(Datum d, int sqlType) throws SQLException
   { return create(new MyAddressORAData(), d, sqlType); }

  /* superclass accessors */

/*
  public void setLine1(String line1) throws SQLException { super.setLine1(line1); }
  public String getLine1() throws SQLException { return super.getLine1(); }
*/

/*
  public void setLine2(String line2) throws SQLException { super.setLine2(line2); }
  public String getLine2() throws SQLException { return super.getLine2(); }
*/

/*
  public void setStreet(String street) throws SQLException { super.setStreet(street); }
  public String getStreet() throws SQLException { return super.getStreet(); }
*/

/*
  public void setCity(String city) throws SQLException { super.setCity(city); }
  public String getCity() throws SQLException { return super.getCity(); }
*/

/*
  public void setState(String state) throws SQLException { super.setState(state); }
  public String getState() throws SQLException { return super.getState(); }
*/

/*
  public void setZip(String zip) throws SQLException { super.setZip(zip); }
  public String getZip() throws SQLException { return super.getZip(); }
*/


  /* superclass methods */
  public String getAddress() throws SQLException
  { 
    String __jRt_0 = null;
    __jRt_0 = super.getAddress();
    return __jRt_0;
  }
}/*@lineinfo:generated-code*/