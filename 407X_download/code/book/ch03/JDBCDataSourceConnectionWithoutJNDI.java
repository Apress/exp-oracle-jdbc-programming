/* This class demonstrates how to connect to a database using DataSource interface without using JNDI.
 * COMPATIBLITY NOTE: tested against 10.1.0.2.0. and 9.2.0.1.0 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
class JDBCDataSourceConnectionWithoutJNDI
{
  public static void main (String args[])
  {
    /*-------------------------------------------------------------------*/
    /*---- RECOMMENDED WAY OF CONNECTING TO ORACLE USING DATA SOURCES ---*/
    /*-------------------------------------------------------------------*/
    // Connecting to Oracle using Data source without JNDI
    OracleDataSource ods = null;
    try
    {
      ods = new OracleDataSource();
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message and stack trace and exit the application
      System.err.println ("ERROR: Could not instantiate data source! Exiting .." );
      System.err.println ( e.getMessage() );
      e.printStackTrace();
      Runtime.getRuntime().exit (1);
    }
    // set the properties that define the connection
    ods.setDriverType ( "thin" );      // type of driver
    ods.setServerName ( "rmenon-lap" ); // database server name
    ods.setNetworkProtocol("tcp");     // tcp is the default anyway
    ods.setDatabaseName("ora10g");     // Oracle SID
    ods.setPortNumber(1521);           // listener port number
    ods.setUser("scott");              // user name
    ods.setPassword("tiger");          // password
    // get the connection without JNDI
    Connection connection = null;
    try
    {
      connection = ods.getConnection();
      System.out.println( "SUCCESS!");
    // do some work with the connection
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message and stack trace and exit the application
      System.err.println ("ERROR: Could not get the connection! Exiting .." );
      System.err.println ( e.getMessage() );
      e.printStackTrace();
      Runtime.getRuntime().exit (1);
    }
    finally
    {
      try
      {
        if( connection != null )
          connection.close();
      }
      catch (SQLException ignore ) {}
    }
  }
}

