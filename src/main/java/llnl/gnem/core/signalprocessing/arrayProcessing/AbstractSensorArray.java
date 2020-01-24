package llnl.gnem.core.signalprocessing.arrayProcessing;
/**
 * Copyright (c) 2007  Regents of the University of California
 * All rights reserved
 * Author:  Dave Harris
 * Created: Nov 6, 2006
 * Time: 7:09:59 PM
 * Last Modified: Nov 6, 2006
 */

import Jampack.Zmat;
import Jampack.Znum;

import java.io.PrintStream;

public abstract class AbstractSensorArray implements SensorArray {

  protected String   name;
  protected int      nch;

  protected double[] x;            // element position east in kilometers
  protected double[] y;            // element position north in kilometers
  protected double[] z;            // element depth compared to datum in kilometers

  protected double[] lat;          // element latitude in degrees
  protected double[] lon;          // element longitude in degrees
  protected double[] elev;         // element elevation in kilometers

  protected double   referenceLatitude;
  protected double   referenceLongitude;
  protected double   referenceElevation;

  protected String[] elementNames;



  public AbstractSensorArray( int nch, String name ) {

    this.name = new String( name );
    this.nch  = nch;

    x    = new double[nch];
    y    = new double[nch];
    z    = new double[nch];
    lat  = new double[nch];
    lon  = new double[nch];
    elev = new double[nch];

    elementNames = new String[nch];

  }



  public double[] getElementCoordinates( String elementName ) {

    double[] retval = null;
    for ( int i = 0; i < elementNames.length; i++ ) {
      if ( elementName.indexOf( elementNames[i] ) > -1 ) {
        retval    = new double[3];
        retval[0] = x[i];
        retval[1] = y[i];
        retval[2] = z[i];
        break;
      }
    }

    return retval;
  }


  public void print( PrintStream ps ) {
    ps.println( name );
    ps.println( "  " + ( (float) referenceLatitude ) + "  " + ( (float) referenceLongitude ) +
                "  " + ( (float) referenceElevation ) );
    ps.println();
    for ( int i = 0;  i < elementNames.length;  i++ ) {
      ps.println( "  " + elementNames[i] + "  " + ( (float) x[i]    )
                                         + "  " + ( (float) y[i]    )
                                         + "  " + ( (float) z[i] ) );
    }
  }


  protected void computeXYZ() {
    double scaleNorth = 111.12;
    double scaleEast  = 111.12 * Math.cos( Math.PI/180.0 * referenceLatitude );
    for ( int i = 0;  i < nch;  i++ ) {
      x[i] = scaleEast  * ( lon[i] - referenceLongitude );
      y[i] = scaleNorth * ( lat[i] - referenceLatitude );
      z[i] = - ( elev[i] - referenceElevation );
    }
  }



  public float[] getXArray( String[] list ) {

    float[] retval = new float[ list.length ];

    for ( int j = 0;  j < list.length;  j++ ) {
      retval[j] = 9999.99f;
      for ( int i = 0; i < elementNames.length; i++ ) {
        if ( list[j].indexOf( elementNames[i] ) > -1 ) {
          retval[j]    = (float) x[i];
          break;
        }
      }
    }

    return retval;
  }



  public float[] getXArray() {
    float[] retval = new float[ nch ];
    for ( int i = 0;  i < nch;  i++ ) retval[i] = (float) x[i];
    return retval;
  }



  public float[] getYArray() {
    float[] retval = new float[ nch ];
    for ( int i = 0;  i < nch;  i++ ) retval[i] = (float) y[i];
    return retval;
  }



  public float[] getZArray() {
    float[] retval = new float[ nch ];
    for ( int i = 0;  i < nch;  i++ ) retval[i] = (float) z[i];
    return retval;
  }



  public float[] getYArray( String[] list ) {

    float[] retval = new float[ list.length ];

    for ( int j = 0;  j < list.length;  j++ ) {
      retval[j] = 9999.99f;
      for ( int i = 0; i < elementNames.length; i++ ) {
        if ( list[j].indexOf( elementNames[i] ) > -1 ) {
          retval[j]    = (float) y[i];
          break;
        }
      }
    }

    return retval;
  }



  public float[] getZArray( String[] list ) {

    float[] retval = new float[ list.length ];

    for ( int j = 0;  j < list.length;  j++ ) {
      retval[j] = 9999.99f;
      for ( int i = 0; i < elementNames.length; i++ ) {
        if ( list[j].indexOf( elementNames[i] ) > -1 ) {
          retval[j]    = (float) z[i];
          break;
        }
      }
    }

    return retval;
  }



  public Zmat calculateTheoreticalSteeringVector( float[] s, float freq ) {
    return calculateTheoreticalSteeringVector( s, freq, x, y, z );
  }



  public Zmat calculateTheoreticalSteeringVector( float[] s, float freq, double[] xs, double[] ys, double[] zs ) {
    int ns = xs.length;
    Zmat e = new Zmat( ns, 1 );
    double scale = 1.0 / Math.sqrt( ns );
    for ( int ich = 0;  ich < ns;  ich++ ) {
      float delay  = (float) ( s[0]*xs[ich] + s[1]*ys[ich] + s[2]*zs[ich] );
//      float delay = s[0]*x[ich] + s[1]*y[ich];                 // neglecting elevation corrections
      double phase = 2.0*Math.PI*freq*delay;
      e.put( ich, 0, new Znum( scale*Math.cos(phase), scale*Math.sin(phase) ) );
    }
    return e;
  }

}
