alter session set container=ORCL;

CREATE TABLESPACE dftt_data
DATAFILE '/opt/oracle/oradata/dftt.dbf' SIZE 5M AUTOEXTEND ON;
   
CREATE TABLESPACE users_data
DATAFILE '/opt/oracle/oradata/users.dbf' SIZE 5M AUTOEXTEND ON;

CREATE TABLESPACE dftt_index
DATAFILE '/opt/oracle/oradata/dftt_index.dbf' SIZE 5M AUTOEXTEND ON;