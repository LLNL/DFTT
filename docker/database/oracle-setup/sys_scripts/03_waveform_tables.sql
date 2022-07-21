ALTER SESSION SET CURRENT_SCHEMA = DFTT; 
--------------------------------------------------------
--  File created - Tuesday-April-12-2022   
--------------------------------------------------------

CREATE TABLE "DFTT"."FILTER_IN_USE"
(
  filter_id NUMBER(9) not null
);

--------------------------------------------------------
--  DDL for Sequence ADSL_CHANNEL_EPOCH_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "DFTT"."ADSL_CHANNEL_EPOCH_ID_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 7358331 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
  GRANT SELECT ON "DFTT"."ADSL_CHANNEL_EPOCH_ID_SEQ" TO "DFTT";
--------------------------------------------------------
--  DDL for Sequence ADSL_CHANNEL_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "DFTT"."ADSL_CHANNEL_ID_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 3501818 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
  GRANT SELECT ON "DFTT"."ADSL_CHANNEL_ID_SEQ" TO "DFTT";
--------------------------------------------------------
--  DDL for Sequence ADSL_EPOCH_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "DFTT"."ADSL_EPOCH_ID_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 2252117 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
  GRANT SELECT ON "DFTT"."ADSL_EPOCH_ID_SEQ" TO "DFTT";
--------------------------------------------------------
--  DDL for Sequence ADSL_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "DFTT"."ADSL_ID_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 867124 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
  GRANT SELECT ON "DFTT"."ADSL_ID_SEQ" TO "DFTT";
--------------------------------------------------------
--  DDL for Sequence SOURCE_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "DFTT"."SOURCE_ID_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 71 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence STATION_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "DFTT"."STATION_ID_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 768829 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
  GRANT SELECT ON "DFTT"."STATION_ID_SEQ" TO "DFTT";
--------------------------------------------------------
--  DDL for Sequence NETWORK_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "DFTT"."NETWORK_ID_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 5972 NOCACHE  NOORDER  NOCYCLE  NOKEEP  GLOBAL ;
  GRANT SELECT ON "DFTT"."NETWORK_ID_SEQ" TO "DFTT";
--------------------------------------------------------
--  DDL for Table CONTINUOUS_WAVEFORM
--------------------------------------------------------

  CREATE TABLE "DFTT"."CONTINUOUS_WAVEFORM" 
   (	"WAVEFORM_ID" NUMBER(10,0), 
	"ADSL_CHANNEL_ID" NUMBER(10,0), 
	"BEGIN_TIME" NUMBER, 
	"END_TIME" NUMBER, 
	"NSAMP" NUMBER(38,0), 
	"SAMPRATE" NUMBER, 
	"DATA_TYPE" VARCHAR2(10 BYTE), 
	"FOFF" NUMBER, 
	"DIR" VARCHAR2(200 BYTE), 
	"DFILE" VARCHAR2(200 BYTE), 
	"LDDATE" DATE
   ) ;

   COMMENT ON COLUMN "DFTT"."CONTINUOUS_WAVEFORM"."LDDATE" IS 'DAte this row was loaded into the database';
  GRANT SELECT ON "DFTT"."CONTINUOUS_WAVEFORM" TO PUBLIC;
  GRANT DELETE ON "DFTT"."CONTINUOUS_WAVEFORM" TO "DFTT";
  GRANT INSERT ON "DFTT"."CONTINUOUS_WAVEFORM" TO "DFTT";
  GRANT SELECT ON "DFTT"."CONTINUOUS_WAVEFORM" TO "DFTT";
  GRANT UPDATE ON "DFTT"."CONTINUOUS_WAVEFORM" TO "DFTT";
