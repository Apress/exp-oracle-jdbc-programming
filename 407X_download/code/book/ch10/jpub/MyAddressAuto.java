/*@lineinfo:filename=MyAddressAuto*//*@lineinfo:user-code*//*@lineinfo:1^1*/package book.ch10.jpub;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import java.sql.SQLData;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import oracle.sql.STRUCT;
import oracle.jpub.runtime.MutableStruct;
import sqlj.runtime.ref.DefaultContext;
import sqlj.runtime.ConnectionContext;

public class MyAddressAuto extends AddressAuto implements SQLData
{
   public MyAddressAuto() { super(); }

  /* superclass accessors */

/*
  public void setLine1(String line1)
 { super.setLine1(line1); }

  public String getLine1()   { return super.getLine1(); }
 */

/*
  public void setLine2(String line2)
 { super.setLine2(line2); }

  public String getLine2()   { return super.getLine2(); }
 */

/*
  public void setStreet(String street)
 { super.setStreet(street); }

  public String getStreet()   { return super.getStreet(); }
 */

/*
  public void setCity(String city)
 { super.setCity(city); }

  public String getCity()   { return super.getCity(); }
 */

/*
  public void setState(String state)
 { super.setState(state); }

  public String getState()   { return super.getState(); }
 */

/*
  public void setZip(String zip)
 { super.setZip(zip); }

  public String getZip()   { return super.getZip(); }
 */


  /* superclass methods */
  public String getAddress() throws SQLException
  { 
    String __jRt_0 = null;
    __jRt_0 = super.getAddress();
    return __jRt_0;
  }
}/*@lineinfo:generated-code*/