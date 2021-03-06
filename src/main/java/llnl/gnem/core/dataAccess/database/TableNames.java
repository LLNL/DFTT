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
package llnl.gnem.core.dataAccess.database;

public class TableNames {

    public static final String SCHEMA = "llnl2";
    public static final String POLYGON_DATA_TABLE = SCHEMA + ".POLYGON_DATA";
    public static final String POLYGON_TABLE = SCHEMA + ".POLYGON";
    public static final String POLYGON_SET_TABLE = SCHEMA + ".POLYGON_SET";
    public static final String ORIGIN_SOLUTION_TABLE = SCHEMA + ".ORIGIN_SOLUTION";
    public static final String PREFERRED_MAGNITUDE_TABLE = SCHEMA + ".PREFERRED_MAGNITUDE";
    public static final String WAVEFORM_SEGMENT_TABLE = SCHEMA + ".WAVEFORM_SEGMENT";
    public static final String ETYPE_DESC_TABLE = SCHEMA + ".ETYPE_DESC";
    public static final String TEMP_EVID_TABLE = SCHEMA + ".GLOB_TEMP_TABLE_EVID";
    public static final String STREAM_TABLE = SCHEMA + ".STREAM";
    public static final String STATION_EPOCH_VIEW = SCHEMA + ".STATION_EPOCH_VIEW";
    public static final String ORIGERR_TABLE = SCHEMA + ".ORIGERR";
    public static final String NETMAG_VIEW = SCHEMA + ".NETMAG_VIEW";
    public static final String ORIGIN_TABLE = SCHEMA + ".ORIGIN_SOLUTION";
    public static final String MT_SOLUTION_TABLE = SCHEMA + ".SOURCE_INVERSION_RESULT";
    public static final String MOMENT_TENSOR_TABLE = SCHEMA + ".MOMENT_TENSOR";
    public static final String FP_SOLUTION_TABLE = SCHEMA + ".FOCAL_PLANE_SOLUTION";
    public static final String SPRINGER_EXPLOSION_TABLE = SCHEMA + ".SPRINGER_EXPLOSION_INFO";
    public static final String WAVEFORM_VIEW = SCHEMA + ".WAVEFORM_VIEW";
    public static final String STREAM_EPOCH_VIEW = SCHEMA + ".STREAM_EPOCH_VIEW";
    public static final String PHASE_DESC_TABLE = SCHEMA + ".PHASE_DESC";
    public static final String STORED_FILTER_TABLE = SCHEMA + ".STORED_FILTER";
    public static final String STATION_ALIAS_TABLE = SCHEMA + ".STATION_ALIAS";
    public static final String ARRIVAL_TABLE = SCHEMA + ".ARRIVAL";
    public static final String STATION_EPOCH_TABLE = SCHEMA + ".STATION_EPOCH";
    public static final String EVENT_DESCRIPTION_TABLE = SCHEMA + ".EVENT_DESCRIPTION";
    public static final String PICK_GROUP_TABLE = SCHEMA + ".PICK_GROUP";
    public static final String PREFERRED_ARRIVAL_TABLE = SCHEMA + ".PREFERRED_ARRIVAL";
    public static final String ARRIVAL_WAVEFORM_ASSOC_TABLE = SCHEMA + ".ARRIVAL_WAVEFORM_ASSOC";
    public static final String APPLIED_FILTER_TABLE = SCHEMA + ".APPLIED_FILTER";
    public static final String ARRIVAL_CHARACTERISTIC_TABLE = SCHEMA + ".ARRIVAL_CHARACTERISTIC";
    public static final String CORR_MATRIX_ELEMENT_TABLE = SCHEMA + ".CORR_MATRIX_ELEMENT";
    public static final String CORRELATION_PICK_TABLE = SCHEMA + ".CORRELATION_PICK";
    public static final String SEARCH_LINK_TABLE = SCHEMA + ".SEARCH_LINK";
    public static final String STATION_TABLE = SCHEMA + ".STATION";
    public static final String NETWORK_TABLE = SCHEMA + ".NETWORK";
    public static final String SOURCE_TABLE = SCHEMA + ".SOURCE";
    public static final String NETMAG_TABLE = SCHEMA + ".NETMAG";
    public static final String QUICK_ORIGIN_LOOKUP_TABLE = SCHEMA + ".QUICK_ORIGIN_LOOKUP";
    public static final String WAVEFORM_CSS_CAL_FACTOR_TABLE = SCHEMA + ".WAVEFORM_CSS_CAL_FACTOR";
    public static final String STREAM_EPOCH_TABLE = SCHEMA + ".STREAM_EPOCH";
    public static final String ARRIVAL_GROUP_TABLE = SCHEMA + ".ARRIVAL_GROUP";
    public static final String REVIEWED_ARRIVAL_TABLE = SCHEMA + ".REVIEWED_ARRIVAL";
    public static final String ARRAY_MEMBER_TABLE = SCHEMA + ".ARRAY_MEMBER";
    public static final String ARRAY_TABLE = SCHEMA + ".ARRAY";
    public static final String RESPONSE_VIEW = SCHEMA + ".RESPONSE_VIEW";
    public static final String QC_SEGMENT_QUALITY_TABLE = SCHEMA + ".QC_SEGMENT_QUALITY";
    public static final String QC_FILTER_BAND_TABLE = SCHEMA + ".QC_FILTER_BAND";
    public static final String QC_SEGMENT_ERROR_TABLE = SCHEMA + ".QC_SEGMENT_ERROR";
    public static final String QC_SEGMENT_PHASE_QUAL_TABLE = SCHEMA + ".QC_SEG_PHASE_QUALITY";
    public static final String QC_SEG_NOISE_TRAINING_RESULT_TABLE = SCHEMA + ".QC_SEG_NOISE_TRAINING_RESULT";
    public static final String QC_SEG_SIGNAL_TRAINING_RESULT_TABLE = SCHEMA + ".QC_SEG_SIGNAL_TRAINING_RESULT";
    public static final String EVENT_SOURCE_ASSOC_TABLE = SCHEMA + ".EVENT_SOURCE_ASSOC";
    public static final String SEISMIC_EVENT_TABLE = SCHEMA + ".SEISMIC_EVENT";
    public static final String ORIGIN_INFO_TABLE = SCHEMA + ".ORIGIN_INFO";
    public static final String NETMAG_SOURCE_ASSOC_TABLE = SCHEMA + ".NETMAG_SOURCE_ASSOC";
    public static final String STREAM_VIEW = SCHEMA + ".STREAM_VIEW";
    public static final String STATION_VIEW = SCHEMA + ".STATION_VIEW";
    public static final String STATION_MAGNITUDE_TABLE = SCHEMA + ".STATION_MAGNITUDE";
    public static final String ARRIVAL_SOURCE_ASSOC_TABLE = SCHEMA + ".ARRIVAL_SOURCE_ASSOC";
    public static final String ARRIVAL_GEOMETRY_TABLE = SCHEMA + ".ARRIVAL_GEOMETRY";
    public static final String ARRIVAL_ORIGIN_ASSOC_TABLE = SCHEMA + ".ARRIVAL_ORIGIN_ASSOC";
    public static final String ASSOC_SLOWNESS_STAT_TABLE = SCHEMA + ".ASSOC_SLOWNESS_STAT";
    public static final String ASSOC_AZIMUTH_STAT_TABLE = SCHEMA + ".ASSOC_AZIMUTH_STAT";
    public static final String ARRIVAL_QML_AMPLITUDE_TABLE = SCHEMA + ".ARRIVAL_QML_AMPLITUDE";
    public static final String DATA_SERVICES_TABLE = SCHEMA + ".DATA_SERVICES";
    public static final String MOMENT_TENSOR_SOURCE_ASSOC_TABLE = SCHEMA + ".MOMENT_TENSOR_SOURCE_ASSOC";
    public static final String SOURCE_INVERSION_RESULT_TABLE = SCHEMA + ".SOURCE_INVERSION_RESULT";
    public static final String MAGTYPE_RANK_TABLE = SCHEMA + ".MAGTYPE_RANK";
    public static final String MAG_AUTHOR_ERROR_TABLE = SCHEMA + ".MAG_AUTHOR_ERROR";
    public static final String ARRIVAL_AMPLITUDE_TABLE = SCHEMA + ".ARRIVAL_AMPLITUDE";
    public static final String ARRIVAL_DATA_STAGE_TABLE = SCHEMA + ".ARRIVAL_DATA_STAGE";
    public static final String ARRIVAL_QML_AMP_STAGE_TABLE = SCHEMA + ".ARRIVAL_QML_AMP_STAGE";
    public static final String STATION_MAG_STAGE_TABLE = SCHEMA + ".STATION_MAG_STAGE";
    public static final String AK135_PHASE_TABLE = SCHEMA + ".AK135_PHASE";
    public static final String FUZZY_PHASE_MATCH_TABLE = SCHEMA + ".FUZZY_PHASE_MATCH"; 
    public static final String FAILED_ASSOCIATION_TABLE = SCHEMA + ".FAILED_ASSOCIATION"; 
    public static final String NETWORK_VIEW = SCHEMA + ".NETWORK_VIEW"; 
    public static final String BULLETIN_RETRIEVAL_STATISTICS_TABLE = SCHEMA + ".BULLETIN_RETRIEVAL_STATISTICS";
    public static final String ADSL_LOCATOR_TABLE = SCHEMA + ".ADSL_LOCATOR";
    public static final String ADSL_EPOCH_TABLE = SCHEMA + ".ADSL_EPOCH";
    public static final String ADSL_CHANNEL_TABLE = SCHEMA + ".ADSL_CHANNEL";
    public static final String ADSL_CHANNEL_EPOCH_TABLE = SCHEMA + ".ADSL_CHANNEL_EPOCH";
    public static final String ADSL_RESPONSE_TABLE = SCHEMA + ".ADSL_RESPONSE";
    public static final String ADSL_EPOCH_ERROR_TABLE = SCHEMA + ".ADSL_EPOCH_ERROR";
    public static final String ADSL_CHANNEL_VIEW = SCHEMA + ".ADSL_CHANNEL_VIEW";
    public static final String ADSL_EPOCH_VIEW = SCHEMA + ".ADSL_EPOCH_VIEW";
    public static final String ADSL_STATION_GROUP_MEMBER_TABLE = SCHEMA + ".ADSL_STATION_GROUP_MEMBER";
    public static final String NETWORK_VERSION_TABLE = SCHEMA + ".NETWORK_VERSION";
    public static final String ADSL_CHANNEL_EPOCH_VIEW = SCHEMA + ".ADSL_CHANNEL_EPOCH_VIEW";
    public static final String GENERATED_STATION_EPOCH_TABLE= SCHEMA + ".GENERATED_STATION_EPOCH";

}
