/** This program demonstrate how to use the Java class
* MyAddressAuto that maps to the address object type with
* its wrapper method generated automatically with 
* JPublisher.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0. and  9.2.0.1.0.
*/
import java.util.Map;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import book.util.JDBCUtil;
import book.ch10.jpub.MyAddressAuto;
public class DemoSQLDataAuto
{
  public static void main(String[] args) throws Exception
  {  
    Connection connection = null;
    PreparedStatement pstmt = null; // select
    ResultSet rset = null;
    try
    {
      connection = JDBCUtil.getConnection (
        "benchmark", "benchmark", "ora10g" );
      Map myMap = connection.getTypeMap();
      myMap.put ( "BENCHMARK.ADDRESS", 
        Class.forName("book.ch10.jpub.MyAddressAuto") );
      // select the object and invoke the wrapper method 
      String selectStmt = "select value(a) from address_table a";
      pstmt = connection.prepareStatement ( selectStmt );
      rset = pstmt.executeQuery();
      while ( rset.next() )
      {
        MyAddressAuto address = (MyAddressAuto) rset.getObject(1);
        /* Following statement sets up the sql runtime
           context. If you try to invoke the wrapper method
           before executing this statement, you would 
           get the following error
           Exception in thread "main" java.sql.SQLException: 
             found null connection context
             at sqlj.runtime.error.Errors.raiseError(Errors.java:118)
             at sqlj.runtime.error.Errors.raiseError(Errors.java:60)
             at sqlj.runtime.error.RuntimeRefErrors.raise_NULL_CONN_CTX(
               RuntimeRefErrors.java:118)
             at book.ch10.jpub.AddressAuto.getAddress(AddressAuto.java:148)
             at book.ch10.jpub.MyAddressAuto.getAddress(MyAddressAuto.java:68)
             at DemoSQLDataAuto.main(DemoSQLDataAuto.java:50)
          */
        address.setConnectionContext(
          new sqlj.runtime.ref.DefaultContext(connection));
        System.out.println( address.getAddress() );
      }
    }
    finally
    {
      JDBCUtil.close ( rset, pstmt, connection );
    }
  } 
}
