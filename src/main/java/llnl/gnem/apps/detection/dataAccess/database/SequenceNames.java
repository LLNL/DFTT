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

import llnl.gnem.dftt.core.database.ConnectedUser;

public class SequenceNames {


    private static final String PICKID_SEQ = "PICKID";
    private static final String FILTERID_SEQ = "FILTERID";
    private static final String EVENTID_SEQ = "EVENTID";
    private static final String TRIGGERID_SEQ = "TRIGGERID";
    private static final String DETECTIONID_SEQ = "DETECTIONID";
    private static final String CONFIGID_SEQ = "CONFIGID";
    private static final String STREAMID_SEQ = "STREAMID";
    private static final String DETECTORID_SEQ = "DETECTORID";
    private static final String RUNID_SEQ = "RUNID";

    public static String getPickidSequenceName() {
        return PICKID_SEQ;
    }

    public static String getFilterdSequenceName() {
        return  FILTERID_SEQ;
    }

    public static String getEventidSequenceName() {
        return  EVENTID_SEQ;
    }

    public static String getTriggeridSequenceName() {
        return TRIGGERID_SEQ;
    }
    
    public static String getDetectionidSequenceName(){
        return DETECTIONID_SEQ;
    }

    public static String getConfigidSequenceName()
    {
        return  CONFIGID_SEQ;
    }
    
    public static String getStreamidSequenceName()
    {
        return  STREAMID_SEQ;
    }
    
    public static String getDetectoridSequenceName()
    {
        return  DETECTORID_SEQ;
    }

    public static String getRunidSequenceName() {
        return  RUNID_SEQ;
    }
}
