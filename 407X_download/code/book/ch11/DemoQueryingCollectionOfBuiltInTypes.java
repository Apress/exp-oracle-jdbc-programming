/** This program demonstrates how to select a collection of built-in type 
* into JDBC (we use varray of varchar2 and varray of number
* to demonstrate the concepts).
* COMPATIBLITY NOTE:
*  runs successfully against 9.2.0.1.0 and 10.1.0.2.0
*/
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.ResultSet;
import oracle.sql.ArrayDescriptor;
import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.NUMBER;
import oracle.sql.CHAR;
import book.util.JDBCUtil;
import book.util.Util;
class DemoQueryingCollectionOfBuiltInTypes
{
  public static void main(String args[]) throws Exception
  {
    Util.checkProgramUsage( args );
    Connection conn = null;
    try
    {
      conn = JDBCUtil.getConnection("benchmark", "benchmark", args[0]);
      ARRAY varcharArray = _doSelectVarchar2Array( conn );
      ARRAY numberArray = _doSelectNumberArray(conn );
      System.out.println( "varchar array: " + varcharArray);
      System.out.println( "number array: " + numberArray);
      _printArrayInfo( varcharArray );
      _printArrayInfo( numberArray );
    }
    finally
    {
      // release JDBC resources
      JDBCUtil.close( conn );
    }
  }
  private static ARRAY _doSelectVarchar2Array( Connection conn )
  throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    ARRAY array = null;
    try
    {
      // Step 1 - prepare the statement
      String stmtString = "select varray_column from varchar_varray_table";
      pstmt = conn.prepareStatement( stmtString );
      // Step 2 - execute the statement and get the result set
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        // instead of the following you could also use:
        // ARRAY array = ((OracleResultSet ) rset).getARRAY(1);
        array =( ARRAY ) rset.getArray(1);
        _doUseGetArray( array );
        _doUseResultSet( array );
        _doUseGetOracleArray( array );
      }
    }
    finally
    {
      JDBCUtil.close( rset);
      JDBCUtil.close( pstmt);
    }
    return array;
  }
  private static void _doUseGetArray( ARRAY array )
  throws SQLException
  {
    System.out.println("In _doUseGetArray");
    // Since varchar2 maps by default to String,
    // we can typecast the results to a String array.
    String[] arrayInJava = (String[])array.getArray();
    for( int i=0; i < arrayInJava.length; i++ )
    {
      
      System.out.println(arrayInJava[i]);
    }
    System.out.println("Exiting _doUseGetArray");
  }

  private static void _doUseGetOracleArray( ARRAY array )
  throws SQLException
  {
    System.out.println("In _doUseGetOracleArray");
    Datum[] arrayElements = (Datum[])array.getOracleArray();
    for( int i=0; i < arrayElements.length; i++ )
    {
      System.out.println((CHAR)arrayElements[i]);
    }
    System.out.println("Exiting _doUseGetOracleArray");
  }

  private static void _doUseResultSet( ARRAY array )
  throws SQLException
  {
    System.out.println("In _doUseResultSet");
    ResultSet rset = null;
    try
    {
      rset = array.getResultSet();
      while( rset.next() )
      {
        int index = rset.getInt( 1 );
        String stringValue = rset.getString( 2 );
        System.out.println("element number " + index + " = " + stringValue);
      }
    }
    finally
    {
      JDBCUtil.close( rset);
    }
    System.out.println("Exiting _doUseResultSet");
  }
/*
   demos how to select a varray of numbers from a table.
   demos the special case of numbers for which we have
   Oracle extension methods in the ARRAY class. 
 */
  private static ARRAY _doSelectNumberArray( Connection conn )
  throws SQLException
  {
    PreparedStatement pstmt = null;
    ResultSet rset = null;
    ARRAY array = null;
    try
    {
      // Step 1 - prepare and execute the statement
      String stmtString = "select varray_column from number_varray_table";
      pstmt = conn.prepareStatement( stmtString );
      rset = pstmt.executeQuery();
      while( rset.next() )
      {
        // Step 2 - In the loop get the ARRAY object
        // instead of the following you could also use:
        // ARRAY array = ((OracleResultSet ) rset).getARRAY(1);
        array = (ARRAY) rset.getArray(1);
        // Step 3 - Retrieve the array from the ARRAY object
        _doUseNumericExtensionsForNumArray( array );
      }
    }
    finally
    {
      JDBCUtil.close( rset);
      JDBCUtil.close( pstmt);
    }
    return array;
  }
  private static void _doUseNumericExtensionsForNumArray( ARRAY array )
    throws SQLException
  {
    System.out.println("In _doUseNumericExtensionsForNumArray");
    // For array of numbers we can use special Oracle
    // extensions as shown below:

    int[] arrayInJava = (int[])array.getIntArray();
    for( int i=0; i < arrayInJava.length; i++ )
    {
      System.out.println(arrayInJava[i]);
    }
    System.out.println("Exiting _doUseNumericExtensionsForNumArray");
  }
  private static void _printArrayInfo( ARRAY array )
    throws SQLException
  {
    System.out.println( "In _printArrayInfo" );
    //print some info from array for demo
    System.out.println( "\tbase type name: " + array.getBaseTypeName() );
    System.out.println( "\tsql type name: " + array.getSQLTypeName() );
    System.out.println( "\tlength: " + array.length() );
    ArrayDescriptor descriptor = array.getDescriptor();
    if( descriptor.getArrayType() == ArrayDescriptor.TYPE_NESTED_TABLE )
    {
      System.out.println( "\tit is a nested table." );
    }
    else
    {
      System.out.println( "\tit is a varray." );
    }
  }
}// end of program
