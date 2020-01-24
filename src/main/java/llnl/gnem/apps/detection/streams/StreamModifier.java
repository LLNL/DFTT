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
public interface StreamModifier {
    void put(StreamSegment segment)  throws InterruptedException;
    StreamSegment take()  throws InterruptedException;
}
