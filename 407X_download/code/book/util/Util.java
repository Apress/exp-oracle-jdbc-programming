package book.util;
/* Common utilities used in the book */

public class Util
{
  public static void main(String[] args)
  {  
    int length = Integer.parseInt( args[0] );
    System.out.println(leftPad( "hello", length, '*') );
  } 
  /**
    * this method simply checks that we pass the database
    * name when invoking an example program in this book.
    */

  public static void checkProgramUsage( String[] args )
  {
    if( args.length != 1 )
    {
      System.out.println(" Usage: java <program_name> <database_name>");
      System.exit(1);
    }
  }

  public static String rightPad( String string, int length )
  {
    return rightPad( string, length, ' ' );
  }

  public static String leftPad( String string, int length )
  {
    return leftPad( string, length, ' ' );
  }

  public static String rightPad( String string, int length, char ch )
  {
    if( string == null || string.length() >= length )
      return string;
    StringBuffer sb = new StringBuffer( string );
    for( int i=(string.length()-1); i < length-1; i++ )
      sb.append( ch );
    return sb.toString();
  }

  public static String leftPad( String string, int length, char ch )
  {
    if( string == null || string.length() >= length )
      return string;
    StringBuffer sb = new StringBuffer( length );
    for( int i=(string.length()-1); i < length-1; i++ )
      sb.append( ch );
    return sb.append( string ).toString();
  }
}
