/* This class is an interface to invoke the debug package from Java.
   This debug package can be downloaded from the code download area
   for this book at http://www.apress.com
 */
package book.util;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import oracle.jdbc.OracleTypes;
import book.util.JDBCUtil;
public class JDebug
{
 /* init()
  * invokes debug.init()
  *
  * @param connection Oracle connection
  * @param debugFlag - flag that shows if debugging is on or off. This
  *                    can be the one flag you put in your URL to turn 
  *                    debugging on or off. Note that this flag is not
  *                    passed on to the PL/SQL package "debug".
  * @param modules - PL/SQL modules for which debugging should be turned on.
  * @param directory - the directory object name which points to a 
  *                    a directory in which the debug file will be put.
  * @param debugFileName - the file name for logging debug messages.
  * @param dbUserName - the database user for which the debug is enabled
  * @param showDateFlag - flag indicating if date should be shown in 
  *                       output - has value 'YES'or 'NO'
  * @param dateFormat   - format of the date
  * @param nameLength   - length of the name of the PL/SQL procedure 
  * @param showSessionIDFlag - flag indicating if database session id 
  *                        should be shown in the output - has value 
  *                        'YES'or 'NO'
  * @throws SQLException
*
  */
  public static void init ( Connection connection, 
    String debugFlag, String modules, String directory, 
    String debugFileName, String dbUserName, 
    String showDateFlag, String dateFormat,
    int nameLength, String showSessionIDFlag )
    throws SQLException
  {
    CallableStatement cstmt = null;
    try
    {
      cstmt = connection.prepareCall ( 
        INIT_DEBUGGING_STMT );
      cstmt.setInt ( 1, Integer.parseInt ( debugFlag ) );
      cstmt.setString ( 2, modules );
      cstmt.setString ( 3, directory );
      cstmt.setString ( 4, debugFileName );
      cstmt.setString ( 5, dbUserName );
      cstmt.setString ( 6, showDateFlag );
      cstmt.setString ( 7, dateFormat );
      cstmt.setInt ( 8, nameLength );
      cstmt.setString ( 9, showSessionIDFlag );
      cstmt.executeUpdate();
    }
    finally
    {
      JDBCUtil.close ( cstmt );
    }
  }
 /* clear()
  * invokes debug.clear()
  * This method should be called to stop debug messages
  * from being generated or output to any destination.
  * procedure clear(
  *   p_user in varchar2 default user,
  *   p_dir  in varchar2 default null,
  *   p_file in varchar2 default null );
  * @param connection Oracle connection
  * @throws SQLException
  */
  public static void clear ( Connection connection, 
   String debugFlag, String dbUserName, 
    String directory, String debugFileName )
    throws SQLException
  {
    if( debugFlag == null ||  
        debugFlag.equals(DEBUG_OFF) ||
        !_isValidDebugOutputOption ( debugFlag ) )
      return ;
    CallableStatement cstmt = null;
    try
    {
      cstmt = connection.prepareCall ( CLEAR_DEBUGGING_STMT );
      cstmt.setString( 1, dbUserName );
      cstmt.setString( 2, directory );
      cstmt.setString( 3, debugFileName );
      cstmt.executeUpdate();
    }
    finally
    {
      JDBCUtil.close ( cstmt );
    }
  }
 /* getDebugMessageAndFlush()
  *
  * invokes debug.get_debug_message_flush()
  *
  * This method gets the debug messages emitted by the methods
  * in the plsql package debug (see the comments on top of this
  * file); concatenates each of them with an html break in between
  * and returns the resulting string  - this string can be
  * displayed in your jsp page for example. After
  * that it also deletes the messages (so the next call to this method 
  * would not see the previously outputted messages.) This is useful, 
  * if you follow the appraoach of calling this method at the end of a 
  * URL's event handler instead of Approach 2 which involves:
  *
  * a. deleting all messages in the beginning of the URL event handler
  *    by calling deleteDebugMessages.
  * b. calling getDebugMessageInfo() at the end of the event handler.
  *
  * @param connection Oracle connection
  * @param debugFlag - the method proceeds only if this flag
  *                    is set to a value that indicates that a
  *                    debug mode is on. To turn it on the
  *                    debugFlag value should not be DEBUG_OFF
  *                    For valid values of this parameter, 
  *                    see comments on method enable().
  *
  * @throws SQLException
  */

