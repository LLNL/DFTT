FROM llnl/docker-gui:8.4

ARG NAME="Detection Framework GUI"
ARG DESCRIPTION="GUI based tools for the Detection Framework project"
ARG LICENSE="MIT"
ARG VENDOR="LLNL"

ARG DB_URL=dftt-db-service
ARG DB_PORT=1521
ARG DB_NAME=ORCL
ARG EXTERNAL_SCHEMA_NAME=dftt

ENV DB_URL=$DB_URL
ENV DB_PORT=$DB_PORT
ENV DB_NAME=$DB_NAME
ENV EXTERNAL_SCHEMA_NAME=$EXTERNAL_SCHEMA_NAME

ARG JAVA_OPTS="-XX:MaxRAMPercentage=80 -Ddatabase_type=ORACLE -Dsun.java2d.xrender=false -Djava.util.prefs.systemRoot=/tmp/.java -Djava.util.prefs.syncInterval=2000000 -Dllnl.gnemcore.config.dir=/opt/apps/"
ENV JAVA_OPTS=${JAVA_OPTS} -DEXTERNAL_SCHEMA_NAME=${EXTERNAL_SCHEMA_NAME}

LABEL name=$NAME
LABEL maintainer="Justin Barno (barno1@llnl.gov)"
LABEL gov.llnl.tags="correlation, geophysics, utilities"

ADD DetectionFrameworkOpenSource-*.jar /opt/apps/
ADD Builder_2.0.sh /opt/apps/
ADD ConfigCreator_2.0.sh /opt/apps/
ADD DetSegExtractor_2.0.sh /opt/apps/
ADD FrameworkRunner_2.0.sh /opt/apps/
ADD setup_database.sh /setup_database.sh

CMD ["/bin/bash", "-c", "/setup_database.sh && /run.sh"]