/* This program prepares dynamically a query where number of binds are known only at run time.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0 and 9.2.0.1.0.
*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
class DemoDynamicQueryUsingCstmt
{
  public static void main(String args[]) throws Exception
  {
    if( args.length != 0 && args.length != 1 && args.length != 2)
    {
      System.err.println( "Usage: java DemoDynamicQueryUsingCstmt [ename_value] [dept_no_value]. A value of \"null\" for first parameter will indicate that you did not specify any value for ename. A value of -1 for the second parameter indicates you did not specify any value for deptno" );
      Runtime.getRuntime().exit( 1 );
    }
    
    if( ( args.length == 1 ) && ( !"null".equals(args[0] ) ) )
    {
      ename = args[0];
    }
    else if( args.length == 2 )
    {
      if( !"null".equals(args[0] ) )
      {
        ename = args[0];
      }
      deptno = Integer.parseInt( args[1] );
    }
    if( ename != null )
    {
      System.out.println( "ename = " + ename );
    }
    if( deptno != -1 )
    {
      System.out.println( "deptno = " + deptno );
    }
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("scott", "tiger", "ora10g");
      _executeDynamicQuery( conn, ename, deptno );
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
  private static void _executeDynamicQuery( Connection conn, String ename, int deptno )
    throws SQLException
  {
    String stmtStr = "{call hr_app_ctx_pkg.execute_dynamic_query( ?, ?, ?)}";
    ResultSet rset = null;
    CallableStatement cstmt = null;
    try
    {
      cstmt = conn.prepareCall( stmtStr );
      if( ename != null )
      {
        cstmt.setString( 1, ename );
      }
      else
      {
        cstmt.setNull(1, OracleTypes.VARCHAR);
      }
      if( deptno != -1 )
      {
        cstmt.setInt( 2, deptno );
      }
      else
      {
        cstmt.setNull(2, OracleTypes.NUMBER);
      }
      cstmt.registerOutParameter( 3, OracleTypes.CURSOR );
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( 3 );
      while( rset.next() )
      {
        System.out.println( rset.getString( 1 ) + ", " +
          rset.getInt( 2 ) + ", " + 
          rset.getString( 3 ) + ", " + 
          rset.getInt( 4 ) );
      }
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
    }
  } 
  private static String ename = null;
  private static int deptno = -1;
} // end of program
