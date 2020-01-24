/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.streams;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import llnl.gnem.apps.detection.core.dataObjects.TransformedStreamSegment;
import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;

/**
 *
 * @author dodge
 */
public class StreamSegmentCache {
    Queue<TransformedStreamSegment> segmentQueue;
    Queue<StreamSegment> rawQueue;
    
    public StreamSegmentCache()
    {
        segmentQueue = new ArrayBlockingQueue<>(2);
        rawQueue = new ArrayBlockingQueue<>(2);
    }

    public void push(TransformedStreamSegment segment,StreamSegment rawSegment) {
        if( segmentQueue.size() == 2 && rawQueue.size() == 2){
            segmentQueue.remove();
            rawQueue.remove();
        }
        segmentQueue.add(segment);
        rawQueue.add(rawSegment);
    }

    public StreamSegment getStream() {
        StreamSegment[] segmentArray = segmentQueue.toArray(new StreamSegment[1]);
        return new StreamSegment(segmentArray[0], segmentArray[1]);
    }

    public StreamSegment getRawStream() {
        StreamSegment[] segmentArray = rawQueue.toArray(new StreamSegment[1]);
        return new StreamSegment(segmentArray[0], segmentArray[1]);
    }

    public boolean canProduceConcatenatedSegment() {
        return segmentQueue.size() == 2;
    }
}