--------------------------------------------------------
--  DDL for Table CONTINUOUS_WAVEFORM_CAL_FACTOR
--------------------------------------------------------

  CREATE TABLE "DFTT"."CONTINUOUS_WAVEFORM_CAL_FACTOR" 
   (	"WAVEFORM_ID" NUMBER(10,0), 
	"CALIB" NUMBER, 
	"CALPER" NUMBER
   ) ;
  GRANT SELECT ON "DFTT"."CONTINUOUS_WAVEFORM_CAL_FACTOR" TO PUBLIC;
  GRANT DELETE ON "DFTT"."CONTINUOUS_WAVEFORM_CAL_FACTOR" TO "DFTT";
  GRANT INSERT ON "DFTT"."CONTINUOUS_WAVEFORM_CAL_FACTOR" TO "DFTT";
  GRANT SELECT ON "DFTT"."CONTINUOUS_WAVEFORM_CAL_FACTOR" TO "DFTT";
  GRANT UPDATE ON "DFTT"."CONTINUOUS_WAVEFORM_CAL_FACTOR" TO "DFTT";
--------------------------------------------------------
--  DDL for Table ADSL_CHANNEL
--------------------------------------------------------

  CREATE TABLE "DFTT"."ADSL_CHANNEL" 
   (	"ADSL_CHANNEL_ID" NUMBER(10,0), 
	"ADSL_ID" NUMBER(10,0), 
	"BAND" VARCHAR2(10 BYTE), 
	"INSTRUMENT_CODE" VARCHAR2(10 BYTE), 
	"ORIENTATION_CODE" VARCHAR2(10 BYTE), 
	"DESCRIPTION" VARCHAR2(100 BYTE), 
	"CHAN" VARCHAR2(8 BYTE), 
	"ARCHIVE_ROW" VARCHAR2(1 BYTE), 
	"LDDATE" DATE
   ) ;

   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL"."ADSL_CHANNEL_ID" IS 'Surrogate primary key of this table';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL"."ADSL_ID" IS 'Foreign key from the ADSL_LOCATOR table. Ties this to a station and location code';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL"."BAND" IS 'The channel band, e.g. Broad band, short period, etc.';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL"."INSTRUMENT_CODE" IS 'The channel instrument code, e.g. high gain, low gain, etc.';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL"."ORIENTATION_CODE" IS 'The channel orientation, e.g. North, East, etc.';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL"."DESCRIPTION" IS 'An optional description';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL"."CHAN" IS 'The (usually 3-character) channel code';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL"."ARCHIVE_ROW" IS 'Identifies whether this row has been superceded by the source. If description changes of channel not reported then set to "y"';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL"."LDDATE" IS 'The date this row was loaded into the database.';
   COMMENT ON TABLE "DFTT"."ADSL_CHANNEL"  IS 'Describes the channels associated with an ADSL_LOCATOR';
  GRANT SELECT ON "DFTT"."ADSL_CHANNEL" TO "DFTT";
  GRANT DELETE ON "DFTT"."ADSL_CHANNEL" TO "DFTT";
  GRANT INSERT ON "DFTT"."ADSL_CHANNEL" TO "DFTT";
  GRANT SELECT ON "DFTT"."ADSL_CHANNEL" TO "DFTT";
  GRANT UPDATE ON "DFTT"."ADSL_CHANNEL" TO "DFTT";
--------------------------------------------------------
--  DDL for Table ADSL_CHANNEL_EPOCH
--------------------------------------------------------

  CREATE TABLE "DFTT"."ADSL_CHANNEL_EPOCH" 
   (	"ADSL_CHANNEL_EPOCH_ID" NUMBER(10,0), 
	"ADSL_CHANNEL_ID" NUMBER(10,0), 
	"ADSL_EPOCH_ID" NUMBER(10,0), 
	"DEPTH" NUMBER, 
	"AZIMUTH" NUMBER, 
	"DIP" NUMBER, 
	"SAMPRATE" NUMBER, 
	"INSTRUMENT_DESCRIPTION" VARCHAR2(200 BYTE), 
	"INSTRUMENT_MFG" VARCHAR2(100 BYTE), 
	"INSTRUMENT_SN" VARCHAR2(50 BYTE), 
	"ARCHIVE_ROW" VARCHAR2(1 BYTE), 
	"LDDATE" DATE
   ) ;

   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL_EPOCH"."ADSL_CHANNEL_EPOCH_ID" IS 'Primary key of this table';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL_EPOCH"."ADSL_CHANNEL_ID" IS 'Foreign key from the ADSL_CHANNEL table. Identifies the channel for which this epoch applies.';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL_EPOCH"."ADSL_EPOCH_ID" IS 'Foreign key from the ADSL_EPOCH table. Identifies the channel epoch';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL_EPOCH"."DEPTH" IS 'Depth of burial of this channel';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL_EPOCH"."AZIMUTH" IS 'Azimuth w.r.t. North of this channel';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL_EPOCH"."DIP" IS 'The dip in degrees of this channel';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL_EPOCH"."SAMPRATE" IS 'The sample rate of the channel in Hz.';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL_EPOCH"."ARCHIVE_ROW" IS 'Identifies whether this row has been superceded by the source. As long as the request to source returns the same (depth,azimuth ... instrument_sn) for the adsl_channel_id and adsl_epoch_id this column is set to "n", otherwise "y"
