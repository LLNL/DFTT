--------------------------------------------------------
--  File created - Tuesday-October-31-2023   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence BEAMID
--------------------------------------------------------

   CREATE SEQUENCE  "BEAMID"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 2 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence CLUSTERID
--------------------------------------------------------

   CREATE SEQUENCE  "CLUSTERID"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 2 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence CONFIGID
--------------------------------------------------------

   CREATE SEQUENCE  "CONFIGID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 461 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence DETECTIONID
--------------------------------------------------------

   CREATE SEQUENCE  "DETECTIONID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 252981 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence DETECTORID
--------------------------------------------------------

   CREATE SEQUENCE  "DETECTORID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 48061 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence FILTERID
--------------------------------------------------------

   CREATE SEQUENCE  "FILTERID"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 5071 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence PICKID
--------------------------------------------------------

   CREATE SEQUENCE  "PICKID"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence RUNID
--------------------------------------------------------

   CREATE SEQUENCE  "RUNID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 93 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence STREAMID
--------------------------------------------------------

   CREATE SEQUENCE  "STREAMID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 83 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence TRIGGERID
--------------------------------------------------------

   CREATE SEQUENCE  "TRIGGERID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1043861 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Table APP_USER_FILTER
--------------------------------------------------------

  CREATE TABLE "APP_USER_FILTER" 
   (	"APP_NAME" VARCHAR2(50 BYTE), 
	"AUTH" VARCHAR2(15 BYTE), 
	"FILTERID" NUMBER(9,0)
   ) ;
--------------------------------------------------------
--  DDL for Table ARRAY_DETECTOR_PARAMS
--------------------------------------------------------

  CREATE TABLE "ARRAY_DETECTOR_PARAMS" 
   (	"DETECTORID" NUMBER(9,0), 
	"ARRAY_NAME" VARCHAR2(10 BYTE), 
	"STA_DURATION" NUMBER, 
	"LTA_DURATION" NUMBER, 
	"GAP_DURATION" NUMBER, 
	"ENABLE_SPAWNING" VARCHAR2(1 BYTE), 
	"BACK_AZIMUTH" NUMBER, 
	"VELOCITY" NUMBER
   ) ;

   COMMENT ON TABLE "ARRAY_DETECTOR_PARAMS"  IS 'This detector contains the parameters that define the operation of an array power detector that has been previously instantiated and stored in the database.';
--------------------------------------------------------
--  DDL for Table ARRAY_INFO
--------------------------------------------------------

  CREATE TABLE "ARRAY_INFO" 
   (	"AGENCY" VARCHAR2(15 BYTE), 
	"NETWORK_CODE" VARCHAR2(20 BYTE), 
	"ARRAY_NAME" VARCHAR2(15 BYTE), 
	"STATION_CODE" VARCHAR2(15 BYTE), 
	"STANAME" VARCHAR2(250 BYTE), 
	"BEGIN_TIME" NUMBER, 
	"ONDATE" NUMBER, 
	"END_TIME" NUMBER, 
	"OFFDATE" NUMBER, 
	"LAT" FLOAT(53), 
	"LON" FLOAT(53), 
	"ELEV" NUMBER, 
	"STATYPE" VARCHAR2(4 BYTE), 
	"DNORTH" NUMBER, 
	"DEAST" NUMBER
   ) ;

   COMMENT ON TABLE "ARRAY_INFO"  IS 'The ARAY_INFO table is used by DFTT to define seismic arrays for use in ARRAY configurations.  When a user runs ConfigCreator with the -r option, the specified name is used to look up the array elements and their offsets  in this table.';

--------------------------------------------------------
--  DDL for Table ARRAY_INFO_IRIS
--------------------------------------------------------

  CREATE TABLE "ARRAY_INFO_IRIS" 
   (	"AGENCY" CHAR(7 BYTE), 
	"NETWORK_CODE" CHAR(2 BYTE), 
	"ARRAY_NAME" VARCHAR2(15 BYTE), 
	"STATION_CODE" VARCHAR2(15 BYTE), 
	"STANAME" VARCHAR2(250 BYTE), 
	"BEGIN_TIME" NUMBER, 
	"ONDATE" NUMBER, 
	"END_TIME" NUMBER, 
	"OFFDATE" NUMBER, 
	"LAT" FLOAT(53), 
	"LON" FLOAT(53), 
	"ELEV" NUMBER, 
	"STATYPE" VARCHAR2(4 BYTE), 
	"DNORTH" NUMBER, 
	"DEAST" NUMBER
   ) ;
  GRANT SELECT ON "ARRAY_INFO_IRIS" TO PUBLIC;
--------------------------------------------------------
--  DDL for Table ARRIVAL
--------------------------------------------------------

  CREATE TABLE "ARRIVAL" 
   (	"EVENT_ID" NUMBER(10,0), 
	"ARRIVAL_ID" NUMBER(10,0), 
	"ARRIVAL_TYPE" VARCHAR2(30 BYTE), 
	"CONFIGID" NUMBER(9,0), 
	"AGENCY" VARCHAR2(20 BYTE), 
	"NETWORK" VARCHAR2(10 BYTE), 
	"NETWORK_START_DATE" NUMBER(8,0), 
	"STATION_CODE" VARCHAR2(10 BYTE), 
	"CHAN" VARCHAR2(8 BYTE), 
	"LOCATION_CODE" VARCHAR2(10 BYTE), 
	"IPHASE" VARCHAR2(20 BYTE), 
	"TIME" NUMBER, 
	"DETECTORID" NUMBER(9,0), 
	"DETECTIONID" NUMBER(9,0)
   ) ;
