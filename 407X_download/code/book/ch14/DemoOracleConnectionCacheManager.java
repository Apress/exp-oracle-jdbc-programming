/* This program demonstrates using the Oracle connection cache manager API.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0
*/
import java.sql.Connection;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.pool.OracleConnectionCacheManager;
class DemoOracleConnectionCacheManager
{
  public static void main(String args[]) throws Exception
  {
    OracleDataSource ods = new OracleDataSource();
    ods.setURL ( "jdbc:oracle:thin:@rmenon-lap:1521:ora10g" ); 
    ods.setUser("scott");           // user name
    ods.setPassword("tiger");       // password
    // enable implicit caching
    ods.setConnectionCachingEnabled( true );
    // set cache properties (use a properties file in production code.)
    Properties cacheProperties = new Properties();
    cacheProperties.setProperty( "InitialLimit", "2" );
    cacheProperties.setProperty( "MinLimit", "3" );
    cacheProperties.setProperty( "MaxLimit", "15" );
    ods.setConnectionCacheProperties(cacheProperties);
    System.out.println("Connection Cache Properties: ");
    System.out.println("\tInitialLimit: 2");
    System.out.println("\tMinLimit: 3");
    System.out.println("\tMaxLimit: 15");
    // create the connection cache
    OracleConnectionCacheManager occm = 
      OracleConnectionCacheManager.getConnectionCacheManagerInstance();
    occm.createCache( CONNECTION_CACHE_NAME, ods, cacheProperties );
    System.out.println( "Just after creating the cache, " +
      "active connections: " +
      occm.getNumberOfActiveConnections( CONNECTION_CACHE_NAME )  + 
      ", available connections: " +
      occm.getNumberOfAvailableConnections( CONNECTION_CACHE_NAME ) );
    // get first connection
    Connection conn1 = ods.getConnection("scott", "tiger");
    System.out.println( "After getting first connection from cache, " +
      "active connections: " +
      occm.getNumberOfActiveConnections( CONNECTION_CACHE_NAME )  + 
      ", available connections: " +
      occm.getNumberOfAvailableConnections( CONNECTION_CACHE_NAME ) );
    conn1.close();
    System.out.println( "After closing first connection, " +
      "active connections: " +
      occm.getNumberOfActiveConnections( CONNECTION_CACHE_NAME )  + 
      ", available connections: " +
      occm.getNumberOfAvailableConnections( CONNECTION_CACHE_NAME ));
    // get 3 connections to go beyond the InitialMinimum limit
    Connection conn2 = ods.getConnection("scott", "tiger");
    Connection conn3 = ods.getConnection("scott", "tiger");
    Connection conn4 = ods.getConnection("scott", "tiger");
    System.out.println( "After getting 3 connections, " +
      "active connections: " +
      occm.getNumberOfActiveConnections( CONNECTION_CACHE_NAME )  + 
      ", available connections: " +
      occm.getNumberOfAvailableConnections( CONNECTION_CACHE_NAME ) );
    // close one connection - the number of connections should not
    // go below 3 since we set a MinLimit value of 3.
    conn2.close();
    System.out.println( "After closing 1 connection, " +
      "active connections: " +
      occm.getNumberOfActiveConnections( CONNECTION_CACHE_NAME )  + 
      ", available connections: " +
      occm.getNumberOfAvailableConnections( CONNECTION_CACHE_NAME ) );
    conn3.close();
    conn4.close();
    // what happens if we disable cache and try to get a connection?
    occm.disableCache( CONNECTION_CACHE_NAME );
    Connection conn5 = ods.getConnection("scott", "tiger");
    conn5.close();
  }// end of main
  private static final String CONNECTION_CACHE_NAME = "myConnectionCache";
}// end of program
