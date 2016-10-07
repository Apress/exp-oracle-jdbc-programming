/** This program compares read using Bfile and External tables.
* COMPATIBLITY NOTE:
*   runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.Connection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BFILE;
import book.util.JDBCUtil;
import book.util.JBenchmark;
class BenchmarkReadUsingBfileAndExternalTables extends JBenchmark
{
  public static void main(String args[]) throws Exception
  {
    if( args.length != 1 && args.length != 2 )
    {
      System.err.println(" Usage: java <program_name> <database_name> [prefetch_size]");
      Runtime.getRuntime().exit( 1 );
    }
    int prefetchSize = 20;
    if( args.length == 2 )
    {
      prefetchSize = Integer.parseInt( args[1] );
    }
    System.out.println( "Prefetch size for external table: " + prefetchSize );
    Connection conn = null;
    try
    {
      // get connection (auto commit is off )
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      _prepareBenchmarkStatements( conn );
      new BenchmarkReadUsingBfileAndExternalTables()._runBenchmark( conn, prefetchSize );
    }
    finally
    {
      // release resources associated with JDBC in the finally clause.
      _closeBenchmarkStatements( conn );
      JDBCUtil.close( conn );
    }
  }
  private void _runBenchmark( Connection conn, int prefetchSize ) throws Exception
  {
    Object[] params = new Object[ 1 ];
    params[0] = new Integer( prefetchSize );
    timeMethod( JBenchmark.FIRST_METHOD, conn, params, BFILE_DESC );
    timeMethod( JBenchmark.SECOND_METHOD, conn, params, EXTERNAL_TABLE_DESC );
  }
  /* reads an ascii file using bfile. */
  public void firstMethod( Connection conn, Object[] params ) throws Exception
  {
    OracleResultSet orset = null;
    BFILE bfile = null;
    BufferedReader reader = null;
    InputStream in = null;
    long numOfCharacersRead = 0;
    try
    {
      _bfilePstmt.setInt( 1, 1 );
      orset = (OracleResultSet) _bfilePstmt.executeQuery();
      String line = null;
      while( orset.next() )
      {
        bfile = orset.getBfile( 1 );
        bfile.openFile();
        in = bfile.getBinaryStream();
        reader = new BufferedReader(new InputStreamReader(in));
        numOfCharacersRead = 0;
        while (( line = reader.readLine() ) != null )
        {
          //System.out.println( line );
          numOfCharacersRead += line.length();
        }
      }
    }
    finally
    {
      if( in != null )
        in.close();
      if( bfile != null )
        bfile.closeFile();
      JDBCUtil.close( orset);
    }
    //System.out.println( "No of characters read: " + numOfCharacersRead );
  }
  /* reads from a text file using external tables.  */
  public void secondMethod( Connection conn, Object[] parameters ) throws Exception
  {
    ResultSet rset = null;
    long numOfCharacersRead = 0;
    int prefetchSize = ((Integer) parameters[0]).intValue();
    try
    {
      _externalTablePstmt.setFetchSize( prefetchSize );
      rset = _externalTablePstmt.executeQuery();
      numOfCharacersRead = 0;
      while( rset.next() )
      {
        String line1 = rset.getString(1); 
        numOfCharacersRead += line1.length();
      }
    }
    finally
    {
      JDBCUtil.close( rset);
    }
    //System.out.println( "No of characters read: " + numOfCharacersRead );
  }
  private static void _prepareBenchmarkStatements( Connection conn )
    throws SQLException 
  {
    String stmtString = "select data from et_table";
    _externalTablePstmt = conn.prepareStatement( stmtString );
    stmtString = "select bfile_col from bfile_table "+
        " where id = ?";
    _bfilePstmt = conn.prepareStatement( stmtString );
  }
  private static void _closeBenchmarkStatements( Connection conn )
    throws SQLException 
  {
    JDBCUtil.close( _bfilePstmt );
    JDBCUtil.close( _externalTablePstmt );
  }
  private static final String BFILE_DESC = "Using Bfile";
  private static final String EXTERNAL_TABLE_DESC = "Using external table";
  private static PreparedStatement _bfilePstmt;
  private static PreparedStatement _externalTablePstmt;
}
