/* This program simply geenrates a divide by zero error and prints the stack trace.
*/
class DemoJavaStackTrace
{
  public static void main(String args[])
  {
    try
    {
      p1();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  } // end of main()
  static void p1()
  {
    System.out.println("in p1" );
    p2();
  }
  static void p2()
  {
    System.out.println("in p2" );
    p3();
  }
  static void p3()
  {
    System.out.println("in p3" );
    int x = 1/0; // will cause an exception
  }
} // end of program
