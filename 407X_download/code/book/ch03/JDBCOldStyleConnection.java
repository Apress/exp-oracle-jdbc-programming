/* This class demonstrates how to connect to a database using DriverManager class. 
 * COMPATIBLITY NOTE: tested against 10.1.0.2.0. and 9.2.0.1.0 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import oracle.jdbc.OracleDriver;
class JDBCOldStyleConnection
{
  public static void main (String args[]) 
  {
    /*--------------------------------------------------------------*/
    /*---- OLD STYLE OF ESTABLISHING CONNECTION NOT RECOMMENDED --*/
    /*--------------------------------------------------------------*/
    // set up a TNS connect descriptor that will be used subsequently
    // below

    // First register the driver. This would enable the JVM to load the 
    // JDBC driver implementation into memory
    try
    {
      DriverManager.registerDriver( new OracleDriver());
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message and stack trace and exit the application
      System.err.println ("ERROR: Could not register the driver! Exiting .." );
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
    // We assume the following in all examples below (You need to modify this 
    // information appropriately for your code to work):
    // 
    // hostname = rmenon-lap
    // listener port number = 1521
    // Oracle SID = ora10g
    //
    // Thin driver using host:port_sid 
    //
    // Set up the URL that forms the address of the database
    // to which you want to point to.
    // The generic form of URL for Oracle database is:
    //     jdbc:oracle:driver_type:@pointer_to_database
    //     pointer_to_database := {host:port:sid | net_service_name |
    //                             connect_descriptor }

    Connection thinDriverConnection = null;
    try
    {
      String thinDriverURL = "jdbc:oracle:thin:@rmenon-lap:1521:ora10g";

      thinDriverConnection = DriverManager.getConnection ( 
        thinDriverURL, "scott", "tiger" );
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message and stack trace and exit the application
      System.err.println ("ERROR: Could not get connection! Exiting .." );
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
    finally
    {
      try
      {
        if( thinDriverConnection != null )
          thinDriverConnection.close();
      }
      catch (SQLException ignore ) {}
    }
    String connectDescriptor = "(DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = rmenon-lap)(PORT = 1521))) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = ora10g.us.oracle.com)))";
    // Thin driver connection using connect descriptor
    try
    {
      String thinDriverConnectDescriptorURL = 
        "jdbc:oracle:thin:@" + connectDescriptor;

      thinDriverConnection = DriverManager.getConnection ( 
        thinDriverConnectDescriptorURL, "scott", "tiger" );
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message and stack trace and exit the application
      System.err.println ("ERROR: Could not get connection! Exiting .." );
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
    finally
    {
      try
      {
        if( thinDriverConnection != null )
          thinDriverConnection.close();
      }
      catch (SQLException ignore ) { }
    }

    // OCI driver connection using net service name of ora10g
    Connection ociDriverConnection = null;
    try
    {
      String ociDriverURL = "jdbc:oracle:oci:@ora10g";

      ociDriverConnection = DriverManager.getConnection ( 
        ociDriverURL, "scott", "tiger" );
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message and stack trace and exit the application
      System.err.println ("ERROR: Could not get connection! Exiting .." );
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
    finally
    {
      try
      {
        if( ociDriverConnection != null )
          ociDriverConnection.close();
      }
      catch (SQLException ignore ) { }
    }

    // OCI driver connection using connect descriptor
    try
    {
      String ociDriverConnectDescriptorURL = 
        "jdbc:oracle:oci:@" + connectDescriptor;

      ociDriverConnection = DriverManager.getConnection ( 
        ociDriverConnectDescriptorURL, "scott", "tiger" );
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message and stack trace and exit the application
      System.err.println ("ERROR: Could not get connection! Exiting .." );
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
    finally
    {
      try
      {
        if( ociDriverConnection != null )
          ociDriverConnection.close();
      }
      catch (SQLException ignore ) { }
    }
  }//end of main
}// end of program