';
   COMMENT ON COLUMN "DFTT"."ADSL_CHANNEL_EPOCH"."LDDATE" IS 'Date this row was loaded into the database';
   COMMENT ON TABLE "DFTT"."ADSL_CHANNEL_EPOCH"  IS 'This table contains all the epochs supplied for each ADSL_CHANNEL. Epochs in this table may be overlapping and duplicative.';
  GRANT SELECT ON "DFTT"."ADSL_CHANNEL_EPOCH" TO PUBLIC;
  GRANT DELETE ON "DFTT"."ADSL_CHANNEL_EPOCH" TO "DFTT";
  GRANT INSERT ON "DFTT"."ADSL_CHANNEL_EPOCH" TO "DFTT";
  GRANT SELECT ON "DFTT"."ADSL_CHANNEL_EPOCH" TO "DFTT";
  GRANT UPDATE ON "DFTT"."ADSL_CHANNEL_EPOCH" TO "DFTT";
--------------------------------------------------------
--  DDL for Table ADSL_EPOCH
--------------------------------------------------------

  CREATE TABLE "DFTT"."ADSL_EPOCH" 
   (	"ADSL_EPOCH_ID" NUMBER(10,0), 
	"ADSL_ID" NUMBER(10,0), 
	"BEGIN_TIME" NUMBER, 
	"END_TIME" NUMBER, 
	"LAT" NUMBER, 
	"LON" NUMBER, 
	"ELEV" NUMBER, 
	"EPOCH_VERSION" NUMBER(8,0), 
	"POSITION_TYPE" VARCHAR2(10 BYTE), 
	"ARCHIVE_ROW" VARCHAR2(1 BYTE), 
	"LDDATE" DATE
   ) ;

   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."ADSL_EPOCH_ID" IS 'The primary key of this relation';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."ADSL_ID" IS 'Foreign key from the ADSL_LOCATOR table';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."BEGIN_TIME" IS 'epoch time of this epoch start';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."END_TIME" IS 'epoch time of this epoch end';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."LAT" IS 'the latitude of the station';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."LON" IS 'the longitude of the station';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."ELEV" IS 'the elevation of the station';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."EPOCH_VERSION" IS 'tie breaker for cases where a source has specified more than 1 row for a ADSL_ID,BEGIN_TIME,END_TIME';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."POSITION_TYPE" IS 'FDSN allows position to be specified at the station level or at the channel level. In addition, many sources only supply position to the ADS level (no location code)';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."ARCHIVE_ROW" IS 'Identifies whether this row has been superceded by the source. As long as the request to source returns the (begin_time, end_time,lat,lon,elev) this column is set to "n", otherwise "y"';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH"."LDDATE" IS 'Date this row was loaded';
   COMMENT ON TABLE "DFTT"."ADSL_EPOCH"  IS 'contains the epochs for a particular location. Until I understand how to interoperate with FDSN these data live off to the side.';
  GRANT SELECT ON "DFTT"."ADSL_EPOCH" TO PUBLIC;
  GRANT DELETE ON "DFTT"."ADSL_EPOCH" TO "DFTT";
  GRANT INSERT ON "DFTT"."ADSL_EPOCH" TO "DFTT";
  GRANT SELECT ON "DFTT"."ADSL_EPOCH" TO "DFTT";
  GRANT UPDATE ON "DFTT"."ADSL_EPOCH" TO "DFTT";
