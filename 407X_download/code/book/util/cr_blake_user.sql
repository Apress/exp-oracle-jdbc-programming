create user blake identified by blake default tablespace users quota
unlimited on users;

grant create session to blake;
grant all on scott.emp to blake;
