create user king identified by king default tablespace users quota
unlimited on users;

grant create session to king;
grant all on scott.emp to king;
