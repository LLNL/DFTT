/*-
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
package llnl.gnem.core.waveform.io.css;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import llnl.gnem.core.util.FileUtil.DriveMapper;
import llnl.gnem.core.waveform.Wfdisc;

/**
 *
 * @author dodge1
 */
public class WfdiscReader {

    public static ArrayList<Wfdisc> readSpaceDelimitedWfdiscFile(String wfdiscFlatFileName) throws FileNotFoundException {
        ArrayList<Wfdisc> result = new ArrayList<>();
        File file = new File(DriveMapper.getInstance().maybeMapPath(wfdiscFlatFileName));
        try ( Scanner sc = new Scanner(file)) {

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] fields = line.split("\\s+");
                if (fields.length == 20) {
                    int idx = 0;
                    String sta = fields[idx++];
                    String chan = fields[idx++];
                    double time = Double.parseDouble(fields[idx++]);
                    int wfid = Integer.parseInt(fields[idx++]);
                    int chanid = Integer.parseInt(fields[idx++]);
                    int jdate = Integer.parseInt(fields[idx++]);
                    double endtime = Double.parseDouble(fields[idx++]);
                    int nsamp = Integer.parseInt(fields[idx++]);
                    double samprate = Double.parseDouble(fields[idx++]);
                    double calib = Double.parseDouble(fields[idx++]);
                    double calper = Double.parseDouble(fields[idx++]);
                    String instype = fields[idx++];
                    String segtype = fields[idx++];
                    String datatype = fields[idx++];
                    String clip = fields[idx++];
                    String dir = fields[idx++];
                    String dfile = fields[idx++];
                    int foff = Integer.parseInt(fields[idx++]);
                    int commid = Integer.parseInt(fields[idx++]);
                    Wfdisc row = new Wfdisc(sta, chan, time, wfid, chanid, jdate, endtime,
                            nsamp, samprate, calib, calper, instype, segtype,
                            datatype, clip, dir, dfile, foff, commid);
                    result.add(row);
                }
            }
        }
        return result;
    }

    public static String getFullyQualifiedDfileName(Wfdisc row, PathType pathType, Path parent, String userPath) {
        String dirToUse = null;
        switch (pathType) {
            case Internal:
                dirToUse = row.getDir();
                break;
            case FilePath:
                dirToUse = parent.toString();
                break;
            case UserSpecified:
                dirToUse = userPath;
                break;
            case FilePathPlusRel:
                File file = new File(parent.toString(), row.getDir());

                dirToUse = file.getAbsolutePath();
                break;
        }
        File dfilePath = new File(dirToUse, row.getDfile());
        return dfilePath.toPath().normalize().toString();
    }


}
