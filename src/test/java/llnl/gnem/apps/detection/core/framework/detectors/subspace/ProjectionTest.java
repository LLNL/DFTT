/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.framework.detectors.subspace;

import Jampack.JampackException;
import Jampack.JampackParameters;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dodge1
 */
public class ProjectionTest {
  //  private final Projection projection;

    public ProjectionTest() throws JampackException {
        try {
            if (JampackParameters.getBaseIndex() != 0) {
                JampackParameters.setBaseIndex(0);
            }
        } catch (JampackException ex) {
            Logger.getLogger(ProjectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<float[][]> oldTemplate = new ArrayList< float[][]>();

        float[][] x = new float[2][20];
        x[0][0] = 0.5f;
        x[1][1] = 0.5f;
        x[0][2] = -0.5f;
        x[1][3] = 0.5f;
        oldTemplate.add(x);
        x = new float[2][20];
        x[0][10] = 0.5f;
        x[1][11] = 0.5f;
        x[0][12] = -0.5f;
        x[1][13] = 0.5f;
        oldTemplate.add(x);

        ArrayList<float[][]> newTemplate = new ArrayList< float[][]>();

        x = new float[2][20];
        x[0][5] = 0.5f;
        x[1][6] = 0.5f;
        x[0][7] = -0.5f;
        x[1][8] = 0.5f;
        newTemplate.add(x);
        x = new float[2][20];
        x[0][15] = 0.5f;
        x[1][16] = 0.5f;
        x[0][17] = -0.5f;
        x[1][18] = 0.5f;
        newTemplate.add(x);

//        projection = new Projection(oldTemplate, newTemplate, 2);
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

    @Test
    public void testPlaceholder() {
        // place holder for actual test. exception if test class contains no tests
        assertTrue(true);
    }

    /**
     * Test of getDecimatedDelay method, of class Projection.
     */
//    @Test
//    public void testGetDecimatedDelay() {
//        System.out.println("getDecimatedDelay");
//        Projection instance = projection;
//        int expResult = 5;
//        int result = instance.getDecimatedDelay();
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of getProjectionValue method, of class Projection.
//     */
//    @Test
//    public void testGetProjectionValue() {
//        System.out.println("getProjectionValue");
//        Projection instance = projection;
//        float expResult = 1.0F;
//        float result = instance.getProjectionValue();
//        assertEquals(expResult, result, 0.0);
//    }
// 
}
