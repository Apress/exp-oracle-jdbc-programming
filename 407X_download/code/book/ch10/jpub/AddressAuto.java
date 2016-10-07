/*@lineinfo:filename=AddressAuto*//*@lineinfo:user-code*//*@lineinfo:1^1*/package book.ch10.jpub;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import java.sql.SQLData;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import oracle.sql.STRUCT;
import oracle.jpub.runtime.MutableStruct;
import sqlj.runtime.ref.DefaultContext;
import sqlj.runtime.ConnectionContext;

public class AddressAuto implements SQLData
{
  public static final String _SQL_NAME = "BENCHMARK.ADDRESS";
  public static final int _SQL_TYPECODE = OracleTypes.STRUCT;

  /* connection management */
  protected DefaultContext __tx = null;
  protected Connection __onn = null;
  public void setConnectionContext(DefaultContext ctx) throws SQLException
  { release(); __tx = ctx; }
  public DefaultContext getConnectionContext() throws SQLException
  { if (__tx==null)
    { __tx = (__onn==null) ? DefaultContext.getDefaultContext() : new DefaultContext(__onn); }
    return __tx;
  };
  public Connection getConnection() throws SQLException
  { return (__onn==null) ? ((__tx==null) ? null : __tx.getConnection()) : __onn; }
  public void release() throws SQLException
  { if (__tx!=null && __onn!=null) __tx.close(ConnectionContext.KEEP_CONNECTION);
    __onn = null; __tx = null;
  }

  private String m_line1;
  private String m_line2;
  private String m_street;
  private String m_city;
  private String m_state;
  private String m_zip;

  /* constructors */
  public AddressAuto()
  { __tx = DefaultContext.getDefaultContext(); }
  public AddressAuto(DefaultContext c) /*throws SQLException*/
  { __tx = c; }
  public AddressAuto(Connection c) /*throws SQLException*/
  { __onn = c;  }

  public AddressAuto(String line1, String line2, String street, String city, String state, String zip) throws SQLException
  {
    setLine1(line1);
    setLine2(line2);
    setStreet(street);
    setCity(city);
    setState(state);
    setZip(zip);
  }
  public void readSQL(SQLInput stream, String type)
  throws SQLException
  {
      setLine1(stream.readString());
      setLine2(stream.readString());
      setStreet(stream.readString());
      setCity(stream.readString());
      setState(stream.readString());
      setZip(stream.readString());
  }

  public void writeSQL(SQLOutput stream)
  throws SQLException
  {
      stream.writeString(getLine1());
      stream.writeString(getLine2());
      stream.writeString(getStreet());
      stream.writeString(getCity());
      stream.writeString(getState());
      stream.writeString(getZip());
  }

  public String getSQLTypeName() throws SQLException
  {
    return _SQL_NAME;
  }

  /* accessor methods */
  public String getLine1()
  { return m_line1; }

  public void setLine1(String line1)
  { m_line1 = line1; }


  public String getLine2()
  { return m_line2; }

  public void setLine2(String line2)
  { m_line2 = line2; }


  public String getStreet()
  { return m_street; }

  public void setStreet(String street)
  { m_street = street; }


  public String getCity()
  { return m_city; }

  public void setCity(String city)
  { m_city = city; }


  public String getState()
  { return m_state; }

  public void setState(String state)
  { m_state = state; }


  public String getZip()
  { return m_zip; }

  public void setZip(String zip)
  { m_zip = zip; }


  public String getAddress ()
  throws SQLException
  {
    AddressAuto __jPt_temp = this;
    String __jPt_result;
    /*@lineinfo:generated-code*//*@lineinfo:136^5*/

//  ************************************************************
//  #sql [getConnectionContext()] { BEGIN
//        :__jPt_result := :__jPt_temp.GET_ADDRESS();
//        END;
//       };
//  ************************************************************

{
  // declare temps
  oracle.jdbc.OracleCallableStatement __sJT_st = null;
  sqlj.runtime.ref.DefaultContext __sJT_cc = getConnectionContext(); if (__sJT_cc==null) sqlj.runtime.error.RuntimeRefErrors.raise_NULL_CONN_CTX();
  sqlj.runtime.ExecutionContext.OracleContext __sJT_ec = ((__sJT_cc.getExecutionContext()==null) ? sqlj.runtime.ExecutionContext.raiseNullExecCtx() : __sJT_cc.getExecutionContext().getOracleContext());
  try {
   String theSqlTS = "BEGIN\n       :1  :=  :2 .GET_ADDRESS();\n      END;";
   __sJT_st = __sJT_ec.prepareOracleCall(__sJT_cc,"0book.ch10.jpub.AddressAuto",theSqlTS);
   if (__sJT_ec.isNew())
   {
      __sJT_st.registerOutParameter(1,oracle.jdbc.OracleTypes.VARCHAR);
   }
   // set IN parameters
   __sJT_st.setObject(2,__jPt_temp);
 // execute statement
   __sJT_ec.oracleExecuteUpdate();
   // retrieve OUT parameters
   __jPt_result =  (String) __sJT_st.getString(1);
  } finally { __sJT_ec.oracleClose(); }
}


//  ************************************************************

/*@lineinfo:user-code*//*@lineinfo:140^5*/
    return __jPt_result;
  }
}/*@lineinfo:generated-code*/