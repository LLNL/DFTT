package llnl.gnem.core.polygon;



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