  public static String getDebugMessageAndFlush ( Connection connection, 
    String debugFlag )
    throws SQLException
  {
    if( debugFlag == null ||  
        debugFlag.equals(DEBUG_OFF) ||
        !_isValidDebugOutputOption ( debugFlag ) )
      return "";

    return _getDebugMessageAndFlush ( connection );
  }

 /*
  * This method prints the debug Message passed.
  * 
  * invokes debug.f()
  *
  * @param connection Oracle connection
  * @param debugFlag - the method proceeds only if this flag
  *                    is set to a value that indicates that a
  *                    debug mode is on. To turn it on the
  *                    debugFlag value should not be DEBUG_OFF
  *                    For valid values of this parameter, 
  *                    see comments on method enable().
  * @param message - the message may contain C style entries %s.
  *
  * @param messageArguments - a String array that contains 
  *                  the arguments substituting the c-style
  *                  entries %s, in message - in the correct
  *                  order.
  *
  * Example invocation:
  *   JDebug.printDebugMessage( connection, debugFlag,
  *     "A debug Message, value 1 %s, value 2 %s", 
  *        new String[]{stringValue, intValue+""} );
  *
  *  NOTE: Currently, an int value has to be passed as a string
  *        as shown in the above example.
  * @throws SQLException
  */

  public static void printDebugMessage ( Connection connection, 
    String debugFlag, String message, String[] messageArguments )
    throws SQLException
  {
    if(debugFlag == null ||  debugFlag.equals(DEBUG_OFF) )
      return ;
  
    int numOfArgs = 0;

    if( messageArguments != null )
    {
      numOfArgs = messageArguments.length;
      if( numOfArgs >= 10 )
        numOfArgs = 10; // only 10 args supported
    }

    CallableStatement cstmt = null;
    try
    {
      cstmt = connection.prepareCall ( 
       PRINT_DEBUG_MESSAGE_STMT  );
      cstmt.setString ( 1, MIDDLE_TIER_DEBUG_MESSAGE_PREFIX + message );
      for( int i=0; i < numOfArgs; i++ )
      {
        cstmt.setString ( i+2, messageArguments[i] );
      }
      for( int i=numOfArgs; i < 10 ; i++ )
      {
        cstmt.setString ( i+2, "" );
      }
      cstmt.executeUpdate();
    }
    finally
    {
      JDBCUtil.close ( cstmt );
    }
    return;

  }

 /* printDebugMessage()
  * 
  * invokes debug.f()
  *
  * This method prints the debug Message passed. This method is
  * useful for printing the middle tier messages along with the
  * the pl/sql messages thus completing the gap. Now you can see
  * messages from all pieces of code (middle tier and database)
  * that were used to generate a URL in sequence at the flip of
  * the flag in URL as described in the beginning of this class.
  * 
  * @param connection Oracle connection
  * @param debugFlag - the method proceeds only if this flag
  *                    is set to a value that indicates that a
  *                    debug mode is on. To turn it on the
  *                    debugFlag value should not be DEBUG_OFF
  *                    For valid values of this parameter, 
  *                    see comments on method enable().
  *
  * @param message - the message to be printed
  *
  * @throws SQLException
  */

  public static void printDebugMessage ( Connection connection, 
    String debugFlag, String message)
    throws SQLException
  {
    if( debugFlag == null ||  
        debugFlag.equals(DEBUG_OFF) ||
        !_isValidDebugOutputOption ( debugFlag ) )
      return ;

    printDebugMessage ( connection, debugFlag, message, 
      (String[]) null );
  }

  ////////////////  PUBLIC CONSTANTS //////////////////////////
  /* the debug flag in the URL should be set to the following value
   * to turn the debug OFF
   * from the URL as a string - wanted to avoid converting to int.
   */
  public static final String DEBUG_OFF = "0";
  /*
   * enable debugging so that you see the output only in the URL
   * displayed - don't do any trace file generation in the trace
   * file directory.
   */
  public static final String  LOG_FOR_MIDTIER_ONLY = "1";
  /*
   * enable debugging so that you see the output only in the trace 
   *  file directory - don't display the output in the URL.
   */
  public static final String  LOG_IN_TRACE_FILES_ONLY = "2";
  /*
   * enable debugging so that you see the output the trace 
   * file as well as on the URL.
   */
  public static final String LOG_FOR_TRACE_FILES_AND_MIDTIER = "3";
  /*
   * constant that indicates that debugging is on for all modules.
   */
  public static final String ALL_MODULES = "ALL";
  /*
   * default directory
   */
  public static final String DEFAULT_DEBUG_DIRECTORY = "TEMP";
  /*
   * default directory
   */
  public static final String DEFAULT_DEBUG_FILE_NAME = "debug.txt";
  /*
   * YES
   */
  public static final String YES = "YES";
  /*
   * NO
   */
  public static final String NO = "NO";
  /*
   * default date format
   */
  public static final String DEFAULT_DATE_FORMAT = "MMDDYYYY HH24MISS";
  /*
   * default name length
   */
  public static final int DEFAULT_NAME_LENGTH = 30;
  ////////////  PRIVATE SECTION //////////////////////////
 /* _getDebugMessageAndFlush()
  *
  * This method gets the debug messages emitted by the methods
  * in the plsql package debug (see the comments on top of this
  * file); concatenates each of them with an html break in between
  * and returns the resulting string  - this string can be
  * displayed in uix, for example, using the tag <rawText>.
  *
  * Depending on the passed procedure, it retains the debug messages
  * or flushes them. 
  * 
  * @param connection Oracle connection
  *
  * @param procedureName - the procedure that gets invoked. 
  *                
  * @throws SQLException
  */

