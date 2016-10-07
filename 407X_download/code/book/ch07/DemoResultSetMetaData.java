/* This program demonstrates the use of ResultSetMetaData interface.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0 and 9.2.0.1.0.
*/
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Connection;
import book.util.JDBCUtil;
class DemoResultSetMetaData
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    try
    {
      if( args.length != 1 )
      {
        System.err.println( "Usage: java DemoResultSetMetaData <query>" );
        Runtime.getRuntime().exit( 1 );
      }
      conn = JDBCUtil.getConnection("scott", "tiger", "ora10g");
      pstmt = conn.prepareStatement( args[0] );
      rset = pstmt.executeQuery();
      printResults( rset );
    }
    catch (SQLException e)
    {
      JDBCUtil.printException ( e );
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( rset );
      JDBCUtil.close( pstmt );
      JDBCUtil.close( conn );
    }
  } // end of main()
  public static void printResults( ResultSet rset ) throws SQLException
  {
    while( rset.next() )
    {
      ResultSetMetaData rsetMetaData = rset.getMetaData();
      System.out.println( "----------------------------" );
      for( int i=0; i < rsetMetaData.getColumnCount(); i++ )
      {
        Object columnValue = rset.getObject( i + 1 );
        String className = rsetMetaData.getColumnClassName( i + 1 );
        if( "java.math.BigDecimal".equals( className ) )
        {
          BigDecimal bigDecimalValue = (BigDecimal) columnValue;
        }
        else if( "java.lang.String".equals( className ) )
        {
          String strValue = (String) columnValue;
        }
        else if( "java.sql.Timestamp".equals( className ) )
        {
          // Due to a bug, class for a date is printed as java.sql.Timestamp
          // instead of "java.sql.Date"
          Date dateValue = (Date) columnValue;
        }
        String columnName = rsetMetaData.getColumnName( i + 1 );
        StringBuffer columnInfo = new StringBuffer();
        columnInfo.append( columnName ).append( ": " ).append( columnValue );
        System.out.println( columnInfo.toString() );
      }
      System.out.println( "----------------------------" );
    }
  }
} // end of program
