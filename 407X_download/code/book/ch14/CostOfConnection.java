/** This program times the process of establishing and closing a connection.
* COMPATIBLITY NOTE: runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.Connection;
import oracle.jdbc.pool.OracleDataSource;
import book.util.JBenchmark;
class CostOfConnection extends JBenchmark
{
  private static int _numOfConnections = 1;
  private OracleDataSource _ods;
  public void firstMethod( ) throws Exception
  {
    Connection connections[] = new Connection[ _numOfConnections ];
    for( int i=0; i < _numOfConnections; i++ )
    {
      try
      {
        connections[i] = _ods.getConnection();
      }
      catch( Exception e )
      {
        System.err.println( "failed in connection number: " + i );
        throw e;
      }
    }
    for( int i=0; i < _numOfConnections; i++ )
    {
      if( connections[i] != null )
      {
        connections[i].close();
      }
    }
  }
  public static void main(String args[]) throws Exception
  {
    if( args.length == 1 ) 
    {
      _numOfConnections = Integer.parseInt( args[0] );
    }
    new CostOfConnection()._runBenchmark();
  }
  private void _runBenchmark() throws Exception
  {
    _ods = new OracleDataSource();
    _ods.setURL ( "jdbc:oracle:thin:@rmenon-lap:1521:ora10g" ); 
    _ods.setUser("scott");           // user name
    _ods.setPassword("tiger");       // password
    // time the process of establishing a connection - method 1
    String msg = "Establishing " + _numOfConnections + " connection(s) and closing them";
    timeMethod( JBenchmark.FIRST_METHOD, null, null, msg );
  }
}
