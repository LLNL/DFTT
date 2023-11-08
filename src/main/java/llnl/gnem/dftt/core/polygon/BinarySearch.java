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
package llnl.gnem.dftt.core.polygon;



/**
 * User: dodge1
 * Date: Jul 26, 2005
 * Time: 12:02:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class BinarySearch {

   public static int[] bounds( final double[] array, final double value )
   {
       return range( array, value, value );
   }


    public static int[] bounds( final float[] array, final float value )
    {
        return range( array, value, value );
    }


    static public int[] range( final double[] array, final double floor, final double ceiling )
    {
        final int[] answer = new int[2];
        int high;
        int low;
        int probe;

        // work on floor
        high = array.length;
        low = -1;
        while( high - low > 1 ){
            probe = ( high + low ) >> 1;
            if( array[probe] < floor )
                low = probe;
            else
                high = probe;
        }
        answer[0] = low;

        // work on ceiling
        high = array.length;
        low = -1;
        while( high - low > 1 ){
            probe = ( high + low ) >> 1;
            if( array[probe] > ceiling )
                high = probe;
            else
                low = probe;
        }
        answer[1] = high;
        return answer;
    }


    static public int[] range( final float[] array, final float floor, final float ceiling )
    {
        final int[] answer = new int[2];
        int high;
        int low;
        int probe;

        // work on floor
        high = array.length;
        low = -1;
        while( high - low > 1 ){
            probe = ( high + low ) >> 1;
            if( array[probe] < floor )
                low = probe;
            else
                high = probe;
        }
        answer[0] = low;

        // work on ceiling
        high = array.length;
        low = -1;
        while( high - low > 1 ){
            probe = ( high + low ) >> 1;
            if( array[probe] > ceiling )
                high = probe;
            else
                low = probe;
        }
        answer[1] = high;
        return answer;
    }



}