--------------------------------------------------------
--  DDL for Table BULLETIN_DETECTOR_SPEC
--------------------------------------------------------

  CREATE TABLE "BULLETIN_DETECTOR_SPEC" 
   (	"DETECTORID" NUMBER(9,0), 
	"BULLETIN" BLOB, 
	"TYPE" VARCHAR2(20 BYTE), 
	"ENABLE_SPAWNING" VARCHAR2(1 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table CONFIGURATION
--------------------------------------------------------

  CREATE TABLE "CONFIGURATION" 
   (	"CONFIGID" NUMBER(9,0), 
	"CONFIG_NAME" VARCHAR2(100 BYTE), 
	"CONFIG_DIR" VARCHAR2(1000 BYTE), 
	"CONFIG_FILE_NAME" VARCHAR2(500 BYTE), 
	"SOURCE_TYPE" VARCHAR2(30 BYTE), 
	"SOURCE_IDENTIFIER" VARCHAR2(200 BYTE)
   ) ;

   COMMENT ON COLUMN "CONFIGURATION"."CONFIG_NAME" IS 'A unique name that identifies this configuration. ';
   COMMENT ON COLUMN "CONFIGURATION"."CONFIG_DIR" IS 'Directory containing the config files';
      COMMENT ON TABLE "CONFIGURATION"  IS 'This table contains configuration information for StreamProcessors. A configuration specifies a collection of channels that will be processed by the same stream processor ( either wide band or narrow band).';
  GRANT SELECT ON "CONFIGURATION" TO PUBLIC;
--------------------------------------------------------
--  DDL for Table CONFIGURATION_GROUP
--------------------------------------------------------

  CREATE TABLE "CONFIGURATION_GROUP" 
   (	"GROUPID" NUMBER, 
	"CONFIGID" NUMBER(9,0)
   ) ;

   COMMENT ON TABLE "CONFIGURATION_GROUP"  IS 'Builder has a limited ability to perform picking and event formation using detection results from multiple configurations for stations processing common events. The CONFIGURATION_GROUP table is used to relate these common configurations. It must be populated manually.';
--------------------------------------------------------
--  DDL for Table DETECTION
--------------------------------------------------------

  CREATE TABLE "DETECTION" 
   (	"DETECTIONID" NUMBER(9,0), 
	"RUNID" NUMBER(9,0), 
	"TRIGGERID" NUMBER(9,0), 
	"DETECTORID" NUMBER(9,0)
   ) ;

   COMMENT ON COLUMN "DETECTION"."DETECTORID" IS 'This is understood to be the ID of the detector that originally created the detection. Later association to a different detector is handled using the detection_detector_assoc table.';
   COMMENT ON TABLE "DETECTION"  IS 'This represents a trigger that has been promoted to a detection. In current form, the detectorid key is a reference to the detector that created this detection. When a subspace detector is created from an incoherent detector, the detectorid is set to be that of the new subspace detector. It might be better to remove this column and relay on the detection_detector assoc table. But then, what to do about trigger_record?';
  GRANT SELECT ON "DETECTION" TO PUBLIC;
--------------------------------------------------------
--  DDL for Table DETECTION_FK_MEASUREMENT
--------------------------------------------------------

  CREATE TABLE "DETECTION_FK_MEASUREMENT" 
   (	"DETECTIONID" NUMBER(9,0), 
	"TIME" NUMBER, 
	"WINLEN" NUMBER, 
	"FK_QUAL" NUMBER, 
	"BACK_AZIMUTH" NUMBER, 
	"VELOCITY" NUMBER, 
	"SX" NUMBER, 
	"SY" NUMBER
   ) ;

   COMMENT ON TABLE "DETECTION_FK_MEASUREMENT"  IS 'FK measurements made in Builder are stored in this table.';
--------------------------------------------------------
--  DDL for Table DETECTOR
--------------------------------------------------------

  CREATE TABLE "DETECTOR" 
   (	"DETECTORID" NUMBER(9,0), 
	"STREAMID" NUMBER(9,0), 
	"DETECTORTYPE" VARCHAR2(30 BYTE), 
	"THRESHOLD" NUMBER, 
	"BLACKOUT_SECONDS" NUMBER, 
	"CREATION_RUNID" NUMBER(9,0), 
	"SOURCE_INFO" VARCHAR2(250 BYTE), 
	"IS_RETIRED" VARCHAR2(1 BYTE), 
	"LDDATE" DATE
   ) ;

   COMMENT ON COLUMN "DETECTOR"."THRESHOLD" IS 'A trigger is declared when detection statistic exceeds this value.';
   COMMENT ON COLUMN "DETECTOR"."BLACKOUT_SECONDS" IS 'No new triggers may be declared within blackout_seconds of an existing trigger.';
   COMMENT ON COLUMN "DETECTOR"."CREATION_RUNID" IS 'For detectors created by the framework this is the runid of the framework run in which the detector was created. Otherwise this column is null. This column is used to allow a run of the framework to be performed using only detectors that were created in a specified run of the framework.';
   COMMENT ON COLUMN "DETECTOR"."SOURCE_INFO" IS 'When not null, this column contains information about the source of the detector. For example, subspace detectors created from user-supplied templates have this column set to the path of the detector template.';
   COMMENT ON COLUMN "DETECTOR"."IS_RETIRED" IS 'When a detector is taken out of service because all of its detections have re-assigned to other detectors, it is removed from the framework and this column i sset to ''y''. When retrieving detectors at start of run, do not retrieve retired detectors.';
   COMMENT ON TABLE "DETECTOR"  IS 'All detectors have an entry in this table. It identifies the detector type and holds other attributes that are common to all detectors.';
--------------------------------------------------------
--  DDL for Table DETECTOR_CHANNEL
--------------------------------------------------------

  CREATE TABLE "DETECTOR_CHANNEL" 
   (	"DETECTORID" NUMBER(9,0), 
	"AGENCY" VARCHAR2(10 BYTE), 
	"NETWORK_CODE" VARCHAR2(10 BYTE), 
	"NET_START_DATE" NUMBER(8,0), 
	"STATION_CODE" VARCHAR2(8 BYTE), 
	"CHAN" VARCHAR2(8 BYTE), 
	"LOCATION_CODE" VARCHAR2(4 BYTE), 
	"POSITION" NUMBER(8,0)
   ) ;
--------------------------------------------------------
--  DDL for Table DETECTOR_PHASE_PICK
--------------------------------------------------------

  CREATE TABLE "DETECTOR_PHASE_PICK" 
   (	"PICKID" NUMBER(10,0), 
	"DETECTORID" NUMBER(9,0), 
	"DETECTIONID" NUMBER(10,0), 
	"PHASE" VARCHAR2(8 BYTE), 
	"TIME" NUMBER, 
	"PICK_STD" NUMBER, 
	"LDDATE" DATE
   ) ;
--------------------------------------------------------
--  DDL for Table DETECTOR_THRESHOLD_HISTORY
--------------------------------------------------------

  CREATE TABLE "DETECTOR_THRESHOLD_HISTORY" 
   (	"RUNID" NUMBER(9,0), 
	"DETECTORID" NUMBER(9,0), 
	"TIME" NUMBER, 
	"THRESHOLD" NUMBER
   ) ;

   COMMENT ON TABLE "DETECTOR_THRESHOLD_HISTORY"  IS 'The DETECTOR_THRESHOLD_HISTORY contains for each detector and runid a history of the threshold in effect at (data) time instants. At every point where threshold estimation occurs a new value is calculated from the accumulated detection statistics. However, the value actually adopted is the mean of the previous threshold and the newly-calculated threshold.';
--------------------------------------------------------
--  DDL for Table DETECTOR_TRAINING_DATA
--------------------------------------------------------

  CREATE TABLE "DETECTOR_TRAINING_DATA" 
   (	"DETECTORID" NUMBER(9,0), 
	"STATUS" VARCHAR2(15 BYTE)
   ) ;

   COMMENT ON TABLE "DETECTOR_TRAINING_DATA"  IS 'The Builder program has the ability to classify detectors as being one of (good, bad, unusable)
where bad is intended to mean the template contains artifacts and unusable means that the signal
is just different from the kind of signal which we hope to detect.
Training occurs by typing one of ("g", "b","u") while a detector is selected. This table holds the training data.';
--------------------------------------------------------
--  DDL for Table DET_STAT_HISTOGRAM
--------------------------------------------------------

  CREATE TABLE "DET_STAT_HISTOGRAM" 
   (	"DETECTORID" NUMBER(10,0), 
	"RUNID" NUMBER(9,0), 
	"BIN_NUM" NUMBER(8,0), 
	"VALUE" NUMBER
   ) ;
--------------------------------------------------------
--  DDL for Table FRAMEWORK_RUN
--------------------------------------------------------

  CREATE TABLE "FRAMEWORK_RUN" 
   (	"RUNID" NUMBER(9,0), 
	"RUN_DATE" DATE, 
	"WFDISC_USED" VARCHAR2(300 BYTE), 
	"CONFIGID" NUMBER(9,0), 
	"CONFIG_FILE_TEXT" BLOB, 
	"COMMAND_LINE_TEXT" VARCHAR2(500 BYTE), 
	"END_DATE" DATE, 
	"FIXED_RAW_SAMPLE_RATE" NUMBER
   ) ;

   COMMENT ON TABLE "FRAMEWORK_RUN"  IS 'Every time the framework is run a new entry is created here. The various result tables all hang off this table.';
--------------------------------------------------------
--  DDL for Table GROUP_STATION_DATA
--------------------------------------------------------

  CREATE TABLE "GROUP_STATION_DATA" 
   (	"GROUPID" NUMBER, 
	"CONFIGID" NUMBER(9,0), 
	"STA" VARCHAR2(10 BYTE), 
	"STLA" NUMBER, 
	"STLO" NUMBER
   ) ;

   COMMENT ON TABLE "GROUP_STATION_DATA"  IS 'This table is used in multi-configuration processing and specifically for populating the multi-station picing dialog.
The table must be populated manually. It is only read by Builder.';
--------------------------------------------------------
--  DDL for Table ORIGIN
--------------------------------------------------------

  CREATE TABLE "ORIGIN" 
   (	"EVID" NUMBER(9,0), 
	"LAT" NUMBER, 
	"LON" NUMBER, 
	"DEPTH" NUMBER, 
	"TIME" NUMBER, 
	"MAG" NUMBER, 
	"AUTH" VARCHAR2(15 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table PHASE_PICK
--------------------------------------------------------

  CREATE TABLE "PHASE_PICK" 
   (	"PICKID" NUMBER(10,0), 
	"CONFIGID" NUMBER(9,0), 
	"DETECTIONID" NUMBER(10,0), 
	"AGENCY" VARCHAR2(10 BYTE), 
	"NETWORK" VARCHAR2(10 BYTE), 
	"NETWORK_START_DATE" NUMBER(8,0), 
	"STATION_CODE" VARCHAR2(6 BYTE), 
	"CHAN" VARCHAR2(8 BYTE), 
	"LOCATION_CODE" VARCHAR2(2 BYTE), 
	"PHASE" VARCHAR2(8 BYTE), 
	"TIME" NUMBER, 
	"PICK_STD" NUMBER, 
	"LDDATE" DATE
   ) ;

   COMMENT ON TABLE "PHASE_PICK"  IS 'This table contains picks made in the Builder program.';
--------------------------------------------------------
--  DDL for Table SITE
--------------------------------------------------------

  CREATE TABLE "SITE" 
   (	"STA" VARCHAR2(10 BYTE), 
	"ONDATE" NUMBER(8,0), 
	"OFFDATE" NUMBER(8,0), 
	"LAT" FLOAT(53), 
	"LON" FLOAT(53), 
	"ELEV" FLOAT(24), 
	"STANAME" VARCHAR2(250 BYTE), 
	"STATYPE" VARCHAR2(4 BYTE), 
	"REFSTA" VARCHAR2(10 BYTE), 
	"DNORTH" FLOAT(53), 
	"DEAST" FLOAT(53), 
	"LDDATE" DATE
   ) ;
  GRANT SELECT ON "SITE" TO PUBLIC;
--------------------------------------------------------
--  DDL for Table STALTA_DETECTOR_PARAMS
--------------------------------------------------------

  CREATE TABLE "STALTA_DETECTOR_PARAMS" 
   (	"DETECTORID" NUMBER(9,0), 
	"STA_DURATION" NUMBER, 
	"LTA_DURATION" NUMBER, 
	"GAP_DURATION" NUMBER, 
	"ENABLE_SPAWNING" VARCHAR2(1 BYTE)
   ) ;

   COMMENT ON TABLE "STALTA_DETECTOR_PARAMS"  IS 'Holds parameters required to construct an STA/LTA detector.';
--------------------------------------------------------
--  DDL for Table STORED_FILTER
--------------------------------------------------------

  CREATE TABLE "STORED_FILTER" 
   (	"FILTER_ID" NUMBER(9,0), 
	"TYPE" VARCHAR2(2 BYTE), 
	"CAUSAL" VARCHAR2(1 BYTE), 
	"FILTER_ORDER" NUMBER(8,0), 
	"LOWPASS" NUMBER, 
	"HIGHPASS" NUMBER, 
	"DESCRIPTION" VARCHAR2(100 BYTE), 
	"IMPULSE_RESPONSE" VARCHAR2(3 BYTE), 
	"AUTH" VARCHAR2(50 BYTE), 
	"IS_DEFAULT" VARCHAR2(1 BYTE)
   ) ;

   COMMENT ON TABLE "STORED_FILTER"  IS 'This table contains definitions of IIR filters. It is used primarily by the Builder pogram.';
--------------------------------------------------------
--  DDL for Table STREAM
--------------------------------------------------------

  CREATE TABLE "STREAM" 
   (	"STREAMID" NUMBER(9,0), 
	"CONFIGID" NUMBER(9,0), 
	"STREAM_NAME" VARCHAR2(200 BYTE), 
	"STREAM_DIR" VARCHAR2(1000 BYTE), 
	"STREAM_CONFIG_FILE_NAME" VARCHAR2(100 BYTE), 
	"PREPROCESSOR_PARAMS" BLOB, 
	"LOW_CORNER" NUMBER, 
	"HIGH_CORNER" NUMBER, 
	"FILTER_ORDER" NUMBER
   ) ;

   COMMENT ON COLUMN "STREAM"."STREAM_DIR" IS 'The directory containing the stream configuration files.';
  GRANT SELECT ON "STREAM" TO PUBLIC;
--------------------------------------------------------
--  DDL for Table STREAM_CHANNEL
--------------------------------------------------------

  CREATE TABLE "STREAM_CHANNEL" 
   (	"STREAMID" NUMBER(9,0), 
	"AGENCY" VARCHAR2(10 BYTE), 
	"NETWORK_CODE" VARCHAR2(10 BYTE), 
	"NET_START_DATE" NUMBER(8,0), 
	"STATION_CODE" VARCHAR2(8 BYTE), 
	"CHAN" VARCHAR2(8 BYTE), 
	"LOCATION_CODE" VARCHAR2(4 BYTE)
   ) ;

   COMMENT ON TABLE "STREAM_CHANNEL"  IS 'This table specifies the channels that make up a stream.';
  GRANT SELECT ON "STREAM_CHANNEL" TO PUBLIC;
--------------------------------------------------------
--  DDL for Table STREAM_FK_PARAM
--------------------------------------------------------

  CREATE TABLE "STREAM_FK_PARAM" 
   (	"STREAMID" NUMBER(9,0), 
	"CONFIGID" NUMBER(9,0), 
	"STREAM_NAME" VARCHAR2(200 BYTE), 
	"FKS_MAX" NUMBER, 
	"MIN_FK_FREQ" NUMBER, 
	"MAX_FK_FREQ" NUMBER, 
	"MIN_FK_QUAL" NUMBER, 
	"FK_WIN_LEN" NUMBER
   ) ;
--------------------------------------------------------
--  DDL for Table SUBSPACE_DETECTOR_PARAMS
--------------------------------------------------------

  CREATE TABLE "SUBSPACE_DETECTOR_PARAMS" 
   (	"DETECTORID" NUMBER(9,0), 
	"NUM_CHANNELS" NUMBER(9,0), 
	"RANK" NUMBER(9,0), 
	"STA_DURATION" NUMBER, 
	"LTA_DURATION" NUMBER, 
	"GAP_DURATION" NUMBER, 
	"NORMALIZATION" VARCHAR2(20 BYTE)
   ) ;

   COMMENT ON TABLE "SUBSPACE_DETECTOR_PARAMS"  IS 'contains all parameters (not template data) required to construct a subspace detector.';
--------------------------------------------------------
--  DDL for Table SUBSPACE_TEMPLATE
--------------------------------------------------------

  CREATE TABLE "SUBSPACE_TEMPLATE" 
   (	"DETECTORID" NUMBER(9,0), 
	"TEMPLATE" BLOB DEFAULT empty_blob(), 
	"NORMALIZATION" VARCHAR2(20 BYTE), 
	"TBP" NUMBER
   ) ;

   COMMENT ON COLUMN "SUBSPACE_TEMPLATE"."DETECTORID" IS 'Foreign key from the detector table';
   COMMENT ON COLUMN "SUBSPACE_TEMPLATE"."TEMPLATE" IS 'Serialized SubspaceTemplate object';
   COMMENT ON COLUMN "SUBSPACE_TEMPLATE"."TBP" IS 'Time bandwidth Product of template';
   COMMENT ON TABLE "SUBSPACE_TEMPLATE"  IS 'This table contains the template data for the subspace detectors.';
--------------------------------------------------------
--  DDL for Table TRIGGER_CLASSIFICATION
--------------------------------------------------------

  CREATE TABLE "TRIGGER_CLASSIFICATION" 
   (	"TRIGGERID" NUMBER(9,0), 
	"ARTIFACT_STATUS" VARCHAR2(15 BYTE), 
	"USABILITY_STATUS" VARCHAR2(15 BYTE)
   ) ;

   COMMENT ON TABLE "TRIGGER_CLASSIFICATION"  IS 'The Builder program is capable of training a random forest classifier that can categorize a detector as one of
(good, bad, unusable). At one point the framework_runner had an option to use this classifier to classify triggers.
Such classified triggers would be recorded in this table. That option has since een removed. However, the code to write
a classification result still exists and full capability could be reinstated at some point.';
  GRANT SELECT ON "TRIGGER_CLASSIFICATION" TO PUBLIC;
--------------------------------------------------------
--  DDL for Table TRIGGER_DATA_DEFECT
--------------------------------------------------------

  CREATE TABLE "TRIGGER_DATA_DEFECT" 
   (	"TRIGGERID" NUMBER(10,0), 
	"DEFECT_BEGIN" NUMBER, 
	"DEFECT_END" NUMBER, 
	"DEFECT_TYPE" VARCHAR2(20 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Table TRIGGER_DATA_FEATURE
--------------------------------------------------------

  CREATE TABLE "TRIGGER_DATA_FEATURE" 
   (	"TRIGGERID" NUMBER(10,0), 
	"SNR" NUMBER, 
	"AMPLITUDE" NUMBER, 
	"TIME_CENTROID" NUMBER, 
	"TIME_SIGMA" NUMBER, 
	"TEMPORAL_SKEWNESS" NUMBER, 
	"TEMPORAL_KURTOSIS" NUMBER, 
	"FREQ_SIGMA" NUMBER, 
	"TBP" NUMBER, 
	"SKEWNESS" NUMBER, 
	"KURTOSIS" NUMBER, 
	"RAW_SKEWNESS" NUMBER, 
	"RAW_KURTOSIS" NUMBER, 
	"FREQ_CENTROID" NUMBER, 
	"RELATIVE_AMPLITUDE" NUMBER
   ) ;
--------------------------------------------------------
--  DDL for Table TRIGGER_FK_DATA
--------------------------------------------------------

  CREATE TABLE "TRIGGER_FK_DATA" 
   (	"TRIGGERID" NUMBER(10,0), 
	"FK_QUAL" NUMBER, 
	"BACK_AZIMUTH" NUMBER, 
	"VELOCITY" NUMBER, 
	"SX" NUMBER, 
	"SY" NUMBER
   ) ;

   COMMENT ON COLUMN "TRIGGER_FK_DATA"."TRIGGERID" IS 'Foreign key from TRIGGER_RECORD';
   COMMENT ON COLUMN "TRIGGER_FK_DATA"."FK_QUAL" IS 'FK Quality Factor between 0 and 1';
   COMMENT ON COLUMN "TRIGGER_FK_DATA"."BACK_AZIMUTH" IS 'Azimuth in degrees from North';
   COMMENT ON COLUMN "TRIGGER_FK_DATA"."VELOCITY" IS 'Velocity in km/s';
   COMMENT ON COLUMN "TRIGGER_FK_DATA"."SX" IS 'X (North) Slowness in s/km';
   COMMENT ON COLUMN "TRIGGER_FK_DATA"."SY" IS 'Y (East) Slowness in s/km';
  GRANT SELECT ON "TRIGGER_FK_DATA" TO PUBLIC;
--------------------------------------------------------
--  DDL for Package TIME_UTIL
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "TIME_UTIL" is

  -- Author  : DODGE1
  -- Created : 5/11/2020 4:52:49 PM
  -- Purpose :

  function date2epoch(pi_date in date) return number deterministic
    parallel_enable;

  function epoch2ts(p_timestamp number) return timestamp deterministic
    parallel_enable;

end time_util;


/
--------------------------------------------------------
--  DDL for Package Body TIME_UTIL
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "TIME_UTIL" is
  c_base_date      constant date := to_date('1970-01-01', 'YYYY-MM-DD');
  c_seconds_in_day constant number := 24 * 60 * 60;

  function date2epoch(pi_date in date) return number deterministic
    parallel_enable is
    v_unix_timestamp number;
  begin
    v_unix_timestamp := trunc((pi_date - c_base_date) * c_seconds_in_day);
    if (v_unix_timestamp < 0) then
      raise_application_error(-20000,
                              'date2epoch:: date2epoch cannot be negative');
    end if;
    return v_unix_timestamp;
  end date2epoch;
  --==========================================================================================

  function epoch2ts(p_timestamp number) return timestamp deterministic
    parallel_enable is
  begin
    return timestamp '1970-01-01 00:00:00' + numtodsinterval(p_timestamp,
                                                             'SECOND');

  end epoch2ts;
  --==========================================================================================

end time_util;


/
--------------------------------------------------------
--  DDL for Function AZ
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "AZ" ( stla number, stlo number, evla number, evlo number )
return number as language java name 'llnl.gnem.util.EModel.getAzimuth(double, double, double, double)
return double';

/
--------------------------------------------------------
--  DDL for Function AZWGS84
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "AZWGS84" ( stla number, stlo number, evla number, evlo number )
return number as language java name 'llnl.gnem.util.EModel.getAzimuthWGS84(double, double, double, double)
return double';

/
--------------------------------------------------------
--  DDL for Function DELTA
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "DELTA" ( stla number, stlo number, evla number, evlo number )
return number as language java name 'llnl.gnem.util.EModel.getDelta(double, double, double, double)
return double';

/
--------------------------------------------------------
--  DDL for Function DELTAWGS84
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "DELTAWGS84" ( stla number, stlo number, evla number, evlo number )
return number as language java name 'llnl.gnem.util.EModel.getDeltaWGS84(double, double, double, double)
return double';

/
--------------------------------------------------------
--  DDL for Function DIST
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "DIST" ( stla number, stlo number, evla number, evlo number )
return number as language java name 'llnl.gnem.util.EModel.getDistance(double, double, double, double)
return double';

/
--------------------------------------------------------
--  DDL for Function DISTWGS84
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "DISTWGS84" ( stla number, stlo number, evla number, evlo number )
return number as language java name 'llnl.gnem.util.EModel.getDistanceWGS84(double, double, double, double)
return double';

/
--------------------------------------------------------
--  DDL for Function EPOCH2JDATE
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "EPOCH2JDATE" ( v_time in number ) return number as
language java name 'llnl.gnem.util.TimeT.EpochToJdate( double ) return int';

/
--------------------------------------------------------
--  DDL for Function EPOCH2STR
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "EPOCH2STR" ( v_time in number, v_format in varchar2 ) return varchar2
as language java name 'llnl.gnem.util.TimeT.EpochToString2(double,java.lang.String) return java.lang.String';

/
--------------------------------------------------------
--  DDL for Function EPOCH2STR2
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "EPOCH2STR2" ( v_time in number ) return varchar2
as language java name 'llnl.gnem.util.TimeT.EpochToString( double ) return java.lang.String';

/
--------------------------------------------------------
--  DDL for Function JDATE2EPOCH
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "JDATE2EPOCH" ( v_jdate in number ) return number as
language java name 'llnl.gnem.util.TimeT.jdateToEpoch( int ) return double';

/
--------------------------------------------------------
--  DDL for Table TRIGGER_RECORD
--------------------------------------------------------

  CREATE TABLE "TRIGGER_RECORD" 
   (	"TRIGGERID" NUMBER(9,0), 
	"RUNID" NUMBER(9,0), 
	"DETECTORID" NUMBER(9,0), 
	"TIME" NUMBER, 
	"DETECTION_STATISTIC" NUMBER, 
	"PROCESSED" VARCHAR2(1 BYTE), 
	"REJECTED" VARCHAR2(1 BYTE), 
	"SRC_DETECTORID" NUMBER(9,0), 
	"SRC_TRIGGERID" NUMBER(9,0), 
	"SUBSTITUTION_REASON" VARCHAR2(30 BYTE), 
	"SIGNAL_DURATION" NUMBER
   ) ;

   COMMENT ON COLUMN "TRIGGER_RECORD"."TIME" IS 'corrected trigger time';
   COMMENT ON COLUMN "TRIGGER_RECORD"."SIGNAL_DURATION" IS 'The length in seconds from the corrected trigger time to the end of the window. The sum of trigger_offset and signal_duration is the length of temnplates to create from this trigger.';
--------------------------------------------------------
--  DDL for Index ARRAY_DET_PARAMS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ARRAY_DET_PARAMS_PK" ON "ARRAY_DETECTOR_PARAMS" ("DETECTORID") 
  ;
--------------------------------------------------------
--  DDL for Index ARRAY_INFO_UNQ
--------------------------------------------------------

  CREATE UNIQUE INDEX "ARRAY_INFO_UNQ" ON "ARRAY_INFO" ("AGENCY", "NETWORK_CODE", "STATION_CODE", "BEGIN_TIME", "END_TIME") 
  ;
--------------------------------------------------------
--  DDL for Index ARRIVAL_EVID_IDX
--------------------------------------------------------

  CREATE INDEX "ARRIVAL_EVID_IDX" ON "ARRIVAL" ("EVENT_ID") 
  ;
--------------------------------------------------------
--  DDL for Index BULL_DET_SPEC_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "BULL_DET_SPEC_PK" ON "BULLETIN_DETECTOR_SPEC" ("DETECTORID") 
  ;
--------------------------------------------------------
--  DDL for Index CONFIG_GROUP_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "CONFIG_GROUP_PK" ON "CONFIGURATION_GROUP" ("GROUPID") 
  ;
--------------------------------------------------------
--  DDL for Index CONFIG_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "CONFIG_PK" ON "CONFIGURATION" ("CONFIGID") 
  ;
--------------------------------------------------------
--  DDL for Index CONFIG_UNQ
--------------------------------------------------------

  CREATE UNIQUE INDEX "CONFIG_UNQ" ON "CONFIGURATION" ("CONFIG_NAME") 
  ;
--------------------------------------------------------
--  DDL for Index DETECTION_DETECTORID_IDX
--------------------------------------------------------

  CREATE INDEX "DETECTION_DETECTORID_IDX" ON "DETECTION" ("DETECTORID") 
  ;
--------------------------------------------------------
--  DDL for Index DETECTION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DETECTION_PK" ON "DETECTION" ("DETECTIONID") 
  ;
--------------------------------------------------------
--  DDL for Index DETECTION_RUNID_IDX
--------------------------------------------------------

  CREATE INDEX "DETECTION_RUNID_IDX" ON "DETECTION" ("RUNID") 
  ;
--------------------------------------------------------
--  DDL for Index DETECTION_TRIGGERID_IDX
--------------------------------------------------------

  CREATE INDEX "DETECTION_TRIGGERID_IDX" ON "DETECTION" ("TRIGGERID") 
  ;
--------------------------------------------------------
--  DDL for Index DETECTOR_CHANNEL_UNQ
--------------------------------------------------------

  CREATE UNIQUE INDEX "DETECTOR_CHANNEL_UNQ" ON "DETECTOR_CHANNEL" ("DETECTORID", "AGENCY", "NETWORK_CODE", "NET_START_DATE", "STATION_CODE", "CHAN", "LOCATION_CODE") 
  ;
--------------------------------------------------------
--  DDL for Index DETECTOR_CR_RUNID_IDX
--------------------------------------------------------

  CREATE INDEX "DETECTOR_CR_RUNID_IDX" ON "DETECTOR" ("CREATION_RUNID") 
  ;
--------------------------------------------------------
--  DDL for Index DETECTOR_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DETECTOR_PK" ON "DETECTOR" ("DETECTORID") 
  ;
--------------------------------------------------------
--  DDL for Index DETECTOR_STREAMID_IDX
--------------------------------------------------------

  CREATE INDEX "DETECTOR_STREAMID_IDX" ON "DETECTOR" ("STREAMID") 
  ;
--------------------------------------------------------
--  DDL for Index DETECTOR_TEMP_DIM_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DETECTOR_TEMP_DIM_PK" ON "SUBSPACE_TEMPLATE" ("DETECTORID") 
  ;
--------------------------------------------------------
--  DDL for Index DET_CLASS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DET_CLASS_PK" ON "DETECTOR_TRAINING_DATA" ("DETECTORID") 
  ;
--------------------------------------------------------
--  DDL for Index DET_STAT_HIST_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "DET_STAT_HIST_PK" ON "DET_STAT_HISTOGRAM" ("DETECTORID", "RUNID", "BIN_NUM") 
  ;
--------------------------------------------------------
--  DDL for Index DET_STAT_HIST_RID_IDX
--------------------------------------------------------

  CREATE INDEX "DET_STAT_HIST_RID_IDX" ON "DET_STAT_HISTOGRAM" ("RUNID") 
  ;
--------------------------------------------------------
--  DDL for Index DTH_RUNID_DETID_IDX
--------------------------------------------------------

  CREATE INDEX "DTH_RUNID_DETID_IDX" ON "DETECTOR_THRESHOLD_HISTORY" ("RUNID", "DETECTORID") 
  ;
--------------------------------------------------------
--  DDL for Index FRAMEWORK_RUN_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FRAMEWORK_RUN_PK" ON "FRAMEWORK_RUN" ("RUNID") 
  ;
--------------------------------------------------------
--  DDL for Index F_R_CONFIGID_IDX
--------------------------------------------------------

  CREATE INDEX "F_R_CONFIGID_IDX" ON "FRAMEWORK_RUN" ("CONFIGID") 
  ;
--------------------------------------------------------
--  DDL for Index ORIGIN_TIME_IDX
--------------------------------------------------------

  CREATE INDEX "ORIGIN_TIME_IDX" ON "ORIGIN" ("TIME") 
  ;
--------------------------------------------------------
--  DDL for Index PHASE_PICK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PHASE_PICK_PK" ON "PHASE_PICK" ("PICKID") 
  ;
--------------------------------------------------------
--  DDL for Index STALTA_DETECT_PARAMS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "STALTA_DETECT_PARAMS_PK" ON "STALTA_DETECTOR_PARAMS" ("DETECTORID") 
  ;
--------------------------------------------------------
--  DDL for Index STALTA_DETECT_PARAMS_UNQ
--------------------------------------------------------

  CREATE UNIQUE INDEX "STALTA_DETECT_PARAMS_UNQ" ON "STALTA_DETECTOR_PARAMS" ("DETECTORID", "STA_DURATION", "LTA_DURATION", "GAP_DURATION") 
  ;
--------------------------------------------------------
--  DDL for Index STORED_FILTER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "STORED_FILTER_PK" ON "STORED_FILTER" ("FILTER_ID") 
  ;
--------------------------------------------------------
--  DDL for Index STREAM_CONFIGID_IDX
--------------------------------------------------------

  CREATE INDEX "STREAM_CONFIGID_IDX" ON "STREAM" ("CONFIGID") 
  ;
--------------------------------------------------------
--  DDL for Index STREAM_FK_CONFIGID_IDX
--------------------------------------------------------

  CREATE INDEX "STREAM_FK_CONFIGID_IDX" ON "STREAM_FK_PARAM" ("CONFIGID") 
  ;
--------------------------------------------------------
--  DDL for Index STREAM_FK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "STREAM_FK_PK" ON "STREAM_FK_PARAM" ("STREAMID") 
  ;
--------------------------------------------------------
--  DDL for Index STREAM_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "STREAM_PK" ON "STREAM" ("STREAMID") 
  ;
--------------------------------------------------------
--  DDL for Index SUBSPACE_DET_PARAMS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SUBSPACE_DET_PARAMS_PK" ON "SUBSPACE_DETECTOR_PARAMS" ("DETECTORID") 
  ;

--------------------------------------------------------
--  DDL for Index S_C_3_COL_IDX
--------------------------------------------------------

  CREATE INDEX "S_C_3_COL_IDX" ON "STREAM_CHANNEL" ("STREAMID", "STATION_CODE", "CHAN") 
  ;
--------------------------------------------------------
--  DDL for Index S_C_ALL_COL_IDX
--------------------------------------------------------

  CREATE INDEX "S_C_ALL_COL_IDX" ON "STREAM_CHANNEL" ("STREAMID", "AGENCY", "NETWORK_CODE", "NET_START_DATE", "STATION_CODE", "CHAN", "LOCATION_CODE") 
  ;
--------------------------------------------------------
--  DDL for Index S_C_STREAMID_IDX
--------------------------------------------------------

  CREATE INDEX "S_C_STREAMID_IDX" ON "STREAM_CHANNEL" ("STREAMID") 
  ;
--------------------------------------------------------
--  DDL for Index TRIGGER_FK_DATA_AZ_VEL_IDX
--------------------------------------------------------

  CREATE INDEX "TRIGGER_FK_DATA_AZ_VEL_IDX" ON "TRIGGER_FK_DATA" ("BACK_AZIMUTH", "VELOCITY") 
  ;
--------------------------------------------------------
--  DDL for Index TRIGGER_FK_DATA_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TRIGGER_FK_DATA_PK" ON "TRIGGER_FK_DATA" ("TRIGGERID") 
  ;
--------------------------------------------------------
--  DDL for Index TRIGGER_RECORD_DETID_IDX
--------------------------------------------------------

  CREATE INDEX "TRIGGER_RECORD_DETID_IDX" ON "TRIGGER_RECORD" ("DETECTORID") 
  ;
--------------------------------------------------------
--  DDL for Index TRIGGER_RECORD_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TRIGGER_RECORD_PK" ON "TRIGGER_RECORD" ("TRIGGERID") 
  ;
--------------------------------------------------------
--  DDL for Index TRIGGER_RECORD_RUNID_IDX
--------------------------------------------------------

  CREATE INDEX "TRIGGER_RECORD_RUNID_IDX" ON "TRIGGER_RECORD" ("RUNID") 
  ;
--------------------------------------------------------
--  DDL for Index TRIGGER_RECORD_TIME_IDX
--------------------------------------------------------

  CREATE INDEX "TRIGGER_RECORD_TIME_IDX" ON "TRIGGER_RECORD" ("TIME") 
  ;
--------------------------------------------------------
--  DDL for Index TRIG_CLASS_TID_IDX
--------------------------------------------------------

  CREATE INDEX "TRIG_CLASS_TID_IDX" ON "TRIGGER_CLASSIFICATION" ("TRIGGERID") 
  ;
--------------------------------------------------------
--  DDL for Index TRIG_DATA_FEAT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "TRIG_DATA_FEAT_PK" ON "TRIGGER_DATA_FEATURE" ("TRIGGERID") 
  ;
--------------------------------------------------------
--  DDL for Index TRIG_D_D_END_IDX
--------------------------------------------------------

  CREATE INDEX "TRIG_D_D_END_IDX" ON "TRIGGER_DATA_DEFECT" ("DEFECT_END") 
  ;
--------------------------------------------------------
--  DDL for Index TRIG_D_D_TIME_IDX
--------------------------------------------------------

  CREATE INDEX "TRIG_D_D_TIME_IDX" ON "TRIGGER_DATA_DEFECT" ("DEFECT_BEGIN") 
  ;
--------------------------------------------------------
--  DDL for Index TRIG_D_D_TRIGGERID_IDX
--------------------------------------------------------

  CREATE INDEX "TRIG_D_D_TRIGGERID_IDX" ON "TRIGGER_DATA_DEFECT" ("TRIGGERID") 
  ;
--------------------------------------------------------
--  DDL for Function TK1
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "TK1" (v_phase varchar2, v_delt number, v_depth number) return number is

begin

  return(sys.takeoffangle1(v_phase, v_delt, v_depth));
  exception
    when others
      then return -1;

end tk1;


/
--------------------------------------------------------
--  Constraints for Table APP_USER_FILTER
--------------------------------------------------------

  ALTER TABLE "APP_USER_FILTER" MODIFY ("APP_NAME" NOT NULL ENABLE);
  ALTER TABLE "APP_USER_FILTER" MODIFY ("AUTH" NOT NULL ENABLE);
  ALTER TABLE "APP_USER_FILTER" MODIFY ("FILTERID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ARRAY_DETECTOR_PARAMS
--------------------------------------------------------

  ALTER TABLE "ARRAY_DETECTOR_PARAMS" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_DETECTOR_PARAMS" MODIFY ("ARRAY_NAME" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_DETECTOR_PARAMS" MODIFY ("STA_DURATION" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_DETECTOR_PARAMS" MODIFY ("LTA_DURATION" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_DETECTOR_PARAMS" MODIFY ("GAP_DURATION" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_DETECTOR_PARAMS" MODIFY ("ENABLE_SPAWNING" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_DETECTOR_PARAMS" MODIFY ("BACK_AZIMUTH" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_DETECTOR_PARAMS" MODIFY ("VELOCITY" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_DETECTOR_PARAMS" ADD CONSTRAINT "ARRAY_DET_PARAMS_PK" PRIMARY KEY ("DETECTORID")
  USING INDEX  ENABLE;
  ALTER TABLE "ARRAY_DETECTOR_PARAMS" ADD CONSTRAINT "ADP_ENABLE_SPAWNING_CHK" CHECK (ENABLE_SPAWNING in ('y','n')) ENABLE;
--------------------------------------------------------
--  Constraints for Table ARRAY_INFO
--------------------------------------------------------

  ALTER TABLE "ARRAY_INFO" MODIFY ("AGENCY" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("NETWORK_CODE" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("ARRAY_NAME" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("STATION_CODE" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("STANAME" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("BEGIN_TIME" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("ONDATE" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("END_TIME" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("OFFDATE" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("LAT" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("LON" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("ELEV" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("STATYPE" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("DNORTH" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" MODIFY ("DEAST" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO" ADD CONSTRAINT "ARRAY_INFO_UNQ" UNIQUE ("AGENCY", "NETWORK_CODE", "STATION_CODE", "BEGIN_TIME", "END_TIME")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table ARRAY_INFO_IRIS
--------------------------------------------------------

  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("END_TIME" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("OFFDATE" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("LAT" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("LON" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("ELEV" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("STATYPE" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("DNORTH" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("DEAST" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("ARRAY_NAME" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("STATION_CODE" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("STANAME" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("BEGIN_TIME" NOT NULL ENABLE);
  ALTER TABLE "ARRAY_INFO_IRIS" MODIFY ("ONDATE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ARRIVAL
--------------------------------------------------------

  ALTER TABLE "ARRIVAL" MODIFY ("EVENT_ID" NOT NULL ENABLE);
  ALTER TABLE "ARRIVAL" MODIFY ("ARRIVAL_ID" NOT NULL ENABLE);
  ALTER TABLE "ARRIVAL" MODIFY ("CONFIGID" NOT NULL ENABLE);
  ALTER TABLE "ARRIVAL" MODIFY ("STATION_CODE" NOT NULL ENABLE);
  ALTER TABLE "ARRIVAL" MODIFY ("IPHASE" NOT NULL ENABLE);
  ALTER TABLE "ARRIVAL" MODIFY ("TIME" NOT NULL ENABLE);
  ALTER TABLE "ARRIVAL" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "ARRIVAL" MODIFY ("DETECTIONID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table BULLETIN_DETECTOR_SPEC
--------------------------------------------------------

  ALTER TABLE "BULLETIN_DETECTOR_SPEC" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "BULLETIN_DETECTOR_SPEC" MODIFY ("BULLETIN" NOT NULL ENABLE);
  ALTER TABLE "BULLETIN_DETECTOR_SPEC" MODIFY ("TYPE" NOT NULL ENABLE);
  ALTER TABLE "BULLETIN_DETECTOR_SPEC" MODIFY ("ENABLE_SPAWNING" NOT NULL ENABLE);
  ALTER TABLE "BULLETIN_DETECTOR_SPEC" ADD CONSTRAINT "BULL_DET_SPEC_PK" PRIMARY KEY ("DETECTORID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table CONFIGURATION
--------------------------------------------------------

  ALTER TABLE "CONFIGURATION" MODIFY ("CONFIGID" NOT NULL ENABLE);
  ALTER TABLE "CONFIGURATION" MODIFY ("CONFIG_NAME" NOT NULL ENABLE);
  ALTER TABLE "CONFIGURATION" ADD CONSTRAINT "CONFIG_PK" PRIMARY KEY ("CONFIGID")
  USING INDEX  ENABLE;
  ALTER TABLE "CONFIGURATION" ADD CONSTRAINT "CONFIG_UNQ" UNIQUE ("CONFIG_NAME")
  USING INDEX  ENABLE;
  --------------------------------------------------------
--  Constraints for Table CONFIGURATION_GROUP
--------------------------------------------------------

ALTER TABLE "CONFIGURATION_GROUP" MODIFY ("GROUPID" NOT NULL ENABLE);
  ALTER TABLE "CONFIGURATION_GROUP" MODIFY ("CONFIGID" NOT NULL ENABLE);
ALTER TABLE "CONFIGURATION_GROUP" ADD CONSTRAINT "CONFIG_GROUP_PK" PRIMARY KEY ("GROUPID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table DETECTION
--------------------------------------------------------

  ALTER TABLE "DETECTION" MODIFY ("DETECTIONID" NOT NULL ENABLE);
  ALTER TABLE "DETECTION" MODIFY ("RUNID" NOT NULL ENABLE);
  ALTER TABLE "DETECTION" MODIFY ("TRIGGERID" NOT NULL ENABLE);
  ALTER TABLE "DETECTION" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "DETECTION" ADD CONSTRAINT "DETECTION_PK" PRIMARY KEY ("DETECTIONID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table DETECTOR
--------------------------------------------------------

  ALTER TABLE "DETECTOR" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR" MODIFY ("STREAMID" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR" MODIFY ("DETECTORTYPE" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR" MODIFY ("THRESHOLD" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR" MODIFY ("BLACKOUT_SECONDS" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR" MODIFY ("IS_RETIRED" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR" MODIFY ("LDDATE" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR" ADD CONSTRAINT "DETECTOR_PK" PRIMARY KEY ("DETECTORID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table DETECTOR_CHANNEL
--------------------------------------------------------

  ALTER TABLE "DETECTOR_CHANNEL" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_CHANNEL" MODIFY ("STATION_CODE" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_CHANNEL" MODIFY ("CHAN" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_CHANNEL" MODIFY ("POSITION" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_CHANNEL" ADD CONSTRAINT "DETECTOR_CHANNEL_UNQ" UNIQUE ("DETECTORID", "AGENCY", "NETWORK_CODE", "NET_START_DATE", "STATION_CODE", "CHAN", "LOCATION_CODE")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table DETECTOR_THRESHOLD_HISTORY
--------------------------------------------------------

  ALTER TABLE "DETECTOR_THRESHOLD_HISTORY" MODIFY ("RUNID" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_THRESHOLD_HISTORY" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_THRESHOLD_HISTORY" MODIFY ("TIME" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_THRESHOLD_HISTORY" MODIFY ("THRESHOLD" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table DETECTOR_TRAINING_DATA
--------------------------------------------------------

  ALTER TABLE "DETECTOR_TRAINING_DATA" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_TRAINING_DATA" ADD CONSTRAINT "DET_CLASS_PK" PRIMARY KEY ("DETECTORID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table DET_STAT_HISTOGRAM
--------------------------------------------------------

  ALTER TABLE "DET_STAT_HISTOGRAM" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "DET_STAT_HISTOGRAM" MODIFY ("RUNID" NOT NULL ENABLE);
  ALTER TABLE "DET_STAT_HISTOGRAM" MODIFY ("BIN_NUM" NOT NULL ENABLE);
  ALTER TABLE "DET_STAT_HISTOGRAM" ADD CONSTRAINT "DET_STAT_HIST_PK" PRIMARY KEY ("DETECTORID", "RUNID", "BIN_NUM")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table FRAMEWORK_RUN
--------------------------------------------------------

  ALTER TABLE "FRAMEWORK_RUN" MODIFY ("RUNID" NOT NULL ENABLE);
  ALTER TABLE "FRAMEWORK_RUN" MODIFY ("RUN_DATE" NOT NULL ENABLE);
  ALTER TABLE "FRAMEWORK_RUN" MODIFY ("WFDISC_USED" NOT NULL ENABLE);
  ALTER TABLE "FRAMEWORK_RUN" MODIFY ("CONFIGID" NOT NULL ENABLE);
  ALTER TABLE "FRAMEWORK_RUN" ADD CONSTRAINT "FRAMEWORK_RUN_PK" PRIMARY KEY ("RUNID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table GROUP_STATION_DATA
--------------------------------------------------------

    ALTER TABLE "GROUP_STATION_DATA" MODIFY ("STA" NOT NULL ENABLE);
  ALTER TABLE "GROUP_STATION_DATA" MODIFY ("STLA" NOT NULL ENABLE);
  ALTER TABLE "GROUP_STATION_DATA" MODIFY ("STLO" NOT NULL ENABLE);
ALTER TABLE "GROUP_STATION_DATA" MODIFY ("CONFIGID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ORIGIN
--------------------------------------------------------

  ALTER TABLE "ORIGIN" MODIFY ("EVID" NOT NULL ENABLE);
  ALTER TABLE "ORIGIN" MODIFY ("LAT" NOT NULL ENABLE);
  ALTER TABLE "ORIGIN" MODIFY ("LON" NOT NULL ENABLE);
  ALTER TABLE "ORIGIN" MODIFY ("DEPTH" NOT NULL ENABLE);
  ALTER TABLE "ORIGIN" MODIFY ("TIME" NOT NULL ENABLE);
  ALTER TABLE "ORIGIN" MODIFY ("MAG" NOT NULL ENABLE);
  ALTER TABLE "ORIGIN" MODIFY ("AUTH" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PHASE_PICK
--------------------------------------------------------

  ALTER TABLE "PHASE_PICK" MODIFY ("PICKID" NOT NULL ENABLE);
  ALTER TABLE "PHASE_PICK" MODIFY ("CONFIGID" NOT NULL ENABLE);
  ALTER TABLE "PHASE_PICK" MODIFY ("STATION_CODE" NOT NULL ENABLE);
  ALTER TABLE "PHASE_PICK" MODIFY ("CHAN" NOT NULL ENABLE);
  ALTER TABLE "PHASE_PICK" MODIFY ("PHASE" NOT NULL ENABLE);
  ALTER TABLE "PHASE_PICK" MODIFY ("TIME" NOT NULL ENABLE);
  ALTER TABLE "PHASE_PICK" MODIFY ("LDDATE" NOT NULL ENABLE);
ALTER TABLE "PHASE_PICK" ADD CONSTRAINT "PHASE_PICK_PK" PRIMARY KEY ("PICKID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table SITE
--------------------------------------------------------

  ALTER TABLE "SITE" MODIFY ("STA" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("ONDATE" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("OFFDATE" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("LAT" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("LON" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("STANAME" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("STATYPE" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("REFSTA" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("DNORTH" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("DEAST" NOT NULL ENABLE);
  ALTER TABLE "SITE" MODIFY ("LDDATE" NOT NULL ENABLE);
  ALTER TABLE "SITE" ADD CONSTRAINT "SITE_PK" PRIMARY KEY ("STA", "ONDATE") DISABLE;
--------------------------------------------------------
--  Constraints for Table STALTA_DETECTOR_PARAMS
--------------------------------------------------------

  ALTER TABLE "STALTA_DETECTOR_PARAMS" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "STALTA_DETECTOR_PARAMS" MODIFY ("STA_DURATION" NOT NULL ENABLE);
  ALTER TABLE "STALTA_DETECTOR_PARAMS" MODIFY ("LTA_DURATION" NOT NULL ENABLE);
  ALTER TABLE "STALTA_DETECTOR_PARAMS" MODIFY ("GAP_DURATION" NOT NULL ENABLE);
  ALTER TABLE "STALTA_DETECTOR_PARAMS" MODIFY ("ENABLE_SPAWNING" NOT NULL ENABLE);
  ALTER TABLE "STALTA_DETECTOR_PARAMS" ADD CONSTRAINT "STALTA_DETECT_PARAMS_PK" PRIMARY KEY ("DETECTORID")
  USING INDEX  ENABLE;
  ALTER TABLE "STALTA_DETECTOR_PARAMS" ADD CONSTRAINT "STALTA_DETECT_PARAMS_UNQ" UNIQUE ("DETECTORID", "STA_DURATION", "LTA_DURATION", "GAP_DURATION")
  USING INDEX  ENABLE;
  ALTER TABLE "STALTA_DETECTOR_PARAMS" ADD CONSTRAINT "ENABLE_SPAWNING_CHK" CHECK (ENABLE_SPAWNING in ('y','n')) ENABLE;
--------------------------------------------------------
--  Constraints for Table STORED_FILTER
--------------------------------------------------------

  ALTER TABLE "STORED_FILTER" MODIFY ("FILTER_ID" NOT NULL ENABLE);
  ALTER TABLE "STORED_FILTER" MODIFY ("TYPE" NOT NULL ENABLE);
  ALTER TABLE "STORED_FILTER" MODIFY ("CAUSAL" NOT NULL ENABLE);
  ALTER TABLE "STORED_FILTER" MODIFY ("FILTER_ORDER" NOT NULL ENABLE);
  ALTER TABLE "STORED_FILTER" MODIFY ("LOWPASS" NOT NULL ENABLE);
  ALTER TABLE "STORED_FILTER" MODIFY ("HIGHPASS" NOT NULL ENABLE);
  ALTER TABLE "STORED_FILTER" MODIFY ("DESCRIPTION" NOT NULL ENABLE);
  ALTER TABLE "STORED_FILTER" MODIFY ("IMPULSE_RESPONSE" NOT NULL ENABLE);
  ALTER TABLE "STORED_FILTER" MODIFY ("AUTH" NOT NULL ENABLE);
ALTER TABLE "STORED_FILTER" ADD CONSTRAINT "STORED_FILTER_PK" PRIMARY KEY ("FILTER_ID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table STREAM
--------------------------------------------------------

  ALTER TABLE "STREAM" MODIFY ("STREAMID" NOT NULL ENABLE);
  ALTER TABLE "STREAM" MODIFY ("CONFIGID" NOT NULL ENABLE);
  ALTER TABLE "STREAM" MODIFY ("STREAM_NAME" NOT NULL ENABLE);
  ALTER TABLE "STREAM" ADD CONSTRAINT "STREAM_PK" PRIMARY KEY ("STREAMID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table STREAM_CHANNEL
--------------------------------------------------------

  ALTER TABLE "STREAM_CHANNEL" MODIFY ("STREAMID" NOT NULL ENABLE);
  ALTER TABLE "STREAM_CHANNEL" MODIFY ("STATION_CODE" NOT NULL ENABLE);
  ALTER TABLE "STREAM_CHANNEL" MODIFY ("CHAN" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table STREAM_FK_PARAM
--------------------------------------------------------

  ALTER TABLE "STREAM_FK_PARAM" MODIFY ("STREAMID" NOT NULL ENABLE);
  ALTER TABLE "STREAM_FK_PARAM" MODIFY ("CONFIGID" NOT NULL ENABLE);
  ALTER TABLE "STREAM_FK_PARAM" MODIFY ("STREAM_NAME" NOT NULL ENABLE);
  ALTER TABLE "STREAM_FK_PARAM" MODIFY ("FKS_MAX" NOT NULL ENABLE);
  ALTER TABLE "STREAM_FK_PARAM" MODIFY ("MIN_FK_FREQ" NOT NULL ENABLE);
  ALTER TABLE "STREAM_FK_PARAM" MODIFY ("MAX_FK_FREQ" NOT NULL ENABLE);
  ALTER TABLE "STREAM_FK_PARAM" MODIFY ("MIN_FK_QUAL" NOT NULL ENABLE);
  ALTER TABLE "STREAM_FK_PARAM" MODIFY ("FK_WIN_LEN" NOT NULL ENABLE);
  ALTER TABLE "STREAM_FK_PARAM" ADD CONSTRAINT "STREAM_FK_PK" PRIMARY KEY ("STREAMID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table SUBSPACE_DETECTOR_PARAMS
--------------------------------------------------------

  ALTER TABLE "SUBSPACE_DETECTOR_PARAMS" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "SUBSPACE_DETECTOR_PARAMS" MODIFY ("NUM_CHANNELS" NOT NULL ENABLE);
  ALTER TABLE "SUBSPACE_DETECTOR_PARAMS" MODIFY ("RANK" NOT NULL ENABLE);
  ALTER TABLE "SUBSPACE_DETECTOR_PARAMS" ADD CONSTRAINT "SUBSPACE_DET_PARAMS_PK" PRIMARY KEY ("DETECTORID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table SUBSPACE_TEMPLATE
--------------------------------------------------------

  ALTER TABLE "SUBSPACE_TEMPLATE" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "SUBSPACE_TEMPLATE" MODIFY ("TEMPLATE" NOT NULL ENABLE);
  ALTER TABLE "SUBSPACE_TEMPLATE" ADD CONSTRAINT "DETECTOR_TEMP_DIM_PK" PRIMARY KEY ("DETECTORID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table TRIGGER_CLASSIFICATION
--------------------------------------------------------

  ALTER TABLE "TRIGGER_CLASSIFICATION" MODIFY ("TRIGGERID" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_CLASSIFICATION" ADD CONSTRAINT "TRIG_CLASS_PK" PRIMARY KEY ("TRIGGERID")
  USING INDEX "TRIG_CLASS_TID_IDX"  ENABLE;
--------------------------------------------------------
--  Constraints for Table TRIGGER_DATA_DEFECT
--------------------------------------------------------

  ALTER TABLE "TRIGGER_DATA_DEFECT" MODIFY ("DEFECT_END" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_DATA_DEFECT" MODIFY ("DEFECT_TYPE" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_DATA_DEFECT" MODIFY ("TRIGGERID" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_DATA_DEFECT" MODIFY ("DEFECT_BEGIN" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TRIGGER_DATA_FEATURE
--------------------------------------------------------

  ALTER TABLE "TRIGGER_DATA_FEATURE" MODIFY ("TRIGGERID" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_DATA_FEATURE" ADD CONSTRAINT "TRIG_DATA_FEAT_PK" PRIMARY KEY ("TRIGGERID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table TRIGGER_FK_DATA
--------------------------------------------------------

  ALTER TABLE "TRIGGER_FK_DATA" MODIFY ("TRIGGERID" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_FK_DATA" MODIFY ("FK_QUAL" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_FK_DATA" MODIFY ("BACK_AZIMUTH" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_FK_DATA" MODIFY ("VELOCITY" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_FK_DATA" MODIFY ("SX" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_FK_DATA" MODIFY ("SY" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_FK_DATA" ADD CONSTRAINT "TRIGGER_FK_DATA_PK" PRIMARY KEY ("TRIGGERID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table TRIGGER_RECORD
--------------------------------------------------------

  ALTER TABLE "TRIGGER_RECORD" MODIFY ("TRIGGERID" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_RECORD" MODIFY ("RUNID" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_RECORD" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_RECORD" MODIFY ("TIME" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_RECORD" MODIFY ("DETECTION_STATISTIC" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_RECORD" MODIFY ("PROCESSED" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_RECORD" MODIFY ("REJECTED" NOT NULL ENABLE);
  ALTER TABLE "TRIGGER_RECORD" ADD CONSTRAINT "TRIGGER_RECORD_PK" PRIMARY KEY ("TRIGGERID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ARRAY_DETECTOR_PARAMS
--------------------------------------------------------

  ALTER TABLE "ARRAY_DETECTOR_PARAMS" ADD CONSTRAINT "ARRAY_DET_PARAMS_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table BULLETIN_DETECTOR_SPEC
--------------------------------------------------------

  ALTER TABLE "BULLETIN_DETECTOR_SPEC" ADD CONSTRAINT "BULL_DET_SPEC_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CONFIGURATION_GROUP
--------------------------------------------------------

  ALTER TABLE "CONFIGURATION_GROUP" ADD CONSTRAINT "CONFIG_GROUP_CFID_FK" FOREIGN KEY ("CONFIGID")
	  REFERENCES "CONFIGURATION" ("CONFIGID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DETECTION
--------------------------------------------------------

  ALTER TABLE "DETECTION" ADD CONSTRAINT "DETECTION_DETECTORID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "DETECTION" ADD CONSTRAINT "DETECTION_RUNID_FK" FOREIGN KEY ("RUNID")
	  REFERENCES "FRAMEWORK_RUN" ("RUNID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "DETECTION" ADD CONSTRAINT "DETECTION_TRIGGERID_FK" FOREIGN KEY ("TRIGGERID")
	  REFERENCES "TRIGGER_RECORD" ("TRIGGERID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DETECTOR
--------------------------------------------------------

  ALTER TABLE "DETECTOR" ADD CONSTRAINT "DETECTOR_CR_RUNID_FK" FOREIGN KEY ("CREATION_RUNID")
	  REFERENCES "FRAMEWORK_RUN" ("RUNID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "DETECTOR" ADD CONSTRAINT "DETECTOR_STREAMID_FK" FOREIGN KEY ("STREAMID")
	  REFERENCES "STREAM" ("STREAMID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DETECTOR_CHANNEL
--------------------------------------------------------

  ALTER TABLE "DETECTOR_CHANNEL" ADD CONSTRAINT "DETECTOR_CHANNEL_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DETECTOR_THRESHOLD_HISTORY
--------------------------------------------------------

  ALTER TABLE "DETECTOR_THRESHOLD_HISTORY" ADD CONSTRAINT "DTH_DETECTORID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "DETECTOR_THRESHOLD_HISTORY" ADD CONSTRAINT "DTH_RUNID_FK" FOREIGN KEY ("RUNID")
	  REFERENCES "FRAMEWORK_RUN" ("RUNID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DETECTOR_TRAINING_DATA
--------------------------------------------------------

  ALTER TABLE "DETECTOR_TRAINING_DATA" ADD CONSTRAINT "DET_CLASS_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DET_STAT_HISTOGRAM
--------------------------------------------------------

  ALTER TABLE "DET_STAT_HISTOGRAM" ADD CONSTRAINT "D_S_H_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "DET_STAT_HISTOGRAM" ADD CONSTRAINT "D_S_H_RUNID_FK" FOREIGN KEY ("RUNID")
	  REFERENCES "FRAMEWORK_RUN" ("RUNID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FILTER_IN_USE
--------------------------------------------------------

  ALTER TABLE "FILTER_IN_USE" ADD CONSTRAINT "FILTER_IN_USE_FID_FK" FOREIGN KEY ("FILTER_ID")
	  REFERENCES "STORED_FILTER" ("FILTER_ID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FRAMEWORK_RUN
--------------------------------------------------------

  ALTER TABLE "FRAMEWORK_RUN" ADD CONSTRAINT "F_R_CONFIGID_FK" FOREIGN KEY ("CONFIGID")
	  REFERENCES "CONFIGURATION" ("CONFIGID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table GROUP_STATION_DATA
--------------------------------------------------------

  ALTER TABLE "GROUP_STATION_DATA" ADD CONSTRAINT "G_S_D_CONFIGID_FK" FOREIGN KEY ("CONFIGID")
	  REFERENCES "CONFIGURATION" ("CONFIGID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "GROUP_STATION_DATA" ADD CONSTRAINT "G_S_G_GROUPID_FK" FOREIGN KEY ("GROUPID")
	  REFERENCES "CONFIGURATION_GROUP" ("GROUPID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PHASE_PICK
--------------------------------------------------------

  ALTER TABLE "PHASE_PICK" ADD CONSTRAINT "PHASE_PICK_CONFIGID_FK" FOREIGN KEY ("CONFIGID")
	  REFERENCES "CONFIGURATION" ("CONFIGID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "PHASE_PICK" ADD CONSTRAINT "PHASE_PICK_DETID_FK" FOREIGN KEY ("DETECTIONID")
	  REFERENCES "DETECTION" ("DETECTIONID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table STALTA_DETECTOR_PARAMS
--------------------------------------------------------

  ALTER TABLE "STALTA_DETECTOR_PARAMS" ADD CONSTRAINT "STALTA_DETECT_PARAMS_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table STREAM
--------------------------------------------------------

  ALTER TABLE "STREAM" ADD CONSTRAINT "STREAM_CONFIGID_FK" FOREIGN KEY ("CONFIGID")
	  REFERENCES "CONFIGURATION" ("CONFIGID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table STREAM_CHANNEL
--------------------------------------------------------

  ALTER TABLE "STREAM_CHANNEL" ADD CONSTRAINT "STREAM_CHAN_STREAMID_FK" FOREIGN KEY ("STREAMID")
	  REFERENCES "STREAM" ("STREAMID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table STREAM_FK_PARAM
--------------------------------------------------------

  ALTER TABLE "STREAM_FK_PARAM" ADD CONSTRAINT "STREAM_FK_CONFIGID_FK" FOREIGN KEY ("CONFIGID")
	  REFERENCES "CONFIGURATION" ("CONFIGID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "STREAM_FK_PARAM" ADD CONSTRAINT "STREAM_FK_STREAMID_FK" FOREIGN KEY ("STREAMID")
	  REFERENCES "STREAM" ("STREAMID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SUBSPACE_DETECTOR_PARAMS
--------------------------------------------------------

  ALTER TABLE "SUBSPACE_DETECTOR_PARAMS" ADD CONSTRAINT "SUBSPACE_DET_PARAMS_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SUBSPACE_TEMPLATE
--------------------------------------------------------

  ALTER TABLE "SUBSPACE_TEMPLATE" ADD CONSTRAINT "DETECTOR_TEMP_DIM_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TRIGGER_CLASSIFICATION
--------------------------------------------------------

  ALTER TABLE "TRIGGER_CLASSIFICATION" ADD CONSTRAINT "TRIG_CLASS_TRGID_FK" FOREIGN KEY ("TRIGGERID")
	  REFERENCES "TRIGGER_RECORD" ("TRIGGERID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TRIGGER_DATA_DEFECT
--------------------------------------------------------

  ALTER TABLE "TRIGGER_DATA_DEFECT" ADD CONSTRAINT "TRIG_D_D_TRIGID_FK" FOREIGN KEY ("TRIGGERID")
	  REFERENCES "TRIGGER_RECORD" ("TRIGGERID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TRIGGER_DATA_FEATURE
--------------------------------------------------------

  ALTER TABLE "TRIGGER_DATA_FEATURE" ADD CONSTRAINT "TRIG_DATA_FEAT_TRIGID_FK" FOREIGN KEY ("TRIGGERID")
	  REFERENCES "TRIGGER_RECORD" ("TRIGGERID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TRIGGER_FK_DATA
--------------------------------------------------------

  ALTER TABLE "TRIGGER_FK_DATA" ADD CONSTRAINT "TRIGGER_FK_DATA_TRIGID_FK" FOREIGN KEY ("TRIGGERID")
	  REFERENCES "TRIGGER_RECORD" ("TRIGGERID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TRIGGER_RECORD
--------------------------------------------------------

  ALTER TABLE "TRIGGER_RECORD" ADD CONSTRAINT "TRIGGER_RECORD_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "TRIGGER_RECORD" ADD CONSTRAINT "TRIGGER_RECORD_RUNID_FK" FOREIGN KEY ("RUNID")
	  REFERENCES "FRAMEWORK_RUN" ("RUNID") ON DELETE CASCADE ENABLE;
