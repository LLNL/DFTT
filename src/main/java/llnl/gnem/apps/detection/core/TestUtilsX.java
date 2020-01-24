/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package llnl.gnem.apps.detection.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author dodge1
 */
public class TestUtilsX {
    
    public ArrayList<Float> getStoredFloatArray(String resourceName) throws IOException {

        InputStream in = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        ArrayList<Float> values = new ArrayList<Float>();
        try {
            in = getClass().getResourceAsStream(resourceName);
            isr = new InputStreamReader(in);
            br = new BufferedReader(isr);

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                values.add(Float.parseFloat(line.trim()));
            }
            return values;
        } finally {
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }
}
