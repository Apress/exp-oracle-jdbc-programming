/*@lineinfo:filename=AddressORAData*//*@lineinfo:user-code*//*@lineinfo:1^1*/package book.ch10.jpub;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import oracle.jpub.runtime.MutableStruct;
import sqlj.runtime.ref.DefaultContext;
import sqlj.runtime.ConnectionContext;

public class AddressORAData implements ORAData, ORADataFactory
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

  protected MutableStruct _struct;

  private static int[] _sqlType =  { 12,12,12,12,12,12 };
  private static ORADataFactory[] _factory = new ORADataFactory[6];
  protected static final AddressORAData _AddressORADataFactory = new AddressORAData();

  public static ORADataFactory getORADataFactory()
  { return _AddressORADataFactory; }

  protected static java.util.Hashtable _map = new java.util.Hashtable();
  protected static boolean _initialized = false;
  protected static synchronized void init()
  { if (!_initialized)
    { _initialized=true;
      _map.put("BENCHMARK.ADDRESS",book.ch10.jpub.MyAddressORAData.getORADataFactory());
  } }

  /* constructors */
  protected void _init_struct(boolean init)
  { if (init) _struct = new MutableStruct(new Object[6], _sqlType, _factory); }
  public AddressORAData()
  { _init_struct(true); __tx = DefaultContext.getDefaultContext(); }
  public AddressORAData(DefaultContext c) /*throws SQLException*/
  { _init_struct(true); __tx = c; }
  public AddressORAData(Connection c) /*throws SQLException*/
  { _init_struct(true); __onn = c; }
  public AddressORAData(String line1, String line2, String street, String city, String state, String zip) throws SQLException
  {
    _init_struct(true);
    setLine1(line1);
    setLine2(line2);
    setStreet(street);
    setCity(city);
    setState(state);
    setZip(zip);
  }

  /* ORAData interface */
  public Datum toDatum(Connection c) throws SQLException
  {
    if (__tx!=null && __onn!=c) release();
    __onn = c;
    return _struct.toDatum(c, _SQL_NAME);
  }


  /* ORADataFactory interface */
  public ORAData create(Datum d, int sqlType) throws SQLException
  { return create(null, d, sqlType); }
  public void setFrom(AddressORAData o) throws SQLException
  { setContextFrom(o); setValueFrom(o); }
  protected void setContextFrom(AddressORAData o) throws SQLException
  { release(); __tx = o.__tx; __onn = o.__onn; }
  protected void setValueFrom(AddressORAData o) { _struct = o._struct; }
  protected ORAData create(AddressORAData o, Datum d, int sqlType) throws SQLException
  {
    if (d == null) { if (o!=null) { o.release(); }; return null; }
    if (o == null) return createFromFactory("AddressORAData", d, sqlType);
    o._struct = new MutableStruct((STRUCT) d, _sqlType, _factory);
    o.__onn = ((STRUCT) d).getJavaSqlConnection();
    return o;
  }
  protected ORAData createExact(Datum d, int sqlType) throws SQLException
  {
    AddressORAData o = new AddressORAData();
    o._struct = new MutableStruct((STRUCT) d, _sqlType, _factory);
    o.__onn = ((STRUCT) d).getJavaSqlConnection();
    return o;
  }
  protected ORAData createFromFactory(String s, Datum d, int sqlType) throws SQLException
  {
    String sql = ((STRUCT) d).getSQLTypeName();
    init();
    AddressORAData factory = (AddressORAData)_map.get(sql);
    if (factory == null) {
       int p;
       if ((p=sql.indexOf(".")) >= 0) {
          factory = (AddressORAData)_map.get(sql.substring(p+1));
          if (factory!=null) _map.put(sql,factory); }
       if (factory == null) throw new SQLException
          ("Unable to convert a "+sql+" to a "+s+" or a subclass of "+s);
    }
    return factory.createExact(d,sqlType);
  }

  /* accessor methods */
  public String getLine1() throws SQLException
  { return (String) _struct.getAttribute(0); }

  public void setLine1(String line1) throws SQLException
  { _struct.setAttribute(0, line1); }


  public String getLine2() throws SQLException
  { return (String) _struct.getAttribute(1); }

  public void setLine2(String line2) throws SQLException
  { _struct.setAttribute(1, line2); }


  public String getStreet() throws SQLException
  { return (String) _struct.getAttribute(2); }

  public void setStreet(String street) throws SQLException
  { _struct.setAttribute(2, street); }


  public String getCity() throws SQLException
  { return (String) _struct.getAttribute(3); }

  public void setCity(String city) throws SQLException
  { _struct.setAttribute(3, city); }


  public String getState() throws SQLException
  { return (String) _struct.getAttribute(4); }

  public void setState(String state) throws SQLException
  { _struct.setAttribute(4, state); }


  public String getZip() throws SQLException
  { return (String) _struct.getAttribute(5); }

  public void setZip(String zip) throws SQLException
  { _struct.setAttribute(5, zip); }


  public String getAddress ()
  throws SQLException
  {
    AddressORAData __jPt_temp = this;
    String __jPt_result;
    /*@lineinfo:generated-code*//*@lineinfo:169^5*/

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
   __sJT_st = __sJT_ec.prepareOracleCall(__sJT_cc,"0book.ch10.jpub.AddressORAData",theSqlTS);
   if (__sJT_ec.isNew())
   {
      __sJT_st.registerOutParameter(1,oracle.jdbc.OracleTypes.VARCHAR);
   }
   // set IN parameters
   if (__jPt_temp==null) __sJT_st.setNull(2,2002,"BENCHMARK.ADDRESS"); else __sJT_st.setORAData(2,__jPt_temp);
 // execute statement
   __sJT_ec.oracleExecuteUpdate();
   // retrieve OUT parameters
   __jPt_result =  (String) __sJT_st.getString(1);
  } finally { __sJT_ec.oracleClose(); }
}


//  ************************************************************

/*@lineinfo:user-code*//*@lineinfo:173^5*/
    return __jPt_result;
  }
}/*@lineinfo:generated-code*/