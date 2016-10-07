/** This program demonstrates how to pass a collection into a 
* PL/SQL procedure from JDBC.
* COMPATIBLITY NOTE:
*  runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.CallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.sql.ArrayDescriptor;
import oracle.sql.ARRAY;
import book.util.JDBCUtil;
import book.util.Util;
class DemoPassingCollectionToProcedure
{
  public static void main(String args[]) throws Exception
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      _doPassArrayToAProcedure( conn );
      conn.commit();
    }
    finally
    {
      // release JDBC resources
      JDBCUtil.close( conn );
    }
  }
  private static void _doPassArrayToAProcedure( Connection conn )
  throws SQLException
  {
    CallableStatement cstmt = null;
    try
    {
      // Step 1 - create the array descriptor - first check
      // if the connection object has it or not.
      // create one only if required.
      ArrayDescriptor arrayDescriptor = (ArrayDescriptor)
        ((OracleConnection) conn).getDescriptor( 
          "BENCHMARK.VARRAY_OF_VARCHARS" );
      if( arrayDescriptor == null )
      {
        System.out.println("creating array descriptor");
        arrayDescriptor = ArrayDescriptor.createDescriptor( 
            "BENCHMARK.VARRAY_OF_VARCHARS", conn );
      }
      // Step 2 - create the array contents and the ARRAY object
      String[] elements = new String[] { "elem 1", "elem 2" };
      ARRAY array = new ARRAY ( arrayDescriptor, conn, elements );
      // Step 3 - pass it to the callable statement
      String stmtString = 
        "begin demo_varray_pkg.demo_passing_varray_param(?); end;";
      cstmt = conn.prepareCall( stmtString );
      cstmt.setArray( 1, array );
      cstmt.execute();
    }
    finally
    {
      JDBCUtil.close( cstmt);
    }
  }
}
