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
package llnl.gnem.apps.detection.util.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StreamAvailability;
import llnl.gnem.dftt.core.dataAccess.database.oracle.waveformUtil.StreamRateKey;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class StreamOrganizer {

    private final Map<StreamRateKey, Collection<StreamAvailability>> myMap;

    public StreamOrganizer() {
        myMap = new HashMap<>();
    }

    public void add(Collection<StreamAvailability> csa) {
        for (StreamAvailability sa : csa) {
            StreamRateKey akey = new StreamRateKey(sa.getKey(), Math.round(sa.getSampleRate()));
            Collection<StreamAvailability> tmp = myMap.get(akey);
            if (tmp == null) {
                tmp = new ArrayList<>();
                myMap.put(akey, tmp);
            }
            tmp.add(sa);
        }
    }

    public Collection<StreamAvailability> getMergedSegments() {
        ArrayList<StreamAvailability> result = new ArrayList<>();

        for (StreamRateKey akey : myMap.keySet()) {
            StreamKey key = null;
            Double maxTime = -Double.MAX_VALUE;
            Double minTime = - maxTime;
            int timeSpans = 0;
            Double rate = null;
            Collection<StreamAvailability> tmp = myMap.get(akey);
            for (StreamAvailability sa : tmp) {
                if (key == null) {
                    key = sa.getKey();
                }
                if(sa.getRange().getStart() < minTime ) minTime = sa.getRange().getStart();
                if(sa.getRange().getEnd() > maxTime)maxTime = sa.getRange().getEnd();

                if (rate == null) {
                    rate = sa.getSampleRate();
                }
                timeSpans += sa.getTimeSpans();
            }
            result.add(new StreamAvailability(key, new Epoch(minTime,maxTime), rate, timeSpans));
        }

         Collections.sort(result);
         return result;
    }

    public boolean isEmpty() {
        return myMap.isEmpty();
    }

}
