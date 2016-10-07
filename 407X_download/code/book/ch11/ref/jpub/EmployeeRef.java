package book.ch11.ref.jpub;

import java.sql.SQLException;
import java.sql.Connection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.Datum;
import oracle.sql.REF;
import oracle.sql.STRUCT;

public class EmployeeRef implements ORAData, ORADataFactory
{
  public static final String _SQL_BASETYPE = "BENCHMARK.EMP_REF_TYPE";
  public static final int _SQL_TYPECODE = OracleTypes.REF;

  REF _ref;

private static final EmployeeRef _EmployeeRefFactory = new EmployeeRef();

  public static ORADataFactory getORADataFactory()
  { return _EmployeeRefFactory; }
  /* constructor */
  public EmployeeRef()
  {
  }

  /* ORAData interface */
  public Datum toDatum(Connection c) throws SQLException
  {
    return _ref;
  }

  /* ORADataFactory interface */
  public ORAData create(Datum d, int sqlType) throws SQLException
  {
    if (d == null) return null; 
    EmployeeRef r = new EmployeeRef();
    r._ref = (REF) d;
    return r;
  }

  public static EmployeeRef cast(ORAData o) throws SQLException
  {
     if (o == null) return null;
     try { return (EmployeeRef) getORADataFactory().create(o.toDatum(null), OracleTypes.REF); }
     catch (Exception exn)
     { throw new SQLException("Unable to convert "+o.getClass().getName()+" to EmployeeRef: "+exn.toString()); }
  }

  public Employee getValue() throws SQLException
  {
     return (Employee) Employee.getORADataFactory().create(
       _ref.getSTRUCT(), OracleTypes.REF);
  }

  public void setValue(Employee c) throws SQLException
  {
    _ref.setValue((STRUCT) c.toDatum(_ref.getJavaSqlConnection()));
  }
}
