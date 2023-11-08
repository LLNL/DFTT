#!/bin/sh
java ${JAVA_OPTS}  -DEXTERNAL_SCHEMA_NAME=LLNL2  -cp /opt/apps/DetectionFrameworkv2.0/latest/detection-framework-opensource-2.0.2.jar llnl.gnem.apps.detection.util.configuration.ConfigCreator $@

