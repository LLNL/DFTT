/*
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
package llnl.gnem.apps.detection.util.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.text.DecimalFormat;

import llnl.gnem.apps.detection.util.TimeStamp;

public class SACHeader {

    public static final int SAC_HEADER_SIZE = 632;
    public static final int NVHDR_OFFSET = 76 * 4;

    // undefined values for SAC header variables
    public static float FLOAT_UNDEF = -12345.0f;
    public static int INT_UNDEF = -12345;
    public static String STRING8_UNDEF = "-12345  ";
    public static String STRING16_UNDEF = "-12345          ";
    public static final int DEFAULT_NVHDR = 6;

    // header value initializations
    public ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    public float delta = FLOAT_UNDEF;
    public float depmin = FLOAT_UNDEF;
    public float depmax = FLOAT_UNDEF;
    public float scale = FLOAT_UNDEF;
    public float odelta = FLOAT_UNDEF;
    public float b = FLOAT_UNDEF;
    public float e = FLOAT_UNDEF;
    public float o = FLOAT_UNDEF;
    public float a = FLOAT_UNDEF;
    public float fmt = FLOAT_UNDEF;
    public float t0 = FLOAT_UNDEF;
    public float t1 = FLOAT_UNDEF;
    public float t2 = FLOAT_UNDEF;
    public float t3 = FLOAT_UNDEF;
    public float t4 = FLOAT_UNDEF;
    public float t5 = FLOAT_UNDEF;
    public float t6 = FLOAT_UNDEF;
    public float t7 = FLOAT_UNDEF;
    public float t8 = FLOAT_UNDEF;
    public float t9 = FLOAT_UNDEF;
    public float f = FLOAT_UNDEF;
    public float resp0 = FLOAT_UNDEF;
    public float resp1 = FLOAT_UNDEF;
    public float resp2 = FLOAT_UNDEF;
    public float resp3 = FLOAT_UNDEF;
    public float resp4 = FLOAT_UNDEF;
    public float resp5 = FLOAT_UNDEF;
    public float resp6 = FLOAT_UNDEF;
    public float resp7 = FLOAT_UNDEF;
    public float resp8 = FLOAT_UNDEF;
    public float resp9 = FLOAT_UNDEF;
    public float stla = FLOAT_UNDEF;
    public float stlo = FLOAT_UNDEF;
    public float stel = FLOAT_UNDEF;
    public float stdp = FLOAT_UNDEF;
    public float evla = FLOAT_UNDEF;
    public float evlo = FLOAT_UNDEF;
    public float evel = FLOAT_UNDEF;
    public float evdp = FLOAT_UNDEF;
    public float mag = FLOAT_UNDEF;
    public float user0 = FLOAT_UNDEF;
    public float user1 = FLOAT_UNDEF;
    public float user2 = FLOAT_UNDEF;
    public float user3 = FLOAT_UNDEF;
    public float user4 = FLOAT_UNDEF;
    public float user5 = FLOAT_UNDEF;
    public float user6 = FLOAT_UNDEF;
    public float user7 = FLOAT_UNDEF;
    public float user8 = FLOAT_UNDEF;
    public float user9 = FLOAT_UNDEF;
    public float dist = FLOAT_UNDEF;
    public float az = FLOAT_UNDEF;
    public float baz = FLOAT_UNDEF;
    public float gcarc = FLOAT_UNDEF;
    public float sb = FLOAT_UNDEF;
    public float sdelta = FLOAT_UNDEF;
    public float depmen = FLOAT_UNDEF;
    public float cmpaz = FLOAT_UNDEF;
    public float cmpinc = FLOAT_UNDEF;
    public float xminimum = FLOAT_UNDEF;
    public float xmaximum = FLOAT_UNDEF;
    public float yminimum = FLOAT_UNDEF;
    public float ymaximum = FLOAT_UNDEF;
    public float unused6 = FLOAT_UNDEF;
    public float unused7 = FLOAT_UNDEF;
    public float unused8 = FLOAT_UNDEF;
    public float unused9 = FLOAT_UNDEF;
    public float unused10 = FLOAT_UNDEF;
    public float unused11 = FLOAT_UNDEF;
    public float unused12 = FLOAT_UNDEF;

    public int nzyear = INT_UNDEF;
    public int nzjday = INT_UNDEF;
    public int nzhour = INT_UNDEF;
    public int nzmin = INT_UNDEF;
    public int nzsec = INT_UNDEF;
    public int nzmsec = INT_UNDEF;
    public int nvhdr = DEFAULT_NVHDR;
    public int norid = INT_UNDEF;
    public int nevid = INT_UNDEF;
    public int npts = INT_UNDEF;
    public int nsnpts = INT_UNDEF;
    public int nwfid = INT_UNDEF;
    public int nxsize = INT_UNDEF;
    public int nysize = INT_UNDEF;
    public int unused15 = INT_UNDEF;
    public int iftype = INT_UNDEF;
    public int idep = INT_UNDEF;
    public int iztype = INT_UNDEF;
    public int unused16 = INT_UNDEF;
    public int iinst = INT_UNDEF;
    public int istreg = INT_UNDEF;
    public int ievreg = INT_UNDEF;
    public int ievtyp = INT_UNDEF;
    public int iqual = INT_UNDEF;
    public int isynth = INT_UNDEF;
    public int imagtyp = INT_UNDEF;
    public int imagsrc = INT_UNDEF;
    public int unused19 = INT_UNDEF;
    public int unused20 = INT_UNDEF;
    public int unused21 = INT_UNDEF;
    public int unused22 = INT_UNDEF;
    public int unused23 = INT_UNDEF;
    public int unused24 = INT_UNDEF;
    public int unused25 = INT_UNDEF;
    public int unused26 = INT_UNDEF;
    public int leven = INT_UNDEF;
    public int lpspol = INT_UNDEF;
    public int lovrok = INT_UNDEF;
    public int lcalda = INT_UNDEF;
    public int unused27 = INT_UNDEF;

    public String kstnm = STRING8_UNDEF;
    public String kevnm = STRING16_UNDEF;
    public String khole = STRING8_UNDEF;
    public String ko = STRING8_UNDEF;
    public String ka = STRING8_UNDEF;
    public String kt0 = STRING8_UNDEF;
    public String kt1 = STRING8_UNDEF;
    public String kt2 = STRING8_UNDEF;
    public String kt3 = STRING8_UNDEF;
    public String kt4 = STRING8_UNDEF;
    public String kt5 = STRING8_UNDEF;
    public String kt6 = STRING8_UNDEF;
    public String kt7 = STRING8_UNDEF;
    public String kt8 = STRING8_UNDEF;
    public String kt9 = STRING8_UNDEF;
    public String kf = STRING8_UNDEF;
    public String kuser0 = STRING8_UNDEF;
    public String kuser1 = STRING8_UNDEF;
    public String kuser2 = STRING8_UNDEF;
    public String kcmpnm = STRING8_UNDEF;
    public String knetwk = STRING8_UNDEF;
    public String kdatrd = STRING8_UNDEF;
    public String kinst = STRING8_UNDEF;

    private TimeStamp startTime = null;

    /* Constants used by SAC. */
    public static final int IREAL = 0;
    public static final int ITIME = 1;
    public static final int IRLIM = 2;
    public static final int IAMPH = 3;
    public static final int IXY = 4;
    public static final int IUNKN = 5;
    public static final int IDISP = 6;
    public static final int IVEL = 7;
    public static final int IACC = 8;
    public static final int IB = 9;
    public static final int IDAY = 10;
    public static final int IO = 11;
    public static final int IA = 12;
    public static final int IT0 = 13;
    public static final int IT1 = 14;
    public static final int IT2 = 15;
    public static final int IT3 = 16;
    public static final int IT4 = 17;
    public static final int IT5 = 18;
    public static final int IT6 = 19;
    public static final int IT7 = 20;
    public static final int IT8 = 21;
    public static final int IT9 = 22;
    public static final int IRADNV = 23;
    public static final int ITANNV = 24;
    public static final int IRADEV = 25;
    public static final int ITANEV = 26;
    public static final int INORTH = 27;
    public static final int IEAST = 28;
    public static final int IHORZA = 29;
    public static final int IDOWN = 30;
    public static final int IUP = 31;
    public static final int ILLLBB = 32;
    public static final int IWWSN1 = 33;
    public static final int IWWSN2 = 34;
    public static final int IHGLP = 35;
    public static final int ISRO = 36;
    public static final int INUCL = 37;
    public static final int IPREN = 38;
    public static final int IPOSTN = 39;
    public static final int IQUAKE = 40;
    public static final int IPREQ = 41;
    public static final int IPOSTQ = 42;
    public static final int ICHEM = 43;
    public static final int IOTHER = 44;
    public static final int IGOOD = 45;
    public static final int IGLCH = 46;
    public static final int IDROP = 47;
    public static final int ILOWSN = 48;
    public static final int IRLDTA = 49;
    public static final int IVOLTS = 50;
    public static final int INIV51 = 51;
    public static final int INIV52 = 52;
    public static final int INIV53 = 53;
    public static final int INIV54 = 54;
    public static final int INIV55 = 55;
    public static final int INIV56 = 56;
    public static final int INIV57 = 57;
    public static final int INIV58 = 58;
    public static final int INIV59 = 59;
    public static final int INIV60 = 60;

    public SACHeader() {
        // default constructor
    }

    /**
     * * Constructs the header from a stream. The NVHDR value (should be 6) is
     * checked to see if byte swapping is needed. If so, all header values are
     * byte swapped and the byteOrder is set to IntelByteOrder (LITTLE_ENDIAN)
     * so that the data section will also be byte swapped on read. Extra care is
     * taken to do all byte swapping before the byte values are transformed into
     * floats as java can do very funny things if the byte-swapped float happens
     * to be a NaN.
     */
    public SACHeader(DataInputStream indis) throws FileNotFoundException, IOException {

        byte[] headerBuf = new byte[SAC_HEADER_SIZE];
        indis.readFully(headerBuf);

        if (headerBuf[NVHDR_OFFSET] == 6 && headerBuf[NVHDR_OFFSET + 1] == 0 && headerBuf[NVHDR_OFFSET + 2] == 0 && headerBuf[NVHDR_OFFSET + 3] == 0) {

            byteOrder = ByteOrder.LITTLE_ENDIAN;

            // little endian byte order, swap bytes on first 110 4-byte values in header, rest are text
            for (int i = 0; i < 110 * 4; i += 4) {
                byte tmp = headerBuf[i];
                headerBuf[i] = headerBuf[i + 3];
                headerBuf[i + 3] = tmp;
                tmp = headerBuf[i + 1];
                headerBuf[i + 1] = headerBuf[i + 2];
                headerBuf[i + 2] = tmp;
            }
        }

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(headerBuf));

        delta = dis.readFloat();
        depmin = dis.readFloat();
        depmax = dis.readFloat();
        scale = dis.readFloat();
        odelta = dis.readFloat();
        b = dis.readFloat();
        e = dis.readFloat();
        o = dis.readFloat();
        a = dis.readFloat();
        fmt = dis.readFloat();
        t0 = dis.readFloat();
        t1 = dis.readFloat();
        t2 = dis.readFloat();
        t3 = dis.readFloat();
        t4 = dis.readFloat();
        t5 = dis.readFloat();
        t6 = dis.readFloat();
        t7 = dis.readFloat();
        t8 = dis.readFloat();
        t9 = dis.readFloat();
        f = dis.readFloat();
        resp0 = dis.readFloat();
        resp1 = dis.readFloat();
        resp2 = dis.readFloat();
        resp3 = dis.readFloat();
        resp4 = dis.readFloat();
        resp5 = dis.readFloat();
        resp6 = dis.readFloat();
        resp7 = dis.readFloat();
        resp8 = dis.readFloat();
        resp9 = dis.readFloat();
        stla = dis.readFloat();
        stlo = dis.readFloat();
        stel = dis.readFloat();
        stdp = dis.readFloat();
        evla = dis.readFloat();
        evlo = dis.readFloat();
        evel = dis.readFloat();
        evdp = dis.readFloat();
        mag = dis.readFloat();
        user0 = dis.readFloat();
        user1 = dis.readFloat();
        user2 = dis.readFloat();
        user3 = dis.readFloat();
        user4 = dis.readFloat();
        user5 = dis.readFloat();
        user6 = dis.readFloat();
        user7 = dis.readFloat();
        user8 = dis.readFloat();
        user9 = dis.readFloat();
        dist = dis.readFloat();
        az = dis.readFloat();
        baz = dis.readFloat();
        gcarc = dis.readFloat();
        sb = dis.readFloat();
        sdelta = dis.readFloat();
        depmen = dis.readFloat();
        cmpaz = dis.readFloat();
        cmpinc = dis.readFloat();
        xminimum = dis.readFloat();
        xmaximum = dis.readFloat();
        yminimum = dis.readFloat();
        ymaximum = dis.readFloat();
        unused6 = dis.readFloat();
        unused7 = dis.readFloat();
        unused8 = dis.readFloat();
        unused9 = dis.readFloat();
        unused10 = dis.readFloat();
        unused11 = dis.readFloat();
        unused12 = dis.readFloat();

        nzyear = dis.readInt();
        nzjday = dis.readInt();
        nzhour = dis.readInt();
        nzmin = dis.readInt();
        nzsec = dis.readInt();
        nzmsec = dis.readInt();
        nvhdr = dis.readInt();
        norid = dis.readInt();
        nevid = dis.readInt();
        npts = dis.readInt();
        nsnpts = dis.readInt();
        nwfid = dis.readInt();
        nxsize = dis.readInt();
        nysize = dis.readInt();
        unused15 = dis.readInt();
        iftype = dis.readInt();
        idep = dis.readInt();
        iztype = dis.readInt();
        unused16 = dis.readInt();
        iinst = dis.readInt();
        istreg = dis.readInt();
        ievreg = dis.readInt();
        ievtyp = dis.readInt();
        iqual = dis.readInt();
        isynth = dis.readInt();
        imagtyp = dis.readInt();
        imagsrc = dis.readInt();
        unused19 = dis.readInt();
        unused20 = dis.readInt();
        unused21 = dis.readInt();
        unused22 = dis.readInt();
        unused23 = dis.readInt();
        unused24 = dis.readInt();
        unused25 = dis.readInt();
        unused26 = dis.readInt();
        leven = dis.readInt();
        lpspol = dis.readInt();
        lovrok = dis.readInt();
        lcalda = dis.readInt();
        unused27 = dis.readInt();

        byte[] eightBytes = new byte[8];
        byte[] sixteenBytes = new byte[16];

        dis.readFully(eightBytes);
        kstnm = new String(eightBytes);
        dis.readFully(sixteenBytes);
        kevnm = new String(sixteenBytes);
        dis.readFully(eightBytes);
        khole = new String(eightBytes);
        dis.readFully(eightBytes);
        ko = new String(eightBytes);
        dis.readFully(eightBytes);
        ka = new String(eightBytes);
        dis.readFully(eightBytes);
        kt0 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt1 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt2 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt3 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt4 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt5 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt6 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt7 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt8 = new String(eightBytes);
        dis.readFully(eightBytes);
        kt9 = new String(eightBytes);
        dis.readFully(eightBytes);
        kf = new String(eightBytes);
        dis.readFully(eightBytes);
        kuser0 = new String(eightBytes);
        dis.readFully(eightBytes);
        kuser1 = new String(eightBytes);
        dis.readFully(eightBytes);
        kuser2 = new String(eightBytes);
        dis.readFully(eightBytes);
        kcmpnm = new String(eightBytes);
        dis.readFully(eightBytes);
        knetwk = new String(eightBytes);
        dis.readFully(eightBytes);
        kdatrd = new String(eightBytes);
        dis.readFully(eightBytes);
        kinst = new String(eightBytes);

        // construct time stamp
        startTime = new TimeStamp(nzyear, nzjday, nzhour, nzmin, nzsec, nzmsec);
        startTime.plus(b);
    }

    public TimeStamp getStartTime() {
        return new TimeStamp(startTime);
    }

    public void setStartTime(TimeStamp T) {
        nzyear = T.getYear();
        nzjday = T.getJDay();
        nzhour = T.getHour();
        nzmin = T.getMin();
        nzsec = T.getSec();
        nzmsec = T.getMsec();
    }

    public final static int swapBytes(int val) {
        return ((val & 0xff000000) >>> 24) + ((val & 0x00ff0000) >> 8) + ((val & 0x0000ff00) << 8) + ((val & 0x000000ff) << 24);
    }

    /**
     * * write a single float to the stream, swapping bytes if needed.
     */
    private final void writeFloat(RandomAccessFile file, float val) throws IOException {
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            // careful here as dos.writeFloat() will collapse all NaN floats to
            // a single NaN value. But we are trying to write out byte swapped values
            // so different floats that are all NaN are different values in the
            // other byte order. Solution is to swap on the integer bits, not the float
            file.writeInt(swapBytes(Float.floatToRawIntBits(val)));
        } else {
            file.writeFloat(val);
        }
    }

    /**
     * * write a single int to the stream, swapping bytes if needed.
     */
    private final void writeInt(RandomAccessFile file, int val) throws IOException {
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            file.writeInt(swapBytes(val));
        } else {
            file.writeInt(val);
        }
    }

    public void writeHeader(RandomAccessFile file) throws IOException {

        file.writeFloat(delta);
        file.writeFloat(depmin);
        file.writeFloat(depmax);
        file.writeFloat(scale);
        file.writeFloat(odelta);
        file.writeFloat(b);
        file.writeFloat(e);
        file.writeFloat(o);
        file.writeFloat(a);
        file.writeFloat(fmt);
        file.writeFloat(t0);
        file.writeFloat(t1);
        file.writeFloat(t2);
        file.writeFloat(t3);
        file.writeFloat(t4);
        file.writeFloat(t5);
        file.writeFloat(t6);
        file.writeFloat(t7);
        file.writeFloat(t8);
        file.writeFloat(t9);
        file.writeFloat(f);
        file.writeFloat(resp0);
        file.writeFloat(resp1);
        file.writeFloat(resp2);
        file.writeFloat(resp3);
        file.writeFloat(resp4);
        file.writeFloat(resp5);
        file.writeFloat(resp6);
        file.writeFloat(resp7);
        file.writeFloat(resp8);
        file.writeFloat(resp9);
        file.writeFloat(stla);
        file.writeFloat(stlo);
        file.writeFloat(stel);
        file.writeFloat(stdp);
        file.writeFloat(evla);
        file.writeFloat(evlo);
        file.writeFloat(evel);
        file.writeFloat(evdp);
        file.writeFloat(mag);
        file.writeFloat(user0);
        file.writeFloat(user1);
        file.writeFloat(user2);
        file.writeFloat(user3);
        file.writeFloat(user4);
        file.writeFloat(user5);
        file.writeFloat(user6);
        file.writeFloat(user7);
        file.writeFloat(user8);
        file.writeFloat(user9);
        file.writeFloat(dist);
        file.writeFloat(az);
        file.writeFloat(baz);
        file.writeFloat(gcarc);
        file.writeFloat(sb);
        file.writeFloat(sdelta);
        file.writeFloat(depmen);
        file.writeFloat(cmpaz);
        file.writeFloat(cmpinc);
        file.writeFloat(xminimum);
        file.writeFloat(xmaximum);
        file.writeFloat(yminimum);
        file.writeFloat(ymaximum);
        file.writeFloat(unused6);
        file.writeFloat(unused7);
        file.writeFloat(unused8);
        file.writeFloat(unused9);
        file.writeFloat(unused10);
        file.writeFloat(unused11);
        file.writeFloat(unused12);

        file.writeInt(nzyear);
        file.writeInt(nzjday);
        file.writeInt(nzhour);
        file.writeInt(nzmin);
        file.writeInt(nzsec);
        file.writeInt(nzmsec);
        file.writeInt(nvhdr);
        file.writeInt(norid);
        file.writeInt(nevid);
        file.writeInt(npts);
        file.writeInt(nsnpts);
        file.writeInt(nwfid);
        file.writeInt(nxsize);
        file.writeInt(nysize);
        file.writeInt(unused15);
        file.writeInt(iftype);
        file.writeInt(idep);
        file.writeInt(iztype);
        file.writeInt(unused16);
        file.writeInt(iinst);
        file.writeInt(istreg);
        file.writeInt(ievreg);
        file.writeInt(ievtyp);
        file.writeInt(iqual);
        file.writeInt(isynth);
        file.writeInt(imagtyp);
        file.writeInt(imagsrc);
        file.writeInt(unused19);
        file.writeInt(unused20);
        file.writeInt(unused21);
        file.writeInt(unused22);
        file.writeInt(unused23);
        file.writeInt(unused24);
        file.writeInt(unused25);
        file.writeInt(unused26);
        file.writeInt(leven);
        file.writeInt(lpspol);
        file.writeInt(lovrok);
        file.writeInt(lcalda);
        file.writeInt(unused27);

        kstnm = padString(kstnm, 8);
        file.writeBytes(kstnm);
        kevnm = padString(kevnm, 16);
        file.writeBytes(kevnm);
        khole = padString(khole, 8);
        file.writeBytes(khole);
        ko = padString(ko, 8);
        file.writeBytes(ko);
        ka = padString(ka, 8);
        file.writeBytes(ka);
        kt0 = padString(kt0, 8);
        file.writeBytes(kt0);
        kt1 = padString(kt1, 8);
        file.writeBytes(kt1);
        kt2 = padString(kt2, 8);
        file.writeBytes(kt2);
        kt3 = padString(kt3, 8);
        file.writeBytes(kt3);
        kt4 = padString(kt4, 8);
        file.writeBytes(kt4);
        kt5 = padString(kt5, 8);
        file.writeBytes(kt5);
        kt6 = padString(kt6, 8);
        file.writeBytes(kt6);
        kt7 = padString(kt7, 8);
        file.writeBytes(kt7);
        kt8 = padString(kt8, 8);
        file.writeBytes(kt8);
        kt9 = padString(kt9, 8);
        file.writeBytes(kt9);
        kf = padString(kf, 8);
        file.writeBytes(kf);
        kuser0 = padString(kuser0, 8);
        file.writeBytes(kuser0);
        kuser1 = padString(kuser1, 8);
        file.writeBytes(kuser1);
        kuser2 = padString(kuser2, 8);
        file.writeBytes(kuser2);
        kcmpnm = padString(kcmpnm, 8);
        file.writeBytes(kcmpnm);
        knetwk = padString(knetwk, 8);
        file.writeBytes(knetwk);
        kdatrd = padString(kdatrd, 8);
        file.writeBytes(kdatrd);
        kinst = padString(kinst, 8);
        file.writeBytes(kinst);
    }

    private String padString(String field, int length) {
        String retval = new String(field);
        if (retval.length() > length) {
            retval = retval.substring(0, length - 1);
        }
        while (retval.length() < length) {
            retval += " ";
        }
        return retval;
    }

    public static final DecimalFormat decimalFormat = new DecimalFormat("#####.####");

    public static String format(String label, float f) {

        String s = label + " = ";
        String fString = decimalFormat.format(f);
        while (fString.length() < 8) {
            fString = " " + fString;
        }
        s = s + fString;
        while (s.length() < 21) {
            s = " " + s;
        }
        return s;
    }

    public static String formatLine(String s1, float f1, String s2, float f2, String s3, float f3, String s4, float f4, String s5, float f5) {
        return format(s1, f1) + format(s2, f2) + format(s3, f3) + format(s4, f4) + format(s5, f5);
    }

    public void printHeader(PrintStream ps) {

        ps.println(formatLine("delta", delta, "depmin", depmin, "depmax", depmax, "scale", scale, "odelta", odelta));
        ps.println(formatLine("b", b, "e", e, "o", o, "a", a, "fmt", fmt));
        ps.println(formatLine("t0", t0, "t1", t1, "t2", t2, "t3", t3, "t4", t4));
        ps.println(formatLine("t5", t5, "t6", t6, "t7", t7, "t8", t8, "t9", t9));
        ps.println(formatLine("f", f, "resp0", resp0, "resp1", resp1, "resp2", resp2, "resp3", resp3));
        ps.println(formatLine("resp4", resp4, "resp5", resp5, "resp6", resp6, "resp7", resp7, "resp8", resp8));
        ps.println(formatLine("resp9", resp9, "stla", stla, "stlo", stlo, "stel", stel, "stdp", stdp));
        ps.println(formatLine("evla", evla, "evlo", evlo, "evel", evel, "evdp", evdp, "mag", mag));
        ps.println(formatLine("user0", user0, "user1", user1, "user2", user2, "user3", user3, "user4", user4));
        ps.println(formatLine("user5", user5, "user6", user6, "user7", user7, "user8", user8, "user9", user9));
        ps.println(formatLine("dist", dist, "az", az, "baz", baz, "gcarc", gcarc, "sb", sb));
        ps.println(formatLine("sdelta", sdelta, "depmen", depmen, "cmpaz", cmpaz, "cmpinc", cmpinc, "xminimum", xminimum));
        ps.println(formatLine("xmaximum", xmaximum, "yminimum", yminimum, "ymaximum", ymaximum, "unused6", unused6, "unused7", unused7));
        ps.println(formatLine("unused8", unused8, "unused9", unused9, "unused10", unused10, "unused11", unused11, "unused12", unused12));
        ps.println(formatLine("nzyear", nzyear, "nzjday", nzjday, "nzhour", nzhour, "nzmin", nzmin, "nzsec", nzsec));
        ps.println(formatLine("nzmsec", nzmsec, "nvhdr", nvhdr, "norid", norid, "nevid", nevid, "npts", npts));
        ps.println(formatLine("nsnpts", nsnpts, "nwfid", nwfid, "nxsize", nxsize, "nysize", nysize, "unused15", unused15));
        ps.println(formatLine("iftype", iftype, "idep", idep, "iztype", iztype, "unused16", unused16, "iinst", iinst));
        ps.println(formatLine("istreg", istreg, "ievreg", ievreg, "ievtyp", ievtyp, "iqual", iqual, "isynth", isynth));
        ps.println(formatLine("imagtyp", imagtyp, "imagsrc", imagsrc, "unused19", unused19, "unused20", unused20, "unused21", unused21));
        ps.println(formatLine("unused22", unused22, "unused23", unused23, "unused24", unused24, "unused25", unused25, "unused26", unused26));
        ps.println(formatLine("leven", leven, "lpspol", lpspol, "lovrok", lovrok, "lcalda", lcalda, "unused27", unused27));
        ps.println(" kstnm = " + kstnm + " kevnm = " + kevnm + " khole = " + khole + " ko = " + ko);
        ps.println(" ka =  " + ka + " kt0 = " + kt0 + " kt1 = " + kt1 + " kt2 = " + kt2);
        ps.println(" kt3 = " + kt3 + " kt4 = " + kt4 + " kt5 = " + kt5 + " kt6 = " + kt6);
        ps.println(" kt7 = " + kt7 + " kt8 = " + kt8 + " kt9 = " + kt9 + " kf = " + kf);
        ps.println(" kuser0 = " + kuser0 + " kuser1 = " + kuser1 + " kuser2 = " + kuser2 + " kcmpnm = " + kcmpnm);
        ps.println(" knetwk = " + knetwk + " kdatrd = " + kdatrd + " kinst = " + kinst);
    }

}
