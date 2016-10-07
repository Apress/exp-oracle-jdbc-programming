/** This program demonstrates how to 
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
import java.sql.ResultSet;
import oracle.sql.STRUCT;
import book.util.JDBCUtil;
import book.util.Util;
class DemoQueryAndUpdateUsingWeaklyTypedRefs
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
      // release resources associated with JDBC
      // in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  private static Ref _getRefForUpdate( Connection conn, int empNo )
    throws SQLException
  {
    String stmtString = 
      "select ref(e) " +
      " from emp_table_with_ref e " +
      " where e.emp_no = ? for update nowait";
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, empNo );
      rset = pstmt.executeQuery();
      Ref managerRef = null;
      if(rset.next()) // only one row expected
      {
        managerRef = rset.getRef( 1 );
      }
      return managerRef;
    }
    finally
    {
      JDBCUtil.close( rset);
      JDBCUtil.close( pstmt);
    }
  }
  private static void _doSelectRef( Connection conn )
    throws SQLException
  {
    String stmtString = "select e.emp_no, e.name, e.manager" +
                        " from emp_table_with_ref e";
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      rset = pstmt.executeQuery();
      System.out.println("executed query");
      while(rset.next())
      {
        int empNo = rset.getInt( 1 );
        String name = rset.getString( 2 );
        System.out.println("emp no : " + empNo );
        System.out.println("emp name : " + name );
        Ref managerRef = rset.getRef( 3 );
        // retrieve the underlying object - since
        // there is no mapping, we will get it
        // as a STRUCT.
        if( managerRef != null )
        {
          System.out.println( "Reference SQL Type: " + 
            managerRef.getBaseTypeName() );
          // The getObject() of java.sql.Ref gives 
	  // an Unsupported feature exception when working with
	  // 9i and 10g r1. Hence we have to use the getValue() method in
	  // oracle.sql.REF.
          // Following gives an Unsupported feature in 9i and 10g
          //STRUCT manager = (STRUCT) ((oracle.sql.REF)managerRef).getObject();
          STRUCT manager = (STRUCT) ((oracle.sql.REF)managerRef).getValue();
          Object attributes[] = manager.getAttributes();
          System.out.println("no of manager attributes : " + 
            attributes.length );
          for(int i=0; i < attributes.length; i++ )
          {
            if( attributes[i] != null )
            {
              System.out.println("\tattribute # " + i + " class name " +
                attributes[i].getClass().getName() + " value " +
                attributes[i]);
            }
          }
        }
      }
    }
    finally
    {
      JDBCUtil.close( rset);
      JDBCUtil.close( pstmt);
    }
  }
  private static void _doUpdateRef( Connection conn )
    throws SQLException
  {
    Ref newManagerRef = _getRefForUpdate( conn, 1 );
    _updateEmployeeRecord( conn, 3, newManagerRef );
    conn.commit();
  }
  private static void _updateEmployeeRecord( Connection conn, int empNo, 
    Ref newManagerRef )
    throws SQLException
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
} // end of program
