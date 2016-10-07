/* This program demonstrates optimistic locking using checksum
* COMPATIBLITY NOTE: tested against 10.1.0.2.0.*/
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.CallableStatement;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
import book.util.InputUtil;
class DemoOptLockingUsingChecksum
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection( "scott", "tiger", "ora10g" );
      int empNo = 7654;
      long rowChecksum = _displayEmpDetails( conn, empNo );
      InputUtil.waitTillUserHitsEnter("Row has been selected but is not locked.");
      _updateEmpInfo( conn, empNo, 1450, "MARTIN", rowChecksum );
    }
    finally
    {
      JDBCUtil.close ( conn );
    }
  }// end of main
  private static long _displayEmpDetails( Connection conn, int empNo )
    throws SQLException
  {
    CallableStatement cstmt = null;
    long rowChecksum = 0;
    int salary = 0;
    String empName = null;
    try
    {
      cstmt = conn.prepareCall( "{call opt_lock_chksum_demo.get_emp_details(?, ?, ?, ?)}" );
      cstmt.setInt( 1, empNo  );
      cstmt.registerOutParameter( 2, OracleTypes.VARCHAR );
      cstmt.registerOutParameter( 3, OracleTypes.NUMBER );
      cstmt.registerOutParameter( 4, OracleTypes.NUMBER );
      cstmt.execute();
      empName = cstmt.getString( 2 );
      salary = cstmt.getInt( 3 );
      rowChecksum = cstmt.getLong( 4 );
      System.out.println( "empno: " + empNo + ", name: " + empName + ", salary: " + salary + ", checksum: " + rowChecksum );
    }
    finally
    {
      JDBCUtil.close( cstmt );
    }
    return rowChecksum;
  }
  private static void _updateEmpInfo( Connection conn, int empNo, int newSalary, String newEmpName, long rowChecksum )
    throws SQLException
  {
    CallableStatement cstmt = null;
    try
    {
      cstmt = conn.prepareCall( "{call opt_lock_chksum_demo.update_emp_info(?, ?, ?, ?, ?)}" );
      cstmt.setInt( 1, empNo  );
      cstmt.setInt( 2, newSalary  );
      cstmt.setString( 3, newEmpName  );
      cstmt.setLong( 4, rowChecksum  );
      cstmt.registerOutParameter( 5, OracleTypes.NUMBER );
      cstmt.execute();
      int numOfRowsUpdated = cstmt.getInt( 5 );
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
