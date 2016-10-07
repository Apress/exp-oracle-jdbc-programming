/** This program simply prints out a ref cursor which points to a different query based on passed criteria
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.CallableStatement;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
class DemoRefCursor
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    CallableStatement cstmt = null;
    ResultSet rset = null;
    // first parameter: database name; second parameter: criterion; third parameter: bind value
    try
    {
      // get connection - auto commit is off
      conn = (Connection) JDBCUtil.  getConnection("scott", "tiger", args[0]);
      String stmtString = "{call demo_refcursor( ?, ?, ? ) }";
      cstmt = conn.prepareCall( stmtString );
      cstmt.setString( 1, args[1] ); // criterion
      cstmt.setString( 2, args[2] ); // bind value 
      cstmt.registerOutParameter( 3, OracleTypes.CURSOR ); // returned cursor
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( 3 );
      while( rset.next() )
      {
        System.out.println( rset.getInt( 1 ) + ", " + rset.getString( 2 ) + ", " + rset.getString( 3 ) );
      }
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
      JDBCUtil.close( conn );
    }
  }
}
