/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.signalProcessing;

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
public class FKMeasurementTest {
    
    private FKMeasurement FKM;
    
    public FKMeasurementTest() {
        float[] xnorth = new float[5];
        float[] xeast = new float[5];
        xnorth[0] = 1.0f;
        xeast[0] = 0.0f;
        xnorth[1] = 0.0f;
        xeast[1] = 0.0f;
        xnorth[2] = -1.0f;
        xeast[2] = 0.0f;
        xnorth[3] = 0.0f;
        xeast[3] = -1.0f;
        xnorth[4] = 0.0f;
        xeast[4] = 1.0f;

        ArrayList<float[]> waveforms = new ArrayList<float[]>();
        float[] waveform = new float[100];
        waveform[46] = 1.0f;
        waveforms.add(waveform);
        waveform = new float[100];
        waveform[50] = 1.0f;
        waveforms.add(waveform);
        waveform = new float[100];
        waveform[54] = 1.0f;
        waveforms.add(waveform);
        waveform = new float[100];
        waveform[54] = 1.0f;
        waveforms.add(waveform);
        waveform = new float[100];
        waveform[46] = 1.0f;
        waveforms.add(waveform);

      FKM = new FKMeasurement(0.5f, 32, xnorth, xeast, waveforms, 0.05f, 1.0f, 5.0f, 2.0f, waveforms);

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
     * Test of getSlownessEstimate method, of class FKMeasurement.
     */
    @Test
    public void testGetSlownessEstimate() {
        System.out.println("getSlownessEstimate");
        FKMeasurement instance = FKM;
        float[] expResult = {-0.0011922f ,0.0011922f};
        float[] result = instance.getSlownessEstimate();
        assertArrayEquals(expResult, result,.00001f);
    }

    /**
     * Test of getQuality method, of class FKMeasurement.
     */
    @Test
    public void testGetQuality() {
        System.out.println("getQuality");
        FKMeasurement instance = FKM;
        float expResult = 0.9726411F;
        float result = instance.getQuality();
        assertEquals(expResult, result, 0.0001);
    }
}
