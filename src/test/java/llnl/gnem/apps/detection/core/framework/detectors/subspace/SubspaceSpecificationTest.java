/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors.subspace;

import java.util.ArrayList;
import llnl.gnem.core.util.StreamKey;
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
public class SubspaceSpecificationTest {
    private final SubspaceSpecification spec;
    
    public SubspaceSpecificationTest() {
        ArrayList< StreamKey> staChanList = new ArrayList< >();
        staChanList.add(new StreamKey("KK01", "SHZ"));
        staChanList.add(new StreamKey("KK02", "SHZ"));
        staChanList.add(new StreamKey("KK03", "SHZ"));
        staChanList.add(new StreamKey("KK04", "SHZ"));
        staChanList.add(new StreamKey("KK05", "SHZ"));
        staChanList.add(new StreamKey("KK06", "SHZ"));
        staChanList.add(new StreamKey("KK07", "SHZ"));
        staChanList.add(new StreamKey("KK08", "SHZ"));
        staChanList.add(new StreamKey("KK09", "SHZ"));

        spec = new SubspaceSpecification(
                0.2f,
                3.0f,
                25.0f,
                50.0f,
                0.7f,
                staChanList);
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
     * Test of getEnergyCaptureThreshold method, of class SubspaceSpecification.
     */
    @Test
    public void testGetEnergyCaptureThreshold() {
        System.out.println("getEnergyCaptureThreshold");
        SubspaceSpecification instance = spec;
        float expResult = 0.7F;
        float result = instance.getEnergyCaptureThreshold();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of spawningEnabled method, of class SubspaceSpecification.
     */
    @Test
    public void testSpawningEnabled() {
        System.out.println("spawningEnabled");
        SubspaceSpecification instance = spec;
        boolean expResult = false;
        boolean result = instance.spawningEnabled();
        assertEquals(expResult, result);
    }


  
}
