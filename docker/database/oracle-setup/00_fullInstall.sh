#!/bin/bash
set -e

SYS_SCRIPTS=/opt/oracle/scripts/setup/sys_scripts
DFTT_SCRIPTS=/opt/oracle/scripts/setup/dftt_scripts
DFTT_USER=dftt
DFTT_PWD=changeit
DFTT_USER_AUTH=${DFTT_USER}/${DFTT_PWD}

for f in $SYS_SCRIPTS/*; do
    case "$f" in
        *.sh)     echo "$0: running $f"; . "$f" ;;
        *.sql)    echo "$0: running $f"; echo "exit" | $ORACLE_HOME/bin/sqlplus -s "sys/$ORACLE_PWD@localhost:1521/ORCL as SYSDBA" @"$f"; echo ;;
        *)        echo "$0: ignoring $f" ;;
    esac
    echo "";
done

for f in $DFTT_SCRIPTS/*; do
    case "$f" in
        *.sh)     echo "$0: running $f"; . "$f" ;;
        *.sql)    echo "$0: running $f"; echo "exit" | $ORACLE_HOME/bin/sqlplus -s "${DFTT_USER_AUTH}@localhost:1521/ORCL" @"$f"; echo ;;
        *)        echo "$0: ignoring $f" ;;
    esac
    echo "";
done