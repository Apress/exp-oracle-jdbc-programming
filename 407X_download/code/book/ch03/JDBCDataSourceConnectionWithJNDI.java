/* This class demonstrates how to connect to a database using DataSource interface using JNDI.
 * COMPATIBLITY NOTE: tested against 10.1.0.2.0. and 9.2.0.1.0 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
class JDBCDataSourceConnectionWithJNDI
{
  public static void main (String args[])
  {
    // Connecting to Oracle using Data source using JNDI
    // Get the connection WITH JNDI - this needs jndi.jar
    // in your CLASSPATH. This also needs the jar files associated
    // with the reference implementation of JNDI ( in my case, 
    // Sun's JNDI FileSystems implementation; these were
    // providerutil.jar and fscontext.jar files)in your CLASSPATH.
    OracleDataSource ods = null;
    Connection connection = null;
    try
    {
      Properties properties = new Properties();
      properties.setProperty( Context.INITIAL_CONTEXT_FACTORY,
       "com.sun.jndi.fscontext.RefFSContextFactory");
      properties.setProperty( Context.PROVIDER_URL,
       "file:B:/code/book/ch03/jndi_test");
      // First register with JNDI to associate a logical name with
      // the connection.
      Context context = new InitialContext(properties);
      // create the data source
      ods = new OracleDataSource();
      ods.setDriverType ( "thin" );      // type of driver
      ods.setServerName ( "rmenon-lap" ); // database server name
      ods.setNetworkProtocol("tcp");     // tcp is the default anyway
      ods.setDatabaseName("ora10g");     // Oracle SID
      ods.setPortNumber(1521);           // listener port number
      // associate a logical name with the connection service.
      // Following recommended convention, we use a subcontext
      // jdbc and put our name under it as jdbc/testdb
      context.bind ( "jdbc/testdb", ods );
      DataSource dsUsingJNDI = (DataSource) context.lookup("jdbc/testdb");
      connection = dsUsingJNDI.getConnection("scott", "tiger");
      System.out.println( "SUCCESS!");
    }
    catch (NamingException e)
    {
      // handle the exception properly - in this case, we just
      // print a message and stack trace and exit the application
      System.out.println ("ERROR: in registering with JNDI! Exiting .." );
      System.out.println (e.getMessage() );
      e.printStackTrace();
      System.exit (1);
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

