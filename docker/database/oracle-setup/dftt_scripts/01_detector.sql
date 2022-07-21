--------------------------------------------------------
--  File created - Wednesday-April-06-2022   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence CONFIGID
--------------------------------------------------------

   CREATE SEQUENCE  "CONFIGID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 83 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence DETECTIONID
--------------------------------------------------------

   CREATE SEQUENCE  "DETECTIONID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 36725 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence DETECTORID
--------------------------------------------------------

   CREATE SEQUENCE  "DETECTORID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 7470 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence EVENTID
--------------------------------------------------------

   CREATE SEQUENCE  "EVENTID"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
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

   CREATE SEQUENCE  "TRIGGERID"  MINVALUE 0 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 154349 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
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
   COMMENT ON COLUMN "CONFIGURATION"."SOURCE_TYPE" IS 'one of CssDatabase, DFTTDatabase, FDSN. When null, CssDatabase is assumed.';
   COMMENT ON COLUMN "CONFIGURATION"."SOURCE_IDENTIFIER" IS 'When source_type is CssDatabase this is the name of the continuous_wfdisc table. When source_type is FDSN this is the agency string. WHen source_type is DFTTDatabase this field is ignored.';
   COMMENT ON TABLE "CONFIGURATION"  IS 'This table contains configuration information for StreamProcessors. A configuration specifies a collection of channels that will be processed by the same stream processor ( either wide band or narrow band).';
  GRANT SELECT ON "CONFIGURATION" TO PUBLIC;
--------------------------------------------------------
--  DDL for Table CONFIGURATION_GROUP
--------------------------------------------------------

  CREATE TABLE "CONFIGURATION_GROUP" 
   (	"GROUPID" NUMBER, 
	"CONFIGID" NUMBER(9,0)
   ) ;
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
--------------------------------------------------------
--  DDL for Table DETECTOR_TRAINING_DATA
--------------------------------------------------------

  CREATE TABLE "DETECTOR_TRAINING_DATA" 
   (	"DETECTORID" NUMBER(9,0), 
	"STATUS" VARCHAR2(15 BYTE)
   ) ;
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
--  DDL for Table EVENT
--------------------------------------------------------

  CREATE TABLE "EVENT" 
   (	"EVENTID" NUMBER(10,0), 
	"MIN_TIME" NUMBER, 
	"MAX_TIME" NUMBER
   ) ;

   COMMENT ON COLUMN "EVENT"."EVENTID" IS 'unique identifier';
   COMMENT ON COLUMN "EVENT"."MIN_TIME" IS 'No picks before this time';
   COMMENT ON COLUMN "EVENT"."MAX_TIME" IS 'No picks after this time';
--------------------------------------------------------
--  DDL for Table EVENT_PICK_ASSOC
--------------------------------------------------------

  CREATE TABLE "EVENT_PICK_ASSOC" 
   (	"EVENTID" NUMBER(10,0), 
	"PICKID" NUMBER(10,0)
   ) ;
