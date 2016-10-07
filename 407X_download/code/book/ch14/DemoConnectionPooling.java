/* This program demonstrates how to create a pooled connection
* and obtain a logical connection from it.
* COMPATIBLITY NOTE: runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.Connection;
import javax.sql.PooledConnection;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import book.util.InputUtil;

class DemoConnectionPooling
{
  public static void main(String args[]) throws Exception
  {
    OracleConnectionPoolDataSource ocpds = new OracleConnectionPoolDataSource();
    ocpds.setURL ( "jdbc:oracle:thin:@usunrat24.us.oracle.com:1521:ora92i" ); 
    ocpds.setUser("scott");           // user name
    ocpds.setPassword("tiger");       // password
    PooledConnection pooledConnection = ocpds.getPooledConnection();
    InputUtil.waitTillUserHitsEnter("Done creating pooled connection.");
    Connection connection = pooledConnection.getConnection();
    InputUtil.waitTillUserHitsEnter("Done getting connection from pooled connection object.");
    connection.close();
    InputUtil.waitTillUserHitsEnter("Done closing logical connection");
    pooledConnection.close();
    InputUtil.waitTillUserHitsEnter("Done closing pooled connection");
  }
}
