/** This program compares the effect of using ARRAY methods
* specific to numeric collections. We compare the following
* for a numeric collection.
*    1. Using getArray()
*    2. Using getOracleArray()
*    3. Using getResultSet()
*    3. Using getIntArray()
* COMPATIBLITY NOTE:
*  runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import oracle.sql.ArrayDescriptor;
import oracle.sql.ARRAY;
import oracle.sql.Datum;
import book.util.JDBCUtil;
import book.util.JBenchmark;
import book.util.Util;
class BenchmarkNumericExtension extends JBenchmark
{
  public static void main(String args[]) throws Exception
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      ARRAY array = _fetchArray( conn );
      new BenchmarkNumericExtension()._runBenchmark(
        conn, new Object[] { array } );
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
      String stmtString = "select nt_col from number_table_nt" + 
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
  private void _runBenchmark( Connection conn, Object[] parameters ) 
    throws Exception
  {
    timeMethod( JBenchmark.FIRST_METHOD, conn, parameters, 
      GET_ARRAY_DESC );
    timeMethod( JBenchmark.SECOND_METHOD, conn, parameters, 
      GET_ORACLE_ARRAY_DESC );
    timeMethod( JBenchmark.THIRD_METHOD, conn, parameters, 
      GET_RESULT_SET_DESC );
    timeMethod( JBenchmark.FOURTH_METHOD, conn, parameters, 
      USE_NUMERIC_EXTENSION_DESC );
  }
  public void firstMethod( Connection conn, Object[] parameters ) 
    throws Exception
  {
    if( !firstMethodFlag )
    {
      System.out.println("Inside getArray method" );
      firstMethodFlag = true;
    }
    ARRAY array = (ARRAY) parameters [0];
    int numOfRecordsRetrieved = 0;
    Object[] arrayInJava = (Object[])array.getArray();
    for( int i=0; i < arrayInJava.length; i++ )
    {
      Object arrayElement = arrayInJava[ i ];
      numOfRecordsRetrieved++;
    }
  }
  public void secondMethod( Connection conn, Object[] parameters ) 
  throws Exception
  {
    if( !secondMethodFlag )
    {
      System.out.println("Inside getOracleArray method" );
      secondMethodFlag = true;
    }
    ARRAY array = (ARRAY) parameters [0];
    int numOfRecordsRetrieved = 0;
    Datum[] arrayElements = (Datum[])array.getOracleArray();
    for( int i=0; i < arrayElements.length; i++ )
    {
      Object arrayElement = arrayElements[ i ];
      numOfRecordsRetrieved++;
    }
  }
  public void thirdMethod( Connection conn, Object[] parameters ) 
    throws Exception
  {
    if( !thirdMethodFlag )
    {
      System.out.println("Inside getResultSet method" );
      thirdMethodFlag = true;
    }
    ARRAY array = (ARRAY) parameters [0];
    int numOfRecordsRetrieved = 0;
    ResultSet rset = null;
    try
    {
      rset = array.getResultSet();
      while( rset.next() )
      {
        Object object = rset.getObject(1);
        numOfRecordsRetrieved++;
      }
    }
    finally
    {
      JDBCUtil.close( rset);
    }
  }
  public void fourthMethod( Connection conn, Object[] parameters ) 
    throws Exception
  {
    if( !fourthMethodFlag )
    {
      System.out.println("Inside numeric extension method" );
      fourthMethodFlag = true;
    }
    ARRAY array = (ARRAY) parameters [0];
    int numOfRecordsRetrieved = 0;
    int[] arrayElements = (int[])array.getIntArray();
    for( int i=0; i < arrayElements.length; i++ )
    {
      int j = arrayElements[ i ];
      numOfRecordsRetrieved++;
    }
  }
  private static final String GET_ARRAY_DESC = "getArray()";
  private static final String GET_ORACLE_ARRAY_DESC = "getOracleArray()";
  private static final String GET_RESULT_SET_DESC = "getResultSet()";
  private static final String USE_NUMERIC_EXTENSION_DESC = "Numeric Extensions";

  private static boolean firstMethodFlag;
  private static boolean secondMethodFlag;
  private static boolean thirdMethodFlag;
  private static boolean fourthMethodFlag;
}
