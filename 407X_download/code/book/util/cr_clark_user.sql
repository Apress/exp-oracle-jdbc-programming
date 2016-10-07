create user clark identified by clark default tablespace users quota
unlimited on users;

grant create session to clark;
grant all on scott.emp to clark;
