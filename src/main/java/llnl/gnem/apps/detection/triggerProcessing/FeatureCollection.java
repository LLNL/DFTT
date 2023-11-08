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
package llnl.gnem.apps.detection.triggerProcessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import llnl.gnem.apps.detection.triggerProcessing.SeismogramFeatures.FeatureType;
import llnl.gnem.dftt.core.util.PairT;
import llnl.gnem.dftt.core.util.SeriesMath;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class FeatureCollection {

    private final Map<StreamKey, SeismogramFeatures> featureList;

    public FeatureCollection() {
        featureList = new HashMap<>();
    }
    
    public FeatureCollection(List<PairT<StreamKey,SeismogramFeatures>> data)
    {
        featureList = new HashMap<>();
        for( PairT<StreamKey,SeismogramFeatures> pt : data){
            featureList.put(pt.getFirst(), pt.getSecond());
        }
    }
    
    public int size()
    {
        return featureList.size();
    }

    public void addFeature(StreamKey channel, SeismogramFeatures features) {
        featureList.put(channel, features);
    }

    public FeatureType[] getFeatureTypes() {
        return SeismogramFeatures.FeatureType.values();
    }

    public Double getMedianValue(FeatureType type) {
        ArrayList<Double> values = new ArrayList<>();
        featureList.values().stream().map((sk) -> sk.getValue(type)).filter((v) -> (v!=null)).forEachOrdered((v) -> {
            values.add(v);
        });
        
        return values.isEmpty() ? null : SeriesMath.getMedian(values);
    }

    public Collection<StreamKey> getKeys() {
        return new ArrayList<>(featureList.keySet());
    }

    public SeismogramFeatures getFeatures(StreamKey key) {
        return featureList.get(key);
    }

}
