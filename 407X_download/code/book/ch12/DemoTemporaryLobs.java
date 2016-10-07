/** This program demonstrates how to use temporary lobs.
* We work with temporary CLOBs though the same concepts
* apply to temporary BLOBs as well.
* COMPATIBLITY NOTE: In 9i You have to use the method 
* getAsciiOutputStream() in CLOB interface instead of the
* standard setAsciiStream() method as explained in the code:
*/
import java.util.Arrays;
import java.sql.SQLException;
import java.sql.Connection;
import java.io.OutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import oracle.sql.CLOB;
import book.util.JDBCUtil;
import book.util.Util;
class DemoTemporaryLobs
{
  public static void main(String args[]) throws Exception
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      // get connection (autocommit is set to false)
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      _insertClobUsingTemporaryClob( conn );
      conn.commit();
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      JDBCUtil.close( conn );
    }
  }
/* demos how to insert a clob using temporary clobs.  */
  private static void _insertClobUsingTemporaryClob( Connection conn ) throws SQLException, IOException
  {
    String insertStmt = "insert into clob_table(x, id, clob_col) " +
      " values( ?, ?, ? ) ";
    PreparedStatement pstmt = null;
    OutputStream out = null;
    CLOB tempClob = null;
    try
    {
      tempClob = CLOB.createTemporary( conn, true, CLOB.DURATION_SESSION );
      // opening the clob improves perforamnce when
      // you do multiple writes to the blob
      tempClob.open( CLOB.MODE_READWRITE);
      int chunkSize = tempClob.getChunkSize();
      System.out.println("chunk size for temporary lob: " 
        + chunkSize);
      byte[] buffer = new byte[ chunkSize ];
      Arrays.fill( buffer, 0, chunkSize, (byte)'b' );
      out = tempClob.setAsciiStream( 0L );
      // In 9i, you would have to use the following method instead of setAsciiStream()
      // out = tempClob.getAsciiOutputStream();
      for( int i=0; i < 10; i++ )
      {
        out.write( buffer, 0, chunkSize );
      }
      pstmt = conn.prepareStatement( insertStmt );
      pstmt.setString( 1, "Using temporary clob" );
      pstmt.setInt( 2, 4 );
      pstmt.setClob( 3, tempClob );
      pstmt.executeUpdate();
    }
    finally
    {
      try
      {
        if( out != null ) out.close();
        if( ( tempClob != null ) )
          CLOB.freeTemporary( tempClob );
      }
      catch (Exception e) { e.printStackTrace(); }
      JDBCUtil.close( pstmt);
    }
  }
}
