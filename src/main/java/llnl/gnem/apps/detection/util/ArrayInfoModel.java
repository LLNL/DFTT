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
package llnl.gnem.apps.detection.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.apps.detection.dataAccess.dataobjects.ArrayConfiguration;
import llnl.gnem.core.util.StreamKey;
import llnl.gnem.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class ArrayInfoModel {

    private final Map<String, ArrayConfiguration> arrayMap;
    private final Map<String, String> stationArrayMap;
    private TimeT currentTime = null;

    private ArrayInfoModel() {
        arrayMap = new HashMap<>();
        stationArrayMap = new HashMap<>();
    }

    public static ArrayInfoModel getInstance() {
        return ArrayInfoModelHolder.INSTANCE;
    }
    
    public void clear()
    {
        arrayMap.clear();
        stationArrayMap.clear();
    }

    public void addArray(ArrayConfiguration array) {
        arrayMap.put(array.getArrayName(), array);
        Collection<String> stations = array.getArrayStationCodes();
        for (String station : stations) {
            stationArrayMap.put(station, array.getArrayName());
        }
    }

    public void setCurrentDate(int aJdate) {
        currentTime = TimeT.getTimeFromJulianDate(aJdate);
    }

    public ArrayConfiguration getGeometry(Collection<StreamKey> staChanList) {
        String arrayName = null;
        for (StreamKey key : staChanList) {
            String tmp = stationArrayMap.get(key.getSta());
            if (tmp == null) {
                continue;
            }
            if (arrayName == null) {
                arrayName = tmp;
            } else if (!tmp.equals(arrayName)) {
                throw new IllegalStateException("Station code: " + tmp + " belongs to more than one array!");
            }
        }
        ArrayConfiguration ac = arrayMap.get(arrayName);
        return ac;
    }

    public ArrayConfiguration getGeometry(String arrayName) {
        return arrayMap.get(arrayName);
    }

    public boolean hasArray(String refSta) {
        ArrayConfiguration tmp = arrayMap.get(refSta);
        return tmp != null;
    }

    private static class ArrayInfoModelHolder {

        private static final ArrayInfoModel INSTANCE = new ArrayInfoModel();
    }
}
