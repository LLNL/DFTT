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
