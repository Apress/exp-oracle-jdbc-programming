/* This program demonstrates refetching of rows in a result set.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0 and 9.2.0.1.0.
*/
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import book.util.JDBCUtil;
import book.util.Benchmark;
import book.util.InputUtil;
class DemoRefreshRow
{
  public static void main(String args[]) throws Exception, IOException
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("scott", "tiger", "ora10g");
      _demoRefreshRow( conn, "select x from t1 order by x" );
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just 
      // print the stack trace.
      JDBCUtil.printException ( e );
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( conn );
    }
  } // end of main()
  private static void _demoRefreshRow( Connection conn, String stmtString )
  throws SQLException, IOException
  {
    ResultSet rset = null;
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( stmtString,
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      System.out.print( "For statement: " + stmtString + ", " );
      //JDBCUtil.printRsetTypeAndConcurrencyType( pstmt );
      rset = (ResultSet) pstmt.executeQuery();
      JDBCUtil.printRsetTypeAndConcurrencyType( rset );
      rset.setFetchSize(7);
      rset.next(); // moves to first row
      InputUtil.waitTillUserHitsEnter( "Perform delete/update and " );
      //start trace
      Benchmark.startTrace( conn );
      rset.refreshRow();
      System.out.println( "Row number 1 has a value = " + rset.getInt( 1 ) );
      rset.next(); // moves to second row
      System.out.println( "Row number 2 has a value = " + rset.getInt( 1 ) );
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  } 
} // end of program
