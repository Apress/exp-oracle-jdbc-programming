/** This program demonstrates explicit statement caching.
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.ResultSet;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
import book.util.Util;
class DemoExplicitCaching
{
  public static void main(String args[]) throws SQLException
  {
    Util.checkProgramUsage( args );
    OracleConnection conn = null;
    try
    {
      // get connection 
      conn = (OracleConnection) JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      System.out.println("explicit caching enabled: " + 
        conn.getExplicitCachingEnabled() );
      System.out.println("cache size: " + conn.getStatementCacheSize() );
      // enable trace
      JDBCUtil.startTrace( conn );
      for( int i=0; i < 1000; i++ )
      {
        _doSelect ( conn, "/*+ explicit disabled */" );
      }
      conn.setExplicitCachingEnabled( true );
      conn.setStatementCacheSize( 10 );
      System.out.println("explicit caching enabled: " + 
        conn.getExplicitCachingEnabled() );
      System.out.println("cache size: " + 
        conn.getStatementCacheSize() );
      for( int i=0; i < 1000; i++ )
      {
        _doSelect ( conn, "/*+ explicit enabled */" );
      }
      // demonstrating use of explicit caching with callable statement
      for( int i=0; i < 1000; i++ )
      {
        _doExecuteCallableStatement( conn, "/*+ enabled explicit caching for callable statement */" );
      }
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  /////////////////// PRIVATE SECTION /////////////////
  private static void _doSelect( OracleConnection conn, String tag ) throws SQLException
  {
    OraclePreparedStatement opstmt = null;
    ResultSet rset = null;
    String stmtString = "select " + tag + " count(*) from dual";
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

  private static void _doExecuteCallableStatement( OracleConnection conn, String tag ) throws SQLException
  {
    OracleCallableStatement ocstmt = null;
    ResultSet rset = null;
    String stmtString = "begin" + tag + " ? := f; end;";
    String stmtKey = EXPLICIT_CACHING_KEY_PREFIX + stmtString;
    try
    {
      ocstmt = ( OracleCallableStatement) conn.
        getCallWithKey( stmtKey );
      if( ocstmt == null )
      {
        ocstmt = ( OracleCallableStatement) conn.  prepareCall( stmtString );
      }
      ocstmt.registerOutParameter( 1, OracleTypes.CURSOR );
      ocstmt.execute();
      rset = (ResultSet) ocstmt.getObject( 1 );
    }
    finally
    {
      JDBCUtil.close( rset );
      try
      {
        ocstmt.closeWithKey( stmtKey );
      }
      catch ( Exception e) { e.printStackTrace(); }
    }
  }
  private static final String EXPLICIT_CACHING_KEY_PREFIX = "EXPLICIT_CACHING_KEY_PREFIX";
}
