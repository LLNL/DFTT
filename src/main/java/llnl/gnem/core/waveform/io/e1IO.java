package llnl.gnem.core.waveform.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import llnl.gnem.core.util.FileSystemException;
import llnl.gnem.core.waveform.Utility;

/**
 * A class that reads binary waveform data encoded using the USNDC e1 format.
 */
public class e1IO extends BinaryDataReader {
    private final static int[] samples_per_block = {7, 3, 4, 5, 4, 1};
    private final static int[] words_per_block = {2, 1, 1, 2, 2, 1};
    private final static int[] bits_per_sample = {9, 10, 7, 12, 15, 28};
    private final static int[] initial_shift = {1, 2, 4, 4, 4, 4};
    private final static long int_mask = 0xFFFFFFFFL;

    @Override
    public BinaryData getBuffer(int npts) {
        return new IntBinaryData(npts);
    }

    @Override
    public int getTotalBytes(String filename, int npts) {
        File file = new File(filename);
        return (int) file.length();
    }

    @Override
    public int getTotalBytes(int available, int npts) {
        return available;
    }

    /**
     * Read a file containing e1 data and return the result as a BinaryData
     * object.
     *
     * @param stream
     * @param byteOffset
     * @param totalBytes
     * @param buffer
     * @return 
     * @throws java.io.IOException
     */
    @Override
    public BinaryData readData(InputStream stream, int byteOffset, int totalBytes, BinaryData buffer) throws IOException {
        return readIntData(stream, byteOffset, buffer, totalBytes);
    }
/*
 * e-format in general consists of a series of buffers, each a multiple
 * of 4 bytes.  Each buffer begins witha header follwed by a series of
 * compressed packets.  There is no index block, the index is provided
 * with each packet.
 * The header 8 bytes long, consisting of:
 * 1) a 2-byte integer giving the length of the buffer in bytes, including
 *    the 8-byte header.
 * 2) a 2-byte integer giving the number of samples represented by the
 *    buffer.
 * 3) a 4-bit integer that is 1 if the following data are uncompressed
 *    32-bit integers, or 0 for compressed.
 * 4) a 4-bit integer that is the number of difference operations applied to
 *    the data.
 * 5) a 24-bit value that is the equivalent to the last sample in the
 *    block once the block is fully decompressed.
 *
 * This header is then immediately followed by either the compressed
 * packets or the uncompressed data if the uncompressed flag is 1.
 *
 * Compressed packets come in 6 index types.  If the first bit of the
 * packet is 0, the packet will be 8 bytes long and have 7 samples using
 * 9 bits per sample immediately following that first bit.  For all packets,
 * the data bits immediately follow the index bits.  The following
 * table give the 6 index types:
 *
 * index bits  bytes for packet  samples  bits per sample
 * ----------  ----------------  -------  ---------------
 *    0                    2        7                9
 *    10                   1        3               10
 *    1100                 1        4                7
 *    1101                 2        5               12
 *    1110                 2        4               15
 *    1111                 1        1               28
 *
 * Note that on compression, the 3rd case (7 bps) is checked first, and
 * therefore will come first in the order of packet types.
 */
    public static BinaryData readIntData(InputStream stream, int byteOffset, BinaryData buffer, int totalBytes) throws IOException {
        long[] in = s4IO.readUnsignedData(stream, byteOffset, totalBytes);
        int rv = 0;
        int idx = 0;
        int lastSizeBeforeError = 0;
        int inMax = in.length * 4;
        while (rv < buffer.size() && (idx + 2) * 4 < inMax) {
            int check = e_decompress(in, idx, buffer, rv);
            if (check <= 0) {
                return BinaryData.truncateTo(buffer, lastSizeBeforeError);
            }
            lastSizeBeforeError = rv;
            rv += check;
            idx += 2;
            while (idx < inMax / 4 && in[idx] == 0) {
                ++idx;
            }
            if (rv > buffer.size()) {
                break;
            }
        }
        return buffer;
    }

