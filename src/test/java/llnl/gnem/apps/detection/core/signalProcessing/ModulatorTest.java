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
public class ModulatorTest {
    
    public ModulatorTest() {
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
     * Test of modulate method, of class Modulator.
     */
    @Test
    public void testModulate_floatArrArr() {
        System.out.println("modulate");
        float[] x = new float[100];
        float[] y = new float[100];
        for (int i = 0; i < 100; i++) {
            x[i] = 0.0f;
            y[i] = 1.0f;
        }
        float[][] z = new float[2][];
        z[0] = x;
        z[1] = y;
        Modulator mod = new Modulator(1, 37, -1);
        mod.modulate(z);
       
        try{
        ArrayList<Float> xList = new TestUtilsX().getStoredFloatArray("/detection/core/signalProcessing/ModulatorX.txt");
        ArrayList<Float> yList = new TestUtilsX().getStoredFloatArray("/detection/core/signalProcessing/ModulatorY.txt");
            for (int j = 0; j < 100; j++) {
                assertEquals(x[j], xList.get(j), 0.0001f);
                assertEquals(y[j], yList.get(j), 0.0001f);
            }
        
        }
        catch( Exception ex )
        {
            fail("Failed retrieving resources.");
        }
        
    }

   
    
}
