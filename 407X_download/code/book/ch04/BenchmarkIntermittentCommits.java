/* This class demonstrates why you should only commit at the end of your transaction - it showcases the performance degradation when you issue a commit in the middle of your transaction.
 * COMPATIBLITY NOTE: tested against 10.1.0.2.0. and 9.2.0.1.0 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import book.util.JDBCUtil;
import book.util.JRunstats;
class BenchmarkIntermittentCommits
{
  public static void main( String[] args ) throws Exception
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection( "benchmark", "benchmark", "ora10g" );
      JRunstats.prepareBenchmarkStatements( conn );
      JRunstats.markStart( conn );
      _doInsertCommitInLoop( conn );
      JRunstats.markMiddle( conn );
      _doInsertCommitOutsideLoop( conn );
      JRunstats.markEnd( conn );
    }
    finally
    {
      JRunstats.closeBenchmarkStatements( conn );
      JDBCUtil.close( conn );
    }
  }
  private static void _doInsertCommitInLoop( Connection conn ) throws SQLException
  {
    String stmtString = "insert into t1( x ) values ( ? )";
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      for( int i=0; i < NUM_OF_RECORDS; i++ )
      {
        pstmt.setInt( 1, 1 );
        pstmt.executeUpdate();
        conn.commit();
      }
    }
    finally
    {
      JDBCUtil.close( pstmt );
    }
  }
  private static void _doInsertCommitOutsideLoop( Connection conn ) throws SQLException
  {
    String stmtString = "insert into t1( x ) values ( ? )";
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      for( int i=0; i < NUM_OF_RECORDS; i++ )
      {
        pstmt.setInt( 1, 1 );
        pstmt.executeUpdate();
      }
      conn.commit();
    }
    finally
    {
      JDBCUtil.close( pstmt );
    }
  }

  private static final int NUM_OF_RECORDS = 10000;
}
