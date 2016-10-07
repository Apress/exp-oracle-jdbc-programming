/* This program demonstrates how using bind variables can prevent SQL injection attacks.
* COMPATIBLITY NOTE: runs successfully against 9.2.0.1.0 and 10.1.0.2.0.
*/
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import book.util.JDBCUtil;
class DemoSQLInjection
{
  public static void main(String args[])
  {
    _validateProgramInputs( args );
    String selectedOption = args[0];
    String username = args[1];
    String password = args[2];
    Connection conn = null;
    try
    {
      // get connection
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      if( NO_BIND.equals( selectedOption ) )
      {
        _authenticateWithoutUsingBindValues( conn, selectedOption, username, password );
      }
      else
      {
        _authenticateUsingBindValues( conn, selectedOption, username, password );
      }
    }
    catch (SQLException e)
    {
      // handle the exception properly - in this case, we just
      // print a message, and rollback
      JDBCUtil.printExceptionAndRollback( conn, e );
    }
    finally
    {
      // release JDBC resources in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  // authenticate without using bind values
  private static void _authenticateWithoutUsingBindValues( Connection conn,
    String selectedOption, String username, String password ) throws SQLException
  {
    Statement stmt = null;
    ResultSet rset = null;
    try
    {
      stmt = conn.createStatement();
      String verifyStmtString = "select count(*) from user_info " +
                                "where username = '" + username + "'" +
                                " and password = '" + password + "'";
      System.out.println("verify statement: " + verifyStmtString );
      rset = stmt.executeQuery( verifyStmtString );
      while( rset.next() )
      {
        int count = rset.getInt(1);
        if( count == 0 )
          System.out.println("Invalid username and password - access denied!");
        else 
          System.out.println("Congratulations! You have been " +
            "authenticated successfully!");
      }
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( stmt );
    }
  }
  // authenticate using bind values
  private static void _authenticateUsingBindValues( Connection conn,
    String selectedOption, String username, String password ) throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      String verifyStmtString = "select count(*) from user_info " +
                                "where username = ? "+
                                " and password = ?";
      System.out.println("verify statement: " + verifyStmtString );
      // prepare the statement
      pstmt = conn.prepareStatement( verifyStmtString );
      // bind the values
      pstmt.setString(1, username );
      pstmt.setString(2, password );
      // execute the statement
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        int count = rset.getInt(1);
        if( count == 0 )
          System.out.println("Invalid username and password - access denied!");
        else 
          System.out.println("Congratulations! You have been " +
          "authenticated successfully!");
      }
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }
  // check command line parameters.
  private static void _validateProgramInputs( String[] args )
  {
    if( args.length != 3 )
    {
      System.out.println(" Usage: java <program_name> <bind|nobind> <username> <password>");
      System.exit(1);
    }
    if( !( NO_BIND.equals( args[0] )  || BIND.equals( args[0] ) ) )
    {
      System.out.println(" Usage: java <program_name> <bind|nobind> <username> <password>");
      System.exit(1);
    }
  }
  private static final String NO_BIND= "nobind";
  private static final String BIND= "bind";
} // end of program