    public static boolean WriteIntData(int[] data, int numSamples, String fileName) throws IOException {
        byte[] inbytes = Utility.intArraytoByteArray_4bytes_per_int(data);
        E1Writer.writeE1Format(inbytes, numSamples, 4, fileName);    // 4 characters per 32 bit word
        int totalBytes = (int) new File(fileName).length();
        BinaryData buffer = readIntData(new FileInputStream(fileName), 0, new IntBinaryData(numSamples), totalBytes);
        int[] test = buffer.getIntData();
        boolean dataMatches = Arrays.equals(data, test);
        if (!dataMatches) { // file is no good so get rid of it.
            boolean deleted = new File(fileName).delete();
            if (!deleted) {
                throw new FileSystemException("Could not delete test file: " + fileName);
            }
        }
        return dataMatches;
    }

    private static void b_lshift(long[] input, int offset, int nwords, int size) {
        long ltmp;
        long rtmp;
        long right = 4 * 8 - size;
        ltmp = (input[offset] << (long) size) & int_mask;
        for (int i = 0; i < nwords - 1; i++) {
            rtmp = ((input[offset + i + 1]) >> right) & int_mask;
            input[offset + i] = ltmp | rtmp;
            ltmp = ((input[offset + i + 1]) << (long) size) & int_mask;
        }
        input[offset + nwords - 1] = ltmp;
    }

    private static int e_decompress(long[] in, int inOff, BinaryData out, int outOff) {
        int i;
        int j;
        int k;
        int ms;
        long check;
        int code;
        int idx = 0;

        int maxIdx = out.size() - 1;

        int ns = (int) (in[inOff] & 0x0000ffff);
        int nd = (int) (((in[1 + inOff]) >> 24) & int_mask);

        /*
         * nd is now overloaded:
         *
         * first quartet has the number of diffs.
         *
         * second quartet has a code: 0 for "regular" e compression
         *
         * 1 for uncompressed 32 bit ints
         */
        switch (nd >> 4) {
            case 0:
                nd &= 0xf;
                check = in[1 + inOff] & 0xffffff;
                /*
                 * drop high 8 bits
                 */

                if ((check & 0x800000) != 0) {
                    check |= 0xff000000;
                    /* sign extend */
                }
                ms = 0;
                i = 2;
                while (ms < ns) {
                    int m = i + inOff;
                    code = (int) ((in[m] >> 28) & int_mask);
                    if (code < 8) {
                        idx = 0;
                    } else if (code < 0xc) {
                        idx = 1;
                    } else if (code >= 0xc) {
                        idx = 2 + (code & 0x3);
                    }

                    b_lshift(in, m, words_per_block[idx], initial_shift[idx]);
                    for (j = 0; j < samples_per_block[idx]; j++) {
                        int idxTmp = Math.min(outOff + ms++, maxIdx);
                        out.setInt(idxTmp, Utility.b_lval(in[m], bits_per_sample[idx]));
                        b_lshift(in, m, words_per_block[idx], bits_per_sample[idx]);
                    }
                    i += words_per_block[idx];
                }
                for (k = 0; k < nd; k++) {
                    for (j = 1; j < ms; ++j) {
                        int outIdx = outOff + j;
                        if (outIdx < out.size()) {
                            out.setInt(outIdx, out.getInt(outIdx) + out.getInt(outIdx - 1));
                        }
                    }
                }
                int outIdx = outOff + ms - 1;
                if (outIdx < out.size() && check == out.getInt(outIdx)) {
                    return (ms);
                } else {
                    return (-1);
                }
            case 1:
                copyToOutputBuffer(out, outOff, in, inOff + 2, ns);
                return (ns);
            default:
                return (-1);
        }
    }

    private static void copyToOutputBuffer(BinaryData out, int outOff, long[] in, int start, int npts) {
        for (int j = 0; j < npts; ++j) {
            out.setInt(j + outOff, (int) in[j + start]);
            in[j + start] = 0; /* zero-out used samples to allow setting correct starting point for next iteration. */
        }
    }
}
