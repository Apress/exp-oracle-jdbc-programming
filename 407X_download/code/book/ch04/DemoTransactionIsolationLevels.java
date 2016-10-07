/* This class demonstrates how to set different transaction levels in Oracle.
 * COMPATIBLITY NOTE: tested against 10.1.0.2.0. and 9.2.0.1.0 */
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import book.util.JDBCUtil;
class DemoTransactionIsolationLevels
{
  public static void main( String[] args ) throws Exception
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection( "scott", "tiger", args[0] );
      int txnIsolationLevel = conn.getTransactionIsolation();
      System.out.println( "Default transaction isolation level: " + 
        _getTransactionIsolationDesc( txnIsolationLevel ) );
      conn.setTransactionIsolation( Connection.TRANSACTION_SERIALIZABLE );
      txnIsolationLevel = conn.getTransactionIsolation();
      System.out.println( "transaction isolation level is now " + 
        _getTransactionIsolationDesc( txnIsolationLevel ) );
    }
    finally
    {
      JDBCUtil.close( conn );
    }
    String stmtString = "begin set transaction read only; end;";
    CallableStatement cstmt = null;
    try
    {
      conn = JDBCUtil.getConnection( "scott", "tiger", "ora10g" );
      System.out.println( "Setting the transaction isolation level to READ ONLY");
      cstmt = conn.prepareCall( stmtString );
      cstmt.execute();
      int txnIsolationLevel = conn.getTransactionIsolation();
      System.out.println( "transaction isolation level is now " + 
        _getTransactionIsolationDesc( txnIsolationLevel ) );
    }
    finally
    {
      JDBCUtil.close( cstmt );
      JDBCUtil.close( conn );
    }
  }
  private static String _getTransactionIsolationDesc ( int txnIsolationLevel )
  {
    switch( txnIsolationLevel )
    {
      case Connection.TRANSACTION_READ_COMMITTED:
        return "READ_COMMITTED";
      case Connection.TRANSACTION_SERIALIZABLE:
        return "TRANSACTION_SERIALIZABLE";
      case Connection.TRANSACTION_READ_UNCOMMITTED:
        return "TRANSACTION_READ_UNCOMMITTED";
      case Connection.TRANSACTION_REPEATABLE_READ:
        return "TRANSACTION_REPEATABLE_READ";
      case Connection.TRANSACTION_NONE:
        return "TRANSACTION_NONE";
    }
    return "UNKNOWN";
  }
}
