#!/bin/sh
cat <<EOF > /tmp/users.sql
create user $DFTT_USER
  identified by $DFTT_PWD
  default tablespace USERS
  temporary tablespace TEMP
  profile DEFAULT;

grant connect to $DFTT_USER;
grant resource to $DFTT_USER;
grant create sequence to $DFTT_USER;
grant create table to $DFTT_USER;
grant unlimited tablespace to $DFTT_USER;
quit;

EOF

sqlplus sys/$ORACLE_PWD@localhost:1521/ORCL as SYSDBA @/tmp/users.sql 

rm /tmp/users.sql