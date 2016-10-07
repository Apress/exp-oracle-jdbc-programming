/** This program demonstrates impact of session cursor cache.
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0 */
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import book.util.JDBCUtil;
import book.util.JRunstats;
class DemoSessionCachedCursors
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    // first parameter: database name
    try
    {
      // get connection - auto commit is off
      conn = (Connection) JDBCUtil.  getConnection("benchmark", "benchmark", args[0]);
      JDBCUtil.startTrace( conn );
      JRunstats.prepareBenchmarkStatements ( conn );
      JRunstats.markStart ( conn );
      JDBCUtil.setSessionCachedCursors( conn, 0 );
      String stmtString = "select x from t1";
      PreparedStatement pstmt = null;
      ResultSet rset = null;
      for( int i=0; i < 10000; i++ )
      {
        try
        {
          pstmt = conn.prepareStatement( stmtString );
          rset = pstmt.executeQuery();
        }
        finally
        {
          // release resources associated with JDBC in the finally clause.
          JDBCUtil.close( rset );
          JDBCUtil.close( pstmt );
        }
      }
      JRunstats.markMiddle ( conn );
      JDBCUtil.setSessionCachedCursors( conn, 500 );
      stmtString = "select x from t1";
      PreparedStatement pstmt1 = null;
      ResultSet rset1 = null;
      for( int i=0; i < 10000; i++ )
      {
        try
        {
          pstmt1 = conn.prepareStatement( stmtString );
          rset1 = pstmt1.executeQuery();
        }
        finally
        {
          // release resources associated with JDBC in the finally clause.
          JDBCUtil.close( rset1 );
          JDBCUtil.close( pstmt1 );
        }
      }
      JRunstats.markEnd ( conn );
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      JRunstats.closeBenchmarkStatements ( conn );
      JDBCUtil.close( conn );
    }
  }
}
