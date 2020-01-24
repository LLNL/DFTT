/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.core.dataObjects;

import llnl.gnem.core.metadata.site.core.CssSite;
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
public class ArrayElementTest {
    
    ArrayElement element;
    public ArrayElementTest() {
        CssSite site = new CssSite("KS01", 1995001, 2286324, 30.0, 100.0,1.0, "KS01", "ss", "KSAR",1.0, 1.0);
        element = new ArrayElement(site, 1.0);
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
     * Test of delayInSeconds method, of class ArrayElement.
     */
    @Test
    public void testDelayInSeconds_floatArr() {
        System.out.println("delayInSeconds");
        float[] s = {0.1f,0};
        ArrayElement instance = element;
        double expResult = -0.10000000149011612;
        double result = instance.delayInSeconds(s);
    }

    /**
     * Test of delayInSeconds method, of class ArrayElement.
     */
    @Test
    public void testDelayInSeconds_float_float() {
        System.out.println("delayInSeconds");
        float sn = 0.0F;
        float se = 0.1F;
        ArrayElement instance = element;
        double expResult = -0.10000000149011612;
        double result = instance.delayInSeconds(sn, se);
        assertEquals(expResult, result, 0.00001);
    }

    /**
     * Test of delayInSeconds method, of class ArrayElement.
     */
    @Test
    public void testDelayInSeconds_3args() {
        System.out.println("delayInSeconds");
        float sn = 0.1F;
        float se = 0.0F;
        float sz = 0.0F;
        ArrayElement instance = element;
        double expResult = -0.10000000149011612;
        double result = instance.delayInSeconds(sn, se, sz);
        assertEquals(expResult, result, 0.00001);
    }

    /**
     * Test of toString method, of class ArrayElement.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        ArrayElement instance = element;
        String expResult = "KS01 lat: 30.000000 lon: 100.000000 elev: 1.000000 dn: 1.000000 de: 1.000000 dz: 1.000000";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
}
