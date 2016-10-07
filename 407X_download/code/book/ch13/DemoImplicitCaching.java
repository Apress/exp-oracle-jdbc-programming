/* * This program demonstrates implicit statement caching.
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
import book.util.Util;
class DemoImplicitCaching
{
  public static void main(String args[]) throws SQLException
  {
    Util.checkProgramUsage( args );
    OracleConnection conn = null;
    try
    {
      // get connection
      conn = (OracleConnection) JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      System.out.println("implicit caching enabled: " + conn.getImplicitCachingEnabled() );
      System.out.println("cache size: " + conn.getStatementCacheSize() );
      JDBCUtil.startTrace( conn );
      for( int i=0; i < 1000; i++ )
      {
        _doSelect ( conn, "/*+ implicit disabled */" );
      }
      conn.setImplicitCachingEnabled( true );
      conn.setStatementCacheSize( 10 );
      System.out.println("implicit caching enabled: " + 
        conn.getImplicitCachingEnabled() );
      System.out.println("cache size: " + 
        conn.getStatementCacheSize() );
      for( int i=0; i < 1000; i++ )
      {
        _doSelect ( conn, "/*+ implicit enabled */" );
      }
      // demonstrating use of implicit caching with callable statement
      for( int i=0; i < 1000; i++ )
      {
        _doExecuteCallableStatement( conn, "/*+ enabled implicit caching for callable statement */" );
      }
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  /////////////////// PRIVATE SECTION /////////////////
  private static void _doSelect( Connection conn, String tag ) throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    String stmtString = "select " + tag + " count(*) from dual";
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      rset = pstmt.executeQuery();
    }
    finally
    {
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }

  private static void _doExecuteCallableStatement( Connection conn, String tag ) throws SQLException
  {
    CallableStatement cstmt = null;
    ResultSet rset = null;
    String stmtString = "begin" + tag + " ? := f; end;";
    try
    {
      cstmt = conn.prepareCall( stmtString );
      cstmt.registerOutParameter( 1, OracleTypes.CURSOR );
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( 1 );
    }
    finally
    {
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
    }
  }
}
