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
package llnl.gnem.dftt.core.traveltime.Ak135;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import llnl.gnem.dftt.core.util.FileInputArrayLoader;

/**
 *
 * @author myers30
 */
public class EllipticityCorrectionLoader {

    private float[] dists;
    private float[] depths;
    private float[][] t0;
    private float[][] t1;
    private float[][] t2;
    private ArrayList<String> lines;
    private int currentLine;

    public EllipticityCorrectionLoader(String Phase) throws IOException {
        this(FileInputArrayLoader.fillStrings("/tmp/ec_ak135."  + Phase));
    }

    public EllipticityCorrectionLoader(String Path, String file) throws IOException {
        this(FileInputArrayLoader.fillStrings(Path + "/" + file));
    }
    
    public EllipticityCorrectionLoader(InputStream stream) throws IOException {
        this(FileInputArrayLoader.fillStrings(stream));
    }
    
    public EllipticityCorrectionLoader(String[] lines) {
        this.lines = new ArrayList<String>(Arrays.asList(lines));
        loadLines();
    }

    private void loadLines() {
        String line = nextLine();
        line = nextLine();
        StringTokenizer tok = new StringTokenizer(line);
        int Ndepth = Integer.parseInt(tok.nextToken());
        int NdepthLines = (int) Math.ceil(Ndepth / 10.d);
        depths = new float[Ndepth];
        int j = 0;
        for (int i = 0; i < NdepthLines; i++) {
            line = nextLine();
            tok = new StringTokenizer(line);
            while (tok.hasMoreTokens()) {
                depths[j] = Float.parseFloat(tok.nextToken());
                j++;
            }
        }

        line = nextLine();
        tok = new StringTokenizer(line);
        int Ndist = Integer.parseInt(tok.nextToken());
        int NdistLines = (int) Math.ceil(Ndist / 10.d);
        dists = new float[Ndist];
        j = 0;
        for (int i = 0; i < NdistLines; i++) {
            line = nextLine();
            tok = new StringTokenizer(line);
            while (tok.hasMoreTokens()) {
                dists[j] = Float.parseFloat(tok.nextToken());
                j++;
            }
        }

        this.t0 = new float[Ndepth][Ndist];
        this.t1 = new float[Ndepth][Ndist];
        this.t2 = new float[Ndepth][Ndist];
        String temp;
        for (int i = 0; i < Ndepth; i++) {
            nextLine();
            for (int k = 0; k < Ndist; k++) {
                line = nextLine();
                tok = new StringTokenizer(line);
                temp = tok.nextToken();
                this.t0[i][k] = Float.parseFloat(temp.trim());
                temp = tok.nextToken();
                this.t1[i][k] = Float.parseFloat(temp.trim());
                temp = tok.nextToken();
                this.t2[i][k] = Float.parseFloat(temp.trim());
            }
        }
    }

    private String nextLine() {
        String line = this.lines.get(this.currentLine);
        this.currentLine++;
        return (line);
    }

    public float[] getDists() {
        return dists;
    }

    public float[] getDepths() {
        return depths;
    }

    public float[][] getT0() {
        return t0;
    }

    public float[][] getT1() {
        return t1;
    }

    public float[][] getT2() {
        return t2;
    }
}
