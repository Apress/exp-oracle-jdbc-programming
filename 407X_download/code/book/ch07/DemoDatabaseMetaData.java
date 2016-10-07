/* This program demonstrates querying information about database and JDBC driver using the DatabaseMetaData interface.
* COMPATIBLITY NOTE: runs successfully against 10.1.0.2.0 and 9.2.0.1.0.
*/
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Connection;
import book.util.JDBCUtil;
class DemoDatabaseMetaData
{
  public static void main(String args[]) throws Exception
  {
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("scott", "tiger", "ora10g");
      DatabaseMetaData dbMetaData = conn.getMetaData();
      System.out.println("Database Major version: " + dbMetaData.getDatabaseMajorVersion() );
      System.out.println("Database Minor version: " + dbMetaData.getDatabaseMinorVersion() );
      System.out.println("Default Transaction isolation: " + dbMetaData.getDefaultTransactionIsolation() );
      System.out.println("Driver major Version: " + dbMetaData.getDriverMajorVersion() );
      System.out.println("Driver minor Version: " + dbMetaData.getDriverMinorVersion() );
      System.out.println( "JDBC major version: " + dbMetaData.getJDBCMajorVersion() );
      System.out.println( "JDBC minor version: " + dbMetaData.getJDBCMinorVersion() );
      System.out.println( "Maximum char literal length: " + dbMetaData.getMaxCharLiteralLength() );
      System.out.println( "Maximum column name length: " + dbMetaData.getMaxColumnNameLength() );
      System.out.println( "Maximum columns in group by: " + dbMetaData.getMaxColumnsInGroupBy() );
      System.out.println( "Maximum columns in select: " + dbMetaData.getMaxColumnsInSelect() );
      System.out.println( "Maximum columns in table: " + dbMetaData.getMaxColumnsInTable() );
      System.out.println( "Maximum tables in select: " + dbMetaData.getMaxTablesInSelect() );
    }
    catch (SQLException e)
    {
      JDBCUtil.printException ( e );
    }
    finally
    {
      // release the JDBC resources in the finally clause.
      JDBCUtil.close( conn );
    }
  } // end of main()
} // end of program
