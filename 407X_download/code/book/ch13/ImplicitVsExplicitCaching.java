/** This program compares implicit statement caching with 
* explicit statement caching in terms of elapsed time.
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleConnection;
import book.util.JDBCUtil;
import book.util.Util;
class ImplicitVsExplicitCaching
{
  public static void main(String args[]) throws SQLException
  {
    Util.checkProgramUsage( args );
    OracleConnection conn = null;
    try
    {
      // get connection 
      conn = (OracleConnection) JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      conn.setExplicitCachingEnabled( true );
      conn.setImplicitCachingEnabled( true );
      conn.setStatementCacheSize( 10 );
      System.out.println("explicit caching enabled: " + 
        conn.getExplicitCachingEnabled() );
      System.out.println("implicit caching enabled: " + 
        conn.getImplicitCachingEnabled() );
      System.out.println("cache size: " + 
        conn.getStatementCacheSize() );
      int numOfRuns = 5000;
      long startTime = System.currentTimeMillis();
      for( int i=0; i < numOfRuns; i++ )
      {
        _doSelectWithExplicitCachingEnabled( conn );
      }
      long endTime = System.currentTimeMillis();
      System.out.println("Implicit took: " + (endTime-startTime)+ " ms ");
      startTime = System.currentTimeMillis();
      for( int i=0; i < numOfRuns; i++ )
      {
        _doSelectWithImplicitCachingEnabled( conn );
      }
      endTime = System.currentTimeMillis();
      System.out.println("Explicit took: " + (endTime-startTime)+ " ms ");
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  /////////////////// PRIVATE SECTION /////////////////
  private static void _doSelectWithExplicitCachingEnabled( OracleConnection conn ) throws SQLException
  {
    OraclePreparedStatement opstmt = null;
    ResultSet rset = null;
    String stmtString = "select count(*) from t1";
    String stmtKey = EXPLICIT_CACHING_KEY_PREFIX + stmtString;
    try
    {
      opstmt = ( OraclePreparedStatement) conn.
        getStatementWithKey( stmtKey );
      if( opstmt == null )
      {
        opstmt = ( OraclePreparedStatement) conn.
          prepareStatement( stmtString );
      }
      rset = opstmt.executeQuery();
    }
    finally
    {
      JDBCUtil.close( rset );
      try
      {
        opstmt.closeWithKey( stmtKey );
      }
      catch ( Exception e) { e.printStackTrace();}
    }
  }
  private static void _doSelectWithImplicitCachingEnabled( OracleConnection conn ) throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    String stmtString = "select count(*) from t1";
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      rset = pstmt.executeQuery();
    }
    finally
    {
      JDBCUtil.close( pstmt );
      JDBCUtil.close( rset );
    }
  }
  private static final String EXPLICIT_CACHING_KEY_PREFIX = 
    "EXPLICIT_CACHING_KEY_PREFIX";
}
