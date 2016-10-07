/* This class runs a query against SCOTT schema.
 * It serves to demonstrate the following concepts:
 * 1. How to connect to the Oracle Database
 * 2. How to run a query against the database
 * 3. How to do the clean up afterwards.
 * COMPATIBLITY NOTE: tested against 10.1.0.2.0. and 9.2.0.1.0 */
// importing standard JDBC classes - standard JDBC classes are under
// java.sql class hierarchy
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
// importing Oracle specific JDBC classes - Oracle specific JDBC 
// classes are under oracle.jdbc class hierarchy
import oracle.jdbc.pool.OracleDataSource;
class GetEmpDetails
{
  public static void main(String args[])
  {
    String user = "scott"; // modify this value to your db user
    String password = "tiger"; // modify this value to your db user password
    String host = "rmenon-lap"; // modify this value to your db host
    String port = "1521"; // modify this value to your db listener port
    String dbService = "ora10g"; // modify this value to your db service name
    String thinDriverPrefix = "jdbc:oracle:thin";
    String thinConnectURL = thinDriverPrefix + ":" + user + "/" +
     password + "@" + host + ":" + port + ":" + dbService;
    // the string value = "jdbc:oracle:thin:scott/tiger@usunrat24:1521:ora10g";
    System.out.println("Database connect url: " + thinConnectURL);
    System.out.print("Establishing connection to the database...");
    ResultSet rset = null;
    Connection conn = null;
    Statement stmt = null;
    try
    {
      // instantiate and initialize OracleDataSource 
      OracleDataSource ods = new OracleDataSource();
      ods.setURL(thinConnectURL );
      // get the connection
      conn = ods.getConnection();
      System.out.println("Connected.\nPrinting query results ...\n");
      // Create a stmt
      stmt = conn.createStatement();
      // execute the query
      rset = stmt.executeQuery( "select empno, ename, job from mp" );
      // declare constants for column indexes in the query (indexes begin with 1)
      final int EMPNO_COLUMN_INDEX = 1;
      final int ENAME_COLUMN_INDEX = 2;
      final int JOB_COLUMN_INDEX = 3;
      // print the results
      while (rset.next())
      {
        int empNo = rset.getInt ( EMPNO_COLUMN_INDEX );
        String empName = rset.getString ( ENAME_COLUMN_INDEX );
        String empJob = rset.getString ( JOB_COLUMN_INDEX );
        System.out.println( empNo + " " + empName + " " + empJob );
      }
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message and stack trace and exit the application
      System.err.println ("error message: " + e.getMessage() );
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
    finally
    {
      // close the result set, the stmt and connection.
      // ignore any exceptions since we are in the
      // finally clause.
      try
      {
        if( rset != null )
          rset.close();
        if( stmt != null )
          stmt.close();
        if( conn != null )
          conn.close();
      }
      catch ( SQLException ignored ) {ignored.printStackTrace(); }
    }
  }
}