--------------------------------------------------------
--  DDL for Table EVENT_STATION_TIMES
--------------------------------------------------------

  CREATE TABLE "EVENT_STATION_TIMES" 
   (	"EVID" NUMBER(9,0), 
	"EVLA" NUMBER, 
	"EVLO" NUMBER, 
	"EVDP" NUMBER, 
	"OTIME" NUMBER, 
	"MAG" NUMBER, 
	"AUTH" VARCHAR2(15 BYTE), 
	"CONFIGID" NUMBER(9,0), 
	"STA" VARCHAR2(10 BYTE), 
	"STLA" NUMBER, 
	"STLO" NUMBER, 
	"PTIME" NUMBER, 
	"STIME" NUMBER, 
	"DETECTORID" NUMBER(10,0), 
	"MAG_CORR" NUMBER DEFAULT -999
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
--------------------------------------------------------
--  DDL for Table PHASE_WEIGHT
--------------------------------------------------------

  CREATE TABLE "PHASE_WEIGHT" 
   (	"STA" VARCHAR2(8 BYTE), 
	"PHASE" VARCHAR2(8 BYTE), 
	"TIME_WEIGHT" NUMBER, 
	"AZ_WEIGHT" NUMBER, 
	"SLOW_WEIGHT" NUMBER
   ) ;
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
--  DDL for Table SUBSPACE_DETECTOR_WINDOW
--------------------------------------------------------

  CREATE TABLE "SUBSPACE_DETECTOR_WINDOW" 
   (	"DETECTORID" NUMBER(9,0), 
	"WINDOW_LENGTH" NUMBER
   ) ;
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
  GRANT SELECT ON "TRIGGER_CLASSIFICATION" TO PUBLIC;
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
--  DDL for Table WINDOW_SELECTOR_RESULTS
--------------------------------------------------------

  CREATE TABLE "WINDOW_SELECTOR_RESULTS" 
   (	"RUNID" NUMBER(9,0), 
	"DETECTORID" NUMBER(9,0), 
	"NUM_CHANNELS" NUMBER(9,0), 
	"NUM_DETECTIONS" NUMBER(9,0), 
	"ELAPSED" NUMBER, 
	"THRESHOLD" NUMBER, 
	"WINDOW_START" NUMBER, 
	"WINDOW_END" NUMBER, 
	"REFINED_WINDOW_START" NUMBER, 
	"SNR" NUMBER, 
	"LDDATE" DATE
   ) ;
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
--  DDL for Index BULL_DET_SPEC_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "BULL_DET_SPEC_PK" ON "BULLETIN_DETECTOR_SPEC" ("DETECTORID") 
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
--  DDL for Index DET_FK_DETID_IDX
--------------------------------------------------------

  CREATE INDEX "DET_FK_DETID_IDX" ON "DETECTION_FK_MEASUREMENT" ("DETECTIONID") 
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
--  DDL for Index EVENT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "EVENT_PK" ON "EVENT" ("EVENTID") 
  ;
--------------------------------------------------------
--  DDL for Index EV_PICK_ASSOC_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "EV_PICK_ASSOC_PK" ON "EVENT_PICK_ASSOC" ("EVENTID", "PICKID") 
  ;
--------------------------------------------------------
--  DDL for Index EV_STA_TIMES_CFG_PTIME_IDX
--------------------------------------------------------

  CREATE INDEX "EV_STA_TIMES_CFG_PTIME_IDX" ON "EVENT_STATION_TIMES" ("CONFIGID", "PTIME") 
  ;
--------------------------------------------------------
--  DDL for Index EV_STA_TIMES_EVID_IDX
--------------------------------------------------------

  CREATE INDEX "EV_STA_TIMES_EVID_IDX" ON "EVENT_STATION_TIMES" ("EVID") 
  ;
--------------------------------------------------------
--  DDL for Index EV_STA_TIMES_STA_IDX
--------------------------------------------------------

  CREATE INDEX "EV_STA_TIMES_STA_IDX" ON "EVENT_STATION_TIMES" ("STA") 
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
--  DDL for Index SS_DET_WIN_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SS_DET_WIN_PK" ON "SUBSPACE_DETECTOR_WINDOW" ("DETECTORID") 
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
--  DDL for Function TK1
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "TK1" (v_phase varchar2, v_delt number, v_depth number) return number is

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
  ALTER TABLE "CONFIGURATION" ADD CONSTRAINT "SOURCE_TYPE_CHK" CHECK (source_type in ('CssDatabase','DFTTDatabase','FDSN')) ENABLE;
--------------------------------------------------------
--  Constraints for Table CONFIGURATION_GROUP
--------------------------------------------------------

  ALTER TABLE "CONFIGURATION_GROUP" MODIFY ("CONFIGID" NOT NULL ENABLE);
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
--  Constraints for Table DETECTION_FK_MEASUREMENT
--------------------------------------------------------

  ALTER TABLE "DETECTION_FK_MEASUREMENT" MODIFY ("DETECTIONID" NOT NULL ENABLE);
  ALTER TABLE "DETECTION_FK_MEASUREMENT" MODIFY ("TIME" NOT NULL ENABLE);
  ALTER TABLE "DETECTION_FK_MEASUREMENT" MODIFY ("FK_QUAL" NOT NULL ENABLE);
  ALTER TABLE "DETECTION_FK_MEASUREMENT" MODIFY ("BACK_AZIMUTH" NOT NULL ENABLE);
  ALTER TABLE "DETECTION_FK_MEASUREMENT" MODIFY ("VELOCITY" NOT NULL ENABLE);
  ALTER TABLE "DETECTION_FK_MEASUREMENT" MODIFY ("SX" NOT NULL ENABLE);
  ALTER TABLE "DETECTION_FK_MEASUREMENT" MODIFY ("SY" NOT NULL ENABLE);
  ALTER TABLE "DETECTION_FK_MEASUREMENT" ADD CONSTRAINT "DET_FK_MEAS_PK" PRIMARY KEY ("DETECTIONID")
  USING INDEX "DET_FK_DETID_IDX"  ENABLE;
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
--  Constraints for Table DETECTOR_PHASE_PICK
--------------------------------------------------------

  ALTER TABLE "DETECTOR_PHASE_PICK" MODIFY ("PICKID" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_PHASE_PICK" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_PHASE_PICK" MODIFY ("DETECTIONID" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_PHASE_PICK" MODIFY ("PHASE" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_PHASE_PICK" MODIFY ("TIME" NOT NULL ENABLE);
  ALTER TABLE "DETECTOR_PHASE_PICK" MODIFY ("LDDATE" NOT NULL ENABLE);
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
--  Constraints for Table EVENT
--------------------------------------------------------

  ALTER TABLE "EVENT" MODIFY ("EVENTID" NOT NULL ENABLE);
  ALTER TABLE "EVENT" ADD CONSTRAINT "EVENT_PK" PRIMARY KEY ("EVENTID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table EVENT_PICK_ASSOC
--------------------------------------------------------

  ALTER TABLE "EVENT_PICK_ASSOC" MODIFY ("EVENTID" NOT NULL ENABLE);
  ALTER TABLE "EVENT_PICK_ASSOC" MODIFY ("PICKID" NOT NULL ENABLE);
  ALTER TABLE "EVENT_PICK_ASSOC" ADD CONSTRAINT "EV_PICK_ASSOC_PK" PRIMARY KEY ("EVENTID", "PICKID")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table EVENT_STATION_TIMES
--------------------------------------------------------

  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("EVID" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("EVLA" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("EVLO" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("EVDP" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("OTIME" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("MAG" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("AUTH" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("CONFIGID" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("STA" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("STLA" NOT NULL ENABLE);
  ALTER TABLE "EVENT_STATION_TIMES" MODIFY ("STLO" NOT NULL ENABLE);
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

  ALTER TABLE "GROUP_STATION_DATA" MODIFY ("CONFIGID" NOT NULL ENABLE);
  ALTER TABLE "GROUP_STATION_DATA" MODIFY ("STA" NOT NULL ENABLE);
  ALTER TABLE "GROUP_STATION_DATA" MODIFY ("STLA" NOT NULL ENABLE);
  ALTER TABLE "GROUP_STATION_DATA" MODIFY ("STLO" NOT NULL ENABLE);
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
--------------------------------------------------------
--  Constraints for Table PHASE_WEIGHT
--------------------------------------------------------

  ALTER TABLE "PHASE_WEIGHT" MODIFY ("STA" NOT NULL ENABLE);
  ALTER TABLE "PHASE_WEIGHT" MODIFY ("PHASE" NOT NULL ENABLE);
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
--  Constraints for Table SUBSPACE_DETECTOR_WINDOW
--------------------------------------------------------

  ALTER TABLE "SUBSPACE_DETECTOR_WINDOW" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "SUBSPACE_DETECTOR_WINDOW" ADD CONSTRAINT "SS_DET_WIN_PK" PRIMARY KEY ("DETECTORID")
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
--  Constraints for Table WINDOW_SELECTOR_RESULTS
--------------------------------------------------------

  ALTER TABLE "WINDOW_SELECTOR_RESULTS" MODIFY ("RUNID" NOT NULL ENABLE);
  ALTER TABLE "WINDOW_SELECTOR_RESULTS" MODIFY ("DETECTORID" NOT NULL ENABLE);
  ALTER TABLE "WINDOW_SELECTOR_RESULTS" MODIFY ("NUM_CHANNELS" NOT NULL ENABLE);
  ALTER TABLE "WINDOW_SELECTOR_RESULTS" MODIFY ("NUM_DETECTIONS" NOT NULL ENABLE);
  ALTER TABLE "WINDOW_SELECTOR_RESULTS" MODIFY ("ELAPSED" NOT NULL ENABLE);
  ALTER TABLE "WINDOW_SELECTOR_RESULTS" MODIFY ("THRESHOLD" NOT NULL ENABLE);
  ALTER TABLE "WINDOW_SELECTOR_RESULTS" MODIFY ("WINDOW_START" NOT NULL ENABLE);
  ALTER TABLE "WINDOW_SELECTOR_RESULTS" MODIFY ("WINDOW_END" NOT NULL ENABLE);
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
--  Ref Constraints for Table DETECTION
--------------------------------------------------------

  ALTER TABLE "DETECTION" ADD CONSTRAINT "DETECTION_DETECTORID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "DETECTION" ADD CONSTRAINT "DETECTION_RUNID_FK" FOREIGN KEY ("RUNID")
	  REFERENCES "FRAMEWORK_RUN" ("RUNID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "DETECTION" ADD CONSTRAINT "DETECTION_TRIGGERID_FK" FOREIGN KEY ("TRIGGERID")
	  REFERENCES "TRIGGER_RECORD" ("TRIGGERID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DETECTION_FK_MEASUREMENT
--------------------------------------------------------

  ALTER TABLE "DETECTION_FK_MEASUREMENT" ADD CONSTRAINT "DET_FK_MEAS_DETID_FK" FOREIGN KEY ("DETECTIONID")
	  REFERENCES "DETECTION" ("DETECTIONID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DETECTOR
--------------------------------------------------------

  ALTER TABLE "DETECTOR" ADD CONSTRAINT "DETECTOR_RUNID_FK" FOREIGN KEY ("CREATION_RUNID")
	  REFERENCES "FRAMEWORK_RUN" ("RUNID") ON DELETE SET NULL ENABLE;
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
--  Ref Constraints for Table EVENT_PICK_ASSOC
--------------------------------------------------------

  ALTER TABLE "EVENT_PICK_ASSOC" ADD CONSTRAINT "EV_PICK_ASSOC_EVID_FK" FOREIGN KEY ("EVENTID")
	  REFERENCES "EVENT" ("EVENTID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FRAMEWORK_RUN
--------------------------------------------------------

  ALTER TABLE "FRAMEWORK_RUN" ADD CONSTRAINT "FRAMEWORK_RUN_CFGID_FK" FOREIGN KEY ("CONFIGID")
	  REFERENCES "CONFIGURATION" ("CONFIGID") ON DELETE CASCADE ENABLE;
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
--  Ref Constraints for Table SUBSPACE_DETECTOR_WINDOW
--------------------------------------------------------

  ALTER TABLE "SUBSPACE_DETECTOR_WINDOW" ADD CONSTRAINT "SS_DET_WIN_DETID_FK" FOREIGN KEY ("DETECTORID")
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
--  Ref Constraints for Table TRIGGER_DATA_FEATURE
--------------------------------------------------------

  ALTER TABLE "TRIGGER_DATA_FEATURE" ADD CONSTRAINT "TRIG_DATA_FEAT_TRGID_FK" FOREIGN KEY ("TRIGGERID")
	  REFERENCES "TRIGGER_RECORD" ("TRIGGERID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TRIGGER_FK_DATA
--------------------------------------------------------

  ALTER TABLE "TRIGGER_FK_DATA" ADD CONSTRAINT "TRIGGER_FK_DATA_TRGID_FK" FOREIGN KEY ("TRIGGERID")
	  REFERENCES "TRIGGER_RECORD" ("TRIGGERID") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TRIGGER_RECORD
--------------------------------------------------------

  ALTER TABLE "TRIGGER_RECORD" ADD CONSTRAINT "TRIG_REC_DETID_FK" FOREIGN KEY ("DETECTORID")
	  REFERENCES "DETECTOR" ("DETECTORID") ON DELETE CASCADE ENABLE;
  ALTER TABLE "TRIGGER_RECORD" ADD CONSTRAINT "TRIG_REC_RUNID_FK" FOREIGN KEY ("RUNID")
	  REFERENCES "FRAMEWORK_RUN" ("RUNID") ON DELETE CASCADE ENABLE;
