/** This program compares the following three approaches
* of retrieving array elements (after you have retrieved
* the ARRAY object from the database) with and without
* auto indexing and auto buffering on.
*    1. Using getArray()
*    2. Using getOracleArray()
*    3. Using getResultSet()
* COMPATIBLITY NOTE:
*  runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import oracle.sql.ARRAY;
import oracle.sql.Datum;
import book.util.JDBCUtil;
import book.util.JBenchmark;
class BenchmarkCollectionRetrievalMethods extends JBenchmark
{
  public static void main(String args[]) throws Exception
  {
    _checkProgramUsage( args );
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      ARRAY array = _fetchArray( conn );
      array.setAutoBuffering( autoBufferingFlag );
      array.setAutoIndexing( autoIndexingFlag );
      String optionsDesc = " AutoBuffering: " + autoBufferingFlag +
                           " AutoIndexing: " + autoIndexingFlag;
      int[] indexes = new int[ 10000 ];
      for( int i=0; i < 10000; i++ )
      {
        int randomNumber = (int)(Math.random()* 9999);
        indexes[i] = randomNumber;
      }
      new BenchmarkCollectionRetrievalMethods()._runBenchmark(
        conn, new Object[] { array, indexes }, optionsDesc );
    }
    finally
    {
      // release JDBC resources in the finally clause.
      JDBCUtil.close( conn );
    }
  }
  private static ARRAY _fetchArray( Connection conn )
  throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    ARRAY array = null;
    try
    {
      // Step 1 - prepare and execute the statement
      String stmtString = "select varray_column from number_varray_table" + 
                          " where rownum <= 1";
      pstmt = conn.prepareStatement( stmtString );
      rset = pstmt.executeQuery();
      if( rset.next() )
      {
        array = (ARRAY) rset.getArray(1);
      }
    }
    finally
    {
      JDBCUtil.close( rset);
      JDBCUtil.close( pstmt);
    }
    return array;
  }
  private void _runBenchmark( Connection conn, 
    Object[] parameters, String optionsDesc) 
    throws Exception
  {
    timeMethod( JBenchmark.FIRST_METHOD, conn, parameters, 
      GET_ARRAY_DESC + optionsDesc );
    timeMethod( JBenchmark.SECOND_METHOD, conn, parameters, 
      GET_ORACLE_ARRAY_DESC + optionsDesc );
    timeMethod( JBenchmark.THIRD_METHOD, conn, parameters, 
      GET_RESULT_SET_DESC + optionsDesc );
  }
  public void firstMethod( Connection conn, Object[] parameters ) 
    throws Exception
  { 
    ARRAY array = (ARRAY) parameters [0];
    int[] indexes = (int[]) parameters [1];
    Object[] arrayInJava = (Object[])array.getArray();
    Object arrayElement = null;
    int i=0;
    for( i=0; i < arrayInJava.length; i++ )
    {
      arrayElement = arrayInJava[ indexes[i] ];
    }
  }
  public void secondMethod( Connection conn, Object[] parameters ) 
    throws Exception
  {
    ARRAY array = (ARRAY) parameters [0];
    int[] indexes = (int[]) parameters [1];
    Datum[] arrayElements = (Datum[])array.getOracleArray();
    int i=0;
    Object arrayElement = null;
    for( i=0; i < arrayElements.length; i++ )
    {
      arrayElement = arrayElements[ indexes[i] ];
    }
  }
  public void thirdMethod( Connection conn, Object[] parameters ) 
    throws Exception
  {
    ARRAY array = (ARRAY) parameters [0];
    int numOfRecordsRetrieved = 0;
    ResultSet rset = null;
    try
    {
      rset = array.getResultSet();
      while( rset.next() )
      {
        Object arrayElement = rset.getObject(2);
        numOfRecordsRetrieved++;
      }
    }
    finally
    {
      JDBCUtil.close( rset);
    }
  }
  private static void _checkProgramUsage( String[] args )
  {
    if( args.length != 1 && args.length != 2 &&
        args.length != 3 )
    {
      System.out.println(
        "Usage: java <program_name> <database_name> [true|false][true|false]."
        + " The second parameter (optional) sets the autobuffering mode on or off"
        + " The third parameter (optional) sets the autoindexing mode on or off");
      System.exit(1);
    }
    if( args.length >= 2 )
    {
      autoBufferingFlag = Boolean.valueOf( args[1] ).booleanValue();
    }
    if( args.length == 3 )
    {
      autoIndexingFlag = Boolean.valueOf( args[2] ).booleanValue();
    }
    System.out.println( "auto buffering flag: " + autoBufferingFlag );
    System.out.println( "auto indexing flag: " + autoIndexingFlag );
  }
  private static boolean autoBufferingFlag = false;
  private static boolean autoIndexingFlag = false;
  private static final String GET_ARRAY_DESC = "getArray()";
  private static final String GET_ORACLE_ARRAY_DESC = "getOracleArray()";
  private static final String GET_RESULT_SET_DESC = "getResultSet()";
}
