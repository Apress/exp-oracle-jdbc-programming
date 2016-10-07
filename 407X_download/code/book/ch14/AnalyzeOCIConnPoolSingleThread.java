/*
* This program demonstrates use of OCI connection pooling in a single threaded program.
* COMPATIBLITY NOTE: tested against 10.1.0.2.0.
*/
import java.sql.Connection;
import java.util.Properties;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import book.util.InputUtil;
class AnalyzeOCIConnPoolSingleThread
{
  public static void main(String args[]) throws Exception
  {
    int numOfSessionsToOpen = _getNumOfSessionsToOpen( args );
    String tnsAlias = "(DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = rmenon-lap)(PORT = 1521))) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = ora10g.us.oracle.com)))";
    OracleOCIConnectionPool oocp = new OracleOCIConnectionPool();
    oocp.setURL ( "jdbc:oracle:oci:@"+ tnsAlias ); 
    oocp.setUser("scott");           // user name
    oocp.setPassword("tiger");       // password
    // set pool config properties
    Properties poolConfigProperties = new Properties();
    poolConfigProperties.setProperty( OracleOCIConnectionPool.CONNPOOL_MIN_LIMIT, "3" );
    poolConfigProperties.setProperty( OracleOCIConnectionPool.CONNPOOL_INCREMENT, "1" );
    poolConfigProperties.setProperty( OracleOCIConnectionPool.CONNPOOL_MAX_LIMIT, "20" );
    long startTime = System.currentTimeMillis();
    oocp.setPoolConfig( poolConfigProperties );
    long endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish initial pool size of "+ oocp.getPoolSize() + " connections." );
    //print config properties
    System.out.println( "min Limit: " + oocp.getMinLimit() );
    System.out.println( "max Limit: " + oocp.getMaxLimit() );
    System.out.println( "connection increment : " + oocp.getConnectionIncrement() );
    System.out.println( "timeout: " + oocp.getTimeout() );
    System.out.println( "nowait: " + oocp.getNoWait() );
    System.out.println( "num of physical connections: " + oocp.getPoolSize() );
    Connection[] scottConnections = new Connection[ numOfSessionsToOpen ];
    InputUtil.waitTillUserHitsEnter( "before establishing scott connections");
    for( int i=0; i < numOfSessionsToOpen; i++ )
    {
      // time the process of establishing a connection
      startTime = System.currentTimeMillis();
      scottConnections[i] = oocp.getConnection("scott", "tiger");
      endTime = System.currentTimeMillis();
      System.out.println("It took " + (endTime-startTime) + 
        " ms to establish session # " + (i+1) + " (scott)." );
      System.out.println( "num of physical connections: " + oocp.getPoolSize() );
    }
    InputUtil.waitTillUserHitsEnter();
    Connection[] benchmarkConnections = new Connection[ numOfSessionsToOpen ];
    for( int i=0; i < numOfSessionsToOpen; i++ )
    {
      // time the process of establishing a connection
      startTime = System.currentTimeMillis();
      benchmarkConnections[i] = oocp.getConnection( "benchmark", "benchmark");
      endTime = System.currentTimeMillis();
      System.out.println("It took " + (endTime-startTime) + 
        " ms to establish the session # " + (i+1) + " (benchmark)." );
      System.out.println( "num of physical connections: " + oocp.getPoolSize() );
    }
    InputUtil.waitTillUserHitsEnter();
    // close all connections (or sessions)
    for( int i=0; i < numOfSessionsToOpen; i++ )
    {
      if( benchmarkConnections[i] != null )
        benchmarkConnections[i].close();
      if( scottConnections[i] != null )
        scottConnections[i].close();
    }
    // Now set the min limit to 6
    poolConfigProperties.setProperty( OracleOCIConnectionPool.CONNPOOL_MIN_LIMIT, "6" );
    startTime = System.currentTimeMillis();
    oocp.setPoolConfig( poolConfigProperties );
    endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish initial pool size of "+ oocp.getPoolSize() + " connections." );
    //print config properties
    System.out.println( "min Limit: " + oocp.getMinLimit() );
    System.out.println( "max Limit: " + oocp.getMaxLimit() );
    System.out.println( "connection increment : " + oocp.getConnectionIncrement() );
    System.out.println( "timeout: " + oocp.getTimeout() );
    System.out.println( "nowait: " + oocp.getNoWait() );
    System.out.println( "num of physical connections: " + oocp.getPoolSize() );
    scottConnections = new Connection[ numOfSessionsToOpen ];
    InputUtil.waitTillUserHitsEnter( "before establishing scott connections");
    for( int i=0; i < numOfSessionsToOpen; i++ )
    {
      // time the process of establishing a connection
      startTime = System.currentTimeMillis();
      scottConnections[i] = oocp.getConnection("scott", "tiger");
      endTime = System.currentTimeMillis();
      System.out.println("It took " + (endTime-startTime) + 
        " ms to establish session # " + (i+1) + " (scott)." );
      System.out.println( "num of physical connections: " + oocp.getPoolSize() );
    }
    InputUtil.waitTillUserHitsEnter();
    benchmarkConnections = new Connection[ numOfSessionsToOpen ];
    for( int i=0; i < numOfSessionsToOpen; i++ )
    {
      // time the process of establishing a connection
      startTime = System.currentTimeMillis();
      benchmarkConnections[i] = oocp.getConnection( "benchmark", "benchmark");
      endTime = System.currentTimeMillis();
      System.out.println("It took " + (endTime-startTime) + 
        " ms to establish the session # " + (i+1) + " (benchmark)." );
      System.out.println( "num of physical connections: " + oocp.getPoolSize() );
    }
    InputUtil.waitTillUserHitsEnter();
    // close all connections (or sessions)
    for( int i=0; i < numOfSessionsToOpen; i++ )
    {
      if( benchmarkConnections[i] != null )
        benchmarkConnections[i].close();
      if( scottConnections[i] != null )
        scottConnections[i].close();
    }
  }// end of main
  private static int _getNumOfSessionsToOpen( String[] args )
  {
    int numOfSessionsToOpen = 3; //by default open 3 sessions
    if( args.length == 1 )
    {
      numOfSessionsToOpen = Integer.parseInt( args[0] );
    }
    System.out.println( "Num of sessions to open for scott and benchmark each = " + numOfSessionsToOpen);
    return numOfSessionsToOpen;
  }
}// end of program
