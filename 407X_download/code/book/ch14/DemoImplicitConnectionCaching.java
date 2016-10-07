/* This program demonstrates implicit conneciton caching.
* COMPATIBLITY NOTE: runs successfully 10.1.0.2.0
*/
import java.sql.Connection;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import book.util.InputUtil;
class DemoImplicitConnectionCaching
{
  public static void main(String args[]) throws Exception
  {
    OracleDataSource ods = new OracleDataSource();
    ods.setURL ( "jdbc:oracle:thin:@rmenon-lap:1521:ora10g" ); 
    ods.setUser("scott");           // user name
    ods.setPassword("tiger");       // password
    // enable implicit caching
    ods.setConnectionCachingEnabled( true );
    // set cache properties
    Properties cacheProperties = new Properties();
    cacheProperties.setProperty( "InitialLimit", "3" );
    cacheProperties.setProperty( "MinLimit", "3" );
    cacheProperties.setProperty( "MaxLimit", "15" );
    ods.setConnectionCacheProperties(cacheProperties);
    // time the process of establishing first connection
    long startTime = System.currentTimeMillis();
    Connection conn1 = ods.getConnection("scott", "tiger");
    long endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish the 1st connection (scott)." );
    InputUtil.waitTillUserHitsEnter();
    // time the process of establishing second connection
    startTime = System.currentTimeMillis();
    Connection conn2 = ods.getConnection("scott", "tiger");
    endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish the 2nd connection (scott)." );
    InputUtil.waitTillUserHitsEnter();
    // time the process of establishing 3rd connection
    startTime = System.currentTimeMillis();
    Connection conn3 = ods.getConnection("benchmark", "benchmark");
    endTime = System.currentTimeMillis();
    System.out.println("It took " + (endTime-startTime) + 
      " ms to establish the 3rd connection (benchmark)." );
    InputUtil.waitTillUserHitsEnter();
    // close all connections
    conn1.close();
    InputUtil.waitTillUserHitsEnter("After closing the first connection.");
    conn2.close();
    conn3.close();
  }// end of main
}// end of program
