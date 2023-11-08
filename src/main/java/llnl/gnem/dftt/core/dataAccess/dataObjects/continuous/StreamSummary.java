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
package llnl.gnem.dftt.core.dataAccess.dataObjects.continuous;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import llnl.gnem.dftt.core.util.Epoch;
import llnl.gnem.dftt.core.util.StreamKey;
import llnl.gnem.dftt.core.util.TimeT;

/**
 *
 * @author dodge1
 */
public class StreamSummary {

    private final Map<StreamKey, Collection<StreamAvailability>> streamAvailabilityMap;

    public StreamSummary(Collection<StreamAvailability> streams) {
        this.streamAvailabilityMap = new HashMap<>();
        if (!streams.isEmpty()) {
            StreamAvailability sa = streams.iterator().next();
            streamAvailabilityMap.put(sa.getKey(), streams);
        }

    }

    public StreamSummary(Map<StreamKey, Collection<StreamAvailability>> streamAvailabilityMap) {
        this.streamAvailabilityMap = new HashMap<>(streamAvailabilityMap);
    }

    public StreamSupport getSupport(StreamKey key, int minJdate, int maxJdate) {
        TimeT begin = TimeT.getTimeFromJulianDate(minJdate);
        TimeT end = TimeT.getTimeFromJulianDate(maxJdate);
        end.add(TimeT.SECPERDAY);
        Epoch requested = new Epoch(begin, end);
        Collection<StreamAvailability> sac = streamAvailabilityMap.get(key);
        if (sac == null) {
            return null;
        }

        Long rate = null;
        Map<Long, Set<StreamAvailability>> foo = new HashMap<>();
        for (StreamAvailability sa : sac) {
            if (sa.getRange().intersects(requested)) {
                rate = Math.round(sa.getSampleRate());
                Set<StreamAvailability> tmp = foo.get(rate);
                if (tmp == null) {
                    tmp = new HashSet<>();
                    foo.put(rate, tmp);
                }
                tmp.add(sa);
            }
        }
        if (foo.size() > 1) {
            throw new IllegalStateException("More than one sample rate for " + key + " in requested time interval!");
        }
        return buildSupport(rate, foo.get(rate));
    }


    private StreamSupport buildSupport(Long rate, Set<StreamAvailability> values) {
        Double maxVal = -Double.MAX_VALUE;
        Double minVal = -maxVal;
        for (StreamAvailability sa : values) {
            if (sa.getRange().getStart() < minVal) {
                minVal = sa.getRange().getStart();
            }
            if (sa.getRange().getEnd() > maxVal) {
                maxVal = sa.getRange().getEnd();
            }
        }
        return new StreamSupport(new Epoch(minVal, maxVal), (double) rate);
    }
}
