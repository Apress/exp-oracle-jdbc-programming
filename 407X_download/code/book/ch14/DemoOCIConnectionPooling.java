/*
* This program demonstrates explicit statement caching.
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.Connection;
import java.util.Properties;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import book.util.InputUtil;
class DemoOCIConnectionPooling
{
  public static void main(String args[]) throws Exception
  {
    String tnsAlias = "(DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = rmenon-lap)(PORT = 1521))) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = ora10g.us.oracle.com)))";
    OracleOCIConnectionPool ods = new OracleOCIConnectionPool();
    ods.setURL ( "jdbc:oracle:oci:@"+ tnsAlias ); 
    ods.setUser("scott");           // user name
    ods.setPassword("tiger");       // password
    // set cache properties
    Properties cacheProperties = new Properties();
    cacheProperties.setProperty( 
      OracleOCIConnectionPool.CONNPOOL_MIN_LIMIT, "2" );
    cacheProperties.setProperty( 
      OracleOCIConnectionPool.CONNPOOL_INCREMENT, "1" );
    cacheProperties.setProperty( 
      OracleOCIConnectionPool.CONNPOOL_MAX_LIMIT, "10" );
    ods.setPoolConfig( cacheProperties );
    System.out.println("Min Limit: 2");
    System.out.println("Max Limit: 10");
    System.out.println("Increment : 1");
    System.out.println("pool size:" + ods.getPoolSize());
    // time the process of establishing first connection
    long startTime = System.currentTimeMillis();
    Connection conn1 = ods.getConnection("scott", "tiger");
    long endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish the first connection (scott)." );
    InputUtil.waitTillUserHitsEnter();
    // time the process of establishing 2nd connection
    startTime = System.currentTimeMillis();
    Connection conn2 = ods.getConnection("scott", "tiger");
    endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish the second connection (scott)." );
    InputUtil.waitTillUserHitsEnter();
    // time the process of establishing 3rd connection
    startTime = System.currentTimeMillis();
    Connection conn3 = ods.getConnection("benchmark", "benchmark");
    endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish the 3rd connection (benchmark)." );
    InputUtil.waitTillUserHitsEnter();
    // time the process of establishing 4th connection
    Connection conn4= ods.getConnection("benchmark", "benchmark");
    endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish the 4th connection (benchmark)." );
    InputUtil.waitTillUserHitsEnter();
    // time the process of establishing 5th connection
    Connection conn5= ods.getConnection("benchmark", "benchmark");
    endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish the 5th connection (benchmark)." );
    InputUtil.waitTillUserHitsEnter();
    // time the process of closing a connection
    startTime = System.currentTimeMillis();
    conn1.close();
    endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to close a connection." );
    InputUtil.waitTillUserHitsEnter();
    conn2.close();
    conn3.close();
    conn4.close();
    conn5.close();
  }
}
