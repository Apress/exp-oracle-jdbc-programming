package book.util;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class InputUtil
{
  public static void main(String[] args) 
    throws Exception
  {  
    String line = waitTillUserHitsEnter();
    System.out.println( line );
    line = waitTillUserHitsEnter();
    System.out.println( line );
  } 

  public static String waitTillUserHitsEnter( String message )
    throws IOException
  {
    System.out.println( message );
    return waitTillUserHitsEnter();
  }

  public static String waitTillUserHitsEnter()
    throws IOException
  {
    System.out.println("Press Enter to continue..." );
    String line = null;
    line = standardInput.readLine();

    return line;
  }
  static BufferedReader standardInput = new BufferedReader(
      new InputStreamReader( System.in ) );
}

