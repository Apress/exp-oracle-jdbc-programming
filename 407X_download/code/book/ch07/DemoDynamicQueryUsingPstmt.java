/* This program prepares dynamically a query where number of binds are known only at run time.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0 and 9.2.0.1.0.
*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import book.util.JDBCUtil;
class DemoDynamicQueryUsingPstmt
{
  public static void main(String args[]) throws Exception
  {
    if( args.length != 0 && args.length != 1 && args.length != 2)
    {
      System.err.println( "Usage: java DemoDynamicQueryUsingPstmt [ename_value] [dept_no_value]. A value of \"null\" for first parameter will indicate that you did not specify any value for ename. A value of -1 for the second parameter indicates you did not specify any value for deptno" );
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
      String queryStmt = _buildDynamicQuery( ename, deptno );
      _executeDynamicQuery( conn, queryStmt, ename, deptno );
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
  private static String _buildDynamicQuery( String ename, int deptno )
  {
    StringBuffer queryStmt = new StringBuffer("select ename, deptno, job, sal from emp where 0 = 0");
    if( ename != null )
    {
      queryStmt.append( " and ename like ?");
    }
    if( deptno != -1 )
    {
      queryStmt.append( " and deptno = ?" );
    }
    return queryStmt.toString();
  }
  private static void _executeDynamicQuery( Connection conn, String queryStmt, String ename, int deptno )
    throws SQLException
  {
    ResultSet rset = null;
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( queryStmt );
      int colIndex = 1;
      if( ename != null )
      {
        pstmt.setString( colIndex++, ename+"%" );
      }
      if( deptno != -1 )
      {
        pstmt.setInt( colIndex, deptno );
      }
      rset = pstmt.executeQuery();
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
      JDBCUtil.close( pstmt );
    }
  } 
  private static String ename = null;
  private static int deptno = -1;
} // end of program
