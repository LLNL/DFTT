package llnl.gnem.core.waveform.io;

import llnl.gnem.core.waveform.Utility;

/**
 * User: ganzberger1
 * Date: Jun 1, 2005
 * Time: 2:07:21 PM
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */

public class E1Compressor {


    private final static int BIT7 = 0x3f;
    private final static int BIT9 = 0xff;
    private final static int BIT10 = 0x1ff;
    private final static int BIT12 = 0x7ff;
    private final static int BIT15 = 0x3fff;

    private final static int MASK7 = 0x7f;
    private final static int MASK9 = 0x1ff;
    private final static int MASK10 = 0x3ff;
    private final static int MASK12 = 0xfff;
    private final static int MASK15 = 0x7fff;
    private final static int MASK24 = 0x00ffffff;
    private final static int MASK28 = 0x0fffffff;

    private final static int PACK7 = 0xc0000000;
    private final static int PACK9 = 0;
    private final static int PACK10 = 0x80000000;
    private final static int PACK12 = 0xd0000000;
    private final static int PACK15 = 0xe0000000;
    private final static int PACK28 = 0xf0000000;

    private final static int UNCOMPRESSED = 0x10;
    private final static int WFM_COMP_SHORT_BLOCK = (1);



/*int          *in;           the input buffer
int            len;          ... and its length
unsigned int *out;          the output buffer ... assumed to be int
                               enough
int            outlen;       the length of an output block in 4-byte words
int            last;         how to treat the last block ?
int           *ncomp;        the number of input samples processed
int          *wbuf;         a work buffer - assumed to be int enough */
    static DataBuffer compress(int[] in, int len, long[] out, int outlen, int last, int ncomp, int[] wbuf, int[] ibuf) {

        DataBuffer bufdata = new DataBuffer();
        int d10, d11, d20, d21, d30, d31, d40, d41;

        double[] av = new double[5];
        int sample, pack;
        int found, diff, wbuf_im1;
        int clipped, i, j, ndiff, ns, shift;
        int buf_remaining;
        int samples_this_buf, samples_remaining;
        int nbytes;

        int buflen = (outlen - 2) * 4;
        long[] buf;
        out = new long[outlen * 4];
        buf = out;
        int buf_cnt;  //  added this to keep tract of buffer location
        int out_cnt = 0;  //  added this to keep track of out location
        int wbuf_cnt = 0; //  added this to keep track of wbuf location.

        for (ns = 0; ns < len; ns++) {
            if (in[ns] < -0x00800000 || in[ns] > 0x007fffff)
                break;
        }


        clipped = (ns < len) ? 1 : 0;

        if ((clipped != 0) && (ns < outlen - 2)) {

            ncomp = Math.min((outlen - 2), len);


            for (int x = 0; x < outlen * 4; x++) out[x] = 0;
            /**
             *   s is short pointer, it points to the beginning of out and will be incremented.
             *   below we also us it to set values in
             */


            short[] sin = new short[2];
            // ganz mimic the use of short pointers to set values in int array.
            sin[0] = (short) (outlen * 4);   /* the length of the block in bytes */
            sin[1] = (short) ncomp;          /* the length of the block in samples */

            int[] nout = Utility.shortArrayToIntArray_2shorts_per_int(sin);
            /*
             * 1st byte of the 2nd header word indicates the block is
             * not compressed.
             */
            out[0] = nout[0];

            out[1] = 0;          /* initialize the second header word */
            //c[4] = (char) UNCOMPRESSED;

            byte[] bin1 = new byte[4];
            bin1[0] = (byte) (bin1[0] + UNCOMPRESSED);
            bin1[1] = (byte) (bin1[1] + 0);
            bin1[2] = (byte) (bin1[2] + 0);
            bin1[3] = (byte) (bin1[3] + 0);

            int[] nout1 = Utility.byteArrayToIntArray_4bytes_per_int(bin1);

            out[1] = nout1[0];

            buf_cnt = out_cnt + 2;
//            if ((buflen != 2040) || (buf.length < 2040) || (in.length < 2040))
//                System.out.println("found it");
            for (j = 0; j < buflen; j++) buf[buf_cnt + j] = in[j];

            /*
              *  if this is the last buffer, all input samples are
              *  compressed and last = SHORT_BLOCK then,
              *  reset the length of the block to the actual
              *  bytes
             */

            if (last == WFM_COMP_SHORT_BLOCK && ncomp == len) {
                //s[0] = len * 4 + 8;
                sin[0] = (short) (len * 4 + 8); // (outlen * 4);   /* the length of the block in bytes */
                sin[1] = (short) ncomp;          /* the length of the block in samples */
                nout = Utility.shortArrayToIntArray_2shorts_per_int(sin);
                out[0] = nout[0];
            }
            bufdata.buf = buf;
            bufdata.wbuf = wbuf;
            bufdata.nbytes = (int) sin[0];
            bufdata.nw = ncomp;
            return (bufdata);
            // return ((int) sin[0]);
        }

        /*
         *  from now on, `ns' is the number of samples we're
         *  dealing with.
         */

        /*
         *  our target compression is 3-4 :1, so optimal
         *  input parameters are:
         *  `ns' (intwords) = (integral multiple of) 4 * `outlen' (bytes)
         *  figuring about 1 word/byte.
         */

        /*
         *  scan the input buffer for successive differences
         *  to adapt to the best compression scheme.
         *  sample
         */

        d10 = d20 = d30 =  0;
        av[0] = 0;
        av[1] = av[2] = av[3] = av[4] = 0;

        for (i = 1; i < ns; i++) {
            d11 = in[i] - in[i - 1];
            d21 = d11 - d10;
            d31 = d21 - d20;
            d41 = d31 - d30;
            d10 = d11;
            d20 = d21;
            d30 = d31;
            d40 = d41;
            av[0] += (double) Math.abs(in[i]);
            av[1] += (double) Math.abs(d10);
            av[2] += (double) Math.abs(d20);
            av[3] += (double) Math.abs(d30);
            av[4] += (double) Math.abs(d40);
        }


        for (ndiff = 0, i = 1; i < 5; i++) {
            if (av[i] < av[0]) {
                ndiff = i;
                av[0] = av[i];
            }
        }

        /*
         *  now we'll loop over our input buffer and compress.
         */

        /*
         *  diff'em
         */

        wbuf = new int[ns];   // word length
        for (j = 0; j < ns; j++) wbuf[j] = ibuf[j];


        for (j = 0; j < ndiff; j++) {
            for (i = 1, wbuf_im1 = wbuf[wbuf_cnt]; i < ns; i++) {
                diff = wbuf[wbuf_cnt + i] - wbuf_im1;
                wbuf_im1 = wbuf[wbuf_cnt + i];
                wbuf[wbuf_cnt + i] = diff;
            }
        }


        nbytes = 0;
        samples_remaining = ns;

        buf_remaining = outlen - 2;
        samples_this_buf = 0;

        /*
         * get the current buffer, initialize, and
         * set up the equivalencies
         */

        buf_cnt = out_cnt;


        for (i = 0; i < outlen; i++) buf[buf_cnt + i] = 0;

        /* initialize the second header word */
        //c[4] = (char) UNCOMPRESSED;
        byte[] bin2 = new byte[4];
        bin2[0] = (byte) (bin2[0] + ndiff);
        bin2[1] = (byte) (bin2[1] + 0);
        bin2[2] = (byte) (bin2[2] + 0);
        bin2[3] = (byte) (bin2[3] + 0);

        int[] nout = Utility.byteArrayToIntArray_4bytes_per_int(bin2);

        buf[1] = nout[0];


        /*
         * update the buffer to the first data slot
         */


        buf_cnt = buf_cnt + 2;

        while (buf_remaining > 0 && samples_remaining > 0) {
            /*
             *  try for 4 7-bit samples
             */

            if (samples_remaining > 3) {
                for (found = 1, i = 0; i < 4; i++) {
                    if ((double) Math.abs(wbuf[wbuf_cnt + i]) > BIT7) {
                        found = 0;
                        break;
                    }
                }
                if (found != 0) {
                    pack = PACK7;
                    for (i = 0, shift = 21;
                         i < 4; i++, shift -= 7) {
                        sample = wbuf[wbuf_cnt + i] & MASK7;
                        sample <<= shift;
                        pack |= sample;
                    }

                    buf[buf_cnt++] = pack;

                    samples_remaining -= 4;
                    buf_remaining -= 1;
                    samples_this_buf += 4;

                    wbuf_cnt += 4;
                    continue;
                }
            }

            /*
             *  next try for 7 9-bit samples
             */

            if (samples_remaining > 6 && buf_remaining > 1) {
                for (found = 1, i = 0; i < 7; i++) {
                    if ((double) Math.abs(wbuf[wbuf_cnt + i]) > BIT9) {
                        found = 0;
                        break;
                    }
                }
                if (found != 0) {
                    pack = PACK9;
                    for (i = 0, shift = 22; i < 3;
                         i++, shift -= 9) {
                        sample = wbuf[wbuf_cnt + i] & MASK9;
                        sample <<= shift;
                        pack |= sample;
                    }
                    sample = wbuf[wbuf_cnt + 3] & MASK9;
                    pack |= (sample >> 5);

                    buf[buf_cnt++] = pack;

                    pack = (sample << 27);
                    for (i = 4, shift = 18; i < 7;
                         i++, shift -= 9) {
                        sample = wbuf[wbuf_cnt + i] & MASK9;
                        sample <<= shift;
                        pack |= sample;
                    }

                    buf[buf_cnt++] = pack;

                    samples_remaining -= 7;
                    buf_remaining -= 2;
                    samples_this_buf += 7;

                    wbuf_cnt += 7;

                    continue;
                }
            }

            /*
             *  next try for 3 10-bit samples
             */

            if (samples_remaining > 2) {
                for (found = 1, i = 0; i < 3; i++) {
                    if ((double) Math.abs(wbuf[wbuf_cnt + i]) > BIT10) {
                        found = 0;
                        break;
                    }
                }
                if (found != 0) {
                    pack = PACK10;
                    for (i = 0, shift = 20; i < 3; i++,
                            shift -= 10) {
                        sample = wbuf[wbuf_cnt + i] & MASK10;
                        sample <<= shift;
                        pack |= sample;
                    }

                    buf[buf_cnt++] = pack;

                    samples_remaining -= 3;
                    buf_remaining -= 1;
                    samples_this_buf += 3;

                    wbuf_cnt += 3;

                    continue;
                }
            }

            /*
             *  next try for 5 12-bit samples
             */

            if (samples_remaining > 4 && buf_remaining > 1) {
                for (found = 1, i = 0; i < 5; i++) {
                    if ((double) Math.abs(wbuf[wbuf_cnt + i]) > BIT12) {
                        found = 0;
                        break;
                    }
                }
                if (found != 0) {
                    pack = PACK12;
                    for (i = 0, shift = 16; i < 2;
                         i++, shift -= 12) {
                        sample = wbuf[wbuf_cnt + i] & MASK12;
                        sample <<= shift;
                        pack |= sample;
                    }
                    sample = wbuf[wbuf_cnt + 2] & MASK12;
                    pack |= (sample >> 8);

                    buf[buf_cnt++] = pack;

                    pack = (sample << 24);
                    for (i = 3, shift = 12; i < 5;
                         i++, shift -= 12) {
                        sample = wbuf[wbuf_cnt + i] & MASK12;
                        sample <<= shift;
                        pack |= sample;
                    }

                    buf[buf_cnt++] = pack;

                    samples_remaining -= 5;
                    buf_remaining -= 2;
                    samples_this_buf += 5;

                    wbuf_cnt += 5;

                    continue;
                }
            }

            /*
             *  next try for 4 15-bit samples
             */

            if (samples_remaining > 3 && buf_remaining > 1) {
                for (found = 1, i = 0; i < 4; i++) {
                    if ((double) Math.abs(wbuf[wbuf_cnt + i]) > BIT15) {
                        found = 0;
                        break;
                    }
                }
                if (found != 0) {
                    pack = PACK15;
                    sample = wbuf[wbuf_cnt] & MASK15;
                    sample <<= 13;
                    pack |= sample;

                    sample = wbuf[wbuf_cnt + 1] & MASK15;
                    pack |= (sample >> 2);

                    buf[buf_cnt++] = pack;

                    pack = (sample << 30);
                    sample = wbuf[wbuf_cnt + 2] & MASK15;
                    pack |= (sample << 15);
                    sample = wbuf[wbuf_cnt + 3] & MASK15;
                    pack |= sample;

                    buf[buf_cnt++] = pack;

                    samples_remaining -= 4;
                    buf_remaining -= 2;
                    samples_this_buf += 4;

                    wbuf_cnt += 4;

                    continue;
                }
            }

            /*
             *  if we get here, then the sample fits into a
             *  28 bit word
             */

            pack = PACK28;
            pack |= wbuf[wbuf_cnt] & MASK28;

            buf[buf_cnt++] = pack;
            samples_remaining -= 1;
            buf_remaining -= 1;
            samples_this_buf += 1;

            wbuf_cnt += 1;

        }
        /*
     * snag the last decompressed word written
     */

        pack = in[ns - samples_remaining - 1] & MASK24;

        if (samples_this_buf > 0) out[1] |= pack;

        /*
         * why did we stop ?
         */

        if (buf_remaining == 0)     // was !buf_remaining
        {
            /*
             * out of buffer write header and go to next buffer
             */

            // ganz mimic the use of short pointers to set values in long array.
            short[] sin = new short[2];

            sin[0] = (short) (outlen * 4);   /* the length of the block in bytes */
            sin[1] = (short) samples_this_buf;          /* the length of the block in samples */

            nout = Utility.shortArrayToIntArray_2shorts_per_int(sin);

            out[0] = nout[0];

            /* add to the total written */

            nbytes += sin[0];         // fixed this was s


        } else if (clipped != 0) {
            /*
             * out of samples, one special case here:
             * IF we found a clipped sample, then we
             * want that clipped sample to show up in the
             * next buffer that the calling function sends
             * us.  So if the current # of samples in our
             * buffer is less than `outlen - 2', we'll drop
             * the current record and return.
             *
             * however, if the current # of samples is greater
             * or equal to `outlen - 2', then the clipped sample
             * will NOT fit into the next buffer and so we'll go
             * ahead and write a padded compressed block.
             */

            if (samples_this_buf >= outlen - 2) {
                /*
                 * take this buffer and break out
                 */
                short[] sin = new short[2];
                sin[0] = (short) (outlen * 4);   /* the length of the block in bytes */
                sin[1] = (short) samples_this_buf;          /* the length of the block in samples */

                nout = Utility.shortArrayToIntArray_2shorts_per_int(sin);

                out[0] = nout[0];

                nbytes += sin[0];
            } else {
                /*
                 *  drop this buffer and return NOW
                 *  pretend we didn't even SEE this buffer
                 */
      //          buf_remaining = 0;
                samples_remaining += samples_this_buf;
            }
        } else {
            /*
             * out of samples, if its not the last buffer then
             * drop it - shouldn't get here often !
             */

            // public static int  WFM_COMP_SHORT_BLOCK = 1;
            //public static int  WFM_COMP_FILL_BLOCK = 2;

            short[] sin = new short[2];
            switch (last) {
                case 1: //WFM_COMP_SHORT_BLOCK:
                    /*
                     *  set s[0] to actual # of bytes
                     */

                    sin[0] = (short) ((outlen - buf_remaining) * 4);   /* the length of the block in bytes */
                    sin[1] = (short) samples_this_buf;          /* the length of the block in samples */
                    nout = Utility.shortArrayToIntArray_2shorts_per_int(sin);
                    buf[0] = nout[0];

                    nbytes += sin[0];
                    break;
                case 2: //WFM_COMP_FILL_BLOCK:
                    sin[0] = (short) (outlen * 4);   /* the length of the block in bytes */
                    sin[1] = (short) samples_this_buf;          /* the length of the block in samples */
                    nout = Utility.shortArrayToIntArray_2shorts_per_int(sin);
                    buf[0] = nout[0];
                    nbytes += sin[0];
                    break;
                default :
                    /*
                     *  drop this buffer and return
                     *  pretend we didn't even SEE this buffer
                     */
                    samples_remaining += samples_this_buf;
                    break;
            }
        }
        /*
         *
         *  OK !! we're outta here - figure out what to return
         */

        ncomp = ns - samples_remaining;

        bufdata.buf = buf;
        bufdata.wbuf = wbuf;
        bufdata.nbytes = nbytes;
        bufdata.nw = ncomp;
        return (bufdata);

    }

    /* created this class to pass buffers and parameters around to preserve their content between calls */
    static class DataBuffer {
         long buf[];
         int wbuf[];
         int nbytes;
         int nw;
    }
}
