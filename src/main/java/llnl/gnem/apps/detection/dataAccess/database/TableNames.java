/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.dataAccess.database;

/**
 *
 * @author dodge1
 */
public class TableNames {

    private static final String ORIGIN_TABLE = "ORIGIN";
    private static final String EVENT_STATION_TIMES_TABLE = "EVENT_STATION_TIMES";
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
    private static final String DETECTOR_THRESHOLD_HISTORY_TABLE = "DETECTOR_THRESHOLD_HISTORY";
    private static final String SUBSPACE_TEMPLATE_TABLE = "SUBSPACE_TEMPLATE";
    private static final String SUBSPACE_DETECTOR_PARAMS_TABLE = "SUBSPACE_DETECTOR_PARAMS";
    private static final String DETECTOR_TABLE = "DETECTOR";
    private static final String DETECTOR_TRAINING_DATA_TABLE = "DETECTOR_TRAINING_DATA";
    private static final String TRIGGER_CLASSIFICATION_TABLE = "TRIGGER_CLASSIFICATION";
    private static final String TRIGGER_DATA_FEATURE_TABLE = "TRIGGER_DATA_FEATURE";
    private static final String TRIGGER_FK_DATA_TABLE = "TRIGGER_FK_DATA";
    private static final String CONFIGURATION_TABLE = "CONFIGURATION";
    private static final String STREAM_FK_PARAM_TABLE = "STREAM_FK_PARAM";
    private static final String DET_STAT_HISTOGRAM_TABLE = "DET_STAT_HISTOGRAM";
    private static final String ARRAY_DETECTOR_PARAMS_TABLE = "ARRAY_DETECTOR_PARAMS";
    private static final String BULLETIN_DETECTOR_SPEC_TABLE = "BULLETIN_DETECTOR_SPEC";
    private static final String STALTA_DETECTOR_PARAMS_TABLE = "STALTA_DETECTOR_PARAMS";
    private static final String BEAM_RECIPE_TABLE = "BEAM_RECIPE";
    private static final String  ARRAY_INFO_TABLE = "ARRAY_INFO";


    public static String getOriginTable() {
        return  ORIGIN_TABLE;
    }

    public static String getEventStationTimesTable() {
        return EVENT_STATION_TIMES_TABLE;
    }

    public static String getFrameworkRunTable() {
        return  FRAMEWORK_RUN_TABLE;
    }

    public static String getDetectionTable() {
        return DETECTION_TABLE;
    }

    public static String getTriggerRecordTable() {
        return  TRIGGER_RECORD_TABLE;
    }

    public static String getPhasePickTable() {
        return  PHASE_PICK_TABLE;
    }

    public static String getGroupStationDataTable() {
        return  GROUP_STATION_DATA_TABLE;
    }

    public static String getConfigurationGroupTable() {
        return  CONFIGURATION_GROUP_TABLE;
    }

    public static String getDetectorChannelTable() {
        return  DETECTOR_CHANNEL_TABLE;
    }

    public static String getStreamChannelTable() {
        return STREAM_CHANNEL_TABLE;
    }

    public static String getStreamTable() {
        return  STREAM_TABLE;
    }

    public static String getStoredFilterTable() {
        return  STORED_FILTER_TABLE;
    }

    public static String getEventTable() {
        return EVENT_TABLE;
    }

    public static String getEventPickAssocTable() {
        return EVENT_PICK_ASSOC_TABLE;
    }

    public static String getDetectorThresholdHistoryTable() {

        return DETECTOR_THRESHOLD_HISTORY_TABLE;
    }

    public static String getSubspaceTemplateTable() {
        return SUBSPACE_TEMPLATE_TABLE;
    }

    public static String getSubspaceDetectorParamsTable() {
        return SUBSPACE_DETECTOR_PARAMS_TABLE;
    }

    public static String getDetectorTable() {
        return  DETECTOR_TABLE;
    }

    public static String getDetectorTrainingDataTable() {
        return  DETECTOR_TRAINING_DATA_TABLE;
    }

    public static String getTriggerClassificationTable() {
        return TRIGGER_CLASSIFICATION_TABLE;
    }

    public static String getTriggerDataFeatureTable() {
        return  TRIGGER_DATA_FEATURE_TABLE;
    }

    public static String getTriggerFkDataTable() {
        return  TRIGGER_FK_DATA_TABLE;
    }

    public static String getConfigurationTable() {
        return  CONFIGURATION_TABLE;
    }

    public static String getStreamFKParamTableName() {
        return STREAM_FK_PARAM_TABLE;
    }

    public static String getDetStatHistogramTable() {
        return DET_STAT_HISTOGRAM_TABLE;
    }

    public static String getArrayDetectorParamsTable() {
        return  ARRAY_DETECTOR_PARAMS_TABLE;
    }
    
    public static String getBulletinDetectorSpecTable(){
        return BULLETIN_DETECTOR_SPEC_TABLE;
    }
    
    public static String getSTALTADetectorParamsTable(){
         return STALTA_DETECTOR_PARAMS_TABLE;
    }

    public static String getBeamRecipeTable() {
        return  BEAM_RECIPE_TABLE;
    }
 
    public static String getArrayInfoTable() {
        return  ARRAY_INFO_TABLE;
    }

}
