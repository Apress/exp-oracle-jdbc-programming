/* This program demonstrates how to use the Statement interface
* to query and modify data and execute stored procedures. 
* Note that you should not use Statement class for executing
* SQL in your production code since it does not allow you to
* use bind variables. You should use either PreparedStatement
* or CallableStatement class.
*  COMPATIBLITY NOTE: runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import book.ch03.JDBCUtil;
public class DemoStatement
{
  public static void main(String args[])
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("scott", "tiger", args[0]);
      _demoQuery( conn );
      _demoInsert( conn );
      _demoExecute( conn, "select empno, ename from emp where job = 'CLERK'" );
      _demoExecute( conn, "insert into t1( x) values( 2 ) " );
      _demoInvokingSQLProcedure( conn );
      conn.commit();
    }
    catch (SQLException e)
    {
      // handle the exception - in this case, we 
      // rollback the transaction and 
      // print an error message and stack trace.
      JDBCUtil.printExceptionAndRollback ( conn, e );
    }
    finally
    {
      // release resources associated with JDBC
      // in the finally clause.
      JDBCUtil.close( conn );
    }
  } // end of main
  // execute query using Statement interface
  private static void _demoQuery( Connection conn ) throws SQLException
  {
    ResultSet rset = null;
    Statement stmt = null;
    try
    {
      stmt = conn.createStatement();
      // execute the query
      rset = stmt.executeQuery( 
        "select empno, ename, hiredate from emp where job = 'CLERK'" );
      // loop through result set and print
      while (rset.next())
      {
        int empNo = rset.getInt ( 1 );
        String empName = rset.getString ( 2 );
        Date hireDate = rset.getDate ( 3 );
        System.out.println( empNo + "," + empName + "," + hireDate );
      }
    }
    finally
    {
      JDBCUtil.close( rset );
      JDBCUtil.close( stmt );
    }
  }
  // demonstrate execute() method of Statement interface
  private static void _demoExecute( Connection conn, String sqlStmt ) throws SQLException
  {
    ResultSet rset = null;
    Statement stmt = null;
    try
    {
      stmt = conn.createStatement();
      // execute the query
      boolean isQuery = stmt.execute( sqlStmt );
      // if it is a query, get the result set and print the results
      if( isQuery )
      {
        rset = stmt.getResultSet();
        while (rset.next())
        {
          int empNo = rset.getInt ( "empno" );
          String empName = rset.getString ( "ename" );
          System.out.println( empNo + "," + empName );
        }
      }
      else
      {
        // we assume it is an insert, update, or delete statement
        int numOfRowsAffected = stmt.getUpdateCount();
        System.out.println( "Number of rows affected by execute() = " + 
          numOfRowsAffected );
      }
    }
    finally
    {
      JDBCUtil.close( rset );
      JDBCUtil.close( stmt );
    }
  }
  // demonstrate inserting record using Statement interface
  private static void _demoInsert( Connection conn ) throws SQLException
  {
    Statement stmt = null;
    try
    {
      stmt = conn.createStatement();
      // execute the insert
      int numOfRowsInserted = stmt.executeUpdate( "insert into t1( x) values( 1 ) " );
      System.out.println( "Number of rows inserted successfully = " + numOfRowsInserted );
    }
    finally
    {
      JDBCUtil.close( stmt );
    }
  }
  private static void _demoInvokingSQLProcedure( Connection conn ) throws SQLException
  {
    Statement stmt = null;
    try
    {
      stmt = conn.createStatement();
      // execute the sql procedure
      boolean ignore = stmt.execute( "begin p2( 3 ); end;" );
    }
    finally
    {
      JDBCUtil.close( stmt );
    }
  }
} // end of program
