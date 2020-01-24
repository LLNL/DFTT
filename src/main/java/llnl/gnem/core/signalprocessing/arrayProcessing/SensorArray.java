package llnl.gnem.core.signalprocessing.arrayProcessing;
/**
 * Copyright (c) 2007  Regents of the University of California
 * All rights reserved
 * Author:  Dave Harris
 * Created: Nov 6, 2006
 * Time: 7:35:45 PM
 * Last Modified: January 20, 2006
 */

import Jampack.Zmat;
import java.io.PrintStream;

public interface SensorArray {

  public double[] getElementCoordinates( String elementName );

  public float[]  getXArray( String[] list );

  public float[]  getYArray( String[] list );

  public float[]  getZArray( String[] list );

  public float[]  getXArray();

  public float[]  getYArray();

  public float[]  getZArray();

  public Zmat     calculateTheoreticalSteeringVector( float[] s, float freq );

  public void     print( PrintStream ps );

}
