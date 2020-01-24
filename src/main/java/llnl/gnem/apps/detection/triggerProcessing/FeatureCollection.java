/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.triggerProcessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import llnl.gnem.apps.detection.triggerProcessing.SeismogramFeatures.FeatureType;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.SeriesMath;
import llnl.gnem.core.util.StreamKey;

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
