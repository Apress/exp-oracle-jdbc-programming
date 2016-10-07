/* This program illustrates a special case of Oracle update batching where the results are non intitive although correct as per the JDBC specification.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0. and  9.2.0.1.0.
*/
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import oracle.jdbc.OraclePreparedStatement;
import book.util.JDBCUtil;
class TestUpdateBatching
{
  public static void main(String args[])throws Exception
  {
    if( args.length != 1 ) 
    {
      System.out.println("Usage: java TestUpdateBatching <batch_size>" );
    }
    int batchSize = Integer.parseInt( args[0] );
    Connection conn = null;
    Statement stmt = null;
    OraclePreparedStatement ipstmt = null;
    OraclePreparedStatement dpstmt = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      stmt = conn.createStatement ();
      ipstmt = (OraclePreparedStatement) conn.prepareStatement(
         "insert into t1( x ) values ( ? )");
      ipstmt.setExecuteBatch( batchSize );
      dpstmt = (OraclePreparedStatement) conn.prepareStatement(
        "delete from t1 where x = ?" );
      dpstmt.setExecuteBatch( batchSize );
      for( int i = 0; i < 2; i++ )
      {
        ipstmt.setInt(1, i );
        int numOfRowsInserted = ipstmt.executeUpdate();
        System.out.println("num of rows inserted: " + numOfRowsInserted );
        dpstmt.setInt(1, i+1 );
        int numOfRowsDeleted = dpstmt.executeUpdate();
        System.out.println("num of rows Deleted: " + numOfRowsDeleted );
      }
      ipstmt.sendBatch();
      dpstmt.sendBatch();
      conn.commit();
    }
    catch (Exception e)
    {
      // handle the exception properly - in this case, we just
      // print a message, and rollback
      JDBCUtil.printExceptionAndRollback( conn, e );
    }
    finally
    {
      // close the result set, the stmt and connection.
      // ignore any exceptions since we are in the
      // finally clause.
      JDBCUtil.close( ipstmt );
      JDBCUtil.close( dpstmt );
      JDBCUtil.close( conn );
    }
  }
}

