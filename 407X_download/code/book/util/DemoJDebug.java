/*
 * This class demonstrates how to use JDebug class
 */
package book.util;
import java.sql.Connection;
import java.sql.CallableStatement;
import oracle.jdbc.OracleTypes;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import book.util.JDBCUtil;

import java.sql.SQLException;
import java.sql.ResultSet;

public class DemoJDebug
{
  public static void main( String[] args ) throws SQLException
  {
    Connection conn = null;
    String debugFlag = JDebug.LOG_FOR_TRACE_FILES_AND_MIDTIER;
    String dbUserName = "BENCHMARK";
    String directory = JDebug.DEFAULT_DEBUG_DIRECTORY;
    String debugFileName = JDebug.DEFAULT_DEBUG_FILE_NAME;
    try
    {
      conn = JDBCUtil.getConnection( "benchmark", "benchmark", "ora10g" );
      JDebug.init( conn, debugFlag, JDebug.ALL_MODULES, 
        directory,
        debugFileName,
        "benchmark", JDebug.YES, JDebug.DEFAULT_DATE_FORMAT, 
        JDebug.DEFAULT_NAME_LENGTH, JDebug.YES );
      JDebug.printDebugMessage( conn, debugFlag, 
        "Before invoking PL/SQL Code" );
      _callDemoDebug( conn );
      JDebug.printDebugMessage( conn, debugFlag, 
        "Before invoking PL/SQL Code" );
      System.out.println( JDebug.getDebugMessageAndFlush( conn, debugFlag ) );
      JDebug.clear( conn, debugFlag, dbUserName, directory, debugFileName );
    }
    catch (SQLException e)
    {
      // handle the exception properly - we just print the stack trace.
      JDBCUtil.printException ( e );
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  private static void _callDemoDebug( Connection conn ) throws SQLException
  {
    CallableStatement cstmt = null;
    try
    {
      cstmt = conn.prepareCall( "{call demo_debug()}" );
      cstmt.execute();
    }
    finally
    {
      JDBCUtil.close( cstmt );
    }
  }
}


