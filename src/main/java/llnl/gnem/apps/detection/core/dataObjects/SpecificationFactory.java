/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.apps.detection.core.dataObjects;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import llnl.gnem.apps.detection.core.framework.detectors.array.ArrayDetectorSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.bulletin.BulletinSpecification;
import llnl.gnem.apps.detection.core.framework.detectors.power.STALTASpecification;
import llnl.gnem.apps.detection.core.framework.detectors.subspace.SubspaceSpecification;

public class SpecificationFactory {

    private static final String DETECTOR_TYPE_KEY = "detectorType";

    public static DetectorSpecification getSpecification( String filename ) throws FileNotFoundException, IOException, ParseException, SQLException {
        try ( FileInputStream stream = new FileInputStream(filename) ) {
            return getSpecification(stream);
        }
    }

    public static DetectorSpecification getSpecification(InputStream stream) throws IOException, ParseException, SQLException {

        DetectorSpecification retval        = null;
        DetectorType          detType       = null;
        boolean               fileTypeFound = false;
        InputStreamReader     isr           = null;
        BufferedReader        br            = null;
        StringBuilder         sb            = new StringBuilder();
        
        try {
            isr = new InputStreamReader(stream);
            br  = new BufferedReader(isr);

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
                sb.append("\n");

                if (        line.contains( DETECTOR_TYPE_KEY )  &&  line.contains( DetectorType.STALTA.getName() ) ) {
                    detType = DetectorType.STALTA;
                    fileTypeFound = true;
                } else if ( line.contains( DETECTOR_TYPE_KEY )  &&  line.contains( DetectorType.SUBSPACE.getName() ) ) {
                    detType = DetectorType.SUBSPACE;
                    fileTypeFound = true;
                } else if ( line.contains( DETECTOR_TYPE_KEY )  &&  line.contains( DetectorType.ARRAYPOWER.getName() ) ) {
                    detType = DetectorType.ARRAYPOWER;
                    fileTypeFound = true;
                } else if ( line.contains( DETECTOR_TYPE_KEY )  &&  line.contains( DetectorType.BULLETIN.getName() ) ) {
                    detType = DetectorType.BULLETIN;
                    fileTypeFound = true;
                } else if ( line.contains( DETECTOR_TYPE_KEY )  &&  line.contains( DetectorType.FSTATISTIC.getName() ) ) {
                    detType = DetectorType.FSTATISTIC;
                    fileTypeFound = true;
                }

            }
        } finally {
            if (br != null) {
                br.close();

            }
            if (isr != null) {
                isr.close();
            }
        }

        if ( fileTypeFound ) {
            byte[] bytes = sb.toString().getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
            switch ( detType ) {
                case STALTA:
                    retval = new STALTASpecification( bais );
                    break;
                case SUBSPACE:
                    retval = new SubspaceSpecification( bais );
                    break;
                case ARRAYPOWER:
                    retval = new ArrayDetectorSpecification( bais, false, "", -1 );
                    break;
                case BULLETIN:
                    retval = new BulletinSpecification( bais );
                    break;
                case FSTATISTIC:
                    throw new UnsupportedOperationException( "FStatistic not implemented" );
            }

        } else {
            throw new IllegalStateException("Invalid detector type was specified !");
        }

        return retval;
    }
}
