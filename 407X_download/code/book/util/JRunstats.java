/* This program is a Java wrapper around the runstats utility written
 * by Tom Kyte and available at http://asktom.oracle.com/~tkyte/runstats.html.
 */
package book.util;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
public class JRunstats
{
  /* marks start of benchmark */
  public static void markStart( Connection connection )
    throws SQLException
  {
    _startTime = System.currentTimeMillis();
    _benchmarkStatementArray[BENCHMARK_START_INDEX].execute();
  }
  /* marks middle of benchmark */
  public static void markMiddle( Connection connection )
    throws SQLException
  {
    _middleTime = System.currentTimeMillis();
    _benchmarkStatementArray[BENCHMARK_MIDDLE_INDEX].execute();
  }
  /* marks end of benchmark - also takes a threshold tha controls
   * amount of data printed. It results in JRunstats printing only
   * latches and statistics whose absolute value is greater than this
   * threshold
   */
  public static void markEnd( Connection connection, int benchmarkDifferenceThreshold )
    throws SQLException
  {
    _markEnd( connection, benchmarkDifferenceThreshold );
  }
  /* marks end of benchmark */
  public static void markEnd( Connection connection )
    throws SQLException
  {
    _markEnd( connection, DEFAULT_BENCHMARK_DIFFERENCE_THRESHOLD  );
  }
  /* closes all benchmark related statements */
  public static void closeBenchmarkStatements ( 
    Connection connection )  throws SQLException
  {
    for( int i=0; i < _benchmarkStatementArray.length; i++)
    {
      _benchmarkStatementArray[i].close();
    }
  }
  /* prepares all benchmark related statements */
  public static void prepareBenchmarkStatements ( 
    Connection connection ) throws SQLException
  {
    _benchmarkStatementArray[BENCHMARK_START_INDEX]= 
      connection.prepareCall( BENCHMARK_START );

    _benchmarkStatementArray[BENCHMARK_MIDDLE_INDEX]= 
      connection.prepareCall( BENCHMARK_MIDDLE );

    _benchmarkStatementArray[BENCHMARK_STOP_INDEX]= 
      connection.prepareCall( BENCHMARK_STOP );
    _dbmsOutput = new DbmsOutput ( connection );
    _dbmsOutput.enable ( DBMS_OUTPUT_BUFFER_SIZE );
  }
  //////////////////////////// PRIVATE SECTION ///////////////
  /* prints benchmark results*/
  private static void _printBenchmarkResults() throws SQLException
  {
    System.out.println( "------- Benchmark Results --------" );
    System.out.println( "Results from RUNSTATS utility" );
    _dbmsOutput.show();
    _dbmsOutput.close();
    System.out.println( "" );
    System.out.println( "Runtime Execution Time Differences as seen by the client" );
    long run1 = _middleTime-_startTime;
    long run2 = _endTime-_middleTime;
    System.out.println( "Run1 ran in " + run1/10 + " hsecs");
    System.out.println( "Run2 ran in " + run2/10 + " hsecs");
    System.out.println( "Run1 ran in " + Math.round((run1*100.00)/(run2)) + "% of the time" );

  }
  /* marks end of benchmark - also takes a threshold that controls
   * amount of data printed. It results in JRunstats printing only
   * those differences in latches and statistics whose absolute 
   * value is greater than this threshold
   */
  private static void _markEnd( Connection connection, int benchmarkDifferenceThreshold )
    throws SQLException
  {
    
    _endTime = System.currentTimeMillis();
    _benchmarkStatementArray[BENCHMARK_STOP_INDEX].setInt(1, 
      benchmarkDifferenceThreshold);
    _benchmarkStatementArray[BENCHMARK_STOP_INDEX].execute();
    _printBenchmarkResults();
  }
  private static long _startTime;
  private static long _middleTime;
  private static long _endTime;
  private static String BENCHMARK_START = "begin runstats_pkg.rs_start; end;";
  private static String BENCHMARK_MIDDLE = "begin runstats_pkg.rs_middle; end;";
  private static String BENCHMARK_STOP = "begin runstats_pkg.rs_stop(?); end;";
  private static CallableStatement[] _benchmarkStatementArray = new CallableStatement[3]; 
  private static DbmsOutput _dbmsOutput;
  private static final int DBMS_OUTPUT_BUFFER_SIZE = 1000000;
  private static final int BENCHMARK_START_INDEX = 0;
  private static final int BENCHMARK_MIDDLE_INDEX = 1;
  private static final int BENCHMARK_STOP_INDEX = 2;
  private static final int DEFAULT_BENCHMARK_DIFFERENCE_THRESHOLD = 0;
}
