/* This program demonstrates how to use the JRunstats utility */
package book.util;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
public class DemoJRunstats 
{
  public static void main(String[] args) throws Exception
  {  
    String queryUsingCBO = "select count(*) " +
                           "from t1, t " +
                           "where t1.x = t.x " +
                           "and t1.x = ?";
    String queryUsingRBO = "select /*+ RULE */ count(*) " +
                           "from t1, t " +
                           "where t1.x = t.x " +
                           "and t1.x = ?";
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      JRunstats.prepareBenchmarkStatements( conn );
      JRunstats.markStart( conn );
      _executeQuery( conn, queryUsingCBO );
      JRunstats.markMiddle( conn );
      _executeQuery( conn, queryUsingRBO );
      JRunstats.markEnd( conn );
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
      JRunstats.closeBenchmarkStatements( conn );
      JDBCUtil.close( conn );
    }
  } 
  private static void _executeQuery( Connection conn, String query ) throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      pstmt = conn.prepareStatement( query );
      pstmt.setInt( 1, 0 );
      rset = pstmt.executeQuery();
      System.out.println( "printing query results ...\n");
      while (rset.next())
      {
        int count = rset.getInt ( 1 );
        System.out.println( "count = " + count );
      }
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }
}
