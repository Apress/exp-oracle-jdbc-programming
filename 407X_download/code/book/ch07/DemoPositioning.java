/* This program demonstrates positioning in a scrollable result set.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0 and 9.2.0.1.0.
*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import book.util.JDBCUtil;
class DemoPositioning
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("scott", "tiger", "ora10g");
      _demoPositioning( conn );
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just 
      // print the stack trace.
      JDBCUtil.printException ( e );
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( conn );
    }
  } // end of main()
  private static void _demoPositioning( Connection conn )
  throws SQLException
  {
    ResultSet rset = null;
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( "select x from t1 order by x",
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      JDBCUtil.printRsetTypeAndConcurrencyType( pstmt );
      rset = (ResultSet) pstmt.executeQuery();
      JDBCUtil.printRsetTypeAndConcurrencyType( rset );
      rset.last(); // go to the last row
      System.out.println( "current position: " + rset.getRow() );
      rset.first(); // go to the first row
      System.out.println( "Is it the first row?: " + rset.isFirst() );
      rset.absolute( 4 ); // go to the row number 4
      System.out.println( "current position: " + rset.getRow() );
      rset.relative( +3 ); // go to the next 3 rows from current row
      System.out.println( "current position: " + rset.getRow() );
      rset.relative( -2 ); // go to the previous 2 rows from current row
      System.out.println( "current position: " + rset.getRow() );
      rset.beforeFirst( ); // go to the position before the first row
      rset.next(); // now go to first row
      System.out.println( "current position: " + rset.getRow() );
      rset.afterLast( ); // go to the position after the last row
      rset.previous(); // now go to last row
      System.out.println( "current position: " + rset.getRow() );
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  } 
} // end of program
