/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2022 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.apps.detection.core.windowRevision;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;


import com.oregondsp.signalProcessing.Sequence;
import com.oregondsp.signalProcessing.filter.iir.Butterworth;
import com.oregondsp.signalProcessing.filter.iir.IIRFilter;
import com.oregondsp.signalProcessing.filter.iir.PassbandType;

public class TestWindowSelection {
/*	
	public static void main( String[] args ) {
		
		String base = "G:/DATA/IncorrectTimingExamples/12074";
		
		String[] fileList = { "PD01_SHZ.sac", "PD02_SHZ.sac", "PD03_SHZ.sac", "PD04_SHZ.sac", 
	              "PD05_SHZ.sac", "PD06_SHZ.sac", "PD07_SHZ.sac", "PD08_SHZ.sac", 
	              "PD09_SHZ.sac", "PD10_SHZ.sac", "PD11_SHZ.sac", "PD12_SHZ.sac", 
	              "PD13_SHZ.sac", "PD32_SHZ.sac" };
//		String[] fileList = { "NV01_SHZ.sac", "NV02_SHZ.sac", "NV03_SHZ.sac", "NV04_SHZ.sac", 
//	              			  "NV05_SHZ.sac", "NV06_SHZ.sac", "NV07_SHZ.sac", "NV08_SHZ.sac", 
//	              			  "NV09_SHZ.sac", "NV10_SHZ.sac", "NV11_SHZ.sac" };
		
		int nch                  = fileList.length;
		int npts                 = 7001;
		int analysisWindowLength = 200;
		int minimumWindowLength  = 500;
		
		ArrayList< float[][] > X = new ArrayList<>();
		
		Path dir = Paths.get( base );
		
		IIRFilter F = new Butterworth( 4, PassbandType.BANDPASS, 1.0, 5.0, 0.025 );
		
		try( DirectoryStream<Path> stream = Files.newDirectoryStream( dir ) ) {
			
			for ( Path entry : stream ) {
				
				if ( Files.isDirectory( entry ) ) {
				
					float[][] x = new float[ nch ][ npts ];
				
					for ( int ich = 0;  ich < nch;  ich++ ) {
						SACFileChannel sfc = SACFileChannel.fromStringPath( entry.toString() + "/" + fileList[ich], StandardOpenOption.READ );
						sfc.read( x[ich] );
						Sequence.rmean( x[ich] );
						F.initialize();
						F.filter( x[ich] );
						sfc.close();
					}	
				
					X.add( x );
					
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int decrate        = 1;
		boolean refine     = true;
		float SNRThreshold = 1.5f;
		
		long t0 = System.currentTimeMillis();
		
		WindowSelector selector = new WindowSelector( X, nch, npts, analysisWindowLength, decrate, minimumWindowLength, refine, SNRThreshold );
		
		long t1 = System.currentTimeMillis();
		
		System.out.println( "Elapsed time:  " + ( (t1-t0) / 1000.0 ) );
		
		try {
			SACFileChannel sfc = SACFileChannel.fromStringPath( base + "/energyCapture0", StandardOpenOption.WRITE );
			sfc.getHeader().delta = 0.05f;
			sfc.write( selector.getEnergyCapture1() );
			sfc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			SACFileChannel sfc = SACFileChannel.fromStringPath( base + "/energyCapture1", StandardOpenOption.WRITE );
			sfc.getHeader().delta = 0.05f;
			sfc.write( selector.getEnergyCapture2() );
			sfc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int[] window = selector.getWindow();
		
		DecimalFormat DF1 = new DecimalFormat( "0.00000" );
		DecimalFormat DF2 = new DecimalFormat( "####0" );
		
		try {
			PrintStream ps = new PrintStream( new FileOutputStream( base + "/window.txt" ) );
			ps.println( DF1.format( selector.getEnergyCaptureThreshold() ) + "  " + DF2.format( window[0] ) + "  " + DF2.format( window[1] ) );
			ps.close();
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}

	}
	*/
}
