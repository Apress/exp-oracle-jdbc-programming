/* This program demonstrates scroll-sensiive result set.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0 and 9.2.0.1.0.
*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
class DemoPagination
{
  public static void main(String args[]) throws Exception
  {
    if( args.length != 0 && args.length != 2 && args.length != 3 )
    {
      System.err.println( "Usage: java DemoPagination [<min_row_number> <max_row_number> <order_by_clause>]" );
      System.exit( 1 );
    }
    if( args.length >= 2 )
    {
      s_minRowNumber = Integer.parseInt( args[0] );
      s_maxRowNumber = Integer.parseInt( args[1] );
    }
    if( args.length == 3 )
      s_orderByClause = args[ 2 ];
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("scott", "tiger", "ora10g");
      long startTime = System.currentTimeMillis(); 
      _showCurrentSetOfRows( conn, s_minRowNumber, s_maxRowNumber, s_orderByClause );
      long endTime = System.currentTimeMillis();
      System.out.println( "time taken: " + (endTime-startTime) + " milliseconds" );
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
  private static void _showCurrentSetOfRows( Connection conn, int minRowNumber, int maxRowNumber, String orderByClause )
  throws SQLException
  {
    ResultSet rset = null;
    CallableStatement cstmt = null;
    try
    {
      cstmt = conn.prepareCall( "{call demo_pagination.get_details( ?, ?, ?, ?)}" );
      cstmt.setInt( 1, minRowNumber );
      cstmt.setInt( 2, maxRowNumber );
      cstmt.setString( 3, orderByClause );
      cstmt.registerOutParameter( 4, OracleTypes.CURSOR );
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( 4 );
      rset.setFetchSize(10);
      while( rset.next() )
      {
        System.out.println( rset.getInt( 1 ) + ", " + rset.getDate( 2 ) );
      }
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
    }
  } 
  private static int s_minRowNumber = 1;
  private static int s_maxRowNumber = 10;
  private static String s_orderByClause = "order by x, y";
} // end of program
