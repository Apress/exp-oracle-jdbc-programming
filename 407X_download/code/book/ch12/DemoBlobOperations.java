/** This program demonstrates how to read from and write to a blob.
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
// importing standard JDBC classes under java.sql class hierarchy
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Blob;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import oracle.sql.BLOB;
import book.util.JDBCUtil;
import book.util.Util;
class DemoBlobOperations
{
  public static void main(String args[])
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      // following gets connection; sets auto commit to true
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      _readBlob( conn );
      _writeBlob( conn );
      _appendToBlob( conn );
      conn.commit();
    }
    catch (Exception e)
    {
      JDBCUtil.printExceptionAndRollback(conn, e );
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      JDBCUtil.close( conn );
    }
  }
 /* demos how to read from a blob in the database. */
  private static void _readBlob( Connection conn ) throws SQLException, IOException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    InputStream byteStream = null;
    try
    {
      String stmtString = "select blob_col from blob_table "+
        " where id = ?";
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, 1 );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        BLOB blob = (BLOB) rset.getBlob( 1 );
        byteStream = blob.getBinaryStream();
        byte [] byteArray= new byte [10];
        int numOfBytesRead = 0;
        int bytesRead = -1;
        while( (bytesRead = byteStream.read( byteArray ) ) != -1 )
        {
          System.out.print( new String(byteArray, 0, bytesRead));
          numOfBytesRead += bytesRead;
        }
        System.out.println("total bytes read: " + numOfBytesRead );
      }
    }
    finally
    {
      if( byteStream != null )
        byteStream.close();
      JDBCUtil.close( pstmt);
      JDBCUtil.close( rset);
    }
  }
 /* demos how to write to a blob in the database (overwriting from the beginning.) */
  private static void _writeBlob( Connection conn ) throws SQLException, IOException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    OutputStream out = null;
    try
    {
      String stmtString = 
        "select blob_col from blob_table " +
        " where id = ? for update";
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, 1 );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        BLOB blob = (BLOB)rset.getBlob( 1 );
        /* Following gives an "Unsupported feature"
           exception
          OutputStream ostream = blob.setBinaryStream(1L);
         */
        String newBlobData = new String(
          "data to overwrite existing data in the beginning");
        byte[] byteArray = newBlobData.getBytes();
        // Note that you get fetch out of sequence error, if you 
        // have autocommit set to true!
        out = blob.getBinaryOutputStream();
        // You can also use the following to get an ascii stream
        // OutStream writer= blob.getAsciiOutputStream();
        out.write( byteArray );
      }
    }
    finally
    {
      if( out != null )
        out.close();
      JDBCUtil.close( pstmt);
      JDBCUtil.close( rset);
    }
  }
 /* demos how to append to a blob in the database.  */
  private static void _appendToBlob( Connection conn )
  throws SQLException, IOException
  {
    CallableStatement cstmt = null;
    try
    {
      String stmtString = 
        "declare " +
        "  l_blob blob;" +
        "begin " +
        "  select blob_col into l_blob from blob_table " +
        "  where id = ? and rownum <= 1 for update;" +
        "  dbms_lob.writeappend( l_blob, ?, ? ); " +
        "end;";
      String newBlobData = new String("data to be appended");
      byte[] byteArray = newBlobData.getBytes();
      cstmt = conn.prepareCall( stmtString );
      cstmt.setInt( 1, 1 );
      cstmt.setInt( 2, byteArray.length );
      cstmt.setBytes( 3, byteArray );
      cstmt.execute();
    }
    finally
    {
      JDBCUtil.close( cstmt);
    }
  }
}
