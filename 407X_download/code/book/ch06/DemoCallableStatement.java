/** This program demonstrates how to use CallableStatement.
* It demonstrates how to use
*    1. SQL 92 syntax for calling stored procedures.
*    2. Oracle syntax for calling stored procedures.
*    3. Using bind by parameter index, bind by parameter index
*       using parameter names and bind by parameter name.
* COMPATIBLITY NOTE:
*   runs successfully against 10.1.0.2.0.
*   Against 9.2.0.1.0, you have to comment out the
*   code using binding by name feature to compile and
*   run this as bind by name is not supported in 9i.
*/
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
import book.util.Util;
class DemoCallableStatement
{
  public static void main(String args[])
  {
    Util.checkProgramUsage( args );
    ResultSet rset = null;
    Connection conn = null;
    CallableStatement cstmt = null;
    try
    {
      // get connection - make sure you modify this call and
      // the JDBCUtil.getConnection() method to point to
      // your database, user and password.
      conn = JDBCUtil.getConnection("scott", "tiger", args[0]);
      ////////////        Example 1          ///////////////
      ////////////  SQL 92 Syntax, procedure ///////////////
      ////////////  parameter bound by index ///////////////
      _demoSql92SyntaxProcedureBindByIndex( conn );
      ////////////        Example 2          ///////////////
      ////////////  Oracle Syntax, function  ///////////////
      ////////////  parameter bound by index ///////////////
      _demoOracleSyntaxFunctionBindByIndex( conn );
      ////////////        Example 3          ///////////////
      ////////////  Oracle Syntax, procedure ///////////////
      ////////////  parameter bound by name  ///////////////
      _demoOracleSyntaxProcedureBindByName( conn );
      ////////////        Example 4          ///////////////
      ////////////  Oracle Syntax, procedure ///////////////
      ////////////  named parameter          ///////////////
      ////////////  procedure has parameter  ///////////////
      ////////////  with default value.      ///////////////
      _demoOracleSyntaxProcedureBindByNameWithDefault( conn );
      ////////////        Example 5          ///////////////
      ////////////  Oracle Syntax, procedure ///////////////
      ////////////  update example           ///////////////
      _demoOracleSyntaxProcedureBindByNameUpdate( conn );
    }
    catch (SQLException e)
    {
      // print stack trace.
      JDBCUtil.printException( e );
    }
    finally
    {
      // close the connection in finally clause
      JDBCUtil.close( conn );
    }
  }
  ////////////  PRIVATE SECTION ////////////////
  private static void _demoSql92SyntaxProcedureBindByIndex( Connection conn ) 
    throws SQLException
  {
    System.out.println( "Example 1, SQL 92 syntax, calling a procedure, binding by index" );
    int inputEmpNo = 7369;
    CallableStatement cstmt = null;
    ResultSet rset = null;
    try
    {
      // The procedure invoked below has a signature of:
      // procedure get_emp_details_proc( p_empno in number,
      //  p_emp_details_cursor out sys_refcursor )

      // formulate a callable statement string using sql 92
      // syntax
      String sql92Style = 
        "{ call callable_stmt_demo.get_emp_details_proc(?,?) }";
      // create the CallableStatement object
      cstmt = conn.prepareCall( sql92Style );
      // bind the input value
      cstmt.setInt(1, inputEmpNo );
      // register the output value
      cstmt.registerOutParameter( 2, OracleTypes.CURSOR );
      // execute the query
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( 2 );
      // print the result 
      while (rset.next())
      {
        int empNo = rset.getInt ( 1 );
        String empName = rset.getString ( 2 );
        String empJob = rset.getString ( 3 );
        System.out.println( empNo + " " + empName + " " + empJob );
      }
    }
    finally
    {
      // release JDBC resources in finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
    }
  }
  private static void _demoOracleSyntaxFunctionBindByIndex( Connection conn )
    throws SQLException
  {
    System.out.println( "\nExample 2, Oracle syntax,  calling a function, binding by index" );
    int inputEmpNo = 7369;
    ResultSet rset = null;
    CallableStatement cstmt = null;
    try
    {
      // The function invoked below has a signature of:
      // function get_emp_details_func( p_empno in number )
      //    return sys_refcursor

      // formulate a callable statement string using Oracle style
      // syntax
      String oracleStyle = 
        "begin ? := callable_stmt_demo.get_emp_details_func(?); end;"; 
      // create the CallableStatement object
      cstmt = conn.prepareCall( oracleStyle );
      // bind the input value
      cstmt.setInt(2, inputEmpNo );
      // register the output value
      cstmt.registerOutParameter( 1, OracleTypes.CURSOR );
      // execute the query
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( 1 );
      // print the result 
      while (rset.next())
      {
        int empNo = rset.getInt ( 1 );
        String empName = rset.getString ( 2 );
        String empJob = rset.getString ( 3 );
        System.out.println( empNo + " " + empName + " " + empJob );
      }
    }
    finally
    {
      // release JDBC resources in finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
    }
  }
  private static void _demoOracleSyntaxProcedureBindByName( Connection conn )
    throws SQLException
  {
    System.out.println( "\nExample 3, Oracle syntax, calling a procedure, bind by name" );
    int inputEmpNo = 7369;
    ResultSet rset = null;
    CallableStatement cstmt = null;
    try
    {
      // The procedure invoked below has a signature of:
      // procedure get_emp_details_proc( p_empno in number,
      //  p_emp_details_cursor out sys_refcursor )

      // formulate a callable statement string using Oracle style
      // syntax
      String oracleStyle = 
        "begin callable_stmt_demo.get_emp_details_proc(?, ?); end;"; 
      // create the CallableStatement object
      cstmt = conn.prepareCall( oracleStyle );
      // bind the input value by name
      cstmt.setInt("p_empno", inputEmpNo );
      // register the output value
      cstmt.registerOutParameter( "p_emp_details_cursor", OracleTypes.CURSOR );
      // execute the query
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( "p_emp_details_cursor" );
      // print the result 
      while (rset.next())
      {
        int empNo = rset.getInt ( 1 );
        String empName = rset.getString ( 2 );
        String empJob = rset.getString ( 3 );
        System.out.println( empNo + " " + empName + " " + empJob );
      }
    }
    finally
    {
      // release JDBC resources in finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
    }
  }
  private static void _demoOracleSyntaxProcedureBindByNameWithDefault( Connection conn )
    throws SQLException
  {
    System.out.println( "\nExample 4, Oracle syntax, calling a procedure, named parameter (with default value)" );
    int inputEmpNo = 7369;
    ResultSet rset = null;
    CallableStatement cstmt = null;
    try
    {
      // The procedure invoked below has a signature of:
      // procedure get_emps_with_high_sal( p_deptno in number,
      //  p_sal_limit in number default 2000 ,
      //  p_emp_details_cursor out sys_refcursor )

      // formulate a callable statement string using Oracle style
      // syntax
      String oracleStyle = 
        "begin callable_stmt_demo.get_emps_with_high_sal(?, ?); end;"; 
      // create the CallableStatement object
      cstmt = conn.prepareCall( oracleStyle );
      // bind the input value by name
      cstmt.setInt("p_deptno", 10 );
      // no need to pass the second parameter "p_sal_limit"
      // which gets a default value of 2000
      // register the output value
      cstmt.registerOutParameter( "p_emp_details_cursor", 
        OracleTypes.CURSOR );
      // execute the query
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject( "p_emp_details_cursor" );
      // print the result 
      while (rset.next())
      {
        int empNo = rset.getInt ( 1 );
        String empName = rset.getString ( 2 );
        String empJob = rset.getString ( 3 );
        int empSal = rset.getInt ( 4 );
        System.out.println( empNo + " " + empName + " " + empJob + " " +
          empSal );
      }
    }
    finally
    {
      // release JDBC resources in finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( cstmt );
    }
  }
  private static void _demoOracleSyntaxProcedureBindByNameUpdate( Connection conn )
    throws SQLException
  {
    System.out.println( "\nExample 5, Oracle syntax, calling a procedure, update example" );
    CallableStatement cstmt = null;
    try
    {
      // The procedure invoked below has a signature of:
      //   procedure give_raise( p_deptno in number )

      // formulate a callable statement string using Oracle style
      // syntax
      String oracleStyle = 
        "begin callable_stmt_demo.give_raise( ? ); end;"; 
      // create the CallableStatement object
      cstmt = conn.prepareCall( oracleStyle );
      // bind the input value by name
      cstmt.setInt("p_deptno", 10 );
      // execute 
      cstmt.execute();
      conn.commit();
    }
    catch (SQLException e)
    {
      // print a message and rollback.
      JDBCUtil.printExceptionAndRollback( conn, e );
    }
    finally
    {
      // release JDBC resources in finally clause.
      JDBCUtil.close( cstmt );
    }
  }
}
