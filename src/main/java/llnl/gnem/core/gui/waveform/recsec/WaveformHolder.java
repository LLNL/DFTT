package llnl.gnem.core.gui.waveform.recsec;

import llnl.gnem.core.gui.map.origins.OriginInfo;
import llnl.gnem.core.waveform.components.BaseSingleComponent;


/**
 * User: Doug
 * Date: Sep 26, 2009
 * Time: 2:55:50 PM
 */
public interface WaveformHolder {
    float[] getPlotArray();

    double getTime();

    double getSamprate();

    BaseSingleComponent getChannelData();

    double getHeight();

    public double getCenter();

    void setScalingType(ScalingType scalingType);

    void updateAmplitudeInformation(double maxPeakToPeak, double val);

    void magnify();

    void reduce();

    double getDataRange();

    double getMaxDataRange();

    ScalingType getScalingType();

    double getTimeReduction(TimeReductionType timeReduction, OriginInfo origin);
}
