/* This program demonstrates how to deal with null values in JDBC.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0 and 9.2.0.1.0.
*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
class DemoNullValues
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      _insertNull( conn );
      conn.commit();
      _retrieveNull( conn );
    }
    catch (SQLException e)
    {
      JDBCUtil.printException ( e );
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( conn );
    }
  } // end of main()
  private static void _insertNull( Connection conn ) throws SQLException
  {
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( "insert into t1 ( x ) values ( ? )" );
      pstmt.setNull( 1, OracleTypes.NUMBER );
      int numOfRows = pstmt.executeUpdate();
      System.out.println( "Inserted " + numOfRows + " rows with null value" );
    }
    finally
    {
      JDBCUtil.close( pstmt );
    }
  } 
  private static void _retrieveNull( Connection conn ) throws SQLException
  {
    String queryStmt = "select x from t1 where x is null";
    ResultSet rset = null;
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( queryStmt );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        int value = rset.getInt( 1 );
        if( rset.wasNull() )
        {
          System.out.println( "got a null value..." );
        }
        System.out.println( "The value is retrieved as " + value );
      }
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  } 
} // end of program
