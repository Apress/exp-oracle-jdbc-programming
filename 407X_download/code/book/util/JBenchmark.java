/* When benchmarking Java programs, it is a good idea to let the 
 * Java virtual machine reach a steady state which takes a 
 * few minutes. One way to achieve this is to make sure that we 
 * run the program (or method) being benchmarked enough number 
 * of times for the entire benchmark to take around 5 minutes. 
 * For this, we first find out how many runs it takes for a method  
 * being benchmarked to consume 5 minutes. We then run the concerned 
 * method that many times and find out the average time per run by 
 * dividing total time with the number of runs. 
 * This program allows you to do this
 */
package book.util;
import java.sql.Connection;
public class JBenchmark
{
  // classes must override the method that they are
  // timing - by default these methods don't do anything.
  public void firstMethod( Connection conn, Object[] parameters ) throws Exception{ }
  public void secondMethod( Connection conn, Object[] parameters ) throws Exception { }
  public void thirdMethod( Connection conn, Object[] parameters ) throws Exception{ }
  public void fourthMethod( Connection conn, Object[] parameters ) throws Exception{ }
  public void firstMethod() throws Exception{ }
  public void secondMethod() throws Exception{ }
  public void thirdMethod() throws Exception{ }
  public void fourthMethod() throws Exception{ }
  public final void timeMethod( int methodNumber,
    Connection conn, Object[] parameters, String message ) throws Exception
  {
    System.out.println( message );
    // find out how many runs it takes to run for 5 minutes
    long startTime = System.currentTimeMillis();
    _runMethod( methodNumber, conn, parameters );
    long endTime = System.currentTimeMillis();
    long timeTaken = endTime-startTime;
    if( timeTaken == 0 )
    {
      timeTaken = 1; // to avoid divide by zero error in code below
    }
    long numOfRuns = (long)( (5*60*1000)/( timeTaken ) );
    if( numOfRuns == 0 )
    {
      System.out.println( "One run took more than 5 minutes." );
      numOfRuns = 1;
    }
    // average over the number of runs calculated above
    startTime = System.currentTimeMillis();
    for(int i=0; i < numOfRuns; i++ )
    {
      _runMethod( methodNumber, conn, parameters );
    }
    endTime = System.currentTimeMillis();
    long averageRunTime = (endTime-startTime)/numOfRuns;
    System.out.println( "\tOn an average it took " + averageRunTime + " ms (number of runs = " + numOfRuns + ".)");
  }

  private void _runMethod( int methodNumber, Connection conn, Object[] parameters ) throws Exception
  {
    if( conn != null )
    {
      if( methodNumber == FIRST_METHOD )
        firstMethod( conn, parameters );
      else if( methodNumber == SECOND_METHOD )
        secondMethod( conn, parameters );
      else if( methodNumber == THIRD_METHOD )
        thirdMethod( conn, parameters );
      else if( methodNumber == FOURTH_METHOD )
        fourthMethod( conn, parameters );
      else 
      {
        System.err.println( "Invalid method number: " + methodNumber );
        System.exit( 1 );
      }
    }
    else
    {
      if( methodNumber == FIRST_METHOD )
        firstMethod( );
      else if( methodNumber == SECOND_METHOD )
        secondMethod( );
      else if( methodNumber == THIRD_METHOD )
        thirdMethod( );
      else if( methodNumber == FOURTH_METHOD )
        fourthMethod( );
      else 
      {
        System.err.println( "Invalid method number: " + methodNumber );
        System.exit( 1 );
      }
    }
  }

  public static final int FIRST_METHOD = 1;
  public static final int SECOND_METHOD = 2;
  public static final int THIRD_METHOD = 3;
  public static final int FOURTH_METHOD = 4;
}
