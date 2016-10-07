/*This program demonstrates how to use the Savepoint feature
* that has been introduced in JDBC 3.0.
* COMPATIBLITY NOTE:
*   runs successfully on 10.1.0.2.0 and 9.2.0.1.0 */
import java.util.Date;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.PreparedStatement;
import java.sql.Connection;
import book.util.JDBCUtil;
public class DemoSavepoint
{
  public static void main(String args[]) throws SQLException
  {
    Connection conn = null;
    PreparedStatement pstmtLog = null;
    PreparedStatement pstmt = null;
    Savepoint savepoint = null;
    String insertTxnLogStmt = 
      "insert into transaction_log(txn_name, log_message) " +
      "values( ?, ? )";
    String insertStmt = "insert into t1(x) values( ? )";
    try
    {
      try
      {
        conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
        pstmtLog = conn.prepareStatement( insertTxnLogStmt ) ;
        _log( pstmtLog, "demo_savepoint", "starting the txn to demo savepoints at: " + new Date() );
        savepoint = conn.setSavepoint();
        // our real transaction begins
        pstmt = conn.prepareStatement( insertStmt ) ;
        pstmt.setInt( 1, 1 );
        pstmt.executeUpdate();
        pstmt.setInt( 1, 2 );
        pstmt.executeUpdate();
        pstmt.setInt( 1, 3 );
        pstmt.executeUpdate();
      }
      catch (SQLException e)
      {
        // an error occured, we rollback to our save point
        conn.rollback( savepoint );
        // and log the error message
        _log( pstmtLog, "demo_savepoint", "Failed with error: " + e.getMessage());
        // we commit the log data
        conn.commit();
        // and throw the exception
        throw e;
      }
      // if we reach here - it means transaction was successful
      // so we log the "success" message
      _log( pstmtLog, "demo_savepoint", "Successfully ended at: " + new Date() );
      // commit the changes to the database including the log message
      conn.commit();
    }
    finally
    {
      // release JDBC resources in the finally clause.
      JDBCUtil.close( pstmtLog );
      JDBCUtil.close( pstmt );
      JDBCUtil.close( conn );
    }
  }

  private static void _log( PreparedStatement pstmtLog, String txnName, 
    String logMessage ) throws SQLException
  {
    pstmtLog.setString( 1, txnName );
    pstmtLog.setString( 2, logMessage );
    pstmtLog.executeUpdate();
  }
}
