/* * This program demonstrates how to read from and write to a clob.
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.util.Arrays;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Clob;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import oracle.sql.CLOB;
import book.util.JDBCUtil;
import book.util.Util;
class DemoClobOperations
{
  public static void main(String args[])
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      // following gets connection; sets auto commit to true
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      _readClob( conn );
      _readClobInChunks( conn );
      _writeClob( conn );
      _writeClobInChunks( conn );
      _appendToClob( conn );
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
/* demos how to read from a clob in the database. */
  private static void _readClob( Connection conn )
  throws SQLException, IOException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    BufferedReader reader = null;
    try
    {
      String stmtString = "select clob_col from clob_table "+
        " where id = ?";
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, 1 );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        Clob clob = rset.getClob( 1 );
        reader = new BufferedReader ( 
          new InputStreamReader ( clob.getAsciiStream() ) );
        int numOfCharactersRead = 0;
        String line = null;
        while( (line = reader.readLine()) != null )
        {
          //System.out.println( line );
          numOfCharactersRead += line.length();
        }
        System.out.println("num of characters read: " + 
          numOfCharactersRead );
      }
    }
    finally
    {
      if( reader != null )
        reader.close();
      JDBCUtil.close( pstmt);
      JDBCUtil.close( rset);
    }
  }
 /*
   demos how to write to a clob in the database.
 */
  private static void _writeClob( Connection conn ) throws SQLException, IOException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    Writer writer= null;
    //OutputStream writer = null;
    try
    {
      String stmtString = 
        "select clob_col from clob_table " +
        " where id = ? for update";
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, 2 );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        CLOB clob = (CLOB)rset.getClob( 1 );
        String newClobData = new String("NEW CLOB DATA");
       /* Currently all of the following give an unsupported 
          exception:
          OutputStreamWriter writer = new OutputStreamWriter
           ( clob.setAsciiStream(1) );
          BufferedWriter writer = new BufferedWriter(
            clob.setCharacterStream(1) );
          OutputStream outputStream = clob.setAsciiStream( 1L) ;
          Writer writer= clob.setCharacterStream(1L);
        */
        // Note that you get fetch out of sequence error, if you 
        // have autocommit set to true!
        writer= clob.getCharacterOutputStream();
        // You can also use the following to get an ascii stream
        // OutStream writer= clob.getAsciiOutputStream();
        writer.write( newClobData );
      }
    }
    finally
    {
      if( writer != null )
        writer.close();
      JDBCUtil.close( pstmt);
      JDBCUtil.close( rset);
    }
  }
 /* demos how to read a clob in the database piece meal. useful for large clobs.  */
  private static void _readClobInChunks( Connection conn )
  throws SQLException, IOException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    BufferedReader reader = null;
    try
    {
      String stmtString = 
        "select clob_col from clob_table " +
        " where id = ?";
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, 2 );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        System.out.println(": in _readClobInChunks");
        Clob clob = rset.getClob( 1 );
        int chunkSize = ((CLOB) clob).getChunkSize();
        System.out.println( "Chunk Size:" + chunkSize );
        int idealBufferSize = ((CLOB) clob).getBufferSize();
        System.out.println( "Ideal buffer Size:" + idealBufferSize );
        // We can use either the chunk size or the ideal
        // buffer size calculated by JDBC. Ideal buffer size
        // is a multiple of chunk size and is usually close
        // to 32KB. Important thing is to have a buffer size
        // as a multiple of chunk size for optimal performance. 
        // In this example, we use chunk size for the size of 
        // our buffer.
        char[] buffer = new char[ chunkSize ];
        reader = new BufferedReader
          ( new InputStreamReader( clob.getAsciiStream()));
        int length = -1;
        int numOfCharactersRead = 0;
        while (( length = reader.read( buffer, 0, chunkSize)) != -1 )
        {
          //System.out.println( buffer );
          numOfCharactersRead += length;
        }
        System.out.println("num of characters read: " + 
          numOfCharactersRead );
      }
    }
    finally
    {
      if( reader != null )
      {
        reader.close();
      }
      JDBCUtil.close( pstmt);
      JDBCUtil.close( rset);
    }
  }
 /* demos how to write to a clob in chunks to the database.
   Useful when clob sizes are relatively large.
 */
  private static void _writeClobInChunks( Connection conn )
  throws SQLException, IOException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    OutputStream out = null;
    try
    {
      String stmtString = 
        "select clob_col from clob_table " +
        " where id = ? for update";
      pstmt = conn.prepareStatement( stmtString );
      pstmt.setInt( 1, 3 );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        CLOB clob = (CLOB)rset.getClob( 1 );
        int chunkSize = clob.getChunkSize();
        byte[] buffer = new byte[ chunkSize];
        Arrays.fill( buffer, 0, chunkSize, (byte)'a' );
        out = clob.getAsciiOutputStream();
        for( int i=0; i < 10; i++ )
        {
          out.write( buffer, 0, buffer.length );
        }
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
 /* demos how to append to a clob in the database.  */
  private static void _appendToClob( Connection conn ) throws SQLException, IOException
  {
    CallableStatement cstmt = null;
    try
    {
      String stmtString = 
        "declare " +
        "  l_clob clob;" +
        "begin " +
        "  select clob_col into l_clob from clob_table " +
        "  where id = ? and rownum <= 1 for update;" +
        "  dbms_lob.writeappend( l_clob, ?, ? ); " +
        "end;";
      String newClobData = new String("data appended from JDBC");
      cstmt = conn.prepareCall( stmtString );
      cstmt.setInt( 1, 1 );
      cstmt.setInt( 2, newClobData.length() );
      cstmt.setString( 3, newClobData );
      cstmt.execute();
    }
    finally
    {
      JDBCUtil.close( cstmt);
    }
  }
}
