/** This program demonstrates how to select a collection of objects into
* JDBC - and how by default they materialize in Java as 
* oracle.sql.STRUCT objects.
* COMPATIBLITY NOTE:
*  runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.Struct;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.ResultSet;
import oracle.sql.ARRAY;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import book.util.JDBCUtil;
import book.util.Util;

class DemoCollectionOfObjectTypes
{
  public static void main(String args[]) throws Exception
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      // Step 1 - prepare the statement
      String stmtString = "select emp_address_list from emp_table";
      pstmt = conn.prepareStatement( stmtString );
      // Step 2 - execute the statement and get the result set
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        // instead of the following you could also use:
        // ARRAY array = ((OracleResultSet ) rset).getARRAY(1);
        Array array = rset.getArray(1);
        _doUseGetArray( array );
        _doUseResultSet( array );
      }
    }
    finally
    {
      // release JDBC resources
      JDBCUtil.close( rset);
      JDBCUtil.close( pstmt);
      JDBCUtil.close( conn );
    }
  }
  private static void _doUseGetArray( Array array )
  throws SQLException
  {
    System.out.println("In _doUseGetArray");System.out.flush();
    Object[] arrayInJava = (Object[])array.getArray();
    for( int i=0; i < arrayInJava.length; i++ )
    {
      Struct empStruct = (Struct) (arrayInJava[i]);
      Object[] attributes = empStruct.getAttributes();
      for( int j=0; j < attributes.length; j++ )
      {
        System.out.println(attributes[j]);
      }
      System.out.println();
    }
    System.out.println("Exiting _doUseGetArray");System.out.flush();
  }
  private static void _doUseResultSet( Array array )
  throws SQLException
  {
    System.out.println("In _doUseResultSet");
    ResultSet rset = null;
    try
    {
      rset = array.getResultSet();
      while( rset.next() )
      {
        int index = rset.getInt( 1 );
        Struct empStruct = (Struct) rset.getObject( 2 );
        Object[] attributes = empStruct.getAttributes();
        for( int j=0; j < attributes.length; j++ )
        {
          System.out.println(attributes[j]);
        }
        System.out.println();
        
      }
    }
    finally
    {
      JDBCUtil.close( rset);
    }
    System.out.println("Exiting _doUseResultSet");
  }
}