--------------------------------------------------------
--  DDL for Table ADSL_EPOCH_ERROR
--------------------------------------------------------

  CREATE TABLE "DFTT"."ADSL_EPOCH_ERROR" 
   (	"ADSL_EPOCH_ID" NUMBER(10,0), 
	"ERROR_TYPE" VARCHAR2(20 BYTE), 
	"DESCRIPTION" VARCHAR2(300 BYTE), 
	"AUTH" VARCHAR2(20 BYTE), 
	"LDDATE" DATE
   ) ;

   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH_ERROR"."ADSL_EPOCH_ID" IS 'Foreign key from the ADSL_EPOCH table';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH_ERROR"."ERROR_TYPE" IS 'Currently, one of POSITION,ELEVATION';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH_ERROR"."DESCRIPTION" IS 'Text string describing the error';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH_ERROR"."AUTH" IS 'database user that created the row';
   COMMENT ON COLUMN "DFTT"."ADSL_EPOCH_ERROR"."LDDATE" IS 'date the row was loaded into the database.';
   COMMENT ON TABLE "DFTT"."ADSL_EPOCH_ERROR"  IS 'Records station/channel epochs that have a known error or condition that renders them unsuitable for normal use.';
  GRANT DELETE ON "DFTT"."ADSL_EPOCH_ERROR" TO "DFTT";
  GRANT INSERT ON "DFTT"."ADSL_EPOCH_ERROR" TO "DFTT";
  GRANT SELECT ON "DFTT"."ADSL_EPOCH_ERROR" TO "DFTT";
  GRANT UPDATE ON "DFTT"."ADSL_EPOCH_ERROR" TO "DFTT";
--------------------------------------------------------
--  DDL for Table ADSL_LOCATOR
--------------------------------------------------------

  CREATE TABLE "DFTT"."ADSL_LOCATOR" 
   (	"ADSL_ID" NUMBER(10,0), 
	"STATION_ID" NUMBER(10,0), 
	"LOCATION_CODE" VARCHAR2(2 BYTE)
   ) ;

   COMMENT ON COLUMN "DFTT"."ADSL_LOCATOR"."ADSL_ID" IS 'primary key of the relation';
   COMMENT ON COLUMN "DFTT"."ADSL_LOCATOR"."STATION_ID" IS 'child key from the STATION table';
   COMMENT ON COLUMN "DFTT"."ADSL_LOCATOR"."LOCATION_CODE" IS '2-character location code string';
   COMMENT ON TABLE "DFTT"."ADSL_LOCATOR"  IS 'links the STATION table to ADSL_EPOCH and maybe later to STREAM';
  GRANT DELETE ON "DFTT"."ADSL_LOCATOR" TO "DFTT";
  GRANT INSERT ON "DFTT"."ADSL_LOCATOR" TO "DFTT";
  GRANT SELECT ON "DFTT"."ADSL_LOCATOR" TO "DFTT";
  GRANT UPDATE ON "DFTT"."ADSL_LOCATOR" TO "DFTT";
