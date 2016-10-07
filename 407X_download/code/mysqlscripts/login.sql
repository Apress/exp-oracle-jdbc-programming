REM turn off the terminal output - make it so SQLPlus does not
REM print out anything when we log in
set tab off
set termout off
set head off

REM default your editor here.  SQLPlus has many individual settings
REM This is one of the most important ones
REM define _editor=vi

REM serveroutput controls whether your DBMS_OUTPUT.PUT_LINE calls
REM go into the bit bucket (serveroutput off) or get displayed
REM on screen.  I always want serveroutput set on and as big
REM as possible - this does that.  The format wrapped elements
REM causes SQLPlus to preserve leading whitespace - very useful
set serveroutput on size 1000000

REM Here I set some default column widths for commonly queried
REM columns - columns I find myself setting frequently, day after day
column object_name format a30
column segment_name format a30
column file_name format a40
column name format a30
column file_name format a30
column what format a30 word_wrapped
column plan_plus_exp format a100

REM by default, a spool file is a fixed width file with lots of
REM trailing blanks.  Trimspool removes these trailing blanks
REM making the spool file significantly smaller 
set trimspool on

REM LONG controls how much of a LONG or CLOB sqlplus displays
REM by default.  It defaults to 80 characters which in general
REM is far too small.  I use the first 5000 characters by default
set long 5000

REM This sets the default width at which sqlplus wraps output.
REM I use a telnet client that can go upto 131 characters wide -
REM hence this is my preferred setting.
set linesize 131

REM SQLplus will print column headings every N lines of output
REM this defaults to 14 lines.  I find that they just clutter my
REM screen so this setting effectively disables them for all
REM intents and purposes - except for the first page of course
set pagesize 9999

REM here is how I set my signature prompt in sqlplus to
REM username@database>   I use the NEW_VALUE concept to format
REM a nice prompt string that defaults to IDLE (useful for those
REM of you that use sqlplus to startup their databases - the
REM prompt will default to idle> if your database isn't started)
define gname=idle
column global_name new_value gname
select lower(user) || '@' ||
       substr( global_name, 1, decode( dot,
                                       0, length(global_name),
                                          dot-1) ) global_name
  from (select global_name, instr(global_name,'.') dot
          from global_name );
set sqlprompt '&gname> '

REM and lastly, we'll put termout back on so sqlplus prints
REM to the screen
set termout on
