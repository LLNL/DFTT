/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.apps.detection.core.signalProcessing;

import llnl.gnem.apps.detection.core.TestUtilsX;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dodge1
 */
public class STFAnalyzerTest {
    
    public STFAnalyzerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
   
    /**
     * Test of getWindow method, of class STFAnalyzer.
     */
    @Test
    public void testGetWindow() {
        System.out.println("getWindow");
        try{
        ArrayList<Float> windowValues = new TestUtilsX().getStoredFloatArray("/detection/core/signalProcessing/STFAnalyzerData.txt");
        STFAnalyzer instance = new STFAnalyzer(SymmetricWindowType.GAUSSIAN, 256, 3);
        float[] h = instance.getWindow();
        if( h.length != windowValues.size()){
            fail("Resource data length != generated data length!");
        }
        for (int j = 0; j < h.length; ++j) {
            assertEquals(h[j], windowValues.get(j), 0.0001f);
        }
        }
        catch(Exception ex){
            fail("Exception thrown while retrieving resources!");
        }
    }

    
}
