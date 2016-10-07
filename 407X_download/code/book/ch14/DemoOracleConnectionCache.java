/* This program demonstrates how to use Oracle connection cache.
* COMPATIBLITY NOTE: runs successfully against 9.2.0.1.0 and 10.1.0.2.0 
*/
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.pool.OracleConnectionCacheImpl;
import book.util.JDBCUtil;
class DemoOracleConnectionCache
{
  public static void main(String args[]) throws Exception
  {
    OracleConnectionCacheImpl occi = new OracleConnectionCacheImpl();
    occi.setURL ( "jdbc:oracle:thin:@rmenon-lap:1522:ora92" ); 
    occi.setUser("scott");     // user name
    occi.setPassword("tiger"); // password
    occi.setMaxLimit( 3 );     // max # of connections in pool
    occi.setMinLimit( 1 );     // min # of connections in pool
    System.out.println( "By default, the cache scheme is: " + occi.getCacheScheme() );
    occi.setCacheScheme( OracleConnectionCacheImpl.DYNAMIC_SCHEME ); 
    int maxLimit = occi.getMaxLimit();
    System.out.println( "Max Limit: " + maxLimit );
    System.out.println( "Demo of dynamic cache scheme - the default" );
    _getOneMoreThanMaxConnections( occi, maxLimit );
    System.out.println( "\nDemo of fixed return null cache scheme" );
    occi.setCacheScheme( OracleConnectionCacheImpl.FIXED_RETURN_NULL_SCHEME ); 
    _getOneMoreThanMaxConnections( occi , maxLimit);
  }
  private static void _getOneMoreThanMaxConnections( 
    OracleConnectionCacheImpl occi , int maxLimit) throws SQLException
  {
    //Create an array of connections 1 more than max limit
    Connection[] connections = new Connection[ maxLimit + 1 ];
    for( int i=0; i < connections.length; i++ )
    { 
      System.out.print( "Getting connection no " + (i+1) + " ..." );
      connections[i] = occi.getConnection();
      if( connections[i] != null )
        System.out.println( " Successful." );
      else
        System.out.println( " Failed." );
    }
    // close all connections
    for( int i=0; i < connections.length; i++ )
    { 
      JDBCUtil.close( connections[i] );
    }
  }
}
