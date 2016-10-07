create or replace
package debug as

  DEBUG_OFF constant int := 0; -- no debugging messages output to 
                               -- any destination
  LOG_FOR_MIDTIER_ONLY constant int := 1; -- messages go only to middle tier
  LOG_IN_TRACE_FILES_ONLY constant int := 2; --dump only in trace files
  LOG_FOR_TRACE_FILES_AND_MTIER constant int := 3; -- both in middle tier
                                                    -- and trace files
  --
  -- Type Definitions
  --

  type Argv is table of varchar2(4000);
  emptyDebugArgv Argv;

  --
  --  Initializes the debuging for specified p_modules and will dump the
  --  output to the p_dir directory on the server for the user p_user.
  --
  procedure init(
    p_debug_flag in varchar2 default LOG_IN_TRACE_FILES_ONLY,
    p_modules     in varchar2 default 'ALL',
    p_dir         in varchar2 default 'TEMP',
    p_file        in varchar2 default user || '.dbg',
    p_user        in varchar2 default user,
    p_show_date   in varchar2 default 'YES',
    p_date_format in varchar2 default 'MMDDYYYY HH24MISS',
    p_name_len    in number default 30,
    p_show_sesid  in varchar2 default 'NO' );

  procedure status( 
    p_user in varchar2 default user,
    p_dir  in varchar2 default null,
    p_file in varchar2 default null );

  procedure f(
    p_message in varchar2,
    p_arg1    in varchar2 default null,
    p_arg2    in varchar2 default null,
    p_arg3    in varchar2 default null,
    p_arg4    in varchar2 default null,
    p_arg5    in varchar2 default null,
    p_arg6    in varchar2 default null,
    p_arg7    in varchar2 default null,
    p_arg8    in varchar2 default null,
    p_arg9    in varchar2 default null,
    p_arg10   in varchar2 default null );

  procedure fa(
    p_message in varchar2,
    p_args    in Argv default emptyDebugArgv );

  procedure clear(
    p_user in varchar2 default user,
    p_dir  in varchar2 default null,
    p_file in varchar2 default null );

  ----------------------------- get_debug_message -------------------------
  -- 
  --  gets debug messages from the temporary table where all messages
  --  in this session are stored (if configured to do so.)
  --
  --  it returns a cursor containing debug messages.
  --
  --  PARAMETERS
  --  ----------
  --
  --  RETURNS
  --  ----------
  --    cursor containing debug message lines
  ----------------------------------------------------------------------

  function get_debug_message
    return sys_refcursor;

  ----------------------------- get_debug_message_flush ----------------
  --  gets debug messages from the temporary table where all messages
  --  in this session are stored (if configured to do so.) it then
  --  deletes the messages. This is useful if you don't want to see
  --  the same messages again.
  --
  --  it returns a cursor containing debug messages.
  --
  --  PARAMETERS
  --  ----------
  --
  --  RETURNS
  --  ----------
  --    cursor containing debug message lines
  ----------------------------------------------------------------------
  function get_debug_message_flush
    return sys_refcursor ;
end debug;
/

show error

