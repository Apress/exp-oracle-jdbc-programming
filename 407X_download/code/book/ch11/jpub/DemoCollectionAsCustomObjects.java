/** This program demonstrates how to select a collection of objects 
*   using custom collection classes. It also demonstrates how to modify 
*   an existing collection object.
* COMPATIBLITY NOTE:
*  runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.util.Map;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import oracle.jdbc.OracleResultSet;
import oracle.sql.ARRAY;
import book.util.JDBCUtil;
import book.util.Util;
import book.ch11.jpub.Address;
import book.ch11.jpub.AddressList;
class DemoCollectionAsCustomObjects
{
  public static void main(String args[]) throws Exception
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      _demoSelectAsARRAYAndCustomClassObject( conn );
      _demoSelectAsCustomCollectionClass( conn );
      _demoAddingMemberToCollection( conn );
      conn.commit();
    }
    finally
    {
      // release JDBC resources
      JDBCUtil.close( conn );
    }
  }
  /* The following method shows how to use the ARRAY
     class for the nested table itself but select the collection
     elements as a custom class. Thus in our example the
     collection element of type nested_table_of_addresses will be 
     materialized as an ARRAY class whereas each individual
     element is retrieved as a custom Address class.
  */
  private static void _demoSelectAsARRAYAndCustomClassObject( 
    Connection conn ) throws SQLException, ClassNotFoundException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      // Step 1 - add to the type map
      Map map = conn.getTypeMap();
      map.put( "BENCHMARK.ADDRESS", 
        Class.forName(Address.class.getName()) );
      // Step 2 - prepare the statement
      String stmtString = "select emp_address_list from emp_table";
      pstmt = conn.prepareStatement( stmtString );
      // Step 3 - execute the statement and get the result set
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        Array array =( ARRAY ) rset.getArray(1);
        Object[] arrayInJava = (Object[])array.getArray();
        for( int i=0; i < arrayInJava.length; i++ )
        {
          Address address = (Address) arrayInJava[i];
          System.out.println( address.getLine1() );
          System.out.println( address.getState() );
        }
      }
    }
    finally
    {
      JDBCUtil.close( rset);
      JDBCUtil.close( pstmt);
    }
  }
  /* The following method shows how to use the custom
     collection classes AddressList and Address to
     materialize the collection nested_table_of_addresses and its address
     elements respectively. 
  */
  private static void _demoSelectAsCustomCollectionClass( Connection conn )
    throws SQLException, ClassNotFoundException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      String stmtString = "select emp_address_list from emp_table";
      pstmt = conn.prepareStatement( stmtString );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        AddressList addressList = (AddressList)((oracle.jdbc.OracleResultSet) 
          rset).getORAData(1, AddressList.getORADataFactory());
        Address[] arrayInJava = addressList.getArray();
        for( int i=0; i < arrayInJava.length; i++ )
        {
          Address emp = arrayInJava[i];
          System.out.println( emp.getLine1() );
          System.out.println( emp.getState() );
        }
      }
    }
    finally
    {
      JDBCUtil.close( rset);
      JDBCUtil.close( pstmt);
    }
  }
  private static void _demoAddingMemberToCollection( Connection conn )
    throws SQLException, ClassNotFoundException
  {
    AddressList modifiedCollection = _addMemberToArray(conn);
    String stmtString = "update emp_table e set e.emp_address_list = ?";
    PreparedStatement pstmt = null;
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setObject( 1, modifiedCollection );
      pstmt.execute();
    }
    finally
    {
      JDBCUtil.close( pstmt);
    }
  }
  private static AddressList _addMemberToArray( Connection conn )
    throws SQLException, ClassNotFoundException
  {
    Address newAddress = new Address( "1177 Monica Lane", null,
      "Cryptic St", "Los Gatos", "CA", "94877");
    String stmtString = "select emp_address_list " +
                        " from emp_table where empno = ?";
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    AddressList addressList = null;
    try
    {
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, 1 );
      rset = pstmt.executeQuery();
      if( rset.next() ) // assume only one row is updated
      {
        addressList = (AddressList)((oracle.jdbc.OracleResultSet) 
          rset).getORAData(1, AddressList.getORADataFactory());
        Address[] arrayInJava = addressList.getArray();
        Address[] updatedEmpList = new Address[ arrayInJava.length + 1];
        System.arraycopy( arrayInJava, 0, updatedEmpList, 0,
          arrayInJava.length);
        updatedEmpList[ arrayInJava.length ] = newAddress;
        for( int i=0; i < updatedEmpList.length; i++ )
        {
          System.out.println(updatedEmpList[i].getLine1());
        }
        addressList.setArray( updatedEmpList );

      }
    }
    finally
    {
      JDBCUtil.close( rset);
      JDBCUtil.close( pstmt);
    }
    return addressList;
  }
} // end of program