--------------------------------------------------------
--  DDL for Table ADSL_RESPONSE
--------------------------------------------------------

  CREATE TABLE "DFTT"."ADSL_RESPONSE" 
   (	"RESPONSE_ID" NUMBER(10,0), 
	"ADSL_CHANNEL_ID" NUMBER(10,0), 
	"BEGIN_TIME" NUMBER, 
	"END_TIME" NUMBER, 
	"RSPTYPE" VARCHAR2(10 BYTE), 
	"DIR" VARCHAR2(300 BYTE), 
	"DFILE" VARCHAR2(200 BYTE), 
	"ARCHIVE_ROW" VARCHAR2(1 BYTE), 
	"LDDATE" DATE
   ) ;

   COMMENT ON COLUMN "DFTT"."ADSL_RESPONSE"."RESPONSE_ID" IS 'The primary key of this table';
   COMMENT ON COLUMN "DFTT"."ADSL_RESPONSE"."ADSL_CHANNEL_ID" IS 'Foreign key from the ADSL_CHANNEL table. Identifies the channel to which this response applies.';
   COMMENT ON COLUMN "DFTT"."ADSL_RESPONSE"."BEGIN_TIME" IS 'The time this response becomes effective';
   COMMENT ON COLUMN "DFTT"."ADSL_RESPONSE"."END_TIME" IS 'The time at which this response no longer applies';
   COMMENT ON COLUMN "DFTT"."ADSL_RESPONSE"."RSPTYPE" IS 'One of: evresp,sacpzf,pazfir,fap,paz,pazfap';
   COMMENT ON COLUMN "DFTT"."ADSL_RESPONSE"."DIR" IS 'The directory in which the response file is found';
   COMMENT ON COLUMN "DFTT"."ADSL_RESPONSE"."DFILE" IS 'The name of the response file.';
   COMMENT ON COLUMN "DFTT"."ADSL_RESPONSE"."ARCHIVE_ROW" IS 'Identifies whether this row has been superceded by the source. As long as the request to source returns the same (content) for the adsl_channel_id  this column is set to "n", otherwise "y"';
   COMMENT ON COLUMN "DFTT"."ADSL_RESPONSE"."LDDATE" IS 'Date this row was loaded into the database';
  GRANT SELECT ON "DFTT"."ADSL_RESPONSE" TO PUBLIC;
  GRANT DELETE ON "DFTT"."ADSL_RESPONSE" TO "DFTT";
  GRANT INSERT ON "DFTT"."ADSL_RESPONSE" TO "DFTT";
  GRANT SELECT ON "DFTT"."ADSL_RESPONSE" TO "DFTT";
  GRANT UPDATE ON "DFTT"."ADSL_RESPONSE" TO "DFTT";
--------------------------------------------------------
--  DDL for Table ADSL_RESPONSE_SENSORX_MAP
--------------------------------------------------------

  CREATE TABLE "DFTT"."ADSL_RESPONSE_SENSORX_MAP" 
   (	"RESPONSE_ID" NUMBER(10,0), 
	"STA" VARCHAR2(12 BYTE), 
	"CHAN" VARCHAR2(8 BYTE), 
	"TIME" NUMBER, 
	"LDDATE" DATE
   ) ;
--------------------------------------------------------
--  DDL for Table ADSL_STATION_GROUP
--------------------------------------------------------

  CREATE TABLE "DFTT"."ADSL_STATION_GROUP" 
   (	"GROUP_ID" NUMBER(10,0), 
	"GROUP_SIZE" NUMBER(10,0), 
	"MAX_DEVIATION" NUMBER
   ) ;
  GRANT DELETE ON "DFTT"."ADSL_STATION_GROUP" TO "DFTT";
  GRANT INSERT ON "DFTT"."ADSL_STATION_GROUP" TO "DFTT";
  GRANT SELECT ON "DFTT"."ADSL_STATION_GROUP" TO "DFTT";
  GRANT UPDATE ON "DFTT"."ADSL_STATION_GROUP" TO "DFTT";
--------------------------------------------------------
--  DDL for Table ADSL_STATION_GROUP_MEMBER
--------------------------------------------------------

  CREATE TABLE "DFTT"."ADSL_STATION_GROUP_MEMBER" 
   (	"GROUP_ID" NUMBER(10,0), 
	"STATION_ID" NUMBER(10,0), 
	"ARRAY_ID" NUMBER(10,0), 
	"ARRAY_ELEMENT_ID" NUMBER(10,0), 
	"PRIME_MEMBER" VARCHAR2(1 BYTE)
   ) ;
  GRANT DELETE ON "DFTT"."ADSL_STATION_GROUP_MEMBER" TO "DFTT";
  GRANT INSERT ON "DFTT"."ADSL_STATION_GROUP_MEMBER" TO "DFTT";
  GRANT SELECT ON "DFTT"."ADSL_STATION_GROUP_MEMBER" TO "DFTT";
  GRANT UPDATE ON "DFTT"."ADSL_STATION_GROUP_MEMBER" TO "DFTT";
