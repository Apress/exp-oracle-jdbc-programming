/** This program demonstrates how to use java.sql.Struct class
* to retrieve objects.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0, and 9.2.0.1.0
*/
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Struct;
import java.sql.Types;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
class StructQueryExample
{
  public static void main(String args[]) throws SQLException
  {
    if( args.length != 1 )
    {
      System.err.println( "Usage: java <program_name> <database_name>");
      Runtime.getRuntime().exit(1);
    }
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0] );
      _demoQueryWithPreparedStmt( conn, 
        "select value(it) from item_table it" );
      _demoQueryWithPreparedStmt( conn, 
        "select m.manufactured_item from manufactured_item_table m" );
      _demoQueryWithCallableStmt( conn, 
        "begin item_pkg.get_items( ? ); end;" );
      /* gives error 
      _demoStructWithCallableStmtGivesError( conn, 
        "begin item_pkg.get_item( ? ); end;" );
       */
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  private static void _demoQueryWithPreparedStmt( Connection conn,
    String stmtStr ) throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      pstmt = conn.prepareStatement( stmtStr );
      rset = pstmt.executeQuery();
      // print the result 
      while (rset.next())
      {
        // get the "item" object and its attributes
        Struct itemStruct = (Struct) rset.getObject ( 1 );
        Object[] attributes = itemStruct.getAttributes();
        System.out.println ( "num of attributes: " + attributes.length );
        for(int i=0; i < attributes.length; i++ )
        {
          System.out.println ( "class of attribute " + i + " = " +
            (attributes[i]).getClass().getName() + 
            ", value = " + attributes[i] );
        }
      }
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( rset, pstmt);
    }
  }
  private static void _demoQueryWithCallableStmt( Connection conn,
    String stmtStr ) throws SQLException
  {
    CallableStatement cstmt = null;
    ResultSet rset = null;
    try
    {
      cstmt = conn.prepareCall( stmtStr );
      cstmt.registerOutParameter( 1, OracleTypes.CURSOR );
      cstmt.execute();
      rset = (ResultSet) cstmt.getObject(1);
      while( rset.next() )
      {
        Struct itemStruct = (Struct) rset.getObject ( 1 );
        Object[] attributes = itemStruct.getAttributes();
        System.out.println ( "num of attributes: " + attributes.length );
        for(int i=0; i < attributes.length; i++ )
        {
          System.out.println ( "class of attribute " + i + " = " +
            (attributes[i]).getClass().getName() + 
            ", value = " + attributes[i] );
        }
      }
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( rset);
      JDBCUtil.close( cstmt);
    }
  }
  private static void _demoStructWithCallableStmtGivesError( 
    Connection conn, String stmtStr ) throws SQLException
  {
    CallableStatement cstmt = null;
    try
    {
      cstmt = conn.prepareCall( stmtStr );
      cstmt.registerOutParameter( 1, OracleTypes.STRUCT );
      cstmt.execute();
        // get the "item" object and its attributes
        Struct itemStruct = (Struct) cstmt.getObject ( 1 );
        Object[] attributes = itemStruct.getAttributes();
        System.out.println ( "num of attributes: " + attributes.length );
        for(int i=0; i < attributes.length; i++ )
        {
          System.out.println ( "class of attribute " + i + " = " +
            (attributes[i]).getClass().getName() + 
            ", value = " + attributes[i] );
        }
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( cstmt);
    }
  }
}
