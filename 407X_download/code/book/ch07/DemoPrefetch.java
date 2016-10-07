/* This program demonstrates how to set and get fetch size for your
* queries using PreparedStatement.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0, and 9.2.0.1.0
*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Connection;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;

class DemoPrefetch
{
  public static void main(String[] args)
  {
    if( args.length != 3 ) 
    {
      System.err.println( "Usage: java DemoPrefetch <connection level fetch size> <statement level fetch size> <result set level fetch size>");
      Runtime.getRuntime().exit( 1 );
    }
    int connLevelDefaultPrefetch = Integer.parseInt( args[0] );
    int stmtLevelFetchSize = Integer.parseInt( args[1] );
    int rsetLevelFetchSize = Integer.parseInt( args[2] );
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      System.out.println( "\nDefault connection fetch size: " + ((OracleConnection) conn).getDefaultRowPrefetch() );
      System.out.println( "setting the default fetch size at connection level to " + connLevelDefaultPrefetch );
      ((OracleConnection) conn).setDefaultRowPrefetch( connLevelDefaultPrefetch  );
      System.out.println( "Now the connection fetch size: " + ((OracleConnection) conn).getDefaultRowPrefetch() );

      JDBCUtil.startTrace( conn );
      _demoPstmtFetchSize( conn, connLevelDefaultPrefetch, stmtLevelFetchSize);
      _demoPstmtFetchSizeWithRsetOverride( conn, connLevelDefaultPrefetch, stmtLevelFetchSize, rsetLevelFetchSize );
      _demoCstmtFetchSize( conn, connLevelDefaultPrefetch, stmtLevelFetchSize );
      _demoCstmtFetchSizeWithRsetOverride( conn, connLevelDefaultPrefetch, stmtLevelFetchSize, rsetLevelFetchSize );
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
  private static void _demoPstmtFetchSize( Connection conn,
    int connLevelDefaultPrefetch, int stmtLevelFetchSize ) throws SQLException
  {
    System.out.println( "Inside _demoPstmtFetchSize" );
    String sqlTag = "/*+" + 
                    "(CONN="  + connLevelDefaultPrefetch + ")" +
                    "(PSTMT=" + stmtLevelFetchSize       + ")" + 
                 "*/";
    String stmtString = "select x "+ sqlTag + " from t1 where rownum <= ?";
    PreparedStatement pstmt = null;
    ResultSet rset = null;
 
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      System.out.println( "\tDefault statement fetch size: " + pstmt.getFetchSize());
      pstmt.setFetchSize( stmtLevelFetchSize );
      System.out.println( "\tnew statement fetch size: " + pstmt.getFetchSize());
      pstmt.setInt( 1, 100 );
      rset = pstmt.executeQuery();
      System.out.println( "\tResult set fetch size: " + rset.getFetchSize());
      int i=0;
      while (rset.next())
      {
        i++;
      }
      System.out.println( "\tnumber of times in the loop: " + i );
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }
  private static void _demoPstmtFetchSizeWithRsetOverride( Connection conn,
    int connLevelDefaultPrefetch, int stmtLevelFetchSize, 
    int rsetLevelFetchSize ) throws SQLException
  {
    System.out.println( "Inside _demoPstmtFetchSizeWithRsetOverride" );
    String sqlTag = "/*+" + 
                    "(CONN="  + connLevelDefaultPrefetch + ")" +
                    "(PSTMT=" + stmtLevelFetchSize       + ")" + 
                    "(RSET="  + rsetLevelFetchSize       + ")" +
                 "*/";
    String stmtString = "select x "+ sqlTag + " from t1 where rownum <= ?";
    PreparedStatement pstmt = null;
    ResultSet rset = null;
 
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      System.out.println( "\tDefault statement fetch size: " + pstmt.getFetchSize());
      pstmt.setFetchSize( stmtLevelFetchSize );
      System.out.println( "\tnew statement fetch size: " + pstmt.getFetchSize());
      pstmt.setInt( 1, 100 );
      rset = pstmt.executeQuery();
      rset.setFetchSize( rsetLevelFetchSize );
      System.out.println( "\tnew result set fetch size: " + rset.getFetchSize());
      int i=0;
      while (rset.next())
      {
        i++;
      }
      System.out.println( "\tnumber of times in the loop: " + i );
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }
  // demo fetch size using callable statement
  private static void _demoCstmtFetchSize( Connection conn, int connLevelDefaultPrefetch, int stmtLevelFetchSize ) throws SQLException
  {
    System.out.println( "Inside _demoCstmtFetchSize" );
    String sqlTag = "/*+" + 
                    "(CONN="  + connLevelDefaultPrefetch + ")" +
                    "(CSTMT=" + stmtLevelFetchSize       + ")" + 
                 "*/";
    String stmtString = "{ call prefetch_pkg.get_details ( ?, ?, ? ) }";
    CallableStatement cstmt = null;
    ResultSet rset = null;
    try
    {
      cstmt = conn.prepareCall( stmtString );
      System.out.println( "\tDefault statement fetch size: " + cstmt.getFetchSize());
      cstmt.setFetchSize( stmtLevelFetchSize );
      System.out.println( "\tnew statement fetch size: " + cstmt.getFetchSize());
      cstmt.setInt( 1, 100); // number of rows to be fetched
      cstmt.setString( 2, sqlTag );
      cstmt.registerOutParameter( 3, OracleTypes.CURSOR );
      // execute the query
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( 3 );
      System.out.println( "\tresult set fetch size: " + rset.getFetchSize());
      System.out.println( "\tHowever, in case of callable statement, the real fetch size for all result sets obtained from the statement is the same as the one set at the connection level." );
      int i=0;
      while (rset.next())
      {
        i++;
      }
      System.out.println( "\tnumber of times in the loop: " + i );
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
    }
  }
  // demo fetch size using callable statement
  private static void _demoCstmtFetchSizeWithRsetOverride( Connection conn, int connLevelDefaultPrefetch, int stmtLevelFetchSize, int rsetLevelFetchSize ) throws SQLException
  {
    System.out.println( "Inside _demoCstmtFetchSizeWithRsetOverride" );
    String sqlTag = "/*+" + 
                    "(CONN="  + connLevelDefaultPrefetch + ")" +
                    "(CSTMT=" + stmtLevelFetchSize       + ")" + 
                    "(RSET="  + rsetLevelFetchSize       + ")" +
                 "*/";
    String stmtString = "{ call prefetch_pkg.get_details ( ?, ?, ? ) }";
    CallableStatement cstmt = null;
    ResultSet rset = null;
    try
    {
      cstmt = conn.prepareCall( stmtString );
      System.out.println( "\tDefault statement fetch size: " + cstmt.getFetchSize());
      cstmt.setFetchSize( stmtLevelFetchSize );
      System.out.println( "\tnew statement fetch size: " + cstmt.getFetchSize());
      cstmt.setInt( 1, 100); // number of rows to be fetched
      cstmt.setString( 2, sqlTag );
      cstmt.registerOutParameter( 3, OracleTypes.CURSOR );
      // execute the query
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( 3 );
      rset.setFetchSize( rsetLevelFetchSize );
      System.out.println( "\tnew result set fetch size: " + rset.getFetchSize());
      System.out.println( "\tHowever, in case of callable statement, the real fetch size for all result sets obtained from the statement is the same as the one set at the connection level." );
      int i=0;
      while (rset.next())
      {
        i++;
      }
      System.out.println( "\tnumber of times in the loop: " + i );
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
    }
  }
} // end of program
