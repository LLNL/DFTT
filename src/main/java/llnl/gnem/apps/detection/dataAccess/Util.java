/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.dataAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayConfiguration;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayElementInfo;
import llnl.gnem.apps.detection.util.ArrayInfoModel;
import llnl.gnem.dftt.core.dataAccess.DataAccessException;
import llnl.gnem.dftt.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class Util {

    public static void populateArrayInfoModel(int jdate) throws DataAccessException {
        Collection<ArrayElementInfo> allElements = DetectionDAOFactory.getInstance().getArrayConfigurationDAO().getAllArrayElements();
        populateArrayInfoModel(jdate, allElements);
    }

    public static void populateArrayInfoModel(Integer jdate, Collection<ArrayElementInfo> allElements) {
        ArrayInfoModel.getInstance().clear();

        // All elements; all arrays; all epochs
        TimeT aTime = jdate != null ? TimeT.getTimeFromJulianDate(jdate) : null;
        Map<String, Collection<ArrayElementInfo>> arrayElementMap = new HashMap<>();
        for (ArrayElementInfo aei : allElements) {
            // Only include elements that cover the processing start date.
            if (aTime != null && !aei.getEpoch().ContainsTime(aTime)) {
                continue;
            }
            String arrayName = aei.getArrayName();
            Collection<ArrayElementInfo> tmp = arrayElementMap.get(arrayName);
            if (tmp == null) {
                tmp = new ArrayList<>();
                arrayElementMap.put(arrayName, tmp);
            }
            tmp.add(aei);
        }
        for (String arrayName : arrayElementMap.keySet()) {
            Collection<ArrayElementInfo> info = arrayElementMap.get(arrayName);
            ArrayConfiguration array = new ArrayConfiguration(info);
            ArrayInfoModel.getInstance().addArray(array);
        }
    }
}
