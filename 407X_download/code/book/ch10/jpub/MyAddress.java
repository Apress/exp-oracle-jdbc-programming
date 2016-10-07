package book.ch10.jpub;

import java.sql.CallableStatement;
import book.util.JDBCUtil;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import java.sql.SQLData;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import oracle.sql.STRUCT;
import oracle.jpub.runtime.MutableStruct;

public class MyAddress extends Address implements SQLData
{
   public MyAddress() { super(); }

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
/* hand written getAddress() - re-implementation of
   db method 
  public String getAddress()
  {
    StringBuffer addressSB = new StringBuffer();
    addressSB.append( getLine1() ).append( " " ).
              append( getLine2() ).append( " " ).
              append( getStreet() ).append( " " ).
              append( getCity() ).append( " " ).
              append( getState() ).append( " " ).
              append( getZip() );
    return addressSB.toString();
  }
*/
  
  public String getAddress( Connection connection )
    throws SQLException
  {
    String getAddressStmt = 
      "begin ? := " + getSQLTypeName()+".get_address( ? ); end;";
    CallableStatement cstmt = null;
    try
    {
      cstmt = connection.prepareCall ( getAddressStmt );
      cstmt.registerOutParameter ( 1, OracleTypes.VARCHAR );
      // pass the second parameter corresponding to the
      // implicit parameter "self".
      cstmt.setObject( 2, this );
      cstmt.execute();
      String address = (String) cstmt.getObject( 1 );
      return address;
    }
    finally
    {
      JDBCUtil.close ( cstmt );
    }
  }
 
}
