/** This program demonstrates hard and soft parse
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
class DemoParse
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmt1 = null;
    ResultSet rset = null;
    ResultSet rset1 = null;
    // first parameter: database name
    try
    {
      // get connection - auto commit is off
      conn = (Connection) JDBCUtil.  getConnection("scott", "tiger", args[0]);
      JDBCUtil.startTrace( conn );
      String stmtString = "select /*+ prepareStatement() within loop */ empno, job from emp where ename = ?";
      for( int i=0; i < 5; i++ )
      {
        pstmt = conn.prepareStatement( stmtString );
        pstmt.setString( 1, "SCOTT" ); 
        rset = pstmt.executeQuery();
        while( rset.next() )
        {
        }
      }
      stmtString = "select /*+ prepareStatement() outside loop */ empno, job from emp where ename = ?";
      pstmt1 = conn.prepareStatement( stmtString );
      for( int i=0; i < 5; i++ )
      {
        pstmt1.setString( 1, "SCOTT" ); 
        rset1 = pstmt1.executeQuery();
        while( rset1.next() )
        {
        }
      }
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
      JDBCUtil.close( rset1 );
      JDBCUtil.close( pstmt1 );
      JDBCUtil.close( conn );
    }
  }
}
