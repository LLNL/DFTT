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
package llnl.gnem.apps.detection.core.dataObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import llnl.gnem.dftt.core.util.PairT;
import llnl.gnem.dftt.core.util.StreamKey;

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
