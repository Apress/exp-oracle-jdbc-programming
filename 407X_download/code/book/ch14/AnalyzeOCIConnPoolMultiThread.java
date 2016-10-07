/** This program demonstrates use of OCI connection pooling in a multi threaded program.
* COMPATIBLITY NOTE: tested against 10.1.0.2.0.
*/
import java.util.Properties;
import oracle.jdbc.pool.OracleOCIConnectionPool;
public class AnalyzeOCIConnPoolMultiThread
{
  public static void main( String [] args ) throws Exception
  {
    _numOfSessionsToOpen = _getNumOfSessionsToOpen( args );
    String tnsAlias = "(DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = rmenon-lap)(PORT = 1521))) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = ora10g.us.oracle.com)))";
    OracleOCIConnectionPool cpool = new OracleOCIConnectionPool();
    cpool.setURL ( "jdbc:oracle:oci:@"+ tnsAlias ); 
    cpool.setUser("scott");           // user name
    cpool.setPassword("tiger");       // password
    Properties poolConfigProps = new Properties( ) ;
    poolConfigProps.put(OracleOCIConnectionPool.CONNPOOL_MIN_LIMIT, "2") ;
    poolConfigProps.put(OracleOCIConnectionPool.CONNPOOL_INCREMENT, "1") ;
    poolConfigProps.put(OracleOCIConnectionPool.CONNPOOL_MAX_LIMIT, "20") ;
    cpool.setPoolConfig(poolConfigProps);
    System.out.println ("Min poolsize Limit = " + cpool.getMinLimit());
    System.out.println ("Max poolsize Limit = " + cpool.getMaxLimit());
    System.out.println ("Connection Increment = " + cpool.getConnectionIncrement());
    Thread [] threads = new Thread[ _numOfSessionsToOpen ];
    for( int i = 0; i<threads.length; i++ )
    {
      (threads[i] = new WorkerThread( cpool, i )).start();
    }

    // wait till all threads are done.
    for( int i = 0; i<threads.length; i++ )
    {
      threads[i].join();
    }

    cpool.close();
  }

  private static int _getNumOfSessionsToOpen( String[] args )
  {
    int numOfSessionsToOpen = 6; //by default open 6 sessions
    if( args.length == 1 )
    {
      numOfSessionsToOpen = Integer.parseInt( args[0] );
    }
    System.out.println("Total number of sessions to open for " +
      "scott and benchmark = " + numOfSessionsToOpen);

    return numOfSessionsToOpen;
  }

  private static int _numOfSessionsToOpen;
}

