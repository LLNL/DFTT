/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.streams;

import llnl.gnem.apps.detection.core.dataObjects.StreamSegment;



/**
 *
 * @author dodge
 */
public class DoNothingStreamModifier implements StreamModifier {
private  StreamSegment mySegment;
    @Override
    public void put(StreamSegment segment) {
        mySegment = segment;
    }

    @Override
    public StreamSegment take() {
        return mySegment;
    }
    
}
