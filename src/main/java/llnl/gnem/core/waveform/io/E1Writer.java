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
package llnl.gnem.core.waveform.io;

/**
 * User: ganzberger1
 * Date: Jun 1, 2005
 * Time: 2:07:21 PM
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import llnl.gnem.core.waveform.Utility;

public class E1Writer {

    private final static int WFM_COMP_SHORT_BLOCK = 1;

    static int writeE1Format(byte[] buffer, int ns, int in_bpw, String filename) throws FileNotFoundException, IOException {

        int req_len, len; //, nbyte;
        int nw = 0;
        int last, nsamp;
        int samp_len;

        int[] wbuf;

        int ibuf_size = 0;
        long buf[] = null;
        int ibuf[] = null;
        int buffer_cnt = 0;
        FileOutputStream outputFileStream = null;

        // datatype of the input buffer is assumed to be "s4" (Sun integer)


        req_len = 512;

        /*
        * the number of samples to try to compress at one time
        * using 1 sample/byte optimum compression.
        *
        * If we are compressing E format, then samp_len is
        * just req_len.  If we are compressing e format,
        * then try for 1 sample/byte
        */

        samp_len = req_len * 4; // 4 bytes per int in java ....sizeof(int);

        last = 0;    /* a flag indicating the last buffer */

        /*
        *  allocate buffers for writing
        *
        *  ibuf_size is size of ibuf in int words (samples).
        *
        *  workspace required for compression is determined using
        *  `req_len', which is the size of the output record in bytes
        *
        *  It can go as high is 16 (kbytes), which at optimal
        *  compression  of  1 sample/byte, goes to roughly 16k samples
        *
        *  So allocate space for one double/byte, which should be a more
        *  than adequate space.
        *
        *  note sizeof(int) = 4 below ...
        *
        *  req_len is output buffer size in int words
        *  scale by sizeof(int), and size of double to get required space
        */
        try {
            if (ibuf_size < samp_len * 2) {
                ibuf_size = samp_len * 2;

                /*
                 *  ibuf needs to hold ibuf_size int words AND
                 *  ibuf_size/2 doubles
                 */

                ibuf_size = Math.max(ibuf_size, samp_len);

                /*
                 *  ibuf needs to hold a buffer expanded to double
                 */


                int[] nibuf = new int[ibuf_size * 4];
                if (ibuf != null) for (int i = 0; i < ibuf.length; i++) nibuf[i] = ibuf[0];

                /*
                 *  We'll use the second half of ibuf for a temporary
                 *  work space.
                 *
                 *  assumes doubles to be twice as int as a int !!!
                 */
                wbuf = new int[samp_len];

                /*
                *  We need an output buffer for compressed data
                */

                /* I do this to preserve the contents of buf and set it to the required size
                remember we are only using the 1st 32 bits of buf so we can keep all 32 bits
                including the sign, without interpretion */
                long[] nlbuf = new long[ samp_len * 4];
                if (buf != null) for (int i = 0; i < buf.length; i++) nlbuf[i] = buf[0];
                buf = nlbuf;

                /*
                 * set ibuf_size to actual size of ibuf in int words
                 */

            } else {

                /* I do this to preserve the contents of buf and set it to the required size */
                int[] nibuf2 = new int[ ibuf_size];
                if (ibuf != null) for (int i = 0; i < ibuf.length; i++) nibuf2[i] = ibuf[0];

                wbuf = new int[samp_len];

                /* I do this to preserve the contents of buf and set it to the required size
                 remember we are only using the 1st 32 bits of buf so we can keep all 32 bits
                 including the sign, without interpretion */
                long[] nlbuf2 = new long[ samp_len];
                if (buf != null) for (int i = 0; i < buf.length; i++) nlbuf2[i] = buf[0];
                buf = nlbuf2;

            }


            nsamp = ns;   /* current # of samples */
            outputFileStream = new FileOutputStream(filename);    // Create output stream
            while (nsamp > 0) {
                /*
                 * the len of the current buffer
                 */

                len = Math.min(nsamp, samp_len);

                /*
                 * if its the last one, we need to keep track, because
                 * we'll write a short output buffer
                 */

                if (len == nsamp) last = WFM_COMP_SHORT_BLOCK;

                /*
                 *  if input data is NOT already s4, or if
                 *  a calibration is to be applied, then
                 *  copy to s4 and apply calibration
                 */


                ibuf = Utility.byteArrayToIntArray_4bytes_per_int(buffer, buffer_cnt, len * in_bpw);

                /*
                 *  compress the buffer
                 */

                E1Compressor.DataBuffer bd = E1Compressor.compress(ibuf, len, buf, req_len, last, nw, wbuf, ibuf);

                int nbytes = bd.nbytes;  // 2 characters per word
                wbuf = bd.wbuf;
                buf = bd.buf;
                nw = bd.nw;

                byte[] mybytes = Utility.longArraytoByteArray_4bytes_per_long(buf);

                outputFileStream.write(mybytes, 0, nbytes);            //   write bytes
                nsamp -= nw;
                buffer_cnt += nw * in_bpw;
            }

            return (ns - nsamp); /* normal return */


        } finally {
            if (outputFileStream != null)
                outputFileStream.close();  // Always close the streams, even if exceptions were thrown

        }


    }

}