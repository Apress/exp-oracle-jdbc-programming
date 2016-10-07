/* This program demonstrates optimistic locking by saving old column values.
* COMPATIBLITY NOTE: tested against 10.1.0.2.0.*/
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.CallableStatement;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
import book.util.InputUtil;
class DemoOptLockingBySavingOldValues 
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    Object[] empDetails = null;
    try
    {
      conn = JDBCUtil.getConnection( "scott", "tiger", "ora10g" );
      int empNo = 7654;
      empDetails = _displayEmpDetails( conn, empNo );
      InputUtil.waitTillUserHitsEnter("Row has been selected but is not locked.");
      String oldEmpName = (String) empDetails[0];
      int oldSalary = ((Integer) empDetails[1]).intValue();
      _updateEmpInfo( conn, empNo, oldEmpName, oldSalary, "MARTIN", 1450 );
    }
    finally
    {
      JDBCUtil.close ( conn );
    }
  }// end of main
  private static Object[] _displayEmpDetails( Connection conn, int empNo )
    throws SQLException
  {
    Object[] result = new Object[2];
    CallableStatement cstmt = null;
    int salary = 0;
    String empName = null;
    try
    {
      cstmt = conn.prepareCall( "{call opt_lock_save_old_val_demo.get_emp_details(?, ?, ?)}" );
      cstmt.setInt( 1, empNo  );
      cstmt.registerOutParameter( 2, OracleTypes.VARCHAR );
      cstmt.registerOutParameter( 3, OracleTypes.NUMBER );
      cstmt.execute();
      empName = cstmt.getString( 2 );
      salary = cstmt.getInt( 3 );
      System.out.println( "empno: " + empNo + ", name: " + empName + ", salary: " + salary );
      result[0] = empName;
      result[1] = new Integer( salary );
    }
    finally
    {
      JDBCUtil.close( cstmt );
    }
    return result;
  }
  private static void _updateEmpInfo( Connection conn, int empNo, String oldEmpName, int oldSalary, String newEmpName, int newSalary )
    throws SQLException
  {
    CallableStatement cstmt = null;
    try
    {
      cstmt = conn.prepareCall( "{call opt_lock_save_old_val_demo.update_emp_info(?, ?, ?, ?, ?, ?)}" );
      cstmt.setInt( 1, empNo  );
      cstmt.setString( 2, oldEmpName  );
      cstmt.setInt( 3, oldSalary  );
      cstmt.setString( 4, newEmpName  );
      cstmt.setInt( 5, newSalary  );
      cstmt.registerOutParameter( 6, OracleTypes.NUMBER );
      cstmt.execute();
      int numOfRowsUpdated = cstmt.getInt( 6 );
      if( numOfRowsUpdated <= 0 ) 
      {
        System.out.println( "Sorry. Someone else changed the data that you were trying to update. Please retry." );
      }
      else
      {
        System.out.println( "You have successfully updated the employee information." );
      }
    }
    finally
    {
      JDBCUtil.close( cstmt );
    }
  }
}// end of program
