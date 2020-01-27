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
package llnl.gnem.core.waveform.responseProcessing;

import java.io.File;
import net.jcip.annotations.ThreadSafe;

/**
 * Created by dodge1 Date: Jul 1, 2008 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
@ThreadSafe
public class ResponseMetaData
{

    private final String filename;
    private final ResponseType rsptype;
    private final double nominalCalib;
    private final double nominalCalper;
    private final double sensorCalper;
    private final double sensorCalratio;
    private final double wfdiscCalib;
    private final double wfdiscCalper;

    public ResponseMetaData(String filename,
            ResponseType rsptype,
            double nominalCalib,
            double nominalCalper,
            double sensorCalper,
            double sensorCalratio,
            double wfdiscCalib,
            double wfdiscCalper
    )
    {
        this.filename = filename;
        this.rsptype = rsptype;
        this.nominalCalib = nominalCalib;
        this.nominalCalper = nominalCalper;
        this.sensorCalper = sensorCalper;
        this.sensorCalratio = sensorCalratio;
        this.wfdiscCalib = wfdiscCalib;
        this.wfdiscCalper = wfdiscCalper;
    }

    
    public ResponseMetaData(File file,
            ResponseType rsptype,
            double nominalCalib,
            double nominalCalper,
            double sensorCalper,
            double sensorCalratio,
            double wfdiscCalib,
            double wfdiscCalper
    )
    {
        this.filename = file.getAbsolutePath();
        this.rsptype = rsptype;
        this.nominalCalib = nominalCalib;
        this.nominalCalper = nominalCalper;
        this.sensorCalper = sensorCalper;
        this.sensorCalratio = sensorCalratio;
        this.wfdiscCalib = wfdiscCalib;
        this.wfdiscCalper = wfdiscCalper;
    }

    public String getFilename()
    {
        return filename;
    }

    public ResponseType getRsptype()
    {
        return rsptype;
    }

    public double getNominalCalib()
    {
        return nominalCalib;
    }

    public double getNominalCalper()
    {
        return nominalCalper;
    }

    public double getSensorCalper()
    {
        return sensorCalper;
    }

    public double getSensorCalratio()
    {
        return sensorCalratio;
    }

    public double getWfdiscCalib()
    {
        return wfdiscCalib;
    }

    public double getWfdiscCalper()
    {
        return wfdiscCalper;
    }
}
