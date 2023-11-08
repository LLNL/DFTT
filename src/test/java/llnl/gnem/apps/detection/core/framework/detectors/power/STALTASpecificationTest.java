/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors.power;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge1
 */
public class STALTASpecificationTest {
    private final STALTASpecification spec;

    public STALTASpecificationTest() {
        ArrayList<StreamKey> staChanList = new ArrayList<>();
        staChanList.add(new StreamKey("KK01", "SHZ"));
        staChanList.add(new StreamKey("KK02", "SHZ"));
        staChanList.add(new StreamKey("KK03", "SHZ"));
        staChanList.add(new StreamKey("KK04", "SHZ"));
        staChanList.add(new StreamKey("KK05", "SHZ"));
        staChanList.add(new StreamKey("KK06", "SHZ"));
        staChanList.add(new StreamKey("KK07", "SHZ"));
        staChanList.add(new StreamKey("KK08", "SHZ"));
        staChanList.add(new StreamKey("KK09", "SHZ"));

        spec = new STALTASpecification(
                25.0f,
                20.0f,
                staChanList,
                2.0f,
                20.0f,
                1.0f,
                true);
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
     * Test of getSTADuration method, of class STALTASpecification.
     */
    @Test
    public void testGetSTADuration() {
        System.out.println("getSTADuration");
        STALTASpecification instance = spec;
        float expResult = 2.0F;
        float result = instance.getSTADuration();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getLTADuration method, of class STALTASpecification.
     */
    @Test
    public void testGetLTADuration() {
        System.out.println("getLTADuration");
        STALTASpecification instance = spec;
        float expResult = 20.0F;
        float result = instance.getLTADuration();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getGapDuration method, of class STALTASpecification.
     */
    @Test
    public void testGetGapDuration() {
        System.out.println("getGapDuration");
        STALTASpecification instance = spec;
        float expResult = 1.0F;
        float result = instance.getGapDuration();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of spawningEnabled method, of class STALTASpecification.
     */
    @Test
    public void testSpawningEnabled() {
        System.out.println("spawningEnabled");
        STALTASpecification instance = spec;
        boolean expResult = true;
        boolean result = instance.spawningEnabled();
        assertEquals(expResult, result);
    }

}
