/* This program demonstrates how to query data from a table
* using PreparedStatement interface. It illustrates
* both binding a parameter by index and by name.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0.
*    against 9.2.0.1.0, you have to comment out the
*    code using binding by name feature to compile and
*    run this as bind by name is not supported in 9i.
*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import oracle.jdbc.OraclePreparedStatement;
import book.util.JDBCUtil;
import book.util.Util;
class DemoPreparedStatementQuery
{
  public static void main(String args[])
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("scott", "tiger", args[0]);
      _demoBindingByParameterIndex( conn );
      _demoBindingByParameterName( conn );
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
  /* demo parameter binding by index */
  private static void _demoBindingByParameterIndex( Connection conn ) throws SQLException
  {
    String stmtString = "select empno, ename, job from emp where job = ? and hiredate < ?";
    System.out.println( "\nCase 1: bind parameter by index");
    System.out.println( "Statement: " + stmtString );
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    final int JOB_COLUMN_INDEX = 1;
    final int HIREDATE_COLUMN_INDEX = 2;
    final int SELECT_CLAUSE_EMPNO_COLUMN_INDEX = 1;
    final int SELECT_CLAUSE_ENAME_COLUMN_INDEX = 2;
    final int SELECT_CLAUSE_JOB_COLUMN_INDEX = 3;
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      // bind the values
      pstmt.setString(JOB_COLUMN_INDEX, "CLERK" );
      pstmt.setDate(HIREDATE_COLUMN_INDEX, new java.sql.Date( new java.util.Date().getTime()));
      // execute the query
      rset = pstmt.executeQuery();
      // print the result 
      System.out.println( "printing query results ...\n");
      while (rset.next())
      {
        int empNo = rset.getInt ( SELECT_CLAUSE_EMPNO_COLUMN_INDEX );
        String empName = rset.getString ( SELECT_CLAUSE_ENAME_COLUMN_INDEX );
        String empJob = rset.getString ( SELECT_CLAUSE_JOB_COLUMN_INDEX );
        System.out.println( empNo + " " + empName + " " + empJob );
      }
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
    }
  }
  /* demo parameter binding by name - compiles only with 10g! */
  private static void _demoBindingByParameterName( Connection conn ) throws SQLException
  {
    String stmtString = "select empno, ename, job " +
      "from emp where job = :job and hiredate < :hiredate";
    System.out.println( "\nCase 2: bind parameter by name\n");
    System.out.println( "Statment: " + stmtString );
    OraclePreparedStatement opstmt = null;
    ResultSet rset = null;
    final int SELECT_CLAUSE_EMPNO_COLUMN_INDEX = 1;
    final int SELECT_CLAUSE_ENAME_COLUMN_INDEX = 2;
    final int SELECT_CLAUSE_JOB_COLUMN_INDEX = 3;
    try
    {
      opstmt = (OraclePreparedStatement) conn.prepareStatement( stmtString );
      // bind the values
      opstmt.setStringAtName("job", "CLERK" );
      opstmt.setDateAtName("hiredate", new java.sql.Date( new java.util.Date().getTime()));
      // execute the query
      rset = opstmt.executeQuery();
      // print the result 
      System.out.println( "printing query results ...\n");
      while (rset.next())
      {
        int empNo = rset.getInt ( SELECT_CLAUSE_EMPNO_COLUMN_INDEX );
        String empName = rset.getString ( SELECT_CLAUSE_ENAME_COLUMN_INDEX );
        String empJob = rset.getString ( SELECT_CLAUSE_JOB_COLUMN_INDEX );
        System.out.println( empNo + " " + empName + " " + empJob );
      }
    }
    finally
    {
      // release JDBC related resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( opstmt );
    }
  }
} // end of program
