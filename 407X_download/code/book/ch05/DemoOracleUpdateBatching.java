/* This program illustrates use of Oracle update batching.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0. and  9.2.0.1.0.
*/
import java.sql.SQLException;
import java.sql.Statement; // for accessing constants only
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleConnection;
import book.util.JDBCUtil;
import book.util.Util;
class DemoOracleUpdateBatching
{
  public static void main(String args[])
  {
    Util.checkProgramUsage( args );
    OracleConnection oconn = null;
    OraclePreparedStatement opstmt = null;
    try
    {
      // get connection, set it to autocommit within JDBCUtil.getConnection()
      oconn = (OracleConnection)JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      // prepare a stmt to insert data
      opstmt = (OraclePreparedStatement) oconn.prepareStatement(
        "insert into t1( x ) values ( ? )");
      opstmt.setExecuteBatch( 3 );
      // first insert 
      opstmt.setInt(1, 1 );
      // following insert is queued for execution by JDBC
      int numOfRowsInserted = opstmt.executeUpdate(); 
      System.out.println("num of rows inserted: " + numOfRowsInserted );
      // second insert 
      opstmt.setInt(1, 2 );
      // following insert is queued for execution by JDBC
      numOfRowsInserted = opstmt.executeUpdate(); 
      System.out.println("num of rows inserted: " + numOfRowsInserted );
      // third insert 
      opstmt.setInt(1, 3 );
      // since batch size is 3, the following insert will result
      // in JDBC sending all three inserts queued so far (including
      // the one below) for execution
      numOfRowsInserted = opstmt.executeUpdate(); 
      System.out.println("num of rows inserted: " + numOfRowsInserted );
      // fourth insert 
      opstmt.setInt(1, 4 );
      // following insert is queued for execution by JDBC
      numOfRowsInserted = opstmt.executeUpdate(); 
      System.out.println("num of rows inserted: " + numOfRowsInserted );
      // now if you want to explicitly send the batch, you can
      // use the sendBatch method as shown below.
      numOfRowsInserted = opstmt.sendBatch(); 
      System.out.println("num of rows sent for batch: " + numOfRowsInserted );
      oconn.commit();
    }
    catch (Exception e)
    {
      // handle the exception properly - in this case, we just
      // print a message, and rollback
      JDBCUtil.printExceptionAndRollback( oconn, e );
    }
    finally
    {
      // close the result set, the stmt and connection.
      // ignore any exceptions since we are in the
      // finally clause.
      JDBCUtil.close( opstmt );
      JDBCUtil.close( oconn );
    }
  }
}
