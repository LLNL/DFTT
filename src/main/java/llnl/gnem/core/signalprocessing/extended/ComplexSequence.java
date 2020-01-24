package llnl.gnem.core.signalprocessing.extended;


import java.io.PrintStream;

/**
 * A ComplexSequence is a sequence of complex numbers that usually
 * represents a signal.  It uses two float arrrays to represent the
 * real and imaginary parts of the sequence.  The nth value of the
 * ComplexSequence is represented by realvalues[n] + imagvalues[n]*i.
 * <p></p>
 * Most of the interface for this class was written by Dave Harris
 * on 11/13/98.
 * <p></p>
 *
 * @author Timothy Paik
 *         written in June 2004
 */
public class ComplexSequence {

  ////////////////////////
  //   PRIVATE FIELDS   //
  ////////////////////////

  private float[] realvalues;
  private float[] imagvalues;

  //////////////////////
  //   CONSTRUCTORS   //
  //////////////////////


  /**
   * Constructs a ComplexSequence of length n.
   *
   * @param n - the length of the arrays in this ComplexSequence
   */
  public ComplexSequence( int n ) {
    realvalues = new float[n];
    imagvalues = new float[n];
  }


  /**
   * Constructs a ComplexSequence of length n containing n values
   * of x + 0i.
   *
   * @param n - the length of the arrays in this ComplexSequence
   * @param x - the value filled in the real-value array.
   */
  public ComplexSequence( int n, float x ) {

    realvalues = new float[n];
    imagvalues = new float[n];

    for ( int i = 0; i < n; i++ ) {
      realvalues[ i ] = x;
      imagvalues[ i ] = 0;
    }
  }


  /**
   * Constructs a Complex Sequence of length n where each element is
   * equal to real + imag*i.
   *
   * @param n    - the length of this ComplexSequence
   * @param real - the initial value of the real part of each element
   *             in this ComplexSequence
   * @param imag - the initial value of the imaginary part of each
   *             element in this ComplexSequence
   */
  public ComplexSequence( int n, float real, float imag ) {

    realvalues = new float[n];
    imagvalues = new float[n];

    for ( int i = 0; i < n; i++ ) {
      realvalues[ i ] = real;
      imagvalues[ i ] = imag;
    }
  }


  /**
   * Constructs a ComplexSequence about a float array.
   *
   * @param v - the float array representing the real values of this
   *          ComplexSequence
   */
  public ComplexSequence( float[] v ) {
    realvalues = v;
    imagvalues = new float[v.length];
  }


  /**
   * Constructs a ComplexSequence about two float arrays.
   *
   * @param u - the float array representing the real values of this
   *          ComplexSequence
   * @param v - the float array representing the imaginary values of
   *          this ComplexSequence
   * @throws SignalProcessingException - if the lengths of the arrays are different
   */
  public ComplexSequence( float[] u, float[] v ) throws SignalProcessingException {
    if ( u.length != v.length ) {
      throw new SignalProcessingException
          ( "Attempted to construct a ComplexSequence with float arrays of different length" );
    } else {
      realvalues = u;
      imagvalues = v;
    }
  }


