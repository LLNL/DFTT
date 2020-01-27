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
package llnl.gnem.core.waveform;

/**
 * User: dodge1 Date: Feb 6, 2004 Time: 3:38:10 PM To change this template use
 * Options | File Templates.
 */
public class Utility {

    public static short[] byteArrayToShortArray(byte[] in) {
        int ib = 0;
        int bnpts = in.length;
        if (bnpts < 2) {
            throw new IllegalArgumentException("Input byte array too short for conversion!");
        }
        short[] result = new short[bnpts / 2];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            result[j] = (short) (((in[ib++] & 0xff) << 8) + ((in[ib++] & 0xff)));
        }
        return result;
    }

    public static int[] byteArrayToIntArray(byte[] in) {
        int ib = 0;
        int bnpts = in.length;
        if (bnpts < 4) {
            throw new IllegalArgumentException("Input byte array too short for conversion!");
        }
        int[] result = new int[bnpts / 4];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            result[j] = ((in[ib++] & 0xff) << 24) + ((in[ib++] & 0xff) << 16) + ((in[ib++] & 0xff) << 8) + ((in[ib++] & 0xff));
        }
        return result;
    }

    public static long[] byteArrayToLongArray_4bytes_per_long(byte[] in) {
        int ib = 0;
        int bnpts = in.length;
        if (bnpts < 4) {
            throw new IllegalArgumentException("Input byte array too short for conversion!");
        }
        long[] result = new long[bnpts / 4];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            result[j] = ((in[ib++] & 0xffL) << 24) + ((in[ib++] & 0xffL) << 16) + ((in[ib++] & 0xffL) << 8) + ((in[ib++] & 0xffL));
        }
        return result;
    }

    public static int[] byteArrayToIntArray_4bytes_per_int(byte[] in) {
        int ib = 0;
        int bnpts = in.length;
        if (bnpts < 4) {
            throw new IllegalArgumentException("Input byte array too short for conversion!");
        }
        int[] result = new int[bnpts / 4];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            result[j] = ((in[ib++] & 0xff) << 24) + ((in[ib++] & 0xff) << 16) + ((in[ib++] & 0xff) << 8) + ((in[ib++] & 0xff));
        }
        return result;
    }

    public static long[] byteArrayToLongArray_8bytes_per_long(byte[] in) {
        int ib = 0;
        int bnpts = in.length;
        if (bnpts < 4) {
            throw new IllegalArgumentException("Input byte array too short for conversion!");
        }
        long[] result = new long[bnpts / 8];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            result[j] = ((in[ib++] & 0xffL) << 56) + ((in[ib++] & 0xffL) << 48)
                    + ((in[ib++] & 0xffL) << 40) + ((in[ib++] & 0xffL) << 32)
                    + ((in[ib++] & 0xffL) << 24) + ((in[ib++] & 0xffL) << 16)
                    + ((in[ib++] & 0xffL) << 8) + ((in[ib++] & 0xffL));
        }
        return result;
    }

    public static float[] byteArrayToFloatArray(byte[] in) {
        int ib = 0;
        int bnpts = in.length;
        if (bnpts < 4) {
            throw new IllegalArgumentException("Input byte array too short for conversion!");
        }
        float[] result = new float[bnpts / 4];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            result[j] = Float.intBitsToFloat(((in[ib++] & 0xff) << 24) + ((in[ib++] & 0xff) << 16) + ((in[ib++] & 0xff) << 8) + (in[ib++] & 0xff));
        }
        return result;
    }

    public static float[] byteArrayToFloatArray(byte[] in, boolean swapBytes) {
        int ib = 0;
        int bnpts = in.length;
        if (bnpts < 4) {
            throw new IllegalArgumentException("Input byte array too short for conversion!");
        }
        float[] result = new float[bnpts / 4];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            byte a = in[ib++];
            byte b = in[ib++];
            byte c = in[ib++];
            byte d = in[ib++];
            if (swapBytes) {
                result[j] = Float.intBitsToFloat(((a & 0xff))
                        + ((b & 0xff) << 8)
                        + ((c & 0xff) << 16)
                        + ((d & 0xff) << 24));
            } else {
                result[j] = Float.intBitsToFloat(((a & 0xff) << 24) + ((b & 0xff) << 16) + ((c & 0xff) << 8) + (d & 0xff));
            }
        }
        return result;
    }

    public static double[] byteArrayToDoubleArray(byte[] in) {
        int ib = 0;
        int bnpts = in.length;
        if (bnpts < 4) {
            throw new IllegalArgumentException("Input byte array too short for conversion!");
        }
        double[] result = new double[bnpts / 8];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            long tmp;
            tmp = ((in[ib++] & 0xffL) << 56) + ((in[ib++] & 0xffL) << 48) + ((in[ib++] & 0xffL) << 40) + ((in[ib++] & 0xffL) << 32) + ((in[ib++] & 0xffL) << 24) + ((in[ib++] & 0xffL) << 16) + ((in[ib++] & 0xffL) << 8) + ((in[ib++] & 0xffL));
            result[j] = Double.longBitsToDouble(tmp);
        }
        return result;

    }


    /* the short type in Java is a 16-bit signed two's-complement integer. The number of unique integers
     that can be represented by this scheme is 2^16, or 65,536. Half of the short type's range of values
     are used to represent zero and positive numbers; the other half of the short type's range are used
     to represent negative numbers. The range of negative values for a 16-bit two's-complement number
     is -32,768 (0x8000) to -1 (0xffff). Zero is 0x0000. The range of positive values is one (0x0001)
     to 32,767 (0x7fff).
     */
    public static int[] shortArrayToIntArray_2shorts_per_int(short[] in) {
        int ib = 0;
        int bnpts = in.length;
        if (bnpts < 2) {
            throw new IllegalArgumentException("Input short array too short for conversion!");
        }
        int[] result = new int[bnpts / 2];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            result[j] = ((in[ib++] & 0xffff) << 16) + ((in[ib++] & 0xffff));
        }
        return result;
    }

    public static int SwapIntBytes(byte[] b) {
        byte[] accum = new byte[4];
        int[] result;
        for (int d = 3, u = 0; d >= 0; d--, u++) {
            accum[d] = b[u];
        }
        result = byteArrayToIntArray(accum);
        return (result[0]);
    }

    public static double SwapDoubleBytes(byte[] b) {
        byte[] accum = new byte[8];
        double[] result;
        for (int d = 7, u = 0; d >= 0; d--, u++) {
            accum[d] = b[u];
        }
        result = byteArrayToDoubleArray(accum);
        return (result[0]);
    }

    public static float SwapFloatBytes(byte[] b) {
        byte[] accum = new byte[4];
        float[] result;
        for (int d = 3, u = 0; d >= 0; d--, u++) {
            accum[d] = b[u];
        }
        result = byteArrayToFloatArray(accum);
        return (result[0]);
    }

    public static byte[] floatArrayToByteArray(float[] f) {
        int nbytes = f.length * 4;
        int it;
        byte[] dataBuffer = new byte[nbytes];
        for (int i = 0; i < f.length; i++) {
            it = Float.floatToIntBits(f[i]);
            dataBuffer[4 * i] = (byte) (it >> 24);
            dataBuffer[4 * i + 1] = (byte) (it >> 16);
            dataBuffer[4 * i + 2] = (byte) (it >> 8);
            dataBuffer[4 * i + 3] = (byte) (it);
        }
        return dataBuffer;
    }

    public static byte[] intArrayToByteArray(int[] data, boolean isLittleEndian) {
        int nbytes = data.length * 4;
        byte[] dataBuffer = new byte[nbytes];

        int it;
        for (int i = 0; i < data.length; i++) {
            if (isLittleEndian) {
                it = data[i];
                dataBuffer[4 * i] = (byte) (it);
                dataBuffer[4 * i + 1] = (byte) (it >> 8);
                dataBuffer[4 * i + 2] = (byte) (it >> 16);
                dataBuffer[4 * i + 3] = (byte) (it >> 24);
            } else {
                it = data[i];
                dataBuffer[4 * i] = (byte) (it >> 24);
                dataBuffer[4 * i + 1] = (byte) (it >> 16);
                dataBuffer[4 * i + 2] = (byte) (it >> 8);
                dataBuffer[4 * i + 3] = (byte) (it);
            }
        }
        return dataBuffer;
    }

    /**
     * Converts int array to byte array (higher bytes first)
     *
     * @param from int Array
     * @return result byte Array
     */
    public static byte[] intArraytoByteArray_4bytes_per_int(int[] from) {
        int byteLen = from.length * 4;
        byte[] result = new byte[byteLen];
        for (int j = 0; j < from.length; j++) {
            int offset = j * 4;
            int bits = 32;
            for (int i = 0; i < 4; i++) {
                bits -= 8;
                result[offset++] = (byte) ((from[j] >> bits) & 0xff);
            }
        }
        return result;
    }

    /**
     * Converts long array to byte array (higher bytes first) This only uses the
     * first 4 bytes (32 bits) of the long 8 byte word Doing this preserves the
     * sign bit that would otherwise get lost if we used int instead.
     *
     * @param from long Array
     * @return result byte Array
     */
    public static byte[] longArraytoByteArray_4bytes_per_long(long[] from) {
        int byteLen = from.length * 4;
        byte[] result = new byte[byteLen];
        for (int j = 0; j < from.length; j++) {
            int offset = j * 4;
            int bits = 32;
            for (int i = 0; i < 4; i++) {
                bits -= 8;
                result[offset++] = (byte) ((from[j] >> bits) & 0xff);
            }
        }
        return result;
    }

    /* convert byte array to int array (32 bits) using a long array to preserve sign bit */
    public static int[] byteArrayToIntArray_4bytes_per_int(byte[] in, int start, int len) {
        int ib = start;
        int bnpts = in.length;
        if (bnpts < 4) {
            throw new IllegalArgumentException("Input byte array too short for conversion!");
        }
        int[] result = new int[len / 4];
        int npts = result.length;
        for (int j = 0; j < npts; ++j) {
            result[j] = ((in[ib++] & 0xff) << 24) + ((in[ib++] & 0xff) << 16) + ((in[ib++] & 0xff) << 8) + ((in[ib++] & 0xff));
        }
        return result;
    }

    public static int b_lval(long input, int size) {
        long val = (input >> (4 * 8 - size)) & int_mask;
        long emask = (0xffffffff << (size - 1)) & int_mask;
        if ((val & emask) != 0) {
            val |= emask;
        }
        return (int) (val);
    }
    private final static long int_mask = 0xFFFFFFFFL;
}
