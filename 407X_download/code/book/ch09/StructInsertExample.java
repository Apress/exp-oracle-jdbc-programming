/*
* This program demonstrates how to use java.sql.Struct class
* to insert data into a table containing object columns.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0, and 9.2.0.1.0
*/
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Struct;
import java.sql.Types;
import java.math.BigDecimal;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import book.util.JDBCUtil;
class StructInsertExample
{
  public static void main(String args[]) throws SQLException
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      _demoInsertUsingPreparedStmt( conn );
      _demoInsertUsingCallableStmt( conn );
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  private static void _demoInsertUsingPreparedStmt( Connection conn )
    throws SQLException
  {
    PreparedStatement pstmt = null;
    try
    {
      StructDescriptor itemDescriptor = 
        StructDescriptor.createDescriptor( "BENCHMARK.ITEM", conn );
      Object[] itemAttributes = new Object[ itemDescriptor.getLength()];
      itemAttributes[0] = new BigDecimal(2);
      itemAttributes[1] = "item2";
      itemAttributes[2] = "item2 desc using prepared statement";
      // Next we create the Struct object
      Struct itemObject = new STRUCT ( itemDescriptor, conn,
        itemAttributes );
      pstmt = conn.prepareStatement( "insert into item_table values( ? )");
      pstmt.setObject(1, itemObject, Types.STRUCT );
      int numOfRowsInserted = pstmt.executeUpdate();
      System.out.println( "Inserted " + numOfRowsInserted + " rows");
      conn.commit();
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( pstmt);
    }
  }
  private static void _demoInsertUsingCallableStmt( Connection conn )
    throws SQLException
  {
    CallableStatement cstmt = null;
    try
    {
      StructDescriptor itemDescriptor = 
        StructDescriptor.createDescriptor( "BENCHMARK.ITEM", conn );
      Object[] itemAttributes = new Object[ itemDescriptor.getLength()];
      itemAttributes[0] = new BigDecimal(3);
      itemAttributes[1] = "item2";
      itemAttributes[2] = "item2 desc using callable stmt";
      // Next we create the Struct object
      Struct itemObject = new STRUCT ( itemDescriptor, conn,
        itemAttributes );
      cstmt = conn.prepareCall( "{call item_pkg.insert_item(?)}");
      cstmt.setObject(1, itemObject, Types.STRUCT );
      cstmt.execute();
      conn.commit();
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( cstmt);
    }
  }
}
