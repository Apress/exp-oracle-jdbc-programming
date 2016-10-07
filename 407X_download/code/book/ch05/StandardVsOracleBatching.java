/* This program compares the standard update batching with oracle update batching for elapsed times and latches consumed using the JRunstats utility.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0. and  9.2.0.1.0.
*/
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import oracle.jdbc.OraclePreparedStatement;
import book.util.JDBCUtil;
import book.util.JRunstats;
class StandardVsOracleBatching
{
  private static int s_numberOfRecords = 0;
  private static int s_batchSize = 1;
  private static long s_start = 0;
  private static long s_middle = 0;
  private static long s_end = 0;
  private static int[] s_batchSizeArr = 
    { 1, 5, 10, 50, 75, 100, 150, 200, 300, 400, 500, 
      750, 1000, 2000, 3000, 5000, 10000 };
  private static void _checkUsage (String[] args)
  {
    int argc = args.length;
    if( argc != 1)
    {
      System.err.println( "Usage: java StandardVsOracleBatching <number of records>" );
      Runtime.getRuntime().exit(1);
    }
    s_numberOfRecords = Integer.parseInt( args[0] );
  }
  public static void main(String args[])
  {
    _checkUsage( args );
    Connection conn = null;
    PreparedStatement pstmt = null;
    OraclePreparedStatement opstmt = null;
    String insertStmtStr = "insert into t1( x, y ) values ( ?, ?)";
    try
    {
      // get connection; set autocommit to false within JDBCUtil.
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      pstmt = conn.prepareStatement( insertStmtStr );
      opstmt = (OraclePreparedStatement) conn.prepareStatement(
          insertStmtStr );
      for(int x=0; x < s_batchSizeArr.length; x++ )
      {
        JRunstats.prepareBenchmarkStatements( conn );
        s_batchSize = s_batchSizeArr[x];
        // mark beginning of execute with standard update batching
        JRunstats.markStart( conn );
        s_start = System.currentTimeMillis();
        // execute with standard update batching
        for( int i=0; i < s_numberOfRecords; i++)
        {
          // batch s_batchSize number of statements
          // before sending them as one round trip. 
          int j = 0;
          for( j=0; j < s_batchSize; j++)
          {
            pstmt.setInt(1, i ); 
            pstmt.setString(2, "data" + i  ); 
            pstmt.addBatch();
           // System.out.println( "Inserted " + numOfRowsInserted + " row(s)" );
          }
          i += (j-1);
          int[] updateCounts = pstmt.executeBatch();
          //System.out.println( "i = " + i );
        }
        // mark beginning of execute with Oracle update batching
        JRunstats.markMiddle( conn );
        s_middle = System.currentTimeMillis();
        // set the execute batch size
        opstmt.setExecuteBatch( s_batchSize );
        // bind the values
        for( int i=0; i < s_numberOfRecords; i++)
        {
          // bind the values
          opstmt.setInt(1, i ); 
          opstmt.setString(2, "data"+i  ); 
          int numOfRowsInserted  = opstmt.executeUpdate();
        }
        s_end = System.currentTimeMillis();
        JRunstats.markEnd( conn, 10000 ); 
        System.out.println( "Standard Update batching (recs="+
          s_numberOfRecords+ ", batch=" + s_batchSize + ") = " + (s_middle - s_start ) + " ms" );
        System.out.println( "Oracle Update batching (recs="+
          s_numberOfRecords+ ", batch=" + s_batchSize + ") = " + (s_end - s_middle ) + " ms");
        conn.commit();
        JRunstats.closeBenchmarkStatements( conn );
      }
    }
    catch (Exception e)
    {
      // handle the exception properly - in this case, we just
      // print a message, and rollback
      JDBCUtil.printExceptionAndRollback( conn, e );
    }
    finally
    {
      // release JDBC resources in the finally clause.
      JDBCUtil.close( pstmt );
      JDBCUtil.close( opstmt );
      JDBCUtil.close( conn );
    }
  }
}

