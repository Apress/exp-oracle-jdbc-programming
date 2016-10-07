/** This program demonstrates how, using custom classes, you can
*    1. query a ref object
*    2. dereference it to get its value
*    3. update its value and store it back in the database
* COMPATIBLITY NOTE:
*  runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Ref;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import book.util.JDBCUtil;
import book.util.Util;
import book.ch11.ref.jpub.Employee;
import book.ch11.ref.jpub.EmployeeRef;
class DemoCustomRefQueryAndUpdate
{
  public static void main(String args[]) throws SQLException
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      _doSelectRef( conn );
      _doUpdateRef( conn );
    }
    finally
    {
      // release JDBC resources in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  private static void _doSelectRef( Connection conn )
    throws SQLException
  {
    String stmtString = 
      "select e.emp_no, e.name, e.manager" +
      " from emp_table_with_ref e";
    PreparedStatement pstmt = null;
    OracleResultSet orset = null;
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      orset = (OracleResultSet) pstmt.executeQuery();
      while(orset.next())
      {
        int empNo = orset.getInt( 1 );
        String name = orset.getString( 2 );
        System.out.println("emp no : " + empNo );
        System.out.println("emp name : " + name );
        EmployeeRef managerRef = (EmployeeRef) 
          orset.getORAData(3, EmployeeRef.getORADataFactory());
        // retrieve the underlying object 
        if( managerRef != null )
        {
          Employee manager = managerRef.getValue();
          System.out.println("\t manager emp no" + manager.getEmpNo());
          System.out.println("\t manager emp name" + manager.getName());
          System.out.println("\t manager's manager ref " + 
            manager.getManager());
        }
      }
    }
    finally
    {
      JDBCUtil.close( orset);
      JDBCUtil.close( pstmt);
    }
  }
  private static void _doUpdateRef( Connection conn )
    throws SQLException
  {
    EmployeeRef newManagerRef = _getRefForUpdate( conn, 1 );
    _updateEmployeeRef( conn, 3, newManagerRef );
    conn.commit();
  }
  private static EmployeeRef _getRefForUpdate( Connection conn, int empNo )
    throws SQLException
  {
    String stmtString = 
      "select ref(e) " +
      " from emp_table_with_ref e " +
      " where e.emp_no = ? for update nowait";

    PreparedStatement pstmt = null;
    OracleResultSet orset = null;
             
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, empNo );
      orset = (OracleResultSet) pstmt.executeQuery();
      orset.next();
      EmployeeRef managerRef = (EmployeeRef) 
          orset.getORAData(1, EmployeeRef.getORADataFactory());

      return managerRef;
    }
    finally
    {
      JDBCUtil.close( orset);
      JDBCUtil.close( pstmt);
    }
  }
  private static void _updateEmployeeRef( Connection conn, int empNo, 
    EmployeeRef newManagerRef ) throws SQLException
  {
    String updateStmtString = 
      "update emp_table_with_ref e" +
      " set e.manager = ?" +
      " where e.emp_no = ?";
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( updateStmtString  );
      pstmt.setRef( 1, newManagerRef );
      pstmt.setInt( 2, empNo );
      pstmt.execute();
    }
    finally
    {
      JDBCUtil.close( pstmt);
    }
  }
} //end of program
