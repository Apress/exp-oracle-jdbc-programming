/* This program benchmarks impact of prefetch on a query.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0, and 9.2.0.1.0
*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
import book.util.JBenchmark;

class BenchmarkPrefetch extends JBenchmark
{
  public static void main(String args[])
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      new BenchmarkPrefetch()._runBenchmark( conn );
    }
    catch (Exception e)
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
  private void _runBenchmark( Connection conn ) throws Exception
  {
    String stmtString = "{ call prefetch_pkg.get_details ( ?, ?, ?) }";
    try
    {
      s_cstmt = conn.prepareCall( stmtString );
      JDBCUtil.startTrace( conn );
      for( int i=0; i < s_fetchSizes.length; i++ )
      {
        Integer fetchSize = new Integer ( s_fetchSizes[i] );
        timeMethod( JBenchmark.FIRST_METHOD, conn, new Object[]{fetchSize},
          "Fetch Size: " + fetchSize );
      }
    }
    finally
    {
      JDBCUtil.close( s_cstmt );
    }
  }
  public void firstMethod( Connection conn, Object[] parameters ) throws Exception
  {
    ResultSet rset = null;
    Integer fetchSize = (Integer) parameters[0];
    try
    {
      String sqlTag = "/*+ FETCH_SIZE=" + fetchSize + "*/";
      s_cstmt.setInt( 1, 50000);
      s_cstmt.setString( 2, sqlTag );
      s_cstmt.registerOutParameter( 3, OracleTypes.CURSOR );
      s_cstmt.execute();
      rset = (ResultSet) s_cstmt.getObject( 3 );
      rset.setFetchSize( fetchSize.intValue() );
      int i=0;
      while (rset.next())
      {
        i++;
      }
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
    }
  }
  private static int[] s_fetchSizes = {10, 20, 50, 100, 500, 1000, 5000, 10000, 30000};
  private static CallableStatement s_cstmt;
} // end of program
