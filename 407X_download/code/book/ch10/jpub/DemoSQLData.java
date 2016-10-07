/** This program demonstrates how to use the Java class
* MyAddress that maps to the address object type and uses
* the JDBC standard interface SQLData.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0. and  9.2.0.1.0.
*/
import java.util.Map;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import book.util.JDBCUtil;
import book.ch10.jpub.MyAddress;
public class DemoSQLData
{
  public static void main(String[] args) throws Exception
  {  
    Connection connection = null;
    try
    {
      connection = JDBCUtil.getConnection(
        "benchmark", "benchmark", "ora10g" );
      /*
       * Before reaching here we have already executed 
       * the following two steps:
       *  Step 1: Generated custom classes MyAddress and Address
       *          using the following JPublisher command:
       *    jpub -user=benchmark/benchmark -methods=none 
       *      -builtintypes=jdbc -numbertypes=objectjdbc 
       *      -usertypes=jdbc -sql address:Address:MyAddress
       *      -package="book.ch08.jpub"
       *  Step 2: Extended the MyAddress class to add a wrapper
       *      method corrresponding to the method get_address in the
       *      object type address manually as described in the book.
       */
     
      /* Step 3: We create a map entry and map our class MyAddress
       *      to correspond to the object type address. We then set
       *      the Type Map in the connection. We could also have
       *      used the connection.getTypeMap() and set its values.
       *      The end result should be that the connection has
       *      a type map that maps a Java class to the object type
       *      in the database.
       */   
      Map myMap = connection.getTypeMap();
      myMap.put ( "BENCHMARK.ADDRESS", 
        Class.forName( MyAddress.class.getName() ) );
      /* Step 4: Select, insert or update object table data
       *         as required
       */
      // example demonstrating selecting object(s)
      _demoSelect( connection );
      // example demonstrating inserting object(s)
      _demoInsert( connection );
      // example demonstrating updating object(s)
      _demoUpdate( connection );
      // example demonstrating deleting object(s)
      _demoDelete( connection );
    }
    finally
    {
      JDBCUtil.close ( connection );
    }
  } 
  private static void _demoSelect( Connection connection )
  throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      String selectStmt = "select value(a) from address_table a";
      pstmt = connection.prepareStatement ( selectStmt );
      rset = pstmt.executeQuery();
      while ( rset.next() )
      {
        MyAddress address = (MyAddress) rset.getObject(1);
        System.out.println( address.getAddress( connection ) );
      }
    }
    finally
    {
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }
  private static void _demoInsert( Connection connection )
  throws SQLException
  {
    MyAddress myAddress = new MyAddress ();
    myAddress.setLine1("133 Ferry Rd");
    myAddress.setLine2( "Apt # 24");
    myAddress.setStreet( "Crypton St." ); 
    myAddress.setCity( "Dallas");
    myAddress.setState( "TX" ); 
    myAddress.setZip( "75201" );
    PreparedStatement pstmt = null;
    try
    {
      String insertStmt = "insert into address_table values( ? )";
      pstmt = connection.prepareStatement ( insertStmt );
      pstmt.setObject (1, myAddress );
      int rows = pstmt.executeUpdate();
      System.out.println ( "Inserted " + rows + " row(s) " );
      connection.commit();
    }
    finally
    {
      JDBCUtil.close( pstmt );
    }
  }
  private static void _demoUpdate( Connection connection )
  throws SQLException
  {
    // example demonstrating updating object(s)
    // In this method, you select the object first and
    // change only the fields that you need to change.
    // Then you update the table with the new object.
    // NOTE: WE need to use the "for update nowait" clause
    // to avoid locking issues. We cover this topic in detail
    // in Chapter "Locking Related Issues".
    PreparedStatement pstmt = null;
    PreparedStatement pstmt1 = null;
    ResultSet rset = null;
    try
    {
      MyAddress myAddress = null;
      String selectStmt = "select value(a) from address_table a"+
                   " where line1 = ? and rownum <= 1 for update nowait";
      pstmt = connection.prepareStatement ( selectStmt );
      pstmt.setString( 1, "145 Apt # 7" );
      rset = pstmt.executeQuery();
      while ( rset.next() )
      {
        myAddress = (MyAddress) rset.getObject(1);
        myAddress.setStreet ( "Wonderful St" );
        String updateStmt = "update address_table a" +
                     " set value(a) = ?" +
                     " where a.line1 = ?";
        pstmt1 = connection.prepareStatement ( updateStmt );
        pstmt1.setObject (1, myAddress );
        pstmt1.setString (2, "145 Apt # 7" );
        int rows = pstmt1.executeUpdate();
        System.out.println ( "Updated " + rows + " rows " );
      }
      connection.commit();
    }
    finally
    {
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
      JDBCUtil.close( pstmt1 );
    }
  }
  private static void _demoDelete( Connection connection )
  throws SQLException
  {
    PreparedStatement pstmt = null;
    try
    {
      String deleteStmt = "delete address_table a" +
                   " where a.line1 = ?";
      pstmt = connection.prepareStatement ( deleteStmt );
      pstmt.setString (1, "145 Apt # 7" );
      int rows = pstmt.executeUpdate();
      System.out.println ( "Deleted " + rows + " row(s) " );
      connection.commit();
    }
    finally
    {
      JDBCUtil.close( pstmt );
    }
  }
}
