/*
* This program illustrates use of standard update batching.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0. and  9.2.0.1.0.
*/
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.Statement; // for accessing constants only
import book.util.JDBCUtil;
import book.util.Util;
class DemoStandardUpdateBatching
{
  public static void main(String args[])
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    PreparedStatement pstmt = null;
    int[] updateCounts = null;
    try
    {
      // get connection, set auto commit to false in JDBCUtil method
      // Note: setting auto commit to false is required
      // especially when you are using update batching.
      // of course, you should do this anyway for
      // transaction integrity and performance esp.
      // when developing applications on Oracle.
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      // prepare a stmt to insert data
      pstmt = conn.prepareStatement( "insert into t1( x ) values ( ? )");
      // first insert 
      pstmt.setInt(1, 4 );
      pstmt.addBatch();
      // second insert 
      pstmt.setInt(1, 5 );
      pstmt.addBatch();
      // third insert 
      pstmt.setInt(1, 1 );
      pstmt.addBatch();
      // Manually execute the batch
      updateCounts = pstmt.executeBatch();
      System.out.println( "Inserted " + updateCounts.length + " rows successfully");
      conn.commit();
    }
    catch (BatchUpdateException e)
    {
      // Check if each of the statements in batch was 
      // successful - if not throw Exception
      updateCounts = e.getUpdateCounts();
      for( int k=0; k < updateCounts.length; k++ )
      {
        /*
           For a standard prepared statement batch, it is impossible 
           to know the number of rows affected in the database by 
           each individual statement in the batch. 
           According to the JDBC 2.0 specification, a value of 
           Statement.SUCCESS_NO_INFO indicates that the operation 
           was successful but the number of rows affected is unknown. 
         */
        if( updateCounts[k] != Statement.SUCCESS_NO_INFO )
        {
          String message = "Error in standard batch update - Found a value" +
            " of " + updateCounts[k] + " in the update count "+
            "array for statement number " + k;
          System.out.println( message );
        }
      }
      // print the exception error message and rollback
      JDBCUtil.printExceptionAndRollback( conn, e );
    }
    catch (Exception e)
    {
      // handle the generic exception; print error message and rollback
      JDBCUtil.printExceptionAndRollback( conn, e );
    }
    finally
    {
      // release JDBC resource in the finally clause.
      JDBCUtil.close( pstmt );
      JDBCUtil.close( conn );
    }
  } // end of main
} // end of program
