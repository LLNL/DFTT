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
