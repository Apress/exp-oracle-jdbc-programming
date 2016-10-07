/* This program demonstrates how to use the utility program JBenchmark to
 * compare two methods.
 */
import book.util.JBenchmark;
public class DemoJBenchmark extends JBenchmark
{
  public static void main(String[] args) throws Exception
  {  
    new DemoJBenchmark()._runBenchmark();
  } 

  public void firstMethod() throws Exception
  {
    String x = "";
    for( int i=0; i < 1000; i++ )
      x  = x + Integer.toString(i);
  }
  
  public void secondMethod() throws Exception
  {
    StringBuffer x = new StringBuffer();
    for( int i=0; i < 1000; i++ )
      x.append( Integer.toString(i) );
    String y = x.toString();
  }
  
  private void _runBenchmark() throws Exception
  {
    timeMethod( JBenchmark.FIRST_METHOD, null, null, "Concatenating Using String");
    timeMethod( JBenchmark.SECOND_METHOD, null, null, "Concatenating Using StringBuffer");
  }
}
