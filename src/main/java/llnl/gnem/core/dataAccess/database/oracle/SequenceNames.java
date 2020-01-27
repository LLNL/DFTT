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
package llnl.gnem.core.dataAccess.database.oracle;

import static llnl.gnem.core.dataAccess.database.TableNames.SCHEMA;

/**
 *
 * @author dodge1
 */
public class SequenceNames {
    public static final String ARRIVAL_ID_SEQUENCE = SCHEMA + ".ARRIVAL_ID_SEQ";
    
    public static final String FILTER_ID_SEQUENCE = SCHEMA + ".FILTER_ID_SEQ";
    public static final String GROUP_ID_SEQUENCE = SCHEMA + ".GROUP_ID_SEQ";
    public static final String EVENT_ID_SEQUENCE = SCHEMA + ".EVENT_ID_SEQ";
    public static final String ORIGIN_ID_SEQ = SCHEMA + ".ORIGIN_ID_SEQ";
    public static final String MAGNITUDE_ID_SEQUENCE = SCHEMA + ".MAGNITUDE_ID_SEQ";
    public static final String MOMENT_TENSOR_ID_SEQUENCE = SCHEMA + ".MOMENT_TENSOR_ID_SEQ";
    public static final String STATION_ID_SEQUENCE = SCHEMA + ".STATION_ID_SEQ";
    public static final String STATION_EPOCH_ID_SEQUENCE = SCHEMA + ".STATION_EPOCH_ID_SEQ";
    public static final String STREAM_ID_SEQUENCE = SCHEMA + ".STREAM_ID_SEQ";
    public static final String STREAM_EPOCH_ID_SEQUENCE = SCHEMA + ".STREAM_EPOCH_ID_SEQ";
    public static final String ARRAY_ID_SEQUENCE = SCHEMA + ".ARRAY_ID_SEQ";
    public static final String WAVEFORM_ID_SEQUENCE = SCHEMA + ".WAVEFORM_ID_SEQ";
    public static final String RETRIEVAL_ID_SEQUENCE = SCHEMA + ".RETRIEVAL_ID_SEQ";
    public static final String ADSL_ID_SEQUENCE = SCHEMA + ".ADSL_ID_SEQ";
    public static final String ADSL_EPOCH_ID_SEQUENCE = SCHEMA + ".ADSL_EPOCH_ID_SEQ";
    public static final String ADSL_CHANNEL_ID_SEQUENCE = SCHEMA + ".ADSL_CHANNEL_ID_SEQ";
    public static final String ADSL_CHANNEL_EPOCH_ID_SEQUENCE = SCHEMA + ".ADSL_CHANNEL_EPOCH_ID_SEQ";
    public static final String RESPONSE_ID_SEQUENCE = SCHEMA + ".RESPONSE_ID_SEQ";
    public static final String NETWORK_ID_SEQUENCE = SCHEMA + ".NETWORK_ID_SEQ";
    public static final String SOURCE_ID_SEQUENCE = SCHEMA + ".SOURCE_ID_SEQ";
    
    
}
