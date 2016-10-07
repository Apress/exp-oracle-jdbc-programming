create user adam identified by adam default tablespace users quota
unlimited on users;

grant create session to adam;
grant all on scott.emp to adam;