  /**
   * Constructs a ComplexSequence using the values of a ComplexSequence.
   *
   * @param S - the DoubleComplexSequence containing the initial values of this
   *          DoubleComplexSequence
   */
  public ComplexSequence( ComplexDoubleSequence S ) {

    realvalues = new float[S.length()];
    imagvalues = new float[S.length()];

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = (float) S.getR( i );
      imagvalues[ i ] = (float) S.getI( i );
    }
  }


  /**
   * Constructs a ComplexSequence about another ComplexSequence.  Basically
   * a clone operation.
   *
   * @param S - the other ComplexSequence to clone.
   */
  public ComplexSequence( ComplexSequence S ) {

    realvalues = new float[ S.realvalues.length ];
    imagvalues = new float[ S.realvalues.length ];

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = S.realvalues[ i ];
      imagvalues[ i ] = S.imagvalues[ i ];
    }
  }


  /**
   * Constructs a ComplexSequence using the values of a Sequence.  The imaginary
   * values are set to 0.
   *
   * @param S - the Sequence containing the initial real values of this
   *          ComplexSequence
   */
  public ComplexSequence( RealSequence S ) {

    realvalues = new float[ S.length() ];
    imagvalues = new float[ S.length() ];

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = S.get( i );
      imagvalues[ i ] = 0;
    }
  }


  /**
   * Constructs a ComplexSequence using the values of a DoubleSequence.
   *
   * @param S - the DoubleSequence containing the initial real values of this
   *          ComplexSequence
   */
  public ComplexSequence( RealDoubleSequence S ) {

    realvalues = new float[S.length()];
    imagvalues = new float[S.length()];

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = (float) S.get( i );
      imagvalues[ i ] = 0;
    }
  }

  /////////////////////////
  //   OTHER FUNCTIONS   //
  /////////////////////////


  /**
   * Copies the values from a DoubleSequence into this ComplexSequence.
   *
   * @param ds - the DoubleSequence to get the values from.
   * @throws SignalProcessingException - if the length of the DoubleSequence is
   *                                   different from the length of this ComplexSequence
   */
  public void getValues( RealDoubleSequence ds ) throws SignalProcessingException {

    if ( realvalues.length != ds.length() )
      throw new SignalProcessingException
          ( "Attempted to copy values from DoubleSequence into ComplexSequence of different length" );

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = (float) ds.get( i );
      imagvalues[ i ] = 0;
    }
  }


  /**
   * Copies the values from a DoubleComplexSequence into this ComplexSequence.
   *
   * @param dcs - the DoubleComplexSequence to get the values from.
   * @throws SignalProcessingException - if the length of the DoubleComplexSequence is
   *                                   different from the length of this ComplexSequence
   */
  public void getValues( ComplexDoubleSequence dcs ) throws SignalProcessingException {

    if ( realvalues.length != dcs.length() )
      throw new SignalProcessingException
          ( "Attempted to copy values from DoubleComplexSequence into ComplexSequence of different length" );

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = (float) dcs.getR( i );
      imagvalues[ i ] = (float) dcs.getI( i );
    }
  }


  /**
   * Copies the values from a ComplexSequence into this ComplexSequence.
   *
   * @param cs - the ComplexSequence to get the values from.
   * @throws SignalProcessingException - if the length of the DoubleComplexSequence is
   *                                   different from the length of this ComplexSequence
   */
  public void getValues( ComplexSequence cs ) throws SignalProcessingException {

    if ( realvalues.length != cs.length() )
      throw new SignalProcessingException
          ( "Attempted to copy values from ComplexSequence into another ComplexSequence of different length" );

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = ( cs.get( i ) )[ 0 ];
      imagvalues[ i ] = ( cs.get( i ) )[ 1 ];
    }
  }


  /**
   * Copies the values from a Sequence into this ComplexSequence.
   *
   * @param s - the Sequence to get the values from.
   * @throws SignalProcessingException - if the length of the Sequence is
   *                                   different from the length of this ComplexSequence
   */
  public void getValues( RealSequence s ) throws SignalProcessingException {

    if ( realvalues.length != s.length() )
      throw new SignalProcessingException
          ( "Attempted to copy values from ComplexSequence into another ComplexSequence of different length" );

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = s.get( i );
      imagvalues[ i ] = 0;
    }
  }


  /**
   * Obtains values from a float array and places them into the real array of this
   * ComplexSequence. The imaginary parts of this ComplexSequence are reset
   * to 0.
   *
   * @param s - the float array carrying the new real values of this
   *          ComplexSequence
   * @throws SignalProcessingException - if the double array is not the same length as
   *                                   this ComplexSequence
   */
  public void getValues( float[] s ) throws SignalProcessingException {

    if ( realvalues.length != s.length )
      throw new SignalProcessingException
          ( "Attempted to copy values from float array into ComplexSequence of different length" );

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = s[ i ];
      imagvalues[ i ] = 0;
    }

  }


  /**
   * Obtains values from two float arrays and places them into the arrays of this
   * ComplexSequence.
   *
   * @param s - the float array carrying the new real values of this
   *          ComplexSequence
   * @param t - the float array carrying the new imaginary values of this
   *          ComplexSequence
   * @throws SignalProcessingException - if either of the float arrays is not the
   *                                   same length as this ComplexSequence
   */
  public void getValues( float[] s, float[] t ) throws SignalProcessingException {

    if ( realvalues.length != s.length )
      throw new SignalProcessingException
          ( "Attempted to copy values from float array into ComplexSequence of different length" );

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = s[ i ];
      imagvalues[ i ] = t[ i ];
    }

  }


  /**
   * Accesses the nth value of this ComplexSequence. The first value
   * of the array is the real part, the second is the imaginary part.
   *
   * @param n - the index of the value returned.
   * @return a float[] representing the nth value of the ComplexSequence.
   */
  public float[] get( int n ) {
    float[] cval = new float[2];
    cval[ 0 ] = realvalues[ n ];
    cval[ 1 ] = imagvalues[ n ];
    return cval;
  }


  /**
   * Accesses the real part of the nth value of this ComplexSequence.
   *
   * @param n - the index of the complex value
   * @return a float representing the real part of the nth value
   *         of this DoubleComplexSequence.
   */
  public float getR( int n ) {
    return realvalues[ n ];
  }


  /**
   * Accesses the imaginary part of the nth value of this ComplexSequence.
   *
   * @param n - the index of the complex value
   * @return a float representing the imaginary part of the nth value of this
   *         ComplexSequence.
   */
  public float getI( int n ) {
    return imagvalues[ n ];
  }


  /**
   * Gets the array representing the real values of this ComplexSequence.
   *
   * @return the array representing the reals of this ComplexSequence.
   */
  public float[] getRealArray() {
    return realvalues;
  }


  /**
   * Gets the array representing the imaginary values of this
   * ComplexSequence.
   *
   * @return the array representing the imaginary values of this
   *         ComplexSequence.
   */
  public float[] getImagArray() {
    return imagvalues;
  }


  /**
   * Gets the length of this ComplexSequence.
   *
   * @return the length of this ComplexSequence.
   */
  public int length() {
    return realvalues.length;
  }


  /**
   * Returns the value of the modulus of the largest complex element in
   * this ComplexSequence.
   *
   * @return the modulus of the largest element in this ComplexSequence
   */
  public float extremum() {
    float smax = 0.0f;
    float sabs = 0.0f;
    for ( int i = 0; i < realvalues.length; i++ ) {
      float x = realvalues[ i ];
      float y = imagvalues[ i ];
      sabs = x * x + y * y;
      if ( sabs > smax ) {
        smax = sabs;
      }
    }
    return (float) Math.sqrt( smax );
  }


  /**
   * Returns the index of the largest complex element in this ComplexSequence.
   *
   * @return the index of the complex element with the largest modulus in this
   *         ComplexSequence.
   */
  public int extremumIndex() {
    float smax = 0.0f;
    float sabs = 0.0f;
    int index = 0;
    for ( int i = 0; i < realvalues.length; i++ ) {
      float x = realvalues[ i ];
      float y = imagvalues[ i ];
      sabs = x * x + y * y;
      if ( sabs > smax ) {
        smax = sabs;
        index = i;
      }
    }
    return index;
  }


  /**
   * Subtracts the mean value of this ComplexSequence from every element
   * in this ComplexSequence.
   */
  public void rmean() {

    float rmean = 0.0f;
    float imean = 0.0f;
    for ( int i = 0; i < realvalues.length; i++ ) {
      rmean += realvalues[ i ];
      imean += imagvalues[ i ];
    }
    rmean /= realvalues.length;
    imean /= imagvalues.length;

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] -= rmean;
      imagvalues[ i ] -= imean;
    }

  }


  /**
   * Zeroes out this ComplexSequence.
   */
  public void zero() {

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = 0.0f;
      imagvalues[ i ] = 0.0f;
    }

  }


  /**
   * Modifies this ComplexSequence by scaling this ComplexSequence
   * by a float value.
   *
   * @param a - the value to scale this ComplexSequence by.
   */
  public void scaleBy( float a ) {

    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] *= a;
      imagvalues[ i ] *= a;
    }

  }


  /**
   * Modifies this ComplexSequence by reversing this ComplexSequence.
   */
  public void reverse() {

    float tmp, tmp2;
    int j = realvalues.length - 1;
    int i = 0;
    while ( true ) {
      if ( j <= i ) break;
      tmp = realvalues[ i ];
      tmp2 = imagvalues[ i ];
      realvalues[ i ] = realvalues[ j ];
      imagvalues[ i ] = imagvalues[ j ];
      realvalues[ j ] = tmp;
      imagvalues[ j ] = tmp2;
      i++;
      j--;
    }

  }


  /**
   * Modifies this ComplexSequence by shifting this ComplexSequence over
   * a certain amount of samples and replacing the empty values with zeroes.
   *
   * @param shift - the amount of samples to shift this ComplexSequence by.
   *              A positive shift indicates a shift right.
   */
  public void zshift( int shift ) {

    int n;
    int srcptr, dstptr;
    if ( Math.abs( shift ) > realvalues.length ) {
      zero();
    } else if ( shift < 0 ) {                                    // left shift
      n = realvalues.length + shift;
      dstptr = 0;
      srcptr = -shift;
      for ( int i = 0; i < n; i++ ) {
        realvalues[ dstptr ] = realvalues[ srcptr ];
        imagvalues[ dstptr++ ] = imagvalues[ srcptr++ ];
      }

      zero( realvalues, realvalues.length + shift, -shift );     // zero high end
      zero( imagvalues, imagvalues.length + shift, -shift );     // zero high end
    } else if ( shift > 0 ) {                                    // right shift
      n = realvalues.length - shift;
      dstptr = realvalues.length - 1;
      srcptr = dstptr - shift;
      for ( int i = 0; i < n; i++ ) {
        realvalues[ dstptr ] = realvalues[ srcptr ];
        imagvalues[ dstptr-- ] = imagvalues[ srcptr-- ];
      }

      zero( realvalues, 0, shift );                             // zero low end
      zero( imagvalues, 0, shift );                             // zero low end

    }
  }


  /**
   * Modifies this ComplexSequence by shifting it over a certain
   * amount of samples. Any values shifted "off" the sequence reappear on the
   * other end of this ComplexSequence.
   *
   * @param shift - the amount of samples to shift this ComplexSequence by.
   *              A positive shift indicates a shift right.
   */
  public void cshift( int shift ) {

    //  Arguments:
    //  ----------

    //  int shift           number of samples to shift.

    //                      a negative number indicates a shift left.
    //                      a positive number indicates a shift right.
    //                      zero indicates no shift.

    int bsize = Math.abs( shift );
    float[] buffer = new float[bsize];
    float[] buffer2 = new float[bsize];
    int n = realvalues.length;

    // two cases - right and left shifts


    int i, j;
    if ( shift > 0 ) {                      // right shift

      shift = shift % n;                    // prevent extraneous transfers

      j = n - shift;
      for ( i = 0; i < shift; i++ ) {
        buffer[ i ] = realvalues[ j ];
        buffer2[ i ] = imagvalues[ j++ ];
      }

      j = n - 1;
      i = j - shift;
      while ( i >= 0 ) {
        realvalues[ j ] = realvalues[ i ];
        imagvalues[ j-- ] = imagvalues[ i-- ];
      }
      for ( i = 0; i < shift; i++ ) {
        realvalues[ i ] = buffer[ i ];
        imagvalues[ i ] = buffer2[ i ];
      }
    } else if ( shift < 0 ) {                 // left shift

      shift = shift % n;                    // prevent extraneous transfers

      for ( i = 0; i < -shift; i++ ) {
        buffer[ i ] = realvalues[ i ];
        buffer2[ i ] = imagvalues[ i ];
      }

      j = 0;
      i = -shift;
      while ( i < n ) {
        realvalues[ j ] = realvalues[ i ];
        imagvalues[ j++ ] = imagvalues[ i++ ];
      }

      j = n + shift;
      for ( i = 0; i < -shift; i++ ) {
        realvalues[ j ] = buffer[ i ];
        imagvalues[ j++ ] = buffer2[ i ];
      }
    }

  }


  /**
   * Multiplies a subsequence of a ComplexSequence by a window beginning
   * at the index start and returns the windowed subsequence. Assumes the
   * ComplexSequence is equal to zero outside of its legal range.
   *
   * @param start-  the index at which window should start multiplying.
   * @param window- the ComplexSequence with which to multiply
   *                this ComplexSequence by to get the new ComplexSequence.
   * @return - a new ComplexSequence with the multiplied values
   *         in the window.
   */
  public ComplexSequence window( int start, ComplexSequence window ) {

    try {

      int n = window.length();
      float[] newrealvalues = new float[n];
      float[] newimagvalues = new float[n];

      // check for overlap - if none, return with zero subsequence

      if ( start < realvalues.length && start + n > 0 ) {
        int index0 = Math.max( 0, -start );
        int index1 = Math.min( realvalues.length - start, n );

        float[] windowrealvalues = window.realvalues;
        float[] windowimagvalues = window.imagvalues;

        for ( int i = index0; i < index1; i++ ) {
          newrealvalues[ i ] = ( realvalues[ i + start ] * windowrealvalues[ i ] ) -
              ( imagvalues[ i + start ] * windowimagvalues[ i ] );
          newimagvalues[ i ] = ( realvalues[ i + start ] * windowimagvalues[ i ] ) +
              ( imagvalues[ i + start ] * windowrealvalues[ i ] );
        }

        return new ComplexSequence( newrealvalues, newimagvalues );
      } else {
        return this;
      }

    } catch ( Exception e ) {
      System.err.println( "This error should never be seen." );
      e.printStackTrace();
      return null;
    }

  }


  /**
   * Aliases this ComplexSequence into another ComplexSequence
   * of length N. Does not modify this ComplexSequence.
   *
   * @param N - the length of the new aliased ComplexSequence
   * @return a new ComplexSequence of length N representing the aliased
   *         ComplexSequence
   */
  public ComplexSequence alias( int N ) {

    try {
      float[] newrealvalues = new float[N];
      float[] newimagvalues = new float[N];
      int index = 0;
      for ( int i = 0; i < realvalues.length; i++ ) {
        newrealvalues[ index ] += realvalues[ i ];
        newimagvalues[ index++ ] += imagvalues[ i ];
        if ( index == N ) index = 0;
      }
      return new ComplexSequence( newrealvalues, newimagvalues );
    } catch ( SignalProcessingException se ) {
      System.err.println( "alias(): This error should never be seen" );
      se.printStackTrace();
      return null;
    }

  }


  /**
   * Modifies this ComplexSequence by stretching it out by a factor n.
   * Fills the extra spaces in the ComplexSequence with zeroes.
   *
   * @param factor - the factor by which to stretch this ComplexSequence out by
   */
  public void stretch( int factor ) {
    int n = realvalues.length;
    float[] sptr = new float[factor * n];
    float[] iptr = new float[factor * n];
    zero( sptr, 0, factor * n );
    for ( int i = n - 1; i >= 0; i-- ) {
      sptr[ factor * i ] = realvalues[ i ];
      iptr[ factor * i ] = imagvalues[ i ];
    }
    realvalues = sptr;
    imagvalues = iptr;
  }


  /**
   * Decimates this ComplexSequence by choosing every nth sample, where n = factor.
   * Modifies this ComplexSequence. Disposes of the original arrays that
   * hold the original values for this ComplexSequence.
   *
   * @param factor - the factor by which to decimate this ComplexSequence by.
   */
  public void decimate( int factor ) {

    int dlen = realvalues.length / factor;
    if ( dlen * factor < realvalues.length ) dlen++;
    float[] dptr1 = new float[dlen];
    float[] dptr2 = new float[dlen];
    for ( int i = 0; i < dlen; i++ ) {
      dptr1[ i ] = realvalues[ i * factor ];
      dptr2[ i ] = imagvalues[ i * factor ];
    }
    realvalues = dptr1;
    imagvalues = dptr2;

  }


  /**
   * Modifies this ComplexSequence by cropping out all values outside of the
   * interval specified by the input values. Returns true if the cut is
   * successful, false otherwise.
   *
   * @param i1 - the left index of the cutting region.
   * @param i2 - the right index of the cutting region.
   * @return true if the cut is successful (if the region is inside the bounds of the
   *         original region)
   */
  public boolean cut( int i1, int i2 ) {

    if ( i2 < i1 ) return false;
    if ( i1 < 0 ) return false;
    if ( i2 > realvalues.length - 1 ) return false;

    int n = i2 - i1 + 1;
    float[] newrealvalues = new float[n];
    float[] newimagvalues = new float[n];
    for ( int i = 0; i < n; i++ ) {
      newrealvalues[ i ] = realvalues[ i + i1 ];
      newimagvalues[ i ] = imagvalues[ i + i1 ];
    }
    realvalues = newrealvalues;
    imagvalues = newimagvalues;

    return true;
  }


  /**
   * Modifies this ComplexSequence by subtracting another ComplexSequence
   * from it. Lines up the ComplexSequences at 0 and subtracts every index
   * up to the length of the shorter ComplexSequence.
   *
   * @param S - the ComplexSequence to subtract from this ComplexSequence.
   */
  public void minusEquals( ComplexSequence S ) {

    int n = Math.min( realvalues.length, S.realvalues.length );
    for ( int i = 0; i < n; i++ ) {
      realvalues[ i ] -= S.realvalues[ i ];
      imagvalues[ i ] -= S.imagvalues[ i ];
    }

  }


  /**
   * Modifies this ComplexSequence by adding another ComplexSequence to it.
   * Lines up the ComplexSequences at 0 and adds every index up to the length
   * of the shorter ComplexSequence.
   *
   * @param S - the ComplexSequence to add to this ComplexSequence.
   */
  public void plusEquals( ComplexSequence S ) {

    int n = Math.min( realvalues.length, S.realvalues.length );
    for ( int i = 0; i < n; i++ ) {
      realvalues[ i ] += S.realvalues[ i ];
      imagvalues[ i ] += S.imagvalues[ i ];
    }

  }


  /**
   * Modifies this ComplexSequence by dividing it by another ComplexSequence.
   * Lines up the ComplexSequences at 0 and divides every index up to the length
   * of the shorter ComplexSequence.
   *
   * @param S - the ComplexSequence to divide this ComplexSequence by.
   */
  public void divideBy( ComplexSequence S ) {

    int n = Math.min( realvalues.length, S.realvalues.length );
    float a, b, c, d, denom;
    for ( int i = 0; i < n; i++ ) {
      a = realvalues[ i ];
      b = imagvalues[ i ];
      c = S.realvalues[ i ];
      d = S.imagvalues[ i ];
      denom = c*c + d*d;
      realvalues[ i ] = ( a*c - b*d ) / denom;
      imagvalues[ i ] = ( b*c + a*d ) / denom;
    }

  }


  /**
   * Finds the dot product between this ComplexSequence and another
   * ComplexSequence. If the ComplexSequences have different lengths,
   * then the dot product takes the dot product of the overlapping region
   * of the two ComplexSequences, lining up the ComplexSequences at index
   * 0.
   *
   * @param S - the other ComplexSequence in the dot product.
   * @return the value of the dot product in a float array form: the first
   *         value is the real part, the second is the imaginary
   */
  public float[] dotprod( ComplexSequence S ) {

    int n = Math.min( realvalues.length, S.realvalues.length );
    float realval = 0.0f;
    float imagval = 0.0f;
    float[] realx = realvalues;
    float[] realy = S.realvalues;
    float[] imagx = imagvalues;
    float[] imagy = S.imagvalues;

    for ( int i = 0; i < n; i++ ) {
      realval += ( realx[ i ] * realy[ i ] - imagx[ i ] * imagy[ i ] );
      imagval += ( realx[ i ] * imagy[ i ] + imagx[ i ] * realy[ i ] );
    }

    float[] retvals = new float[2];
    retvals[ 0 ] = realval;
    retvals[ 1 ] = imagval;
    return retvals;

  }


  /**
   * Finds the Sequence containing the square of the modulus of each
   * value in this ComplexSequence.
   *
   * @return a new Sequence containing the square of the modulus of each
   *         value in this ComplexSequence
   */
  public RealSequence sqr() {

    float[] tmp = new float[realvalues.length];
    for ( int i = 0; i < realvalues.length; i++ ) {
      float a = realvalues[ i ];
      float b = imagvalues[ i ];

      tmp[ i ] = ( a * a + b * b );
    }

    return new RealSequence( tmp );
  }


  /**
   * Finds the Sequence containing the modulus of each value in this
   * ComplexSequence.
   *
   * @return a new Sequence containing the modulus of each value in
   *         this ComplexSequence
   */
  public RealSequence modulus() {

    float[] arr = new float[realvalues.length];

    for ( int i = 0; i < realvalues.length; i++ ) {
      double a = realvalues[ i ];
      double b = imagvalues[ i ];

      arr[ i ] = (float) Math.sqrt( a * a + b * b );
    }

    return new RealSequence( arr );
  }


  /**
   * Modifies this ComplexSequence by padding it to a larger length with zero
   * values. If the new length is shorter than this ComplexSequence's length,
   * there is no change to this ComplexSequence.
   *
   * @param newlength - the new length for this ComplexSequence.
   */
  public void pad_to( int newlength ) {

    int n = realvalues.length;
    if ( newlength > n ) {
      float[] tmp1 = new float[newlength];
      float[] tmp2 = new float[newlength];
      for ( int i = 0; i < n; i++ ) {
        tmp1[ i ] = realvalues[ i ];
        tmp2[ i ] = imagvalues[ i ];
      }
      for ( int i = n; i < newlength; i++ ) {
        tmp1[ i ] = 0.0f;
        tmp2[ i ] = 0.0f;
      }
      realvalues = tmp1;
      imagvalues = tmp2;
    }

  }


  /**
   * Modifies this ComplexSequence by taking the discrete Fourier transform
   * of this ComplexSequence.
   */
  public void dft() {

    ComplexFFT fft = new ComplexFFT( realvalues.length, 1 );
    int n = 1;
    while ( n < realvalues.length ) {
      n *= 2;
    }
    pad_to( n );

    fft.cfft( realvalues, imagvalues );
  }


  /**
   * Modifies this ComplexSequence by taking the discrete Fourier transform of
   * this ComplexSequence. Saves time by reusing an old ComplexFFT. Does
   * not use the arbitrary radix stage of the ComplexFFT.
   *
   * @throws SignalProcessingException - if a ComplexFFT too small for this
   *                                   DoubleComplexSequence is passed as a parameter
   */
  public void dft( ComplexFFT fft ) throws SignalProcessingException {

    if ( realvalues.length > fft.length() )
      throw new SignalProcessingException( "ComplexFFTD is too small for DoubleComplexSequence" );

    pad_to( fft.length() );
    fft.cfft( realvalues, imagvalues );
  }


  /**
   * Modifies this ComplexSequence by taking the discrete Fourier transform of
   * this ComplexSequence. Uses the arbitrary radix stage of the ComplexFFTD.
   */
  public void dftRX( int radix ) {

    int newlength = realvalues.length;
    while ( newlength % radix != 0 )
      newlength++;

    ComplexFFT fft = new ComplexFFT( newlength / radix, radix );

    int n = 1;
    while ( n < newlength ) {
      n *= 2;
    }
    pad_to( n * radix );

    fft.cfftRX( realvalues, imagvalues );
  }


  /**
   * Modifies this ComplexSequence by taking the discrete Fourier transform of
   * this ComplexSequence. Uses the arbitrary radix stage of a pre-made ComplexFFT.
   *
   * @throws SignalProcessingException - if a ComplexFFT too small for this
   *                                   ComplexSequence is passed as a parameter
   */
  public void dftRX( ComplexFFT fft ) throws SignalProcessingException {
    if ( realvalues.length > fft.length() )
      throw new SignalProcessingException( "ComplexFFTD is too small for DoubleComplexSequence" );

    pad_to( fft.length() );
    fft.cfftRX( realvalues, imagvalues );
  }


  /**
   * Modifies this ComplexSequence by taking the inverse discrete Fourier
   * transform of this ComplexSequence. Does not have much meaning if the
   * ComplexSequence does not have a length that is a power of 2.
   */
  public void idft() {
    ComplexFFT fft = new ComplexFFT( realvalues.length, 1 );
    int n = 1;
    while ( n < realvalues.length ) {
      n *= 2;
    }
    pad_to( n );

    fft.cifft( realvalues, imagvalues );
  }


  /**
   * Modifies this ComplexSequence by taking the inverse discrete Fourier
   * transform of this ComplexSequence. Uses a premade FFT object to improve
   * speed.
   *
   * @param fft - the ComplexFFT object to take the inverse FFT of this ComplexSequence.
   */
  public void idft( ComplexFFT fft ) {
    int n = 1;
    while ( n < realvalues.length ) {
      n *= 2;
    }
    pad_to( n );

    fft.cifft( realvalues, imagvalues );
  }


  /**
   * Modifies this ComplexSequence by replacing each element in this ComplexSequence
   * by its conjugate.
   */
  public void conjugate() {
    for ( int i = 0; i < imagvalues.length; i++ ) {
      imagvalues[ i ] = -imagvalues[ i ];
    }
  }

     public void times(int c)
    {
        for ( int i = 0; i < realvalues.length; i++ )
        {
            realvalues[ i ] = realvalues[i] * (float) c;
            imagvalues[ i ] = imagvalues[i] * (float) c;
        }
    }
    public void times(float c)
    {
        for ( int i = 0; i < realvalues.length; i++ )
        {
            realvalues[ i ] = realvalues[i] * c;
            imagvalues[ i ] = imagvalues[i] * c;
        }
    }
    public void times(double c)
    {
        for ( int i = 0; i < realvalues.length; i++ )
        {
            realvalues[ i ] = realvalues[i] * (float) c;
            imagvalues[ i ] = imagvalues[i] * (float) c;
        }
    }

  /**
   * Modifies this ComplexSequence by aliasing it by a factor.
   *
   * @param factor - the factor by which to alias this ComplexSequence by
   */
  public void dftAlias( int factor ) {
    ComplexSequence cs = alias( realvalues.length / factor );
    float[] newreals = new float[realvalues.length / factor];
    float[] newimags = new float[realvalues.length / factor];
    for ( int i = 0; i < newreals.length; i++ ) {
      newreals[ i ] = cs.realvalues[ i ] / factor;
      newimags[ i ] = cs.imagvalues[ i ] / factor;
    }
    realvalues = newreals;
    imagvalues = newimags;
  }


  /**
   * Returns a ComplexSequence representing the product between two ComplexSequences.
   *
   * @param x - the first ComplexSequence
   * @param y - the second ComplexSequence
   * @param c - either 1 or -1
   * @return the product between two ComplexSequences
   */
  public static ComplexSequence dftprod( ComplexSequence x, ComplexSequence y,
                                         float c ) {
    int n = x.length();
    ComplexSequence tmp;

    if ( n != y.length() ) {
      tmp = null;
    } else {
      tmp = new ComplexSequence( n );
      float[] xpreal = x.getRealArray();
      float[] xpimag = x.getImagArray();
      float[] ypreal = y.getRealArray();
      float[] ypimag = y.getImagArray();
      float[] tpreal = tmp.getRealArray();
      float[] tpimag = tmp.getImagArray();

      for ( int i = 0; i < n; i++ ) {
        tpreal[ i ] = xpreal[ i ] * ypreal[ i ] - c * xpimag[ i ] * ypimag[ i ];
        tpimag[ i ] = xpimag[ i ] * ypreal[ i ] + c * xpreal[ i ] * ypimag[ i ];
      }
    }

    return tmp;
  }


  /**
   * Prints this ComplexSequence out to a PrintStream.
   *
   * @param ps - the PrintStream accepting the print data.
   */
  public void print( PrintStream ps ) {
    for ( int i = 0; i < realvalues.length; i++ ) {
      ps.println( realvalues[ i ] + "\t" + imagvalues[ i ] );
    }
  }


  /**
   * Modifies this ComplexSequence by setting a value at a given index
   * to a new value.
   *
   * @param i - the index of the changed value
   * @param f - the real part of the new value
   * @param g - the imaginary part of the new value
   */
  public void set( int i, float f, float g ) {
    if ( i >= 0 & i < realvalues.length ) {
      realvalues[ i ] = f;
      imagvalues[ i ] = g;
    }
  }


  /**
   * Modifies this ComplexSequence by setting every value in this
   * ComplexSequence to a constant real value.
   *
   * @param c - the new value for every element in this ComplexSequence
   */
  public void setConstant( float c ) {
    for ( int i = 0; i < realvalues.length; i++ ) {
      realvalues[ i ] = c;
      imagvalues[ i ] = 0;
    }
  }


  /**
   * Zeroes out a part of an array.
   *
   * @param s        - the array to be zeroed out
   * @param start    - the starting index at which to start the zeroes
   * @param duration - the amount of zeroes to place into the array
   */
  protected static void zero( float[] s, int start, int duration ) {
    int j = start;
    for ( int i = 0; i < duration; i++ ) {
      s[ j++ ] = 0.0f;
    }
  }
}