  private static String _getDebugMessageAndFlush ( Connection connection )
    throws SQLException
  {
    StringBuffer result = new StringBuffer ("");
    CallableStatement cstmt = null;
    ResultSet res = null;

    try
    {
      cstmt = connection.prepareCall ( 
        GET_DEBUG_MESSAGE_AND_FLUSH_STMT );
      cstmt.registerOutParameter( 1, OracleTypes.CURSOR);
      cstmt.execute();
      res = (ResultSet) cstmt.getObject( 1 );
      while ( res != null && res.next () )
      {
        result.append ( res.getString (1) );
        result.append ( HTML_BREAK_TAG );
      }
    }
    finally
    {
      JDBCUtil.close ( res );
      JDBCUtil.close ( cstmt );
    }
   
    return result.toString();
  }

 /* _isValidDebugOutputOption()
  *
  * This method checks if the debug option passed in is valid or not.
  *
  * @param debugFlag - This is the value of debug option to be validated
  *                    For valid values of this parameter, see comments on 
  *                    method enable().
  */

  private static boolean _isValidDebugOutputOption ( String debugFlag )
  {
    return debugFlag != null && 
      (
        debugFlag.equals( LOG_FOR_MIDTIER_ONLY ) ||
        debugFlag.equals( LOG_IN_TRACE_FILES_ONLY ) ||
        debugFlag.equals( LOG_FOR_TRACE_FILES_AND_MIDTIER ) ||
        debugFlag.equals( DEBUG_OFF ) 
      );
  }

  /* 
   * a break tag in html. We use this to separate out different
   * debug statement lines when displaying in the URL.
   */

  private static final String HTML_BREAK_TAG = "<br>";

  /* 
   * gets the debug information emitted by procedures in debug
   * package (debug.f and debug.fa.) DELETES them after retrieving 
   * them.
   */

  private static final String GET_DEBUG_MESSAGE_AND_FLUSH_STMT = 
    "begin ? := debug.get_debug_message_flush; end;";

  /* 
   * enable debugging
   * this statement enables the debugging output processing. 
   */

  private static final String INIT_DEBUGGING_STMT = 
    "begin debug.init ( ?, ?, ?, ?, ?, ?, ?, ?, ? ); end;";

  /* 
   * clear debugging
   * this statement disables the debugging output processing. 
   */

  private static final String CLEAR_DEBUGGING_STMT = 
    "begin debug.clear( ?, ?, ? ); end;";

  /* 
   * print debug message
   * this statement just invokes the debug.f with the passed
   * message from the OMS code. This is useful if you want to
   * get OMS layer messages also in the UI screen, for example.
   * Please note that you should invoke this procedure only in
   * debug mode otherwise the db round trips can cause performance
   * problems. This version can take max 10 arguments. It
   * supports simple c style messages.
   */

  private static final String PRINT_DEBUG_MESSAGE_STMT =
    "begin debug.f( ?, ? ,?, ? ,?, ? ,?, ? ,?, ?, ? ); end;";

  /* 
   * UI message prefix
   * Prefix to distinguish between messages that come from pl/sql
   * from messages that came from the middle tier. The messages
   * in the URL that have the prefix below should be coming from 
   * the middle tier.
   */

  private static final String MIDDLE_TIER_DEBUG_MESSAGE_PREFIX =
    "MIDDLE TIER: ";

  private static final String MESSAGE_PARAM_ARRAY_NAME = "DEBUG.ARGV";
}
