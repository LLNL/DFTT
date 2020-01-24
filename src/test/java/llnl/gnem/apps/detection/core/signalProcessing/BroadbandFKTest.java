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
public class BroadbandFKTest {

    private final float[] xnorth;
    private final float[] xeast;
    private final ArrayList<float[]> waveforms;

    public BroadbandFKTest() {
        xnorth = new float[5];
        xeast = new float[5];
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

        waveforms = new ArrayList<>();
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
     * Test of getFKSpectrum method, of class BroadbandFK.
     */
    @Test
    public void testGetFKSpectrum() {
        System.out.println("getFKSpectrum");

        try {
            ArrayList<Float> spectrumValues = new TestUtilsX().getStoredFloatArray("/detection/core/signalProcessing/fkSpectrum.txt");

            BroadbandFK instance = new BroadbandFK(0.5f, 32, xnorth, xeast, waveforms, 0.05f, 1.0f, 5.0f, 2.0f, waveforms);

           
            float[][] result = instance.getFKSpectrum();
            int m = 0;
            for (int j = 0; j < result.length; ++j) {
                float[] v = result[j];
                for (int k = 0; k < v.length; ++k) {
                    assertEquals(v[k], spectrumValues.get(m++), .0001f);
                }
            }
        } catch (Exception ex) {
            fail("Exception thrown while retrieving comparison values!");
        }
    }

    /**
     * Test of getEnergy method, of class BroadbandFK.
     */
    @Test
    public void testGetEnergy() {
        System.out.println("getEnergy");
        BroadbandFK instance = new BroadbandFK(0.5f, 32, xnorth, xeast, waveforms, 0.05f, 1.0f, 5.0f, 2.0f, waveforms);
        float expResult = 260.0F;
        float result = instance.getEnergy();
        assertEquals(expResult, result, 0.001);
    }


}
