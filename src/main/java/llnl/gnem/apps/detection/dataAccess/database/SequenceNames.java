package llnl.gnem.apps.detection.dataAccess.database;

import llnl.gnem.core.database.ConnectedUser;

public class SequenceNames {

    private static final String TMP_SCHEMA = System.getProperty("schema_name");
    private static String SCHEMA = TMP_SCHEMA != null && !TMP_SCHEMA.isEmpty() ? TMP_SCHEMA : "detector";

    public static void setSchemaFromConnectedUser() {
        SCHEMA = ConnectedUser.getInstance().getUser();
    }

    private static final String PICKID_SEQ = "PICKID";
    private static final String FILTERID_SEQ = "FILTERID";
   private static final String EVENTID_SEQ = "EVENTID";


    public static String getPickidSequenceName() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + PICKID_SEQ : PICKID_SEQ;
    }

    public static String getFilterdSequenceName() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + FILTERID_SEQ : FILTERID_SEQ;
    }

    public static String getEventidSequenceName() {
        return SCHEMA != null && !SCHEMA.isEmpty() ? SCHEMA + "." + EVENTID_SEQ : EVENTID_SEQ;
    }
    
}
