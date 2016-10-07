/*
* This program demonstrates how to use java.sql.Struct class
* to update data in a table containing object columns.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0, and 9.2.0.1.0
*/
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Struct;
import java.sql.Types;
import java.math.BigDecimal;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import book.util.JDBCUtil;
class StructUpdateExample
{
  public static void main(String args[]) throws SQLException
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", "ora10g");
      Struct itemStruct = _getItem( conn, 2 );
      _demoUpdateUsingSTRUCT( conn, 2, itemStruct );
      _demoUpdateUsingRelationalSQL( conn, 3 );
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  private static void _demoUpdateUsingSTRUCT( Connection conn,
    int itemID, Struct itemStruct ) throws SQLException
  {
    PreparedStatement pstmt = null;
    STRUCT itemSTRUCT = (STRUCT) itemStruct;
    try
    {
      Object[] itemAttributes = itemStruct.getAttributes();
      itemAttributes[2] = "item2 desc updated using prepared statement";
      // Next we create the Struct object
      StructDescriptor itemDescriptor = itemSTRUCT.getDescriptor();
      Struct itemObject = new STRUCT ( itemDescriptor, conn,
        itemAttributes );
      pstmt = conn.prepareStatement( 
        "update item_table it set value(it) = ? " +
        " where it.id = ?" );
      pstmt.setObject(1, itemObject );
      pstmt.setInt(2, itemID );
      int numOfRowsUpdated = pstmt.executeUpdate();
      System.out.println( "Updated " + numOfRowsUpdated + " rows");
      conn.commit();
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( pstmt);
    }
  }
  private static Struct _getItem( Connection conn,
    int itemID ) throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      pstmt = conn.prepareStatement( 
        "select value(it) from item_table it" +
        " where it.id = ? for update NOWAIT" );
      pstmt.setInt( 1, itemID );
      rset = pstmt.executeQuery();
      // print the result 
      System.out.println( "printing query results ...\n");
      Struct itemStruct = null;
      if (rset.next())
      {
        // get the "item" object and its attributes
        itemStruct = (Struct) rset.getObject ( 1 );
      }
      return itemStruct;
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( rset, pstmt);
    }
  }
  private static void _demoUpdateUsingRelationalSQL( Connection conn,
    int itemID) throws SQLException
  {
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( 
        "update item_table it set it.description= ? " +
        " where it.id = ?" );
      pstmt.setString(1, "item desc updated using relational SQL" );
      pstmt.setInt(2, itemID );
      int numOfRowsUpdated = pstmt.executeUpdate();
      System.out.println( "Updated " + numOfRowsUpdated + " rows");
      conn.commit();
    }
    finally
    {
      // release resources in the finally clause.
      JDBCUtil.close( pstmt);
    }
  }
}
