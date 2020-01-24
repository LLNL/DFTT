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
