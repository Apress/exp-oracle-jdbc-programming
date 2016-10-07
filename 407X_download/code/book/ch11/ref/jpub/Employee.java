package book.ch11.ref.jpub;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import oracle.jpub.runtime.MutableStruct;

public class Employee implements ORAData, ORADataFactory
{
  public static final String _SQL_NAME = "BENCHMARK.EMP_REF_TYPE";
  public static final int _SQL_TYPECODE = OracleTypes.STRUCT;

  protected MutableStruct _struct;

  private static int[] _sqlType =  { 2,12,2006 };
  private static ORADataFactory[] _factory = new ORADataFactory[3];
  static
  {
    _factory[2] = EmployeeRef.getORADataFactory();
  }
  protected static final Employee _EmployeeFactory = new Employee();

  public static ORADataFactory getORADataFactory()
  { return _EmployeeFactory; }
  /* constructors */
  protected void _init_struct(boolean init)
  { if (init) _struct = new MutableStruct(new Object[3], _sqlType, _factory); }
  public Employee()
  { _init_struct(true); }
  public Employee(java.math.BigDecimal empNo, String name, EmployeeRef manager) throws SQLException
  { _init_struct(true);
    setEmpNo(empNo);
    setName(name);
    setManager(manager);
  }

  /* ORAData interface */
  public Datum toDatum(Connection c) throws SQLException
  {
    return _struct.toDatum(c, _SQL_NAME);
  }


  /* ORADataFactory interface */
  public ORAData create(Datum d, int sqlType) throws SQLException
  { return create(null, d, sqlType); }
  protected ORAData create(Employee o, Datum d, int sqlType) throws SQLException
  {
    if (d == null) return null; 
    if (o == null) o = new Employee();
    o._struct = new MutableStruct((STRUCT) d, _sqlType, _factory);
    return o;
  }
  /* accessor methods */
  public java.math.BigDecimal getEmpNo() throws SQLException
  { return (java.math.BigDecimal) _struct.getAttribute(0); }

  public void setEmpNo(java.math.BigDecimal empNo) throws SQLException
  { _struct.setAttribute(0, empNo); }


  public String getName() throws SQLException
  { return (String) _struct.getAttribute(1); }

  public void setName(String name) throws SQLException
  { _struct.setAttribute(1, name); }


  public EmployeeRef getManager() throws SQLException
  { return (EmployeeRef) _struct.getAttribute(2); }

  public void setManager(EmployeeRef manager) throws SQLException
  { _struct.setAttribute(2, manager); }

}
