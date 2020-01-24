/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.dataObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import llnl.gnem.core.util.PairT;
import llnl.gnem.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class TransformedStreamSegment extends StreamSegment {

    private final Map< StreamKey, PairT< WaveformSegment, double[] > > lookupMap;
    private final ArrayList< PairT< WaveformSegment, double[] > >       dataList;
    
    

    public TransformedStreamSegment( Collection< PairT< WaveformSegment, double[] > > data ) {
        
        super( extractSegments(data) );
        lookupMap = new ConcurrentHashMap<>();
        for ( PairT<WaveformSegment, double[]> apair : data ) {
            lookupMap.put(apair.getFirst().getStreamKey(), apair);
        }
        dataList = new ArrayList<>(data);
    }
    
    
    
    private static Collection<WaveformSegment> extractSegments( Collection<PairT<WaveformSegment, double[]>> data )
    {
        Collection<WaveformSegment> result = new ArrayList<>();
        for( PairT<WaveformSegment, double[]> apair : data){
            result.add(apair.getFirst());
        }
        return result;
    }
    
    
    @Override
    public WaveformSegment getWaveformSegment( StreamKey key )
    {
        return lookupMap.get(key).getFirst();
    }
    
    
    public PairT<WaveformSegment, double[]> getSegmentAndDFT(int idx)
    {
        return dataList.get(idx);
    }
    
//    public double[] getDFT( StreamKey key)
//    {
//        return lookupMap.get(key).getSecond();
//    }
//    
//    public ArrayList< double[]> getDataDFTList()
//    {
//        ArrayList< double[]> result = new ArrayList<>();
//        for( int j = 0; j < this.getNumChannels(); ++j){
//            WaveformSegment seg = this.getWaveformSegment(j);
//            double[] v = lookupMap.get(seg.getStreamKey()).getSecond();
//            result.add(v);
//        }
//        return result;
//    }
//    
    
    
    public ArrayList< double[] > getDataDFTList( ArrayList< StreamKey > keys ) {
        ArrayList< double[]> result = new ArrayList<>();
        for(  int j = 0;  j < keys.size();  ++j ){
            double[] v = lookupMap.get( keys.get(j) ).getSecond();
            result.add(v);
}
        return result;
    }
}
