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
package llnl.gnem.apps.detection.source;

import llnl.gnem.apps.detection.database.DbOps;
import java.sql.SQLException;

/**
 * Created by dodge1 Date: Jul 14, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class WfdiscTableSourceData extends SourceData {

    public WfdiscTableSourceData(String srcWfdiscTable, String streamGroup, boolean scaleByCalib) throws Exception {
        super(streamGroup, scaleByCalib);
        testWfdiscTable(srcWfdiscTable);
        setWfdiscTable(srcWfdiscTable);
    }

    private void testWfdiscTable(String wfdiscTable) throws SQLException {

        if (!DbOps.getInstance().isTableExists(wfdiscTable)) {
            throw new IllegalArgumentException(String.format("The specified table(%s) does not exist or is inaccessible!!", wfdiscTable));
        }
    }

    @Override
    public void close() throws SQLException {
    }
}
