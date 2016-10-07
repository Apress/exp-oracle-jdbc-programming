import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import book.util.JDBCUtil;
class WorkerThread extends Thread
{
  WorkerThread( OracleOCIConnectionPool ociConnPool, int _threadNumber ) 
  throws Exception
  {
    super();
    this._ociConnPool = ociConnPool;
    this._threadNumber = _threadNumber;
  }
  public void run()
  {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try 
    {
      if( _threadNumber % 2 == 0 )
      {
        System.out.println( "connecting as scott" );
        conn = _ociConnPool.getConnection("scott", "tiger");
      }
      else
      {
        System.out.println( "connecting as benchmark" );
        conn = _ociConnPool.getConnection("benchmark", "benchmark");
      }
      //System.out.println( "current number of physical connections: " +
      //  _ociConnPool.getPoolSize());
      pstmt = conn.prepareStatement( "select owner from all_objects" );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        rset.getString(1);
      }
      //System.out.println( "b4 end - current number of physical connections: " + _ociConnPool.getPoolSize());
    }
    catch (Exception e )
    { 
      e.printStackTrace(); 
    }
    finally
    {
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
      JDBCUtil.close( conn );
    }
  } // end of run
  private OracleOCIConnectionPool _ociConnPool;
  private int _threadNumber = -1;
} // end of class


