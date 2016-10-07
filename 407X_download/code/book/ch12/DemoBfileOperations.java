/** This program demonstrates how to read from a Bfile.
* Note that BFILEs are read-only - you can not write to
* them.
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.Connection;
import java.io.InputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BFILE;
import book.util.JDBCUtil;
import book.util.Util;
class DemoBfileOperations
{
  public static void main(String args[]) throws Exception
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      // get connection (autocommit is set to false)
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      _readBfileAsBinaryData( conn );
      _readBfileAsAsciiData( conn );
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  /* demos how to read from a bfile from the database as a binary file. */
  private static void _readBfileAsBinaryData( Connection conn ) throws SQLException, IOException
  {
    PreparedStatement pstmt = null;
    OracleResultSet orset = null;
    InputStream in = null;
    BFILE bfile = null;
    try
    {
      String stmtString = "select bfile_col from bfile_table "+
        " where id = ?";
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, 2 );
      orset = (OracleResultSet) pstmt.executeQuery();
      while( orset.next() )
      {
        bfile = orset.getBfile( 1 );
        bfile.openFile();
        in = bfile.getBinaryStream();
        byte[] byteArray = new byte[100];
        int length = -1;
        int numOfBytesRead = 0;
        while (( length = in.read( byteArray)) != -1 )
        {
          //System.out.println( byteArray );
          numOfBytesRead += length;
        }
        System.out.println("binary file: num of bytes read: " + numOfBytesRead);
        System.out.println("");
      }
    }
    finally
    {
      if( in != null )
        in.close();
      if( bfile != null )
        bfile.closeFile();
      JDBCUtil.close( pstmt);
      JDBCUtil.close( orset);
    }
  }
  /* demos how to read from a bfile from the database as an ascii file. */
  private static void _readBfileAsAsciiData( Connection conn ) throws SQLException, IOException
  {
    PreparedStatement pstmt = null;
    OracleResultSet orset = null;
    BFILE bfile = null;
    InputStream in = null;
    try
    {
      String stmtString = "select bfile_col from bfile_table "+
        " where id = ?";
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, 1 );
      orset = (OracleResultSet) pstmt.executeQuery();
      byte[] buffer = new byte[30];
      int numOfCharacersRead = 0;
      int length = -1;
      while( orset.next() )
      {
        bfile = orset.getBfile( 1 );
        bfile.openFile();
        in = bfile.getBinaryStream();
        while (( length = in.read( buffer)) != -1 )
        {
          System.out.print( new String( buffer, 0, length ) );
          numOfCharacersRead += length;
        }
        System.out.println("\ntext file: num of chars read: " + numOfCharacersRead);
      }
    }
    finally
    {
      if( in != null )
        in.close();
      if( bfile != null )
        bfile.closeFile();
      JDBCUtil.close( pstmt);
      JDBCUtil.close( orset);
    }
  }
}
