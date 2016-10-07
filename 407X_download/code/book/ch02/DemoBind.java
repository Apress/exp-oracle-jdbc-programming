/* This program demonstrates importance of using bind variables*/
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import book.util.JDBCUtil;
import book.util.JRunstats;
public class DemoBind 
{
  public static void main(String[] args) throws Exception
  {  
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      JRunstats.prepareBenchmarkStatements( conn );
      JRunstats.markStart( conn );
      _insertWithBind( conn );
      JRunstats.markMiddle( conn );
      _insertWithoutBind( conn );
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
  private static void _insertWithBind( Connection conn ) throws SQLException
  {
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( "insert into t1(x) values( ? ) " );
      for( int i=0; i < 10000; i++ )
      {
        pstmt.setInt( 1, i );
        pstmt.executeUpdate();
      }
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( pstmt );
    }
  }
  private static void _insertWithoutBind( Connection conn ) throws SQLException
  {
    Statement stmt = null;
    try
    {
      stmt = conn.createStatement();
      for( int i=0; i < 10000; i++ )
      {
        stmt.executeUpdate( "insert into t1( x ) values( " + i + ")" );
      }
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( stmt );
    }
  }
}
