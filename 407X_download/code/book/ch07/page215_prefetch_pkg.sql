set echo on
spool prefetch_pkg
create or replace package prefetch_pkg
as
  procedure get_details( p_num_of_rows in number, p_sql_tag in varchar2,
    p_cursor in out sys_refcursor );
end;
/
show errors;

create or replace package body prefetch_pkg
as
  procedure get_details( p_num_of_rows in number, p_sql_tag in varchar2,
    p_cursor in out sys_refcursor )
  is
  begin
    open p_cursor for
      'select '|| p_sql_tag || 'x from t1 where rownum <= :p_fetch_size ' using p_num_of_rows;
  end;
end;
/
show errors;
spool off
