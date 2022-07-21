#!/bin/bash
cat << EOF > /opt/apps/gnemCoreDbCfg.xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<oracleDbServices xmlns="http://www.llnl.gov/gnemcore/1.0.0">
    <services>
        <service>
            <serviceId>dftt-db</serviceId>
            <connectionDescriptor>
            (DESCRIPTION= 
            (ADDRESS=(PROTOCOL = TCP)(HOST = $DB_URL)(PORT = $DB_PORT))
            (CONNECT_DATA= 
                (SERVICE_NAME=$DB_NAME)))
            </connectionDescriptor>
            <isSensitiveService>true</isSensitiveService>
        </service>
    </services>
</oracleDbServices>
EOF