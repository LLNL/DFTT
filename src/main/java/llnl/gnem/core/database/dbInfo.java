package llnl.gnem.core.database;

/*
*  COPYRIGHT NOTICE
*  GnemUtils Version 1.0
*  Copyright (C) 2002 Lawrence Livermore National Laboratory.
*/

/**
 * dbInfo is a class that manages database information stored
 * on the file system. dbInfo provides methods for getting and setting each
 * parameter known to it. It provides default values for all data, and manages the
 * storage of the data between program runs.
 *
 * @author Doug Dodge
 */
public class dbInfo {

    private static dbInfo my_instance = null;

    public static dbInfo getdbInfoInstance()
    {
        if( my_instance == null )
            my_instance = new dbInfo();
        return my_instance;
    }

    /**
     * Gets the driverName attribute of the ProgramData object
     *
     * @return The driverName value
     */
    public String getDriverName()
    {
        return "jdbc:oracle:thin";
    }



    /**
     * Gets the portNumber attribute of the ProgramData object
     *
     * @return The portNumber value
     */
    public int getPortNumber()
    {
        return 1521;
    }



    /**
     * Gets the SITE_TABLE attribute of the dbInfo object
     *
     * @return The SITE_TABLE value
     */
    public String getSiteTable()
    {
        return  "LLNL.SITE";
    }

    /**
     * Gets the SITECHAN_TABLE attribute of the dbInfo object
     *
     * @return The SITECHAN_TABLE value
     */
    public String getSitechanTable()
    {
        return "LLNL.SITECHAN";
    }

    /**
     * Gets the SITECHAN_MASTER_TABLE attribute of the dbInfo object
     *
     * @return The SITECHAN_MASTER_TABLE value
     */
    public String getSitechanMasterTable()
    {
        return "LLNL.SITECHAN_MASTER";
    }

    /**
     * Gets the ETYPE_DESC_TABLE value
     *
     * @return The ETYPE_DESC_TABLE value
     */
    public String getEtypeDescTable()
    {
        return "LLNL.ETYPE_DESC";
    }

    /**
     * Gets the SEARCH_LINK_TABLE value
     *
     * @return The SEARCH_LINK_TABLE value
     */
    public String getSearchLinkTable()
    {
        return "LLNL.SEARCH_LINK";
    }


    /**
     * Gets the EVENT_TABLE value
     *
     * @return The EVENT_TABLE value
     */
    public String getEventTable()
    {
        return "LLNL.EVENT";
    }

    /**
     * Gets the EVENT_ARRIVAL_ASSOC_TABLE value
     *
     * @return The EVENT_ARRIVAL_ASSOC_TABLE value
     */
    public String getEventArrivalAssocTable()
    {
        return "LLNL.EVENT_ARRIVAL_ASSOC";
    }

    /**
     * Gets the ORIGIN_TABLE value
     *
     * @return The ORIGIN_TABLE value
     */
    public String getOriginTable()
    {
        return "LLNL.ORIGIN";
    }


    /**
     * Gets the ORIGERR_TABLE value
     *
     * @return The ORIGERR_TABLE value
     */
    public String getOrigerrTable()
    {
        return "LLNL.ORIGERR";
    }


    /**
     * Gets the NETMAG_TABLE value
     *
     * @return The NETMAG_TABLE value
     */
    public String getNetmagTable()
    {
        return "LLNL.NETMAG";
    }

    /**
     * Gets the WFDISC_TABLE value
     *
     * @return The WFDISC_TABLE value
     */
    public String getWfdiscTable()
    {
        return "LLNL.WFDISC";
    }

    /**
     * Gets the WFTAG_TABLE value
     *
     * @return The WFTAG_TABLE value
     */
    public String getWftagTable()
    {
        return "LLNL.WFTAG";
    }

    /**
     * Gets the INSTRUMENT_TABLE value
     *
     * @return The INSTRUMENT_TABLE value
     */
    public String getInstrumentTable()
    {
        return "LLNL.INSTRUMENT";
    }

    /**
     * Gets the SENSOR_TABLE value
     *
     * @return The SENSOR_TABLE value
     */
    public String getSensorTable()
    {
        return "LLNL.SENSOR";
    }

    /**
     * Gets the ARRIVAL_TABLE value
     *
     * @return The ARRIVAL_TABLE value
     */
    public String getArrivalTable()
    {
        return "LLNL.ARRIVAL";
    }

    /**
     * Gets the ASSOC_TABLE value
     *
     * @return The ASSOC_TABLE value
     */
    public String getAssocTable()
    {
        return "LLNL.ASSOC";
    }

    /**
     * Gets the STORED_FILTER value
     *
     * @return The STORED_FILTER value
     */
    public String getStoredFilterTable()
    {
        return "LLNL.STORED_FILTER";
    }

