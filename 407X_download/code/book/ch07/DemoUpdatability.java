/* This program demonstrates updatablilty of result set.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0, and 9.2.0.1.0 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
class DemoUpdatability
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("scott", "tiger", "ora10g");
      _demoUpdatability( conn );
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
  private static void _demoUpdatability( Connection conn )
  throws SQLException
  {
    System.out.println("Inside _demoUpdatability");
    ResultSet rset = null;
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( "select x from t1",
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      JDBCUtil.printRsetTypeAndConcurrencyType( pstmt );
      rset = (ResultSet) pstmt.executeQuery();
      JDBCUtil.printRsetTypeAndConcurrencyType( rset );
      // demo update row
      rset.absolute( 3 );
      rset.updateInt(1, 31 );
      rset.updateRow();
      // demo delete row
      rset.absolute( 4 );
      rset.deleteRow();
      // demo insert row
      rset.moveToInsertRow();
      rset.updateInt(1, 35 );
      rset.insertRow();
      System.out.println("\tMoving to row where I was before inserting" );
      rset.moveToCurrentRow();
      System.out.println("\tThe row where I was before inserting: " + rset.getRow() );
      conn.commit();
    }
    finally
    {
      //  release the JDBC resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }  
}// end of program
