package llnl.gnem.apps.detection.dataAccess.database;

import llnl.gnem.core.database.ConnectedUser;

/**
 *
 * @author dodge1
 */
public class TableNames {

    private static final String TMP_SCHEMA = System.getProperty("schema_name");
    private static String SCHEMA = TMP_SCHEMA != null && !TMP_SCHEMA.isEmpty() ? TMP_SCHEMA : "detector";
    private static final String SOCORRO_ORIGIN_TABLE = "SOCORRO_ORIGIN";
    private static final String EVENT_STATION_TIMES_TABLE = "EVENT_STATION_TIMES";
    private static final String CONTINUOUS_WFDISC_TABLE = "LLNL.CONTINUOUS_WFDISC";
    private static final String FRAMEWORK_RUN_TABLE = "FRAMEWORK_RUN";
    private static final String DETECTION_TABLE = "DETECTION";
    private static final String TRIGGER_RECORD_TABLE = "TRIGGER_RECORD";
    private static final String PHASE_PICK_TABLE = "PHASE_PICK";
    private static final String GROUP_STATION_DATA_TABLE = "GROUP_STATION_DATA";
    private static final String CONFIGURATION_GROUP_TABLE = "CONFIGURATION_GROUP";
    private static final String DETECTOR_CHANNEL_TABLE = "DETECTOR_CHANNEL";
    private static final String STREAM_CHANNEL_TABLE = "STREAM_CHANNEL";
    private static final String STREAM_TABLE = "STREAM";
    private static final String STORED_FILTER_TABLE = "STORED_FILTER";
    private static final String EVENT_TABLE = "EVENT";
    private static final String EVENT_PICK_ASSOC_TABLE = "EVENT_PICK_ASSOC";

    public static void setSchemaFromConnectedUser() {
        SCHEMA = ConnectedUser.getInstance().getUser();
    }

     public static String getOriginTable() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + SOCORRO_ORIGIN_TABLE : SOCORRO_ORIGIN_TABLE;
    }

    public static String getEventStationTimesTable() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + EVENT_STATION_TIMES_TABLE : EVENT_STATION_TIMES_TABLE;
    }

    public static String getContinuousWfdiscTable() {
        return CONTINUOUS_WFDISC_TABLE;
    }

    public static String getFrameworkRunTable() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + FRAMEWORK_RUN_TABLE : FRAMEWORK_RUN_TABLE;
    }

    public static String getDetectionTable() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + DETECTION_TABLE : DETECTION_TABLE;
    }

    public static String getTriggerRecordTable() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + TRIGGER_RECORD_TABLE : TRIGGER_RECORD_TABLE;
    }

    public static String getPhasePickTable() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + PHASE_PICK_TABLE : PHASE_PICK_TABLE;
    }

    public static String getGroupStationDataTable() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + GROUP_STATION_DATA_TABLE : GROUP_STATION_DATA_TABLE;
    }

    public static String getConfigurationGroupTable() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + CONFIGURATION_GROUP_TABLE : CONFIGURATION_GROUP_TABLE;
    }

    public static String getDetectorChannelTable() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + DETECTOR_CHANNEL_TABLE : DETECTOR_CHANNEL_TABLE;
    }
    
    public static String getStreamChannelTable(){
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + STREAM_CHANNEL_TABLE : STREAM_CHANNEL_TABLE;
    }
    
    public static String getStreamTable(){
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + STREAM_TABLE : STREAM_TABLE;
    }
    
    public static String getStoredFilterTable(){
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + STORED_FILTER_TABLE : STORED_FILTER_TABLE;
    }
    
    public static String getEventTable(){
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + EVENT_TABLE : EVENT_TABLE;
    }
   
    public static String getEventPickAssocTable(){
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + EVENT_PICK_ASSOC_TABLE : EVENT_PICK_ASSOC_TABLE;
    }
    
}