--------------------------------------------------------
--  DDL for Table NETWORK
--------------------------------------------------------

  CREATE TABLE "DFTT"."NETWORK" 
   (	"NETWORK_ID" NUMBER(10,0), 
	"NETWORK_SOURCE_ID" NUMBER(10,0), 
	"NETWORK_CODE" VARCHAR2(20 BYTE), 
	"START_DATE" NUMBER(10,0), 
	"DESCRIPTION" VARCHAR2(1000 BYTE), 
	"BEGIN_TIME" NUMBER, 
	"END_TIME" NUMBER, 
	"NETWORK_TYPE" VARCHAR2(20 BYTE)
   ) ;

   COMMENT ON COLUMN "DFTT"."NETWORK"."NETWORK_ID" IS 'Unique identifier for this network';
   COMMENT ON COLUMN "DFTT"."NETWORK"."NETWORK_SOURCE_ID" IS 'Foreign key from the SOURCE table';
   COMMENT ON COLUMN "DFTT"."NETWORK"."NETWORK_CODE" IS 'The (usually 2-character) string that identifies this network';
   COMMENT ON COLUMN "DFTT"."NETWORK"."START_DATE" IS 'The julian date of the network start';
   COMMENT ON COLUMN "DFTT"."NETWORK"."DESCRIPTION" IS 'Where available a more verbose description of the network';
   COMMENT ON COLUMN "DFTT"."NETWORK"."BEGIN_TIME" IS 'The epoch time marking the beginning of this network.';
   COMMENT ON COLUMN "DFTT"."NETWORK"."END_TIME" IS 'The epoch time marking the end of this network.';
   COMMENT ON COLUMN "DFTT"."NETWORK"."NETWORK_TYPE" IS 'Tempory, Permanent, etc.';
  GRANT SELECT ON "DFTT"."NETWORK" TO PUBLIC;
  GRANT DELETE ON "DFTT"."NETWORK" TO "DFTT";
  GRANT INSERT ON "DFTT"."NETWORK" TO "DFTT";
  GRANT SELECT ON "DFTT"."NETWORK" TO "DFTT";
  GRANT UPDATE ON "DFTT"."NETWORK" TO "DFTT";
--------------------------------------------------------
--  DDL for Table SOURCE
--------------------------------------------------------

  CREATE TABLE "DFTT"."SOURCE" 
   (	"SOURCE_ID" NUMBER(10,0), 
	"SOURCE_CODE" VARCHAR2(15 BYTE), 
	"DESCRIPTION" VARCHAR2(200 BYTE), 
	"LLNL_POC" VARCHAR2(50 BYTE), 
	"DISTRIBUTION" NUMBER, 
	"PUBLICATION" NUMBER, 
	"MODIFY_USER" VARCHAR2(50 BYTE), 
	"MODIFY_DTTN" DATE, 
	"COMMENTS" VARCHAR2(100 BYTE), 
	"RANK" NUMBER, 
	"LOCATION" VARCHAR2(200 BYTE), 
	"WEBSITE" VARCHAR2(200 BYTE), 
	"LAST_UPDATE" DATE DEFAULT sysdate
   ) ;

   COMMENT ON COLUMN "DFTT"."SOURCE"."RANK" IS 'Used in assigning an imputed network to a station supplied without network information, If more than one source have supplied the same network, then the rank is used to break the tie.';
   COMMENT ON TABLE "DFTT"."SOURCE"  IS 'Contains a code and description for a source of seismic data. Sources may be primary (e.g. data center), secondary (e.g. ISC),
or may just represent a collection of data input to DFTT that could not otherwise be accounted for (e.g. LLNL).
All stations (and hence anything that depends on STATION) must have a source. Also, each row in SOURCE
should have a row in the SOURCE_RANK table to allow ordering within NET_STA_GROUPs.';
  GRANT SELECT ON "DFTT"."SOURCE" TO PUBLIC;
