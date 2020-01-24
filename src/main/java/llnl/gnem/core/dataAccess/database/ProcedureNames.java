package llnl.gnem.core.dataAccess.database;

public class ProcedureNames {

    public static final String SCHEMA = "llnl2";
    public static final String BEST_STATION_EPOCH_PROC = SCHEMA + ".STATION_UTIL.GET_BEST_STATION_EPOCH";
    public static final String CREATE_ARRIVAL_GROUP_PROC = SCHEMA + ".bulletin_loading.maybe_create_arrival_group";
    public static final String SET_EVENT_PRIME_PROC = SCHEMA + ".ORIGIN_UTIL.SET_EVENT_PRIME";
    public static final String CREATE_UPDATE_ARRIVAL_GROUP_PROC = SCHEMA + ".arrival_util.build_or_update_arrival_group";
    
    public static final String UPDATE_STATION_GROUP_PROC = "{call "+SCHEMA+".adsl_station_util.update_station_group(?)}";

}
