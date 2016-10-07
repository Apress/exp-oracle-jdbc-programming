/* This program demonstrates scroll-sensiive result set.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0 and 9.2.0.1.0.
*/
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import book.util.JDBCUtil;
import book.util.InputUtil;
class DemoScrollSensitiveResultSet
{
  public static void main(String args[]) throws Exception, IOException
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("scott", "tiger", "ora10g");
      _demoScrollSensitiveResultSet( conn, "select x from t1 order by x" );
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
  private static void _demoScrollSensitiveResultSet( Connection conn, String stmtString )
  throws SQLException, IOException
  {
    ResultSet rset = null;
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( stmtString,
        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
      System.out.print( "For statement: " + stmtString + ", " );
      rset = (ResultSet) pstmt.executeQuery();
      JDBCUtil.printRsetTypeAndConcurrencyType( rset );
      rset.setFetchSize(5);
      System.out.println( "New fetch size: " + rset.getFetchSize() );
      rset.first(); // moves to first row
      System.out.println( "Row number " + rset.getRow() + " has a value = " + rset.getInt( 1 ) );
      InputUtil.waitTillUserHitsEnter( "Perform update on first row and " );
      rset.last(); // moves to last row changing the window size
      rset.first(); // moves back to first row changing the window size
      System.out.println( "Row number " + rset.getRow() + " now has a value = " + rset.getInt( 1 ) );
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  } 
} // end of program