    /**
     * Gets the APPLIED_FILTER value
     *
     * @return The APPLIED_FILTER value
     */
    public String getAppliedFilterTable()
    {
        return "LLNL.APPLIED_FILTER";
    }

    /**
     * Gets the BAD_SEGMENTS_TABLE value
     *
     * @return The BAD_SEGMENTS_TABLE value
     */
    public String getBadSegmentsTable()
    {
        return "LLNL.BAD_SEGMENTS";
    }

    /**
     * Gets the PROBLEM_TYPE_TABLE value
     *
     * @return The PROBLEM_TYPE_TABLE value
     */
    public String getProblemTypeTable()
    {
        return "LLNL.PROBLEM_TYPE";
    }

    /**
     * Gets the MAGNITUDE_TABLE value
     *
     * @return The MAGNITUDE_TABLE value
     */
    public String getMagnitudeTable()
    {
        return "LLNL.MAGNITUDE";
    }

    /**
     * Gets the GT_EPICENTER_TABLE value
     *
     * @return The GT_EPICENTER_TABLE value
     */
    public String getGtEpicenterTable()
    {
        return "LLNL.GT_EPICENTER";
    }


    /**
     * Gets the GT_DEPTH_TABLE value
     *
     * @return The GT_DEPTH_TABLE value
     */
    public String getGtDepthTable()
    {
        return "LLNL.GT_DEPTH";
    }

    /**
     * Gets the LOCATION_PHASES_TABLE value
     *
     * @return The LOCATION_PHASES_TABLE value
     */
    public String getLocationPhasesTable()
    {
        return "LLNL.LOCATION_PHASES";
    }


    public String getSiteMasterTable()
    {
        return "LLNL.SITE_MASTER";
    }

    /**
     * Gets the PREFERRED_ORIGIN_TABLE value
     *
     * @return The PREFERRED_ORIGIN_TABLE value
     */
    public String getPreferredOriginTable()
    {
        return "LLNL.PREFERRED_ORIGIN";
    }


    /**
     * Gets the CHANSET_3D_TABLE value
     *
     * @return The CHANSET_3D_TABLE value
     */
    public String getChanset3DTable()
    {
        return "LLNL.CHANSET_3D";
    }


    /**
     * Gets the ARRIVAL_WAVEFORM_ASSOC_TABLE value
     *
     * @return The ARRIVAL_WAVEFORM_ASSOC_TABLE value
     */
    public String getArrivalWaveformAssocTable()
    {
        return "LLNL.ARRIVAL_WAVEFORM_ASSOC";
    }


    /**
     * Gets the ARRIVAL_AUTH_RANK_TABLE value
     *
     * @return The ARRIVAL_AUTH_RANK_TABLE value
     */
    public String getArrivalAuthRankTable()
    {
        return "LLNL.ARRIVAL_AUTH_RANK";
    }

    /**
     * Gets the BAND_CODE_TABLE value
     *
     * @return The BAND_CODE_TABLE value
     */
    public String getBandCodeTable()
    {
        return "LLNL.BAND_CODE";
    }


    /**
     * Gets the INSTRUMENT_CODE_TABLE value
     *
     * @return The INSTRUMENT_CODE_TABLE value
     */
    public String getInstrumentCodeTable()
    {
        return "LLNL.INSTRUMENT_CODE";
    }


    /**
     * Gets the PHASE_DESC_TABLE value
     *
     * @return The PHASE_DESC_TABLE value
     */
    public String getPhaseDescTable()
    {
        return "LLNL.PHASE_DESC";
    }


    /**
     * Gets the BAD_TRACE_TABLE value
     *
     * @return The BAD_TRACE_TABLE value
     */
    public String getBadTraceTable()
    {
        return "LLNL.BAD_TRACE";
    }


    /**
     * Gets the ARRIVAL_INFO_TABLE value
     *
     * @return The ARRIVAL_INFO_TABLE value
     */
    public String getArrivalInfoTable()
    {
        return "LLNL.ARRIVAL_INFO";
    }


    /**
     * Gets the PREFERRED_ARRIVAL_TABLE value
     *
     * @return The PREFERRED_ARRIVAL_TABLE value
     */
    public String getPreferredArrivalTable()
    {
        return "LLNL.PREFERRED_ARRIVAL";
    }


    /**
     * Gets the POOR_QUALITY_ARRIVAL_TABLE value
     *
     * @return The POOR_QUALITY_ARRIVAL_TABLE value
     */
    public String getKbalapPoorQualityArrivalTable()
    {
        return "LLNL.POOR_QUALITY_ARRIVAL";
    }

}

