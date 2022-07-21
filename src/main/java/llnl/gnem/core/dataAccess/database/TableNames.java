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

    private static String SCHEMA = "dftt";

    static {
        SCHEMA = System.getProperty("EXTERNAL_SCHEMA_NAME");
    }

    public static String getSchema() {
        return SCHEMA;
    }

    public static String getContinuousWaveformTableName() {
        if (SCHEMA == null) {
            throw new IllegalStateException("SCHEMA not set for Type2Database! Use VM argument -DEXTERNAL_SCHEMA_NAME to set correct value.");
        }
        return SCHEMA + ".CONTINUOUS_WAVEFORM";
    }

    public static String getContinuousWaveformViewName() {
        if (SCHEMA == null) {
            throw new IllegalStateException("SCHEMA not set for Type2Database! Use VM argument -DEXTERNAL_SCHEMA_NAME to set correct value.");
        }
        return SCHEMA + ".CONTINUOUS_WAVEFORM_VIEW";
    }

    public static String getArrayMemberViewName() {
        if (SCHEMA == null) {
            throw new IllegalStateException("SCHEMA not set for Type2Database! Use VM argument -DEXTERNAL_SCHEMA_NAME to set correct value.");
        }
        return SCHEMA + ".ARRAY_MEMBER_VIEW";
    }
}
