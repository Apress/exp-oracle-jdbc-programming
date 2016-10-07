/* This program demonstrates proxy authentication
* COMPATIBLITY NOTE: tested against 10.1.0.2.0.
*/
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Properties;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import book.util.JDBCUtil;
class DemoProxyConnection
{
  public static void main(String args[]) throws Exception
  {
    if( args.length != 1 )
    {
      System.out.println( "Usage: java DemoProxyConnection <end_user>");
      System.exit( 1 );
    }
    String endUser = args[0];
    OracleOCIConnectionPool oocp = new OracleOCIConnectionPool();
    oocp.setURL ( "jdbc:oracle:oci:@" ); 
    oocp.setUser("midtier");           // user name
    oocp.setPassword("midtier");       // password
    Properties endUserProps = new Properties();
    endUserProps.setProperty( OracleOCIConnectionPool.PROXY_USER_NAME,
      endUser );
    //endUserProps.setProperty( OracleOCIConnectionPool.PROXY_PASSWORD,
    //  endUser );
    Connection conn = null;
    try
    {
      conn = oocp.getProxyConnection( 
        OracleOCIConnectionPool.PROXYTYPE_USER_NAME, endUserProps );
      System.out.println( "successfully connected...");
      _displayEnabledRoles( conn );
      _enableRole( conn, "all" );
      _displayEnabledRoles( conn );
      _enableRole( conn, "clerk_role" );
      _displayEnabledRoles( conn );
      _enableRole( conn, "manager_role" );
      _displayEnabledRoles( conn );
    }
    finally
    {
      JDBCUtil.close ( conn );
    }
  }// end of main
  // display enabled roles
  private static void _displayEnabledRoles( Connection conn ) 
  throws SQLException
  {
    System.out.println( "Displaying enabled roles...");
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      pstmt = conn.prepareStatement( "select role from session_roles");
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        System.out.println( "\t" + rset.getString( 1 ) );
      }
    }
    finally
    {
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }
  // set a given role or a set of comma separated roles.
  private static void _enableRole( Connection conn, String role )
  throws SQLException
  {
    System.out.println( "Enabling role(s) " + role );
    CallableStatement cstmt = null;
    try
    {
      cstmt = conn.prepareCall( 
        "{call dbms_session.set_role( ? ) }" );
      cstmt.setString( 1, role );
      cstmt.execute();
    }
    finally
    {
      JDBCUtil.close( cstmt );
    }
  }
}// end of program
