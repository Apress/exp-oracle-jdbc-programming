set echo on
spool demo_pagination
create or replace package demo_pagination
as
  procedure get_details( p_min_row_number in number, 
    p_max_row_number in number, p_order_by_clause in varchar2,
    p_cursor in out sys_refcursor );
end;
/
show errors;

create or replace package body demo_pagination
as
  procedure get_details( p_min_row_number in number, 
    p_max_row_number in number, p_order_by_clause in varchar2,
    p_cursor in out sys_refcursor )
  is
    l_our_select_str long;
    l_pagination_select_str long;
  begin
    l_our_select_str := 'select x, y from t2 ' || p_order_by_clause;
    l_pagination_select_str := 'select x, y ' ||
                         'from ' ||
                         '( ' ||
                         ' select /*+ FIRST_ROWS */ a.*, rownum rnum ' ||
                         ' from ' ||
                         ' (' ||  l_our_select_str ||
                         ' ) a ' ||
                         ' where rownum <= :max_row_number ' ||
                        ') ' ||
                        ' where rnum >= :min_row_number';
    dbms_output.put_line( l_our_select_str );
    dbms_output.put_line( l_pagination_select_str );
    open p_cursor for l_pagination_select_str using p_max_row_number, p_min_row_number;
  end;
end;
/
variable x refcursor;
declare
begin
  demo_pagination.get_details( p_min_row_number => 1,
    p_max_row_number => 10, p_order_by_clause => ' order by x, y ',
    p_cursor => :x);
end;
/

show errors;
spool off
