/** This program demonstrate how to use the Java class
* MyAddressORAData to perform DMLs.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0. 
* and  9.2.0.1.0.
*/
import java.util.HashMap;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OraclePreparedStatement;
import book.util.JDBCUtil;
import book.ch10.jpub.MyAddressORAData;
public class DemoORAData
{
  public static void main(String[] args) throws Exception
  {  
    Connection connection = null;
    try
    {
      connection = JDBCUtil.getConnection (
        "benchmark", "benchmark", "ora10g" );
      // example demonstrating first way of selecting object - we use
      // getORAData() method of OracleResultSet.
      _demoSelectUsingGetORAData( connection );
      // example demonstrating second way of selecting object - we use
      // getObject() method of ResultSet specifying a type map.
      _demoSelectUsingGetObject( connection );
      // example demonstrating inserting object(s)
      _demoInsertUsingSetORAData( connection );
      // example demonstrating inserting object(s)- second alternative
      _demoInsertUsingSetObject( connection );
      // example demonstrating updating object(s)
      _demoUpdate( connection );
      // example demonstrating deleting object(s)
    }
    finally
    {
      JDBCUtil.close ( connection );
    }
  } 
  private static void _demoSelectUsingGetORAData( Connection connection)
  throws SQLException
  {
    PreparedStatement pstmt = null;
    OracleResultSet orset = null;
    try
    {
      String selectStmt = "select value(a) from address_table a";
      pstmt = connection.prepareStatement ( selectStmt );
      orset = (OracleResultSet) pstmt.executeQuery();
      while ( orset.next() )
      {
        MyAddressORAData address = (MyAddressORAData) orset.getORAData(1,
          MyAddressORAData.getORADataFactory() );
        System.out.println( address.getAddress() );
      }
    }
    finally
    {
      JDBCUtil.close( orset );
      JDBCUtil.close( pstmt );
    }
  }
  private static void _demoSelectUsingGetObject( Connection connection)
  throws SQLException, ClassNotFoundException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      HashMap myMap = new HashMap();
      myMap.put( "BENCHMARK.ADDRESS", 
        Class.forName(MyAddressORAData.class.getName() ) );
      String selectStmt = "select value(a) from address_table a";
      pstmt = connection.prepareStatement ( selectStmt );
      rset = pstmt.executeQuery();
      while ( rset.next() )
      {
        MyAddressORAData address = (MyAddressORAData) 
          rset.getObject(1, myMap );
        System.out.println( address.getAddress() );
      }
    }
    finally
    {
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }
  private static void _demoInsertUsingSetORAData( Connection connection)
  throws SQLException
  {
    OraclePreparedStatement opstmt = null;
    try
    {
      MyAddressORAData myAddress = new MyAddressORAData();
      myAddress.setLine1("133 Ferry Rd");
      myAddress.setLine2( "Apt # 24");
      myAddress.setStreet( "Crypton St." ); 
      myAddress.setCity( "Dallas");
      myAddress.setState( "TX" ); 
      myAddress.setZip( "75201" );
      String insertStmt = "insert into address_table values( ? )";
      opstmt = (OraclePreparedStatement)
        connection.prepareStatement ( insertStmt );
      opstmt.setORAData (1, myAddress );
      int rows = opstmt.executeUpdate();
      System.out.println ( "Inserted " + rows + " row(s) " );
      connection.commit();
    }
    finally
    {
      JDBCUtil.close( opstmt );
    }
  }
  private static void _demoInsertUsingSetObject( Connection connection)
  throws SQLException, ClassNotFoundException
  {
    PreparedStatement pstmt = null;
    try
    {
      MyAddressORAData myAddress = new MyAddressORAData();
      myAddress.setLine1("134 Ferry Rd");
      myAddress.setLine2( "Apt # 24");
      myAddress.setStreet( "Crypton St." ); 
      myAddress.setCity( "Dallas");
      myAddress.setState( "TX" ); 
      myAddress.setZip( "75201" );
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
  private static void _demoUpdate( Connection connection)
  throws SQLException, ClassNotFoundException
  {
    OraclePreparedStatement opstmt = null;
    PreparedStatement pstmt = null;
    OracleResultSet orset = null;
    try
    {
      MyAddressORAData myAddress = null;
      String selectStmt = "select value(a) from address_table a"+
                   " where line1 = ? for update nowait";
      pstmt = connection.prepareStatement ( selectStmt );
      pstmt.setString( 1, "145 Apt # 7" );
      orset = (OracleResultSet) pstmt.executeQuery();
      if ( orset.next() )
      {
        myAddress = (MyAddressORAData) orset.getORAData(1,
          MyAddressORAData.getORADataFactory() );
        myAddress.setStreet ( "Wonderful St" );
        String updateStmt = "update address_table a" +
                     " set value(a) = ?" +
                     " where a.line1 = ?";
        opstmt = (OraclePreparedStatement)
            connection.prepareStatement ( updateStmt );
        opstmt.setORAData (1, myAddress );
        opstmt.setString (2, "145 Apt # 7" );
        int rows = opstmt.executeUpdate();
        System.out.println ( "Updated " + rows + " rows " );
      }
      connection.commit();
    }
    finally
    {
      JDBCUtil.close( orset );
      JDBCUtil.close( opstmt );
    }
  }
  private static void _demoDelete( Connection connection)
  throws SQLException, ClassNotFoundException
  {
    PreparedStatement pstmt = null;
    try
    {
      String deleteStmt = "delete address_table a" +
                   " where a.line1 like ?";
      pstmt = connection.prepareStatement ( deleteStmt );
      pstmt.setString (1, "Mountain View" );
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
