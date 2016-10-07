/* This program shows how to insert, update and delete data using PreparedStatement interface.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0.
*    against 9.2.0.1.0, you have to comment out the
*    code using binding by name feature to compile and
*    run this as bind by name is not supported in 9i.
*/
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import oracle.jdbc.OraclePreparedStatement;
import book.util.JDBCUtil;
import book.util.Util;
class DemoInsUpdDelUsingPreparedStatement
{
  public static void main(String args[])
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    PreparedStatement pstmt = null;
    try
    {
      // get connection
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      _demoInsert( conn );
      _demoUpdate( conn );
      _demoDelete( conn );
      conn.commit();
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message, and rollback
      JDBCUtil.printExceptionAndRollback( conn, e );
    }
    finally
    {
      // release JDBC resources in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  // demo insert
  private static void _demoInsert( Connection conn ) throws SQLException
  {
    PreparedStatement pstmt = null;
    try
    {
      // prepare the statement
      pstmt = conn.prepareStatement( "insert into t1 (x, y, z) values ( ?, ?, ? )");
      // bind the values
      pstmt.setInt(1, 5 ); // bind the value 5 to the first placeholder
      pstmt.setString(2, "string 5" );
      pstmt.setDate(3, new java.sql.Date( new java.util.Date().getTime()));
      // execute the statement
      int numOfRowsInserted  = pstmt.executeUpdate();
      System.out.println( "Inserted " + numOfRowsInserted + " row(s)" );
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( pstmt );
    }
  }
  // demo update use bind by name
  private static void _demoUpdate( Connection conn ) throws SQLException
  {
    OraclePreparedStatement opstmt = null;
    try
    {
      // prepare the statement
      opstmt = (OraclePreparedStatement) 
        conn.prepareStatement( "update t1 set y = :y where x = :x");
      // bind the values by name.
      opstmt.setStringAtName("y", "string 1 updated" ); 
      opstmt.setIntAtName("x", 1 );
      // execute the statement
      int numOfRowsUpdated  = opstmt.executeUpdate();
      System.out.println( "Updated " + numOfRowsUpdated + " row(s)" );
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( opstmt );
    }
  }
  // demo delete
  private static void _demoDelete( Connection conn ) throws SQLException
  {
    PreparedStatement pstmt = null;
    try
    {
      // prepare the statement
      pstmt = conn.prepareStatement( "delete from t1 where x = ?");
      // bind the values
      pstmt.setInt(1, 2 );
      // execute the statement
      int numOfRowsDeleted  = pstmt.executeUpdate();
      System.out.println( "Deleted " + numOfRowsDeleted + " row(s)" );
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( pstmt );
    }
  }
} // end of program