--------------------------------------------------------
--  DDL for Table STATION
--------------------------------------------------------

  CREATE TABLE "DFTT"."STATION" 
   (	"STATION_ID" NUMBER(10,0), 
	"STATION_CODE" VARCHAR2(15 BYTE), 
	"DESCRIPTION" VARCHAR2(250 BYTE), 
	"NETWORK_ID" NUMBER(10,0), 
	"NET_ASSIGNMENT_TYPE" VARCHAR2(100 BYTE), 
	"LDDATE" DATE DEFAULT sysdate
   ) ;

   COMMENT ON COLUMN "DFTT"."STATION"."NETWORK_ID" IS 'Unique identifier for this network';
  GRANT SELECT ON "DFTT"."STATION" TO PUBLIC;
  GRANT DELETE ON "DFTT"."STATION" TO "DFTT";
  GRANT INSERT ON "DFTT"."STATION" TO "DFTT";
  GRANT SELECT ON "DFTT"."STATION" TO "DFTT";
  GRANT UPDATE ON "DFTT"."STATION" TO "DFTT";
--------------------------------------------------------
--  DDL for View CONTINUOUS_WAVEFORM_VIEW
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "DFTT"."CONTINUOUS_WAVEFORM_VIEW" ("WAVEFORM_ID", "ADSL_CHANNEL_ID", "ADSL_ID", "STATION_ID", "NETWORK_ID", "SOURCE_ID", "AGENCY", "NETWORK_CODE", "NET_START_DATE", "STATION_CODE", "LOCATION_CODE", "CHAN", "BEGIN_TIME", "END_TIME", "NSAMP", "SAMPRATE", "CALIB", "CALPER", "DATA_TYPE", "FOFF", "DIR", "DFILE", "LDDATE") AS 
  select a.waveform_Id,
       a.adsl_channel_id,
       c.adsl_id,
       d.station_id,
       e.network_id,
       f.source_id,
       f.source_code agency,
       e.network_code,
       e.start_date net_start_date,
       d.station_code,
       c.location_code,
       b.chan,
       a.begin_time,
       a.end_time,
       a.nsamp,
       a.samprate,
       calib,
       calper,
       a.data_type,
       a.foff,
       a.dir,
       a.dfile,
       a.lddate
  from continuous_waveform a left join continuous_waveform_cal_factor ab on a.waveform_id = ab.waveform_id ,
       adsl_channel        b,
       adsl_locator        c,
       station             d,
       network             e,
       source              f
 where a.adsl_channel_id = b.adsl_channel_id
   and b.adsl_id = c.adsl_id
   and c.station_id = d.station_id
   and d.network_id = e.network_id
   and e.network_source_id = f.source_id;

GRANT SELECT ON "DFTT"."CONTINUOUS_WAVEFORM_VIEW" TO PUBLIC;
create sequence "DFTT".WFID minvalue 1 start with 1 increment by 1 nocache;

-- Create table
create table "DFTT".CONTINUOUS_WFDISC (
  wfid NUMBER(10) not null,
  net VARCHAR2(8),
  sta VARCHAR2(8) not null,
  chan VARCHAR2(8) not null,
  locid VARCHAR2(2),
  time NUMBER not null,
  endtime NUMBER not null,
  jdate NUMBER(8) not null,
  nsamp NUMBER(9) not null,
  samprate NUMBER not null,
  calib NUMBER not null,
  calper NUMBER not null,
  datatype VARCHAR2(2) not null,
  dir VARCHAR2(256) not null,
  dfile VARCHAR2(64) not null,
  foff NUMBER(10) not null,
  lddate DATE not null
);

-- Create/Recreate indexes 
create index "DFTT".CW_CHAN_IDX on "DFTT".CONTINUOUS_WFDISC (CHAN);

create index "DFTT".CW_N_S_C_L_T_IDX on "DFTT".CONTINUOUS_WFDISC (NET, STA, CHAN, LOCID, TIME);

create index "DFTT".CW_STA_CHAN_TIME_ET_IDX on "DFTT".CONTINUOUS_WFDISC (STA, CHAN, TIME, ENDTIME);

create index "DFTT".CW_STA_IDX on "DFTT".CONTINUOUS_WFDISC (STA);

create index "DFTT".CW_TIME_IDX on "DFTT".CONTINUOUS_WFDISC (TIME);

-- Create/Recreate primary, unique and foreign key constraints 
alter table
  "DFTT".CONTINUOUS_WFDISC
add
  constraint CONTINUOUS_WFDISC_PK primary key (WFID) using index;

alter index CONTINUOUS_WFDISC_PK nologging;